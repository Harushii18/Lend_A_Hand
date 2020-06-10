package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
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
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegActivity1Donee extends AppCompatActivity {
    private TabLayout tbDoneeReg;
    private TextInputLayout txtPassword, txtPassword2, txtUsername;
    private String strUsername, strPassword, strPassword2;
    private boolean invalidUsername = false;
    private OkHttpClient client;
    private String urlLink = "https://lamp.ms.wits.ac.za/home/s2089676/";
    boolean usernameExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide title bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reg1_donee);

        initViews();

        //select current tab
        tbDoneeReg.getTabAt(0).select();

        //this method ensures that only the next step can be accessed
        setTabInteractivity();

        //this method is to handle the on change event handlers of the edit texts
        setUserComponentErrorInteractivity();


        tbDoneeReg.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {

                    if (validateInput()) {
                        //if it is valid
                        //TODO: store hashed password in class
                        Intent intent = new Intent(RegActivity1Donee.this, RegActivity2Donee.class);
                        //pass password and username
                        intent.putExtra("username", strUsername);
                        intent.putExtra("password", strPassword);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        tbDoneeReg.getTabAt(0).select();
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
        txtUsername.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String username = txtUsername.getEditText().getText().toString();
                if (username.length() != 0) {
                    if (username.contains(" ")) {
                        //if statement to prevent the error from reloading every time
                        if (invalidUsername == false) {
                            txtUsername.setError(getText(R.string.txt_invalid_username));
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
                tbDoneeReg.getTabAt(0).select();
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
                tbDoneeReg.getTabAt(0).select();
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
                    tbDoneeReg.getTabAt(0).select();
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
                txtUsername.setError(getText(R.string.txt_invalid_username));
                blnValid = false;
            } else {
                urlLink = urlLink + "usercheck.php";

                RequestBody formBody=new FormBody.Builder()
                        .add("username",strUsername)
                        .build();

                Request request = new Request.Builder()
                        .url(urlLink)
                        .post(formBody)
                        .build();
                final CountDownLatch countDownLatch=new CountDownLatch(1);
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
        tbDoneeReg = findViewById(R.id.tbDoneeReg1);
        txtPassword = findViewById(R.id.txtDoneePassword1);
        txtUsername = findViewById(R.id.txtDoneeUsername);
        txtPassword2 = findViewById(R.id.txtDoneePassword2);
        client = new OkHttpClient();
    }
    private void setUsernameExists(boolean blnValue){
        usernameExists=blnValue;

    }
    private void setTabInteractivity() {
        LinearLayout tabStrip2 = ((LinearLayout) tbDoneeReg.getChildAt(0));
        tabStrip2.getChildAt(1).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
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
                return true;
            }
        });
    }

}
