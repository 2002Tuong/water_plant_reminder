package com.example.waterplant.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.waterplant.data.database.entities.Plant;
import com.example.waterplant.data.database.entities.Today;
import com.example.waterplant.data.database.entities.WaterDay;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Database(entities = {Plant.class, WaterDay.class, Today.class},exportSchema = false,version = 9)
public abstract class PlantDatabase extends RoomDatabase {
    public abstract PlantDao getPlantDao();
    public abstract WaterDayDao getWaterDayDao();
    private static volatile PlantDatabase INSTANCE = null;
    private final static int NUM_OF_THREAD = 4;
    public static ExecutorService executorService = Executors.newFixedThreadPool(NUM_OF_THREAD);

    public static PlantDatabase getInstance(Context context) {
        if(INSTANCE == null) {
            synchronized (PlantDatabase.class) {
                INSTANCE = Room.databaseBuilder(context, PlantDatabase.class, "plant_database")
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }

        return INSTANCE;
    }
}
