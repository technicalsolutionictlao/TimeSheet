package com.ictlao.android.app.timesheet;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ictlao.android.app.timesheet.Items.AdminItems;
import com.ictlao.android.app.timesheet.Items.DataResult;
import com.ictlao.android.app.timesheet.Items.UserItems;
import com.ictlao.android.app.timesheet.Manager.DataManager;
import com.ictlao.android.app.timesheet.Manager.DataMemory;
import com.ictlao.android.app.timesheet.Manager.InternetConnection;
import com.ictlao.android.app.timesheet.Manager.SignInResult;
import com.ictlao.android.app.timesheet.Message.LoadingProgress;
import com.ictlao.android.app.timesheet.Message.Message;

import java.util.Objects;

public class LoginAdminActivity extends AppCompatActivity {

    // controls
    private BottomNavigationView mBottomNavigationView;
    private EditText mEmail;
    private EditText mPassword;
    private Button mLogin;
    private TextView mSignUp;

    // progressbar dialog
    private LoadingProgress mProgress;
    // toast message
    private Message mMessage;
    // internet connection
    private InternetConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);
        Objects.requireNonNull(getSupportActionBar()).hide();
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLogin = findViewById(R.id.adminLogin_button);
        mSignUp = findViewById(R.id.signUp);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mProgress = new LoadingProgress(this);
        mMessage = new Message(this);
        mConnection = new InternetConnection(this);
        onInitialize();
    }

    // initialize data to controls
    private void onInitialize(){
        mBottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.user)finish();
            return true;
        });
        mBottomNavigationView.setSelectedItemId(R.id.admin);
        mPassword.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_GO){
                mLogin.performClick();
                return true;
            }
            return false;
        });
        mLogin.setOnClickListener(view -> onLogin());
        mSignUp.setOnClickListener(view -> onSignUp());
    }

    // login with form data
    private void onLogin(){
        String userId = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String message = getErrorMessage(userId,password);
        if(message.equals("")){
            if(mConnection.isConnected()) {
                mEmail.getText().clear();
                mPassword.getText().clear();
                mProgress.show();
                onPost(userId, password);
            }
        }
        if(!message.equals("")){
            mMessage.show(message);
        }
    }

    // get error message when user not filed the form
    private String getErrorMessage(String userId,String password){
        String s = "";
        if(userId.equals(""))s = getString(R.string.hint_enter_email);
        if(password.equals(""))s = getString(R.string.hint_enter_password);
        return s;
    }

    // post data to sign in
    private void onPost(String userId,String password){
        DataManager.onAdminSignIn(userId, password, new SignInResult() {
            @Override
            public void onSuccess(FirebaseUser user) {
                if(user != null){
                    DataManager.setFirebaseUser(user);
                    onCheckCompany();
                }else{
                    mMessage.show(getString(R.string.message_incorrect_email_and_password));
                }
                mProgress.dismiss();
            }

            @Override
            public void onFail(String error) {
                mMessage.show(getString(R.string.message_incorrect_email_and_password));
                mProgress.dismiss();
            }
        });
    }

    // check if it is a company user
    private void onCheckCompany(){
        DataManager.onCompanyListener(new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                AdminItems items = snapshot.getValue(AdminItems.class);
                if(items != null){
                    if(items.isEnable()){
                        // String profile = "";
                        // if(user.getPhotoUrl() != null)profile = user.getPhotoUrl().toString();
                        // AdminItems items = new AdminItems("",user.getEmail(),password,user.getUid(),profile,true,user.getUid());
                        DataMemory.setUser(items);
                        DataManager.setAdminItems(items);
                        onStartActivity(HomeActivity.class);
                    }else{
                        WarningActivity.adminItems = items;
                        onWaringActivity();
                    }
                }
            }

            @Override
            public void onError(DatabaseError error) {
                mProgress.dismiss();
            }
        });
    }

    // start sign up form
    private void onSignUp(){
        onStartActivity();
    }

    // start activity sign up
    private void onStartActivity(){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // start activity
    private void onStartActivity(Class next){
        finish();
        Intent intent = new Intent(this, next);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if(DataMemory.getAllowNotify()) {
            startService(new Intent(this, NotificationService.class));
        }
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // start warning acivity when user has been suspaned
    private void onWaringActivity(){
        Intent intent = new Intent(this, WarningActivity.class);
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
        mMessage.dispose();
        mConnection.dispose();
    }
}