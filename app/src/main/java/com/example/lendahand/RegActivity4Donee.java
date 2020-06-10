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

public class RegActivity4Donee extends AppCompatActivity {
    private TabLayout tbDoneeReg;
    private TextInputLayout txtStreetAddress, txtSuburb, txtProvince, txtPostalCode;
    private String strStreetAddress, strSuburb, strProvince, strPostalCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide title bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reg4_donee);
        initViews();

        //select current tab
        tbDoneeReg.getTabAt(3).select();

        //this method changes tab icons to arrows to show step was completed
        setTabIcons();

        //this method ensures that only the next step can be accessed
        setTabInteractivity();

        //this method is to handle the on change event handlers of the edit texts
        setUserComponentErrorInteractivity();

        tbDoneeReg.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 4) {
                    //TODO: Validation goes here
                    if (validateInput()) {
                        Intent intent = new Intent(RegActivity4Donee.this, RegActivity5.class);
                        //get from previous activity
                        Bundle bundle = getIntent().getExtras();
                        String strPassword = bundle.getString("password");
                        String strUsername = bundle.getString("username");
                        String strFName = bundle.getString("fname");
                        String strLName = bundle.getString("lname");
                        String strEmail = bundle.getString("email");
                        String strPhoneNumber = bundle.getString("phoneNo");
                        //pass to next activity
                        intent.putExtra("password", strPassword);
                        intent.putExtra("username", strUsername);
                        intent.putExtra("fname", strFName);
                        intent.putExtra("lname", strLName);
                        intent.putExtra("email", strEmail);
                        intent.putExtra("phoneNo", strPhoneNumber);
                        intent.putExtra("postcode", strPostalCode);
                        intent.putExtra("prov", strProvince);
                        intent.putExtra("streetadd", strStreetAddress);
                        intent.putExtra("suburb", strSuburb);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        tbDoneeReg.getTabAt(3).select();
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
        //extract contents of textboxes
        extractInput();
       if (strPostalCode.length()==0){
            txtPostalCode.setError(getText(R.string.txt_empty_field));
            blnValid=false;
        }else{
            if ((strPostalCode.length()<4) || (!Pattern.matches("^[0-9]*$", strPostalCode))){
                txtPostalCode.setError(getText(R.string.txt_invalid_postal_code));
                blnValid=false;
            }
        }

        if (strSuburb.length()==0){
            txtSuburb.setError(getText(R.string.txt_empty_field));
            blnValid=false;
        }
        if (strStreetAddress.length()==0){
            txtStreetAddress.setError(getText(R.string.txt_empty_field));
            blnValid=false;
        }
        if (strProvince.length()==0){
            txtProvince.setError(getText(R.string.txt_empty_field));
            blnValid=false;
        }

        return blnValid;
    }

    private void extractInput() {
        strPostalCode=txtPostalCode.getEditText().getText().toString().trim();
        strProvince=txtProvince.getEditText().getText().toString().trim();
        strStreetAddress=txtStreetAddress.getEditText().getText().toString().trim();
        strSuburb=txtSuburb.getEditText().getText().toString().trim();
    }

    private void setUserComponentErrorInteractivity() {
        txtSuburb.getEditText().addTextChangedListener(new TextWatcher() {
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
                tbDoneeReg.getTabAt(3).select();
                if(s.length() == 0) {
                    txtSuburb.setError(getText(R.string.txt_empty_field));
                }else{
                    txtSuburb.setError(null);
                    txtSuburb.setErrorEnabled(false);
                }
            }
        });
        txtStreetAddress.getEditText().addTextChangedListener(new TextWatcher() {
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
                tbDoneeReg.getTabAt(3).select();
                if(s.length() == 0) {
                    txtStreetAddress.setError(getText(R.string.txt_empty_field));
                }else{
                    txtStreetAddress.setError(null);
                    txtStreetAddress.setErrorEnabled(false);
                }
            }


        });
        txtProvince.getEditText().addTextChangedListener(new TextWatcher() {
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
                tbDoneeReg.getTabAt(3).select();
                if(s.length() == 0) {
                    txtProvince.setError(getText(R.string.txt_empty_field));
                }else{
                    txtProvince.setError(null);
                    txtProvince.setErrorEnabled(false);
                }
            }


        });
        txtPostalCode.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String postalCode = txtPostalCode.getEditText().getText().toString().trim();
                if (postalCode.length()!=0) {
                    if (!Pattern.matches("^[0-9]*$", postalCode)) {
                        txtPostalCode.setError(getText(R.string.txt_invalid_postal_code));
                    } else {
                        if (postalCode.length() < 4) {
                            txtPostalCode.setError(getText(R.string.txt_invalid_postal_code));
                        } else {
                            txtPostalCode.setError(null);
                            txtPostalCode.setErrorEnabled(false);
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
                tbDoneeReg.getTabAt(3).select();
                if(s.length() == 0) {
                    txtPostalCode.setError(getText(R.string.txt_empty_field));
                }
            }


        });
    }

    private void initViews() {
        tbDoneeReg = findViewById(R.id.tbDoneeReg4);
        txtPostalCode=findViewById(R.id.txtDoneePostalCode);
        txtProvince=findViewById(R.id.txtDoneeProvince);
        txtStreetAddress=findViewById(R.id.txtDoneeStreetAddress);
        txtSuburb=findViewById(R.id.txtDoneeSuburb);
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
                return false;
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
        tbDoneeReg.getTabAt(1).setIcon(R.drawable.ic_progress_complete_sel);
        tbDoneeReg.getTabAt(2).setIcon(R.drawable.ic_progress_complete_sel);
    }
}
