package com.example.lendahand;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;

public class CartActivity extends AppCompatActivity  {
    TextView requested;
    ImageView trash;
    int [] ID=MainActivity.IDArray.ID;
    String[] Item = MainActivity.ItemArray.Item;
    String[] Qty = MainActivity.QtyArray.Qty;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        LinearLayout items_layout= findViewById(R.id.items_layout);


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


                trash= new ImageView(this);

                trash.setImageResource(R.drawable.trash_icon);

                requested= new TextView(this);
                String s= Item[i] +" "+"x "+Qty[i];
                requested.setText(s);
                DeleteMethod(requested,i,right);
                requested.setTextSize(16);
                LinearLayout.LayoutParams lip= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,80);
                lip.setMarginStart(450);

                //lip.setMarginEnd(40);
                //lip.rightMargin=50;

                //lip.setMarginStart(150);
                //lip.leftMargin= 450;
                lip.bottomMargin=15;
                LinearLayout.LayoutParams lp= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,100);
                lp.leftMargin=35;
               // lp.bottomMargin=15;
                lp.topMargin=10;
                right.addView(requested,lp);
                right.addView(trash,lip);
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

    private void DeleteMethod(final TextView requested, final int i, final RelativeLayout right) {
        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s= Item[i]+" "+"x0";
                requested.setText(s);
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
