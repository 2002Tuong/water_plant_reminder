package com.example.waterplant.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.waterplant.database.entities.Plant;
import com.example.waterplant.database.entities.Today;
import com.example.waterplant.database.entities.WaterDay;

import java.util.List;

import javax.inject.Inject;

@Dao
public interface PlantDao {

    //command work with Plant entity
    @Query("Select * from plant ")
    LiveData<List<Plant>> getAll();
    @Query("Select * from plant where plant.id = :id")
    LiveData<Plant> getSpecie(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Plant plant);

    @Delete
    void delete(Plant plant);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Plant plant);

    //command work with WaterDay entity
    @Insert(onConflict =  OnConflictStrategy.IGNORE)
    void insertDay(WaterDay waterDay);

    @Query("select * from water_dates where id = :id ")
    LiveData<List<WaterDay>> getPlantRemindDates(Integer id);

    @Query("delete from water_dates where id = :plantId")
    void deleteDates(Integer plantId);

    //command work with Today entity
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateToday(Today today);
    @Insert
    void insertToday(Today today);
    @Query("select * from plant where plant.id in ( select water_dates.id from water_dates, to_day where water_dates.date = to_day.date) ")
    LiveData<List<Plant>> getPlantToday();

    @Query("select * from plant where plant.name like :query")
    LiveData<List<Plant>> searchByQuery(String query);
}
