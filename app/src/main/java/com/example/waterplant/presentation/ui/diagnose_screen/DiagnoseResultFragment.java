package com.example.waterplant.presentation.ui.diagnose_screen;

import static android.app.Activity.RESULT_OK;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.waterplant.R;
import com.example.waterplant.databinding.FragmentDiagnoseResultBinding;
import com.example.waterplant.presentation.ui.diagnose_screen.state.DiagnoseResultUiState;
import com.example.waterplant.presentation.ui.diagnose_screen.state.ThumbnailUiState;
import com.example.waterplant.presentation.ui.diagnose_screen.viewmodel.DiagnoseViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiagnoseResultFragment#} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class DiagnoseResultFragment extends Fragment {


    private FragmentDiagnoseResultBinding binding;
    private DiagnoseViewModel viewModel;
    ActivityResultLauncher<IntentSenderRequest> intentSenderLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK) {
                        Log.d("DiagnoseResult", "delete success ok");
                    }
                }
            }
    );
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(DiagnoseViewModel.class);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDiagnoseResultBinding.inflate(inflater);
        binding.retakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //deleteThumb();
                getActivity().getViewModelStore().clear();
                Navigation.findNavController(view).navigate(R.id.action_diagnoseResultFragment_to_diagnoseFragment);
                deleteThumb();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getDiagnoseResultUiState().observe(getViewLifecycleOwner(), new Observer<DiagnoseResultUiState>() {
            @Override
            public void onChanged(DiagnoseResultUiState diagnoseResultUiState) {
                binding.diagnoseRes.setText(diagnoseResultUiState.getDiagnoseResult());
                binding.advice.setText(diagnoseResultUiState.getAdvice());
                binding.background.setBackground(AppCompatResources.getDrawable(requireContext(), diagnoseResultUiState.getBackground()));
                binding.thumbnailIcon.setImageResource(diagnoseResultUiState.getIcon());
                if(diagnoseResultUiState.getThumbnail() != null) {
                    binding.thumbnail.setImageBitmap(diagnoseResultUiState.getThumbnail());
                }
                if(diagnoseResultUiState.getDiagnoseResult() == R.string.good_plant) {
                    binding.retakeBtn.setVisibility(View.VISIBLE);
                }else {
                    binding.retakeBtn.setVisibility(View.INVISIBLE);
                }
            }
        });

    }


    private void deleteThumb() {
        List<ThumbnailUiState> listThumbs = viewModel.getListThumb();
        List<Uri> listUris = new ArrayList<>();
        ContentResolver contentResolver = getActivity().getContentResolver();
        for (ThumbnailUiState thumb : listThumbs) {
            Uri uri = Uri.parse(thumb.getUrl());
            listUris.add(uri);
        }
        viewModel.deleteListThumb(contentResolver);
        viewModel.hadDiagnosed();
        viewModel.hadSeenResult();
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            IntentSender intentSender = MediaStore.createTrashRequest(contentResolver, listUris, true).getIntentSender();
//            intentSenderLauncher.launch(new IntentSenderRequest.Builder(intentSender).build());
//            for(Uri uri : listUris) {
//                contentResolver.delete(uri, null, null);
//            }
//        }else {
//            for(Uri uri : listUris) {
//                contentResolver.delete(uri, null, null);
//            }
//        }

    }
}