package com.example.lendahand;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class RegActivity4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide title bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reg4);
    }
}
