package com.ictlao.android.app.timesheet.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ictlao.android.app.timesheet.R;

public class WarningDialog {
    public static final int Success = R.drawable.ic_success;
    public static final int Warning = R.drawable.ic_warning;
    public static final int Error = R.drawable.ic_error;

    private AlertDialog alertDialog;

    private ImageView icon;
    private TextView title;
    private TextView description;
    private Button ok;

    public interface Listener {
        void onOk();
    }

    private Listener listener;

    public WarningDialog(Activity activity){
        onInitialize(activity);
    }

    private void onInitialize(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = activity.getLayoutInflater().inflate(R.layout.warning_dialog_layout,null);
        icon = view.findViewById(R.id.icon);
        title = view.findViewById(R.id.title);
        description = view.findViewById(R.id.description);
        ok = view.findViewById(R.id.ok);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        ok.setOnClickListener(view1 -> onOk());
    }

    private void onOk(){
        dismiss();
        listener.onOk();
    }

    private void dismiss(){
        if(alertDialog != null){
            if(alertDialog.isShowing()){
                alertDialog.dismiss();
            }
        }
    }

    public void show(int icon,String title,String description, Listener listener){
        this.listener = listener;
        if(alertDialog != null){
            if(!alertDialog.isShowing()){
                this.icon.setImageResource(icon);
                this.title.setText(title);
                this.description.setText(description);
                alertDialog.show();
            }
        }
    }

    public void dispose(){
        alertDialog = null;
        icon = null;
        title = null;
        description = null;
        ok = null;
    }
}
