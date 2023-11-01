package com.ictlao.android.app.timesheet.Adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ictlao.android.app.timesheet.Items.WorkTimeItems;
import com.ictlao.android.app.timesheet.R;

import java.util.ArrayList;

public class WorkTimeAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<WorkTimeItems> workTimeItems;

    private static class Holder {
        public TextView name;
        public TextView start_work;
        public TextView end_work;
    }

    public WorkTimeAdapter(Activity activity, ArrayList<WorkTimeItems> workTimeItems){
        this.activity = activity;
        this.workTimeItems = workTimeItems;
    }

    @Override
    public int getCount() {
        return workTimeItems.size();
    }

    @Override
    public Object getItem(int i) {
        return workTimeItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        WorkTimeItems items = (WorkTimeItems) getItem(i);
        if(row == null){
            row = activity.getLayoutInflater().inflate(R.layout.adapter_work_time_layout,null);
            Holder holder = new Holder();
            holder.name = row.findViewById(R.id.name);
            holder.start_work = row.findViewById(R.id.start_work);
            holder.end_work = row.findViewById(R.id.end_work);
            row.setTag(holder);
        }
        Holder holder = (Holder) row.getTag();
        holder.name.setText(items.getPart());
        holder.start_work.setText(items.getTimeIn());
        holder.end_work.setText(items.getTimeOut());
        return row;
    }
}
