package com.example.lendahand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class AboutUs extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        drawerLayout = findViewById(R.id.drawer_layout_About_Us);
        navigationView=findViewById(R.id.nav_view_AboutUs);
        toolbar=findViewById(R.id.toolbarAboutUs);

        /*toolbar, so toolbar acts as action bar to utilise menu toggle*/

        setSupportActionBar(toolbar);

        /*---------------------nav view-----------------------------------------*/
        navigationView.bringToFront(); //nav view can slide back

        //show which nav item was selected
        navigationView.setCheckedItem(R.id.nav_about);

        //hide certain menu options depending on if donee is pending or not
        Menu nav_Menu = navigationView.getMenu();
        String status=StayLoggedIn.getDoneeStatus(AboutUs.this);
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

        //toggle is for the nav bar to go back and forth
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.nav_open,R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        /*make menu clickable*/
        navigationView.setNavigationItemSelectedListener(this);

    }
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }

    }
    /*OnClick for navigation bar menu items*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent i;
        switch (item.getItemId()){
            case R.id.nav_request : i= new Intent(this, CategoryListActivity.class); //Request items menu item
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.nav_list: i=new Intent(this,DonorRankingList.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.nav_home: i= new Intent(this, DoneeDashboard.class);
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
            case R.id.nav_logout:
                StayLoggedIn.clearUserDetails(this);
                i = new Intent(this, LoginScreenActivity.class);
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
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START); //Close drawer after menu item is selected
        return true;
    }
}
