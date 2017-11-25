package com.nutrifit.tipper.nutrifit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nutrifit.tipper.nutrifit.Database.DatabaseHandler;
import com.nutrifit.tipper.nutrifit.Objects.User;

public class FitnessGoalsActivity extends AppCompatActivity {

    private RadioGroup fitnessGoalsRadioGroup;
    private DatabaseHandler db;
    private User user;
    public static final String PREFS_NAME = "USER" ;
    public static final String USER = "USER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_goals);

        db = new DatabaseHandler(this);

        user = getUserData();

        /** User selecting Fitness Goals: Lose Weight or Gain Weight **/
        fitnessGoalsRadioGroup = (RadioGroup) findViewById(R.id.fitnessGoalsRadioGroup);
        fitnessGoalsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                int radioButtonID = fitnessGoalsRadioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) fitnessGoalsRadioGroup.findViewById(radioButtonID);
                String selectFitnessGoals = (String) radioButton.getText();
                updateFitnessGoals(user.getGender(), selectFitnessGoals);
                saveUserData(getApplicationContext(), user);
                db.updateUser(user);

            }
        });

        /** Submit New User Information **/
        Button submitFitnessGoalsButton = (Button) findViewById(R.id.submitFitnessGoals);
        submitFitnessGoalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.updateUser(user);
                Intent i = new Intent(getApplicationContext(), SearchRecipeActivity.class);
                startActivity(i);
                finish();
            }
        });

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

    private User getUserData()
    {
        SharedPreferences settings;
        settings = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String userObj = settings.getString(PREFS_NAME, null);
        User retUser = gson.fromJson(userObj, User.class);
        return retUser;
    }

    private void updateFitnessGoals(String gender, String selectedFitnessGoals)
    {

        if(gender.equalsIgnoreCase("female") && selectedFitnessGoals.equals("Lose Weight")) {
            user.setFitnessGoals(selectedFitnessGoals);
            user.setCaloriesToBurnPerDay(1500);
            Toast.makeText(getApplicationContext(), "Calories Intake Per Day: 1500", Toast.LENGTH_SHORT);
        } else if (gender.equalsIgnoreCase("female") && selectedFitnessGoals.equals("Gain Weight")) {
            user.setFitnessGoals(selectedFitnessGoals);
            user.setCaloriesToBurnPerDay(2000);
            Toast.makeText(getApplicationContext(), "Calories Intake Per Day: 2000", Toast.LENGTH_SHORT);
        } else if (gender.equalsIgnoreCase("male") && selectedFitnessGoals.equals("Lose Weight")) {
            user.setFitnessGoals(selectedFitnessGoals);
            user.setCaloriesToBurnPerDay(2000);
            Toast.makeText(getApplicationContext(), "Calories Intake Per Day: 2500", Toast.LENGTH_SHORT);
        } else if (gender.equalsIgnoreCase("male") && selectedFitnessGoals.equals("Gain Weight")) {
            user.setFitnessGoals(selectedFitnessGoals);
            user.setCaloriesToBurnPerDay(2500);
            Toast.makeText(getApplicationContext(), "Calories Intake Per Day: 1500", Toast.LENGTH_SHORT);
        }

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(db != null) {
            db.close();
        }
    }
}
