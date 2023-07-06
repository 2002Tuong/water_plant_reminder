package com.example.waterplant.presentation.ui.diagnose_screen.viewmodel;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.waterplant.R;
import com.example.waterplant.common.Constant;
import com.example.waterplant.common.Labels;

import com.example.waterplant.presentation.ui.diagnose_screen.state.DiagnoseResultUiState;
import com.example.waterplant.presentation.ui.diagnose_screen.state.DiagnoseUiState;
import com.example.waterplant.presentation.ui.diagnose_screen.state.ProcessUiState;
import com.example.waterplant.presentation.ui.diagnose_screen.state.ThumbnailUiState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import dagger.hilt.android.lifecycle.HiltViewModel;


public class DiagnoseViewModel extends ViewModel {
    private MutableLiveData<DiagnoseUiState> diagnoseUiState = new MutableLiveData<>(new DiagnoseUiState());
    private MutableLiveData<DiagnoseResultUiState> diagnoseResultUiState = new MutableLiveData<>(new DiagnoseResultUiState());
    private MutableLiveData<ProcessUiState> processUiState = new MutableLiveData<>(new ProcessUiState());
    public LiveData<DiagnoseUiState> getDiagnoseUiState(){
        return diagnoseUiState;
    }
    public LiveData<DiagnoseResultUiState> getDiagnoseResultUiState() { return diagnoseResultUiState;}
    public LiveData<ProcessUiState> getProcessUiState() { return processUiState;}


    public Boolean isMax() {
        if(diagnoseUiState.getValue() != null) {
            return diagnoseUiState.getValue().isFull();
        }
        
       return false;
    }
    //this method process with DiagnoseUiState;
    //update diagnoseUiState;
    public void update(Uri uri, Boolean fromPhotoPicker) {
        if(uri == null) return;
        ThumbnailUiState thumbnail = new ThumbnailUiState(uri.toString(), fromPhotoPicker);
        DiagnoseUiState cur = diagnoseUiState.getValue();
        if(cur.isFull()) return;
        DiagnoseUiState newState = new DiagnoseUiState();

        List<ThumbnailUiState> newList = cur.getThumbnails();
        newList.add(thumbnail);
        newState.setThumbnails(newList);

        newState.setSize(cur.getSize() + 1);

        if(newState.getSize() >= Constant.MAX_QUANTITY_THUMB) {
            newState.setFull(true);
        }else {
            newState.setFull(false);
        }

        diagnoseUiState.postValue(newState);
    }
    public ThumbnailUiState getThumb(Integer index) {
        if(index > diagnoseUiState.getValue().getSize()) return null ;
        return diagnoseUiState.getValue().getThumbnails().get(index);
    }
    public  List<ThumbnailUiState> getListThumb() {
        return diagnoseUiState.getValue().getThumbnails();
    }
    public void deleteListThumb(ContentResolver resolver) {
        for(ThumbnailUiState thumb : diagnoseUiState.getValue().getThumbnails()) {
            if(!thumb.isFromPhotoPicker()) {
                Uri uri = Uri.parse(thumb.getUrl());
                resolver.delete(uri, null,null);
            }
        }
    }
    //get random thumbnail url;
    public String getRandomUrl() {
        Random random = new Random();
        List<ThumbnailUiState> thumbnails = diagnoseUiState.getValue().getThumbnails();
        return thumbnails.get(0).getUrl();
    }
    public void hadDiagnosed() {
        diagnoseUiState.setValue(new DiagnoseUiState());
    }


    //this method process with ProcessUiState
    public void update(Boolean isProcess) {
        processUiState.setValue(new ProcessUiState(isProcess));
    }
    public void update(String thumbnailUrl, Boolean isProcess) {
        processUiState.setValue(new ProcessUiState(thumbnailUrl,isProcess));
    }
    public void endProcess() {
        processUiState.setValue(new ProcessUiState());
    }
    //this method process with ResultDiagnoseUiState
    //labels GOOD->
    //labels BAD ->
    public void update(String labels, Bitmap thumb) {
        if(Objects.equals(labels, Labels.GOOD)) {
            diagnoseResultUiState.setValue(
                    new DiagnoseResultUiState(R.string.good_plant,
                            R.string.no_detect_issue,
                            R.drawable.green_leaf,
                            R.drawable.green_gradient,
                            thumb));
        }else if(Objects.equals(labels, Labels.BAD)) {
            diagnoseResultUiState.setValue(
                    new DiagnoseResultUiState(R.string.underwater,
                            R.string.advice_underwater,
                            R.drawable.red_plant,
                            R.drawable.red_gradient,
                            thumb));
        }
    }
    public void hadSeenResult() {diagnoseResultUiState.setValue(new DiagnoseResultUiState());}

}
