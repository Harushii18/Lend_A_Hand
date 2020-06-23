package com.example.lendahand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class CourierInfoAdapter extends RecyclerView.Adapter<CourierInfoAdapter.ViewHolder> {

    ArrayList<CourierInfo> CourierList;
    Context context;

    public CourierInfoAdapter(ArrayList<CourierInfo> CourierList)
    {
        this.CourierList = CourierList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.courierlistitem,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CourierInfo item = CourierList.get(position);

        holder.txtNum.setText(item.getStrNumDeliveries());
        holder.txtName.setText(item.getStrCourierName());
    }

    @Override
    public int getItemCount() {
        return CourierList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView txtName;
        TextView txtNum;
        CardView cv;

        public ViewHolder(View itemView)
        {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtCourierNameItem);
            txtNum = itemView.findViewById(R.id.txtNumDeliveriesItem);
            cv = itemView.findViewById(R.id.cvItem);
        }

    }
}