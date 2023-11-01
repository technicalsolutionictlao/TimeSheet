package com.ictlao.android.app.timesheet.Message;

import android.app.Activity;
import android.widget.Toast;

public class Message {
    private Activity activity;
    private Toast toast;
    public Message(Activity activity){
        this.activity = activity;
    }
    public void show(String message){
        toast = Toast.makeText(activity,message,Toast.LENGTH_LONG);
        toast.show();
    }
    public void dismiss(){
        if(toast != null){
            toast.cancel();
        }
    }

    public void dispose(){
        toast = null;
        activity = null;
    }
}
