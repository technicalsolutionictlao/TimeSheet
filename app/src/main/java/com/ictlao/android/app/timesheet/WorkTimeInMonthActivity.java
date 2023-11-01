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
import com.ictlao.android.app.timesheet.Dialog.WarningDialog;
import com.ictlao.android.app.timesheet.Dialog.YMExportDialog;
import com.ictlao.android.app.timesheet.Items.DataResult;
import com.ictlao.android.app.timesheet.Items.TimeSheetItems;
import com.ictlao.android.app.timesheet.Items.UserItems;
import com.ictlao.android.app.timesheet.Items.VacationItems;
import com.ictlao.android.app.timesheet.Manager.CreateExcelFile;
import com.ictlao.android.app.timesheet.Manager.DataManager;
import com.ictlao.android.app.timesheet.Message.LoadingProgress;

import java.util.ArrayList;
import java.util.Objects;

public class WorkTimeInMonthActivity extends AppCompatActivity {

    // controls
    private TextView mYM;
    private TextView mExportAll;
    private ListView mListView;

    // progressbar dialog
    private LoadingProgress mProgress;
    // list of all users
    private ArrayList<UserItems> userItems;
    // export year and month dialog
    private YMExportDialog ymExportDialog;

    // warning dialog
    private WarningDialog warningDialog;
    // create excel file
    private CreateExcelFile createExcelFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_time_in_month);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.menu_work_time));

        mYM = findViewById(R.id.ym);
        mExportAll = findViewById(R.id.export_all);
        mListView = findViewById(R.id.listView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mProgress = new LoadingProgress(this);
        ymExportDialog = new YMExportDialog(this);
        createExcelFile = new CreateExcelFile(this);
        warningDialog = new WarningDialog(this);
        onInitialize();
    }

    // initialize data to controls
    private void onInitialize(){
        createExcelFile.onCreatePath();
        mProgress.show();
        DataManager.onUserChangeListener(new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                ArrayList<UserItems> list = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    UserItems items = data.getValue(UserItems.class);
                    if(items != null){
                        list.add(items);
                    }
                }
                onShowListView(list);
                userItems = list;
                mProgress.dismiss();
            }

            @Override
            public void onError(DatabaseError error) {
                mProgress.dismiss();
            }
        });
        mYM.setText(DataManager.getCurrentYM());
        mExportAll.setOnClickListener(view -> onExportDialog());
    }

    // show export year and month dialog
    private void onExportDialog(){
        ymExportDialog.show(
                DataManager.getCurrentYear(),
                DataManager.getCurrentMonth(),
                (year, month, is_real_time) -> {
                    mProgress.show();
                    onVacationListener(year,month,is_real_time);
        });
    }

    // load vacation items
    private void onVacationListener(int year,int month,boolean is_real_time){
        DataManager.onVacationListener(year,month, new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                ArrayList<VacationItems> list = new ArrayList<>();
                if(userItems != null) {
                    for(int i = 0; i < userItems.size(); i++) {
                        for (DataSnapshot data : snapshot.child(userItems.get(i).getUserId()).getChildren()) {
                            VacationItems items = data.getValue(VacationItems.class);
                            if (items != null) {
                                list.add(items);
                            }
                        }
                    }
                }
                onTimeSheetListener(list,year,month,is_real_time);
            }

            @Override
            public void onError(DatabaseError error) {
                mProgress.dismiss();
            }
        });
    }

    // load time sheet
    private void onTimeSheetListener(ArrayList<VacationItems> vacationItems, int year,int month, boolean is_real_time){
        DataManager.onTimeSheetListener(year, month, new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                ArrayList<TimeSheetItems> list = new ArrayList<>();
                if(userItems != null) {
                    for(int i = 0; i < userItems.size(); i++) {
                        for (DataSnapshot data : snapshot.child(userItems.get(i).getUserId()).getChildren()) {
                            TimeSheetItems items = data.getValue(TimeSheetItems.class);
                            if (items != null) {
                                if (!is_real_time) {
                                    String check_in_time = DataManager.getRuleTime(items.getCheckInTime(),DataManager.CHECK_IN);
                                    String check_out_time = DataManager.getRuleTime(items.getCheckOutTime(),DataManager.CHECK_OUT);
                                    items.setCheckInTime(check_in_time);
                                    items.setCheckOutTime(check_out_time);
                                }
                                list.add(items);
                            }
                        }
                    }
                }
                onProcess(vacationItems,list,year,month);
            }

            @Override
            public void onError(DatabaseError error) {
                mProgress.dismiss();
            }
        });
    }

    // start create excel file
    private void onProcess(ArrayList<VacationItems> vacationItems,
                           ArrayList<TimeSheetItems> timeSheetItems,
                           int year,int month){
        mProgress.dismiss();
        if(userItems != null) {
            String fileName = DataManager.getExcelFileName();
            String message = createExcelFile.onCreateTotalTime(userItems, timeSheetItems, vacationItems, year, month, fileName);
            String title = getString(R.string.title_warning);
            String description = createExcelFile.getDisplayPath();
            int icon = 0;
            if (message.equals(CreateExcelFile.Failed)) icon = WarningDialog.Warning;
            if (message.equals(CreateExcelFile.Success)) icon = WarningDialog.Success;
            if (message.equals("")) {
                icon = WarningDialog.Warning;
                description = DataManager.getWithDash(year, month) + getString(R.string.out_of_service);
            }
            warningDialog.show(icon, title, description, () -> {
                //
            });
        }
    }

    // show list view
    private void onShowListView(ArrayList<UserItems> list){
        UserItemAdapter adapter = new UserItemAdapter(this, list){
            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                View row = super.getView(i, view, viewGroup);
                if(i % 2 == 1){
                    row.setBackgroundResource(R.drawable.list_gray);
                }else {
                    row.setBackgroundResource(R.drawable.sub_list_dark_gray);
                }
                Animation animation = AnimationUtils.loadAnimation(WorkTimeInMonthActivity.this, R.anim.slide_in_right);
                row.setAnimation(animation);
                return row;
            }
        };
        adapter.NotifyNumberEnable(false);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener((adapterView, view, i, l) -> {
            WorkTimeDetailActivity.userItems = (UserItems) adapterView.getItemAtPosition(i);
            onStartActivity(WorkTimeDetailActivity.class);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)finish();
        return super.onOptionsItemSelected(item);
    }

    // start activity
    private void onStartActivity(Class next){
        Intent intent = new Intent(this, next);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgress.dispose();
        ymExportDialog.dispose();
        warningDialog.dispose();
    }
}