package com.ictlao.android.app.timesheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ictlao.android.app.timesheet.Adapter.VacationAdapter;
import com.ictlao.android.app.timesheet.Items.DataResult;
import com.ictlao.android.app.timesheet.Items.UserItems;
import com.ictlao.android.app.timesheet.Items.VacationItems;
import com.ictlao.android.app.timesheet.Manager.DataManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class UserVacationActivity extends AppCompatActivity {

    // controls
    private ImageView profile;
    private TextView name;
    private TextView email;
    private TextView condition;
    private TextView day_qty;
    private TextView date;
    private TextView reason;
    private ListView listView;
    private TextView none_service;
    private LinearLayout linearLayout;

    // collect in another file
    public static UserItems userItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_vacation);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_vactions);

        profile = findViewById(R.id.profile);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        condition = findViewById(R.id.condition);
        day_qty = findViewById(R.id.day_qty);
        date = findViewById(R.id.date);
        reason = findViewById(R.id.reason);
        listView = findViewById(R.id.listView);
        none_service = findViewById(R.id.none_service);
        linearLayout = findViewById(R.id.linear);

        none_service.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        onInitialize();
    }

    // initialize data to controls
    private void onInitialize(){
        DataManager.onVacationListener(userItems.getUserId(), DataManager.getCurrentDate(), new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                ArrayList<VacationItems> list = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    VacationItems items = data.getValue(VacationItems.class);
                    if(items != null){
                        list.add(items);
                    }
                }
                if(list.size() > 0){
                    onShowListView(list);
                }else{
                    onHideScrollView();
                }
            }

            @Override
            public void onError(DatabaseError error) {

            }
        });
    }

    // hide scroll view
    private void onHideScrollView(){
        linearLayout.setVisibility(View.INVISIBLE);
        none_service.setVisibility(View.VISIBLE);
    }

    // show listView
    private void onShowListView(ArrayList<VacationItems> list){
        linearLayout.setVisibility(View.VISIBLE);
        none_service.setVisibility(View.INVISIBLE);
        VacationAdapter adapter = new VacationAdapter(this, list){
            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                View row = super.getView(i, view, viewGroup);
                if(i % 2 == 1){
                    row.setBackgroundResource(R.drawable.list_gray);
                }else {
                    row.setBackgroundResource(R.drawable.sub_list_dark_gray);
                }
                Animation animation = AnimationUtils.loadAnimation(UserVacationActivity.this, R.anim.slide_in_right);
                row.setAnimation(animation);
                return row;
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            onSetData((VacationItems) adapterView.getItemAtPosition(i));
        });
        onSetData(list.get(0));
    }

    // set data to controls
    @SuppressLint("SetTextI18n")
    private void onSetData(VacationItems items){
        if(!items.getProfile_url().equals("")){
            Picasso.get().load(items.getProfile_url()).into(profile);
        }
        name.setText(items.getName());
        email.setText(items.getEmail());
        condition.setText(items.getUseVacationCase());
        day_qty.setText(items.getDay_qty());
        date.setText(items.getStart_date()+" "+DataManager.DASH +" "+items.getEnd_date());
        reason.setText(items.getRemark());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.vacation_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)finish();
        if(item.getItemId() == R.id.add_vacation_day){
            AddVacationDayActivity.userItems = userItems;
            onStartActivity(AddVacationDayActivity.class);
        };
        return super.onOptionsItemSelected(item);
    }

    // start activity
    private void onStartActivity(Class next){
        Intent intent = new Intent(this,next);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}