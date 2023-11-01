package com.ictlao.android.app.timesheet.Dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.ictlao.android.app.timesheet.R;

public class GPSLocationDialog {
    private AlertDialog alertDialog;
    private View view;
    private Activity activity;

    public interface Listener {
        void onOk();
        void onClose();
    }

    private Listener listener;

    @SuppressLint("InflateParams")
    public GPSLocationDialog(Activity activity){
        this.activity = activity;
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        view = layoutInflater.inflate(R.layout.gps_location_dialog, null);
        onInitialize();
    }

    private void onInitialize(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        Button close = view.findViewById(R.id.close);
        Button ok = view.findViewById(R.id.ok);
        close.setOnClickListener(view1 -> {
            dismiss();
            listener.onClose();
        });
        ok.setOnClickListener(view1 -> {
            dismiss();
            onStartOpenGPS();
            listener.onOk();
        });
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.Animation_AppCompat_Dialog;
    }

    public void show(Listener listener){
        this.listener = listener;
        if(alertDialog != null)if(!alertDialog.isShowing())if(!(activity.isFinishing()))alertDialog.show();
    }

    private void dismiss(){
        if(alertDialog != null)if(alertDialog.isShowing())alertDialog.dismiss();
    }

    private void onStartOpenGPS(){
        this.activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    public void dispose(){
        alertDialog = null;
        view = null;
        activity = null;
    }
}
