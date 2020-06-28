package com.example.lendahand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class CategoryListActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private ImageView cart;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    CardView Food;
    CardView SSupplies;
    CardView Stationery;
    CardView Baby;
    CardView Airtime;
    CardView Clothes;
    private TextView txtNavName,txtNavEmail;
    View headerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        drawerLayout = findViewById(R.id.drawer_layout_CategoryList);
        navigationView = findViewById(R.id.nav_view_CategoryList);
        toolbar = findViewById(R.id.toolbar_CategoryList);

        /*toolbar, so toolbar acts as action bar to utilise menu toggle*/

        setSupportActionBar(toolbar);
        /*---------------------nav view-----------------------------------------*/
        navigationView.bringToFront(); //nav view can slide back



        //hide certain menu options depending on if donee is pending or not
        Menu nav_Menu = navigationView.getMenu();
        String status = StayLoggedIn.getDoneeStatus(CategoryListActivity.this);
        if (status.equals("Pending")) {
            nav_Menu.findItem(R.id.nav_donee_edit).setVisible(false);
            nav_Menu.findItem(R.id.nav_request).setVisible(false);
        } else if (status.equals("Rejected")) {
            nav_Menu.findItem(R.id.nav_donee_edit).setVisible(true);
            nav_Menu.findItem(R.id.nav_request).setVisible(false);
        } else if (status.equals("Accepted")) {
            //show which nav item was selected
            nav_Menu.findItem(R.id.nav_donee_edit).setVisible(false);
            nav_Menu.findItem(R.id.nav_request).setVisible(true);
        }

        navigationView.setCheckedItem(R.id.nav_request);
        //initialise nav view header values
        headerView=navigationView.getHeaderView(0);
        txtNavName=headerView.findViewById(R.id.headerName);
        txtNavEmail=headerView.findViewById(R.id.headerEmail);
        txtNavEmail.setText(StayLoggedIn.getEmail(CategoryListActivity.this));
        txtNavName.setText(StayLoggedIn.getFName(CategoryListActivity.this)+' '+StayLoggedIn.getLName(CategoryListActivity.this));

        //toggle is for the nav bar to go back and forth
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        /*make menu clickable*/
        navigationView.setNavigationItemSelectedListener(this);

        /*OnClick Listener for cart*/
        cart = findViewById(R.id.imgViewCart);
        cart.setOnClickListener(this);

        /*OnClick Listener for CardViews*/
        Food = findViewById(R.id.FoodCard);
        SSupplies = findViewById(R.id.SSuppliersCard);
        Stationery = findViewById(R.id.StationeryCard);
        Baby = findViewById(R.id.BabyCard);
        Airtime = findViewById(R.id.AirtimeCard);
        Clothes = findViewById(R.id.ClothesCard);

        Food.setOnClickListener(this);
        SSupplies.setOnClickListener(this);
        Stationery.setOnClickListener(this);
        Baby.setOnClickListener(this);
        Airtime.setOnClickListener(this);
        Clothes.setOnClickListener(this);


    }

    /*OnClick Listener method*/
    @Override
    public void onClick(View v) {
        Intent i;
        String url;
        switch (v.getId()) {
            case R.id.imgViewCart:
                if (DoneeDashboard.QtyArray.Qty[0] == null || DoneeDashboard.QtyArray.Qty[0] == "0") {
                    i = new Intent(this, EmptyCartActivity.class);
                    startActivity(i);
                    break;
                } else {
                    i = new Intent(this, CartActivity.class);
                    startActivity(i);
                    break;
                }

            case R.id.FoodCard:
                i = new Intent(v.getContext(), ListItemsActivity.class);
                url = "https://lamp.ms.wits.ac.za/home/s2089676/items.php?ITEM_TYPE=Food";
                i.putExtra("url", url);
                v.getContext().startActivity(i);
                break;

            case R.id.SSuppliersCard:
                i = new Intent(v.getContext(), ListItemsActivity.class);
                url = "https://lamp.ms.wits.ac.za/home/s2089676/items.php?ITEM_TYPE=Sanitary%20Supplies";
                i.putExtra("url", url);
                v.getContext().startActivity(i);
                break;
            case R.id.StationeryCard:
                i = new Intent(v.getContext(), ListItemsActivity.class);
                url = "https://lamp.ms.wits.ac.za/home/s2089676/items.php?ITEM_TYPE=Stationery";
                i.putExtra("url", url);
                v.getContext().startActivity(i);
                break;
            case R.id.BabyCard:
                i = new Intent(v.getContext(), ListItemsActivity.class);
                url = "https://lamp.ms.wits.ac.za/home/s2089676/items.php?ITEM_TYPE=Baby%20Essentials";
                i.putExtra("url", url);
                v.getContext().startActivity(i);
                break;
            case R.id.AirtimeCard:
                i = new Intent(v.getContext(), ListItemsActivity.class);
                url = "https://lamp.ms.wits.ac.za/home/s2089676/items.php?ITEM_TYPE=Airtime";
                i.putExtra("url", url);
                v.getContext().startActivity(i);
                break;
            default:
                break;

        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Intent i = new Intent(CategoryListActivity.this, DoneeDashboard.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }

    /*OnClick for navigation bar menu items*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
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
            case R.id.nav_home:
                i = new Intent(this, DoneeDashboard.class);
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

}
