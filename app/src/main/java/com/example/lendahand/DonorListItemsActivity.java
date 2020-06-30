package com.example.lendahand;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class DonorListItemsActivity extends AppCompatActivity implements View.OnClickListener{
    EditText qty;
    LinearLayout layout;

    int found= 0;

    int[] tempID= new int[100];
    int[] ID;


    String[] Item;
    String[] tempItem= new String[100];

    String[] Qty;
    String[] tempQty= new String[100];

    int [] RequiredArray;
    int [] tempRequiredArray= new int[100];

    Button btnAdd;

    int enteredqty=0;
    int required;


    int index=1000;
    int limit=0;
    int num=0;

    ProgressBar pb;
    CountDownTimer countDownTimer;

    TextView txtitem_name;
    TextView txtrequired;

    String type;
    String donorProvince;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_list_items);

        type= getIntent().getStringExtra("type"); //get Category type
        donorProvince= StayLoggedIn.getProvince(DonorListItemsActivity.this); //Set to the province of the user





        toolbar=findViewById(R.id.toolbar_ListItems);
        //Make back button on toolbar clickable
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(DonorListItemsActivity.this, DonorCategoryListActivity.class);
                startActivity(i);
                finish();
            }
        });



        pb= findViewById(R.id.progressBar2);
        pb.setProgress(0);
        pb.setSecondaryProgress(0);



        layout = findViewById(R.id.linear_layout);


        ID= DonorDashboardActivity.IDArray.ID;
        Item = DonorDashboardActivity.ItemArray.Item;
        Qty = DonorDashboardActivity.QtyArray.Qty;
        RequiredArray= DonorDashboardActivity.RequiredArray.RequiredArr;

        for(int i=0; i<100;i++){
            if(ID[i]==0){

                index=i-1;
                num=i;
                break;
            }
            else{
                limit=limit+Integer.parseInt(Qty[i]);
            }
        }

        if(index==1000){
            index=99;
        }



        btnAdd= findViewById(R.id.btnAddCart);
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

                pb.setVisibility(View.VISIBLE);
                /*-------------------------------------------------------OkHttp--------------------------------------*/
                //check connectivity
                GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
                if (!globalConnectivityCheck.isNetworkConnected()) {
                    //if internet is not connected
                    Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    //get requests from donee requesting from server
                    performRequest();
                }


            }
        }.start();

    }

    public void performRequest(){
        OkHttpClient client = new OkHttpClient();

        String url ="https://lamp.ms.wits.ac.za/home/s2089676/provinceitems.php";

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

                DonorListItemsActivity.this.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        processJSON(responseData);
                    }
                });
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void processJSON(String json) {
        try {

            JSONArray all = new JSONArray(json);
            for (int i = 0; i < all.length(); i++) {
                JSONObject item = all.getJSONObject(i);

                String item_required= item.getString("QUANTITY");
                int itemrequired= Integer.parseInt(item_required);
                String item_province= item.getString("PROVINCE");
                String item_type= item.getString("ITEM_TYPE");

                String itemid = item.getString("ITEM_ID");
                int item_id=Integer.parseInt(itemid);
                String item_name = item.getString("ITEM_NAME");

                int r=0;

                if(item_type.equals(type) && item_province.equals(donorProvince)&&itemrequired!=0) { //check if items are requested in province

                    found = found + 1;
                    qty = new EditText(this);
                    txtrequired= new TextView(this);
                    txtitem_name = new TextView(this);
                    RelativeLayout rl = new RelativeLayout(this);
                    View view = getLayoutInflater().inflate(R.layout.donor_items_list, null);
                    qty = view.findViewById(R.id.qty);
                    txtrequired=view.findViewById(R.id.required);
                    txtrequired.setText("Required: "+item_required);
                    txtitem_name = view.findViewById(R.id.item_name);
                    txtitem_name.setText(item_name);
                    rl.addView(view);

                    EditTextMethod(qty, item_name, item_id,item_required);//get stuff from EditText


                    GradientDrawable border = new GradientDrawable();
                    border.setColor(0xFFFFFFFF);
                    border.setStroke(1, 0xFFC0C0C0);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        rl.setBackgroundDrawable(border);
                    } else {
                        rl.setBackground(border);
                    }


                    layout.addView(rl);
                }


                //}


            }
            if(found==0){
                Intent i = new Intent(DonorListItemsActivity.this, NoItemsActivity.class); //No items activity
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
            else{
                pb.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void EditTextMethod(final EditText qty, final String item_name, final int item_id, final String item_required) {

        qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {



            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {






            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().length()>0) {

                    enteredqty = Integer.parseInt(s.toString());
                    required = Integer.parseInt(item_required);
                    limit = limit + Integer.parseInt(s.toString());


                    for (int k = 0; k < 100; k++) {

                        if (tempID[k] != 0 && tempID[k] == item_id && s.toString().length() > 0) { //Check for duplicates
                            limit = limit - Integer.parseInt(tempQty[k]);
                            if (limit > 100) {
                                qty.setEnabled(false);
                                qty.setFocusable(false);
                                qty.setFocusableInTouchMode(false);
                                Toast toast = Toast.makeText(DonorListItemsActivity.this, "Sorry. \nYou are only permitted to donate 100 items", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                                qty.setEnabled(true);
                                qty.setFocusable(true);
                                qty.setFocusableInTouchMode(true);
                                int len= qty.getText().length();
                                qty.getText().delete(0, len);
                                tempQty[k] = "0";
                                for (int j = k; j < 100; j++) {//delete null
                                    if (j != 99) {
                                        if (tempID[j + 1] != 0) {
                                            tempID[j] = tempID[j + 1];
                                            tempItem[j] = tempItem[j + 1];
                                            tempQty[j] = tempQty[j + 1];
                                            tempRequiredArray[j] = tempRequiredArray[j + 1];

                                            tempRequiredArray[j + 1] = 0;
                                            tempID[j + 1] = 0;
                                            tempItem[j + 1] = "0";
                                            tempQty[j + 1] = "0";

                                        } else {
                                            tempRequiredArray[j] = 0;
                                            tempID[j] = 0;
                                            tempItem[j] = "0";
                                            tempQty[j] = "0";
                                        }
                                    }
                                }

                                break;
                            } else if (enteredqty <= required) {
                                tempQty[k] = s.toString();
                                break;
                            } //check if its not bigger than required
                            else if (enteredqty > required ) {
                                qty.setEnabled(false);
                                qty.setFocusable(false);
                                qty.setFocusableInTouchMode(false);
                                Toast toast = Toast.makeText(DonorListItemsActivity.this, "Sorry. \nYou are only permitted to donate the number of required items", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                qty.setText(item_required);
                                qty.setEnabled(true);
                                qty.setFocusable(true);
                                qty.setFocusableInTouchMode(true);

                                tempQty[k] = item_required;

                                break;
                            } else if (s.toString().length() < 0 || s.toString().length() == 0) { //s.toString().length()<0
                                for (int j = 0; j < 100; j++) {
                                    if (j != 99) {
                                        if (tempID[j + 1] != 0) {
                                            tempID[j] = tempID[j + 1];
                                            tempItem[j] = tempItem[j + 1];
                                            tempQty[j] = tempQty[j + 1];
                                            tempID[j + 1] = 0;
                                            tempItem[j + 1] = "0";
                                            tempQty[j + 1] = "0";

                                        } else {
                                            tempID[j] = 0;
                                            tempItem[j] = "0";
                                            tempQty[j] = "0";
                                        }
                                    }
                                }

                                break;

                            }

                        } else if (tempID[k] == 0 && s.toString().length() > 0) { // no duplicates found
                            if (limit > 100) {
                                qty.setEnabled(false);
                                qty.setFocusable(false);
                                qty.setFocusableInTouchMode(false);
                                Toast toast = Toast.makeText(DonorListItemsActivity.this, "Sorry. \nYou are only permitted to donate 100 items", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                                qty.setEnabled(true);
                                qty.setFocusable(true);
                                qty.setFocusableInTouchMode(true);
                                int len= qty.getText().length();
                                qty.getText().delete(0, len);


                                break;
                            }
                            else if (enteredqty > required) {
                                qty.setEnabled(false);
                                qty.setFocusable(false);
                                qty.setFocusableInTouchMode(false);
                                Toast toast = Toast.makeText(DonorListItemsActivity.this, "Sorry. \nYou are permitted to donate the number of required items", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                qty.setText(item_required);
                                qty.setEnabled(true);
                                qty.setFocusable(true);
                                qty.setFocusableInTouchMode(true);

                                tempID[k] = item_id;
                                tempItem[k] = item_name;
                                tempQty[k] = item_required;
                                tempRequiredArray[k] = Integer.parseInt(item_required);
                                break;

                            }
                            else {
                                tempID[k] = item_id;
                                tempItem[k] = item_name;
                                tempQty[k] = s.toString();
                                tempRequiredArray[k] = Integer.parseInt(item_required);
                                break;
                            }


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
            case R.id.btnAddCart:
                int temp=0;
                if(tempID[0]==0){
                    Toast.makeText(DonorListItemsActivity.this, "Enter quantity for required items.", Toast.LENGTH_SHORT).show();

                }
                else{
                    for(int j=num; j<100; j++){
                        if(tempID[temp]!=0){
                            ID[j]=tempID[temp];
                            Item[j]=tempItem[temp];
                            Qty[j]=tempQty[temp];
                            RequiredArray[j]= tempRequiredArray[temp];
                            temp=temp+1;
                        }
                        else {
                            temp=temp+1;
                        }
                    }
                    Toast.makeText(DonorListItemsActivity.this, "Successfully added to cart.", Toast.LENGTH_SHORT).show();
                    i = new Intent(this, DonorCategoryListActivity.class);
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
        Intent i = new Intent(DonorListItemsActivity.this, DonorCategoryListActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();

    }


}
