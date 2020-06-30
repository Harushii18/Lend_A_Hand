package com.example.lendahand;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

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

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CartActivity extends AppCompatActivity  {
    TextView item;
    TextView qty;
    Button add;
    Button minus;
    ImageView trash;
    int limit=0;
    int [] ID;
    String[] Item ;
    String[] Qty ;
    Button Checkout;
    private OkHttpClient client;

    String ConfirmedItems="";


    Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //User details

        String usertype= StayLoggedIn.getUserType(CartActivity.this);
        final String username= StayLoggedIn.getUserName(CartActivity.this);
        final String name= StayLoggedIn.getFName(CartActivity.this);
        final String surname = StayLoggedIn.getLName(CartActivity.this);
        final String email= StayLoggedIn.getEmail(CartActivity.this);

        //get date

        SimpleDateFormat currentDate=new SimpleDateFormat("yyyy-MM-dd");
        Date todayDate= new Date();
        final String date= currentDate.format(todayDate);

        final String url= "https://lamp.ms.wits.ac.za/home/s2089676/doneecheckout.php";

        //Using correct array


            ID= DoneeDashboard.IDArray.ID;
            Item = DoneeDashboard.ItemArray.Item;
            Qty = DoneeDashboard.QtyArray.Qty;

        //==============================//

        ////////Add stuff to database
        Checkout=findViewById(R.id.btnCheckout);
        Checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check connectivity
                GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
                if (!globalConnectivityCheck.isNetworkConnected()) {
                    //if internet is not connected
                    Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    final Intent intent = new Intent(CartActivity.this, DoneeDashboard.class);
                    client = new OkHttpClient();
                    String link = url;
                    for (int k =0; k < 50; k++){
                        if(ID[k]!=0){
                            ConfirmedItems=ConfirmedItems+ Item[k]+" x"+Qty[k]+'\n';

                            //Add to database
                            RequestBody formBody = new FormBody.Builder()
                                    .add("username", username)
                                    .add("item", String.valueOf(ID[k]))
                                    .add("qty", Qty[k])
                                    .add("date_ordered", date)
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
                                        Log.d("INSERT","Inserting new request to database successful");
                                    }else{
                                        Log.d("INSERT","Inserting new request to database failed");
                                    }

                                }
                            });
                        }
                        else{
                            break;
                        }
                    }

                    //send email to user telling them their items
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                GMailSender sender = new GMailSender(getText(R.string.txt_developer_email).toString(),
                                        getText(R.string.txt_developer_pword).toString());
                                sender.sendMail(getText(R.string.txt_checkout_email_subject).toString(), getText(R.string.txt_email_body_common).toString()+name+" "+surname+getText(R.string.txt_email_doneecheckout_body)+
                                        ConfirmedItems,getText(R.string.txt_developer_email).toString(),email);
                            } catch (Exception e) {
                                Log.e("SendMail", e.getMessage(), e);
                            }
                        }

                    }).start();


                    final AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this, R.style.AlertDialogTheme); //Error Message for when qty>50
                    builder.setCancelable(true);
                    builder.setTitle("Successfully checked out your items. ");
                    builder.setMessage("You will be emailed shortly with your item details.");


                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (int y =0; y<50; y++){
                                ID[y]=0;
                                Item[y]="0";
                                Qty[0]="0";
                            }

                            startActivity(intent);
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                        }
                    });
                    builder.show();


                } //else
            }
        });





        LinearLayout items_layout= findViewById(R.id.items_layout);


        toolbar=findViewById(R.id.toolbar_Cart);

        //Make back button in toolbar clickable

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(StayLoggedIn.getUserType(CartActivity.this).equals("Donee")){
                    Intent i= new Intent(CartActivity.this, CategoryListActivity.class);
                    startActivity(i);
                    finish();
                }


            }
        });


        ////

        for (int i=0;i<50;i++){ //this loop removes replicas
            if(i!=49){
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
                AddMethod(qty,i); //Method for increasing qty on cart xml
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
                        Intent i= new Intent(CartActivity.this, EmptyCartActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);


                    }

                }
                else{
                    qty.setText(String.valueOf(s));
                    Qty[i]= String.valueOf(s);
                }


            }
        });
    }

    private void AddMethod(final TextView qty, final int i) {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int s= Integer.parseInt(Qty[i])+1;

                limit=limit+1;
                if(limit<=50) {
                    qty.setText(String.valueOf(s));
                    Qty[i] = String.valueOf(s);
                }
                else{
                    Toast.makeText(CartActivity.this, "Sorry, your cart is full.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CartActivity.this, "Item deleted.", Toast.LENGTH_SHORT).show();
                right.setBackgroundColor(Color.parseColor("#D3D3D3")); //Make list item grey

                for(int j=i;j<50;j++){ //Delete in array
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
                if (Qty[0]=="0"){ //Check if cart empty
                    Intent i= new Intent(CartActivity.this, EmptyCartActivity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                }
            }
        });

    }
    @Override
    public void onBackPressed() {


            Intent i = new Intent(CartActivity.this, CategoryListActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();




    }



}
