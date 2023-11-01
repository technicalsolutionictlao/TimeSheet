package com.ictlao.android.app.timesheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.ictlao.android.app.timesheet.Items.UserItems;
import com.ictlao.android.app.timesheet.Manager.DataManager;
import com.ictlao.android.app.timesheet.Manager.InternetConnection;
import com.ictlao.android.app.timesheet.Message.LoadingProgress;
import com.ictlao.android.app.timesheet.Message.Message;

import java.util.Objects;

public class AddVacationDayActivity extends AppCompatActivity {

    // collect data in another file
    public static UserItems userItems;

    // controls
    private EditText mVacationDay;
    private EditText mSickDay;
    private Button mConfirm;

    // toast message
    private Message mMessage;
    // internet connection
    private InternetConnection mConnection;
    // progress dialog
    private LoadingProgress mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vacation_day);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.vacation));

        mVacationDay = findViewById(R.id.vacationAYear);
        mSickDay = findViewById(R.id.sickAYear);
        mConfirm = findViewById(R.id.confirm_button);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMessage = new Message(this);
        mConnection = new InternetConnection(this);
        mProgress = new LoadingProgress(this);
        onInitialize();
    }

    // initialize data to controls
    private void onInitialize(){
        mVacationDay.setText(String.format("%s",userItems.getVacation_day()));
        mSickDay.setText(String.format("%s",userItems.getSick_day()));
        mSickDay.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_GO){
                mConfirm.performClick();
                return true;
            }
            return false;
        });
        mConfirm.setOnClickListener(view -> onSave());
    }

    // check data empty when user press confirm button
    private void onSave(){
        mProgress.show();
        String vacation_day = mVacationDay.getText().toString();
        String sick_day = mSickDay.getText().toString();
        String message = getErrorMessage(vacation_day,sick_day);
        if(message.equals("")){
            if(mConnection.isConnected()){
                mVacationDay.getText().clear();
                mSickDay.getText().clear();
                userItems.setVacation_day(DataManager.getInteger(vacation_day));
                userItems.setSick_day(DataManager.getInteger(sick_day));
                DataManager.updateUser(userItems);
                finish();
            }else {
                message = getString(R.string.connection_error);
            }
        }

        if(!message.equals("")){
            mMessage.show(message);
        }
        mProgress.dismiss();
    }

    // get error message when user not filed the form
    private String getErrorMessage(String vacation_day,String sick_day){
        String s = "";
        if(vacation_day.equals(""))s = getString(R.string.hint_vacation);
        if(sick_day.equals(""))s = getString(R.string.hint_sick);
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
        mProgress.dispose();
        mMessage.dispose();
        mConnection.dispose();
    }
}