package com.example.foodapp;

import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SelectedFoodAdapter.OnFoodSelectionChangedListener {

    private GridView gridViewSelectedFoods;
    private SelectedFoodAdapter selectedFoodAdapter;
    private List<Food> selectedFoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridViewSelectedFoods = findViewById(R.id.gridViewSelectedFoods);
        selectedFoods = new ArrayList<>();

        // Initialize adapter for GridView with listener
        selectedFoodAdapter = new SelectedFoodAdapter(this, selectedFoods, this);
        gridViewSelectedFoods.setAdapter(selectedFoodAdapter);

        // Load selected foods from SharedPreferences
        loadSelectedFoods();

        // Button click listeners to navigate to FoodActivity with different stress levels
        findViewById(R.id.btnLow).setOnClickListener(v -> openFoodActivity("low"));
        findViewById(R.id.btnMedium).setOnClickListener(v -> openFoodActivity("medium"));
        findViewById(R.id.btnHigh).setOnClickListener(v -> openFoodActivity("high"));
    }

    private void openFoodActivity(String level) {
        Intent intent = new Intent(this, FoodActivity.class);
        intent.putExtra("stressLevel", level);
        startActivity(intent);
    }

    @Override
    public void onFoodSelectionChanged(List<Food> updatedSelectedFoods) {
        // Update the selected foods list
        selectedFoods = updatedSelectedFoods;

        // Save the updated list to SharedPreferences
        saveSelectedFoods();

        // Update the UI message
        displaySelectedFoodsMessage();

        // Notify the adapter to update the GridView
        selectedFoodAdapter.updateData(selectedFoods);
    }

    // Method to save selected foods to SharedPreferences
    private void saveSelectedFoods() {
        SharedPreferences sharedPreferences = getSharedPreferences("FoodApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(selectedFoods);
        editor.putString("selectedFoods", json);
        editor.apply();
    }

    // Method to load selected foods from SharedPreferences
    private void loadSelectedFoods() {
        SharedPreferences sharedPreferences = getSharedPreferences("FoodApp", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("selectedFoods", null);
        if (json != null) {
            selectedFoods = gson.fromJson(json, new TypeToken<List<Food>>() {}.getType());
            selectedFoodAdapter.updateData(selectedFoods);  // Update GridView adapter
        }
    }

    // Display selected foods message in the UI
    private void displaySelectedFoodsMessage() {
        // Update the UI with selected foods message
        StringBuilder selectedFoodsMessage = new StringBuilder();
        if (selectedFoods.isEmpty()) {
            selectedFoodsMessage.append("No foods selected yet. Start adding foods to your selection!");
        } else {
            selectedFoodsMessage.append("You've chosen the following foods: ");
            for (Food food : selectedFoods) {
                selectedFoodsMessage.append("\n• ").append(food.getName());
            }
            selectedFoodsMessage.append("\nEnjoy your choices!");
        }
        // Set this message to a TextView (you would have to create this TextView in your layout)
    }
}
