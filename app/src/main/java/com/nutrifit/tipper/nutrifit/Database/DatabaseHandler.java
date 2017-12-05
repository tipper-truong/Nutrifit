package com.nutrifit.tipper.nutrifit.Database;

/**
 * Created by tipper on 11/24/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nutrifit.tipper.nutrifit.Model.Recipe;
import com.nutrifit.tipper.nutrifit.Model.User;

import java.util.ArrayList;

//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;

/**
 * Created by tipper on 9/8/17.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "NutrifitDB";

    // User Table name
    private static final String TABLE_USER = "User";

    // User Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_FITNESS_GOALS = "fitness_goals";
    private static final String KEY_CALORIES_BURNED_PER_DAY = "caloriesToBurnPerDay";

    // Recipe Table name
    private static final String TABLE_RECIPE = "Recipe";

    // Recipe Table Column names
    private static final String KEY_RECIPE_USER_ID = "user_id";
    private static final String KEY_RECIPE_ID = "recipe_id";
    private static final String KEY_RECIPE_NAME = "recipe_name";
    private static final String KEY_RECIPE_IMAGE_URL = "image_url";
    private static final String KEY_RECIPE_SOURCE_DISPLAY_NAME = "source_display_name";
    private static final String KEY_INGREDIENTS = "ingredients";
    private static final String KEY_TOTAL_TIME_IN_SECS = "total_time";
    private static final String KEY_RATING = "rating";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_FIRST_NAME + " TEXT,"
                + KEY_LAST_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_GENDER + " TEXT,"
                + KEY_FITNESS_GOALS + " TEXT,"
                + KEY_CALORIES_BURNED_PER_DAY + " FLOAT, "
                + "UNIQUE (" + KEY_EMAIL + ") ON CONFLICT ROLLBACK)"; //helps avoid duplicates

        String CREATE_RECIPE_TABLE = "CREATE TABLE " + TABLE_RECIPE + "("
                + KEY_RECIPE_ID + " TEXT PRIMARY KEY,"
                + KEY_RECIPE_NAME + " TEXT,"
                + KEY_RECIPE_IMAGE_URL + " TEXT,"
                + KEY_RECIPE_SOURCE_DISPLAY_NAME + " TEXT,"
                + KEY_INGREDIENTS + " TEXT,"
                + KEY_TOTAL_TIME_IN_SECS + " INTEGER,"
                + KEY_RATING + " INTEGER,"
                + KEY_RECIPE_USER_ID + " INTEGER,"
                + "UNIQUE (" + KEY_RECIPE_ID + ") ON CONFLICT ROLLBACK)"; //helps avoid duplicates

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_RECIPE_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v("Upgrading Table", "True");
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /********** USER QUERIES **********/

    public boolean addUser(User user, Context context)
    {
        boolean userExist = false;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FIRST_NAME, user.getFirstName());
        values.put(KEY_LAST_NAME, user.getLastName());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_GENDER, user.getGender());
        values.put(KEY_FITNESS_GOALS, user.getFitnessGoals());
        values.put(KEY_CALORIES_BURNED_PER_DAY, user.getCaloriesToBurnPerDay());

        try {
            db.insertOrThrow(TABLE_USER, null, values);
        } catch (SQLiteConstraintException e) {
            userExist = true;
            Toast.makeText(context, "User already exist, please try again", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }

        db.close();
        return userExist;
    }

    public void updateUser(User user)
    {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(KEY_FITNESS_GOALS, user.getFitnessGoals());
        values.put(KEY_CALORIES_BURNED_PER_DAY, user.getCaloriesToBurnPerDay());

        // updating row
        db.update(TABLE_USER, values, KEY_EMAIL + " = ?",
                new String[] { user.getEmail() });
    }

    public User getUser(String email)
    {
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.query(TABLE_USER, new String[] { KEY_ID, KEY_FIRST_NAME, KEY_LAST_NAME, KEY_EMAIL, KEY_PASSWORD, KEY_GENDER, KEY_FITNESS_GOALS, KEY_CALORIES_BURNED_PER_DAY}, KEY_EMAIL + "=?",
                new String[] {email}, null, null, null, null);

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();

            User retUser = new User(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getInt(7));
            retUser.setId(cursor.getInt(0));

            return retUser;

        } else {
            return null;
        }

    }

    /********** RECIPE QUERIES **********/
    public boolean addRecipe(Recipe recipe)
    {
        // String recipeID, String recipeName, String imageUrl, String sourceDisplayName, ArrayList<String> ingredients, int totalTimeInSeconds, int rating
        boolean recipeAdded = false;

        SQLiteDatabase db = this.getWritableDatabase();

        Gson gson = new Gson();
        String arrayListStr = gson.toJson(recipe.getIngredients());

        ContentValues values = new ContentValues();
        values.put(KEY_RECIPE_ID, recipe.getRecipeID());
        values.put(KEY_RECIPE_NAME, recipe.getRecipeName());
        values.put(KEY_RECIPE_IMAGE_URL, recipe.getImageUrl());
        values.put(KEY_RECIPE_SOURCE_DISPLAY_NAME, recipe.getSourceDisplayName());
        values.put(KEY_INGREDIENTS, arrayListStr);
        values.put(KEY_TOTAL_TIME_IN_SECS, recipe.getTotalTimeInSeconds());
        values.put(KEY_RATING, recipe.getRating());
        values.put(KEY_RECIPE_USER_ID, recipe.getUserID());

        try {
            db.insertOrThrow(TABLE_RECIPE, null, values);
        } catch (SQLiteConstraintException e) {
            recipeAdded = true;
            Log.v("Error", "Recipe already added");
            e.printStackTrace();
        }

        db.close();
        return recipeAdded;
    }

    public Recipe getRecipe(String recipeID)
    {
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.query(TABLE_RECIPE, new String[] { KEY_RECIPE_ID, KEY_RECIPE_NAME, KEY_RECIPE_IMAGE_URL, KEY_RECIPE_SOURCE_DISPLAY_NAME, KEY_INGREDIENTS, KEY_TOTAL_TIME_IN_SECS, KEY_RATING, KEY_RECIPE_USER_ID}, KEY_EMAIL + "=?",
                new String[] {recipeID}, null, null, null, null);

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();

            Gson gson = new Gson();
            ArrayList<String> ingredientList = gson.fromJson(cursor.getString(4), new TypeToken<ArrayList<String>>(){}.getType());

            Recipe recipe = new Recipe(cursor.getInt(7), cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), ingredientList, cursor.getInt(5), cursor.getInt(6));

            return recipe;

        } else {
            return null;
        }
    }

    public ArrayList<Recipe> getAllRecipes(int userID)
    {

        //SELECT ALL Query
        String selectQuery = "SELECT * FROM " + TABLE_RECIPE + " WHERE user_id=" + String.valueOf(userID);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<Recipe> recipeList = new ArrayList<Recipe>();

        //Looping through all the rows and adding to the list
        if (cursor.moveToFirst()) {
            do {
                Gson gson = new Gson();
                ArrayList<String> ingredientList = gson.fromJson(cursor.getString(4), new TypeToken<ArrayList<String>>(){}.getType());
                Recipe recipe = new Recipe(cursor.getInt(7), cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), ingredientList, cursor.getInt(5), cursor.getInt(6));
                recipeList.add(recipe);
            } while (cursor.moveToNext());
        }

        return recipeList;
    }

    public int updateRecipe(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();

        Gson gson = new Gson();
        String arrayListStr = gson.toJson(recipe.getIngredients());

        ContentValues values = new ContentValues();
        values.put(KEY_RECIPE_NAME, recipe.getRecipeName());
        values.put(KEY_RECIPE_IMAGE_URL, recipe.getImageUrl());
        values.put(KEY_RECIPE_SOURCE_DISPLAY_NAME, recipe.getSourceDisplayName());
        values.put(KEY_INGREDIENTS, arrayListStr);
        values.put(KEY_TOTAL_TIME_IN_SECS, recipe.getTotalTimeInSeconds());
        values.put(KEY_RATING, recipe.getRating());

        // updating row
        return db.update(TABLE_RECIPE, values, KEY_RECIPE_ID + " = ?",
                new String[] { String.valueOf(recipe.getRecipeID()) });
    }

    public void deleteRecipe(Recipe recipe) {
        String deleteQuery = "DELETE FROM " + TABLE_RECIPE + " WHERE " + KEY_RECIPE_ID + "= '" + recipe.getRecipeID() + "'" + " AND " + KEY_RECIPE_USER_ID + "= '" + recipe.getUserID() + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteQuery);
        db.close();
    }


}
