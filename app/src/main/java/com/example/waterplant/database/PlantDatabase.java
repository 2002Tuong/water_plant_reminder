package com.example.waterplant.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.waterplant.database.entities.Plant;
import com.example.waterplant.database.entities.Today;
import com.example.waterplant.database.entities.WaterDay;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Database(entities = {Plant.class, WaterDay.class, Today.class},exportSchema = false,version = 7)
public abstract class PlantDatabase extends RoomDatabase {
    public abstract PlantDao getDao();
    private static PlantDatabase INSTANCE = null;
    private final static int NUM_OF_THREAD = 4;
    public static ExecutorService executorService = Executors.newFixedThreadPool(NUM_OF_THREAD);

    private static final Migration MIGARATION_6_7 = new Migration(6,7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //delete one value in old table
            database.execSQL("delete from today where date = 'fri' ");
            //create new table
            database.execSQL("create table if not exists to_day(id INTEGER NOT NULL, date TEXT NOT NULL, PRIMARY KEY (id))");
            //insert  value from old table to new table
            database.execSQL("insert into to_day select 1, * from today");
            //delete old table
            database.execSQL("drop table today");
            //rename table
            //database.execSQL("alter table to_day rename today");
        }
    };


    public static PlantDatabase getInstance(Context context) {
        if(INSTANCE == null) {
            synchronized (PlantDatabase.class) {
                INSTANCE = Room.databaseBuilder(context, PlantDatabase.class, "plant_database")
                        .addMigrations(MIGARATION_6_7)
                        .build();
            }
        }

        return INSTANCE;
    }
}
