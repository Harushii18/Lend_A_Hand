package com.example.lendahand;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class LoginScreenActivity extends AppCompatActivity {
    private TextInputLayout txtPassword, txtUsername;
    private String strUsername, strPassword, strUserType;
    private Button btnLogin;
    private String urlLink = "https://lamp.ms.wits.ac.za/home/s2089676/";
    private OkHttpClient client;
    boolean blnValid=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //hide title bar
        getSupportActionBar().hide();

        //TODO: Shared preferences, stay logged in
        //ensuring that we stay logged in
        /*
        if(StayLoggedIn.getUserName(LoginScreenActivity.this).length() == 0)
        {
            // call Login Activity

        }
        else
        {
            // Stay at the current activity.
        }

*/
        setContentView(R.layout.activity_login_screen);
        //initialise views
        initViews();

    }


    public void LogIn(View view) {
        //extract input
        extractInput();
        //TODO: Validations and decryption

        if (validateUser()) {

            //Toast to display that they are logging in
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast,
                    (ViewGroup) findViewById(R.id.custom_toast_container));
            TextView txtToast = layout.findViewById(R.id.txtToast);
            txtToast.setText(getText(R.string.txt_toast_login));
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();

            //TODO: Change this to certain screen depending on if logged in user is donor/ donee

            final Intent intent = new Intent(this, MainActivity.class);

            //thread is used to make sure toast is just shown on login
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            thread.start();
        } else {
            //show toast on login fail
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_invalid_toast,
                    (ViewGroup) findViewById(R.id.custom_toast_container));
            final TextView txtToast2 = layout.findViewById(R.id.txtToast2);
            txtToast2.setText(getText(R.string.txt_toast_login_invalid));
            Toast toast2 = new Toast(getApplicationContext());
            toast2.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast2.setDuration(Toast.LENGTH_SHORT);
            toast2.setView(layout);
            btnLogin.setEnabled(false);
            toast2.show();

            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            };
            thread.start();
            txtPassword.getEditText().setText("");
            txtUsername.getEditText().setText("");
            btnLogin.setEnabled(true);
        }

    }

    private void extractInput() {
        strPassword = txtPassword.getEditText().getText().toString().trim();
        strUsername = txtUsername.getEditText().getText().toString().trim();
    }

    private void initViews() {
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);

    }

    public void SignIn(View view) {
        Intent intent = new Intent(this, SelectUserTypeActivity.class);
        startActivity(intent);
    }


    private boolean validateUser() {
        blnValid = false;
        if ((strUsername.length() == 0) || (strPassword.length() == 0)) {
            blnValid = false;
        } else {
            client = new OkHttpClient();
            String url= urlLink + "login.php";

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
                    setblnValid(false, "");
                    countDownLatch.countDown();
                    //TODO: Check internet connection here, show toast and close app
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        if (responseData.equals("")) {
                            setblnValid(false, "");
                        } else {
                            try {
                                JSONArray JArray = new JSONArray(responseData);
                                String objPassword, objType;

                                String generatedPassword = "";
                                try {
                                    String salt="A$thy*BJFK_P_$%#";
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

                                for (int i = 0; i < JArray.length(); i++) {
                                    JSONObject object = JArray.getJSONObject(i);
                                    objPassword = object.getString("PASSWORD");
                                    if (objPassword.equals(generatedPassword)) {
                                        objType = object.getString("USER_TYPE");
                                        setblnValid(true, objType);
                                    } else {
                                        setblnValid(false, "");
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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


        }
        return blnValid;
    }

    private void setblnValid(boolean blnChange, String usertype) {
        blnValid = blnChange;
        strUserType = usertype;

    }


}