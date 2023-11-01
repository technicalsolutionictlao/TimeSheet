package com.ictlao.android.app.timesheet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ictlao.android.app.timesheet.Adapter.UserItemAdapter;
import com.ictlao.android.app.timesheet.Adapter.YMRecyclerViewAdapter;
import com.ictlao.android.app.timesheet.Dialog.FolderInfoDialog;
import com.ictlao.android.app.timesheet.Dialog.WarningDialog;
import com.ictlao.android.app.timesheet.Dialog.YMExportDialog;
import com.ictlao.android.app.timesheet.Items.DataResult;
import com.ictlao.android.app.timesheet.Items.TimeSheetItems;
import com.ictlao.android.app.timesheet.Items.UserItems;
import com.ictlao.android.app.timesheet.Items.VacationItems;
import com.ictlao.android.app.timesheet.Items.YMItems;
import com.ictlao.android.app.timesheet.Manager.CreateExcelFile;
import com.ictlao.android.app.timesheet.Manager.DataManager;
import com.ictlao.android.app.timesheet.Message.LoadingProgress;
import com.ictlao.android.app.timesheet.Message.Message;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    // controls
    private TextView mDate;
    private ListView mListView;
    private RecyclerView mRecyclerView;
    private TextView mExportAll;

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mNavigationView;

    // export dialog
    private YMExportDialog ymDialog;
    // toast message
    private Message mMessage;
    // warning dialog
    private WarningDialog warningDialog;
    // progress bar dialog
    private LoadingProgress mProgress;

    // list of all users
    private ArrayList<UserItems> userItems;
    // create excel file
    private CreateExcelFile excelFile;
    // folder info dialog
    private FolderInfoDialog infoDialog;
    // second for back press
    private long seconds = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.toolbar_home);

        DrawerLayout mDrawerLayout = findViewById(R.id.mainDrawerLayout);
        mNavigationView = findViewById(R.id.mainNavigationView);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.nav_open, R.string.nav_close);

        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        mDate = findViewById(R.id.date);
        mListView = findViewById(R.id.listView);
        mRecyclerView = findViewById(R.id.recyclerView);
        mExportAll = findViewById(R.id.export_all);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ymDialog = new YMExportDialog(this);
        mProgress = new LoadingProgress(this);
        mMessage = new Message(this);
        warningDialog = new WarningDialog(this);
        excelFile = new CreateExcelFile(this);
        infoDialog = new FolderInfoDialog(this);
        onInitialize();
    }

    // initialize data to controls
    @SuppressLint("NonConstantResourceId")
    private void onInitialize(){
        mNavigationView.setNavigationItemSelectedListener((item -> {
            switch (item.getItemId()){
                case R.id.notification:
                    onStartActivity(NotificationActivity.class);
                    break;
                case R.id.vacation:
                    onStartActivity(AdminVacationActivity.class);
                    break;
                case R.id.work_time_in_month:
                    onStartActivity(WorkTimeInMonthActivity.class);
                    break;
                case R.id.settings:
                    onStartActivity(SettingActivity.class);
                    break;
            }
            return true;
        }));
        mDate.setText(DataManager.getCurrentDate());
        View view = mNavigationView.getHeaderView(0);
        TextView name = view.findViewById(R.id.name);
        TextView email = view.findViewById(R.id.email);
        ImageView profile = view.findViewById(R.id.profile);
        name.setText(DataManager.getAdminItems().getName());
        email.setText(DataManager.getAdminItems().getEmail());
        if(!DataManager.getAdminItems().getProfile_url().equals("")) {
            Picasso.get().load(DataManager.getAdminItems().getProfile_url()).into(profile);
        }
        DataManager.onYMListener(new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                ArrayList<YMItems> list = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    String ym = data.getKey();
                    if(ym != null){
                        YMItems items = new YMItems(ym);
                        list.add(items);
                    }
                }
                if(list.size() > 0){
                    onShowRecyclerView(list);
                }else {
                    onHideRecyclerView();
                }
            }

            @Override
            public void onError(DatabaseError error) {
                onHideRecyclerView();
            }
        });
        mExportAll.setOnClickListener(view1 -> onShowYMDialog());
        excelFile.onCreatePath();
        onShowListView();
    }

    // show select year and month dialog
    private void onShowYMDialog(){
        String d = mDate.getText().toString();
        int y = DataManager.getYearYM(d);
        int m = DataManager.getMonthYM(d);
        ymDialog.show(y,m,new YMExportDialog.Listener() {
            @Override
            public void onExport(int year, int month, boolean is_real_time) {
                onLoadingData(year, month, is_real_time);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    // load vacation data with year and month
    private void onLoadingData(int year, int month, boolean is_real_time){
        if(userItems != null) {
            mProgress.show();
            ArrayList<VacationItems> vacationItems = new ArrayList<>();
            DataManager.onVacationListener(year, month, new DataResult() {
                @Override
                public void onSuccess(DataSnapshot snapshot) {
                    for(int i = 0; i < userItems.size(); i++){
                        for(DataSnapshot data : snapshot.child(userItems.get(i).getUserId()).getChildren()){
                            VacationItems items = data.getValue(VacationItems.class);
                            if(items != null){
                                vacationItems.add(items);
                            }
                        }
                    }
                    onLoadVacation(year,month,vacationItems,is_real_time);
                }

                @Override
                public void onError(DatabaseError error) {
                    mProgress.dismiss();
                }
            });
        }
    }

    // load timesheet data after vacation data is loaded
    private void onLoadVacation(int year, int month, ArrayList<VacationItems> vacationItems,boolean is_real_time){
        ArrayList<TimeSheetItems> timeSheetItems = new ArrayList<>();
        DataManager.onTimeSheetListener(year, month, new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                for(int i = 0; i < userItems.size(); i++){
                    for(DataSnapshot data : snapshot.child(userItems.get(i).getUserId()).getChildren()){
                        TimeSheetItems items = data.getValue(TimeSheetItems.class);
                        if(items != null){
                            if(!is_real_time){
                                String check_in_time = DataManager.getRuleTime(items.getCheckInTime(),DataManager.CHECK_IN);
                                String check_out_time = DataManager.getRuleTime(items.getCheckOutTime(),DataManager.CHECK_OUT);
                                items.setCheckInTime(check_in_time);
                                items.setCheckOutTime(check_out_time);
                            }
                            timeSheetItems.add(items);
                        }
                    }
                }
                onProcess(userItems,timeSheetItems, year,month, vacationItems, is_real_time);
            }

            @Override
            public void onError(DatabaseError error) {
                mProgress.dismiss();
            }
        });
    }

    // start process to create excel file
    private void onProcess(ArrayList<UserItems> userItems,ArrayList<TimeSheetItems> timeSheetItems, int year, int month,
                           ArrayList<VacationItems> vacationItems, boolean is_real_time){
        String message = "";
        if(timeSheetItems.size() > 0){
            message = excelFile.onCreate(timeSheetItems,userItems,DataManager.getExcelFileName(is_real_time), year,month,vacationItems);
            //mMessage.show(message);
        }
        mProgress.dismiss();
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

    // hide recycler view if no data
    private void onHideRecyclerView(){
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    // show recycler view if has data
    private void onShowRecyclerView(ArrayList<YMItems> list){
        mRecyclerView.setVisibility(View.VISIBLE);
        YMRecyclerViewAdapter adapter = new YMRecyclerViewAdapter(list, items -> Log.e("T", items.getYm()));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);
    }

    // load data to show list view
    private void onShowListView(){
        DataManager.onUserChangeListener(new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                userItems = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    UserItems items = data.getValue(UserItems.class);
                    if(items != null) {
                        if(!items.getEmail().equals("")) {
                            userItems.add(items);
                        }
                    }
                }
                onShow(userItems);
            }

            @Override
            public void onError(DatabaseError error) {

            }
        });
    }

    // show list view
    private void onShow(ArrayList<UserItems> userItems){
        UserItemAdapter adapter = new UserItemAdapter(this, userItems){
            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
               View row =  super.getView(i, view, viewGroup);
               if(i % 2 == 1){
                   row.setBackgroundResource(R.drawable.list_gray);
               }else{
                   row.setBackgroundResource(R.drawable.sub_list_dark_gray);
               }
               Animation animation = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.slide_in_right);
               row.setAnimation(animation);
               return row;
            }
        };
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener((adapterView, view, i, l) -> {
            UserTimeSheetInformationActivity.userItems = (UserItems) adapterView.getItemAtPosition(i);
            onStartActivity(UserTimeSheetInformationActivity.class);
        });
    }

    // start activity
    private void onStartActivity(Class next){
        Intent intent = new Intent(this, next);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(mActionBarDrawerToggle.onOptionsItemSelected(item))return true;
        if(item.getItemId() == R.id.account)onStartActivity(AdminAccountActivity.class);
        if(item.getItemId() == R.id.folder)onShowFolderInfo();
        return super.onOptionsItemSelected(item);
    }

    // show folder info dialog
    private void onShowFolderInfo(){
        infoDialog.show(excelFile.getDisplayPath(), () -> {});
    }

    @Override
    public void onBackPressed() {
        if(seconds + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        }else{
            mMessage.show(getString(R.string.press_again_to_exit));
        }
        seconds = seconds + System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ymDialog.dispose();
        mProgress.dispose();
        mMessage.dispose();
        infoDialog.dispose();
        warningDialog.dispose();
    }
}