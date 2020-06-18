package com.example.lendahand;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DonorRankingList extends AppCompatActivity {

    TextView TxtName;
    TextView TxtSum;
    LinearLayout donor_layout;
    ProgressBar pb;
    CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_ranking_list);

        pb= findViewById(R.id.progressBar);
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
                String url = "https://lamp.ms.wits.ac.za/home/s2089676/donor_ranking.php";

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

                        DonorRankingList.this.runOnUiThread(new Runnable() {
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



        donor_layout= findViewById(R.id.donor_list);





    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void processJSON(String json) {
        try {

            JSONArray all = new JSONArray(json);
            for (int i = 0; i < all.length(); i++) {
                JSONObject item = all.getJSONObject(i);
                String name = item.getString("NAME");
                String surname = item.getString("SURNAME");
                String sum= item.getString("SUM");


               RelativeLayout layout= new RelativeLayout(this);


                /*TxtName= new TextView(this);
                TxtName.setText(name+" "+surname);
                TxtName.setTextSize(15);
                TxtName.setTextColor(Color.parseColor("#000000"));
                ConstraintLayout.LayoutParams lp= new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
                //lp.leftMargin=30;
                //lp.topMargin=10;
                TxtName.setLayoutParams(lp);
                layout.addView(TxtName);





                //Sum
                //TxtSum= new TextView(this);
                TxtSum.setText(sum);
                TxtSum.setTextSize(15);
                TxtSum.setTextColor(Color.parseColor("#000000"));
                //ConstraintLayout.LayoutParams lip= new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
                //lip.setMarginStart(350);
                //lip.topMargin=10;
                //TxtSum.setLayoutParams(lip);
                layout.addView(TxtSum);
                 /*ConstraintSet constraintSet= new ConstraintSet();
                constraintSet.clone(layout);
                //constraintSet.connect(TxtName.getId(),ConstraintSet.LEFT,TxtSum.getId(),ConstraintSet.LEFT,10);
                constraintSet.connect(TxtSum.getId(),ConstraintSet.RIGHT,layout.getId(),constraintSet.RIGHT,0);
                constraintSet.applyTo(layout);*/

                View view= getLayoutInflater().inflate(R.layout.list,null);
                TxtSum= view.findViewById(R.id.Sum);
                TxtSum.setText(sum);
                TxtName= view.findViewById(R.id.Name);
                TxtName.setText(name+" "+surname);
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

            }

           pb.setVisibility(View.GONE);
        } catch(JSONException e){
            e.printStackTrace();
        }


    }
}