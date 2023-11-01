package com.ictlao.android.app.timesheet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
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
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ictlao.android.app.timesheet.Adapter.TimeSheetAdapter;
import com.ictlao.android.app.timesheet.Dialog.GPSLocationDialog;
import com.ictlao.android.app.timesheet.Items.DataResult;
import com.ictlao.android.app.timesheet.Items.MessageItems;
import com.ictlao.android.app.timesheet.Items.TimeSheetItems;
import com.ictlao.android.app.timesheet.Items.UserItems;
import com.ictlao.android.app.timesheet.Items.UserStatusItems;
import com.ictlao.android.app.timesheet.Items.WorkTimeItems;
import com.ictlao.android.app.timesheet.Manager.DataManager;
import com.ictlao.android.app.timesheet.Manager.DataMemory;
import com.ictlao.android.app.timesheet.Manager.GPSManager;
import com.ictlao.android.app.timesheet.Manager.GPSResult;
import com.ictlao.android.app.timesheet.Message.LoadingProgress;
import com.ictlao.android.app.timesheet.Message.Message;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // controls
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mNavigationView = null;

    // finger print ImageView
    private ImageView mFingerPrint;
    //private TextView mMessage;
    // card view
    private CardView cardView;

    // controls
    private TextView mVacationDay;
    private TextView mSickDay;

    // toast message
    private Message _message;
    // progressbar dialog
    private LoadingProgress mProgress;
    // gpsManager for tracking gps
    //private GPSManager gpsManager;
    // gps location dialog for take user to open gps
    //private GPSLocationDialog dialog;
    // location for store gps latitude and longitude
    //private Location location;

    // is for gps is running when user press on button
    private boolean isPress = false;
    // seconds for user back press
    private long seconds = 0;

    // controls
    private ListView mListView;
    private TextView mDate;

    // timesheet items form store data
    private ArrayList<TimeSheetItems> timeSheetItemsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.toolbar_home));

        DrawerLayout mDrawerLayout = findViewById(R.id.mainDrawerLayout);
        mNavigationView = findViewById(R.id.mainNavigationView);
        mFingerPrint = findViewById(R.id.fingerPrint);
        //mMessage = findViewById(R.id.message);
        cardView = findViewById(R.id.cardview);
        mListView = findViewById(R.id.listView);
        mDate = findViewById(R.id.date);

        mVacationDay = findViewById(R.id.vacation_day);
        mSickDay = findViewById(R.id.sick_day);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.nav_open, R.string.nav_close);

        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        _message = new Message(this);
        //dialog = new GPSLocationDialog(this);
        mProgress = new LoadingProgress(this);
        //gpsManager = new GPSManager(this);
        onInitialize();
    }

    // initialize data to controls
    @SuppressLint("NonConstantResourceId")
    private void onInitialize(){
        // navigation view menu
        mNavigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.vacation:
                    onStartActivity(VacationActivity.class);
                    break;
                case R.id.work_today:
                    onStartActivity(WorkTodayActivity.class);
                    break;
            }
            return true;
        });

        // header of the navigation menu
        View view = mNavigationView.getHeaderView(0);
        TextView name = view.findViewById(R.id.name);
        TextView email = view.findViewById(R.id.email);
        ImageView profile = view.findViewById(R.id.profile);
        name.setText(DataManager.getFirebaseUser().getDisplayName());
        email.setText(DataManager.getFirebaseUser().getEmail());
        if(DataManager.getFirebaseUser().getPhotoUrl() != null) {
            Picasso.get().load(DataManager.getFirebaseUser().getPhotoUrl()).into(profile);
        }
        mDate.setText(DataManager.getCurrentDate());
        // finger print event click listener
        mFingerPrint.setOnClickListener(view1 -> {
            onFingerPrint();

            // todo --remove gps 2023/05/17
            /*if(gpsManager.isGPSEnable()){
                if(location != null) {
                    onFingerPrint();
                }else{
                    isPress = true;
                    _message.show(getString(R.string.message_please_wait));
                }
            }else{
                dialog.show(new GPSLocationDialog.Listener() {
                    @Override
                    public void onOk() {

                    }

                    @Override
                    public void onClose() {

                    }
                });
            }*/
        });

        cardView.setOnClickListener(view1 -> {
            onFingerPrint();
            //todo -- remove gps 2023/05/17
/*            if(gpsManager.isGPSEnable()){
                if(location != null) {
                    onFingerPrint();
                }else{
                    isPress = true;
                    _message.show(getString(R.string.message_please_wait));
                }
            }else{
                dialog.show(new GPSLocationDialog.Listener() {
                    @Override
                    public void onOk() {

                    }

                    @Override
                    public void onClose() {

                    }
                });
            }*/
        });

        // gps manager on result call back
        /*gpsManager.onResult(new GPSResult() {
            @Override
            public void onLocationChange(Location _location) {
                if(_location != null){
                    location = _location;
                    if(isPress) {
                        isPress = false;
                        _message.show(getString(R.string.message_ready));
                    }
                }
            }

            @Override
            public void onGPSDisabled(boolean disable) {

            }
        });*/
        onTimeSheetListener();
        onLoadCurrentUser();
    }

    // on load users
    private void onLoadCurrentUser(){
        DataManager.onCurrentUserListener(new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                UserItems items = snapshot.getValue(UserItems.class);
                if(items != null){
                    DataManager.setUserItems(items);
                    mVacationDay.setText(DataManager.getString(items.getVacation_day()));
                    mSickDay.setText(DataManager.getString(items.getSick_day()));
                }
            }

            @Override
            public void onError(DatabaseError error) {

            }
        });
    }

    // load timesheet items
    private void onTimeSheetListener(){
        DataManager.onTimeSheetListener(new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                ArrayList<String> push = new ArrayList<>();
                ArrayList<TimeSheetItems> list = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    TimeSheetItems items = data.getValue(TimeSheetItems.class);
                    String p = data.getKey();
                    if(items != null){
                        list.add(items);
                        push.add(p);
                    }
                }
                onShowListView(list,push);
                if(push.size() > 0){
                    DataMemory.setPushKey(push.get(push.size() - 1));
                }
            }

            @Override
            public void onError(DatabaseError error) {

            }
        });
    }

    // show list view with data
    private void onShowListView(ArrayList<TimeSheetItems> list,ArrayList<String> pushes){
        timeSheetItemsArrayList = list;
        ArrayList<TimeSheetItems> timeSheetItems = DataManager.getTimeSheetItemsWith(list,DataManager.getCurrentDate());
        ArrayList<String> push = DataManager.getPushesWith(list,DataManager.getCurrentDate(),pushes);
        TimeSheetAdapter adapter = new TimeSheetAdapter(this,timeSheetItems){
            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                View row = super.getView(i, view, viewGroup);
                if(i % 2 == 1){
                    row.setBackgroundResource(R.drawable.list_gray);
                }else{
                    row.setBackgroundResource(R.drawable.sub_list_dark_gray);
                }
                Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_in_right);
                row.setAnimation(animation);
                return row;
            }
        };
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener((adapterView, view, i, l) -> {
            String p = push.get(i);
            AddTimeSheetDescriptionActivity.timeSheetItems = (TimeSheetItems) adapterView.getItemAtPosition(i);
            AddTimeSheetDescriptionActivity.push = p;
            onStartActivity(AddTimeSheetDescriptionActivity.class);
        });
    }

    // on finger print
    private void onFingerPrint(){
        mProgress.show();
        DataManager.onStatusListener(new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                String message = "";
                if(snapshot.hasChildren()){
                    UserStatusItems items = snapshot.getValue(UserStatusItems.class);
                    if(items != null){
                        message = getString(R.string.message_already_pressed)+items.getTime();
                        if(!DataManager.isPressAgain(items.getTime())) {
                            onCheckWorkTime(items);
                            message = "";
                        }
                    }
                }else {
                    UserStatusItems userStatusItems = new UserStatusItems(DataManager.getCurrentTime(),DataManager.getFirebaseUser().getUid(),
                            DataManager.CHECK_IN,DataManager.getCurrentTime(),DataManager.getCurrentDate());
                    TimeSheetItems timeSheetItems = new TimeSheetItems(DataManager.getId(),userStatusItems.getUserId(),DataManager.getCurrentDate(),
                            userStatusItems.getStatus(),DataManager.getCurrentTimeMarker(),DataManager.getCurrentTime(),0,0,
                            "",DataManager.getDefaultCheckOutTime(DataManager.getCurrentTime()),DataManager.getCurrentTimeMarker());
                    onSetTimeSheet(userStatusItems,timeSheetItems, false);
                    message = getString(R.string.message_already_pressed)+userStatusItems.getTime();
                }
                if(!message.equals(""))_message.show(message);
                mProgress.dismiss();
            }

            @Override
            public void onError(DatabaseError error) {
                mProgress.dismiss();
            }
        });
    }

    // check if user sigin and signout and then upload user to database
    private void onCheckWorkTime(UserStatusItems userStatusItems){
        boolean isCheckIn = userStatusItems.getStatus().equals(DataManager.CHECK_IN);
        String check = DataManager.CHECK_IN;
        boolean isUpdate = false;
        boolean isOverTime = false;
        TimeSheetItems items = new TimeSheetItems(DataManager.getId(),userStatusItems.getUserId(),DataManager.getCurrentDate(),
                check,DataManager.getCurrentTimeMarker(),DataManager.getCurrentTime(),0,0,
                "",DataManager.getDefaultCheckOutTime(DataManager.getCurrentTime()),DataManager.getCurrentTimeMarker());
        if(timeSheetItemsArrayList != null){
            if(timeSheetItemsArrayList.size() > 0){
                if(DataManager.IsToday(userStatusItems.getDate())){
                    if(isCheckIn){
                        if(!DataManager.IsNearlyIn(DataManager.getCurrentTime(),
                                timeSheetItemsArrayList.get(timeSheetItemsArrayList.size() - 1))){
                            if(!DataMemory.getPushKey().equals("")){
                                check = DataManager.CHECK_OUT;
                                isUpdate = true;
                                items = timeSheetItemsArrayList.get(timeSheetItemsArrayList.size() - 1);
                                items.setCheckStatus(check);
                                items.setCheckOutTime(DataManager.getCurrentTime());
                                items.setTimeOutMarker(DataManager.getCurrentTimeMarker());
                            }
                        }
                        if(DataManager.IsOverTime(DataManager.getCurrentTime())){
                            if(!DataMemory.getPushKey().equals("")){
                                check = DataManager.CHECK_OUT;
                                isUpdate = true;
                                items = timeSheetItemsArrayList.get(timeSheetItemsArrayList.size() - 1);
                                items.setCheckStatus(check);
                                items.setCheckOutTime(DataManager.getCurrentTime());
                                items.setTimeOutMarker(DataManager.getCurrentTimeMarker());
                            }
                        }
                    }else{
                        isOverTime = DataManager.IsOverTime(DataManager.getCurrentTime());
                    }
                }
            }
        }
        if(!isOverTime) {
            userStatusItems.setStatus(check);
            userStatusItems.setDate(DataManager.getCurrentDate());
            userStatusItems.setTime(DataManager.getCurrentTime());
            onSetTimeSheet(userStatusItems, items, isUpdate);
        }
    }

    // save time sheet to database
    private void onSetTimeSheet(UserStatusItems userStatusItems,TimeSheetItems timeSheetItems,boolean isUpdate){
        if(isUpdate){
            /*int year = DataManager.getYearYM(timeSheetItems.getDate());
            int month = DataManager.getMonthYM(timeSheetItems.getDate());*/
            DataManager.setTimeSheet(timeSheetItems,timeSheetItems.getDate(),DataMemory.getPushKey());
        }else{
            DataManager.setTimeSheet(timeSheetItems);
        }
        String user_profile = Objects.requireNonNull(DataManager.getFirebaseUser().getPhotoUrl()).toString();
        MessageItems messageItems = new MessageItems(DataManager.getFirebaseUser().getDisplayName(),false,userStatusItems.getUserId(),DataManager.getCurrentTime(),DataManager.getCurrentDate(), user_profile,0,0);
        DataManager.setUserStatus(userStatusItems);
        onSetMessage(messageItems);
    }

    // deprecate api
    /*private void onSetTimeSheet(TimeSheetItems items){
        DataManager.setTimeSheet(items);
        MessageItems messageItems = new MessageItems(DataManager.getFirebaseUser().getDisplayName(),false,items.getUserId(),DataManager.getCurrentTime(),DataManager.getCurrentDate(), Objects.requireNonNull(DataManager.getFirebaseUser().getPhotoUrl()).toString(),location.getLatitude(),location.getLongitude());
        onSetMessage(messageItems);
    }*/

    // set message items to database
    private void onSetMessage(MessageItems items){
        DataManager.setNotificationMessage(items);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_layout,menu);
        MenuItem menuItem = menu.findItem(R.id.folder);
        menuItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mActionBarDrawerToggle.onOptionsItemSelected(item))return true;
        if(item.getItemId() == android.R.id.home)finish();
        if(item.getItemId() == R.id.account)onStartActivity(AccountActivity.class);
        return super.onOptionsItemSelected(item);
    }

    // start activity
    private void onStartActivity(Class next){
        Intent intent = new Intent(this, next);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onBackPressed() {
        if(seconds + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
        }else{
            _message.show(getString(R.string.press_again_to_exit));
        }
        seconds = seconds+System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _message.dispose();
        mProgress.dispose();
        //gpsManager.dispose();
        //dialog.dispose();
    }
}