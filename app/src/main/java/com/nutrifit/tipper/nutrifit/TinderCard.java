package com.nutrifit.tipper.nutrifit;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.google.gson.Gson;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeHead;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;
import com.mindorks.placeholderview.annotations.swipe.SwipeView;
import com.nutrifit.tipper.nutrifit.Database.DatabaseHandler;
import com.nutrifit.tipper.nutrifit.Model.Recipe;
import com.nutrifit.tipper.nutrifit.Model.User;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

// Code Citation for SwipeView: https://blog.mindorks.com/android-tinder-swipe-view-example-3eca9b0d4794

@Layout(R.layout.tinder_card_view)
public class TinderCard {

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameAgeTxt)
    private TextView nameAgeTxt;

    @View(R.id.locationNameTxt)
    private TextView locationNameTxt;

    @SwipeView
    private android.view.View cardView;

    private Recipe mRecipe;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private DatabaseHandler db;
    private User user;
    public static final String USER = "USER";

    public TinderCard(Context context, Recipe recipe, SwipePlaceHolderView swipeView, User user) {
        db = new DatabaseHandler(context);
        mContext = context;
        mRecipe = recipe;
        mSwipeView = swipeView;
        this.user = user;
    }

    @Resolve
    private void onResolved(){
        MultiTransformation multi = new MultiTransformation(
                new BlurTransformation(mContext, 30),
                new RoundedCornersTransformation(
                        mContext, Utils.dpToPx(7), 0,
                        RoundedCornersTransformation.CornerType.TOP));

        Glide.with(mContext).load(mRecipe.getImageUrl())
                .bitmapTransform(multi)
                .into(profileImageView);
        nameAgeTxt.setText(mRecipe.getRecipeName() + ", " + mRecipe.getRating());
        locationNameTxt.setText(mRecipe.getSourceDisplayName());
    }

    @SwipeHead
    private void onSwipeHeadCard() {
        Glide.with(mContext).load(mRecipe.getImageUrl())
                .bitmapTransform(new RoundedCornersTransformation(
                        mContext, Utils.dpToPx(7), 0,
                        RoundedCornersTransformation.CornerType.TOP))
                .into(profileImageView);
        cardView.invalidate();
    }

    @Click(R.id.profileImageView)
    private void onClick(){
        Log.d("EVENT", "profileImageView click");
//        mSwipeView.addView(this);
    }

    @SwipeOut
    private void onSwipedOut(){
        Log.d("EVENT", "onSwipedOut");
//        mSwipeView.addView(this);
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn(){
        Log.d("EVENT", "onSwipedIn");

        User dbUser = db.getUser(user.getEmail());
        float remaining = 0; // Goal - Food + Exercise = Remaining
        float foodCal = dbUser.getFoodCalories();
        float recipeSwipedCalories = getRecipeCalories(mRecipe);
        if(foodCal == 0) {
            user.setFoodCalories(recipeSwipedCalories);
            remaining = dbUser.getCaloriesToBurnPerDay() - dbUser.getFoodCalories() + user.getFoodCalories();
            user.setCaloriesToBurnPerDay(remaining);
            db.updateUser(user);
            saveUserData(mContext, user);
        } else {
            foodCal += recipeSwipedCalories;
            user.setFoodCalories(foodCal);
            remaining = dbUser.getCaloriesToBurnPerDay() - dbUser.getFoodCalories() + user.getFoodCalories();
            user.setCaloriesToBurnPerDay(remaining);
            db.updateUser(user);
            saveUserData(mContext, user);
        }
        db.addRecipe(mRecipe);
    }

    @SwipeInState
    private void onSwipeInState(){
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
        Log.d("EVENT", "onSwipeOutState");
    }

    private float getRecipeCalories(Recipe recipe)
    {
        float calories;
        if(recipe.getIngredients().size() > 0 && recipe.getIngredients().size() <= 3) {
            calories = 150;
        } else if (recipe.getIngredients().size() > 3 && recipe.getIngredients().size() <= 6) {
            calories = 250;
        } else if(recipe.getIngredients().size() > 6 && recipe.getIngredients().size() <= 9) {
            calories = 350;
        } else if(recipe.getIngredients().size() > 9 && recipe.getIngredients().size() <= 12) {
            calories = 450;
        } else {
            calories = 550;
        }
        return calories;
    }

    private void saveUserData(Context context, User user) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(USER, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String userObj = gson.toJson(user);

        editor.putString(USER, userObj);
        editor.commit();
    }
}
