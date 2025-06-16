package com.app.grabfoodapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.app.grabfoodapp.dto.FoodRecognitionResult;
import com.google.gson.Gson;

import org.tensorflow.lite.Interpreter;
import org.threeten.bp.LocalDateTime;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class FoodRecognizer {
    private static final String TAG = "FoodRecognizer";
    private static final String MODEL_PATH = "model.tflite";
    private static final int IMAGE_SIZE = 128;
    private static final int BATCH_SIZE = 1;
    private static final int PIXEL_SIZE = 3;
    private static final int NUM_CLASSES = 30; // Update based on your model

    private Interpreter tflite;
    private final Context context;
    private ByteBuffer inputBuffer;

    public FoodRecognizer(Context context) {
        this.context = context;
        try {
            tflite = new Interpreter(loadModelFile());
            inputBuffer = ByteBuffer.allocateDirect(
                    BATCH_SIZE * IMAGE_SIZE * IMAGE_SIZE * PIXEL_SIZE * 4);
            inputBuffer.order(ByteOrder.nativeOrder());
        } catch (IOException e) {
            Log.e(TAG, "Error loading model", e);
        }
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        try (FileInputStream inputStream = context.getAssets().openFd(MODEL_PATH).createInputStream();
             FileChannel fileChannel = inputStream.getChannel()) {
            long startOffset = context.getAssets().openFd(MODEL_PATH).getStartOffset();
            long declaredLength = context.getAssets().openFd(MODEL_PATH).getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    public String recognizeFood(Bitmap bitmap) {
        try {
            // Resize bitmap to model input size
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true);

            // Prepare input: convert bitmap to ByteBuffer
            inputBuffer.rewind();
            int[] intValues = new int[IMAGE_SIZE * IMAGE_SIZE];
            resizedBitmap.getPixels(intValues, 0, resizedBitmap.getWidth(), 0, 0,
                    resizedBitmap.getWidth(), resizedBitmap.getHeight());

            // Convert the image to floating point
            for (int pixelValue : intValues) {
                inputBuffer.putFloat(((pixelValue >> 16) & 0xFF) / 255.0f);
                inputBuffer.putFloat(((pixelValue >> 8) & 0xFF) / 255.0f);
                inputBuffer.putFloat((pixelValue & 0xFF) / 255.0f);
            }

            // Run inference
            float[][] outputBuffer = new float[1][NUM_CLASSES];
            tflite.run(inputBuffer, outputBuffer);

            // Process results to JSON format
            String jsonResult = processResults(outputBuffer[0]);

            // Parse JSON to get top class name
            FoodRecognitionResult result = new Gson().fromJson(jsonResult, FoodRecognitionResult.class);
            return result.getTopFoodName();

        } catch (Exception e) {
            Log.e(TAG, "Error processing image", e);
            return "Error: " + e.getMessage();
        }
    }

    private String processResults(float[] probabilities) {
        // Sort indices by probability (descending order)
        Integer[] indices = new Integer[probabilities.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }

        // Sort indices by probability (descending)
        java.util.Arrays.sort(indices, (a, b) -> Float.compare(probabilities[b], probabilities[a]));

        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{")
                .append("\"predictions\": [");

        // Get top 3 predictions
        String[] classNames = getClassNamesFromAssets();
        for (int i = 0; i < Math.min(3, indices.length); i++) {
            int idx = indices[i];
            float confidence = probabilities[idx];
            String confidenceLevel = getConfidenceLevel(confidence);

            jsonBuilder.append("{")
                    .append("\"rank\": ").append(i + 1).append(",")
                    .append("\"class_name\": \"").append(classNames[idx]).append("\",")
                    .append("\"class_index\": ").append(idx).append(",")
                    .append("\"confidence\": ").append(String.format("%.4f", confidence)).append(",")
                    .append("\"percentage\": ").append(String.format("%.2f", confidence * 100)).append(",")
                    .append("\"confidence_level\": \"").append(confidenceLevel).append("\"")
                    .append("}");

            if (i < Math.min(3, indices.length) - 1) {
                jsonBuilder.append(",");
            }
        }

        jsonBuilder.append("],")
                .append("\"inference_time_ms\": 146.7,")
                .append("\"input_shape\": [1, 128, 128, 3],")
                .append("\"timestamp\": \"").append(LocalDateTime.now()).append("\"")
                .append("}");

        return jsonBuilder.toString();
    }
    private String[] getClassNamesFromAssets() {
        try {
            // Read class mapping from assets
            java.io.InputStream is = context.getAssets().open("class_mapping.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            // Parse JSON
            com.google.gson.JsonObject jsonObject = new Gson().fromJson(json, com.google.gson.JsonObject.class);
            com.google.gson.JsonObject indexToClass = jsonObject.getAsJsonObject("index_to_class");

            String[] classNames = new String[NUM_CLASSES];
            for (int i = 0; i < NUM_CLASSES; i++) {
                classNames[i] = indexToClass.get(String.valueOf(i)).getAsString();
            }

            return classNames;
        } catch (IOException e) {
            Log.e(TAG, "Error reading class mapping", e);
            // Fallback to hardcoded class names
            return getClassNames();
        }
    }

    private String getConfidenceLevel(float confidence) {
        if (confidence >= 0.8) return "Very High";
        if (confidence >= 0.6) return "High";
        if (confidence >= 0.4) return "Medium";
        if (confidence >= 0.2) return "Low";
        return "Very Low";
    }

    private String[] getClassNames() {
        // Return your model's class names here
        return new String[] {
                "Banh_canh", "Banh_chung", "Banh_cuon", "Banh_duc", "Banh_gio", "Banh_khot",
                "Banh_mi", "Banh_tet", "Banh_trang_nuong", "Banh_xeo", "Bo_bit_tet", "Bo_kho",
                "Bo_luc_lac", "Bun", "Bun_bo_Hue", "Bun_cha", "Bun_dau_mam_tom", "Bun_thit_nuong",
                "Ca_kho_to", "Canh_chua", "Cao_lau", "Chao_long", "Com_tam", "Hu_tieu",
                "Mi_quang", "Pho", "Pizza", "Spring_rolls", "Xoi", "Empty"
        };
    }
}