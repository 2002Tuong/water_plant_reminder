package com.example.waterplant.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;

import com.example.waterplant.R;
import com.example.waterplant.database.entities.Plant;
import com.example.waterplant.database.entities.WaterDay;
import com.example.waterplant.databinding.FragmentAddNewPlantBinding;
import com.example.waterplant.viewmodel.PlantWaterViewModel;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.time.LocalTime;
import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddNewPlantFragment#} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class AddNewPlantFragment extends Fragment {

    private FragmentAddNewPlantBinding binding;
    private MaterialTimePicker timePicker;
    //time that user set to water plant
    private String waterTime;
    //delay time workrequest do to the time that user want to water their plant
    private Long initDelayTime;
    private String thumbnailUrl = "";
    private ActivityResultLauncher<String> launcher;
    private PlantWaterViewModel viewModel;
    //the next id to create item
    private static int nextAvailableId = 0;

    //key of above id in SharedPreferences
    private static final String ID = "ID";
    public static final String SHARED_PREFERENCES_NAME = "ID_COUNT_SAVE";
    SharedPreferences sharedPreferences;

    Plant plant = null;
    ArrayList<WaterDay> waterDays;

    //create component
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create launcher
        launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result == null) {
                            thumbnailUrl = "";
                            enabledImage(false);
                            return;
                        }

                        thumbnailUrl = result.toString();
                        Glide.with(requireContext())
                                .load(result)
                                .placeholder(R.drawable.icon_add_item)
                                .error(R.drawable.ic_error_24)
                                .into(binding.thumbnail);
                        enabledImage(true);
                    }
                }
        );
        //create viewmodel
        viewModel = new ViewModelProvider(requireActivity()).get(PlantWaterViewModel.class);
        //create sharePreferences
        sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        nextAvailableId = sharedPreferences.getInt(ID, 0);

        //create time picker and set call back on Click Ok button
        timePicker = createTimePicker();
        timePicker.addOnPositiveButtonClickListener(view -> {

            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            initDelayTime = getDistanceTime(LocalTime.now(), LocalTime.of(hour, minute));
            //format: hh:mm
            //example: 02:03 || 20:13
            waterTime = getFormatTimeFromMaterialTimePicker(hour, minute);
            binding.timeTv.setFormat24Hour(waterTime);
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddNewPlantBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            nextAvailableId = savedInstanceState.getInt(ID);
        }
        binding.icon1.setOnClickListener((v) -> {
            launcher.launch("image/*");
        });
        binding.thumbnail.setOnClickListener((v) -> {
            launcher.launch("image/*");
        });
        binding.saveBtn.setOnClickListener((v) -> {
            waterDays = selectDates();
            plant = createNewPlant();
            if (plant != null && !waterDays.isEmpty()) {
                addNewPlant();
                addDayToWaterPlant(waterDays);
                viewModel.requestReminder(plant, waterDays, initDelayTime);
                nextAvailableId += 1;
                Navigation.findNavController(requireView()).popBackStack();
            } else {
                Toast.makeText(this.requireContext(), "Please check if you select day or not", Toast.LENGTH_SHORT).show();
            }
        });
        binding.timeTv.setOnClickListener((v) -> {
            timePicker.show(getParentFragmentManager(), "time picker");
        });

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ID, nextAvailableId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sharedPreferences.edit().putInt(ID, nextAvailableId).apply();
    }

    //show thumbnail when you set
    //when you click add thumbnail then you set it call this method to show that thumbnail
    private void enabledImage(Boolean isEnable) {
        if (isEnable) {
            binding.thumbnail.setVisibility(View.VISIBLE);
            binding.icon1.setVisibility(View.INVISIBLE);
            binding.icon1.setVisibility(View.INVISIBLE);
        } else {
            binding.thumbnail.setVisibility(View.INVISIBLE);
            binding.icon1.setVisibility(View.VISIBLE);
            binding.icon1.setVisibility(View.VISIBLE);
        }
    }

    //isEnabled == true =>  enabled error
    //isEnabled == false => not enabled error
    private void enableErrorName(Boolean isEnabled) {
        if (isEnabled) {
            binding.plantNameTextField.setError("enter a name for your plant");
        }
        binding.plantNameTextField.setErrorEnabled(isEnabled);
    }

    /*    private void enableErrorFrequency(Boolean isEnabled){
    //        if(isEnabled){
    //            binding.frequencyTextField.setError("please enter a number: 10, 100, ...");
    //        }
    //        binding.frequencyTextField.setErrorEnabled(isEnabled);
        }

     */
    private void enableErrorWater(Boolean isEnabled) {
        if (isEnabled) {
            binding.waterTextField.setError("please enter a number: 100, 250, ...");
        }
        binding.waterTextField.setErrorEnabled((isEnabled));
    }

    private void enableErrorTemp(Boolean isEnabled) {
        if (isEnabled) {
            binding.temperatureTextField.setError("please enter a temperature in degree(s) Celsius  ");
        }
        binding.temperatureTextField.setErrorEnabled(isEnabled);
    }

    private void enableErrorLight(Boolean isEnabled) {
        if (isEnabled) {
            binding.lightTextField.setError("there are only three levels: low, medium, high");
        }
        binding.lightTextField.setErrorEnabled(isEnabled);
    }

    private Plant createNewPlant() {
        String name = binding.plantName.getText().toString();
        String frequency = String.valueOf(waterDays.size());  //binding.frequency.getText().toString();
        String water = binding.water.getText().toString();
        String temp = binding.temp.getText().toString();
        String light = binding.light.getText().toString();

        //equal to false if the text is correct format
        //equal to true if the text is incorrect format
        Boolean isEnabledErrorName = !viewModel.checkNameFormat(name);
        //Boolean isEnabledErrorFrequency = !viewModel.checkFrequencyFormat(frequency);
        Boolean isEnabledErrorWater = !viewModel.checkWaterFormat(water);
        Boolean isEnabledErrorTemp = !viewModel.checkTempFormat(temp);
        Boolean isEnabledErrorLight = !viewModel.checkLightFormat(light);

        if (!isEnabledErrorName &&
                /*!isEnabledErrorFrequency*/
                !isEnabledErrorWater &&
                !isEnabledErrorTemp &&
                !isEnabledErrorLight) {
            return viewModel.createNewPlant(nextAvailableId, name, frequency, light, water, temp, thumbnailUrl);
        } else {
            enableErrorName(isEnabledErrorName);
            //enableErrorFrequency(isEnabledErrorFrequency);
            enableErrorWater(isEnabledErrorWater);
            enableErrorTemp(isEnabledErrorTemp);
            enableErrorLight(isEnabledErrorLight);
            return null;
        }

    }

    private Boolean addNewPlant() {
        if (plant != null) {
            viewModel.insertNewPlant(plant);
            return true;
        }
        return false;
    }

    private Boolean addDayToWaterPlant(ArrayList<WaterDay> dates) {
        if (dates.isEmpty()) return false;
        for (WaterDay day : dates) {
            viewModel.insertWaterDay(day);
        }
        return true;
    }

    private ArrayList<WaterDay> selectDates() {
        ArrayList<WaterDay> dates = new ArrayList<>();
        if (binding.monCheckBox.isChecked())
            dates.add(viewModel.createNewWaterDay(nextAvailableId, "mon", waterTime));
        if (binding.tueCheckBox.isChecked())
            dates.add(viewModel.createNewWaterDay(nextAvailableId, "tue", waterTime));
        if (binding.wedCheckBox.isChecked())
            dates.add(viewModel.createNewWaterDay(nextAvailableId, "wed",waterTime));
        if (binding.thuCheckBox.isChecked())
            dates.add(viewModel.createNewWaterDay(nextAvailableId, "thu",waterTime));
        if (binding.friCheckBox.isChecked())
            dates.add(viewModel.createNewWaterDay(nextAvailableId, "fri",waterTime));
        if (binding.satCheckBox.isChecked())
            dates.add(viewModel.createNewWaterDay(nextAvailableId, "sat",waterTime));
        if (binding.sunCheckBox.isChecked())
            dates.add(viewModel.createNewWaterDay(nextAvailableId, "sun",waterTime));
        return dates;
    }


    private MaterialTimePicker createTimePicker() {
        boolean isSystem24Hour = DateFormat.is24HourFormat(requireActivity());
        Integer clockFormat = null;
        if (isSystem24Hour) {
            clockFormat = TimeFormat.CLOCK_24H;
        } else clockFormat = TimeFormat.CLOCK_12H;
        return new MaterialTimePicker.Builder()
                .setTimeFormat(clockFormat)
                .setTitleText("Time to water your plant")
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .build();
    }

    String getFormatTimeFromMaterialTimePicker(int hour, int minute) {
        String time = "";
        if (hour >= 10) {
            time = time + hour;
        } else {
            time += "0" + hour;
        }
        if (minute >= 10) {
            time += ":" + minute;
        } else {
            time += ":0" + minute;
        }
        return time;
    }

    public static long getDistanceTime(LocalTime curTime, LocalTime waterTime) {

        Long curTimeInMili = localTimeToMili(curTime);
        Long waterTimeInMili = localTimeToMili(waterTime);
        if (waterTimeInMili - curTimeInMili >= 0) {
            return waterTimeInMili - curTimeInMili;
        } else {
            return waterTimeInMili - curTimeInMili + 24 * 3600000L;
        }
    }

    public static Long localTimeToMili(LocalTime time) {
        return (time.getHour() * 3600000L + time.getMinute() * 60000L);
    }
}