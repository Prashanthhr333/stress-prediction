package com.example.foodapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class SelectedFoodAdapter extends BaseAdapter {

    private final Context context;
    private List<Food> selectedFoods;
    private final OnFoodSelectionChangedListener listener;

    // Define an interface to notify the activity
    public interface OnFoodSelectionChangedListener {
        void onFoodSelectionChanged(List<Food> updatedSelectedFoods);
    }

    public SelectedFoodAdapter(Context context, List<Food> selectedFoods, OnFoodSelectionChangedListener listener) {
        this.context = context;
        this.selectedFoods = selectedFoods;
        this.listener = listener;
    }

    public void updateData(List<Food> selectedFoods) {
        this.selectedFoods = selectedFoods;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return selectedFoods.size();
    }

    @Override
    public Object getItem(int position) {
        return selectedFoods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_selected_food, parent, false);
        } else {
            view = convertView;
        }

        Food food = selectedFoods.get(position);

        ImageView ivImage = view.findViewById(R.id.ivImage);
        TextView tvName = view.findViewById(R.id.tvName);
        ImageView ivRemove = view.findViewById(R.id.ivRemove); // The remove icon

        tvName.setText(food.getName());

        if (food.getImageUrl() != null && !food.getImageUrl().isEmpty()) {
            Picasso.get().load(food.getImageUrl()).into(ivImage);
        } else {
            ivImage.setImageResource(R.drawable.error_image);
        }

        ivRemove.setOnClickListener(v -> {
            // Remove the food item from the list
            selectedFoods.remove(position);
            // Notify the activity about the updated list
            listener.onFoodSelectionChanged(selectedFoods);
        });

        return view;
    }
}
