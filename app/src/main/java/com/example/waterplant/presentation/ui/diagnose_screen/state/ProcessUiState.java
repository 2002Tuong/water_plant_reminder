package com.example.waterplant.presentation.ui.diagnose_screen.state;

public class ProcessUiState {
    private String url;
    private Boolean isProcess;

    public ProcessUiState() {
        this.url = null;
        this.isProcess = true;
    }

    public ProcessUiState(String url, Boolean isProcess) {
        this.url = url;
        this.isProcess = isProcess;
    }

    public ProcessUiState(Boolean isProcess) {
        this.isProcess = isProcess;
    }

    public String getUrl() {
        return url;
    }

    public Boolean isProcess() {
        return isProcess;
    }
}
