package com.example.lendahand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class AdminViewCourierListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    CourierInfoAdapter courierInfoAdapter;
    ArrayList<CourierInfo> courierlist;

    public static final String[] TvShows= {"Breaking Bad","Rick and Morty", "FRIENDS","Sherlock","Stranger Things"};
    public static final String[] toilets= {"1","2","3","4","5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_courier_list);
        courierlist=new ArrayList<>();
        for(int i=0;i<TvShows.length;i++)
        {
            CourierInfo courierInfo = new CourierInfo();

            courierInfo.setStrCourierName(TvShows[i]);
            courierInfo.setStrNumDeliveries(toilets[i]);

            courierlist.add(courierInfo);
        }


        courierInfoAdapter = new CourierInfoAdapter(courierlist);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewCouriers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(courierInfoAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
