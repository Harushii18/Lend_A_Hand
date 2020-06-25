package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegActivity3 extends AppCompatActivity {
    private TabLayout tbDonorReg;
    private TextInputLayout txtPhoneNumber, txtEmail;
    private String strPhoneNumber, strEmail;
    private String urlLink = "https://lamp.ms.wits.ac.za/home/s2089676/";
    private OkHttpClient client;
    private boolean blnExist;

    //these variables are for checking if user swipes
    private float x1,x2;
    static final int MIN_DISTANCE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide title bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reg3);

        initViews();

        //select current tab
        tbDonorReg.getTabAt(2).select();

        //this method changes tab icons to arrows to show step was completed
        setTabIcons();

        //this method ensures that only the next step can be accessed
        setTabInteractivity();

        //this method is to handle the on change event handlers of the edit texts
        setUserComponentErrorInteractivity();

        tbDonorReg.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 3) {
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
            Intent intent = new Intent(RegActivity3.this, RegActivity4.class);
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
            tbDonorReg.getTabAt(2).select();
        }
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
                tbDonorReg.getTabAt(2).select();
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
                if (phoneNum.length() != 0) {
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
                tbDonorReg.getTabAt(2).select();

                if (s.length() == 0) {
                    txtPhoneNumber.setError(getText(R.string.txt_empty_field));
                }

            }
        });
    }

    private void initViews() {
        tbDonorReg = findViewById(R.id.tbDonorReg3);
        txtEmail = findViewById(R.id.txtDonorEmail);
        txtPhoneNumber = findViewById(R.id.txtDonorPhoneNumber);
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
                return false;
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
            } else {
                //ensuring user is connected to the internet
                GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
                if (!globalConnectivityCheck.isNetworkConnected()) {
                    //if internet is not connected
                    Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    //checking that email exists using Mailbox Validator
                    if (!checkEmailExists()) {
                        txtEmail.setError(getText(R.string.txt_email_exists));
                        blnValid = false;
                    }
                }
            }
        }
        if (strPhoneNumber.length() == 0) {
            txtPhoneNumber.setError(getText(R.string.txt_empty_field));
            blnValid = false;
        } else {
            //checking if it is only numbers
            if ((strPhoneNumber.length() < 10) || (!Pattern.matches("^[0-9]*$", strPhoneNumber))) {
                txtPhoneNumber.setError(getText(R.string.txt_invalid_phone_number));
                blnValid = false;
            }

        }

        return blnValid;
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

    private boolean checkEmailExists() {
        //connect to mailbox validator
        blnExist = false;
        client = new OkHttpClient();
        String url = urlLink + "emailvalidator.php";

        RequestBody formBody = new FormBody.Builder()
                .add("email", strEmail)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                setblnExist(false);
                countDownLatch.countDown();

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        //extract is verified from epi response object
                        JSONObject JObj = new JSONObject(responseData);
                        String objEmailVerified = JObj.getString("is_verified");
                        if (objEmailVerified.equals("True")) {
                            setblnExist(true);
                        } else {
                            setblnExist(false);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                countDownLatch.countDown();
            }
        });

        try {
            //to ensure that main thread waits for this
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return blnExist;
    }

    private void setblnExist(boolean blnChange) {
        //method to change bln exist value due to it being changed in another method
        blnExist = blnChange;
    }

    private void extractInput() {
        strPhoneNumber = txtPhoneNumber.getEditText().getText().toString().trim();
        strEmail = txtEmail.getEditText().getText().toString().trim();
    }


    private void setTabIcons() {
        tbDonorReg.getTabAt(0).setIcon(R.drawable.ic_progress_complete_sel);
        tbDonorReg.getTabAt(1).setIcon(R.drawable.ic_progress_complete_sel);
    }
}
