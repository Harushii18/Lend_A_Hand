package com.example.lendahand;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DoneeEditMotivationalLetterActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtMotLetter;
    private TextInputLayout txtEditMotLetter;
    private OkHttpClient client;
    private String urlLink = "https://lamp.ms.wits.ac.za/home/s2089676/";
    private AlertDialog alertDialog;
    private Button btnConfirmEditMotLetter,btnSelEditMotivationLetter;

    //TODO: Add nav bar to this activity for REJECTED DONEE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donee_edit_motivational_letter);

        //initialise views
        txtMotLetter = findViewById(R.id.txtEditMotivationalLetter);
        btnSelEditMotivationLetter=findViewById(R.id.btnEditMotivationalLetter);

        //initialise on click event handler for button
        btnSelEditMotivationLetter.setOnClickListener(this);

        //populate motivational letter textview with current motivational letter
         populateMotLetterTextView();

    }

    private void populateMotLetterTextView() {
        client = new OkHttpClient();
        String url = urlLink + "getmotivationalletter.php";

        RequestBody formBody = new FormBody.Builder()
                .add("username", StayLoggedIn.getUserName(DoneeEditMotivationalLetterActivity.this))
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
                    String responseData = response.body().string();
                    if (responseData.equals("")) {
                    } else {

                        JSONArray JArray;
                        try {
                            JArray = new JSONArray(responseData);
                            //get the motivational from the server
                            for (int i = 0; i < JArray.length(); i++) {
                                JSONObject object = JArray.getJSONObject(i);
                                //populate motivational letter text view
                                txtMotLetter.setText(object.getString("MOTIVATION_LETTER"));
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

    @Override
    public void onClick(View v) {
        //handles all on click events for buttons
        switch (v.getId()) {
            case R.id.btnEditMotivationalLetter:
                selEditMotivationalLetter(v);
                break;
            case R.id.btnConfirmEditMotivationalLetter:
                EditMotivationalLetter();
                break;
            default:
                break;
        }
    }

    private void EditMotivationalLetter() {
        boolean blnValid = true;
        String strMotivationalLetter = txtEditMotLetter.getEditText().getText().toString().trim();
        if (strMotivationalLetter.length() == 0) {
            txtEditMotLetter.setError(getText(R.string.txt_empty_field));
            blnValid = false;
        }
        if (blnValid) {
            //check connectivity
            GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
            if (!globalConnectivityCheck.isNetworkConnected()) {
                //if internet is not connected
                Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
                toast.show();
            } else {
                //post motivational letter to database
            client = new OkHttpClient();
            String link = urlLink + "editmotivationalletter.php";
            //change motivational letter on okhttp server
            RequestBody formBody = new FormBody.Builder()
                    .add("username", StayLoggedIn.getUserName(DoneeEditMotivationalLetterActivity.this))
                    .add("motletter", strMotivationalLetter)
                    .add("status", "Pending")
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
                        Log.d("EDIT","Editing motivational letter successful");
                    }else{
                        Log.d("EDIT","Editing motivational letter failed");
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

            //change their status in shared preferences
            StayLoggedIn.setDoneeStatus(DoneeEditMotivationalLetterActivity.this,"Pending");

                alertDialog.dismiss();
            }
        } else {
            //set component interactivity
            txtEditMotLetter.getEditText().addTextChangedListener(new TextWatcher() {
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
                    if (s.length() == 0) {
                        txtEditMotLetter.setError(getText(R.string.txt_empty_field));
                    } else {
                        txtEditMotLetter.setError(null);
                        txtEditMotLetter.setErrorEnabled(false);
                    }
                }
            });

        }


    }

    private void selEditMotivationalLetter(View v) {
        //move to next dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(DoneeEditMotivationalLetterActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_motivational_letter, viewGroup, false);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.show();

        //initialise buttons on dialog
        btnConfirmEditMotLetter = dialogView.findViewById(R.id.btnConfirmEditMotivationalLetter);

        //initialise text views
        txtEditMotLetter = dialogView.findViewById(R.id.txtDoneeEditMotLetter);

        //on click listeners for dialog button
        btnConfirmEditMotLetter.setOnClickListener(this);
    }
}
