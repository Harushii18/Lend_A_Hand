package com.example.lendahand;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DonorCart extends AppCompatActivity {

    TextView item;
    TextView qty;
    Button add;
    Button minus;
    ImageView trash;
    int limit=0;
    int [] ID;
    String[] Item ;
    String[] Qty ;
    int [] Required;
    Button Checkout;
    private OkHttpClient client;

    String ConfirmedItems="";
    int r=0;

    String donor_province;

    String UpdateRequestUrl;
    String RequestUrl;
    String CourierUrl;


    Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_cart);

        //User details

        String usertype= StayLoggedIn.getUserType(DonorCart.this);
        final String donor_username= StayLoggedIn.getUserName(DonorCart.this);
        final String name= StayLoggedIn.getFName(DonorCart.this);
        final String surname = StayLoggedIn.getLName(DonorCart.this);
        final String donor_email= StayLoggedIn.getEmail(DonorCart.this);
        donor_province=  StayLoggedIn.getProvince(DonorCart.this);



        RequestUrl= "https://lamp.ms.wits.ac.za/home/s2089676/request.php";// returns donees and request details from furthest requested

        UpdateRequestUrl="https://lamp.ms.wits.ac.za/home/s2089676/updateRequest.php";// UPDATES COURIER NUM DELIVERIES, INSERTS INTO DELIVERY, UPDATES REQUEST QTY, UPDATE DONATION QTY

        CourierUrl= "https://lamp.ms.wits.ac.za/home/s2089676/couriers.php?PROVINCE=";
        CourierUrl=CourierUrl+donor_province; //TO GET A RANDOM COURIER IN THE COUNTRY

        final String url= "https://lamp.ms.wits.ac.za/home/s2089676/donorcheckout.php"; //INSERTS INTO DONATION, UPDATES DONOR NUM_ITEMS, ECHOS NEW GENERATED DONATION_ID


        //Using correct array


        ID= DonorDashboardActivity.IDArray.ID;
        Item = DonorDashboardActivity.ItemArray.Item;
        Qty = DonorDashboardActivity.QtyArray.Qty;
        Required= DonorDashboardActivity.RequiredArray.RequiredArr;

        //==============================//


        ////////Add stuff to database
        Checkout=findViewById(R.id.btnDonorCheckout);
        Checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check connectivity
                GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
                if (!globalConnectivityCheck.isNetworkConnected()) {
                    //if internet is not connected
                    Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    final Intent intent = new Intent(DonorCart.this, DonorDashboardActivity.class);
                    client = new OkHttpClient();
                    String link = url;
                    for (int k =0; k < 100; k++){

                        if(ID[k]!=0){
                            final int K=k;
                            ConfirmedItems=ConfirmedItems+ Item[k]+" x"+Qty[k]+'\n';
                            //Add to database
                            RequestBody formBody = new FormBody.Builder()
                                    .add("username", donor_username)
                                    .add("item", String.valueOf(ID[k]))
                                    .add("qty", Qty[k])
                                    .build();
                            Request request = new Request.Builder()
                                    .url(link)
                                    .post(formBody)
                                    .build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                                    final String responseData = response.body().string();
                                    GetCourierMethod(K,responseData);




                                }
                            });
                            ///=======================================================================





                        }


                        else{
                            break;
                        }
                    }

                    //send email to donor telling them the items they have donated
                    new Thread(new Runnable() {


                        @Override
                        public void run() {
                            try {
                                GMailSender sender = new GMailSender(getText(R.string.txt_developer_email).toString(),
                                        getText(R.string.txt_developer_pword).toString());
                                sender.sendMail(getText(R.string.txt_checkout_email_subject).toString(), getText(R.string.txt_email_body_common).toString()+name+" "+surname+getText(R.string.txt_email_donorcheckout_body)+
                                        ConfirmedItems,getText(R.string.txt_developer_email).toString(),donor_email);
                            } catch (Exception e) {
                                Log.e("SendMail", e.getMessage(), e);
                            }
                        }

                    }).start();


                    final AlertDialog.Builder builder = new AlertDialog.Builder(DonorCart.this, R.style.AlertDialogTheme);
                    builder.setCancelable(true);
                    builder.setTitle("Successfully checked out your items. ");
                    builder.setMessage("You will be emailed shortly with your item details.");


                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            startActivity(intent);
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                        }
                    });
                    builder.show();


                }
            }
        });





        LinearLayout items_layout= findViewById(R.id.items_layout);


        toolbar=findViewById(R.id.toolbar_DonorCart);

        //Make back button in toolbar clickable

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent i= new Intent(DonorCart.this, DonorCategoryListActivity.class);
                    startActivity(i);
                    finish();


            }
        });




        for (int i=0;i<100;i++){ //this loop removes replicas
            if(i!=99){
                for(int j=i+1;j<50;j++){
                    if(ID[i]!=0&&ID[j]!=0&&ID[i]==ID[j]  ){
                        ID[i]=ID[j];
                        Item[i]=Item[j];
                        Qty[i]=Qty[j];
                        ID[j]=0;
                        Item[j]="0";
                        Qty[j]="0";
                    }
                    else{
                        continue;
                    }
                }


            }
            if(ID[i]!=0&&Qty[i].length()>0){ //Displaying in Cart xml


                RelativeLayout right = new RelativeLayout(this);

                item= new TextView(this);
                qty= new TextView(this);
                add= new Button(this);
                minus= new Button(this);
                trash= new ImageView(this);

                r= Required[i];



                View view= getLayoutInflater().inflate(R.layout.cart2,null); //use cart2 xml layout
                qty= view.findViewById(R.id.CartQty);
                qty.setText(Qty[i]);
                limit=limit+Integer.parseInt(Qty[i]);


                item= view.findViewById(R.id.Cart_Item_Name);
                item.setText(Item[i]);

                add= view.findViewById(R.id.btnAddQty);
                minus= view.findViewById(R.id.btnSubtractQty);
                trash= view.findViewById(R.id.Cart_trash);

                right.addView(view);

                DeleteMethod(qty,i,right); //Method for trash button
                AddMethod(qty,i,r); //Method for increasing qty on cart xml
                MinusMethod(qty,i);////Method for decreasing qty on cart xml

                //grey lines in list
                GradientDrawable border = new GradientDrawable();
                border.setColor(0xFFFFFFFF);
                border.setStroke(1,0xFFC0C0C0);
                right.setBackground(border);
                items_layout.addView(right);
            }
            else{
                continue;
            }
        }

    }


    private void MinusMethod(final TextView qty, final int i) {
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limit=limit-1;
                int s= Integer.parseInt(Qty[i])-1;
                if(s==0){ //if qty is zero then delete it in array
                    qty.setText("0");
                    for(int j=i;j<50;j++){ //Deleting in array
                        if(j!=49){
                            if(ID[j+1]!=0){
                                ID[j]=ID[j+1];
                                Item[j]=Item[j+1];
                                Qty[j]=Qty[j+1];
                                ID[j+1]=0;
                                Item[j+1]="0";
                                Qty[j+1]="0";

                            }
                            else{
                                ID[j]=0;
                                Item[j]="0";
                                Qty[j]="0";
                            }
                        }
                    }

                    startActivity(getIntent());
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    if (Qty[0]=="0"){
                        Intent i= new Intent(DonorCart.this, EmptyCartActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        finish();

                    }

                }
                else{
                    qty.setText(String.valueOf(s));
                    Qty[i]= String.valueOf(s);
                }


            }
        });
    }

    private void AddMethod(final TextView qty,  final int i, final int r) {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int s= Integer.parseInt(Qty[i])+1;

                limit=limit+1;
                if(s>r){
                    Toast.makeText(DonorCart.this, "Sorry, you cannot donate more than what is required. ", Toast.LENGTH_LONG).show();
                    limit=limit-1;
                }
                else if(limit<=100) {
                    qty.setText(String.valueOf(s));
                    Qty[i] = String.valueOf(s);
                }
                else if(limit>100){
                    Toast.makeText(DonorCart.this, "Sorry, your cart is full.", Toast.LENGTH_LONG).show();
                    limit=limit-1;
                }

            }
        });
    }

    private void DeleteMethod(final TextView qty, final int i, final RelativeLayout right) {
        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qty.setText("0");
                Toast.makeText(DonorCart.this, "Item deleted.", Toast.LENGTH_SHORT).show();
                right.setBackgroundColor(Color.parseColor("#D3D3D3")); //Make list item grey

                for(int j=i;j<50;j++){ //Delete in array
                    if(j!=49){
                        if(ID[j+1]!=0){
                            ID[j]=ID[j+1];
                            Item[j]=Item[j+1];
                            Qty[j]=Qty[j+1];
                            Required[j]= Required[j+1];

                            ID[j+1]=0;
                            Item[j+1]="0";
                            Qty[j+1]="0";
                            Required[j+1]=0;

                        }
                        else{
                            ID[j]=0;
                            Item[j]="0";
                            Qty[j]="0";
                            Required[j]=0;
                        }
                    }
                }

                startActivity(getIntent());
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                if (Qty[0]=="0"){ //Check if cart empty
                    Intent i= new Intent(DonorCart.this, EmptyCartActivity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                }
            }
        });

    }
    @Override
    public void onBackPressed() {

            Intent i = new Intent(DonorCart.this, DonorCategoryListActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();



    }

    public void GetCourierMethod(final int index, final String DonationID){
        OkHttpClient RequestClient = new OkHttpClient();

        Request RequestRequest = new Request.Builder()
                .url(CourierUrl)
                .build();

        RequestClient.newCall(RequestRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                final String responseData = response.body().string();

                DonorCart.this.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void run() {
                        ResponseCourier(index, DonationID,responseData);
                    }
                });
            }
        });

    }
    public void ResponseCourier(final int index,final String DonationID, String json){
        try {
            JSONArray all = new JSONArray(json);

            //////////////////////////Get random courier////////////////////////////////
            int courierArrayLen=all.length();

            Random randy= new Random();
            int random= randy.nextInt(courierArrayLen);
            JSONObject item = all.getJSONObject(random);

            String CourierID = item.getString("ID");
            String CourierName =item.getString("NAME");
            String CourierSurname= item.getString("SURNAME");
            String CourierPhoneNo= item.getString("PHONE_NO");



            GetRequestsMethod(index,DonationID,CourierID, CourierName,CourierSurname,CourierPhoneNo);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void GetRequestsMethod(final int index, final String DonationID, final String CourierID,  final String CourierName,  final String CourierSurname, final String CourierPhoneNo ){
        OkHttpClient RequestClient = new OkHttpClient();

        Request RequestRequest = new Request.Builder()
                .url(RequestUrl)
                .build();

        RequestClient.newCall(RequestRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                final String responseData = response.body().string();

                DonorCart.this.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void run() {
                        finalMethod(index,responseData,DonationID,CourierID,CourierName,CourierSurname,CourierPhoneNo);
                    }
                });
            }
        });

    }

    public void finalMethod(int index, String json, final String DonationID, String CourierID, final String CourierName, final String CourierSurname, final String CourierPhoneNo){
        //TODO: DELETE STUFF FROM DONOR CART
       try {
            JSONArray all = new JSONArray(json);
            for (int i = 0; i < all.length(); i++) {
                JSONObject item = all.getJSONObject(i);
                final String name =item.getString("NAME");
                final String surname= item.getString("SURNAME");
                String username = item.getString("USERNAME");
                String item_id = item.getString("ITEM_ID");
                String quantity= item.getString("QUANTITY");
                String province= item.getString("PROVINCE");
                final String email= item.getString("EMAIL");

                //Get date for processed delivery.

                SimpleDateFormat currentDate=new SimpleDateFormat("yyyy-MM-dd");
                Date todayDate= new Date();
                final String date_processed= currentDate.format(todayDate);

                Calendar c= Calendar.getInstance();
                c.setTime(todayDate);
                c.add(Calendar.DATE,2);
                final String date_received= currentDate.format(c.getTime());
                ///////


                int qty= Integer.parseInt(quantity);
                int DonorQty= Integer.parseInt(Qty[index]);

                String DonorItemId= String.valueOf(ID[index]);


                if(province.equals(donor_province)&& qty!=0 && item_id.equals(DonorItemId) && DonorQty!=0){


                    while(DonorQty!=0){
                        if(qty!=0 && DonorQty!=0){
                            DonorQty=DonorQty-1;
                            qty=qty-1;
                        }
                        else if((qty==0 && DonorQty!=0)){
                            Qty[index]=String.valueOf(DonorQty);
                            //Updating using php

                            OkHttpClient client = new OkHttpClient();
                            String link = UpdateRequestUrl;

                            RequestBody formBody = new FormBody.Builder()
                                    .add("donation_id",DonationID)
                                    .add("courier_id",CourierID)
                                    .add("donee_username", username)
                                    .add("date_processed",date_processed)
                                    .add("date_received",date_received)
                                    .add("item",item_id)
                                    .add("qty", String.valueOf(qty))
                                    .build();
                            Request request = new Request.Builder()
                                    .url(link)
                                    .post(formBody)
                                    .build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    if (response.isSuccessful()) {
                                        Log.d("INSERT","Updating REQUEST table in database successful");
                                    }else{
                                        Log.d("INSERT","Updating REQUEST table in database failed");
                                    }

                                }
                            });
                            //SEND EMAIL
                            new Thread(new Runnable() {


                                @Override
                                public void run() {
                                    try {
                                        GMailSender sender = new GMailSender(getText(R.string.txt_developer_email).toString(),
                                                getText(R.string.txt_developer_pword).toString());
                                        sender.sendMail(getText(R.string.txt_delivery_subject).toString(), getText(R.string.txt_email_body_common).toString()+name+" "+
                                                        surname+getText(R.string.txt_email_delivery_body)+
                                                "Your Courier: "+ CourierName+CourierSurname+"\n"+"Phone No: "+ CourierPhoneNo+"\n\n"+"Order ID: "+DonationID,
                                                getText(R.string.txt_developer_email).toString(),email);
                                    } catch (Exception e) {
                                        Log.e("SendMail", e.getMessage(), e);
                                    }
                                }

                            }).start();

                            break;

                        }

                    }
                    Qty[index]= "0";
                    client = new OkHttpClient();
                    String link = UpdateRequestUrl;

                    RequestBody formBody = new FormBody.Builder()
                            .add("donation_id",DonationID)
                            .add("courier_id",CourierID)
                            .add("donee_username", username)
                            .add("date_processed",date_processed)
                            .add("date_received",date_received)
                            .add("item",item_id)
                            .add("qty", String.valueOf(qty))
                            .build();
                    Request request = new Request.Builder()
                            .url(link)
                            .post(formBody)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                Log.d("INSERT","Updating REQUEST table in database successful");
                            }else{
                                Log.d("INSERT","Updating REQUEST table in database failed");
                            }

                        }
                    });

                    //SEND EMAIL
                    new Thread(new Runnable() {


                        @Override
                        public void run() {
                            try {
                                GMailSender sender = new GMailSender(getText(R.string.txt_developer_email).toString(),
                                        getText(R.string.txt_developer_pword).toString());
                                sender.sendMail(getText(R.string.txt_delivery_subject).toString(), getText(R.string.txt_email_body_common).toString()+name+" "+surname+getText(R.string.txt_email_delivery_body)+
                                                "Your Courier: "+ CourierName+CourierSurname+"\n"+"Phone No: "+ CourierPhoneNo+"\n\n"+"Order ID: "+DonationID,
                                        getText(R.string.txt_developer_email).toString(),email);
                            } catch (Exception e) {
                                Log.e("SendMail", e.getMessage(), e);
                            }
                        }

                    }).start();
                    break;

                }
                else if(DonorQty==0){
                    break;

                }
                else{
                    continue;

                }






            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }



}

