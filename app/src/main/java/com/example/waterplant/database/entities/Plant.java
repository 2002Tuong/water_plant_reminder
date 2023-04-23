package com.example.waterplant.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "plant")
public class Plant {
    private static int nextIdAvailable = 0;
    @PrimaryKey
    private Integer id;
    private String name;
    private String frequency;
    private String light;
    private String water;
    private String temp;
    @ColumnInfo(name = "thumbnail_url")
    private String thumbnailUrl;

    public Plant(Integer id, String name, String frequency, String light, String water, String temp, String thumbnailUrl) {
        this.id = id;
        this.name = name;
        this.frequency = frequency;
        this.light = light;
        this.water = water;
        this.temp = temp;
        this.thumbnailUrl = thumbnailUrl;
    }
    @Ignore
    public Plant(String name, String frequency, String light, String water, String temp, String thumbnailUrl) {
        this.id = nextIdAvailable;
        this.name = name;
        this.frequency = frequency;
        this.light = light;
        this.water = water;
        this.temp = temp;
        this.thumbnailUrl = thumbnailUrl;
        nextIdAvailable ++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getLight() {
        return light;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public String getWater() {
        return water;
    }

    public void setWater(String water) {
        this.water = water;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
