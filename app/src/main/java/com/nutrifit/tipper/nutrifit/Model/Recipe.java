package com.nutrifit.tipper.nutrifit.Model;

import java.util.ArrayList;

/**
 * Created by tipper on 11/24/17.
 */

public class Recipe {

    // new Recipe(recipeID, recipeName, imageUrls, smallImageUrl, sourceDisplayName, ingredients, totalTimeInSeconds, rating);
    private String recipeID;
    private String recipeName;
    private String imageUrl;
    private String smallImageUrl;
    private String sourceDisplayName;
    private ArrayList<String> ingredients;
    private int totalTimeInSeconds;
    private int rating;

    public Recipe(String recipeID, String recipeName, String imageUrl, String smallImageUrl, String sourceDisplayName, ArrayList<String> ingredients, int totalTimeInSeconds, int rating) {
        this.recipeID = recipeID;
        this.recipeName = recipeName;
        this.imageUrl = imageUrl;
        this.smallImageUrl = smallImageUrl;
        this.sourceDisplayName = sourceDisplayName;
        this.ingredients = ingredients;
        this.totalTimeInSeconds = totalTimeInSeconds;
        this.rating = rating;
    }

    public String getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        this.smallImageUrl = smallImageUrl;
    }

    public String getSourceDisplayName() {
        return sourceDisplayName;
    }

    public void setSourceDisplayName(String sourceDisplayName) {
        this.sourceDisplayName = sourceDisplayName;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public int getTotalTimeInSeconds() {
        return totalTimeInSeconds;
    }

    public void setTotalTimeInSeconds(int totalTimeInSeconds) {
        this.totalTimeInSeconds = totalTimeInSeconds;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
