package com.example.waterplant.data.repository;

import androidx.lifecycle.LiveData;

import com.example.waterplant.data.database.entities.Plant;

import java.util.List;

public interface PlantRepository {
    LiveData<List<Plant>> getAll();
    LiveData<List<Plant>> getPlantToDay();
    LiveData<Plant> getSpecificPlant(Integer id);
    LiveData<List<Plant>> searchByQuery(String query);
    void insertNewPlant(Plant plant);
    void updatePlant(Plant plant);
    void deletePlant(Plant plant);
}
