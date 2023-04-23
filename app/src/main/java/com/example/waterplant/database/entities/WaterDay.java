package com.example.waterplant.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(primaryKeys = {"id","date"}, tableName = "water_dates")
public class WaterDay {
    @ColumnInfo(name = "id")
    @NonNull
    private Integer id;
    @ColumnInfo(name ="date")
    @NonNull
    private String date;

    @ColumnInfo(name = "water_time")
    @NonNull
    private String waterTime;



    public WaterDay(Integer id, String date, String waterTime) {
        this.id = id;
        this.date = date;
        this.waterTime = waterTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @NonNull
    public String getWaterTime() {
        return waterTime;
    }

    public void setWaterTime(@NonNull String waterTime) {
        this.waterTime = waterTime;
    }
}
