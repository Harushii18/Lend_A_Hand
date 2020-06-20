package com.example.lendahand;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StayLoggedIn {
    //this class is to ensure that people can stay logged in on the app if they select the "stay logged in" option
    static final String PREF_USER_NAME= "PREF_USER_NAME";
    static final String PREF_USER_TYPE= "PREF_USER_TYPE";
    static final String PREF_STAY_LOGGED_IN="PREF_STAY_LOGGED_IN";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    public static void setLoggedIn(Context ctx, boolean blnStaySignedIn)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(PREF_STAY_LOGGED_IN,blnStaySignedIn);
        editor.commit();
    }
    public static void setUserType(Context ctx, String userType)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_TYPE, userType);
        editor.commit();
    }
    public static String getUserType(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_TYPE, "");
    }

    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static boolean getLoggedIn(Context ctx)
    {
        return getSharedPreferences(ctx).getBoolean(PREF_STAY_LOGGED_IN, false);
    }

    public static void clearUserDetails(Context ctx){
        SharedPreferences.Editor editor=getSharedPreferences(ctx).edit();
        editor.remove(PREF_USER_NAME);
        editor.remove(PREF_USER_TYPE);
        editor.remove(PREF_STAY_LOGGED_IN);
        editor.commit();
    }


}
