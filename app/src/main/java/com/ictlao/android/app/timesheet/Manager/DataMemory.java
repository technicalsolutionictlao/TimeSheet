package com.ictlao.android.app.timesheet.Manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.ictlao.android.app.timesheet.Items.AdminItems;
import com.ictlao.android.app.timesheet.Items.UserItems;
// Manage data in the memory
public class DataMemory {
    // sharedPreferences name
    private static final String SHARE_NAME = "SHARE_NAME";
    // user id key
    private static final String USER_ID = "USER_ID";
    // password key
    private static final String PASSWORD = "PASSWORD";
    // check admin login key
    private static final String IS_ADMIN = "IS_ADMIN";
    // check allow notification message
    private static final String ALLOW_NOTIFY = "ALLOW_NOTIFY";
    // save last push key
    private static final String PUSH_KEY = "PUSH_KEY";

    private static SharedPreferences sharedPreferences;
    // get instance before used
    public static void getInstance(Activity activity){
        sharedPreferences = activity.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE);
    }
    // set user save into memory
    public static void setUser(UserItems items){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID,items.getEmail());
        editor.putString(PASSWORD,items.getUserId());
        editor.putBoolean(IS_ADMIN,false);
        editor.apply();
    }
    // set user save into memory
    public static void setUser(AdminItems items){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID,items.getEmail());
        editor.putString(PASSWORD,items.getPassword());
        editor.putBoolean(IS_ADMIN,true);
        editor.apply();
    }
    // return user id in memory
    public static String getUserId(){
        return sharedPreferences.getString(USER_ID,"");
    }
    // return password in memory
    public static String getPassword(){
        return sharedPreferences.getString(PASSWORD,"");
    }
    // return login status in memory
    public static boolean isAdmin(){
        return sharedPreferences.getBoolean(IS_ADMIN,false);
    }
    // set allow notification into memory
    public static void setAllowNotify(boolean allow){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ALLOW_NOTIFY,allow);
        editor.apply();
    }
    // return allow notification status in memory
    public static boolean getAllowNotify(){
        return sharedPreferences.getBoolean(ALLOW_NOTIFY, false);
    }
    // clear data in memory
    public static void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID,"");
        editor.putString(PASSWORD,"");
        editor.putBoolean(IS_ADMIN,false);
        editor.apply();
    }

    // get last push key from the disk
    public static String getPushKey(){
        return sharedPreferences.getString(PUSH_KEY,"");
    }

    // save push key to the disk
    public static void setPushKey(String pushKey){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PUSH_KEY,pushKey);
        editor.apply();
    }
}
