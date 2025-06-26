package com.example.foodapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FoodActivity extends AppCompatActivity implements FoodAdapter.OnFoodSelectionChangedListener {

    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private List<Food> foodList;
    private List<Food> selectedFoods;
    private String stressLevel;
    private Button btnExportPDF;
    private TextView tvSelectedFoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food2);

        recyclerView = findViewById(R.id.recyclerView);
        btnExportPDF = findViewById(R.id.btnExportPDF);
        tvSelectedFoods = findViewById(R.id.tvSelectedFoods);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        foodList = new ArrayList<>();
        selectedFoods = new ArrayList<>();

        // Load selected foods from SharedPreferences
        loadSelectedFoods();

        // Initialize adapter and set it to RecyclerView
        adapter = new FoodAdapter(foodList, selectedFoods, this); // Pass listener (this) to adapter
        recyclerView.setAdapter(adapter);

        // Display the initial selected foods message
        displaySelectedFoodsMessage();

        stressLevel = getIntent().getStringExtra("stressLevel");
        if (stressLevel == null || stressLevel.isEmpty()) {
            Toast.makeText(this, "Invalid stress level provided.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadFoods();

        btnExportPDF.setOnClickListener(v -> {
            File pdfFile = exportSelectedFoodsToPDF();
            if (pdfFile != null) {
                openPDF(pdfFile);
            }
        });
    }

    private void loadFoods() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("foods/" + stressLevel);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                foodList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    try {
                        Food food = data.getValue(Food.class);
                        if (food != null) {
                            foodList.add(food);
                        }
                    } catch (Exception e) {
                        Toast.makeText(FoodActivity.this, "Invalid data format detected.", Toast.LENGTH_SHORT).show();
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(FoodActivity.this, "Failed to load foods: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFoodSelectionChanged(List<Food> updatedSelectedFoods) {
        selectedFoods = updatedSelectedFoods;

        // Save the updated list to SharedPreferences
        saveSelectedFoods();

        // Update the UI message and notify adapter
        displaySelectedFoodsMessage();
        adapter.notifyDataSetChanged();
    }

    private void saveSelectedFoods() {
        SharedPreferences sharedPreferences = getSharedPreferences("FoodApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(selectedFoods);
        editor.putString("selectedFoods", json);
        editor.apply();
    }

    private void displaySelectedFoodsMessage() {
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
        tvSelectedFoods.setText(selectedFoodsMessage.toString());
    }

    // Method to export selected foods to PDF
    private File exportSelectedFoodsToPDF() {
        if (selectedFoods.isEmpty()) {
            Toast.makeText(this, "No foods selected to export.", Toast.LENGTH_SHORT).show();
            return null;
        }

        File pdfFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "SelectedFoods.pdf");

        try {
            // Set up the PdfWriter
            PdfWriter writer = new PdfWriter(pdfFile);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            document.add(new Paragraph("Selected Foods:"));

            // Add food details to the PDF
            for (Food food : selectedFoods) {
                document.add(new Paragraph("Name: " + food.getName()));
                document.add(new Paragraph("Calories: " + food.getCalories()));
                document.add(new Paragraph("Contents: " + food.getContents()));
                document.add(new Paragraph("\n"));
            }

            document.close();
            Toast.makeText(this, "PDF Exported!", Toast.LENGTH_SHORT).show();
            return pdfFile;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to export PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    // Method to open the PDF file
    private void openPDF(File pdfFile) {
        Uri pdfUri = FileProvider.getUriForFile(this, "com.example.foodapp.fileprovider", pdfFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    // Load the selected foods from SharedPreferences
    private void loadSelectedFoods() {
        SharedPreferences sharedPreferences = getSharedPreferences("FoodApp", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("selectedFoods", null);
        Type type = new TypeToken<List<Food>>() {}.getType();
        selectedFoods = gson.fromJson(json, type);

        if (selectedFoods == null) {
            selectedFoods = new ArrayList<>();
        }
    }
}
