package com.example.lendahand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class AdminViewCourierListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    RecyclerView recyclerView;
    CourierInfoAdapter courierInfoAdapter;
    ArrayList<CourierInfo> courierlist = new ArrayList<>();
    private String urlLink = "https://lamp.ms.wits.ac.za/home/s2089676/";
    //created this variable for ease of use and re-use of okhttp code
    private String linkEnd = "";
    private OkHttpClient client;
    private Button btnAscName, btnAscNum;

    //variables for navbar
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView txtNavName,txtNavEmail;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_courier_list);


        //initialise drawer views
        initialiseNavBarViews();


        /*---------------------nav view-----------------------------------------*/
        //initialise navigation drawer
        setSupportActionBar(toolbar);

        navigationView.bringToFront(); //nav view can slide back

        //show which nav item was selected
        navigationView.setCheckedItem(R.id.nav_admin_profile);

        //initialise nav view header values
        headerView=navigationView.getHeaderView(0);

        txtNavName=headerView.findViewById(R.id.txtNavName);
        txtNavEmail=headerView.findViewById(R.id.txtNavEmail);
        txtNavEmail.setText(StayLoggedIn.getEmail(AdminViewCourierListActivity.this));
        txtNavName.setText(StayLoggedIn.getFName(AdminViewCourierListActivity.this)+' '+StayLoggedIn.getLName(AdminViewCourierListActivity.this));

        //toggle is for the nav bar to go back and forth
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        /*make menu clickable*/
        navigationView.setNavigationItemSelectedListener(this);

        /*-------------------nav view end------------------------------------*/


        //populate courier list from server via okhttp request
        linkEnd = "viewcouriers.php";
        getCouriers();

        courierInfoAdapter = new CourierInfoAdapter(courierlist);

        //initialise recyclerview
        recyclerView = findViewById(R.id.recyclerViewCouriers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(courierInfoAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initialise buttons
        initButtons();

        //show what the buttons do for better user experience
        btnAscNum.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(),
                        getText(R.string.txt_admin_view_courier_asc_name), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        btnAscName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(),
                        getText(R.string.txt_admin_view_courier_asc_nums), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
    //=========================================================
    //navigation view methods
    private void initialiseNavBarViews() {
        drawerLayout = findViewById(R.id.dlAdminViewCourier);
        navigationView = findViewById(R.id.admin_nav_view_admin_view_courier);
        toolbar = findViewById(R.id.tbAdminViewCourierList);
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
            case R.id.nav_admin_profile:
                i = new Intent(this, ViewProfileActivity.class);
                startActivity(i);
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

    private void initButtons() {
        btnAscName = findViewById(R.id.btnTAscendingName);
        btnAscNum = findViewById(R.id.btnTAscendingNum);
    }

    private void getCouriers() {
        //check connectivity before making request
        GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
        if (!globalConnectivityCheck.isNetworkConnected()) {
            //if internet is not connected
            Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
            toast.show();
        } else {

            client = new OkHttpClient();
            String url = urlLink + linkEnd;

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
                            String objName, objSurname, objNum;

                            for (int i = 0; i < JArray.length(); i++) {
                                //extract json
                                JSONObject object = JArray.getJSONObject(i);
                                objName = object.getString("NAME");
                                objSurname = object.getString("SURNAME");
                                objNum = object.getString("NUM_DELIVERIES");

                                courierlist.add(new CourierInfo(objName + ' ' + objSurname, objNum));
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

    public void ShowListAscendingNames(View view) {
        //remove all items in recycler view and "refresh" the recycler view with ordered views
        clearItems();
        linkEnd = "viewcouriersnamesasc.php";
        //re-add items
        getCouriers();


    }

    private void clearItems() {
        int size = courierlist.size();
        courierlist.clear();
        courierInfoAdapter.notifyItemRangeRemoved(0, size);
    }

    public void ShowListAscendingNum(View view) {
        //remove all items in recycler view and "refresh" the recycler view with ordered views
        clearItems();
        linkEnd = "viewcouriersnumsasc.php";
        //re-add items
        getCouriers();

    }
}
