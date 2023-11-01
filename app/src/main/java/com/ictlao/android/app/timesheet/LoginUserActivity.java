package com.ictlao.android.app.timesheet;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ictlao.android.app.timesheet.Items.AdminItems;
import com.ictlao.android.app.timesheet.Items.DataResult;
import com.ictlao.android.app.timesheet.Items.UserItems;
import com.ictlao.android.app.timesheet.Manager.DataManager;
import com.ictlao.android.app.timesheet.Manager.DataMemory;
import com.ictlao.android.app.timesheet.Message.LoadingProgress;

import java.util.ArrayList;
import java.util.Objects;

public class LoginUserActivity extends AppCompatActivity {

    // call back sign in
    private static final int RC_SIGN_IN = 124;
    // firebase authentication
    private FirebaseAuth mAuth;
    // google sign in client
    private GoogleSignInClient mClient;
    // controls
    private BottomNavigationView mBottomNavigationView;
    // progressbar dialog
    private LoadingProgress mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);
        Objects.requireNonNull(getSupportActionBar()).hide();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        mClient = GoogleSignIn.getClient(this,gso);
        mAuth = FirebaseAuth.getInstance();

        SignInButton signInButton = findViewById(R.id.signInButton);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);

        signInButton.setOnClickListener(view -> signIn());
    }

    @Override
    public void onStart() {
        super.onStart();
        mProgress = new LoadingProgress(this);
        DataManager.setFirebaseUser(mAuth.getCurrentUser());
        updateUI(DataManager.getFirebaseUser());
    }

    // sign in
    private void signIn() {
        Intent signInIntent = mClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    // update ui depend on user status
    private void updateUI(FirebaseUser user){
        mProgress.show();
        if(user != null){
            if(DataMemory.isAdmin()){
                DataManager.setFirebaseUser(user);
                onGetCompany();
            }else {
                mProgress.dismiss();
                onStartActivity();
            }
        }else {
            mProgress.dismiss();
            onInitialize();
        }
    }

    // check if it is a company
    private void onGetCompany(){
        DataManager.onCompanyListener(new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                AdminItems items = snapshot.getValue(AdminItems.class);
                if(items != null){
                    DataManager.setAdminItems(items);
                    if(items.isEnable()) {
                        onStartAdminActivity();
                    }else{
                        // warning
                        WarningActivity.adminItems = items;
                        onStartActivity(WarningActivity.class);
                        finish();
                    }
                }
                mProgress.dismiss();
            }

            @Override
            public void onError(DatabaseError error) {
                mProgress.dismiss();
            }
        });
    }

    // initialize data to controls
    private void onInitialize(){
        mBottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.admin)onStartActivity(LoginAdminActivity.class);
            return true;
        });
        mBottomNavigationView.setSelectedItemId(R.id.user);
    }

    // add new user to firebase authentication
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null) {
                            DataManager.setFirebaseUser(user);
                            onCheckUser(user);
                        }else{
                            updateUI(null);
                        }
                    } else {
                        updateUI(null);
                    }
                });
    }

    // check user
    private void onCheckUser(FirebaseUser user){
        DataManager.onUserListener(new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                boolean isOld = false;
                for(DataSnapshot data : snapshot.getChildren()){
                    UserItems items = data.getValue(UserItems.class);
                    if(items != null){
                        if(user.getUid().equals(items.getUserId())){
                            isOld = true;
                            break;
                        }
                    }
                }
                if(isOld){
                    updateUI(user);
                }else{
                    DataManager.setUser(user);
                    updateUI(user);
                }
                mProgress.dismiss();
            }

            @Override
            public void onError(DatabaseError error) {
                mProgress.dismiss();
            }
        });
    }

    // start activity for the main activity
    private void onStartActivity(){
        finish();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // start activity for the home activity
    private void onStartAdminActivity(){
        finish();
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        if(DataMemory.getAllowNotify()) {
            startService(new Intent(this, NotificationService.class));
        }
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // start activity
    private void onStartActivity(Class next){
        Intent intent = new Intent(this,next);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgress.dispose();
    }
}