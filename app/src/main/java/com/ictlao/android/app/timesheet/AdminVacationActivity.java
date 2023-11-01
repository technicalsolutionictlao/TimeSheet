package com.ictlao.android.app.timesheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ictlao.android.app.timesheet.Adapter.UserItemAdapter;
import com.ictlao.android.app.timesheet.Adapter.VacationAdapter;
import com.ictlao.android.app.timesheet.Items.DataResult;
import com.ictlao.android.app.timesheet.Items.UserItems;
import com.ictlao.android.app.timesheet.Items.VacationItems;
import com.ictlao.android.app.timesheet.Manager.DataManager;

import java.util.ArrayList;
import java.util.Objects;

public class AdminVacationActivity extends AppCompatActivity {

    // controls
    private ListView mListView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_vacation);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_vacation);

        mListView = findViewById(R.id.listView);
        mTextView = findViewById(R.id.none_service);
        mTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        onInitialize();
    }

    // initialize data to controls
    private void onInitialize(){
        DataManager.onUserChangeListener(new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                ArrayList<UserItems> list = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    UserItems items = data.getValue(UserItems.class);
                    if(items != null) {
                        if(!items.getEmail().equals("")) {
                            list.add(items);
                        }
                    }
                }
                if(list.size() > 0){
                    onShowListView(list);
                }else{
                    onShowTextView();
                }
            }

            @Override
            public void onError(DatabaseError error) {

            }
        });
    }

    // show text view if data empty
    private void onShowTextView(){
        mTextView.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.INVISIBLE);
    }

    // show list view if has data
    private void onShowListView(ArrayList<UserItems> list){
        mListView.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.INVISIBLE);
        UserItemAdapter adapter = new UserItemAdapter(this,list){
            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                View row = super.getView(i, view, viewGroup);
                if(i % 2 == 1){
                    row.setBackgroundResource(R.drawable.list_gray);
                }else{
                    row.setBackgroundResource(R.drawable.sub_list_dark_gray);
                }
                Animation animation = AnimationUtils.loadAnimation(AdminVacationActivity.this,R.anim.slide_in_right);
                row.setAnimation(animation);
                return row;
            }
        };
        adapter.NotifyNumberEnable(true);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener((adapterView, view, i, l) -> {
            UserVacationActivity.userItems = (UserItems) adapterView.getItemAtPosition(i);
            onStartActivity(UserVacationActivity.class);
        });
    }

    // start activity with animation
    private void onStartActivity(Class next){
        Intent intent = new Intent(this, next);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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