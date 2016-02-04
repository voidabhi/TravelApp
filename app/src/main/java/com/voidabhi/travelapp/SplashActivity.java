package com.voidabhi.travelapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

    private Handler handler = new Handler();

    //Constants
    private static int SPLASH_TIMEOUT= 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // hide action bar for full screen
        getActionBar().hide();

        // start main activity with delay
        startMainScreenWithDelay();

    }

    // Add delay while showing screen
    private void startMainScreenWithDelay() {

        // runnable thread for the splash screen delay
        Runnable startScreenRunnable = new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        };

        // start splash screen
        handler.postDelayed(startScreenRunnable,SPLASH_TIMEOUT);

    }
}
