package com.ictlao.android.app.timesheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import com.ictlao.android.app.timesheet.Manager.DataManager;
import com.ictlao.android.app.timesheet.Manager.DataMemory;

import java.util.Objects;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    // permissions
    private final String[] permissions =
            {
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
    // request call back
    private static final int REQUEST_PERMISSION = 464;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    @Override
    protected void onStart() {
        super.onStart();
        onInitialize();
    }

    // initialize permissions
    private void onInitialize(){
        if(ContextCompat.checkSelfPermission(this,permissions[0]) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this,permissions[1]) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this,permissions[2]) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, permissions[3]) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, permissions[4]) == PackageManager.PERMISSION_GRANTED){
            onHandle();
        }else{
            ActivityCompat.requestPermissions(this,permissions,REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION){
            onInitialize();
        }
    }

    // start activity with duration
    private void onHandle(){
        new Handler().postDelayed(this::onStartActivity,3000);
    }

    // start activity
    private void onStartActivity(){
        finish();
        Intent intent = new Intent(this, LoginUserActivity.class);
        startActivity(intent);
        //startService(new Intent(this,NotificationService.class));
        DataManager.getInstance();
        DataMemory.getInstance(this);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}