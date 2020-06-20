package com.example.lendahand;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListItemsActivity extends AppCompatActivity implements View.OnClickListener {
    EditText qty;
    LinearLayout layout;

    int[] tempID = new int[50];
    int[] ID;

    String[] Item;
    String[] tempItem = new String[50];

    String[] Qty;
    String[] tempQty = new String[50];

    Button btnAdd;


    int index = 100;
    int limit = 0;
    int num = 0;

    ProgressBar pb;
    CountDownTimer countDownTimer;

    TextView txtitem_name;


    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);


        toolbar = findViewById(R.id.toolbar_ListItems);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListItemsActivity.this, CategoryListActivity.class);
                startActivity(i);
                finish();
            }
        });


        pb = findViewById(R.id.progressBar2);
        pb.setProgress(0);
        pb.setSecondaryProgress(0);


        layout = findViewById(R.id.linear_layout);


        ID = DoneeDashboard.IDArray.ID;
        Item = DoneeDashboard.ItemArray.Item;
        Qty = DoneeDashboard.QtyArray.Qty;

        for (int i = 0; i < 50; i++) {
            if (ID[i] == 0) {

                index = i - 1;
                num = i;
                break;
            } else {
                limit = limit + Integer.parseInt(Qty[i]);
            }
        }

        if (index == 100) {
            index = 49;
        }


        btnAdd = findViewById(R.id.btnAddQty);
        btnAdd.setOnClickListener(this);




        /* Getting stuff from remote database using OkHttp*/
        countDownTimer = new CountDownTimer(3000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress = pb.getProgress() + 2;
                if (progress > pb.getMax()) progress = 0;
                pb.setProgress(progress);

            }

            @Override
            public void onFinish() {
                //check connectivity
                GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
                if (!globalConnectivityCheck.isNetworkConnected()) {
                    //if internet is not connected
                    Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    pb.setVisibility(View.VISIBLE);
                    //perform okhttp request to server
                    performRequest();
                }
            }
        }.start();

    }

    private void performRequest() {
        OkHttpClient client = new OkHttpClient();
        String url = getIntent().getStringExtra("url");

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                final String responseData = response.body().string();

                ListItemsActivity.this.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        processJSON(responseData);
                    }
                });
            }
        });

    }


    public void processJSON(String json) {
        try {
            JSONArray all = new JSONArray(json);
            for (int i = 0; i < all.length(); i++) {
                JSONObject item = all.getJSONObject(i);

                String itemid = item.getString("ITEM_ID");
                int item_id = Integer.parseInt(itemid);
                String item_name = item.getString("ITEM_NAME");

                qty = new EditText(this);

                txtitem_name = new TextView(this);

                RelativeLayout rl = new RelativeLayout(this);

                View view = getLayoutInflater().inflate(R.layout.items_list, null);
                qty = view.findViewById(R.id.qty);
                txtitem_name = view.findViewById(R.id.item_name);
                txtitem_name.setText(item_name);
                rl.addView(view);

                EditTextMethod(qty, item_name, item_id);//get stuff from EditText


                GradientDrawable border = new GradientDrawable();
                border.setColor(0xFFFFFFFF);
                border.setStroke(1, 0xFFC0C0C0);
                rl.setBackground(border);
                layout.addView(rl);

            }
            pb.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void EditTextMethod(final EditText qty, final String item_name, final int item_id) {

        qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ListItemsActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Your cart is full. ");
                builder.setMessage("You are only permitted to request 50 or less items.");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.cancel();
                        //qty.setHint("0");
                        startActivity(getIntent());
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });


                if (s.toString().length() > 0) {
                    limit = limit + Integer.parseInt(s.toString());
                }


                for (int k = 0; k < 50; k++) {

                    if (tempID[k] != 0 && tempID[k] == item_id) {
                        limit = limit - Integer.parseInt(tempQty[k]);
                        //index=index-1;
                        if (s.toString().length() > 0 && limit <= 50) {
                            tempQty[k] = s.toString();
                            break;
                        } else if (limit > 50) {
                            //Toast.makeText(ListItemsActivity.this, "Sorry, your cart is full.", Toast.LENGTH_SHORT).show();

                            builder.show();


                            for (int j = k; j < 50; j++) {
                                if (j != 49) {
                                    if (tempID[j + 1] != 0) {
                                        tempID[j] = tempID[j + 1];
                                        tempItem[j] = tempItem[j + 1];
                                        tempQty[j] = tempQty[j + 1];
                                        tempID[j + 1] = 0;
                                        tempItem[j + 1] = "0";
                                        tempQty[j + 1] = "0";

                                    } else {
                                        tempID[j] = 0;
                                        tempItem[j] = "0";
                                        tempQty[j] = "0";
                                    }
                                }
                            }

                            break;
                        } else {
                            for (int j = k; j < 50; j++) {
                                if (j != 49) {
                                    if (tempID[j + 1] != 0) {
                                        tempID[j] = tempID[j + 1];
                                        tempItem[j] = tempItem[j + 1];
                                        tempQty[j] = tempQty[j + 1];
                                        tempID[j + 1] = 0;
                                        tempItem[j + 1] = "0";
                                        tempQty[j + 1] = "0";

                                    } else {
                                        tempID[j] = 0;
                                        tempItem[j] = "0";
                                        tempQty[j] = "0";
                                    }
                                }
                            }

                            break;
                        }

                    } else if (tempID[k] == 0) {
                        if (limit > 50) {
                            //Toast.makeText(ListItemsActivity.this, "Sorry, your cart is full.", Toast.LENGTH_SHORT).show();

                            builder.show();

                        } else {
                            tempID[k] = item_id;
                            tempItem[k] = item_name;
                            tempQty[k] = s.toString();
                            break;
                        }


                    }
                }


            }//end of textChanged
        });
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.btnAddQty:
                int temp = 0;
                if (tempID[0] == 0) {
                    Toast.makeText(ListItemsActivity.this, "Enter quantity for required items.", Toast.LENGTH_SHORT).show();

                } else {
                    for (int j = num; j < 50; j++) {
                        if (tempID[temp] != 0) {
                            ID[j] = tempID[temp];
                            Item[j] = tempItem[temp];
                            Qty[j] = tempQty[temp];
                            temp = temp + 1;
                        } else {
                            temp = temp + 1;
                        }
                    }
                    Toast.makeText(ListItemsActivity.this, "Successfully added to cart.", Toast.LENGTH_SHORT).show();
                    i = new Intent(this, CategoryListActivity.class);
                    startActivity(i);
                    break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ListItemsActivity.this, CategoryListActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();

    }


}
