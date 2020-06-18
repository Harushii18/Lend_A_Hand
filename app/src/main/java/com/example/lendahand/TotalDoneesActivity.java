package com.example.lendahand;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    TextView TxtProvince;
    TextView TxtSum;
    LinearLayout donor_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_donees);
        donor_layout= findViewById(R.id.donee_list);

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


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void processJSON(String json) {
        try {
            JSONArray all = new JSONArray(json);
            for (int i = 0; i < all.length(); i++) {
                JSONObject item = all.getJSONObject(i);
                String province = item.getString("PROVINCE");
                String sum= item.getString("SUM");

                RelativeLayout layout= new RelativeLayout(this);

                //Name
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
                layout.addView(TxtSum,lip);

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
        } catch(JSONException e){
            e.printStackTrace();
        }


    }
}

