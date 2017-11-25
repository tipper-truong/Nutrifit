package com.nutrifit.tipper.nutrifit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

public class SignInActivity extends AppCompatActivity {

    private Button newUserSignUpButton;
    private CallbackManager callbackManager;
    private ProgressDialog mDialog;
    private LoginButton fbLoginButton;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        newUserSignUpButton = (Button) findViewById(R.id.newUser_SignUp);

        /* New User? Sign up */
        signUp();

        /* Facebook Login */
        facebookLogin();
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

            Log.v("Email", object.getString("email"));
            Log.v("First Name", object.getString("first_name"));
            Log.v("Last Name", object.getString("last_name"));
            Log.v("Gender", object.getString("gender"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}