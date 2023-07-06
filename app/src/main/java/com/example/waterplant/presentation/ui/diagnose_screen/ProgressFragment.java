package com.example.waterplant.presentation.ui.diagnose_screen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.Data;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.waterplant.R;
import com.example.waterplant.common.Constant;
import com.example.waterplant.common.ImageClassifier;
import com.example.waterplant.common.Labels;
import com.example.waterplant.common.env.Utils;
import com.example.waterplant.common.tflite.Classifier;
import com.example.waterplant.common.tflite.YoloV5Classifier;
import com.example.waterplant.databinding.FragmentProgressBinding;
import com.example.waterplant.presentation.ui.diagnose_screen.state.ProcessUiState;
import com.example.waterplant.presentation.ui.diagnose_screen.state.ThumbnailUiState;
import com.example.waterplant.presentation.ui.diagnose_screen.viewmodel.DiagnoseViewModel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProgressFragment#} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class ProgressFragment extends Fragment {

    private DiagnoseReceiver receiver;
    private DiagnoseViewModel viewModel;
    private FragmentProgressBinding binding;
    private ImageClassifier detector;
    private class DiagnoseReceiver extends BroadcastReceiver {
        DiagnoseViewModel viewModel;
        public DiagnoseReceiver(DiagnoseViewModel viewModel) {
            super();
            this.viewModel = viewModel;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            String label = intent.getStringExtra(DiagnoseWorker.RESULT_VALUE);
//            if(Objects.equals(action, DiagnoseWorker.ACTION_COMPLETE_DIAGNOSE)) {
//                viewModel.update(label);
//                viewModel.update(false);
//            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(DiagnoseViewModel.class);
        try {
//            detector = YoloV5Classifier.create(
//                    requireActivity().getAssets(),
//                    "best-fp16.tflite",
//                    Constant.TF_OD_API_LABELS_FILE,
//                    false,
//                    Constant.TF_OD_API_INPUT_SIZE);
            detector = new ImageClassifier(requireContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProgressBinding.inflate(inflater);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getProcessUiState().observe(getViewLifecycleOwner(), new Observer<ProcessUiState>() {
            @Override
            public void onChanged(ProcessUiState newProcessUiState) {
                //process is completed
                if(!newProcessUiState.isProcess()) {
                    viewModel.endProcess();
                    Navigation.findNavController(view).navigate(R.id.action_progressFragment_to_diagnoseResultFragment);
                }
                if(newProcessUiState.getUrl() != null ){
                    Glide.with(getContext()).load(Uri.parse(newProcessUiState.getUrl())).into(binding.thumbnail);
                }
            }

        });
        processImage(getImageToProcess());
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new DiagnoseReceiver(viewModel);
        //LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, new IntentFilter(DiagnoseWorker.ACTION_COMPLETE_DIAGNOSE));

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("ProgressFragment", "onDestroyViewCall");
        //LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private List<Bitmap> getImageToProcess() {
        List<Bitmap> images = new ArrayList<>();
        for(ThumbnailUiState thumbnail : viewModel.getListThumb()) {
            String url = thumbnail.getUrl();
            Bitmap thumb = null;
            try {
                thumb = BitmapFactory.decodeStream(
                        requireContext().getContentResolver().openInputStream(Uri.parse(url))
                );
                Log.d("DiagnoseWorker", thumb.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                thumb = null;
            }
            if(thumb != null) images.add(thumb);
        }
        return images;
    }
    private void processImage(List<Bitmap> images) {
        Handler handler = new Handler();
        List<Bitmap> croppedImages = detector.processImages(images, ImageClassifier.DETECTOR_MODEL_INPUT_SIZE);

        new Thread(() -> {
            List<Bitmap> leaves = new ArrayList<>();
            List<Classifier.Recognition> drawRectList = new ArrayList<>();
            for(int i = 0; i < croppedImages.size(); i++) {
                final  List<Classifier.Recognition> results = detector.detectImage(croppedImages.get(i));
                if(i==0) drawRectList = results;
                for(Classifier.Recognition object : results ) {
                    Bitmap temp = detector.cropImageFromImage(croppedImages.get(i), object.getLocation());
                    leaves.add(detector.processImage(temp, ImageClassifier.CLASSIFIER_MODEL_INPUT_SIZE));
                }
            }
            String label = detector.evaluateTree(leaves);
            List<Classifier.Recognition> finalDrawRectList = drawRectList;
            handler.post(() -> {
                Bitmap temp = handleResult(croppedImages.get(0), finalDrawRectList);
                viewModel.update(label, temp);
                viewModel.update(false);
            });
        }).start();
    }

    private Bitmap handleResult(Bitmap bitmap, List<Classifier.Recognition> results) {
        final Bitmap drawBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        final Canvas canvas = new Canvas(drawBitmap);
        final Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);

        for (final Classifier.Recognition result : results) {
            final RectF location = result.getLocation();
            if (location != null && result.getConfidence() >= ImageClassifier.MINIMUM_CONFIDENCE_TF_OD_API) {
                canvas.drawRect(location, paint);
            }
        }
        return drawBitmap;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        detector.clearModel();
    }
}