package com.example.lendahand;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

public class RegActivityFinal extends AppCompatActivity {
    private TabLayout tbDonorReg;

    //these variables are for checking if user swipes
    private float x1,x2;
    static final int MIN_DISTANCE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide title bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reg_final);

        tbDonorReg = findViewById(R.id.tbDoneeReg6);
        //select current tab
        tbDonorReg.getTabAt(4).select();

        //this method changes tab icons to arrows to show step was completed
        setTabIcons();

        //this method ensures that no previous steps can be accessed
        setTabInteractivity();
    }


    //to prevent swiping
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    // Left to Right swipe action
                    if (x2 > x1)
                    {
                        Toast.makeText(this, getText(R.string.txt_do_not_swipe_back), Toast.LENGTH_SHORT).show ();
                    }

                    // Right to left swipe action
                    else
                    {
                        //do nothing
                    }

                }
                else
                {
                    // don't do anything
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void setTabInteractivity() {
        LinearLayout tabStrip1 = ((LinearLayout) tbDonorReg.getChildAt(0));
        tabStrip1.getChildAt(0).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        LinearLayout tabStrip2 = ((LinearLayout) tbDonorReg.getChildAt(0));
        tabStrip2.getChildAt(1).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        LinearLayout tabStrip3 = ((LinearLayout) tbDonorReg.getChildAt(0));
        tabStrip3.getChildAt(2).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        LinearLayout tabStrip4 = ((LinearLayout) tbDonorReg.getChildAt(0));
        tabStrip4.getChildAt(3).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        LinearLayout tabStrip5 = ((LinearLayout) tbDonorReg.getChildAt(0));
        tabStrip5.getChildAt(4).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private void setTabIcons() {
        tbDonorReg.getTabAt(0).setIcon(R.drawable.ic_progress_complete_sel);
        tbDonorReg.getTabAt(1).setIcon(R.drawable.ic_progress_complete_sel);
        tbDonorReg.getTabAt(2).setIcon(R.drawable.ic_progress_complete_sel);
        tbDonorReg.getTabAt(3).setIcon(R.drawable.ic_progress_complete_sel);
        tbDonorReg.getTabAt(4).setIcon(R.drawable.ic_progress_complete_sel);
    }

    public void DonorLogin(View view) {
        //go to log in activity
        Intent intent = new Intent(this, LoginScreenActivity.class);
        startActivity(intent);
        finish();
    }
}
