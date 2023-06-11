package com.example.waterplant.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.waterplant.data.database.entities.WaterDay;

import java.util.List;

@Dao
public interface WaterDayDao {
    //command work with WaterDay entity
    @Insert(onConflict =  OnConflictStrategy.IGNORE)
    void insertDay(WaterDay waterDay);

    @Query("delete from water_dates where id = :plantId")
    void deleteDates(Integer plantId);

    @Query("select * from water_dates where id = :id ")
    LiveData<List<WaterDay>> getPlantRemindDates(Integer id);
}
