package com.example.lendahand;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class CartActivity extends AppCompatActivity  {
    TextView item;
    TextView qty;
    Button add;
    Button minus;
    ImageView trash;
    int limit=0;
    int [] ID=MainActivity.IDArray.ID;
    String[] Item = MainActivity.ItemArray.Item;
    String[] Qty = MainActivity.QtyArray.Qty;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        LinearLayout items_layout= findViewById(R.id.items_layout);


        toolbar=findViewById(R.id.toolbar_Cart);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(CartActivity.this, CategoryListActivity.class);
                startActivity(i);
                finish();
            }
        });




        for (int i=0;i<50;i++){
            if(i!=49){
                for(int j=i+1;j<50;j++){
                    if(ID[i]!=0&&ID[j]!=0&&ID[i]==ID[j]  ){
                       ID[i]=ID[j];
                       Item[i]=Item[j];
                       Qty[i]=Qty[j];
                        ID[j]=0;
                        Item[j]="0";
                        Qty[j]="0";
                    }
                    else{
                        continue;
                    }
            }


            }
            if(ID[i]!=0&&Qty[i].length()>0){


                RelativeLayout right = new RelativeLayout(this);

                item= new TextView(this);
                qty= new TextView(this);
                add= new Button(this);
                minus= new Button(this);
                trash= new ImageView(this);



                View view= getLayoutInflater().inflate(R.layout.cart2,null);
                qty= view.findViewById(R.id.CartQty);
                qty.setText(Qty[i]);
                limit=limit+Integer.parseInt(Qty[i]);


                item= view.findViewById(R.id.Cart_Item_Name);
                item.setText(Item[i]);

                add= view.findViewById(R.id.btnAddQty);
                minus= view.findViewById(R.id.btnSubtractQty);
                trash= view.findViewById(R.id.Cart_trash);

                right.addView(view);

                DeleteMethod(qty,i,right);
                AddMethod(qty,i);
                MinusMethod(qty,i);


                GradientDrawable border = new GradientDrawable();
                border.setColor(0xFFFFFFFF);
                border.setStroke(1,0xFFC0C0C0);
                if(Build.VERSION.SDK_INT<Build.VERSION_CODES.JELLY_BEAN){
                    right.setBackgroundDrawable(border);
                }
                else{
                    right.setBackground(border);
                }

                items_layout.addView(right);

            }
            else{
                continue;
            }
        }





    }


    private void MinusMethod(final TextView qty, final int i) {
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limit=limit-1;
                int s= Integer.parseInt(Qty[i])-1;
                if(s==0){
                    qty.setText("0");
                    for(int j=i;j<50;j++){
                        if(j!=49){
                            if(ID[j+1]!=0){
                                ID[j]=ID[j+1];
                                Item[j]=Item[j+1];
                                Qty[j]=Qty[j+1];
                                ID[j+1]=0;
                                Item[j+1]="0";
                                Qty[j+1]="0";

                            }
                            else{
                                ID[j]=0;
                                Item[j]="0";
                                Qty[j]="0";
                            }
                        }
                    }

                    startActivity(getIntent());
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    if (MainActivity.QtyArray.Qty[0]=="0"){
                        Intent i= new Intent(CartActivity.this, EmptyCartActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                    }

                }
                else{
                    qty.setText(String.valueOf(s));
                    Qty[i]= String.valueOf(s);
                }


            }
        });
    }

    private void AddMethod(final TextView qty, final int i) {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int s= Integer.parseInt(Qty[i])+1;

                limit=limit+1;
                if(limit<=50) {
                    qty.setText(String.valueOf(s));
                    Qty[i] = String.valueOf(s);
                }
                else{
                    Toast.makeText(CartActivity.this, "Sorry, your cart is full.", Toast.LENGTH_SHORT).show();
                    limit=limit-1;
                }

            }
        });
    }

    private void DeleteMethod(final TextView qty, final int i, final RelativeLayout right) {
        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                qty.setText("0");
                Toast.makeText(CartActivity.this, "Item deleted.", Toast.LENGTH_SHORT).show();
                right.setBackgroundColor(Color.parseColor("#D3D3D3"));

                for(int j=i;j<50;j++){
                    if(j!=49){
                        if(ID[j+1]!=0){
                            ID[j]=ID[j+1];
                            Item[j]=Item[j+1];
                            Qty[j]=Qty[j+1];
                            ID[j+1]=0;
                            Item[j+1]="0";
                            Qty[j+1]="0";

                        }
                        else{
                            ID[j]=0;
                            Item[j]="0";
                            Qty[j]="0";
                        }
                    }
                }

                startActivity(getIntent());
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                if (MainActivity.QtyArray.Qty[0]=="0"){
                    Intent i= new Intent(CartActivity.this, EmptyCartActivity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                }
               



            }
        });

    }
    @Override
    public void onBackPressed() {

            super.onBackPressed();
            Intent i = new Intent(CartActivity.this, CategoryListActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();

    }



}
