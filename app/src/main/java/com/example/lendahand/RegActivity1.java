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

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegActivity1 extends AppCompatActivity {
    private TabLayout tbDonorReg;
    private TextInputLayout txtPassword, txtPassword2, txtUsername;
    private String strUsername, strPassword, strPassword2;
    private boolean invalidUsername = false;
    private OkHttpClient client;
    private String urlLink = "https://lamp.ms.wits.ac.za/home/s2089676/";
    boolean usernameExists = false;

    //these variables are for checking if user swipes
    private float x1, x2;
    static final int MIN_DISTANCE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide title bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reg1);

        initViews();

        //select current tab
        tbDonorReg.getTabAt(0).select();

        //this method ensures that only the next step can be accessed
        setTabInteractivity();

        //this method is to handle the on change event handlers of the edit texts
        setUserComponentErrorInteractivity();

        tbDonorReg.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //check connectivity
                GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
                if (!globalConnectivityCheck.isNetworkConnected()) {
                    //if internet is not connected
                    Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    if (tab.getPosition() == 1) {
                        goToNextActivity();
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

    private void goToNextActivity() {
        if (validateInput()) {
            //if it is valid
            //encrypt and hash password with SHA-512 to send through multiple intents
            String generatedPassword = "";
            try {
                String salt = "A$thy*BJFK_P_$%#";
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                md.update(salt.getBytes(StandardCharsets.UTF_8));
                byte[] hashedPassword = md.digest(strPassword.getBytes(StandardCharsets.UTF_8));
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < hashedPassword.length; i++) {
                    stringBuilder.append(Integer.toString((hashedPassword[i] & 0xff) + 0x100, 16).substring(1));
                }
                generatedPassword = stringBuilder.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(RegActivity1.this, RegActivity2.class);
            //pass password and username
            intent.putExtra("username", strUsername);
            intent.putExtra("password", generatedPassword);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            tbDonorReg.getTabAt(0).select();
        }

    }


    //to prevent swiping
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // Left to Right swipe action
                    if (x2 > x1) {
                        //do nothing
                    }

                    // Right to left swipe action
                    else {
                        goToNextActivity();
                    }

                } else {
                    // don't do anything
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void setUserComponentErrorInteractivity() {
        txtUsername.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String username = txtUsername.getEditText().getText().toString();
                if (username.length() != 0) {
                    if (username.contains(" ")) {
                        //if statement to prevent the error from reloading every time
                        if (invalidUsername == false) {
                            txtUsername.setError(getText(R.string.txt_username_no_spaces));
                            invalidUsername = true;
                        }
                    } else {
                        invalidUsername = false;
                        txtUsername.setError(null);
                        txtUsername.setErrorEnabled(false);
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
                tbDonorReg.getTabAt(0).select();
                if (s.length() == 0) {
                    txtUsername.setError(getText(R.string.txt_empty_field));
                }
            }
        });
        txtPassword.getEditText().addTextChangedListener(new TextWatcher() {
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
                tbDonorReg.getTabAt(0).select();
                if (s.length() == 0) {
                    txtPassword.setError(getText(R.string.txt_empty_field));
                } else {
                    txtPassword.setError(null);
                    txtPassword.setErrorEnabled(false);
                }
            }
        });
        txtPassword2.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String passwrd = txtPassword.getEditText().getText().toString().trim();
                txtPassword.getEditText().setText(passwrd);
                String passwrd2 = txtPassword2.getEditText().getText().toString();
                if (!passwrd2.equals(passwrd)) {
                    txtPassword2.setError(getText(R.string.txt_pword_no_match));
                } else {
                    //to sort out error where it randomly underlines next tab
                    tbDonorReg.getTabAt(0).select();
                    txtPassword2.setError(null);
                    txtPassword2.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });
    }

    private boolean validateInput() {
        boolean blnValid = true;
        //extract contents of textboxes
        extractInput();
        if (strUsername.length() == 0) {
            txtUsername.setError(getText(R.string.txt_empty_field));
            blnValid = false;
        } else {
            if (strUsername.contains(" ")) {
                txtUsername.setError(getText(R.string.txt_username_no_spaces));
                blnValid = false;
            } else {
                //check connectivity
                GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
                if (!globalConnectivityCheck.isNetworkConnected()) {
                    //if internet is not connected
                    Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    String url = urlLink + "usercheck.php";

                    RequestBody formBody = new FormBody.Builder()
                            .add("username", strUsername)
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
                            countDownLatch.countDown();
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            if (response.isSuccessful()) {

                                final String responseData = response.body().string();
                                if (responseData.contains("Matches")) {
                                    setUsernameExists(true);
                                } else {
                                    setUsernameExists(false);
                                }
                                countDownLatch.countDown();
                            }
                        }
                    });

                    try {
                        //to ensure that main thread waits for this
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (usernameExists) {
                        //checking if username already exists

                        txtUsername.setError(getText(R.string.txt_username_exists));
                        blnValid = false;

                    }
                }
            }

        }
        if (!strPassword.equals(strPassword2)) {
            txtPassword2.setError(getText(R.string.txt_pword_no_match));
            blnValid = false;
        }
        if (strPassword.length() == 0) {
            txtPassword.setError(getText(R.string.txt_empty_field));
            blnValid = false;
        }

        return blnValid;
    }

    private void extractInput() {
        strPassword = txtPassword.getEditText().getText().toString().trim();
        strUsername = txtUsername.getEditText().getText().toString().trim();
        strPassword2 = txtPassword2.getEditText().getText().toString().trim();
    }

    private void initViews() {
        tbDonorReg = findViewById(R.id.tbDonorReg1);
        txtPassword = findViewById(R.id.txtDonorPassword1);
        txtUsername = findViewById(R.id.txtDonorUsername);
        txtPassword2 = findViewById(R.id.txtDonorPassword2);
        client = new OkHttpClient();
    }

    private void setUsernameExists(boolean blnValue) {
        usernameExists = blnValue;
    }

    private void setTabInteractivity() {
        LinearLayout tabStrip2 = ((LinearLayout) tbDonorReg.getChildAt(0));
        tabStrip2.getChildAt(1).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
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

}
