package com.nutrifit.tipper.nutrifit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecipeDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecipeDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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

    private OnFragmentInteractionListener mListener;

    public RecipeDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipeDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeDetailsFragment newInstance(String param1, String param2) {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static RecipeDetailsFragment newInstance()
    {
        RecipeDetailsFragment rdFragment = new RecipeDetailsFragment();
        return rdFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View recipeDetailView = inflater.inflate(R.layout.fragment_recipe_details, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar();

        user = getUserData();
        recipe = getRecipeData();

        db = new DatabaseHandler(getActivity());

        recipeImage = (ImageView) recipeDetailView.findViewById(R.id.recipeImage);
        recipeName = (EditText) recipeDetailView.findViewById(R.id.recipeName);
        rating = (RatingBar) recipeDetailView.findViewById(R.id.recipeRating);
        recipeTime = (EditText) recipeDetailView.findViewById(R.id.recipeTime);
        recipeIngredients = (EditText) recipeDetailView.findViewById(R.id.recipeIngredients);
        sourceName = (EditText) recipeDetailView.findViewById(R.id.recipeSourceName);
        calories = (TextView) recipeDetailView.findViewById(R.id.recipeCalories);
        updateButton = (Button) recipeDetailView.findViewById(R.id.updateButton);

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

                        SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = mySPrefs.edit();
                        editor.remove(RECIPE);
                        editor.apply();

                        saveRecipeData(getActivity(), recipe);

                        Toast toast = Toast.makeText(getActivity(), "Updated Recipe successfully", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                        Fragment selectedFragment = new FavoritesFragment();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragmentContainer, selectedFragment);
                        transaction.commit();


                    } else {
                        Toast toast = Toast.makeText(getActivity(), "Updated Recipe unsuccessfully, please try again", Toast.LENGTH_LONG);
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
                Toast toast = Toast.makeText(getActivity(), "User is not allowed to edit calories", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

        return recipeDetailView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private User getUserData()
    {
        SharedPreferences settings;
        settings = getActivity().getSharedPreferences(USER, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String userObj = settings.getString(USER, null);
        User retUser = gson.fromJson(userObj, User.class);
        return retUser;
    }

    private Recipe getRecipeData()
    {
        SharedPreferences settings;
        settings = getActivity().getSharedPreferences(RECIPE, Context.MODE_PRIVATE);
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
        Picasso.with(getActivity()).load(recipe.getImageUrl()).into(recipeImage);

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
