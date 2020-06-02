package com.example.lendahand;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;


import androidx.appcompat.widget.Toolbar;


import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Variables
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        toolbar=findViewById(R.id.toolbar);

        /*toolbar, so toolbar acts as action bar to utilise toggle*/

        setSupportActionBar(toolbar);
        /*nav view*/
        navigationView.bringToFront(); //animation on clicking menu items

        //toggle is for the nav bar to go back and forth
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.nav_open,R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        /*make menu clickable*/
        navigationView.setNavigationItemSelectedListener(this); //animation on menu items
    }

    @Override
    public void onBackPressed() { //so that when back button is pressed, it only closes the nav bar and the app doesn't close
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) { //OnClicks. that intent thing. for menu items,goes in this method
        return true;
    }
}
