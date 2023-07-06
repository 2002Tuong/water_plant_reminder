package com.example.waterplant.presentation.ui.diagnose_screen.state;

import android.graphics.Bitmap;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.example.waterplant.R;

public class DiagnoseResultUiState {
    @StringRes
    private Integer diagnoseResult;
    @StringRes
    private Integer advice;
    @DrawableRes
    private Integer icon;
    @DrawableRes
    private Integer background;

    private Bitmap thumbnail = null;
    public DiagnoseResultUiState() {
        diagnoseResult = R.string.good_plant;
        advice = R.string.no_detect_issue;
        icon = R.drawable.green_leaf;
        background = R.drawable.green_gradient;
        thumbnail = null;
    }
    public DiagnoseResultUiState(Integer diagnoseResult, Integer advice) {
        this.diagnoseResult = diagnoseResult;
        this.advice = advice;
    }

    public DiagnoseResultUiState(Integer diagnoseResult, Integer advice, Integer icon, Integer background, Bitmap thumb) {
        this.diagnoseResult = diagnoseResult;
        this.advice = advice;
        this.icon = icon;
        this.background = background;
        this.thumbnail = thumb;
    }

    public Integer getDiagnoseResult() {
        return diagnoseResult;
    }

    public Integer getAdvice() {
        return advice;
    }

    public Integer getIcon() {
        return icon;
    }

    public Integer getBackground() {
        return background;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }
}
