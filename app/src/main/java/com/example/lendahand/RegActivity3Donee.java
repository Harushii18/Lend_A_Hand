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

public class RegActivity3Donee extends AppCompatActivity {
    private TabLayout tbDoneeReg;
    private TextInputLayout txtPhoneNumber, txtEmail;
    private String strPhoneNumber, strEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide title bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reg3_donee);

        initViews();

        //select current tab
        tbDoneeReg.getTabAt(2).select();

        //this method changes tab icons to arrows to show step was completed
        setTabIcons();

        //this method ensures that only the next step can be accessed
        setTabInteractivity();

        //this method is to handle the on change event handlers of the edit texts
        setUserComponentErrorInteractivity();

        tbDoneeReg.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 3) {
                    if (validateInput()) {
                        Intent intent = new Intent(RegActivity3Donee.this, RegActivity4Donee.class);
                        //get from previous activity
                        Bundle bundle = getIntent().getExtras();
                        String strPassword = bundle.getString("password");
                        String strUsername = bundle.getString("username");
                        String strFName = bundle.getString("fname");
                        String strLName = bundle.getString("lname");
                        //pass to next activity
                        intent.putExtra("password", strPassword);
                        intent.putExtra("username", strUsername);
                        intent.putExtra("fname", strFName);
                        intent.putExtra("lname", strLName);
                        intent.putExtra("email", strEmail);
                        intent.putExtra("phoneNo", strPhoneNumber);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        tbDoneeReg.getTabAt(2).select();
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

    private void setUserComponentErrorInteractivity() {
        txtEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                tbDoneeReg.getTabAt(2).select();
                if (s.length() == 0) {
                    txtEmail.setError(getText(R.string.txt_empty_field));
                } else {
                    txtEmail.setError(null);
                    txtEmail.setErrorEnabled(false);
                }
            }
        });

        txtPhoneNumber.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String phoneNum = txtPhoneNumber.getEditText().getText().toString().trim();
                if (phoneNum.length()!=0) {
                    if (!Pattern.matches("^[0-9]*$", phoneNum)) {
                        txtPhoneNumber.setError(getText(R.string.txt_invalid_phone_number));
                    } else {
                        if (phoneNum.length() < 10) {
                            txtPhoneNumber.setError(getText(R.string.txt_invalid_phone_number));
                        } else {
                            txtPhoneNumber.setError(null);
                            txtPhoneNumber.setErrorEnabled(false);
                        }
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                tbDoneeReg.getTabAt(2).select();

                if (s.length() == 0) {
                    txtPhoneNumber.setError(getText(R.string.txt_empty_field));
                }

            }
        });
    }

    private void initViews() {
        tbDoneeReg = findViewById(R.id.tbDoneeReg3);
        txtEmail = findViewById(R.id.txtDoneeEmail);
        txtPhoneNumber = findViewById(R.id.txtDoneePhoneNumber);
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
                return false;
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

    private boolean validateInput() {
        boolean blnValid = true;
        //extract contents of textboxes
        extractInput();
        if (strEmail.length() == 0) {
            txtEmail.setError(getText(R.string.txt_empty_field));
            blnValid = false;
        } else {
            //checking if email is valid
            if (!Pattern.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", strEmail.toLowerCase())) {
                txtEmail.setError(getText(R.string.txt_invalid_email));
                blnValid = false;
            }
        }
        if (strPhoneNumber.length() == 0) {
            txtPhoneNumber.setError(getText(R.string.txt_empty_field));
            blnValid = false;
        } else {
            //checking if it is only numbers
            if ((strPhoneNumber.length()<10) || (!Pattern.matches("^[0-9]*$", strPhoneNumber))) {
                txtPhoneNumber.setError(getText(R.string.txt_invalid_phone_number));
                blnValid = false;
            }

        }

        return blnValid;
    }

    private void extractInput() {
        strPhoneNumber = txtPhoneNumber.getEditText().getText().toString().trim();
        strEmail = txtEmail.getEditText().getText().toString().trim();
    }

    private void setTabIcons() {
        tbDoneeReg.getTabAt(0).setIcon(R.drawable.ic_progress_complete_sel);
        tbDoneeReg.getTabAt(1).setIcon(R.drawable.ic_progress_complete_sel);
    }
}
