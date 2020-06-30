package com.example.lendahand;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import static java.lang.Thread.sleep;


public class SplashScreenActivity extends AppCompatActivity {
    Handler handler;
    Runnable runnable;
    double splashScreenDuration = 1.5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //removes title bar of splash screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_splash_screen);

        //hides title bar
        getSupportActionBar().hide();

        //launch splash screen
        launchSplashScreen();
    }


    private void launchSplashScreen() {
        handler = new Handler();
        runnable = new Runnable()
        {
            @Override
            public void run() {
                try {
                    //shows splash screen for splashScreenDuration's seconds
                    sleep((long) (1000 * splashScreenDuration));
                } catch (InterruptedException e) {
                    //if error, print stack trace
                    e.printStackTrace();
                }

                //switches to main activity
                Intent intent = new Intent(SplashScreenActivity.this, OnboardingScreen.class);
                startActivity(intent);
                SplashScreenActivity.this.finish();

            }
        };
        handler.postDelayed(runnable, (long) (1000.0*splashScreenDuration));
    }

    //ensures that next UI doesn't load on back button pressed, and closes app entirely
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        handler.removeCallbacks(runnable);
    }
}

