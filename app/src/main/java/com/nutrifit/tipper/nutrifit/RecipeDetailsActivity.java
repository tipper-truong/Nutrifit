package com.nutrifit.tipper.nutrifit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nutrifit.tipper.nutrifit.Database.DatabaseHandler;
import com.nutrifit.tipper.nutrifit.Model.Recipe;
import com.nutrifit.tipper.nutrifit.Model.User;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class RecipeDetailsActivity extends AppCompatActivity {

    private ImageView recipeImage;
    private EditText recipeName;
    private RatingBar rating;
    private EditText recipeTime;
    private EditText recipeIngredients;
    private EditText sourceName;
    private TextView calories;
    public static final String RECIPE = "RECIPE";
    public static final String USER = "USER";
    private User user;
    private Recipe recipe;
    private DatabaseHandler db;
    private Button updateButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        getSupportActionBar().hide();

        db = new DatabaseHandler(this);

        recipeImage = (ImageView) findViewById(R.id.recipeImage);
        recipeName = (EditText) findViewById(R.id.recipeName);
        rating = (RatingBar) findViewById(R.id.recipeRating);
        recipeTime = (EditText) findViewById(R.id.recipeTime);
        recipeIngredients = (EditText) findViewById(R.id.recipeIngredients);
        sourceName = (EditText) findViewById(R.id.recipeSourceName);
        calories = (TextView) findViewById(R.id.recipeCalories);
        updateButton = (Button) findViewById(R.id.updateButton);

        user = getUserData();
        recipe = getRecipeData();

        initializeRecipeDetails();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] ingredientsArr = recipeIngredients.getText().toString().split(",");
                ArrayList<String> ingredientsList = new ArrayList<String>();
                for(int i = 0; i < ingredientsArr.length; i++) {
                    ingredientsList.add(ingredientsArr[i]);
                }
                try {
                    recipe.setUserID(user.getId());
                    recipe.setRecipeID(recipe.getRecipeID());
                    recipe.setRecipeName(recipeName.getText().toString());
                    recipe.setImageUrl(recipe.getImageUrl());
                    recipe.setSourceDisplayName(sourceName.getText().toString());
                    recipe.setIngredients(ingredientsList);
                    recipe.setTotalTimeInSeconds(convertHHMMToSeconds(recipeTime.getText().toString()));
                    recipe.setRating((int) rating.getRating());
                    int dbUpdate = db.updateRecipe(recipe);
                    if(dbUpdate == 1) {
                        saveRecipeData(RecipeDetailsActivity.this, recipe);
                        Toast toast = Toast.makeText(RecipeDetailsActivity.this, "Updated Recipe successfully", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        Intent i = new Intent(RecipeDetailsActivity.this, RecipeDetailsActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast toast = Toast.makeText(RecipeDetailsActivity.this, "Updated Recipe unsuccessfully, please try again", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    db.close();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        calories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(RecipeDetailsActivity.this, "User is not allowed to edit calories", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

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

    private void saveRecipeData(Context context, Recipe recipe)
    {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(RECIPE, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String recipeObj = gson.toJson(recipe);

        editor.putString(RECIPE, recipeObj);
        editor.commit();
    }


    private String convertSecondsToHHMM(int seconds)
    {
        Date d = new Date(seconds * 1000L);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String time = df.format(d);
        return time;
    }

    private int convertHHMMToSeconds(String time) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = dateFormat.parse(time);
        long seconds = date.getTime() / 1000L;
        return (int) seconds;
    }

    private void initializeRecipeDetails()
    {
        Picasso.with(RecipeDetailsActivity.this).load(recipe.getImageUrl()).into(recipeImage);

        recipeName.setText(recipe.getRecipeName());

        rating.setRating(recipe.getRating());

        recipeTime.setText(convertSecondsToHHMM(recipe.getTotalTimeInSeconds()));

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
            calories.setText("250");
        } else if(recipe.getIngredients().size() > 6 && recipe.getIngredients().size() <= 9) {
            calories.setText("350");
        } else if(recipe.getIngredients().size() > 9 && recipe.getIngredients().size() <= 12) {
            calories.setText("450");
        } else {
            calories.setText("550");
        }
    }

}
