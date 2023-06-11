package com.example.waterplant.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.waterplant.data.database.entities.Plant;
import com.example.waterplant.data.database.entities.WaterDay;
import com.example.waterplant.data.database.entities.Today;

import java.util.List;

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


    //command work with Today entity
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateToday(Today today);
    @Insert
    void insertToday(Today today);
    @Query("DELETE FROM to_day")
    void deleteAll();

    @Query("select * from plant where plant.id in ( select water_dates.id from water_dates, to_day where water_dates.date = to_day.date) ")
    LiveData<List<Plant>> getPlantToday();

    @Query("select * from plant where plant.name like :query")
    LiveData<List<Plant>> searchByQuery(String query);
}
