package com.example.lendahand;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //removes title bar of splash screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //dims the activity and status bar
        View decorView = SplashScreenActivity.this.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //ensures that status bar shows in front of full screen splash screen if it is a certain build (android version must be kitkat and above)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }else {
            //covers status bar with full screen splash screen
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.activity_splash_screen);

        //hides title bar
        getSupportActionBar().hide();

        //launch splash screen
        SplashScreenLauncher splashScreenLauncher=new SplashScreenLauncher();
        splashScreenLauncher.start();
    }


    private class SplashScreenLauncher extends Thread{
        public void run(){
            try{
                //shows splash screen for splashScreenDuration's seconds
                double splashScreenDuration = 1.5;
                sleep((long) (1000 * splashScreenDuration));
            }catch(InterruptedException e){
                //if error, print stack trace
                e.printStackTrace();
            }

            //switches to main activity
            Intent intent=new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);
            SplashScreenActivity.this.finish();
        }
    }
}

