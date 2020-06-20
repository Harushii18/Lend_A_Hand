package com.example.lendahand;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class TotalDoneesActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView TxtProvince;
    TextView TxtSum;
    LinearLayout donor_layout;
    ProgressBar pb;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_donees);
        donor_layout = findViewById(R.id.donor_list);


        toolbar = findViewById(R.id.toolbar_Total_Donors);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TotalDoneesActivity.this, DoneeDashboard.class);
                startActivity(i);
                finish();
            }
        });

        pb = findViewById(R.id.progressBar3);
        pb.setProgress(0);
        pb.setSecondaryProgress(0);

        countDownTimer = new CountDownTimer(3000, 100) {
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
                    //get total donees from server
                    performRequest();
                }
            }
        }.start();


    }

    private void performRequest() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://lamp.ms.wits.ac.za/home/s2089676/donees.php";

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

                TotalDoneesActivity.this.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void run() {
                        processJSON(responseData);
                    }
                });
            }
        });
    }


    public void processJSON(String json) {
        try {
            JSONArray all = new JSONArray(json);
            for (int i = 0; i < all.length(); i++) {
                JSONObject item = all.getJSONObject(i);
                String province = item.getString("PROVINCE");
                String sum = item.getString("SUM");

                RelativeLayout layout = new RelativeLayout(this);

                View view = getLayoutInflater().inflate(R.layout.list, null);
                TxtSum = view.findViewById(R.id.Sum);
                TxtSum.setText(sum);
                TxtProvince = view.findViewById(R.id.Name);
                TxtProvince.setText(province);
                layout.addView(view);

                GradientDrawable border = new GradientDrawable();
                border.setColor(0xFFFFFFFF);
                border.setStroke(1, 0xFFC0C0C0);
                layout.setBackground(border);
                donor_layout.addView(layout);
            }
            pb.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}

