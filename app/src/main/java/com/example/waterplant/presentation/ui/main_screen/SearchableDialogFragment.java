package com.example.waterplant.presentation.ui.main_screen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.waterplant.common.adapter.OnItemClickListener;
import com.example.waterplant.common.adapter.PlantListAdapter;
import com.example.waterplant.common.adapter.SearchListAdapter;
import com.example.waterplant.databinding.FragmentSearchableDialogBinding;
import com.example.waterplant.presentation.ui.main_screen.viewmodel.PlantWaterViewModel;


import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchableDialogFragment#} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class SearchableDialogFragment extends Fragment implements SearchView.OnQueryTextListener {
    private FragmentSearchableDialogBinding binding;

    public static String NAME = "SEARCH_FRAGMENT";

    private PlantWaterViewModel viewModel;
    SearchListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(PlantWaterViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchableDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        OnItemClickListener callBack = plant -> {
            NavDirections action = SearchableDialogFragmentDirections.actionSearchableDialogFragmentToDetailPlantFragment(plant.getId());
            Navigation.findNavController(view).navigate(action);
        };


        adapter = new SearchListAdapter(new PlantListAdapter.DiffCallBack(), callBack);
        binding.recycleViewSearch.setAdapter(adapter);
        binding.recycleViewSearch.setLayoutManager(new LinearLayoutManager(this.getContext()));


        binding.searchView.setOnQueryTextListener(this);


    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText != null) {
            viewModel.getSearchPlants(newText+"%").observe(getViewLifecycleOwner(), adapter::submitList);
        }else {
            viewModel.getSearchPlants("").observe(getViewLifecycleOwner(), adapter::submitList);
        }
        return true;
    }

}