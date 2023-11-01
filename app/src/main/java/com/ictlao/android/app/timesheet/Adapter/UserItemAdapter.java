package com.ictlao.android.app.timesheet.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ictlao.android.app.timesheet.Items.DataResult;
import com.ictlao.android.app.timesheet.Items.UserItems;
import com.ictlao.android.app.timesheet.Manager.DataManager;
import com.ictlao.android.app.timesheet.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserItemAdapter extends BaseAdapter {
    private final ArrayList<UserItems> userItems;
    private final Activity activity;
    private boolean enable = false;
    public UserItemAdapter(Activity activity, ArrayList<UserItems> userItems){
        this.activity = activity;
        this.userItems = userItems;
    }

    public void NotifyNumberEnable(boolean enable){
        this.enable = enable;
    }

    private static class Holder {
        public ImageView profile;
        public TextView name;
        public TextView gmail;
        public TextView notify_number;
    }

    @Override
    public int getCount() {
        return userItems.size();
    }

    @Override
    public Object getItem(int i) {
        return userItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        UserItems items = (UserItems) getItem(i);
        if(row == null){
            row = activity.getLayoutInflater().inflate(R.layout.adapter_user_layout,null);
            Holder holder = new Holder();
            holder.profile = row.findViewById(R.id.profile);
            holder.name = row.findViewById(R.id.name);
            holder.gmail = row.findViewById(R.id.gmail);
            holder.notify_number = row.findViewById(R.id.notify_number);
            row.setTag(holder);
        }
        Holder holder = (Holder) row.getTag();
        if(!items.getProfile_url().equals("")){
            Picasso.get().load(items.getProfile_url()).into(holder.profile);
        }
        holder.name.setText(items.getName());
        holder.gmail.setText(items.getEmail());
        holder.notify_number.setVisibility(View.INVISIBLE);
        if(enable){
            holder.notify_number.setVisibility(View.VISIBLE);
            onNotify(holder.notify_number,items);
        }
        return row;
    }

    private void onNotify(TextView textView, UserItems items){
        DataManager.onNotifyNumberListener(items, new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                String txt = DataManager.getString(snapshot.getChildrenCount());
                textView.setText(txt);
                if(txt.equals("0")){
                    textView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onError(DatabaseError error) {

            }
        });
    }
}
