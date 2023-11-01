package com.ictlao.android.app.timesheet.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.ictlao.android.app.timesheet.R;

import java.util.Calendar;

public class YMDialog {
    private int month = 0;
    private int year = 0;
    private Calendar calendar;
    private Activity activity;
    private AlertDialog alertDialog;
    private NumberPicker yPicker;
    private NumberPicker mPicker;
    private Button cancel;
    private Button ok;

    public interface Listener {
        void onOk(int year, int month);
        default void onCancel(){}
    }

    private Listener listener;

    public YMDialog(Activity activity){
        this.activity = activity;
        calendar = Calendar.getInstance();
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        onInitialize();
    }

    private void onInitialize(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = activity.getLayoutInflater().inflate(R.layout.y_m_dialog_layout,null);
        yPicker = view.findViewById(R.id.yPicker);
        mPicker = view.findViewById(R.id.mPicker);
        cancel = view.findViewById(R.id.cancel);
        ok = view.findViewById(R.id.ok);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);

        int ymin = year - 10;
        yPicker.setMaxValue(year);
        yPicker.setMinValue(ymin);
        yPicker.setValue(year);

        int mmin = month + 1;
        mPicker.setMaxValue(12);
        mPicker.setMinValue(1);
        mPicker.setValue(mmin);

        cancel.setOnClickListener(view1 -> onCancelClick());
        ok.setOnClickListener(view1 -> onOkClick());
    }

    private void onCancelClick(){
        if(alertDialog != null) {
            if (alertDialog.isShowing()){
                alertDialog.dismiss();
            }
        }
        listener.onCancel();
    }

    private void onOkClick(){
        int y = 0;
        int m = 0;
        if(alertDialog != null){
            if(alertDialog.isShowing()){
                alertDialog.dismiss();
            }
        }
        y = yPicker.getValue();
        m = mPicker.getValue();
        listener.onOk(y,m);
    }

    public void show(Listener listener){
        this.listener = listener;
        if(alertDialog != null){
            if(!alertDialog.isShowing()){
                alertDialog.show();
            }
        }
    }

    public void dispose(){
        calendar.clear();
        calendar = null;
        activity = null;
        alertDialog = null;
        yPicker = null;
        mPicker = null;
        cancel = null;
        ok = null;
    }
}
