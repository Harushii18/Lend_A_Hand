package com.example.lendahand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class CategoryListActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView cart;
    CardView Food;
    CardView SSupplies;
    CardView Stationery;
    CardView Baby;
    CardView Airtime;
    CardView Clothes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);





        /*OnClick Listener for cart*/
        cart = findViewById(R.id.imgViewCart);
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
        String url;
        switch (v.getId()) {
            case R.id.imgViewCart:
                if(MainActivity.QtyArray.Qty[0]==null||MainActivity.QtyArray.Qty[0]=="0") {
                    i = new Intent(this, EmptyCartActivity.class);
                    startActivity(i);
                    break;
                }
                else{
                    i= new Intent(this,CartActivity.class);
                    startActivity(i);
                    break;
                }

            case R.id.FoodCard: i = new Intent(v.getContext(), ListItemsActivity.class);
                url="https://lamp.ms.wits.ac.za/home/s2089676/items.php?ITEM_TYPE=Food";
                i.putExtra("url", url);
                v.getContext().startActivity(i);
                break;

            case R.id.SSuppliersCard: i = new Intent(v.getContext(), ListItemsActivity.class);
                url="https://lamp.ms.wits.ac.za/home/s2089676/items.php?ITEM_TYPE=Sanitary%20Supplies";
                i.putExtra("url", url);
                v.getContext().startActivity(i);
                break;
            case R.id.StationeryCard: i = new Intent(v.getContext(), ListItemsActivity.class);
                url="https://lamp.ms.wits.ac.za/home/s2089676/items.php?ITEM_TYPE=Stationery";
                i.putExtra("url", url);
                v.getContext().startActivity(i);
                break;
            case R.id.BabyCard: i = new Intent(v.getContext(), ListItemsActivity.class);
                url="https://lamp.ms.wits.ac.za/home/s2089676/items.php?ITEM_TYPE=Baby%20Essentials";
                i.putExtra("url", url);
                v.getContext().startActivity(i);
                break;
            case R.id.AirtimeCard: i = new Intent(v.getContext(), ListItemsActivity.class);
                url="https://lamp.ms.wits.ac.za/home/s2089676/items.php?ITEM_TYPE=Airtime";
                i.putExtra("url", url);
                v.getContext().startActivity(i);
                break;
            default:
                break;

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(CategoryListActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }

}
