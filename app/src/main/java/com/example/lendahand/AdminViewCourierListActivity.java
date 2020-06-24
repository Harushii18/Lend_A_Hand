package com.example.lendahand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdminViewCourierListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    CourierInfoAdapter courierInfoAdapter;
    ArrayList<CourierInfo> courierlist = new ArrayList<>();
    private String urlLink = "https://lamp.ms.wits.ac.za/home/s2089676/";
    //created this variable for ease of use and re-use of okhttp code
    private String linkEnd = "";
    private OkHttpClient client;
    private Button btnAscName, btnAscNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_courier_list);

        //populate courier list from server via okhttp request
        linkEnd = "viewcouriers.php";
        getCouriers();

        courierInfoAdapter = new CourierInfoAdapter(courierlist);

        //initialise recyclerview
        recyclerView = findViewById(R.id.recyclerViewCouriers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(courierInfoAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initialise buttons
        initButtons();

        //show what the buttons do for better user experience
        btnAscNum.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(),
                        getText(R.string.txt_admin_view_courier_asc_name), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        btnAscName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(),
                        getText(R.string.txt_admin_view_courier_asc_nums), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void initButtons() {
        btnAscName = findViewById(R.id.btnTAscendingName);
        btnAscNum = findViewById(R.id.btnTAscendingNum);
    }

    private void getCouriers() {
        //check connectivity before making request
        GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
        if (!globalConnectivityCheck.isNetworkConnected()) {
            //if internet is not connected
            Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
            toast.show();
        } else {

            client = new OkHttpClient();
            String url = urlLink + linkEnd;

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                    countDownLatch.countDown();
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String strResponse = response.body().string();
                        try {
                            JSONArray JArray = new JSONArray(strResponse);
                            String objName, objSurname, objNum;

                            for (int i = 0; i < JArray.length(); i++) {
                                //extract json
                                JSONObject object = JArray.getJSONObject(i);
                                objName = object.getString("NAME");
                                objSurname = object.getString("SURNAME");
                                objNum = object.getString("NUM_DELIVERIES");

                                courierlist.add(new CourierInfo(objName + ' ' + objSurname, objNum));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    countDownLatch.countDown();
                }
            });

            try {
                //to ensure that main thread waits for this
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void ShowListAscendingNames(View view) {
        //remove all items in recycler view and "refresh" the recycler view with ordered views
        clearItems();
        linkEnd = "viewcouriersnamesasc.php";
        //re-add items
        getCouriers();


    }

    private void clearItems() {
        int size = courierlist.size();
        courierlist.clear();
        courierInfoAdapter.notifyItemRangeRemoved(0, size);
    }

    public void ShowListAscendingNum(View view) {
        //remove all items in recycler view and "refresh" the recycler view with ordered views
        clearItems();
        linkEnd = "viewcouriersnumsasc.php";
        //re-add items
        getCouriers();

    }
}
