package com.ictlao.android.app.timesheet.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ictlao.android.app.timesheet.R;

public class FolderInfoDialog {
    private AlertDialog alertDialog;
    private TextView fileInfo;
    private Button Ok;

    public interface Listener {
        void onOk();
    }

    Listener listener;

    public FolderInfoDialog(Activity activity){
        onInitialize(activity);
    }

    private void onInitialize(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_folder_information_layout,null);
        fileInfo = view.findViewById(R.id.folder_info);
        Ok = view.findViewById(R.id.ok);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        Ok.setOnClickListener(view1 -> {
            listener.onOk();
            dismiss();
        });
    }

    public void show(String folderInfo,Listener listener){
        if(alertDialog != null){
            this.listener = listener;
            if(!alertDialog.isShowing()){
                fileInfo.setText(folderInfo);
                alertDialog.show();
            }
        }
    }

    private void dismiss(){
        if(alertDialog != null){
            if(alertDialog.isShowing()){
                alertDialog.dismiss();
            }
        }
    }

    public void dispose(){
        alertDialog = null;
        fileInfo = null;
        Ok = null;
        listener = null;
    }
}
