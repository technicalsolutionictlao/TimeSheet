package com.ictlao.android.app.timesheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ictlao.android.app.timesheet.Adapter.TimeSheetAdapter;
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

import java.util.ArrayList;
import java.util.Objects;

public class UserTimeSheetInformationActivity extends AppCompatActivity {

    // collect data in another file
    public static UserItems userItems;

    // controls
    private ListView listView;
    private TextView textView;
    private TextView none_service;
    private TextView date;
    private ImageView calendar;

    // select year and month dialog
    private YMDialog ymDialog;
    // export year and month dialog
    private YMExportDialog ymExportDialog;
    // progressbar dialog
    private LoadingProgress progress;
    // create excel file
    private CreateExcelFile excelFile;

    //private Message mMessage;
    // warning dialog
    private WarningDialog warningDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_time_sheet_information);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_user_time_sheet);

        listView = findViewById(R.id.listView);
        textView = findViewById(R.id.export_all);
        none_service = findViewById(R.id.none_service);
        date = findViewById(R.id.date);
        calendar = findViewById(R.id.calendar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ymDialog = new YMDialog(this);
        progress = new LoadingProgress(this);
        excelFile = new CreateExcelFile(this);
        //mMessage = new Message(this);
        warningDialog = new WarningDialog(this);
        ymExportDialog = new YMExportDialog(this);
        onInitialize();
    }

    // initialize data to controls
    private void onInitialize(){
        date.setText(DataManager.getCurrentYM());
        textView.setOnClickListener(view -> onLoadData());
        calendar.setOnClickListener(view -> onShowYMDialog());
        onDateSelected(DataManager.getCurrentYear(),DataManager.getCurrentMonth());
    }

    // show month and year dialog
    private void onShowYMDialog(){
        ymDialog.show(new YMDialog.Listener() {
            @Override
            public void onOk(int year, int month) {
                date.setText(DataManager.getWithDash(year,month));
                onDateSelected(year,month);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    // selected year and month
    private void onDateSelected(int year, int month){
        progress.show();
        DataManager.onTimeSheetListener(userItems.getUserId(),year,month,new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                ArrayList<TimeSheetItems> list = new ArrayList<>();
                ArrayList<String> pushKeys = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    String push = data.getKey();
                    TimeSheetItems items = data.getValue(TimeSheetItems.class);
                    if(items != null){
                        list.add(items);
                    }
                    if(push != null){
                        pushKeys.add(push);
                    }
                }
                if(list.size() > 0 && list.size() == pushKeys.size()){
                    onShowListView(list,pushKeys, year, month);
                }else{
                    onHideListView();
                }
                progress.dismiss();
            }

            @Override
            public void onError(DatabaseError error) {
                progress.dismiss();
            }
        });
    }

    // load data with date
    private void onLoadData(){
        String d = date.getText().toString();
        int y = DataManager.getYearYM(d);
        int m = DataManager.getMonthYM(d);
        ymExportDialog.show(y,m,new YMExportDialog.Listener() {
            @Override
            public void onExport(int year, int month, boolean is_real_time) {
                onLoaded(year,month,is_real_time);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    // load vacation data
    private void onLoaded(int year, int month, boolean is_real_time){
        progress.show();
        DataManager.onVacationListener(year, month, new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                ArrayList<VacationItems> list = new ArrayList<>();
                for(DataSnapshot data : snapshot.child(userItems.getUserId()).getChildren()){
                    VacationItems items = data.getValue(VacationItems.class);
                    if(items != null){
                        list.add(items);
                    }
                }
                onLoadTimeSheet(year,month,list,is_real_time);
            }

            @Override
            public void onError(DatabaseError error) {
                progress.dismiss();
            }
        });
    }

    // load time sheet
    private void onLoadTimeSheet(int year, int month, ArrayList<VacationItems> vacationItems,boolean is_real_time){
        DataManager.onTimeSheetListener(year, month, new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                ArrayList<TimeSheetItems> list = new ArrayList<>();
                for(DataSnapshot data : snapshot.child(userItems.getUserId()).getChildren()){
                    TimeSheetItems items = data.getValue(TimeSheetItems.class);
                    if(items != null){
                        if(!is_real_time){
                            String check_in_time = DataManager.getRuleTime(items.getCheckInTime(),items.getCheckStatus());
                            String check_out_time = DataManager.getRuleTime(items.getCheckOutTime(),items.getCheckStatus());
                            items.setCheckInTime(check_in_time);
                            items.setCheckOutTime(check_out_time);
                        }
                        list.add(items);
                    }
                }
                onProcess(year,month,vacationItems,list,is_real_time);
            }

            @Override
            public void onError(DatabaseError error) {
                progress.dismiss();
            }
        });
    }

    // start create excel file
    private void onProcess(int year,int month,ArrayList<VacationItems> vacationItems,
                           ArrayList<TimeSheetItems> timeSheetItems,boolean is_real_time){
        boolean b = excelFile.onCreatePath();
        String message = "";
        if(b){
            if(timeSheetItems.size() > 0) {
                ArrayList<UserItems> _userItems = new ArrayList<>();
                _userItems.add(userItems);
                message = excelFile.onCreate(timeSheetItems, _userItems
                        ,DataManager.getExcelFileName(userItems.getName(), is_real_time),
                        year, month, vacationItems);
                //mMessage.show(message);
            }
        }
        progress.dismiss();
        String title = getString(R.string.title_warning);
        String description = excelFile.getDisplayPath();
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

    // show list view
    private void onShowListView(ArrayList<TimeSheetItems> timeSheetItems,
                                ArrayList<String> pushKeys, int year, int month){
        listView.setVisibility(View.VISIBLE);
        none_service.setVisibility(View.INVISIBLE);
        TimeSheetAdapter adapter = new TimeSheetAdapter(this, timeSheetItems){
            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                View row = super.getView(i, view, viewGroup);
                if(i % 2 == 1){
                    row.setBackgroundResource(R.drawable.list_gray);
                }else{
                    row.setBackgroundResource(R.drawable.sub_list_dark_gray);
                }
                return row;
            }
        };
        listView.setAdapter(adapter);
        /*listView.setOnItemClickListener((adapterView, view, i, l) -> {
            TimeSheetItems items = (TimeSheetItems) adapterView.getItemAtPosition(i);
            String push = pushKeys.get(i);
        });*/
    }

    // hide list view
    private void onHideListView(){
        listView.setVisibility(View.INVISIBLE);
        none_service.setVisibility(View.VISIBLE);
    }

    /*private void onStartActivity(Class next){
        Intent intent = new Intent(this, next);
        startActivity(intent);
    }*/

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
        progress.dispose();
        ymExportDialog.dispose();
        warningDialog.dispose();
    }
}