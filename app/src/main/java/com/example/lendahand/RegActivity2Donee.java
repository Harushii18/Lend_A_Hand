package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class RegActivity2Donee extends AppCompatActivity {
    private TabLayout tbDoneeReg;
    private TextInputLayout txtFName, txtLName;
    private String strFName, strLName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide title bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reg2_donee);

        initViews();
        //select current tab
        tbDoneeReg.getTabAt(1).select();

        //this method changes tab icons to arrows to show step was completed
        setTabIcons();

        //this method ensures that only the next step can be accessed
        setTabInteractivity();

        //this method is to handle the on change event handlers of the edit texts
        setUserComponentErrorInteractivity();
        tbDoneeReg.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() ==2 ) {
                    if (validateInput()) {
                        //TODO: add to class- make lname and fname capitalised on first letter
                        Intent intent = new Intent(RegActivity2Donee.this, RegActivity3Donee.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else{
                        tbDoneeReg.getTabAt(1).select();
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

    private void initViews() {
        tbDoneeReg = findViewById(R.id.tbDoneeReg2);
        txtFName=findViewById(R.id.txtDoneeFirstName);
        txtLName=findViewById(R.id.txtDoneeLastName);
    }

    private void setUserComponentErrorInteractivity() {
        txtFName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                tbDoneeReg.getTabAt(1).select();
                if(s.length() == 0) {
                    txtFName.setError(getText(R.string.txt_empty_field));
                }else{
                    txtFName.setError(null);
                    txtFName.setErrorEnabled(false);
                }
            }
        });
        txtLName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                tbDoneeReg.getTabAt(1).select();
                if(s.length() == 0) {
                    txtLName.setError(getText(R.string.txt_empty_field));
                }else{
                    txtLName.setError(null);
                    txtLName.setErrorEnabled(false);
                }
            }
        });
    }

    private boolean validateInput() {
        boolean blnValid=true;
        //extract contents of textboxes
        extractInput();
        if (strFName.length()==0){
            txtFName.setError(getText(R.string.txt_empty_field));
            blnValid=false;
        }else{
            //checking if name contains numbers
            if (!Pattern.matches("^\\D*$",strFName)) {
                txtFName.setError(getText(R.string.txt_invalid_fname));
                blnValid=false;
            }
        }
        if (strLName.length()==0){
            txtLName.setError(getText(R.string.txt_empty_field));
            blnValid=false;
        }else{
            //checking if name contains numbers
            if (!Pattern.matches("^\\D*$",strLName)) {
                txtLName.setError(getText(R.string.txt_invalid_lname));
                blnValid=false;
            }
        }
        return blnValid;
    }

    private void extractInput() {
        strLName=txtLName.getEditText().getText().toString().trim();
        strFName=txtFName.getEditText().getText().toString().trim();
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
                return false;
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
                return true;
            }
        });
    }

    private void setTabIcons() {
        tbDoneeReg.getTabAt(0).setIcon(R.drawable.ic_progress_complete_sel);
    }
}
