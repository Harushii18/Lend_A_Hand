package com.example.lendahand;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import java.io.IOException;


import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DonorRankingList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private TextView txtNavName, txtNavEmail;
    View headerView;
    TextView TxtName;
    TextView TxtSum;
    LinearLayout donor_layout;
    ProgressBar pb;
    CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get current user
        String user=StayLoggedIn.getUserType(DonorRankingList.this);
        //reuse of code, but only differentcontent views
        if (user.equals("Admin")){
            setContentView(R.layout.activity_admin_donor_ranking_list);
        }else if(user.equals("Donee")){
            setContentView(R.layout.activity_donor_ranking_list);
        }

        drawerLayout = findViewById(R.id.drawer_layout_DonorRank);
        navigationView = findViewById(R.id.nav_view_DonorRank);
        toolbar = findViewById(R.id.toolbar_DonorRank);

        setSupportActionBar(toolbar);
        /*---------------------nav view-----------------------------------------*/
        navigationView.bringToFront(); //nav view can slide back

        //show which nav item was selected
        if (user.equals("Admin")){
            navigationView.setCheckedItem(R.id.nav_admin_donor_list);
        }else if(user.equals("Donee")){
            navigationView.setCheckedItem(R.id.nav_list);
            //hide certain menu options depending on if donee is pending or not
            Menu nav_Menu = navigationView.getMenu();
            String status=StayLoggedIn.getDoneeStatus(DonorRankingList.this);
            if (status.equals("Pending")){
                nav_Menu.findItem(R.id.nav_donee_edit).setVisible(false);
                nav_Menu.findItem(R.id.nav_request).setVisible(false);
            }else if(status.equals("Rejected")){
                nav_Menu.findItem(R.id.nav_donee_edit).setVisible(true);
                nav_Menu.findItem(R.id.nav_request).setVisible(false);
            }else if(status.equals("Accepted")){
                nav_Menu.findItem(R.id.nav_donee_edit).setVisible(false);
                nav_Menu.findItem(R.id.nav_request).setVisible(true);
            };

        }

        //initialise nav view header values
        headerView = navigationView.getHeaderView(0);

        txtNavName = headerView.findViewById(R.id.headerName);
        txtNavEmail = headerView.findViewById(R.id.headerEmail);
        txtNavEmail.setText(StayLoggedIn.getEmail(DonorRankingList.this));
        txtNavName.setText(StayLoggedIn.getFName(DonorRankingList.this) + ' ' + StayLoggedIn.getLName(DonorRankingList.this));

        //toggle is for the nav bar to go back and forth
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        /*make menu clickable*/
        navigationView.setNavigationItemSelectedListener(this);


        pb = findViewById(R.id.progressBar);
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
                    //get all donors by performing a request to the server
                    performRequest();
                }
            }
        }.start();

        donor_layout = findViewById(R.id.donor_list);

    }


    private void performRequest() {
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

    public void processJSON(String json) {
        try {
            JSONArray all = new JSONArray(json);
            for (int i = 0; i < all.length(); i++) {
                JSONObject item = all.getJSONObject(i);
                String name = item.getString("NAME");
                String surname = item.getString("SURNAME");
                String sum = item.getString("NO_ITEMS");

                ConstraintLayout layout = new ConstraintLayout(this);

                View view = getLayoutInflater().inflate(R.layout.list, null);
                TxtSum = view.findViewById(R.id.Sum);
                TxtSum.setText(sum);
                TxtName = view.findViewById(R.id.Name);
                TxtName.setText(name + " " + surname);
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
            case R.id.nav_profile:
                i = new Intent(this, ViewProfileActivity.class);
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
            case R.id.nav_admin_add_courier:
                i = new Intent(this, AdminAddCourierActivity.class); //Request items menu item
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.nav_admin_logout:
                StayLoggedIn.clearUserDetails(this);
                i = new Intent(this, LoginScreenActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.nav_admin_profile:
                i = new Intent(this, ViewProfileActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.nav_admin_courier_list:
                i = new Intent(this, AdminViewCourierListActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.nav_admin_donor_list:
                i = new Intent(this, DonorRankingList.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.nav_admin_pending_req:
                i = new Intent(this, AdminPendingReqActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.nav_home:
                i = new Intent(this, DoneeDashboard.class);
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
}
