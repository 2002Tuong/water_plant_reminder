package com.example.waterplant;

import android.app.Application;

import androidx.hilt.work.HiltWorkerFactory;
import androidx.work.Configuration;
import androidx.work.WorkManager;

import com.example.waterplant.database.PlantDatabase;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class PlantApplication extends Application {

    @Inject
    HiltWorkerFactory hiltWorkerFactory;
    @Override
    public void onCreate() {
        super.onCreate();
        WorkManager.initialize(
                this.getApplicationContext(),
                new Configuration.Builder()
                        .setWorkerFactory(hiltWorkerFactory)
                        .build()
        );
    }
}
