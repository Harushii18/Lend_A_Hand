package com.example.lendahand;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

public class RegActivity5 extends AppCompatActivity {
    private TabLayout tbDoneeReg;
    private TextInputLayout txtMotivationalLetter;
    private String strMotivationalLetter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide title bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reg5);

        tbDoneeReg = findViewById(R.id.tbDoneeReg5);
        txtMotivationalLetter=findViewById(R.id.txtDoneeMotLetter);

        //select current tab
        tbDoneeReg.getTabAt(4).select();

        //this method changes tab icons to arrows to show step was completed
        setTabIcons();

        //this method ensures that only the next step can be accessed
        setTabInteractivity();

        //this method is to handle the on change event handlers of the edit texts
        setUserComponentErrorInteractivity();

        tbDoneeReg.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 5) {
                    if (validateInput()) {
                        //TODO: Add motivational letter to class
                        Intent intent = new Intent(RegActivity5.this, RegActivityFinalDonee.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else{
                        tbDoneeReg = findViewById(R.id.tbDoneeReg5);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private boolean validateInput() {
        boolean blnValid=true;
        strMotivationalLetter=txtMotivationalLetter.getEditText().getText().toString().trim();
        if (strMotivationalLetter.length()==0){
            txtMotivationalLetter.setError(getText(R.string.txt_empty_field));
            blnValid=false;
        }
        return blnValid;
    }

    private void setUserComponentErrorInteractivity() {
        txtMotivationalLetter.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                tbDoneeReg.getTabAt(4).select();
                if(s.length() == 0) {
                    txtMotivationalLetter.setError(getText(R.string.txt_empty_field));
                }else{
                    txtMotivationalLetter.setError(null);
                    txtMotivationalLetter.setErrorEnabled(false);
                }
            }
        });
    }

    private void setTabInteractivity() {
        LinearLayout tabStrip1 = ((LinearLayout) tbDoneeReg.getChildAt(0));
        tabStrip1.getChildAt(0).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        LinearLayout tabStrip2 = ((LinearLayout) tbDoneeReg.getChildAt(0));
        tabStrip2.getChildAt(1).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        LinearLayout tabStrip3 = ((LinearLayout) tbDoneeReg.getChildAt(0));
        tabStrip3.getChildAt(2).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        LinearLayout tabStrip4 = ((LinearLayout) tbDoneeReg.getChildAt(0));
        tabStrip4.getChildAt(3).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        LinearLayout tabStrip5 = ((LinearLayout) tbDoneeReg.getChildAt(0));
        tabStrip5.getChildAt(4).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        LinearLayout tabStrip6 = ((LinearLayout) tbDoneeReg.getChildAt(0));
        tabStrip6.getChildAt(5).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    private void setTabIcons() {
        tbDoneeReg.getTabAt(0).setIcon(R.drawable.ic_progress_complete_sel);
        tbDoneeReg.getTabAt(1).setIcon(R.drawable.ic_progress_complete_sel);
        tbDoneeReg.getTabAt(2).setIcon(R.drawable.ic_progress_complete_sel);
        tbDoneeReg.getTabAt(3).setIcon(R.drawable.ic_progress_complete_sel);

    }
}
