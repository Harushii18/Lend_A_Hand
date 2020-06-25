package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class RegActivity4Donee extends AppCompatActivity {
    private TabLayout tbDoneeReg;
    private TextInputLayout txtStreetAddress, txtSuburb, txtPostalCode,txtProvLayout;
    private AutoCompleteTextView txtProvince;
    private String strStreetAddress, strSuburb, strProvince="", strPostalCode;
    private  String[] arrProvinces = new String[] {"KwaZulu-Natal", "Western Cape", "North West", "Northern Cape","Free State","Gauteng", "Limpopo","Mpumalanga", "Eastern Cape"};
    private ArrayAdapter<String> adapter;

    //these variables are for checking if user swipes
    private float x1,x2;
    static final int MIN_DISTANCE = 100;

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
        String strChkProvince=txtProvince.getText().toString();

        if ((strProvince.length()==0) && (strChkProvince.length()==0)){
            txtProvLayout.setError(getText(R.string.txt_empty_field));
            blnValid=false;
        }else{
            if (strChkProvince!=strProvince){
                boolean blnFound=false;
                //checking that user didn't type in their own province
                for (int i=0;i<arrProvinces.length;i++){
                    if (strChkProvince.equals(arrProvinces[i])){
                        blnFound=true;
                    }
                }
                if (blnFound==false){
                    blnValid=false;
                    txtProvLayout.setError(getText(R.string.txt_invalid_province));
                    txtProvince.setText("");
                }
            }
        }
        return blnValid;
    }

    private void extractInput() {
        strPostalCode=txtPostalCode.getEditText().getText().toString().trim();
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
        txtStreetAddress=findViewById(R.id.txtDoneeStreetAddress);
        txtSuburb=findViewById(R.id.txtDoneeSuburb);
        //populate province drop down menu
        txtProvLayout=findViewById(R.id.txtDoneeProvinceLayout);
        adapter =
                new ArrayAdapter<>(
                        RegActivity4Donee.this,
                        R.layout.reg_prov_combo_box,
                        arrProvinces);
        txtProvince = this.findViewById(R.id.txtDoneeProvince);
        txtProvince.setAdapter(adapter);
        txtProvince.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //extract items
                strProvince=parent.getItemAtPosition(position).toString();
                txtProvLayout.setError(null);
                txtProvLayout.setErrorEnabled(false);
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
