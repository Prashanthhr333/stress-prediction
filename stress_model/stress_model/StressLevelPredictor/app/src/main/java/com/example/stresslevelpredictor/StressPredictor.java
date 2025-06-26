package com.example.stresslevelpredictor;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class StressPredictor {
    private Interpreter tflite;
    private static final String MODEL_PATH = "stress_model.tflite";

    public StressPredictor(Context context) throws IOException {
        tflite = new Interpreter(loadModelFile(context.getAssets()));
    }

    private MappedByteBuffer loadModelFile(AssetManager assets) throws IOException {
        AssetFileDescriptor fileDescriptor = assets.openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public String predictStressLevel(float humidity, float temperature, float stepCount) {
        // Prepare input
        float[][] input = new float[1][3];
        input[0][0] = humidity;
        input[0][1] = temperature;
        input[0][2] = stepCount;

        // Prepare output
        float[][] output = new float[1][3];

        // Run inference
        tflite.run(input, output);

        // Find the index of the highest probability
        int maxIndex = 0;
        for (int i = 1; i < output[0].length; i++) {
            if (output[0][i] > output[0][maxIndex]) {
                maxIndex = i;
            }
        }

        // Map index to stress level
        switch (maxIndex) {
            case 0: return "Low Stress";
            case 1: return "Medium Stress";
            case 2: return "High Stress";
            default: return "Unknown";
        }
    }
}