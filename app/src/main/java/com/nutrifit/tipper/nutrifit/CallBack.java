package com.nutrifit.tipper.nutrifit;

import com.nutrifit.tipper.nutrifit.Model.Recipe;

import java.util.ArrayList;

/**
 * Created by tipper on 12/2/17.
 */

public interface CallBack {

    void onSuccess(ArrayList<Recipe> recipeList);

    void onFail(String msg);
}
