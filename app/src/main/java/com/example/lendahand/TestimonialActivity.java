package com.example.lendahand;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
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

        pb = findViewById(R.id.progressBarT);
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
        layout = findViewById(R.id.ll);
    }

    public void processJSON(String json) {
        try {
            //Remove progress Bar
            pb.setVisibility(View.GONE);
            JSONArray all = new JSONArray(json);

            for (int i = 0; i < all.length(); i++) {
                JSONObject item = all.getJSONObject(i);
                String name = item.getString("NAME");
                String surname = item.getString("SURNAME");
                String testimonial = item.getString("TESTIMONIAL");

                TableRow.LayoutParams params=new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,1.0f);
                TextView tv=new TextView(this);
                tv.setPadding(20,0,20,0);
                tv.setTextSize(24);
                tv.setTextColor(getResources().getColor(R.color.primaryTextColor));
                tv.setText(name+" "+surname+"\n");
                this.layout.addView(tv);

                TextView tv2=new TextView(this);
                tv2.setPadding(20,0,20,0);
                tv2.setTextColor(getResources().getColor(R.color.primaryTextColor));
                tv2.setText(testimonial+"\n\n");
                this.layout.addView(tv2);
            }

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

