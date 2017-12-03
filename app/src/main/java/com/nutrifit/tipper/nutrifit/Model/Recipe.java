package com.nutrifit.tipper.nutrifit.Model;

import java.util.ArrayList;

/**
 * Created by tipper on 11/24/17.
 */

public class Recipe {

    // new Recipe(recipeID, recipeName, imageUrls, smallImageUrl, sourceDisplayName, ingredients, totalTimeInSeconds, rating);
    private int userID;
    private String recipeID;
    private String recipeName;
    private String imageUrl;
    private String sourceDisplayName;
    private ArrayList<String> ingredients;


    private int totalTimeInSeconds;
    private int rating;

    public Recipe(int userID, String recipeID, String recipeName, String imageUrl, String sourceDisplayName, ArrayList<String> ingredients, int totalTimeInSeconds, int rating) {
        this.userID = userID;
        this.recipeID = recipeID;
        this.recipeName = recipeName;
        this.imageUrl = imageUrl;
        this.sourceDisplayName = sourceDisplayName;
        this.ingredients = ingredients;
        this.totalTimeInSeconds = totalTimeInSeconds;
        this.rating = rating;
    }

    public Recipe(String recipeID, String recipeName, String imageUrl, String sourceDisplayName, ArrayList<String> ingredients, int totalTimeInSeconds, int rating) {
        this.recipeID = recipeID;
        this.recipeName = recipeName;
        this.imageUrl = imageUrl;
        this.sourceDisplayName = sourceDisplayName;
        this.ingredients = ingredients;
        this.totalTimeInSeconds = totalTimeInSeconds;
        this.rating = rating;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
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
