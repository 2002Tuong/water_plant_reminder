package com.example.waterplant.common;

import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;

import com.example.waterplant.common.env.Utils;
import com.example.waterplant.common.tflite.Classifier;

import com.example.waterplant.common.tflite.YoloV5Classifier;
import com.example.waterplant.ml.LeafClassifier;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ImageClassifier {
    private Float threshHold = 0.5f;
    private Integer maxResult = 3;

    //private ObjectDetector objectDetector = null;
    private Context context;
    private YoloV5Classifier detector = null;
    private String detectLeafModelName = "best-fp16.tflite" ;

    private YoloV5Classifier classifier = null;
    private String classifierModelName = "leaf-classifier.tflite";

    private LeafClassifier leafClassifier = null;
    public ImageClassifier(Context context) throws IOException {

        this.context = context;
        if(detector == null) {
            detector = YoloV5Classifier.create(
                    context.getAssets(),
                    detectLeafModelName,
                    DETECTOR_MODEL_LABELS_FILE,
                    false,
                    DETECTOR_MODEL_INPUT_SIZE);
        }
//        if(classifier == null) {
//            classifier = YoloV5Classifier.create(
//                    context.getAssets(),
//                    classifierModelName,
//                    CLASSIFIER_MODEL_LABELS_FILE,
//                    false,
//                    CLASSIFIER_MODEL_INPUT_SIZE
//            );
//        }
        if(leafClassifier == null) {
            leafClassifier = LeafClassifier.newInstance(context);
        }
        //setupObjectDetector();
    }
    //find leaf in an image of tree
    public List<Classifier.Recognition> detectImage(Bitmap image)  {

        if(detector == null ){
            try {
                detector = YoloV5Classifier.create(
                        context.getAssets(),
                        "best-fp16.tflite",
                        "file:///android_asset/customclasses.txt",
                        false, DETECTOR_MODEL_INPUT_SIZE);
            }catch (IOException e){
                Log.e("ImageClassifier", e.getMessage());
            }
        }

        try {
            final List<Classifier.Recognition> results = detector.recognizeImage(image);
            final List<Classifier.Recognition> results2= new ArrayList<>();
            for(Classifier.Recognition res : results) {
                if(res.getConfidence() >= MINIMUM_CONFIDENCE_TF_OD_API){
                    results2.add(res);
                }
            }
            return results2;
        } catch (IndexOutOfBoundsException e) {
            Log.d("ImageClassifier", e.getMessage());
            e.printStackTrace();
            return null;
        }

    }

    //leaf -> diseased
    //or leaf -> healthy
    public String classifyLeaf(Bitmap source) {
        try {
            final  List<Classifier.Recognition> recognitionList = classifier.recognizeImage(source);
            int index = 0;
            float maxConfident = 0f;
            for(int i = 0; i < recognitionList.size(); i++) {
                if(recognitionList.get(i).getConfidence() > maxConfident) {
                    index = i;
                    maxConfident = recognitionList.get(i).getConfidence();
                }
            }
            return recognitionList.get(index).getTitle();
        }catch (IndexOutOfBoundsException e) {
            Log.d("ImageClassifier", e.getMessage());
            e.printStackTrace();
            return "healthy";
        }
    }
    public String classifyLeaf1(Bitmap source) {
        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1,224,224,3}, DataType.FLOAT32);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4*CLASSIFIER_MODEL_INPUT_SIZE*CLASSIFIER_MODEL_INPUT_SIZE*3);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[CLASSIFIER_MODEL_INPUT_SIZE * CLASSIFIER_MODEL_INPUT_SIZE];
        source.getPixels(intValues, 0, source.getWidth(), 0, 0, source.getWidth(), source.getHeight());
        int pixel = 0;
        for (int i = 0; i < CLASSIFIER_MODEL_INPUT_SIZE; i++) {
            for (int j = 0; j < CLASSIFIER_MODEL_INPUT_SIZE; j++) {
                int val = intValues[pixel++];//RGB
                byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f/255.f));
                byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f/255.f));
                byteBuffer.putFloat((val& 0xFF) * (1.f/255.f));
            }
        }
        inputFeature0.loadBuffer(byteBuffer);
        LeafClassifier.Outputs outputs = leafClassifier.process(inputFeature0);
        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
        float[] confidence = outputFeature0.getFloatArray();
        int index = 0;
        float maxConfidence = 0;
        for (int i = 0; i <confidence.length; i++) {
            if(confidence[i] > maxConfidence) {
                index = i;
                maxConfidence = confidence[i];
            }
        }
        String[] classes = {"diseased", "healthy"};
        return classes[index];
    }
    public String evaluateTree(List<Bitmap> listLeaves) {
        List<String> healthyLeaves = new ArrayList<>();
        List<String> diseasedLeaves = new ArrayList<>();
        for(Bitmap bitmap: listLeaves) {
            String label = classifyLeaf1(bitmap);
            if(!Objects.equals(label, "healthy")) {
                diseasedLeaves.add(label);
            }else {
                healthyLeaves.add(label);
            }
        }
        float confident = (float) diseasedLeaves.size() / (diseasedLeaves.size() + healthyLeaves.size());
        if(confident >= MINIMUM_CONFIDENCE_OF_CLASSIFIER_TREE) {
            return Labels.BAD;
        }
        return Labels.GOOD;
    }


    //resize image
    public Bitmap processImage(Bitmap image, int size) {
        return Utils.processBitmap(image, size);
    }

    public List<Bitmap> processImages(List<Bitmap> sources, int size) {
        List<Bitmap> resizeImages = new ArrayList<>();
        for(Bitmap image: sources) {
            resizeImages.add(processImage(image, size));
        }
        return resizeImages;
    }
    public Bitmap cropImageFromImage(Bitmap source, RectF location) {
        int width = (int) (location.right - location.left) + 1;
        int height = (int) (location.bottom - location.top) + 1;
        return Bitmap.createBitmap(source, (int) location.left, (int) location.top, width, height);
    }
//    public List<Classifier.Recognition> classifyListImage(List<Bitmap> images) {
//        List<String> results = new ArrayList<>();
//        List<Classifier.Recognition> recognitionList = new ArrayList<>();
//        for (Bitmap img : images) {
//            Classifier.Recognition res = classifyImage(img);
//            recognitionList.add(res);
//            if(res != null) {
//                results.add(res.getTitle());
//                Log.e("ImageClassifier", String.valueOf(res.getConfidence()));
//                Log.e("ImageClassifier", res.getTitle());
//            }
//        }
//        return recognitionList;
//    }

    // Releases model resources if no longer used.
    public void clearModel() {
        if(detector != null) {
            detector.close();
        }
        if(classifier != null) {
            classifier.close();
        }
    }

    public static final int DETECTOR_MODEL_INPUT_SIZE = 416;
    public static final String DETECTOR_MODEL_LABELS_FILE = "file:///android_asset/customclasses.txt";
    public static final int CLASSIFIER_MODEL_INPUT_SIZE = 224;
    public static final String CLASSIFIER_MODEL_LABELS_FILE = "file:///android_asset/classifierlabels.txt";
    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.35f;
    public static final float MINIMUM_CONFIDENCE_OF_CLASSIFIER_TREE = 0.3f;
}
