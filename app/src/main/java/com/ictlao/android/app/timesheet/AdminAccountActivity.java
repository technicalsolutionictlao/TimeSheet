package com.ictlao.android.app.timesheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.ictlao.android.app.timesheet.Manager.DataManager;
import com.ictlao.android.app.timesheet.Manager.DataMemory;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class AdminAccountActivity extends AppCompatActivity {

    // controls
    private ImageView mProfile;
    private TextView mName;
    private TextView mGmail;
    private Button mLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_account);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.my_information));

        mProfile = findViewById(R.id.profile);
        mName = findViewById(R.id.name);
        mGmail = findViewById(R.id.gmail);
        mLogout = findViewById(R.id.logout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        onInitialize();
    }

    // initialize data to controls
    private void onInitialize(){
        if(!DataManager.getAdminItems().getProfile_url().equals("")) {
            Picasso.get().load(DataManager.getAdminItems().getProfile_url()).into(mProfile);
        }
        mName.setText(DataManager.getAdminItems().getName());
        mGmail.setText(DataManager.getAdminItems().getEmail());
        mLogout.setOnClickListener(view -> onSignOut());
    }

    // sign out user
    private void onSignOut(){
        FirebaseAuth.getInstance().signOut();
        finish();
        DataMemory.clear();
        Intent intent = new Intent(this, LoginUserActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if(DataMemory.getAllowNotify()) {
            stopService(new Intent(this, NotificationService.class));
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
}