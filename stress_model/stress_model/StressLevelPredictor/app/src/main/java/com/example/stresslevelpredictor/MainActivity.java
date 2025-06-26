package com.example.stresslevelpredictor;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    private Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        EditText inputHumidity = findViewById(R.id.inputHumidity);
        EditText inputTemperature = findViewById(R.id.inputTemperature);
        EditText inputStepCount = findViewById(R.id.inputStepCount);
        Button predictButton = findViewById(R.id.predictButton);
        TextView resultText = findViewById(R.id.resultText);

        // Load the TensorFlow Lite model
        try {
            tflite = new Interpreter(loadModelFile("stress_model2.tflite"));
        } catch (IOException e) {
            resultText.setText("Error loading model: " + e.getMessage());
            return;
        }

        // Set button click listener
        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    float humidity = Float.parseFloat(inputHumidity.getText().toString());
                    float temperature = Float.parseFloat(inputTemperature.getText().toString());
                    float stepCount = Float.parseFloat(inputStepCount.getText().toString());

                    String prediction = predictStressLevel(humidity, temperature, stepCount);
                    resultText.setText("Predicted Stress Level: " + prediction);
                } catch (NumberFormatException e) {
                    resultText.setText("Please enter valid numbers.");
                }
            }
        });
    }

    private ByteBuffer loadModelFile(String modelPath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(getAssets().openFd(modelPath).getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = getAssets().openFd(modelPath).getStartOffset();
        long declaredLength = getAssets().openFd(modelPath).getLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength).order(ByteOrder.nativeOrder());
    }

    private String predictStressLevel(float humidity, float temperature, float stepCount) {
        // Preprocess input
        float[] input = new float[]{
                (humidity - 20.0f) / 5.78f,
                (temperature - 89.0f) / 5.78f,
                (stepCount - 100.14f) / 58.18f
        };

        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(4 * input.length).order(ByteOrder.nativeOrder());
        for (float value : input) {
            inputBuffer.putFloat(value);
        }

        // Prepare output
        float[][] output = new float[1][3];

        // Run inference
        tflite.run(inputBuffer, output);

        // Map output to stress level
        String[] stressMapping = {"Low", "Medium", "High"};
        int predictedIndex = argMax(output[0]);
        return stressMapping[predictedIndex];
    }

    private int argMax(float[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
