package com.example.waterplant.presentation.ui.diagnose_screen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;

import com.example.waterplant.R;
import com.example.waterplant.databinding.ActivityDiagnoseBinding;
import com.example.waterplant.presentation.ui.diagnose_screen.viewmodel.DiagnoseViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlow;
@AndroidEntryPoint
public class DiagnoseActivity extends AppCompatActivity {
    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.3f;

    ActivityDiagnoseBinding binding;
    NavController navController;
    DiagnoseViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiagnoseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if(navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }


    }


}