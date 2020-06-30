package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class RegActivity2 extends AppCompatActivity {
    private TabLayout tbDonorReg;
    private TextInputLayout txtFName, txtLName;
    private String strFName, strLName;

    //these variables are for checking if user swipes
    private float x1,x2;
    static final int MIN_DISTANCE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide title bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reg2);

        initViews();
        //select current tab
        tbDonorReg.getTabAt(1).select();

        //this method changes tab icons to arrows to show step was completed
        setTabIcons();

        //this method ensures that only the next step can be accessed
        setTabInteractivity();

        //this method is to handle the on change event handlers of the edit texts
        setUserComponentErrorInteractivity();
        tbDonorReg.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() ==2 ) {
                    goToNextActivity();
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

    private void goToNextActivity() {
            if (validateInput()) {

                Intent intent = new Intent(RegActivity2.this, RegActivity3.class);
                //get from previous activity
                Bundle bundle = getIntent().getExtras();
                String strPassword = bundle.getString("password");
                String strUsername = bundle.getString("username");
                //pass to next activity
                intent.putExtra("password", strPassword);
                intent.putExtra("username", strUsername);
                intent.putExtra("fname", strFName);
                intent.putExtra("lname", strLName);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }else{
                tbDonorReg.getTabAt(1).select();
            }

    }

    private void initViews() {
        tbDonorReg = findViewById(R.id.tbDonorReg2);
        txtFName=findViewById(R.id.txtDonorFirstName);
        txtLName=findViewById(R.id.txtDonorLastName);
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
                       goToNextActivity();
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
                tbDonorReg.getTabAt(1).select();
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
                tbDonorReg.getTabAt(1).select();
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
                return false;
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
    }
}
