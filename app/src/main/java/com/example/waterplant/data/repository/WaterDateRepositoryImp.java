package com.example.waterplant.data.repository;

import com.example.waterplant.data.database.PlantDatabase;
import com.example.waterplant.data.database.WaterDayDao;
import com.example.waterplant.data.database.entities.WaterDay;

public class WaterDateRepositoryImp implements WaterDayRepository{

    WaterDayDao dao;
    public WaterDateRepositoryImp(WaterDayDao dao){
        this.dao = dao;
    }

    @Override
    public void insertNewWaterDay(WaterDay day) {
        PlantDatabase.executorService.execute(() -> {
            dao.insertDay(day);
        });
    }

    @Override
    public void deleteDates(Integer id) {
        PlantDatabase.executorService.execute(() -> {
            dao.deleteDates(id);
        });
    }
}
