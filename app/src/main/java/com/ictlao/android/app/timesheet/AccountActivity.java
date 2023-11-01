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
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class AccountActivity extends AppCompatActivity {

    // profile ImageView
    private ImageView mProfile;
    // name TextView
    private TextView mName;
    // gmail TextView
    private TextView mGmail;
    // logout Button
    private Button mLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.my_information);

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

    // initialize the user profile
    private void onInitialize(){
        if(DataManager.getFirebaseUser().getPhotoUrl() != null) {
            Picasso.get().load(DataManager.getFirebaseUser().getPhotoUrl()).into(mProfile);
        }
        mName.setText(DataManager.getFirebaseUser().getDisplayName());
        mGmail.setText(DataManager.getFirebaseUser().getEmail());
        mLogout.setOnClickListener(view -> onSignOut());
    }

    // signOut user
    private void onSignOut(){
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(this, LoginUserActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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