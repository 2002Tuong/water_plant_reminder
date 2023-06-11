package com.example.waterplant.presentation.ui.main_screen.state;

import java.util.List;

public class ListPlantUiState {
    private List<PlantUiState> today;
    private List<PlantUiState> allPlant;

    public List<PlantUiState> getToday() {
        return today;
    }

    public List<PlantUiState> getAllPlant() {
        return allPlant;
    }

    public ListPlantUiState(List<PlantUiState> today, List<PlantUiState> allPlant) {
        this.today = today;
        this.allPlant = allPlant;
    }
}
