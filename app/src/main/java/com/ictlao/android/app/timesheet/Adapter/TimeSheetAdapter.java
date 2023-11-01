package com.ictlao.android.app.timesheet.Adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ictlao.android.app.timesheet.Items.TimeSheetItems;
import com.ictlao.android.app.timesheet.Manager.DataManager;
import com.ictlao.android.app.timesheet.R;

import java.util.ArrayList;

public class TimeSheetAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<TimeSheetItems> timeSheetItems;
    public TimeSheetAdapter(Activity activity, ArrayList<TimeSheetItems> timeSheetItems){
        this.activity = activity;
        this.timeSheetItems = timeSheetItems;
    }

    private static class Holder {
        TextView check_status;
        TextView date;
        TextView time;
        TextView marker;
    }

    @Override
    public int getCount() {
        return timeSheetItems.size();
    }

    @Override
    public Object getItem(int i) {
        return timeSheetItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        TimeSheetItems items = (TimeSheetItems) getItem(i);
        if(row == null){
            row = activity.getLayoutInflater().inflate(R.layout.adapter_time_sheet_layout,null);
            Holder holder = new Holder();
            holder.check_status = row.findViewById(R.id.check_status);
            holder.date = row.findViewById(R.id.date);
            holder.time = row.findViewById(R.id.time);
            holder.marker = row.findViewById(R.id.marker);
            row.setTag(holder);
        }
        Holder holder = (Holder) row.getTag();
        holder.check_status.setText(DataManager.getStatusToShow(items.getCheckStatus()));
        holder.date.setText(items.getDate());
        if(items.getCheckStatus().equals(DataManager.CHECK_IN)){
            holder.time.setText(items.getCheckInTime());
            holder.marker.setText(items.getTimeInMarker());
        }else{
            holder.time.setText(items.getCheckOutTime());
            holder.marker.setText(items.getTimeOutMarker());
        }

        return row;
    }
}
