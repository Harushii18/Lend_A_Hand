package com.example.lendahand;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestimonialActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView TxtName;
    TextView TxtTest;
    LinearLayout layout;
    ProgressBar pb;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testimonial);
        layout = findViewById(R.id.layoutTest);

        //Make button on toolbar clickable
        toolbar = findViewById(R.id.toolbar_Testimonial);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    Intent i = new Intent(v.getContext(), DoneeDashboard.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();

            }
        });

        pb = findViewById(R.id.progressBar3);
        pb.setProgress(0);
        pb.setSecondaryProgress(0);


        countDownTimer = new CountDownTimer(3000, 100) { //Timer for progress Bar
            @Override
            public void onTick(long millisUntilFinished) {
                int progress = pb.getProgress() + 2;
                if (progress > pb.getMax()) progress = 0;
                pb.setProgress(progress);

            }

            @Override
            public void onFinish() {
                //check connectivity
                GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
                if (!globalConnectivityCheck.isNetworkConnected()) {
                    //if internet is not connected
                    Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    pb.setVisibility(View.VISIBLE);
                    //get total donors by sending request to server
                    performRequest();
                }
            }
        }.start();
    }

    public void processJSON(String json) {
        try {
            JSONArray all = new JSONArray(json);
            for (int i = 0; i < all.length(); i++) {
                JSONObject item = all.getJSONObject(i);
                String name = item.getString("NAME");
                String surname = item.getString("SURNAME");
                String testimonial= item.getString("TESTIMONIAL");



                ConstraintLayout clayout = new ConstraintLayout(this);

                View view = getLayoutInflater().inflate(R.layout.testimonial, null);
                TxtName = view.findViewById(R.id.txtTestName);
                TxtName.setText(name+" "+surname);
                TxtTest = view.findViewById(R.id.txtTestimonial);
                TxtTest.setText(testimonial);


                clayout.addView(view);

                layout.addView(clayout);


            }
            pb.setVisibility(View.GONE); //Remove progress Bar
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void performRequest() {

        OkHttpClient client = new OkHttpClient();
        String url = "https://lamp.ms.wits.ac.za/home/s2089676/testimonials.php";

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                final String responseData = response.body().string();

                TestimonialActivity.this.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void run() {
                        processJSON(responseData);
                    }
                });
            }
        });


    }
}

