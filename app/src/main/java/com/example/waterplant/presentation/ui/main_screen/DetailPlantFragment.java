package com.example.waterplant.presentation.ui.main_screen;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import com.example.waterplant.R;
import com.example.waterplant.data.database.entities.Plant;
import com.example.waterplant.databinding.FragmentDetailPlantBinding;
import com.example.waterplant.presentation.ui.main_screen.viewmodel.PlantWaterViewModel;



import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailPlantFragment#} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class DetailPlantFragment extends Fragment {

   private FragmentDetailPlantBinding binding;
   private PlantWaterViewModel viewModel;
   private Plant plant;
   private ActivityResultLauncher<PickVisualMediaRequest> launcher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(PlantWaterViewModel.class);

        launcher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), result -> {
            if(result != null) {
                plant.setThumbnailUrl(result.toString());
                viewModel.updatePlant(plant);
            }
        } );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDetailPlantBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Integer id = DetailPlantFragmentArgs.fromBundle(getArguments()).getId();
        viewModel.getItem(id).observe(getViewLifecycleOwner(), (item) -> {
            plant = item;
            bind(item);
        });
        binding.thumbnail.setOnClickListener((v) -> {
            launcher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
            Log.d("DetailFragment", "Click Click");
        });


    }


    private void bind(Plant plant) {
        binding.frequency.setText(getString(R.string.frequency, Integer.parseInt(plant.getFrequency())));
        binding.water.setText(getString(R.string.water, Integer.parseInt(plant.getWater())));
        binding.plantName.setText(plant.getName());
        binding.temp.setText(getString(R.string.temp, plant.getTemp()));
        binding.light.setText(plant.getLight());

        if(plant.getThumbnailUrl().isEmpty()){
            Log.d("Detail","Running false");

        }else {
            Log.d("Detail","Running");
            Glide.with(binding.getRoot())
                    .load(Uri.parse(plant.getThumbnailUrl()))
                    .into(binding.thumbnail);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("DetailPlantFragment","onDetachCall");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        launcher.unregister();
        Log.d("DetailPlantFragment","onDestroyCall");
    }
}