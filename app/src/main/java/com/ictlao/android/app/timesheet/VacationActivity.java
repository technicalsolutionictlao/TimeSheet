package com.ictlao.android.app.timesheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ictlao.android.app.timesheet.Items.UserItems;
import com.ictlao.android.app.timesheet.Items.VacationItems;
import com.ictlao.android.app.timesheet.Manager.DataManager;
import com.ictlao.android.app.timesheet.Manager.InternetConnection;
import com.ictlao.android.app.timesheet.Message.LoadingProgress;
import com.ictlao.android.app.timesheet.Message.Message;

import java.util.Calendar;
import java.util.Objects;

public class VacationActivity extends AppCompatActivity {

    // controls
    private RadioButton mVacationOfYear;
    private RadioButton mSickOfYear;
    private RadioButton mNotSickAndVacation;
    private EditText mDayQTY;
    private EditText mReason;
    private EditText mStartDate;
    private EditText mEndDate;
    private Button mConfirm;

    //private TextView mVacationDay;
    //private TextView mSickDay;

    // toast message
    private Message mMessage;
    // progressbar dialog
    private LoadingProgress mProgress;
    // internet connection
    private InternetConnection mConnection;

    // date picker dialog
    private DatePickerDialog datePickerDialog;
    private int year = 0;
    private int month = 0;
    private int day = 0;
    private int date_picker = 0;
    // date call back
    private static final int DATE_START = 863;
    private static final int DATE_END = 376;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.toolbar_vacation));

        mVacationOfYear = findViewById(R.id.vacationAYear);
        mSickOfYear = findViewById(R.id.sickAYear);
        mNotSickAndVacation = findViewById(R.id.notUseVacationAndSick);
        mDayQTY = findViewById(R.id.day_qty);
        mReason = findViewById(R.id.reason);
        mStartDate = findViewById(R.id.date_start);
        mEndDate = findViewById(R.id.date_end);
        mConfirm = findViewById(R.id.confirm_button);
        //mVacationDay = findViewById(R.id.vacation_day);
        //mSickDay = findViewById(R.id.sick_day);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMessage = new Message(this);
        mProgress = new LoadingProgress(this);
        mConnection = new InternetConnection(this);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        onInitialize();
    }

    // initialize data to controls
    private void onInitialize(){
        mReason.setOnEditorActionListener(((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_GO){
                mConfirm.performClick();
                return true;
            }
            return false;
        }));
        mConfirm.setOnClickListener(view -> onVacation());
        mStartDate.setText(DataManager.getCurrentDate());
        mEndDate.setText(DataManager.getCurrentDate());
        mStartDate.setOnClickListener(view -> onPickerDialog(DATE_START));
        mEndDate.setOnClickListener(view -> onPickerDialog(DATE_END));

        datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> {
            if(date_picker == DATE_START)onDateStart(year,(month+1),day);
            if(date_picker == DATE_END)onDateEnd(year,(month+1),day);
        },year,month,day);

        /*if(DataManager.getUserItems() != null){
            mVacationDay.setText(""+DataManager.getUserItems().getVacation_day());
            mSickDay.setText(""+DataManager.getUserItems().getSick_day());
        }*/
    }

    // show date picker dialog
    private void onPickerDialog(int date_picker){
        this.date_picker = date_picker;
        datePickerDialog.setTitle(getString(R.string.hint_select_date));
        datePickerDialog.show();
    }

    // set date to edit text
    private void onDateStart(int year, int month, int day){
        mStartDate.setText(DataManager.getWithDash(year, month, day));
    }

    // set date to edit text
    private void onDateEnd(int year, int month, int day){
        mEndDate.setText(DataManager.getWithDash(year, month, day));
    }

    // vacation click
    private void onVacation(){
        String condition = getVacationCondition(mVacationOfYear.isChecked(),mSickOfYear.isChecked(),mNotSickAndVacation.isChecked());
        String day_qty = mDayQTY.getText().toString();
        String reason = mReason.getText().toString();
        String start_date = mStartDate.getText().toString();
        String end_date = mEndDate.getText().toString();
        String message = getErrorMessage(day_qty,reason,condition);
        if(message.equals("")){
            mDayQTY.getText().clear();
            mReason.getText().clear();
            mStartDate.setText(DataManager.getCurrentDate());
            mEndDate.setText(DataManager.getCurrentDate());
            mProgress.show();
            onPostVacation(condition,day_qty,reason, start_date,end_date);
        }
        if(!message.equals("")){
            mMessage.show(message);
        }
    }

    // upload vacation data to database
    private void onPostVacation(String condition,String day_qty,String reason,String start_date,String end_date){
        VacationItems vacationItems = new VacationItems(DataManager.getCurrentDate()+DataManager.getCurrentTime(),
                DataManager.getCurrentDate(),DataManager.getCurrentTime(),condition,reason,DataManager.getFirebaseUser().getUid(),
                Objects.requireNonNull(DataManager.getFirebaseUser().getPhotoUrl()).toString(),DataManager.getFirebaseUser().getDisplayName(),
                DataManager.getFirebaseUser().getEmail(),day_qty,start_date,end_date);
        DataManager.setVacation(vacationItems);
        UserItems userItems = DataManager.getUserItems();
        if(userItems != null) {
            if (condition.equals(getString(R.string.vacation_of_the_year))) {
                int v = userItems.getVacation_day();
                int use = DataManager.getInteger(day_qty);
                int result = v - use;
                userItems.setVacation_day(result);
            }
            if (condition.equals(getString(R.string.vacation_sick_of_year))) {
                int s = userItems.getSick_day();
                int use = DataManager.getInteger(day_qty);
                int result = s - use;
                userItems.setSick_day(result);
            }
            DataManager.updateUser(userItems);
        }
        mMessage.show(getString(R.string.message_success));
        mProgress.dismiss();
    }

    // get condition vacation string
    private String getVacationCondition(boolean voy,boolean soy,boolean not){
        String s = "";
        if(voy)s = getString(R.string.vacation_of_the_year);
        if(soy)s = getString(R.string.vacation_sick_of_year);
        if(not)s = getString(R.string.no_use);
        return s;
    }

    // get error message when user not filed the form
    private String getErrorMessage(String day_qty, String reason, String condition){
        String s = "";
        if(reason.equals(""))s = getString(R.string.hint_enter_reson);
        if(day_qty.equals(""))s = getString(R.string.hint_vacation_qty);
        if(condition.equals(""))s = getString(R.string.title_vacation_condition);
        return s;
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
        mMessage.dispose();
        mProgress.dispose();
        mConnection.dispose();
    }
}