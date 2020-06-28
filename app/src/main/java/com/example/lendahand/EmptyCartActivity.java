package com.example.lendahand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

public class EmptyCartActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_cart);

        toolbar=findViewById(R.id.toolbar_EmptyCart);

        //Make back button on toolbar clickable

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(StayLoggedIn.getUserType(EmptyCartActivity.this).equals("Donee")){
                    Intent i= new Intent(EmptyCartActivity.this, CategoryListActivity.class);
                    startActivity(i);
                    finish();
                }
                else if(StayLoggedIn.getUserType(EmptyCartActivity.this).equals("Donor")){
                    Intent i= new Intent(EmptyCartActivity.this, DonorCategoryListActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        });
    }
    @Override
    public void onBackPressed() {

            //if user type is donor or donee
        if(StayLoggedIn.getUserType(EmptyCartActivity.this).equals("Donee")){
            super.onBackPressed();
            Intent i= new Intent(EmptyCartActivity.this, CategoryListActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
        else if(StayLoggedIn.getUserType(EmptyCartActivity.this).equals("Donor")){
            super.onBackPressed();
            Intent i= new Intent(EmptyCartActivity.this, DonorCategoryListActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }




    }


}
