package com.example.waterplant.data.repository;

import com.example.waterplant.data.database.PlantDao;
import com.example.waterplant.data.database.PlantDatabase;
import com.example.waterplant.data.database.entities.Plant;
import com.example.waterplant.data.database.entities.Today;

public class ToDayRepositoryImp implements TodayRepository{

    PlantDao dao;
    public ToDayRepositoryImp(PlantDao dao) {
        this.dao = dao;
    }

    @Override
    public void insert(Today today) {
        PlantDatabase.executorService.execute(() -> {
            dao.insertToday(today);
        });
    }

    @Override
    public void update(Today today) {
        PlantDatabase.executorService.execute(() -> {
            dao.updateToday(today);
        });
    }

    @Override
    public void deleteAll() {
        PlantDatabase.executorService.execute(() -> {
            dao.deleteAll();
        });

    }
}
