package com.example.foodapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private final List<Food> foodList;
    private final List<Food> selectedFoods;
    private final Context context;
    private final OnFoodSelectionChangedListener listener;

    public FoodAdapter(List<Food> foodList, List<Food> selectedFoods, OnFoodSelectionChangedListener listener) {
        this.foodList = foodList;
        this.selectedFoods = selectedFoods;
        this.context = ((Context) listener); // Cast to context since listener is likely an activity
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);

        holder.tvName.setText(food.getName());
        holder.tvCalories.setText("Calories: " + food.getCalories());
        holder.tvContents.setText("Contents: " + food.getContents());

        // Load the food image
        Glide.with(context)
                .load(food.getImageUrl())
                .placeholder(R.drawable.placeholder) // Default placeholder image
                .error(R.drawable.error_image) // Image shown if loading fails
                .into(holder.ivImage);

        // Check if the food is already selected
        if (selectedFoods.contains(food)) {
            holder.btnSelect.setText("Selected");
            holder.btnSelect.setEnabled(false);
        } else {
            holder.btnSelect.setText("Select");
            holder.btnSelect.setEnabled(true);
        }

        holder.btnSelect.setOnClickListener(v -> {
            if (!selectedFoods.contains(food)) {
                selectedFoods.add(food);
                listener.onFoodSelectionChanged(selectedFoods); // Update the selected foods in the listener (activity)
                notifyItemChanged(position); // Update button state
                Toast.makeText(context, food.getName() + " added to selection.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (selectedFoods.contains(food)) {
                selectedFoods.remove(food);
                listener.onFoodSelectionChanged(selectedFoods); // Update the selected foods in the listener (activity)
                notifyItemChanged(position); // Update button state
                Toast.makeText(context, food.getName() + " removed from selection.", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(context, "This food is not in the selected list.", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCalories, tvContents;
        ImageView ivImage;
        Button btnSelect;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvContents = itemView.findViewById(R.id.tvContents);
            ivImage = itemView.findViewById(R.id.ivImage);
            btnSelect = itemView.findViewById(R.id.btnSelectFood);
        }
    }

    // Functional interface for food selection change
    public interface OnFoodSelectionChangedListener {
        void onFoodSelectionChanged(List<Food> updatedSelectedFoods);
    }
}

