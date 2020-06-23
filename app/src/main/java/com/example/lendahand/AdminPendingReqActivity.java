package com.example.lendahand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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


public class AdminPendingReqActivity extends AppCompatActivity {
    private ArrayList<PendingRequestItem> items = new ArrayList<>();
    private String urlLink = "https://lamp.ms.wits.ac.za/home/s2089676/";
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pending_req);
        //initialise recycler view
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        final PendingRequestAdapter mAdapter = new PendingRequestAdapter(items, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter.setOnItemClickListener(new PendingRequestAdapter.OnItemClickListener() {
            @Override
            public void onButtonClick(int position) {
                //everything the button does is in the adapter
            }
        });

        //check connectivity
        GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
        if (!globalConnectivityCheck.isNetworkConnected()) {
            //if internet is not connected
            Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            //generates cards for each pending request motivation letter
            addItems();
        }
    }

    private void addItems() {
        client = new OkHttpClient();
        String url = urlLink + "adminpendingreq.php";

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
                        String objMotivation, objName, objSurname, objUsername;
                        for (int i = 0; i < JArray.length(); i++) {
                            JSONObject object = JArray.getJSONObject(i);
                            objMotivation = object.getString("MOTIVATION_LETTER");
                            objName = object.getString("NAME");
                            objSurname = object.getString("SURNAME");
                            objUsername = object.getString("USERNAME");
                            items.add(new PendingRequestItem(objName + ' ' + objSurname, objMotivation, "Null", objUsername));
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
