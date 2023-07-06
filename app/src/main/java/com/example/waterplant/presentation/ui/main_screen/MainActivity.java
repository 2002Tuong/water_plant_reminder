package com.example.waterplant.presentation.ui.main_screen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.waterplant.common.Constant;
import com.example.waterplant.R;
import com.example.waterplant.databinding.ActivityMainBinding;
import com.example.waterplant.presentation.ui.diagnose_screen.DiagnoseActivity;
import com.example.waterplant.presentation.ui.main_screen.viewmodel.PlantWaterViewModel;
import com.example.waterplant.data.worker.ChangeTodayWorker;

import org.checkerframework.checker.units.qual.K;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {



    private ActivityMainBinding binding;
    private NavController navController;
    private PlantWaterViewModel viewModel;
    private SharedPreferences sharedPreferences;
    private final String KEY = "KEY_FOR_SHARE_PREFERENCES";
    private Boolean isCreateRequest;
    @Inject
    WorkManager workManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = this.getPreferences(MODE_PRIVATE);
        viewModel = new ViewModelProvider(this).get(PlantWaterViewModel.class);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_hot_fragment);
        navController = navHostFragment.getNavController();
        createChannel(Constant.CHANNEL_NAME, Constant.CHANNEL_ID);
        isCreateRequest = sharedPreferences.getBoolean(KEY, false);
        if(!isCreateRequest) {
            viewModel.insertNow();
            createRequest();
            isCreateRequest= true;
        }
        NavigationUI.setupWithNavController(binding.topAppBar, navController);
        binding.topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.diagnose) {
                    startActivity(new Intent(getApplicationContext() ,DiagnoseActivity.class));
                }else if(item.getItemId() == R.id.searchableDialogFragment) {
                    return NavigationUI.onNavDestinationSelected(item, navController);
                }
                return true;
            }
        });
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if(navDestination.getId() == R.id.searchableDialogFragment) {
                    binding.coordinatorLayout.setVisibility(View.GONE);
                }else {
                    binding.coordinatorLayout.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity", "onPauseCall");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY, true);
        editor.apply();
    }




    private void createChannel(String channelName, String channelId) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setLightColor(Color.BLUE);
        channel.enableLights(true);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE) ;
        manager.createNotificationChannel(channel);
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