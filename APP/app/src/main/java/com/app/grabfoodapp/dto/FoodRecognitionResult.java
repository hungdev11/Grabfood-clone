package com.app.grabfoodapp.dto;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodRecognitionResult {
    private List<FoodPrediction> predictions;
    @SerializedName("inference_time_ms")
    private double inferenceTimeMs;
    @SerializedName("input_shape")
    private int[] inputShape;
    private String timestamp;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoodPrediction {
        private int rank;
        @SerializedName("class_name")
        private String className;
        @SerializedName("class_index")
        private int classIndex;
        private double confidence;
        private double percentage;
        @SerializedName("confidence_level")
        private String confidenceLevel;
    }

    // Get top prediction (first in the list)
    public String getTopFoodName() {
        if (predictions != null && !predictions.isEmpty()) {
            return predictions.get(0).getClassName();
        }
        return "Unknown";
    }
}
