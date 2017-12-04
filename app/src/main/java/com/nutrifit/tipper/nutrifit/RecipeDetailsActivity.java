package com.nutrifit.tipper.nutrifit;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.gson.Gson;
import com.nutrifit.tipper.nutrifit.Model.Recipe;
import com.nutrifit.tipper.nutrifit.Model.User;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class RecipeDetailsActivity extends AppCompatActivity {

    private ImageView recipeImage;
    private EditText recipeName;
    private RatingBar rating;
    private EditText recipeTime;
    private EditText recipeIngredients;
    private EditText sourceName;
    private EditText calories;
    public static final String RECIPE = "RECIPE";
    public static final String USER = "USER";
    private User user;
    private Recipe recipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        getSupportActionBar().hide();

        recipeImage = (ImageView) findViewById(R.id.recipeImage);
        recipeName = (EditText) findViewById(R.id.recipeName);
        rating = (RatingBar) findViewById(R.id.recipeRating);
        recipeTime = (EditText) findViewById(R.id.recipeTime);
        recipeIngredients = (EditText) findViewById(R.id.recipeIngredients);
        sourceName = (EditText) findViewById(R.id.recipeSourceName);
        calories = (EditText) findViewById(R.id.recipeCalories);

        user = getUserData();
        recipe = getRecipeData();

        initializeRecipeDetails();
    }

    private User getUserData()
    {
        SharedPreferences settings;
        settings = RecipeDetailsActivity.this.getSharedPreferences(USER, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String userObj = settings.getString(USER, null);
        User retUser = gson.fromJson(userObj, User.class);
        return retUser;
    }

    private Recipe getRecipeData()
    {
        SharedPreferences settings;
        settings = RecipeDetailsActivity.this.getSharedPreferences(RECIPE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String recipeObj = settings.getString(RECIPE, null);
        Recipe retRecipe = gson.fromJson(recipeObj, Recipe.class);
        return retRecipe;
    }

    private String convertSecondsToHHMMSS(int seconds)
    {
        Date d = new Date(seconds * 1000L);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String time = df.format(d);
        return time;
    }

    private void initializeRecipeDetails()
    {
        Picasso.with(RecipeDetailsActivity.this).load(recipe.getImageUrl()).into(recipeImage);

        recipeName.setText(recipe.getRecipeName());

        rating.setRating(recipe.getRating());

        recipeTime.setText(convertSecondsToHHMMSS(recipe.getTotalTimeInSeconds()));

        for(int i = 0; i < recipe.getIngredients().size(); i++) {
            if(i == recipe.getIngredients().size()-1) {
                recipeIngredients.append(recipe.getIngredients().get(i));
            } else {
                recipeIngredients.append(recipe.getIngredients().get(i) + ", ");
            }
        }

        sourceName.setText(recipe.getSourceDisplayName());

        if(recipe.getIngredients().size() > 0 && recipe.getIngredients().size() <= 3) {
            calories.setText("150");
        } else if (recipe.getIngredients().size() > 3 && recipe.getIngredients().size() <= 6) {
            calories.setText("350");
        } else if(recipe.getIngredients().size() > 6 && recipe.getIngredients().size() <= 9) {
            calories.setText("450");
        } else if(recipe.getIngredients().size() > 9 && recipe.getIngredients().size() <= 12) {
            calories.setText("550");
        } else {
            calories.setText("650");
        }
    }

}
