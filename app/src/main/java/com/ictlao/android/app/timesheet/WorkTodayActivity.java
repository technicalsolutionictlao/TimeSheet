package com.ictlao.android.app.timesheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ictlao.android.app.timesheet.Adapter.TimeSheetAdapter;
import com.ictlao.android.app.timesheet.Items.DataResult;
import com.ictlao.android.app.timesheet.Items.TimeSheetItems;
import com.ictlao.android.app.timesheet.Manager.DataManager;
import com.ictlao.android.app.timesheet.Message.LoadingProgress;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class WorkTodayActivity extends AppCompatActivity {

    // controls
    private TextView mDateTextView;
    private ImageView mCalendarImageView;
    private ListView mListView;

    private DatePickerDialog mDatePickerDialog;
    private int Year = 0;
    private int Month = 0;
    private int Day = 0;

    // progress dialog
    private LoadingProgress mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_today);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.work_today);

        mDateTextView = findViewById(R.id.date);
        mCalendarImageView = findViewById(R.id.calendar);
        mListView = findViewById(R.id.listView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Calendar calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        mProgress = new LoadingProgress(this);
        onInitialize();
    }

    // initialize data to controls
    private void onInitialize(){
        mDateTextView.setText(DataManager.getCurrentDate());
        mCalendarImageView.setOnClickListener(view -> onPickerDialogShow());
        mDatePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> {
            mDateTextView.setText(DataManager.getWithDash(year,(month+1),day));
            onTimeSheetListener(mDateTextView.getText().toString());
        },Year,Month,Day);
        onTimeSheetListener(DataManager.getCurrentDate());
    }

    // load timesheet items
    private void onTimeSheetListener(String date){
        mProgress.show();
        DataManager.onTimeSheetListener(DataManager.getFirebaseUser().getUid(), date, new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                ArrayList<TimeSheetItems> list = new ArrayList<>();
                ArrayList<String> pushes = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    TimeSheetItems items = data.getValue(TimeSheetItems.class);
                    String p = data.getKey();
                    if(items != null){
                        list.add(items);
                        pushes.add(p);
                    }
                }
                onShowListView(list,date,pushes);
                mProgress.dismiss();
            }

            @Override
            public void onError(DatabaseError error) {
                mProgress.dismiss();
            }
        });
    }

    // show date picker dialog
    private void onPickerDialogShow(){
        mDatePickerDialog.setTitle(getString(R.string.hint_select_date));
        mDatePickerDialog.show();
    }

    // show list view
    private void onShowListView(ArrayList<TimeSheetItems> list,String date,ArrayList<String> pushes){
        ArrayList<TimeSheetItems> timeSheetItems = DataManager.getTimeSheetItemsWith(list,date);
        ArrayList<String> push = DataManager.getPushesWith(list,date,pushes);
        TimeSheetAdapter adapter = new TimeSheetAdapter(this, timeSheetItems){
            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                View row = super.getView(i, view, viewGroup);
                if(i % 2 == 1){
                    row.setBackgroundResource(R.drawable.list_gray);
                }else{
                    row.setBackgroundResource(R.drawable.sub_list_dark_gray);
                }
                Animation animation = AnimationUtils.loadAnimation(WorkTodayActivity.this, R.anim.slide_in_right);
                row.setAnimation(animation);
                return row;
            }
        };
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener((adapterView, view, i, l) -> {
            AddTimeSheetDescriptionActivity.push = push.get(i);
            AddTimeSheetDescriptionActivity.timeSheetItems = (TimeSheetItems) adapterView.getItemAtPosition(i);
            onStartActivity(AddTimeSheetDescriptionActivity.class);
        });
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

    // start activity
    private void onStartActivity(Class next){
        Intent intent = new Intent(this, next);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgress.dispose();
    }
}