package com.ictlao.android.app.timesheet.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;

import com.ictlao.android.app.timesheet.R;

import java.util.Calendar;

public class YMExportDialog {
    private int month = 0;
    private int year = 0;
    private Calendar calendar;
    private Activity activity;
    private AlertDialog alertDialog;
    private NumberPicker yPicker;
    private NumberPicker mPicker;
    private Button cancel;
    private Button export;
    private RadioButton real_time;
    private RadioButton modify_time;

    public interface Listener {
        void onExport(int year, int month, boolean is_real_time);

        default void onCancel(){

        }
    }

    private Listener listener;

    public YMExportDialog(Activity activity){
        this.activity = activity;
        calendar = Calendar.getInstance();
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        onInitialize();
    }

    private void onInitialize(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = activity.getLayoutInflater().inflate(R.layout.y_m_export_dialog_layout,null);
        yPicker = view.findViewById(R.id.yPicker);
        mPicker = view.findViewById(R.id.mPicker);
        cancel = view.findViewById(R.id.cancel);
        export = view.findViewById(R.id.export);
        real_time = view.findViewById(R.id.real_time);
        modify_time = view.findViewById(R.id.modify_time);
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
        export.setOnClickListener(view1 -> onOkClick());
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
        listener.onExport(y,m,real_time.isChecked());
    }

    public void show(int year,int month,Listener listener){
        this.listener = listener;
        if(alertDialog != null){
            if(!alertDialog.isShowing()){
                yPicker.setValue(year);
                mPicker.setValue(month);
                alertDialog.show();
            }
        }
    }

    public void setReal_time(boolean b){
        if(alertDialog != null){
            if(real_time != null){
                real_time.setChecked(b);
            }
        }
    }

    public void setModify_time(boolean b){
        if(alertDialog != null){
            if(modify_time != null){
                modify_time.setChecked(b);
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
        export = null;
        real_time = null;
        modify_time = null;
    }
}
