package com.example.waterplant.presentation.ui.main_screen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.waterplant.common.adapter.OnItemClickListener;
import com.example.waterplant.common.adapter.PlantListAdapter;
import com.example.waterplant.data.database.entities.Plant;
import com.example.waterplant.databinding.FragmentListPlantBinding;
import com.example.waterplant.common.util.OnItemTouchCallBack;
import com.example.waterplant.common.util.PlantItemTouchHelperCallBack;
import com.example.waterplant.presentation.ui.main_screen.viewmodel.PlantWaterViewModel;



import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListPlantFragment#} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class ListPlantFragment extends Fragment implements OnItemTouchCallBack {

    private FragmentListPlantBinding binding;
    private PlantWaterViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(PlantWaterViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentListPlantBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        OnItemClickListener callBack = (Plant plant) -> {
            NavDirections dir = ListPlantFragmentDirections.actionListPlantFragmentToDetailPlantFragment(plant.getId());
            Navigation.findNavController(view).navigate(dir);
        };
        ItemTouchHelper helper = new ItemTouchHelper(new PlantItemTouchHelperCallBack(this));

        PlantListAdapter adapterTodayRecycleView = new PlantListAdapter(new PlantListAdapter.DiffCallBack(), callBack);
        viewModel.getAllPlantsToWateringToday().observe(getViewLifecycleOwner(),adapterTodayRecycleView::submitList);
        binding.recycleViewToday.setAdapter(adapterTodayRecycleView);
        helper.attachToRecyclerView(binding.recycleViewToday);
        binding.recycleViewToday.setLayoutManager(new LinearLayoutManager(requireContext()));

        PlantListAdapter adapterYourPlantRecycleView = new PlantListAdapter(new PlantListAdapter.DiffCallBack(), callBack);
        binding.recycleViewYourPlants.setAdapter(adapterYourPlantRecycleView);
        viewModel.getAllPlants().observe(getViewLifecycleOwner(), adapterYourPlantRecycleView::submitList);
        helper.attachToRecyclerView(binding.recycleViewYourPlants);
        binding.recycleViewYourPlants.setLayoutManager(new LinearLayoutManager(requireContext()));

        binding.addNewPlant.setOnClickListener((t) -> {
            @NonNull NavDirections action = ListPlantFragmentDirections.actionListPlantFragmentToAddNewPlantFragment();
            Navigation.findNavController(t).navigate(action);
        });

    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d("ListPlantFragment", "onStopCall");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("ListPlantFragment", "onDestroyViewCall");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ListPlantFragment", "onDestroyCall");
    }

    @Override
    public void onMove(int bindingAdapterPosition, int bindingAdapterPosition1) {

    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder) {
        int position = (int)viewHolder.getBindingAdapterPosition();
        PlantListAdapter adapter = (PlantListAdapter) viewHolder.getBindingAdapter();
        Plant plant = adapter.getItemAtPosition(position);
        viewModel.deletePlant(plant);
        viewModel.deleteDates(plant);
        viewModel.cancelRequest(plant.getId().toString());
    }
}