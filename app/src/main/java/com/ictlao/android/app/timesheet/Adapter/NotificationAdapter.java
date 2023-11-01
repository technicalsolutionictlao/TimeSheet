package com.ictlao.android.app.timesheet.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ictlao.android.app.timesheet.Items.MessageItems;
import com.ictlao.android.app.timesheet.NotificationService;
import com.ictlao.android.app.timesheet.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

// notification adapter for listview messages
public class NotificationAdapter extends BaseAdapter {
    private final Activity activity;
    private final ArrayList<MessageItems> messageItems;
    public NotificationAdapter(Activity activity, ArrayList<MessageItems> messageItems){
        this.activity = activity;
        this.messageItems = messageItems;
    }

    // hold the controls in the single object.
    private static class Holder {
        public ImageView profile;
        public TextView name;
        public TextView time;
    }
    @Override
    public int getCount() {
        return messageItems.size();
    }

    @Override
    public Object getItem(int i) {
        return messageItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        MessageItems items = (MessageItems) getItem(i);
        if(row == null){
            row = activity.getLayoutInflater().inflate(R.layout.adapter_notification_message_layout,null);
            Holder holder = new Holder();
            holder.profile = row.findViewById(R.id.profile);
            holder.name = row.findViewById(R.id.name);
            holder.time = row.findViewById(R.id.time);
            row.setTag(holder);
        }
        Holder holder = (Holder) row.getTag();
        if(!items.getProfile_url().equals("")){
            Picasso.get().load(items.getProfile_url()).into(holder.profile);
        }
        holder.name.setText(items.getName());
        holder.time.setText(items.getTime());
        return row;
    }
}
