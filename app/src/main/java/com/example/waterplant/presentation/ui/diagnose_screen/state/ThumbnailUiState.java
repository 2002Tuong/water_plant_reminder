package com.example.waterplant.presentation.ui.diagnose_screen.state;

public class ThumbnailUiState {
    private String url;
    private String name;
    private Boolean fromPhotoPicker;
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean isFromPhotoPicker() {
        return fromPhotoPicker;
    }
    //from photo picker or from camera
    public void setTakeFrom(Boolean fromPhotoPicker) {
        this.fromPhotoPicker = fromPhotoPicker;
    }

    public ThumbnailUiState(String url) {
        this.url = url;
    }

    public ThumbnailUiState(String url, Boolean fromPhotoPicker) {
        this.url = url;
        this.fromPhotoPicker = fromPhotoPicker;
    }

}
