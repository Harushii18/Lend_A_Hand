package com.example.lendahand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HowItWorksActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_it_works);

        toolbar=findViewById(R.id.toolbar_How);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(HowItWorksActivity.this, DoneeDashboard.class);
                startActivity(i);
                finish();
            }
        });


    }
    @Override
    public void onBackPressed() {

            super.onBackPressed();


    }



}
