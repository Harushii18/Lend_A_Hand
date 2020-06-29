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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class DonorCategoryListActivity extends AppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener {
    //,NavigationView.OnNavigationItemSelectedListener
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_category_list);
        drawerLayout = findViewById(R.id.drawer_layout_CategoryList);
        navigationView=findViewById(R.id.nav_view_CategoryList);
        toolbar=findViewById(R.id.toolbar_CategoryList);

        /*toolbar, so toolbar acts as action bar to utilise menu toggle*/

        setSupportActionBar(toolbar);
        /*---------------------nav view-----------------------------------------*/


        navigationView.bringToFront(); //nav view can slide back

        //show which nav item was selected
        navigationView.setCheckedItem(R.id.nav_donor_donate);

        //toggle is for the nav bar to go back and forth
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.nav_open,R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        /*make menu clickable*/
        navigationView.setNavigationItemSelectedListener(this);

        View headerView= navigationView.getHeaderView(0);
        TextView headerName= headerView.findViewById(R.id.headerName); //changing name and email on nav bar header
        headerName.setText(StayLoggedIn.getFName(DonorCategoryListActivity.this)+" "+StayLoggedIn.getLName(DonorCategoryListActivity.this));

        TextView headerEmail= headerView.findViewById(R.id.headerEmail);
        headerEmail.setText(StayLoggedIn.getEmail(DonorCategoryListActivity.this));





        /*OnClick Listener for cart*/
        cart = findViewById(R.id.imgViewDonorCart);
        cart.setOnClickListener(this);

        /*OnClick Listener for CardViews*/
        Food = findViewById(R.id.FoodCard);
        SSupplies=findViewById(R.id.SSuppliersCard);
        Stationery=findViewById(R.id.StationeryCard);
        Baby=findViewById(R.id.BabyCard);
        Airtime=findViewById(R.id.AirtimeCard);
        Clothes=findViewById(R.id.ClothesCard);

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
        String type;
        switch (v.getId()) {
            case R.id.imgViewDonorCart:
                if(DonorDashboardActivity.QtyArray.Qty[0]==null||DonorDashboardActivity.QtyArray.Qty[0]=="0") {
                    i = new Intent(this, EmptyCartActivity.class);
                    startActivity(i);
                    break;
                }
                else{
                    i= new Intent(this,DonorCart.class);
                    startActivity(i);
                    break;
                }

            case R.id.FoodCard: i = new Intent(v.getContext(), DonorListItemsActivity.class);
                type="Food";
                i.putExtra("type", type);
                v.getContext().startActivity(i);
                break;

            case R.id.SSuppliersCard: i = new Intent(v.getContext(), DonorListItemsActivity.class);
                type="Sanitary Supplies";
                i.putExtra("type", type);
                v.getContext().startActivity(i);
                break;
            case R.id.StationeryCard: i = new Intent(v.getContext(), DonorListItemsActivity.class);
                type="Stationery";
                i.putExtra("type", type);
                v.getContext().startActivity(i);
                break;
            case R.id.BabyCard: i = new Intent(v.getContext(), DonorListItemsActivity.class);
                type="Baby Essentials";
                i.putExtra("type", type);
                v.getContext().startActivity(i);
                break;
            case R.id.AirtimeCard: i = new Intent(v.getContext(), DonorListItemsActivity.class);
                type="Airtime";
                i.putExtra("type", type);
                v.getContext().startActivity(i);
                break;
            default:
                break;

        }
    }
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
            Intent i = new Intent(DonorCategoryListActivity.this, DonorDashboardActivity.class);
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
        switch (item.getItemId()){
            case R.id.nav_donor_list: i=new Intent(this,DonorDonorRankingList.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.nav_donor_about: i=new Intent(this, DonorAboutUs.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.nav_donor_home: i= new Intent(this, DonorDashboardActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.nav_donor_logout:
                StayLoggedIn.clearUserDetails(this);
                i = new Intent(this, LoginScreenActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.nav_donor_profile:
                i = new Intent(this, ViewProfileActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            default:break;
        }
        drawerLayout.closeDrawer(GravityCompat.START); //Close drawer after menu item is selected
        return true;
    }



}
