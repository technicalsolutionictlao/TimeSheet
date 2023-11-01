package com.ictlao.android.app.timesheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ictlao.android.app.timesheet.Adapter.NotificationAdapter;
import com.ictlao.android.app.timesheet.Items.DataResult;
import com.ictlao.android.app.timesheet.Items.MessageItems;
import com.ictlao.android.app.timesheet.Manager.DataManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class NotificationActivity extends AppCompatActivity {

    // controls
    private ListView mListView;
    private TextView mTextView;
    private ImageView mCalendar;
    private TextView mDate;

    // message list for store list of the message
    private ArrayList<MessageItems> messageItems;
    // date picker dialog
    private DatePickerDialog datePickerDialog;
    // year
    private int Year = 0;
    // month
    private int Month = 0;
    // day
    private int Day = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.toolbar_notification_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView = findViewById(R.id.listView);
        mTextView = findViewById(R.id.none_service);
        mTextView.setVisibility(View.INVISIBLE);
        mCalendar = findViewById(R.id.calendar);
        mDate = findViewById(R.id.date);

        messageItems = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Calendar calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        onInitialize();
    }

    // initialize data to controls
    @SuppressLint("SetTextI18n")
    private void onInitialize(){
        mDate.setText(DataManager.getCurrentDate());
        mDate.setOnClickListener(view -> onDatePickerDialogShow());
        mCalendar.setOnClickListener(view -> onDatePickerDialogShow());
        datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> {
            mDate.setText(DataManager.getWithDash(year,(month + 1),day));
            onSelectedDate(DataManager.getWithDash(year,(month + 1),day));
        },Year,Month,Day);
        DataManager.onNotificationMessageListener(new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                messageItems.clear();
                for(DataSnapshot data : snapshot.getChildren()){
                    MessageItems items = data.getValue(MessageItems.class);
                    if(items != null) {
                        messageItems.add(items);
                    }
                }
                onSelectedDate(DataManager.getCurrentDate());
            }

            @Override
            public void onError(DatabaseError error) {

            }
        });
    }

    // show picker dialog
    private void onDatePickerDialogShow(){
        datePickerDialog.setTitle("Select date");
        datePickerDialog.show();
    }

    // select the date
    private void onSelectedDate(String date){
        ArrayList<MessageItems> list = getMessageItems(date);
        if(list.size() > 0){
            onShowListView(list);
        }else{
            onShowTextView();
        }
    }

    // return date of today list
    private ArrayList<MessageItems> getMessageItems(String date){
        ArrayList<MessageItems> list = new ArrayList<>();
        for(int i = 0; i < messageItems.size(); i++){
            MessageItems items = messageItems.get(i);
            if(date.equals(items.getDate())){
                list.add(items);
            }
        }
        return list;
    }

    // show text view if data not exists
    private void onShowTextView(){
        mTextView.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.INVISIBLE);
    }

    // show list view if data exists
    private void onShowListView(ArrayList<MessageItems> list){
        mListView.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.INVISIBLE);
        NotificationAdapter adapter = new NotificationAdapter(this, list){
            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                View row = super.getView(i, view, viewGroup);
                if(i % 2 == 1){
                    row.setBackgroundResource(R.drawable.list_gray);
                }else{
                    row.setBackgroundResource(R.drawable.sub_list_dark_gray);
                }
                Animation animation = AnimationUtils.loadAnimation(NotificationActivity.this, R.anim.slide_in_right);
                row.setAnimation(animation);
                return row;
            }
        };
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener((adapterView, view, i, l) -> {
            NotificationInformationActivity.messageItems = (MessageItems) adapterView.getItemAtPosition(i);
            onStartActivity(NotificationInformationActivity.class);
        });
    }

    // start activity
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