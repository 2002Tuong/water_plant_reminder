package com.example.waterplant.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.waterplant.database.PlantDao;
import com.example.waterplant.database.entities.Plant;
import com.example.waterplant.database.entities.Today;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

//change day in week
@HiltWorker
public class ChangeTodayWorker extends Worker {


    PlantDao dao;
    @AssistedInject
    public ChangeTodayWorker(@Assisted @NonNull Context context, @Assisted @NonNull WorkerParameters workerParams, PlantDao dao) {
        super(context, workerParams);

        this.dao = dao;
    }


    @NonNull
    @Override
    public Result doWork() {
        Log.e(ChangeTodayWorker.class.getName(), "Running");
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("EEE", Locale.US);
        String today = format.format(date);
        Log.e(ChangeTodayWorker.class.getName(), today);
        String temp = today.toLowerCase();
        Log.e(ChangeTodayWorker.class.getName(), temp);
        dao.updateToday(new Today(1,temp));
        return Result.success();
    }
}
