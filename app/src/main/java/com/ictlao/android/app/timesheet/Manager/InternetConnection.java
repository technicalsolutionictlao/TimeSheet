package com.ictlao.android.app.timesheet.Manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
// Manage the connection
public class InternetConnection {
    private Context mContext = null;
    // constructor of this class
    public InternetConnection(Context context){
        mContext = context;
    }
    // check if connected
    public boolean isConnected(){
        boolean isConnected = false;
        try {
            ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if(info != null){
                if(info.isConnected()) {
                    isConnected = true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return isConnected;
    }

    // dispose this class
    public void dispose(){
        mContext = null;
    }
}
