package com.ictlao.android.app.timesheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.Switch;

import com.ictlao.android.app.timesheet.Adapter.WorkTimeAdapter;
import com.ictlao.android.app.timesheet.Items.WorkTimeItems;
import com.ictlao.android.app.timesheet.Manager.DataManager;
import com.ictlao.android.app.timesheet.Manager.DataMemory;

import java.util.ArrayList;
import java.util.Objects;

public class SettingActivity extends AppCompatActivity {

    // controls
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch mAllowNotify;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.setting);

        mAllowNotify = findViewById(R.id.allow_notify);
        mListView = findViewById(R.id.listView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        onInitialize();
    }

    // initialize data to controls
    private void onInitialize(){
        ArrayList<WorkTimeItems> list = DataManager.Part.getDefault();
        WorkTimeAdapter adapter = new WorkTimeAdapter(this, list){
            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                View row = super.getView(i, view, viewGroup);
                if(i % 2 == 1){
                    row.setBackgroundResource(R.drawable.list_gray);
                }else{
                    row.setBackgroundResource(R.drawable.sub_list_dark_gray);
                }
                Animation animation = AnimationUtils.loadAnimation(SettingActivity.this, R.anim.slide_in_right);
                row.setAnimation(animation);
                return row;
            }
        };
        mListView.setAdapter(adapter);
        mAllowNotify.setOnCheckedChangeListener((compoundButton, b) -> {
            DataMemory.setAllowNotify(b);
            if(DataMemory.getAllowNotify()){
                startService(new Intent(this,NotificationService.class));
            }else{
                stopService(new Intent(this, NotificationService.class));
            }
        });
        mAllowNotify.setChecked(DataMemory.getAllowNotify());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}