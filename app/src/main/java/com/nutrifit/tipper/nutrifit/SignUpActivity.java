package com.nutrifit.tipper.nutrifit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nutrifit.tipper.nutrifit.Database.DatabaseHandler;
import com.nutrifit.tipper.nutrifit.Model.User;

public class SignUpActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private Spinner genderSpinner;
    private RadioGroup fitnessGoalsRadioGroup;
    private Button signUpButton;
    public static final String USER = "USER";
    public static final String SIGN_UP = "SIGN_UP";
    private String selectedFitnessGoals;
    private User user;
    private boolean signUpFirstTime;
    private DatabaseHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().hide();

        db = new DatabaseHandler(this);

        firstName = (EditText) findViewById(R.id.signUp_firstName);
        lastName = (EditText) findViewById(R.id.signUp_lastName);
        email = (EditText) findViewById(R.id.signUp_email);
        password = (EditText) findViewById(R.id.signUp_password);
        genderSpinner = (Spinner) findViewById(R.id.genderSpinner);
        fitnessGoalsRadioGroup = (RadioGroup) findViewById(R.id.signUp_fitnessGoals);
        signUpButton = (Button) findViewById(R.id.signUp);

        fitnessGoalsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                int radioButtonID = fitnessGoalsRadioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) fitnessGoalsRadioGroup.findViewById(radioButtonID);
                selectedFitnessGoals = (String) radioButton.getText();

            }
        });


        /** Sign Up New User **/
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String gender = genderSpinner.getItemAtPosition(genderSpinner.getSelectedItemPosition()).toString();
                if(!gender.equals("Choose your gender")) {
                    if(gender.equalsIgnoreCase("female") && selectedFitnessGoals.equals("Lose Weight")) {
                        Toast toast = Toast.makeText(SignUpActivity.this, "Calories Intake Per Day: 1500", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 0);
                        toast.show();
                        user = new User(firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(), password.getText().toString(), gender, selectedFitnessGoals, 1500);
                    } else if (gender.equalsIgnoreCase("female") && selectedFitnessGoals.equals("Gain Weight")) {
                        Toast toast = Toast.makeText(SignUpActivity.this, "Calories Intake Per Day: 2000", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 0);
                        toast.show();
                        user = new User(firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(), password.getText().toString(), gender, selectedFitnessGoals, 2000);
                    } else if (gender.equalsIgnoreCase("male") && selectedFitnessGoals.equals("Lose Weight")) {
                        Toast toast = Toast.makeText(SignUpActivity.this, "Calories Intake Per Day: 2000", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 0);
                        toast.show();
                        user = new User(firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(), password.getText().toString(), gender, selectedFitnessGoals, 2000);
                    } else if (gender.equalsIgnoreCase("male") && selectedFitnessGoals.equals("Gain Weight")) {
                        Toast toast = Toast.makeText(SignUpActivity.this, "Calories Intake Per Day: 2500", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 0);
                        toast.show();
                        user = new User(firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(), password.getText().toString(), gender, selectedFitnessGoals, 2500);
                    }
                    saveUserData(getApplicationContext(), user);
                    boolean userExist = db.addUser(user, SignUpActivity.this);
                    if(userExist) {
                        Toast toast = Toast.makeText(SignUpActivity.this, "User already exist, please try again", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 0);
                        toast.show();
                    }

                    saveSignUpFirstTime(SignUpActivity.this);

                    Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
                    startActivity(i);

                    db.close();

                } else {
                    Toast toast = Toast.makeText(SignUpActivity.this, "Please select a gender", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 0);
                    toast.show();
                }
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

    private void saveSignUpFirstTime(Context context)
    {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(SIGN_UP, Context.MODE_PRIVATE);
        editor = settings.edit();

        signUpFirstTime = true;

        editor.putBoolean(SIGN_UP, signUpFirstTime);
        editor.commit();
    }


}
