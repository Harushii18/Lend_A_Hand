package com.example.lendahand;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

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

public class TotalDonorsActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView TxtProvince;
    TextView TxtSum;
    LinearLayout donor_layout;
    ProgressBar pb;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_donors);
        donor_layout= findViewById(R.id.donor_list);


        toolbar=findViewById(R.id.toolbar_Total_Donors);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(TotalDonorsActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });




        //pb=findViewById(R.id.progressBar);
        pb= findViewById(R.id.progressBar3);
        pb.setProgress(0);
        pb.setSecondaryProgress(0);
        //pb.setMax(10);
        //

        countDownTimer= new CountDownTimer(3000,100) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress = pb.getProgress()+2;
                if(progress>pb.getMax()) progress=0;
                pb.setProgress(progress);

            }

            @Override
            public void onFinish() {
                //Toast.makeText(DonorRankingList.this,"Done",Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.VISIBLE);

        OkHttpClient client = new OkHttpClient();
        String url = "https://lamp.ms.wits.ac.za/home/s2089676/donors.php";

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

                TotalDonorsActivity.this.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void run() {
                        processJSON(responseData);
                    }
                });
            }
        });
            }
        }.start();



    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void processJSON(String json) {
        try {
            JSONArray all = new JSONArray(json);
            for (int i = 0; i < all.length(); i++) {
                JSONObject item = all.getJSONObject(i);
                String province = item.getString("PROVINCE");
                String sum= item.getString("SUM");

                RelativeLayout layout= new RelativeLayout(this);

                /*Name
                TxtProvince= new TextView(this);
                TxtProvince.setText(province);
                TxtProvince.setTextColor(Color.parseColor("#000000"));
                LinearLayout.LayoutParams lp= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,100);
                lp.leftMargin=35;
                lp.topMargin=10;
                layout.addView(TxtProvince,lp);


                //Sum
                TxtSum= new TextView(this);
                TxtSum.setText(sum);
                TxtSum.setTextColor(Color.parseColor("#000000"));
                LinearLayout.LayoutParams lip= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,100);
                lip.setMarginStart(350);
                lip.topMargin=10;
                layout.addView(TxtSum,lip);*/
                View view= getLayoutInflater().inflate(R.layout.list,null);
                TxtSum= view.findViewById(R.id.Sum);
                TxtSum.setText(sum);
                TxtProvince= view.findViewById(R.id.Name);
                TxtProvince.setText(province);
                layout.addView(view);

                GradientDrawable border = new GradientDrawable();
                border.setColor(0xFFFFFFFF);
                border.setStroke(1,0xFFC0C0C0);
                if(Build.VERSION.SDK_INT<Build.VERSION_CODES.JELLY_BEAN){
                    layout.setBackgroundDrawable(border);
                }
                else{
                    layout.setBackground(border);
                }
                donor_layout.addView(layout);



            } pb.setVisibility(View.GONE);
        } catch(JSONException e){
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {

            super.onBackPressed();


    }

}

