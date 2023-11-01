package com.ictlao.android.app.timesheet.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ictlao.android.app.timesheet.Items.YMItems;
import com.ictlao.android.app.timesheet.R;

import java.util.List;

public class YMRecyclerViewAdapter extends RecyclerView.Adapter<YMRecyclerViewAdapter.Holder> {
    private static final int gray = R.drawable.ic_circle_gray;
    private static final int green = R.drawable.ic_circle_green;

    private List<YMItems> ymItems;
    private static TextView province;
    //private static ImageView selected;

    public interface listener
    {
        void onClick(YMItems items);
    }

    private static listener listener = null;

    public YMRecyclerViewAdapter(List<YMItems> ymItems, listener _listener){
        this.ymItems = ymItems;
        listener = _listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_month_selection_layout,parent,false);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        YMItems items = ymItems.get(position);
        province.setText(items.getYm());
        //selected.setImageResource(gray);
        province.setOnClickListener(view ->listener.onClick(items));
    }

    @Override
    public int getItemCount() {
        return ymItems.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        public Holder(@NonNull View itemView) {
            super(itemView);
            province = itemView.findViewById(R.id.ym);
            //selected = itemView.findViewById(R.id.selected_image);
            itemView.setOnClickListener(view -> {});
        }
    }
}
