package com.example.waterplant.presentation.ui.main_screen.state;

public class PlantUiState {
    private String name;
    private String thumbnailUrl;
    private String water;

    public PlantUiState(String name, String thumbnailUrl, String water) {
        this.name = name;
        if(thumbnailUrl == null) {
            this.thumbnailUrl = "";
        }else {
            this.thumbnailUrl = thumbnailUrl;
        }
        this.water = water;
    }

    public String getName() {
        return name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getWater() {
        return water;
    }
}
