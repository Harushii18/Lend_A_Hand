package com.example.lendahand;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import static android.graphics.Color.parseColor;


public class SelectUserTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide title bar
        getSupportActionBar().hide();

        //get colour of status bar to be the same as background
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(parseColor("#81d4fa"));

        setContentView(R.layout.activity_select_user_type);
    }

    public void Login(View view) {
        //go to log in activity
        Intent intent = new Intent(this, LoginScreenActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToDonorRegScreen(View view) {
        //go to donor reg activity
        Intent intent = new Intent(this, RegActivity1.class);
        startActivity(intent);
        //didn't add finish, so they can go back
    }

    public void goToDoneeRegScreen(View view) {
        //go to donee reg activity
        Intent intent = new Intent(this, RegActivity1Donee.class);
        startActivity(intent);
        //didn't add finish, so they can go back
    }
}
