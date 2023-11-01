package com.ictlao.android.app.timesheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.ictlao.android.app.timesheet.Items.AdminItems;

import java.util.Objects;

public class WarningActivity extends AppCompatActivity {

    // collect in another form
    public static AdminItems adminItems;

    // controls
    private TextView mName;
    private TextView mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_warning));

        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
    }

    @Override
    protected void onStart() {
        super.onStart();
        onInitialize();
    }

    // initialize data to the controls
    private void onInitialize(){
        mName.setText(adminItems.getName());
        mEmail.setText(adminItems.getEmail());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        FirebaseAuth.getInstance().signOut();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}