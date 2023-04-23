package com.example.waterplant.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;


import com.example.waterplant.database.PlantDao;
import com.example.waterplant.database.PlantDatabase;
import com.example.waterplant.database.entities.Plant;
import com.example.waterplant.database.entities.WaterDay;
import com.example.waterplant.worker.RemindWorker;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

@HiltViewModel
public class PlantWaterViewModel extends ViewModel {
    PlantDao dao;

    @Inject
    WorkManager workManager;

    @Inject
    public PlantWaterViewModel(PlantDao dao) {
        this.dao = dao;
    }

    public LiveData<List<Plant>> getAllPlantsToWateringToday() { return dao.getPlantToday();}
    public LiveData<List<Plant>> getAllPlants() {
        return dao.getAll();
    }
    public LiveData<Plant> getItem(Integer id ){
        return dao.getSpecie(id);
    }
    public Plant createNewPlant(int id,String name, String frequency, String light, String water, String temp, String thumbnailUrl) {
        return new Plant(id,name,frequency,light,water,temp, thumbnailUrl);
    }
    public void insertNewPlant(Plant plant) {
        PlantDatabase.executorService.submit(() -> {
            dao.insert(plant);
        });
    }

    public WaterDay createNewWaterDay(int id, String day, String waterTime){
        return new WaterDay(id, day, waterTime);
    }
    public void insertWaterDay(WaterDay waterDay) {
        PlantDatabase.executorService.execute(() -> {
            dao.insertDay(waterDay);
        });
    }
    //check user's input format
    public Boolean checkNameFormat(String name) {
        if(name.isEmpty()) return false;
        return true;
    }
    public Boolean checkFrequencyFormat(String frequency) {
        try {
            Integer.parseInt(frequency);
        }catch (Exception e) {
            return false;
        }
        return true;
    }
    public Boolean checkWaterFormat(String water) {
        try {
            Integer.parseInt(water);
        }catch (Exception e) {
            return false;
        }
        return true;
    }
    public Boolean checkTempFormat(String temp) {
        try {
            Integer.parseInt(temp);
        }catch (Exception e) {
            return false;
        }
        return true;}
    public Boolean checkLightFormat(String temp) {
        String temperature = temp.toLowerCase();
        String[] states = {"low", "medium", "high"};
        for(String state : states ) {
            if(state.equals(temperature)) {
                return true;
            }
        }
        return false;
    }

    //request workmanager start work to notification
    public void requestReminder(Plant plant, List<WaterDay> dates, Long initDelayTime) {
        String[] list = new String[dates.size()];
        int i;
        for (i = 0; i < list.length; i += 1){
            list[i] = dates.get(i).getDate();
        }
        Data data = new Data.Builder()
                .putString("name", plant.getName())
                .putStringArray("waterday", list)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(RemindWorker.class, 1, TimeUnit.DAYS)
                .setInputData(data)
                .setInitialDelay(initDelayTime, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build();

        workManager.enqueueUniquePeriodicWork(plant.getId().toString() + "remind", ExistingPeriodicWorkPolicy.KEEP, request);

    }
    public void cancelRequest(String tag) {
        workManager.cancelUniqueWork(tag+"remind");
    }


    public void updatePlant(Plant plant) {
        PlantDatabase.executorService.execute(() -> {
            dao.update(plant);
        });

    }
    public void deletePlant(Plant plant) {
        PlantDatabase.executorService.execute(() -> {
            dao.delete(plant);
        });
    }
    public void deleteDates(Plant plant) {
        PlantDatabase.executorService.execute(()->
        {
            dao.deleteDates(plant.getId()
            );}
        );
    }

    public LiveData<List<Plant>> getSearchPlants(String query) {
        return dao.searchByQuery(query);
    }
}

