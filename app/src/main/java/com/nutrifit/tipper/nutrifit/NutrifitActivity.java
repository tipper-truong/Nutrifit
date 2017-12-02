package com.nutrifit.tipper.nutrifit;


import android.app.SearchManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nutrifit.tipper.nutrifit.Model.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NutrifitActivity extends AppCompatActivity {

    private ArrayList<Recipe> recipeList;
    private RequestQueue requestQueue;
    private static String RECIPE_URL = "http://api.yummly.com/v1/api/recipes?_app_id=e8611421&_app_key=7102c1ff2ffc74cf805a8bee8b7281a8&q=healthy%20pho";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrifit);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#60b0f4")));

        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = new CardFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }


        //recipeList = new ArrayList<Recipe>();
        //requestQueue = Volley.newRequestQueue(this);
        // retrieve recipe data
        // retrieveRecipeData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    private void retrieveRecipeData()
    {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, RECIPE_URL, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try {
                            parseRecipeData(response);
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
            recipeList.add(recipe);
        }

    }
}
