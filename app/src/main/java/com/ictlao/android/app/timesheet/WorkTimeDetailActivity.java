package com.ictlao.android.app.timesheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ictlao.android.app.timesheet.Dialog.WarningDialog;
import com.ictlao.android.app.timesheet.Dialog.YMDialog;
import com.ictlao.android.app.timesheet.Dialog.YMExportDialog;
import com.ictlao.android.app.timesheet.Items.DataResult;
import com.ictlao.android.app.timesheet.Items.TimeSheetItems;
import com.ictlao.android.app.timesheet.Items.UserItems;
import com.ictlao.android.app.timesheet.Items.VacationItems;
import com.ictlao.android.app.timesheet.Manager.CreateExcelFile;
import com.ictlao.android.app.timesheet.Manager.DataManager;
import com.ictlao.android.app.timesheet.Message.LoadingProgress;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class WorkTimeDetailActivity extends AppCompatActivity {

    // collect in another file
    public static UserItems userItems;

    // controls
    private ImageView mProfile;
    private TextView mName;
    private TextView mYM;
    private ImageView mCalendar;
    private TextView mAllTimeInMonth;
    private TextView mTotalTimeInMonth;
    private TextView mLostTimeInMonth;
    private TextView mExport;
    private RadioButton mRealTime;
    private RadioButton mModifyTime;

    // select year and month dialog
    private YMDialog ymDialog;
    // export year and month dialog
    private YMExportDialog ymExportDialog;
    // progressbar dialog
    private LoadingProgress mProgress;
    // create excel file
    private CreateExcelFile createExcelFile;
    // warning dialog
    private WarningDialog warningDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_time_detail);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.working_time));

        mProfile = findViewById(R.id.profile);
        mName = findViewById(R.id.name);
        mYM = findViewById(R.id.ym);
        mCalendar = findViewById(R.id.calendar);
        mAllTimeInMonth = findViewById(R.id.all_time_in_month);
        mTotalTimeInMonth = findViewById(R.id.total_time);
        mLostTimeInMonth = findViewById(R.id.lost_time);
        mExport = findViewById(R.id.export);
        mRealTime = findViewById(R.id.real_time);
        mModifyTime = findViewById(R.id.modify_time);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ymDialog = new YMDialog(this);
        ymExportDialog = new YMExportDialog(this);
        mProgress = new LoadingProgress(this);
        createExcelFile = new CreateExcelFile(this);
        warningDialog = new WarningDialog(this);
        onInitialize();
    }

    // initialize data to controls
    private void onInitialize(){
        createExcelFile.onCreatePath();
        if(!userItems.getProfile_url().equals("")){
            Picasso.get().load(userItems.getProfile_url()).into(mProfile);
        }
        mName.setText(userItems.getName());
        mYM.setText(DataManager.getCurrentYM());
        mCalendar.setOnClickListener(view -> onYMDialog());
        mExport.setOnClickListener(view -> onExport());
        mRealTime.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                onCheckChange(b);
            }
        });
        mModifyTime.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                onCheckChange(false);
            }
        });
        onSetAllTimeInMonth(DataManager.getCurrentYear(),DataManager.getCurrentMonth());
        onTimeSheetListener(DataManager.getCurrentYear(),DataManager.getCurrentMonth(),mRealTime.isChecked());
    }

    // radio check change
    private void onCheckChange(boolean isReal_time){
        String ym = mYM.getText().toString();
        int y = DataManager.getYearYM(ym);
        int m = DataManager.getMonthYM(ym);
        onTimeSheetListener(y,m,isReal_time);
    }

    // show export year and month dialog
    private void onExport(){
        String ym = mYM.getText().toString();
        int y = DataManager.getYearYM(ym);
        int m = DataManager.getMonthYM(ym);
        ymExportDialog.show(y, m, (year, month, is_real_time) -> {
            mProgress.show();
            onVacationListener(year, month, is_real_time);
        });
        ymExportDialog.setModify_time(IsModifyTimeCheck());
        ymExportDialog.setReal_time(IsRealTimeCheck());
    }

    private boolean IsRealTimeCheck(){
        return mRealTime.isChecked();
    }

    private boolean IsModifyTimeCheck(){
        return mModifyTime.isChecked();
    }

    // load vacation data
    private void onVacationListener(int year,int month,boolean is_real_time){
        String ym = mYM.getText().toString();
        DataManager.onVacationListener(userItems.getUserId(),ym, new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                ArrayList<VacationItems> list = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    VacationItems items = data.getValue(VacationItems.class);
                    if(items != null){
                        list.add(items);
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

    // load time sheet data
    private void onTimeSheetListener(ArrayList<VacationItems> vacationItems, int year,int month, boolean is_real_time){
        DataManager.onTimeSheetListener(userItems.getUserId(), year, month, new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                ArrayList<TimeSheetItems> list = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    TimeSheetItems items = data.getValue(TimeSheetItems.class);
                    if(items != null){
                        if(!is_real_time) {
                            String check_in_time = DataManager.getRuleTime(items.getCheckInTime(),DataManager.CHECK_IN);
                            String check_out_time = DataManager.getRuleTime(items.getCheckOutTime(),DataManager.CHECK_OUT);
                            items.setCheckInTime(check_in_time);
                            items.setCheckOutTime(check_out_time);
                        }
                        list.add(items);
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
        ArrayList<UserItems> users = new ArrayList<>();
        users.add(userItems);
        String fileName = DataManager.getExcelFileName(userItems.getName());
        String message = createExcelFile.onCreateTotalTime(users,timeSheetItems,vacationItems,year,month,fileName);
        mProgress.dismiss();
        String title = getString(R.string.title_warning);
        String description = createExcelFile.getDisplayPath();
        int icon = 0;
        if(message.equals(CreateExcelFile.Failed))icon = WarningDialog.Warning;
        if(message.equals(CreateExcelFile.Success))icon = WarningDialog.Success;
        if(message.equals("")){
            icon = WarningDialog.Warning;
            description = DataManager.getWithDash(year,month)+getString(R.string.out_of_service);
        }
        warningDialog.show(icon, title, description, () -> {
            //
        });
    }

    // load time sheet items
    private void onTimeSheetListener(int year, int month,boolean isRealTime){
        DataManager.onTimeSheetListener(userItems.getUserId(), year, month, new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                ArrayList<TimeSheetItems> list = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    TimeSheetItems items = data.getValue(TimeSheetItems.class);
                    if(items != null){
                        if(!isRealTime) {
                            String check_in_time = DataManager.getRuleTime(items.getCheckInTime(),DataManager.CHECK_IN);
                            String check_out_time = DataManager.getRuleTime(items.getCheckOutTime(),DataManager.CHECK_OUT);
                            items.setCheckInTime(check_in_time);
                            items.setCheckOutTime(check_out_time);
                        }
                        list.add(items);
                    }
                }
                onSetUserTotalTime(list,year,month);
            }

            @Override
            public void onError(DatabaseError error) {
                mProgress.dismiss();
            }
        });
    }

    // set total time to controls
    private void onSetUserTotalTime(ArrayList<TimeSheetItems> list,int year,int month){
        String total_time = DataManager.getTotalTime(list, year, month);
        String all_time = mAllTimeInMonth.getText().toString();
        mTotalTimeInMonth.setText(total_time);
        mLostTimeInMonth.setText(DataManager.getAllLostTimeInMonth(total_time,all_time));
    }

    // show year and month dialog
    private void onYMDialog(){
        ymDialog.show((year, month) -> {
            mYM.setText(DataManager.getWithDash(year,month));
            onSetAllTimeInMonth(year, month);
            onTimeSheetListener(year, month,mRealTime.isChecked());
        });
    }

    // set all work time in month to controls
    private void onSetAllTimeInMonth(int year, int month){
        mAllTimeInMonth.setText(DataManager.getAllTimeInMonth(year,month));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ymDialog.dispose();
        ymExportDialog.dispose();
    }
}