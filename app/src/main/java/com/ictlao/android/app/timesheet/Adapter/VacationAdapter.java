package com.ictlao.android.app.timesheet.Adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ictlao.android.app.timesheet.Items.VacationItems;
import com.ictlao.android.app.timesheet.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VacationAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<VacationItems> vacationItems;
    public VacationAdapter(Activity activity, ArrayList<VacationItems> vacationItems){
        this.activity = activity;
        this.vacationItems = vacationItems;
    }

    private static class Holder{
        public ImageView profile;
        public TextView name;
        public TextView date;
        public TextView time;
        public TextView condition;
    }

    @Override
    public int getCount() {
        return vacationItems.size();
    }

    @Override
    public Object getItem(int i) {
        return vacationItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        VacationItems items = (VacationItems) getItem(i);
        if(row == null){
            row = activity.getLayoutInflater().inflate(R.layout.adapter_vacation_layout,null);
            Holder holder = new Holder();
            holder.profile = row.findViewById(R.id.profile);
            holder.name = row.findViewById(R.id.name);
            holder.time = row.findViewById(R.id.time);
            holder.date = row.findViewById(R.id.date);
            holder.condition = row.findViewById(R.id.condition);
            row.setTag(holder);
        }
        Holder holder = (Holder) row.getTag();
        if(!items.getProfile_url().equals("")){
            Picasso.get().load(items.getProfile_url()).into(holder.profile);
        }
        holder.name.setText(items.getName());
        holder.date.setText(items.getDate());
        holder.time.setText(items.getTime());
        holder.condition.setText(items.getUseVacationCase());
        return row;
    }
}
