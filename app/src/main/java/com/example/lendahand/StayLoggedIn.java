package com.example.lendahand;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StayLoggedIn {
    //this class is to ensure that people can stay logged in on the app if they select the "stay logged in" option
    //but might need to make it more secure??
    static final String PREF_USER_NAME= "username";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }


}
