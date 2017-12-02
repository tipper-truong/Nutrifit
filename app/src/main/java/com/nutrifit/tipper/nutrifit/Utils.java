package com.nutrifit.tipper.nutrifit;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nutrifit.tipper.nutrifit.Model.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Utils {

    private static final String TAG = "Utils";
    private ArrayList<Recipe> recipeList;
    private RequestQueue requestQueue;
    private String recipeName;
    private static String RECIPE_URL = "http://api.yummly.com/v1/api/recipes?_app_id=e8611421&_app_key=7102c1ff2ffc74cf805a8bee8b7281a8&q=healthy%20pho";

    public Utils(Context context, String recipeName)
    {
        recipeList = new ArrayList<Recipe>();
        requestQueue = Volley.newRequestQueue(context);
        this.recipeName = recipeName;
    }


    public static Point getDisplaySize(WindowManager windowManager){
        try {
            if(Build.VERSION.SDK_INT > 16) {
                Display display = windowManager.getDefaultDisplay();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                display.getMetrics(displayMetrics);
                return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
            }else{
                return new Point(0, 0);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new Point(0, 0);
        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public void loadRecipeData(final CallBack onCallBack)
    {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, RECIPE_URL, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try {
                            parseRecipeData(response);
                            onCallBack.onSuccess(recipeList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );

        requestQueue.add(getRequest);

    }


    private void parseRecipeData(JSONObject response) throws JSONException {
        JSONArray matchesArray = response.getJSONArray("matches");
        for(int i = 0; i < matchesArray.length(); i++) {
            String recipeID = matchesArray.getJSONObject(i).getString("id");
            String recipeName = matchesArray.getJSONObject(i).getString("recipeName");
            String imageUrls = matchesArray.getJSONObject(i).getJSONObject("imageUrlsBySize").getString("90");
            String smallImageUrl = matchesArray.getJSONObject(i).getString("smallImageUrls");
            String sourceDisplayName = matchesArray.getJSONObject(i).getString("sourceDisplayName");
            ArrayList<String> ingredients = new ArrayList<String>();
            JSONArray ingredientsArray = matchesArray.getJSONObject(i).getJSONArray("ingredients");
            for(int j = 0; j < ingredientsArray.length(); j++) {
                ingredients.add(ingredientsArray.get(j).toString());
            }
            int totalTimeInSeconds = matchesArray.getJSONObject(i).getInt("totalTimeInSeconds");
            int rating = matchesArray.getJSONObject(i).getInt("rating");
            Recipe recipe = new Recipe(recipeID, recipeName, imageUrls, smallImageUrl, sourceDisplayName, ingredients, totalTimeInSeconds, rating);
            Log.v("Recipe Name", recipe.getRecipeName());
            recipeList.add(recipe);
        }



    }


}



