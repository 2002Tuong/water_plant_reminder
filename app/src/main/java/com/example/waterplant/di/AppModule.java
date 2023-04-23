package com.example.waterplant.di;

import android.app.Application;
import android.content.Context;

import androidx.work.WorkManager;

import com.example.waterplant.database.PlantDao;
import com.example.waterplant.database.PlantDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;

import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {


    @Provides
    @Singleton
    public PlantDatabase providePlantDatabase(@ApplicationContext Context context) {
        return PlantDatabase.getInstance(context);
    }

    @Provides
    @Singleton
    public PlantDao providePlantDao(PlantDatabase plantDatabase) {
        return plantDatabase.getDao();
    }

    @Provides
    @Singleton
    public WorkManager provideWorkManager(@ApplicationContext Context context) { return WorkManager.getInstance(context);}
}
