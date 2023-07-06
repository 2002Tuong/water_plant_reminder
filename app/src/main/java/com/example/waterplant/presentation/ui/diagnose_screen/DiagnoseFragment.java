package com.example.waterplant.presentation.ui.diagnose_screen;



import static android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION;
import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.WorkManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.waterplant.R;
import com.example.waterplant.common.Constant;
import com.example.waterplant.databinding.FragmentDiagnoseBinding;
import com.example.waterplant.presentation.ui.diagnose_screen.state.DiagnoseUiState;
import com.example.waterplant.presentation.ui.diagnose_screen.state.ThumbnailUiState;
import com.example.waterplant.presentation.ui.diagnose_screen.viewmodel.DiagnoseViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiagnoseFragment#} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class DiagnoseFragment extends Fragment {

    private FragmentDiagnoseBinding binding;
    private DiagnoseViewModel viewModel;
    ImageCapture imageCapture;
    //this is for request permission needed to launcher camera
    ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    for( Boolean res : result.values()) {
                        if(!res) {
                            Toast.makeText(requireContext(), "please give permission to access your camera", Toast.LENGTH_SHORT).show();
                            return;

                        }
                    }
                    startCamera();
                }
            });
    //user also can pick image in library with this
    ActivityResultLauncher<PickVisualMediaRequest> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.PickVisualMedia(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    viewModel.update(result, true);
                }
            }
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!allPermissionGranted()) {
            requestPermissionLauncher.launch(Constant.REQUEST_PERMISSIONS().toArray(new String[0]));
        }
        //if Device SDK > 30 , we need permission to access Scoped storage
        //to delete thumbnail that user delete
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            Intent intent = new Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("require access to file")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(intent);
                        }
                    })
                    .create()
                    .show();
        }
        startCamera();

        viewModel = new ViewModelProvider(getActivity()).get(DiagnoseViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDiagnoseBinding.inflate(inflater);
        binding.photoGalleryBtnBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageLauncher.launch(
                      new PickVisualMediaRequest.Builder()
                              .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                              .build()
                );
            }
        });
        binding.takePhotoBtn.setOnClickListener(view -> takePhoto());

        binding.okBtn.setOnClickListener(view -> {
            String url = viewModel.getRandomUrl();
            //update ProcessUiState
            viewModel.update(url,true);
            Navigation.findNavController(view).navigate(R.id.action_diagnoseFragment_to_progressFragment);
        });
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getDiagnoseUiState().observe(getViewLifecycleOwner(), new Observer<DiagnoseUiState>() {
            @Override
            public void onChanged(DiagnoseUiState newState) {
                if(newState.getSize() == 1) {
                    binding.thumbnail1.setImageURI(Uri.parse(newState.getThumbnails().get(0).getUrl()));
                }else if(newState.getSize() == 2) {
                    binding.thumbnail1.setImageURI(Uri.parse(newState.getThumbnails().get(0).getUrl()));
                    binding.thumbnail2.setImageURI(Uri.parse(newState.getThumbnails().get(1).getUrl()));
                }else if(newState.getSize() == 3) {
                    binding.thumbnail1.setImageURI(Uri.parse(newState.getThumbnails().get(0).getUrl()));
                    binding.thumbnail2.setImageURI(Uri.parse(newState.getThumbnails().get(1).getUrl()));
                    binding.thumbnail3.setImageURI(Uri.parse(newState.getThumbnails().get(2).getUrl()));
                }

                if(newState.isFull()) {
                    //TODO: navigation
                    String url = viewModel.getRandomUrl();
                    //update ProcessUiState
                    viewModel.update(url,true);
                    Navigation.findNavController(view).navigate(R.id.action_diagnoseFragment_to_progressFragment);
                }

                if(newState.getSize() == 0) {
                    binding.okBtn.setVisibility(View.INVISIBLE);
                }else {
                    binding.okBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requestPermissionLauncher.unregister();
        pickImageLauncher.unregister();
    }

    private Boolean allPermissionGranted() {
        for(String permission : Constant.REQUEST_PERMISSIONS()) {
            if( ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }

        return true;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
                    ProcessCameraProvider cameraProvider;
                    try {
                        cameraProvider = cameraProviderFuture.get();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                        cameraProvider = null;
                    }

                    if(cameraProvider != null) {
                        //Preview use case
                        Preview preview = new Preview.Builder()
                                .build();

                        preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

                        //Image capture use case
                        imageCapture =new ImageCapture.Builder()
                                .build();

                        // Select back camera as a default
                        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                        try {
                            cameraProvider.unbindAll();
                            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture );
                        } catch (Exception e) {

                        }
                    }
                }
                , ContextCompat.getMainExecutor(requireContext()));
    }

    private void takePhoto() {
        if(imageCapture == null) return;
        // Create time stamped name and MediaStore entry.
        String name = new SimpleDateFormat(Constant.FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image");
        }
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
                getActivity().getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
        )
                .build();
        imageCapture.takePicture(outputFileOptions,
                Executors.newSingleThreadExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        if(outputFileResults.getSavedUri() != null) {
                            viewModel.update(outputFileResults.getSavedUri(), false);

                        }
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}