package com.example.lendahand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

public class OnboardingScreen extends AppCompatActivity {
    public static CustomViewPager viewPager;

    SliderAdapter sliderAdapter;
    TabLayout tabIndicator;
    Button btnNext;
    Button btnGetStarted;
    Button btnPrev;
    int position=0;
    Animation animationGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_screen);
        //Hooks
        //Initialise all views
        viewPager=(CustomViewPager) findViewById(R.id.slider);
        btnGetStarted=(Button)findViewById(R.id.btnGetStarted);
        btnNext=(Button)findViewById(R.id.btnNext);
        btnPrev=(Button)findViewById(R.id.btnPrev);
        tabIndicator=findViewById(R.id.tabLayout);
        animationGetStarted= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.btn_get_started_anim);

        //Call Adapter
        sliderAdapter=new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        //set tabs to be in line with dots
        tabIndicator.setupWithViewPager(viewPager);

        //next button, on click, go to next slide
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position=viewPager.getCurrentItem();
                if (position<5) {
                    position++;
                    viewPager.setCurrentItem(position);
                    if (position==2){
                        btnPrev.setVisibility(View.VISIBLE);
                    }
                    if (position==4){
                        loadLastScreen();
                    }
                }
            }
        });

        //previous button on click, return to previous slide
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position=viewPager.getCurrentItem();
                if (position>0) {
                    position--;
                    viewPager.setCurrentItem(position);
                }
                if (position==0){
                    btnPrev.setVisibility(View.INVISIBLE);
                }
            }
        });

        //making certain buttons show/hide on tab movement
        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()<4){
                    btnPrev.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.VISIBLE);
                    tabIndicator.setVisibility(View.VISIBLE);
                    btnGetStarted.setVisibility(View.INVISIBLE);
                }
                if (tab.getPosition()==0){
                    btnPrev.setVisibility(View.INVISIBLE);
                }
                if (tab.getPosition()==4){
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        //on click method for get started to go to next activity
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OnboardingScreen.this,LoginScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        //makes certain that onboarding screen doesn't open again after installation
        if (isOpenAlready()){
            Intent intent = new Intent(OnboardingScreen.this,LoginScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            SharedPreferences.Editor editor=getSharedPreferences("slide",MODE_PRIVATE).edit();
            editor.putBoolean("slide",true);
            editor.commit();
        }
    }

    //removes next, indicator and previous button and shows get started button
    private void loadLastScreen() {
        viewPager.setPagingEnabled(false);
        btnNext.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        btnPrev.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);

        //animate get started button
        btnGetStarted.setAnimation(animationGetStarted);
    }

    //method to prevent opening onboarding screens after initial installation
    private boolean isOpenAlready() {
        SharedPreferences sharedPreferences=getSharedPreferences("slide",MODE_PRIVATE);
        boolean result=sharedPreferences.getBoolean("slide",false);
        return result;
    }

}
