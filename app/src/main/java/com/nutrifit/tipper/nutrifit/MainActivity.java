package com.nutrifit.tipper.nutrifit;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app Next activity
                Intent i = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, 5000);
    }
}
