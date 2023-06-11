package com.example.waterplant.di;

import android.content.Context;

import androidx.work.WorkManager;

import com.example.waterplant.data.database.PlantDao;
import com.example.waterplant.data.database.PlantDatabase;
import com.example.waterplant.data.database.WaterDayDao;
import com.example.waterplant.data.repository.PlantRepository;
import com.example.waterplant.data.repository.PlantRepositoryImp;
import com.example.waterplant.data.repository.ToDayRepositoryImp;
import com.example.waterplant.data.repository.TodayRepository;
import com.example.waterplant.data.repository.WaterDateRepositoryImp;
import com.example.waterplant.data.repository.WaterDayRepository;

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
        return plantDatabase.getPlantDao();
    }

    @Provides
    @Singleton
    public WaterDayDao provideWaterDayDao(PlantDatabase database) {
        return database.getWaterDayDao();
    }

    @Provides
    @Singleton
    public PlantRepository providePlantRepository(PlantDao dao) {
        return new PlantRepositoryImp(dao);
    }

    @Provides
    @Singleton
    public WaterDayRepository provideWaterDayRepository(WaterDayDao dao) {
        return new WaterDateRepositoryImp(dao);
    }

    @Provides
    @Singleton
    public TodayRepository provideTodayRepository(PlantDao dao) {
        return new ToDayRepositoryImp(dao);
    }

    @Provides
    @Singleton
    public WorkManager provideWorkManager(@ApplicationContext Context context) { return WorkManager.getInstance(context);}
}
