package com.example.waterplant.presentation.ui.diagnose_screen.state;

import java.util.ArrayList;
import java.util.List;

public class DiagnoseUiState {
    private Boolean isFull;
    private List<ThumbnailUiState> thumbnails;
    private Integer size;

    public DiagnoseUiState() {
        isFull = false;
        thumbnails = new ArrayList<>();
        size = 0;
    }
    public DiagnoseUiState(Boolean isFull, List<ThumbnailUiState> thumbnails, Integer size) {
        this.isFull = isFull;
        this.thumbnails = thumbnails;
        this.size = size;
    }

    public Boolean isFull() {
        return isFull;
    }

    public void setFull(Boolean full) {
        isFull = full;
    }

    public List<ThumbnailUiState> getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(List<ThumbnailUiState> thumbnails) {
        this.thumbnails = thumbnails;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
