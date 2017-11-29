package com.nutrifit.tipper.nutrifit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.nutrifit.tipper.nutrifit.Database.DatabaseHandler;
import com.nutrifit.tipper.nutrifit.Model.User;

import org.json.JSONException;
import org.json.JSONObject;


public class SignInActivity extends AppCompatActivity {

    private Button signInButton;
    private Button newUserSignUpButton;
    private EditText signInEmail;
    private EditText signInPW;
    private CallbackManager callbackManager;
    private ProgressDialog mDialog;
    private LoginButton fbLoginButton;
    private DatabaseHandler db;
    public static final String USER = "USER";
    public static final String BOOLEAN_SIGNUP = "SIGN_UP";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        db = new DatabaseHandler(this);

        /** If user already signed in, just direct them to Home Page **/
        sessionUser();

        /* For creating/upgrading tables because doing Database db = new DatabaseHandler(); doesn't create new tables or upgrade it for some reason */
        SQLiteDatabase database = new DatabaseHandler(getApplicationContext()).getWritableDatabase();
        database.close();

        signInEmail = (EditText) findViewById(R.id.signIn_email);
        signInPW = (EditText) findViewById(R.id.signIn_password);
        signInButton = (Button) findViewById(R.id.signInButton);
        newUserSignUpButton = (Button) findViewById(R.id.newUser_SignUp);

        /* Checking user credentials when signing in */
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signInEmail.getText().toString();
                User retUser = db.getUser(email);
                String password = signInPW.getText().toString();
                String retPassword = retUser.getPassword();

                Log.v("Email", email);
                Log.v("Password", password);
                Log.v("DB Email", retUser.getEmail());
                Log.v("DB Password", retPassword);
                if(retUser.getEmail().equals(email) && retPassword.equals(password)) {
                    Intent i = new Intent(v.getContext(), SearchRecipeActivity.class);
                    startActivity(i);
                    //finish();
                    saveSignUpFirstTime(SignInActivity.this);

                } else {
                    Toast.makeText(getApplicationContext(), "User credentials incorrect, please try again", Toast.LENGTH_SHORT);
                }
            }
        });

        /* New User? Sign up */
        signUp();

        /* Facebook Login */
        facebookLogin();
    }

    private void sessionUser()
    {
        if(!signUpFirstTime()) {
            User sessionUser = getUserData();
            try {
                User dbUser = db.getUser(sessionUser.getEmail());
                if (dbUser != null && dbUser.getCaloriesToBurnPerDay() != 0 && dbUser.getFitnessGoals() != null) {
                    Intent i = new Intent(SignInActivity.this, SearchRecipeActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    // Log out from Facebook
                    LoginManager.getInstance().logOut();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private void signUp()
    {
        newUserSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });
    }

    private void facebookLogin()
    {
        callbackManager = CallbackManager.Factory.create();
        fbLoginButton = (LoginButton) findViewById(R.id.login_facebook_button);
        fbLoginButton.setReadPermissions("public_profile", "email");
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mDialog = new ProgressDialog(SignInActivity.this);
                mDialog.setMessage("Retrieving Facebook User Data...");
                mDialog.show();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        mDialog.dismiss();
                        Log.d("Response", response.toString());
                        getFacebookData(object);
                    }
                });

                // Request Graph API
                Bundle parameters = new Bundle();
                parameters.putString("fields","id, email, first_name, last_name, gender");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        if(AccessToken.getCurrentAccessToken() != null) {
            Log.v("Email Access Token", AccessToken.getCurrentAccessToken().getUserId());
        }
    }

    private void getFacebookData(JSONObject object)
    {
        try {

            String firstName = object.getString("first_name");
            String lastName = object.getString("last_name");
            String email = object.getString("email");
            String gender = object.getString("gender");

            User user = new User(firstName, lastName, email, null, gender, null, 0);
            boolean userExist = db.addUser(user, SignInActivity.this);
            if(!userExist) {
                saveUserData(getApplicationContext(), user);
                Intent i = new Intent(SignInActivity.this, FitnessGoalsActivity.class);
                startActivity(i);
                finish();
            } else {
                Intent i = new Intent(SignInActivity.this, SearchRecipeActivity.class);
                startActivity(i);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
        settings = getApplicationContext().getSharedPreferences(USER, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String userObj = settings.getString(USER, null);
        User retUser = gson.fromJson(userObj, User.class);
        return retUser;
    }

    private void saveSignUpFirstTime(Context context)
    {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(BOOLEAN_SIGNUP, Context.MODE_PRIVATE);
        editor = settings.edit();

        boolean signUpFirstTime = false;

        editor.putBoolean(BOOLEAN_SIGNUP, signUpFirstTime);
        editor.commit();
    }


    private boolean signUpFirstTime()
    {
        SharedPreferences settings;
        settings = getApplicationContext().getSharedPreferences(BOOLEAN_SIGNUP, Context.MODE_PRIVATE);
        boolean signUpFirstTime = settings.getBoolean(BOOLEAN_SIGNUP, true);
        return signUpFirstTime;
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