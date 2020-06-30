package com.example.lendahand;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class Left extends LinearLayout {
    TextView item_name;

    //EditText qty;
    public Left(Context context) {
        super(context);
        setOrientation(LinearLayout.HORIZONTAL);



        item_name= new TextView(context);

        item_name.setTextSize(16);
        item_name.setPadding(0,20,0,0);

        LayoutParams lp= new LayoutParams(LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

       /*qty = new EditText(context);
        qty.setInputType(InputType.TYPE_CLASS_NUMBER);
        qty.setHint("Enter Qty");*/
        lp.weight=0;

        lp.leftMargin=40;
        lp.bottomMargin=15;


        addView(item_name, lp);


        //LinearLayout rightLayout= new LinearLayout(context);
       // rightLayout.setOrientation(LinearLayout.VERTICAL);
       // rightLayout.addView(qty);

       // addView(rightLayout);

    }
    public void populate(JSONObject item)  {

        try {
            item_name.setText(item.getString("ITEM_NAME"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


}
