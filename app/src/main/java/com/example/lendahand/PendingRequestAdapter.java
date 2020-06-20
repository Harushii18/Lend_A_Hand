package com.example.lendahand;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.embersoft.expandabletextview.ExpandableTextView;

public class PendingRequestAdapter extends RecyclerView.Adapter<PendingRequestAdapter.ViewHolder> {
    private ArrayList<PendingRequestItem> items;
    private Context context;
    private OnItemClickListener mListener;
    private String urlLink = "https://lamp.ms.wits.ac.za/home/s2089676/";
    private OkHttpClient client;

    public interface OnItemClickListener {
        void onButtonClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public PendingRequestAdapter(ArrayList<PendingRequestItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public PendingRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_pending_donee_card, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingRequestAdapter.ViewHolder holder, final int position) {
        holder.isBinding = true;
        final PendingRequestItem item = items.get(position);
        holder.txtDoneeName.setText(item.getDoneeName());
        holder.txtMotivationLetter.setText(item.getMotivationLetter());
        holder.txtMotivationLetter.setOnStateChangeListener(new ExpandableTextView.OnStateChangeListener() {
            @Override
            public void onStateChange(boolean isShrink) {
                PendingRequestItem contentItem = items.get(position);
                contentItem.setShrink(isShrink);
                items.set(position, contentItem);
            }
        });
        holder.txtMotivationLetter.setText(item.getMotivationLetter());
        holder.txtMotivationLetter.resetState(item.isShrink());

        holder.rgStatus.check(items.get(position).getCheckedID());
        holder.isBinding = false;

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        boolean isBinding;
        ExpandableTextView txtMotivationLetter;
        TextView txtDoneeName;
        Button btnConfirm;
        RadioGroup rgStatus;
        int rbAccept, rbReject;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            txtMotivationLetter = itemView.findViewById(R.id.txtAdminMotivationLetter);
            txtDoneeName = itemView.findViewById(R.id.txtAdminDoneePending);
            btnConfirm = itemView.findViewById(R.id.btnAdminDoneePending);
            rgStatus = itemView.findViewById(R.id.rgStatus);
            rbAccept = R.id.rbAccept;
            rbReject = R.id.rbReject;

            //set on click listener for the button
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onButtonClick(position);

                            if (items.get(position).getStatus().equals("Null")) {
                                //if they didn't select a radio button
                                Toast toast = Toast.makeText(v.getContext(), "No option selected", Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                //check connectivity
                                GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(v.getContext());
                                if (!globalConnectivityCheck.isNetworkConnected()) {
                                    //if internet is not connected
                                    Toast toast = Toast.makeText(v.getContext(), "Internet disconnected. Please try again", Toast.LENGTH_SHORT);
                                    toast.show();
                                } else {
                                    //change status of donee in database
                                    addToDatabase(items.get(position).getPendingUsername(), items.get(position).getStatus());
                                    Toast toast = Toast.makeText(v.getContext(), "Donee status changed", Toast.LENGTH_SHORT);
                                    toast.show();
                                    //remove that item after changing donee's status
                                    items.remove(position);
                                    notifyDataSetChanged();
                                }
                            }

                        }
                    }
                }
            });

            rgStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    int position = getAdapterPosition();

                    RadioButton radioButton = group.findViewById(checkedId);

                    //get the selected radio button
                    if (!isBinding) {
                        items.get(position).setCheckedID(checkedId);
                        items.get(position).setStatus(radioButton != null ? radioButton.getText().toString() : "");
                    }
                }
            });

        }
    }

    private void addToDatabase(String strUsername, String strStatus) {
        client = new OkHttpClient();
        String url = urlLink + "adminpostpending.php";

        if (strStatus.equals("Accept")) {
            strStatus = "Accepted";
        } else {
            strStatus = "Rejected";
        }

        RequestBody formBody = new FormBody.Builder()
                .add("username", strUsername)
                .add("status", strStatus)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
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
                    Log.d("ADMIN UPDATE", "Updating donee status successful");
                } else {
                    Log.d("ADMIN UPDATE", "Updating donee status failed");
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
