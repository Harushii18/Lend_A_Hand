package com.example.lendahand;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.widget.Toolbar;


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

public class DonorDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    //Variables
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView Nadine;
    Toolbar toolbar;
    Button donateButton;
    CardView How;
    private CardView DonorsView;
    private CardView DoneesView;
    CardView MostRequested;

    TextView TotalDonors;
    TextView TotalDonees;
    int sum;
    int doneesum;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_dashboard);

        TotalDonors= findViewById(R.id.textDonorDonors);
        TotalDonees=findViewById(R.id.textDonorDonees);

        Nadine= findViewById(R.id.NadineTextView);

        String temp= "Hi "+ StayLoggedIn.getFName(DonorDashboardActivity.this)+"!";
        Nadine.setText(temp);



        DonorsView= findViewById(R.id.Donor_DonorsView);
        DoneesView=findViewById(R.id.Donor_DoneesView);



        donateButton=findViewById(R.id.Donatebutton);
        donateButton.setOnClickListener(this);

        How= findViewById(R.id.Donor_HowItWorks);
        MostRequested= findViewById(R.id.RequestedCardView);


        How.setOnClickListener(this);
        DonorsView.setOnClickListener(this);
        DoneesView.setOnClickListener(this);
        MostRequested.setOnClickListener(this);



        drawerLayout = findViewById(R.id.drawer_donor_layout);
        navigationView=findViewById(R.id.nav_donor_view);
        toolbar=findViewById(R.id.toolbar_donor);

        View headerView= navigationView.getHeaderView(0);
        TextView headerName= headerView.findViewById(R.id.headerName);//changing name and email on nav bar header
        headerName.setText(StayLoggedIn.getFName(DonorDashboardActivity.this)+" "+StayLoggedIn.getLName(DonorDashboardActivity.this));

        TextView headerEmail= headerView.findViewById(R.id.headerEmail);
        headerEmail.setText(StayLoggedIn.getEmail(DonorDashboardActivity.this));

        /*toolbar, so toolbar acts as action bar to utilise toggle*/

        setSupportActionBar(toolbar);
        /*nav view*/
        navigationView.bringToFront(); //animation on clicking menu items

        //show which nav item was selected
        navigationView.setCheckedItem(R.id.nav_donor_home);


        //toggle is for the nav bar to go back and forth
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.nav_open,R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        /*make menu clickable*/
        navigationView.setNavigationItemSelectedListener(this); //animation on menu items


        /*-------------------------------------------------------OkHttp--------------------------------------*/
        //check connectivity
        GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
        if (!globalConnectivityCheck.isNetworkConnected()) {
            //if internet is not connected
            Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            //get total donors and donees by requesting from server
            performDonorRequest();
            performDoneeRequest();
        }
    }

    private void performDonorRequest() {
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

                DonorDashboardActivity.this.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void run() {
                        processJSON(responseData);
                    }
                });
            }
        });

    }
    private void performDoneeRequest() {
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

                DonorDashboardActivity.this.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void run() {
                        processDoneeJSON(responseData);
                    }
                });
            }
        });

    }

    /*-------------------JSON method------------------------*/
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void processJSON(String json) {
        try {
            JSONArray all = new JSONArray(json);
            for (int i = 0; i < all.length(); i++) {
                JSONObject item = all.getJSONObject(i);
                String SUM = item.getString("SUM");
                sum = sum + Integer.parseInt(SUM);
            }
            TotalDonors.setText(sum + " Donors in total!");
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /*-------------------Donee JSON method------------------------*/
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void processDoneeJSON(String json) {
        try {
            JSONArray all = new JSONArray(json);
            for (int i = 0; i < all.length(); i++) {
                JSONObject item = all.getJSONObject(i);
                String SUM = item.getString("SUM");
                doneesum = doneesum + Integer.parseInt(SUM);
            }
            TotalDonees.setText(doneesum + " lives impacted");
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() { //so that when back button is pressed, it only closes the nav bar and the app doesn't close
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            int ID[]= DonorDashboardActivity.IDArray.ID;
            if(ID[0]==0){
                this.finishAffinity();
            }
            else{

                final AlertDialog.Builder builder = new AlertDialog.Builder(DonorDashboardActivity.this, R.style.AlertDialogTheme); //Error Message for when qty>50
                builder.setCancelable(true);
                builder.setTitle("Cart not empty. ");
                builder.setMessage("You still have items in your cart, are you sure you want to exit?");


                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DonorDashboardActivity.this.finishAffinity();


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }

        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) { //OnClicks. that intent thing. for menu items,goes in this method
        Intent i;
        switch (item.getItemId()) {
            case R.id.nav_donor_donate:
                i = new Intent(this, DonorCategoryListActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.nav_donor_list:
                i = new Intent(this, DonorDonorRankingList.class);
                startActivity(i);
                break;
            case R.id.nav_donor_about:
                i = new Intent(this, DonorAboutUs.class);
                startActivity(i);
                break;
            case R.id.nav_donor_logout:
                StayLoggedIn.clearUserDetails(this);
                i = new Intent(this, LoginScreenActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.Donor_HowItWorks : i= new Intent(this, DonorHowItWorksActivity.class); //HowItWorks cardView
                startActivity(i);
                break;
            case R.id.RequestedCardView: i= new Intent(this, MostRequestedItemsActivity.class);
                startActivity(i);
                break;
            case R.id.Donatebutton : i = new Intent(this, DonorCategoryListActivity.class);
                startActivity(i);
                break;
            case R.id.Donor_DonorsView : i= new Intent(this, TotalDonorsActivity.class);
                startActivity(i);
                break;
            case R.id.Donor_DoneesView : i= new Intent(this, TotalDoneesActivity.class);
                startActivity(i);
                break;
            default:break;
        }

    }

    /*------Item Arrays*---------------*/

    public static class IDArray {
        public static int[] ID = new int[100];

    }

    public static class ItemArray {
        public static String[] Item = new String[100];
    }

    public static class QtyArray {
        public static String[] Qty = new String[100];
    }
    public static class RequiredArray{
        public static int[] RequiredArr= new int [100];
    }



}
