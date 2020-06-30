package com.example.lendahand;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegActivity5 extends AppCompatActivity {
    private TabLayout tbDoneeReg;
    private TextInputLayout txtMotivationalLetter;
    private String strMotivationalLetter;
    private String urlLink = "https://lamp.ms.wits.ac.za/home/s2089676/";
    private OkHttpClient client;

    //these variables are for checking if user swipes
    private float x1,x2;
    static final int MIN_DISTANCE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide title bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reg5);

        tbDoneeReg = findViewById(R.id.tbDoneeReg5);
        txtMotivationalLetter = findViewById(R.id.txtDoneeMotLetter);

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
                //check connectivity
                GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
                if (!globalConnectivityCheck.isNetworkConnected()) {
                    //if internet is not connected
                    Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    if (tab.getPosition() == 5) {
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
        //check connectivity
        GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
        if (!globalConnectivityCheck.isNetworkConnected()) {
            //if internet is not connected
            Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            if (validateInput()) {
                Intent intent = new Intent(RegActivity5.this, RegActivityFinalDonee.class);
                //get from previous activity
                Bundle bundle = getIntent().getExtras();
                String strPassword = bundle.getString("password");
                final String strUsername = bundle.getString("username");
                String strFName = bundle.getString("fname");
                String strLName = bundle.getString("lname");
                final String strEmail = bundle.getString("email");
                String strPhoneNumber = bundle.getString("phoneNo");
                String strPostalCode = bundle.getString("postcode");
                String strStreetAddress = bundle.getString("streetadd");
                String strSuburb = bundle.getString("suburb");
                String strProvince = bundle.getString("prov");

                //ensure the values are of proper format
                assert strFName != null;
                strFName = capitalizeWord(strFName.toLowerCase());
                assert strLName != null;
                strLName = capitalizeWord(strLName.toLowerCase());
                assert strStreetAddress != null;
                strStreetAddress = capitalizeWord(strStreetAddress.toLowerCase());
                assert strSuburb != null;
                strSuburb = capitalizeWord(strSuburb.toLowerCase());


                client = new OkHttpClient();
                String link = urlLink + "doneepost.php";

                RequestBody formBody = new FormBody.Builder()
                        .add("username", strUsername)
                        .add("pass", strPassword)
                        .add("fname", strFName)
                        .add("surname", strLName)
                        .add("email", strEmail)
                        .add("phone", strPhoneNumber)
                        .add("street", strStreetAddress)
                        .add("sub", strSuburb)
                        .add("prov", strProvince)
                        .add("postcode", strPostalCode)
                        .add("utype", "donee")
                        .add("mot", strMotivationalLetter)
                        .add("stat", "Pending")
                        .build();

                Request request = new Request.Builder()
                        .url(link)
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
                            Log.d("INSERT","Inserting new donee to database successful");
                        }else{
                            Log.d("INSERT","Inserting new donee to database failed");
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
                //send email to user telling them that their account has been created
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            GMailSender sender = new GMailSender(getText(R.string.txt_developer_email).toString(),
                                    getText(R.string.txt_developer_pword).toString());
                            sender.sendMail(getText(R.string.txt_email_subject).toString(), getText(R.string.txt_email_body_common).toString()+strUsername+getText(R.string.txt_email_body_donee).toString(),
                                    getText(R.string.txt_developer_email).toString(), strEmail);
                        } catch (Exception e) {
                            Log.e("SendMail", e.getMessage(), e);
                        }
                    }

                }).start();

                //go to next activity
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                tbDoneeReg = findViewById(R.id.tbDoneeReg5);
            }
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
    private String capitalizeWord(String str) {
        String words[] = str.split("\\s");
        String capitalizeWord = "";
        for (String w : words) {
            String first = w.substring(0, 1);
            String afterfirst = w.substring(1);
            capitalizeWord += first.toUpperCase() + afterfirst + " ";
        }
        return capitalizeWord.trim();
    }

    private boolean validateInput() {
        boolean blnValid = true;
        strMotivationalLetter = txtMotivationalLetter.getEditText().getText().toString().trim();
        if (strMotivationalLetter.length() == 0) {
            txtMotivationalLetter.setError(getText(R.string.txt_empty_field));
            blnValid = false;
        }
        return blnValid;
    }

    private void setUserComponentErrorInteractivity() {
        txtMotivationalLetter.getEditText().addTextChangedListener(new TextWatcher() {
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
                tbDoneeReg.getTabAt(4).select();
                if (s.length() == 0) {
                    txtMotivationalLetter.setError(getText(R.string.txt_empty_field));
                } else {
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
