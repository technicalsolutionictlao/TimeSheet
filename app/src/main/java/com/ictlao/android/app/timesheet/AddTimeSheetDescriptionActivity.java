package com.ictlao.android.app.timesheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ictlao.android.app.timesheet.Items.TimeSheetItems;
import com.ictlao.android.app.timesheet.Manager.DataManager;
import com.ictlao.android.app.timesheet.Manager.InternetConnection;
import com.ictlao.android.app.timesheet.Message.LoadingProgress;
import com.ictlao.android.app.timesheet.Message.Message;

import java.util.Objects;

public class AddTimeSheetDescriptionActivity extends AppCompatActivity {
    // collect data from another file
    public static TimeSheetItems timeSheetItems;
    public static String push = "";

    // controls
    private TextView mDateTime;
    private EditText mDescription;
    private Button mConformButton;

    // internet connection function
    private InternetConnection mConnection;
    // progress dialog
    private LoadingProgress mProgress;
    // toast message
    private Message mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time_sheet_description);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_add_description);

        mDateTime = findViewById(R.id.date_time);
        mDescription = findViewById(R.id.description);
        mConformButton = findViewById(R.id.confirm_button);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mConnection = new InternetConnection(this);
        mProgress = new LoadingProgress(this);
        mMessage = new Message(this);
        onInitialize();
    }

    // initialize data to controls
    private void onInitialize(){
        mDateTime.setText(String.format("%s %s",timeSheetItems.getDate(),timeSheetItems.getCheckInTime()));
        mDescription.setText(timeSheetItems.getDescription());
        mDescription.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_GO){
                mConformButton.performClick();
                return true;
            }
            return false;
        });
        mConformButton.setOnClickListener(view -> onDescription());
    }

    // check description empty when user press confirm button
    private void onDescription(){
        String description = mDescription.getText().toString();
        String message = "";
        if(!description.equals("")){
            if(mConnection.isConnected()){
                mDescription.getText().clear();
                mProgress.show();
                onPostTimeSheetItems(description);
            }else {
                message = getString(R.string.connection_error);
            }
        }else{
            message = getString(R.string.hint_enter_reson);
        }

        if(!message.equals("")){
            mMessage.show(message);
        }
    }

    // set description to timesheet items then upload to database
    private void onPostTimeSheetItems(String description){
        timeSheetItems.setDescription(description);
        int year = DataManager.getYearYM(timeSheetItems.getDate());
        int month = DataManager.getMonthYM(timeSheetItems.getDate());
        DataManager.setTimeSheet(timeSheetItems,year,month,push);
        mProgress.dismiss();
        finish();
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
        mConnection.dispose();
        mMessage.dispose();
        mProgress.dispose();
    }
}