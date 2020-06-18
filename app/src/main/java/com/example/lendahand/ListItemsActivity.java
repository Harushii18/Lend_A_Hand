package com.example.lendahand;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class ListItemsActivity extends AppCompatActivity implements View.OnClickListener {
    EditText qty;
    LinearLayout rightLayout;
    LinearLayout leftLayout;

    int[] tempID= new int[50];
    int[] ID;

    String[] Item;
    String[] tempItem= new String[50];

    String[] Qty;
    String[] tempQty= new String[50];

    Button btnAdd;


    int index=100;
    int num=0;

    ProgressBar pb;
    CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);
        pb= findViewById(R.id.progressBar2);
        pb.setProgress(0);
        pb.setSecondaryProgress(0);



        rightLayout = findViewById(R.id.RightLayout);
        leftLayout = findViewById(R.id.LeftLayout);

        ID= MainActivity.IDArray.ID;
        Item = MainActivity.ItemArray.Item;
        Qty = MainActivity.QtyArray.Qty;

        for(int i=0; i<50;i++){
            if(ID[i]==0){
                index=i-1;
                num=i;
                break;
            }
        }
        if(index==100){
            index=49;
        }



        btnAdd= findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);




        /* Getting stuff from remote database using OkHttp*/
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
        String url = getIntent().getStringExtra("url");

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

                ListItemsActivity.this.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void processJSON(String json) {
        try {
            JSONArray all = new JSONArray(json);
            for (int i = 0; i < all.length(); i++) {
                JSONObject item = all.getJSONObject(i);

                String itemid = item.getString("ITEM_ID");
                int item_id=Integer.parseInt(itemid);
                String item_name = item.getString("ITEM_NAME");

                Left left = new Left(this);/////

                ////////////////////////////////////////////////////////
                /*---------------------------------------------------------------editText for Quantity------------------------------------------------------*/

                LinearLayout right = new LinearLayout(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.WRAP_CONTENT);
                qty = new EditText(this);
                qty.setInputType(InputType.TYPE_CLASS_NUMBER);

                qty.setHint("0");
                qty.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#81d4fa")));
                qty.setHighlightColor(Color.parseColor("#81d4fa"));
                lp.weight = 0;
                lp.leftMargin = 15;
                lp.bottomMargin = 15;
                right.addView(qty, lp);
                EditTextMethod(qty, item_name,item_id);//get stuff from EditText
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                left.populate(item);


                leftLayout.addView(left);

                rightLayout.addView(right);

                /*----------------------------------Setting Colour Layouts----------------------------------------*/
                if (i % 2 == 0) {
                    right.setBackgroundColor(Color.parseColor("#e6f2ff"));
                    left.setBackgroundColor(Color.parseColor("#e6f2ff"));
                } else {
                    right.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    left.setBackgroundColor(Color.parseColor("#FFFFFF"));

                }


            }
            pb.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void EditTextMethod(final EditText qty, final String item_name, final int item_id) {
        qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                index=index+1;
                for (int k=0;k<50;k++){

                if(index>49){
                    Toast.makeText(ListItemsActivity.this, "Sorry, your cart is full.", Toast.LENGTH_SHORT).show();
                   qty.setHint("0");
                   break;
                }
                else {
                    if (tempID[k] != 0 && tempID[k] == item_id) {
                        index=index-1;
                        if(s.toString().length()>0) {
                            tempQty[k] = s.toString();
                            break;
                        }
                        else{
                            for(int j=k;j<50;j++){
                                if(j!=49){
                                    if(tempID[j+1]!=0){
                                        tempID[j]=tempID[j+1];
                                        tempItem[j]=tempItem[j+1];
                                        tempQty[j]=tempQty[j+1];
                                        tempID[j+1]=0;
                                        tempItem[j+1]="0";
                                        tempQty[j+1]="0";

                                    }
                                    else{
                                        tempID[j]=0;
                                        tempItem[j]="0";
                                        tempQty[j]="0";
                                    }
                                }
                            }

                            break;
                        }

                    }
                    else if (tempID[k] == 0) {
                        tempID[k] = item_id;
                        tempItem[k] = item_name;
                        tempQty[k] = s.toString();
                        break;

                    }
                }





                }

            }//end of textChanged
        });
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.btnAdd:
                int temp=0;
                if(tempID[0]==0){
                    Toast.makeText(ListItemsActivity.this, "Enter quantity for required items.", Toast.LENGTH_SHORT).show();
                }
                else{
                    for(int j=num; j<50; j++){
                        if(tempID[temp]!=0){
                            ID[j]=tempID[temp];
                            Item[j]=tempItem[temp];
                            Qty[j]=tempQty[temp];
                            temp=temp+1;
                        }
                        else {
                            temp=temp+1;
                        }
                    }
                Toast.makeText(ListItemsActivity.this, "Successfully added to cart.", Toast.LENGTH_SHORT).show();
                i = new Intent(this, CategoryListActivity.class);
                startActivity(i);
                break;
                }
            break;
            default:break;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ListItemsActivity.this, CategoryListActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }
}
