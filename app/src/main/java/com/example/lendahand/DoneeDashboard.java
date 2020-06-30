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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class DoneeDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    //Variables
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private CardView DonorsView;
    private CardView HowCardview;
    private CardView DoneesView;
    private CardView TestimonialView;
    private Button requestButton;
    TextView TotalDonors;
    TextView TotalDonees;
    int sum;
    int doneesum;

    TextView Nadine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donee_dashboard);
        TotalDonors = findViewById(R.id.textDonors);
        TotalDonees = findViewById(R.id.textDonees);
        requestButton = findViewById(R.id.button);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        HowCardview = findViewById(R.id.HowItWorks_cardview);
        DonorsView = findViewById(R.id.DonorsView);
        DoneesView = findViewById(R.id.DoneesView);
        TestimonialView= findViewById(R.id.TestView);
        Nadine= findViewById(R.id.NadineTextView);

        String temp= "Hi "+ StayLoggedIn.getFName(DoneeDashboard.this)+"!";
        Nadine.setText(temp);

        View headerView= navigationView.getHeaderView(0);
        TextView headerName= headerView.findViewById(R.id.headerName); //changing name and email on nav bar header
        headerName.setText(StayLoggedIn.getFName(DoneeDashboard.this)+" "+StayLoggedIn.getLName(DoneeDashboard.this));

        TextView headerEmail= headerView.findViewById(R.id.headerEmail);
        headerEmail.setText(StayLoggedIn.getEmail(DoneeDashboard.this));




        /*toolbar, so toolbar acts as action bar to utilise menu toggle*/

        setSupportActionBar(toolbar);
        /*---------------------nav view-----------------------------------------*/
        navigationView.bringToFront(); //nav view can slide back

        //show which nav item was selected
        navigationView.setCheckedItem(R.id.nav_home);



        //hide certain menu options depending on if donee is pending or not
        Menu nav_Menu = navigationView.getMenu();
        String status=StayLoggedIn.getDoneeStatus(DoneeDashboard.this);
        if (status.equals("Pending")){
            nav_Menu.findItem(R.id.nav_donee_edit).setVisible(false);
            nav_Menu.findItem(R.id.nav_request).setVisible(false);
            requestButton.setVisibility(View.GONE);
        }else if(status.equals("Rejected")){
            nav_Menu.findItem(R.id.nav_donee_edit).setVisible(true);
            nav_Menu.findItem(R.id.nav_request).setVisible(false);
            requestButton.setVisibility(View.GONE);
        }else if(status.equals("Accepted")){
            nav_Menu.findItem(R.id.nav_donee_edit).setVisible(false);
            nav_Menu.findItem(R.id.nav_request).setVisible(true);
        };




        //toggle is for the nav bar to go back and forth
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        /*make menu clickable*/
        navigationView.setNavigationItemSelectedListener(this);

        /*--------------CardView-----------------------*/
        HowCardview.setOnClickListener(this);
        DonorsView.setOnClickListener(this);
        DoneesView.setOnClickListener(this);
        TestimonialView.setOnClickListener(this);


        /*----------Button----------*/
        requestButton.setOnClickListener(this);

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

                DoneeDashboard.this.runOnUiThread(new Runnable() {
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

                DoneeDashboard.this.runOnUiThread(new Runnable() {
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

    /*--------------------------------------------OnClick Listener method--------------------------------------------------------------*/
    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.HowItWorks_cardview:
                i = new Intent(this, HowItWorksActivity.class); //HowItWorks cardView
                startActivity(i);
                break;
            case R.id.button:
                i = new Intent(this, CategoryListActivity.class); //RequestButton
                startActivity(i);
                break;
            case R.id.DonorsView:
                i = new Intent(this, TotalDonorsActivity.class);
                startActivity(i);
                break;
            case R.id.DoneesView:
                i = new Intent(this, TotalDoneesActivity.class);
                startActivity(i);
                break;
            case R.id.TestView:
                i = new Intent(this, TestimonialActivity.class );
                startActivity(i);
                break;
            default:
                break;
        }

    }

    //so that when back button is pressed, it only closes the nav bar and the app doesn't close
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            int ID[]= DoneeDashboard.IDArray.ID;
            if(ID[0]==0){
                this.finishAffinity();
            }
            else{

                final AlertDialog.Builder builder = new AlertDialog.Builder(DoneeDashboard.this, R.style.AlertDialogTheme); //Error Message for when qty>50
                builder.setCancelable(true);
                builder.setTitle("Cart not empty. ");
                builder.setMessage("You still have items in your cart, are you sure you want to exit?");


                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DoneeDashboard.this.finishAffinity();


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


    /*OnClick for navigation bar menu items*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.nav_request:
                i = new Intent(this, CategoryListActivity.class); //Request items menu item
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.nav_list:
                i = new Intent(this, DonorRankingList.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.nav_about:
                i = new Intent(this, AboutUs.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.nav_donee_edit:
                i = new Intent(this, DoneeEditMotivationalLetterActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.nav_profile:
                i = new Intent(this, ViewProfileActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.nav_logout:
                StayLoggedIn.clearUserDetails(this);
                i = new Intent(this, LoginScreenActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START); //Close drawer after menu item is selected
        return true;
    }
    /*------Item Arrays*---------------*/

    public static class IDArray {
        public static int[] ID = new int[50];

    }

    public static class ItemArray {
        public static String[] Item = new String[50];
    }

    public static class QtyArray {
        public static String[] Qty = new String[50];
    }




}
