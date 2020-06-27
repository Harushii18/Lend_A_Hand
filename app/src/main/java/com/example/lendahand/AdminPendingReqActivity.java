package com.example.lendahand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import okhttp3.Response;


public class AdminPendingReqActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ArrayList<PendingRequestItem> items = new ArrayList<>();
    private String urlLink = "https://lamp.ms.wits.ac.za/home/s2089676/";
    private OkHttpClient client;

    //variables for navbar
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView txtNavName,txtNavEmail;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pending_req);

        //initialise drawer views
        initialiseNavBarViews();


        /*---------------------nav view-----------------------------------------*/
        //initialise navigation drawer
        setSupportActionBar(toolbar);

        navigationView.bringToFront(); //nav view can slide back

        //initialise nav view header values
        headerView=navigationView.getHeaderView(0);

        txtNavName=headerView.findViewById(R.id.txtNavName);
        txtNavEmail=headerView.findViewById(R.id.txtNavEmail);
        txtNavEmail.setText(StayLoggedIn.getEmail(AdminPendingReqActivity.this));
        txtNavName.setText(StayLoggedIn.getFName(AdminPendingReqActivity.this)+' '+StayLoggedIn.getLName(AdminPendingReqActivity.this));

        //show which nav item was selected
        navigationView.setCheckedItem(R.id.nav_admin_pending_req);

        //toggle is for the nav bar to go back and forth
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        /*make menu clickable*/
        navigationView.setNavigationItemSelectedListener(this);

        /*-------------------nav view end------------------------------------*/
        //initialise recycler view
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        final PendingRequestAdapter mAdapter = new PendingRequestAdapter(items, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter.setOnItemClickListener(new PendingRequestAdapter.OnItemClickListener() {
            @Override
            public void onButtonClick(int position) {
                //everything the button does is in the adapter
            }
        });

        //check connectivity
        GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
        if (!globalConnectivityCheck.isNetworkConnected()) {
            //if internet is not connected
            Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            //generates cards for each pending request motivation letter
            addItems();
        }
    }

    //=========================================================
    //navigation view methods
    private void initialiseNavBarViews() {
        drawerLayout = findViewById(R.id.dlAdminPending);
        navigationView = findViewById(R.id.admin_nav_view_pending_req);
        toolbar = findViewById(R.id.tbAdminDashboard);

    }


    /*OnClick for navigation bar menu items*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.nav_admin_add_courier:
                i = new Intent(this, AdminAddCourierActivity.class); //Request items menu item
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.nav_admin_courier_list:
                i = new Intent(this, AdminViewCourierListActivity.class);
                startActivity(i);
                break;
            case R.id.nav_admin_donor_list:
                i = new Intent(this, DonorRankingList.class);
                startActivity(i);
                break;
            case R.id.nav_admin_logout:
                StayLoggedIn.clearUserDetails(this);
                i = new Intent(this, LoginScreenActivity.class);
                startActivity(i);
                break;
            case R.id.nav_admin_profile:
                i = new Intent(this, ViewProfileActivity.class);
                startActivity(i);
                break;
            case R.id.nav_admin_pending_req:
                i = new Intent(this, AdminPendingReqActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START); //Close drawer after menu item is selected
        return true;
    }

    //so that when back button is pressed, it only closes the nav bar and the app doesn't close
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    //==========================================================

    private void addItems() {
        client = new OkHttpClient();
        String url = urlLink + "adminpendingreq.php";

        Request request = new Request.Builder()
                .url(url)
                .build();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                countDownLatch.countDown();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    String strResponse = response.body().string();
                    try {
                        JSONArray JArray = new JSONArray(strResponse);
                        String objMotivation, objName, objSurname, objUsername;
                        for (int i = 0; i < JArray.length(); i++) {
                            JSONObject object = JArray.getJSONObject(i);
                            objMotivation = object.getString("MOTIVATION_LETTER");
                            objName = object.getString("NAME");
                            objSurname = object.getString("SURNAME");
                            objUsername = object.getString("USERNAME");
                            items.add(new PendingRequestItem(objName + ' ' + objSurname, objMotivation, "Null", objUsername));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                countDownLatch.countDown();
            }
        });

        try {
            //to ensure that main thread waits for this
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
