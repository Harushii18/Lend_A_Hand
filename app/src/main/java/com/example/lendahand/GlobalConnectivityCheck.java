package com.example.lendahand;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

//this class is created to check connectivity before every action that requires a call to the server
public class GlobalConnectivityCheck {
    Context context;

    public GlobalConnectivityCheck(Context context){
        this.context=context;
    }
    public boolean isNetworkConnected(){
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if ((networkInfo!=null) && (networkInfo.isConnectedOrConnecting())){
            return true;
        }else{
            return false;
        }
    }
}
