package com.ictlao.android.app.timesheet.Message;

import android.app.Activity;
import android.app.ProgressDialog;

import com.ictlao.android.app.timesheet.R;

public class LoadingProgress {
    private ProgressDialog progressDialog;

    public LoadingProgress(Activity activity){
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getString(R.string.loading));
        progressDialog.setCancelable(false);
    }

    public void show(){
        if(progressDialog != null){
            if(!progressDialog.isShowing()){
                progressDialog.show();
            }
        }
    }

    public void show(String message){
        if(progressDialog != null){
            if(!progressDialog.isShowing()){
                progressDialog.setMessage(message);
                progressDialog.show();
            }
        }
    }

    public void dismiss(){
        if(progressDialog != null){
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }

    public void dispose(){
        progressDialog = null;
    }
}
