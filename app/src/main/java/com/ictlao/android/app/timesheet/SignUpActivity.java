package com.ictlao.android.app.timesheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ictlao.android.app.timesheet.Items.AdminItems;
import com.ictlao.android.app.timesheet.Items.DataResult;
import com.ictlao.android.app.timesheet.Manager.DataManager;
import com.ictlao.android.app.timesheet.Manager.DataMemory;
import com.ictlao.android.app.timesheet.Manager.InternetConnection;
import com.ictlao.android.app.timesheet.Manager.SignInResult;
import com.ictlao.android.app.timesheet.Message.LoadingProgress;
import com.ictlao.android.app.timesheet.Message.Message;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    // controls
    private EditText mCompanyName;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private Button mSignUpButton;

    // internet connection
    private InternetConnection mConnection;
    // progressbar dialog
    private LoadingProgress mProgress;
    // toast message
    private Message mMessage;

    // firebase authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.sign_up));

        mCompanyName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mConfirmPassword = findViewById(R.id.confirm_password);
        mSignUpButton = findViewById(R.id.signUpButton);
        mAuth = FirebaseAuth.getInstance();
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
        mConfirmPassword.setOnEditorActionListener((textView, i, keyEvent) ->{
            if(i == EditorInfo.IME_ACTION_GO){
                mSignUpButton.performClick();
                return true;
            }
            return false;
        });

        mSignUpButton.setOnClickListener(view -> onSignUp());
    }

    // sign up
    private void onSignUp(){
        String company = mCompanyName.getText().toString();
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String confirm = mConfirmPassword.getText().toString();
        String message = getErrorMessage(company,email,password,confirm);
        if(message.equals("")){
            if(mConnection.isConnected()){
                mCompanyName.getText().clear();
                mEmail.getText().clear();
                mPassword.getText().clear();
                mConfirmPassword.getText().clear();
                mProgress.show();
                onCreateUsers(company,email,password);
            }else{
                message = getString(R.string.connection_error);
            }
        }
        if(!message.equals("")){
            mMessage.show(message);
        }
    }

    // get error message when user not filed the form
    private String getErrorMessage(String company,String email,String password,String confirm){
        String s = "";
        if(confirm.equals(""))s = getString(R.string.hint_confirm_password);
        if(password.equals(""))s = getString(R.string.hint_enter_password);
        if(!confirm.equals("") && !password.equals("")){
            if(!password.equals(confirm)){
                s = getString(R.string.hint_password_not_match);
            }
            if(password.length() < 6){
                s = getString(R.string.hint_password_not_enought);
            }
        }
        if(email.equals(""))s = getString(R.string.hint_enter_email);
        if(!email.equals("")){
            if(!DataManager.isEmailValid(email)){
                s = getString(R.string.hint_incorrect_email);
            }
        }
        if(company.equals(""))s = getString(R.string.hint_enter_company_name);
        return s;
    }

    // create new user
    private void onCreateUsers(String company,String email,String password){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                updateUI(mAuth.getCurrentUser(),company,email,password);
            }else{
                String message = Objects.requireNonNull(task.getException()).getMessage();
                if(message != null){
                    if(!message.equals(""))mMessage.show(message);
                }
                mProgress.dismiss();
            }
        });
    }

    // update ui depend on user status
    private void updateUI(FirebaseUser firebaseUser,String company,String email,String password){
        if(firebaseUser != null){
            DataManager.setFirebaseUser(firebaseUser);
            String url = "";
            if(firebaseUser.getPhotoUrl() != null)url = firebaseUser.getPhotoUrl().toString();
            AdminItems items = new AdminItems(company,email,password,
                    firebaseUser.getUid(),url,true,firebaseUser.getUid());
            DataManager.setCompany(items);
            onGetAdminItems();
        }else{
            mProgress.dismiss();
        }
    }

    // get admin items
    private void onGetAdminItems(){
        DataManager.onCompanyListener(new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                AdminItems items = snapshot.getValue(AdminItems.class);
                if(items != null){
                    DataMemory.setUser(items);
                    DataManager.setAdminItems(items);
                    onStartActivity(HomeActivity.class);
                }
                mProgress.dismiss();
            }

            @Override
            public void onError(DatabaseError error) {
                mProgress.dismiss();
            }
        });
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