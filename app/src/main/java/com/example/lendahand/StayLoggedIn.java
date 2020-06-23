package com.example.lendahand;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StayLoggedIn {
    //this class is to ensure that people can stay logged in on the app if they select the "stay logged in" option
    static final String PREF_USER_NAME= "PREF_USER_NAME";
    static final String PREF_USER_TYPE= "PREF_USER_TYPE";
    static final String PREF_STAY_LOGGED_IN="PREF_STAY_LOGGED_IN";
    static final String PREF_EMAIL_ADDRESS="PREF_USER_EMAIL";
    static final String PREF_USER_FNAME="PREF_USER_FNAME";
    static final String PREF_USER_LNAME="PREF_USER_LNAME";
    static final String PREF_USER_PROVINCE="PREF_USER_PROVINCE";
    static final String PREF_DONEE_STATUS="PREF_DONEE_STATUS";

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

    public static String getEmail(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_EMAIL_ADDRESS, "");
    }

    public static void setEmail(Context ctx, String userEmail)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_EMAIL_ADDRESS, userEmail);
        editor.commit();
    }

    public static String getDoneeStatus(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_DONEE_STATUS, "");
    }

    public static void setDoneeStatus(Context ctx, String doneeStatus)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_DONEE_STATUS, doneeStatus);
        editor.commit();
    }

    public static String getProvince(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_PROVINCE, "");
    }

    public static void setProvince(Context ctx, String userProvince)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_PROVINCE, userProvince);
        editor.commit();
    }

    public static String getFName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_FNAME, "");
    }

    public static void setFName(Context ctx, String userFName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_FNAME, userFName);
        editor.commit();
    }

    public static String getLName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_LNAME, "");
    }

    public static void setLName(Context ctx, String userLName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_LNAME, userLName);
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
        //remove all users' details for next login
        SharedPreferences.Editor editor=getSharedPreferences(ctx).edit();
        editor.remove(PREF_USER_NAME);
        editor.remove(PREF_USER_TYPE);
        editor.remove(PREF_STAY_LOGGED_IN);
        editor.remove(PREF_EMAIL_ADDRESS);
        editor.remove(PREF_USER_LNAME);
        editor.remove(PREF_USER_FNAME);
        editor.remove(PREF_USER_PROVINCE);
        editor.remove(PREF_DONEE_STATUS);
        editor.commit();
    }


}
