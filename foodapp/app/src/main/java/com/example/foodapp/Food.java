package com.example.foodapp;
public class Food {
    private String name;
    private String imageUrl;
    private String calories; // Change from int to String
    private String contents;

    public Food() { }
    public Food(String name, String imageUrl, String calories, String contents) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.calories = calories;
        this.contents = contents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
