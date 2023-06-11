package com.example.waterplant.data.repository;

import androidx.lifecycle.LiveData;

import com.example.waterplant.data.database.PlantDao;
import com.example.waterplant.data.database.PlantDatabase;
import com.example.waterplant.data.database.entities.Plant;

import java.util.List;

import javax.inject.Inject;

public class PlantRepositoryImp implements PlantRepository {

    PlantDao plantDao;
    public PlantRepositoryImp(PlantDao plantDao){
        this.plantDao = plantDao;
    }
    @Override
    public LiveData<List<Plant>> getAll() {
        return plantDao.getAll();
    }

    @Override
    public LiveData<List<Plant>> getPlantToDay() {
        return plantDao.getPlantToday();
    }

    @Override
    public LiveData<Plant> getSpecificPlant(Integer id) {
        return plantDao.getSpecie(id);
    }

    @Override
    public LiveData<List<Plant>> searchByQuery(String query) {
        return plantDao.searchByQuery(query);
    }

    @Override
    public void insertNewPlant(Plant plant) {
        PlantDatabase.executorService.execute(new Runnable() {
            @Override
            public void run() {
                plantDao.insert(plant);
            }
        });
    }

    @Override
    public void updatePlant(Plant plant) {
        PlantDatabase.executorService.execute(new Runnable() {
            @Override
            public void run() {
                plantDao.update(plant);
            }
        });
    }

    @Override
    public void deletePlant(Plant plant) {
        PlantDatabase.executorService.execute(new Runnable() {
            @Override
            public void run() {
                plantDao.delete(plant);
            }
        });
    }
}
