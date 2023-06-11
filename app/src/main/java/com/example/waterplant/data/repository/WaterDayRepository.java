package com.example.waterplant.data.repository;

import com.example.waterplant.data.database.entities.Plant;
import com.example.waterplant.data.database.entities.WaterDay;

public interface WaterDayRepository {
    void insertNewWaterDay(WaterDay day);
    void deleteDates(Integer id);
}
