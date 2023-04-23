package com.example.waterplant.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.waterplant.Constant;
import com.example.waterplant.R;
import com.example.waterplant.databinding.ActivityMainBinding;
import com.example.waterplant.viewmodel.PlantWaterViewModel;
import com.example.waterplant.worker.ChangeTodayWorker;

import java.time.LocalTime;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private PlantWaterViewModel viewModel;
    @Inject
    WorkManager workManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_hot_fragment);
        navController = navHostFragment.getNavController();
        createChannel(Constant.CHANNEL_NAME, Constant.CHANNEL_ID);
        createRequest();
    }

    private void createChannel(String channelName, String channelId) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLightColor(Color.BLUE);
            channel.enableLights(true);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE) ;
            manager.createNotificationChannel(channel);
        }
    }

    //this will create request that change the watering plant today
    void createRequest() {
        Log.e(MainActivity.class.getName(), "main is running");
        LocalTime midNightTime = LocalTime.of(0,5);
        LocalTime curTime = LocalTime.now();
        Long distance = AddNewPlantFragment.getDistanceTime(curTime, midNightTime);

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(ChangeTodayWorker.class,1, TimeUnit.DAYS)
                .setInitialDelay(distance, TimeUnit.MILLISECONDS)
                .build();
        workManager.enqueueUniquePeriodicWork("change_today_request", ExistingPeriodicWorkPolicy.KEEP,request);

        }


}