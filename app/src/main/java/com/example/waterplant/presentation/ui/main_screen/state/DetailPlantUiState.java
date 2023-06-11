package com.example.waterplant.presentation.ui.main_screen.state;

import androidx.room.ColumnInfo;

public class DetailPlantUiState {
    private String name;
    private String frequency;
    private String light;
    private String water;
    private String temp;
    private String thumbnailUrl;

    public DetailPlantUiState(String name, String frequency, String light, String water, String temp, String thumbnailUrl) {
        this.name = name;
        this.frequency = frequency;
        this.light = light;
        this.water = water;
        this.temp = temp;
        if(thumbnailUrl==null) {
            this.thumbnailUrl = "";
        }else {
            this.thumbnailUrl = thumbnailUrl;
        }

    }

    public String getName() {
        return name;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getLight() {
        return light;
    }

    public String getWater() {
        return water;
    }

    public String getTemp() {
        return temp;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
