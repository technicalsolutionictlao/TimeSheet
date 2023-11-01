package com.ictlao.android.app.timesheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ictlao.android.app.timesheet.Items.MessageItems;
import com.ictlao.android.app.timesheet.Manager.DataManager;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.Objects;

public class NotificationInformationActivity extends AppCompatActivity implements OnMapReadyCallback {

    // collect message items in another file
    public static MessageItems messageItems;

    // controls
    private ImageView profile;
    private TextView name;
    private TextView date;
    private TextView time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_information);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.toolbar_notification_message));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        profile = findViewById(R.id.profile);
        name = findViewById(R.id.name);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
    }

    @Override
    protected void onStart() {
        super.onStart();
        onInitialize();
    }

    // initialize data to controls
    private void onInitialize(){
        if(!messageItems.getProfile_url().equals("")){
            Picasso.get().load(messageItems.getProfile_url()).into(profile);
        }
        name.setText(messageItems.getName());
        date.setText(messageItems.getDate());
        time.setText(messageItems.getTime());
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
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.setMaxZoomPreference(20.0f);
        googleMap.setMinZoomPreference(2f);
        LatLng latLng = new LatLng(messageItems.getLatitude(),messageItems.getLongitude());
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .snippet(messageItems.getDate()+" "+messageItems.getTime())
                .title(messageItems.getName()));
        googleMap.setOnMarkerClickListener(marker -> {
            onOpenLocationMapApp(marker.getPosition());
            return true;
        });
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(messageItems.getLatitude(),messageItems.getLongitude())));
    }

    // open map location with user data
    private void onOpenLocationMapApp(LatLng latLng)
    {
        String uri = String.format(Locale.getDefault(), "http://maps.google.com/maps?q=loc:%f,%f", latLng.latitude,latLng.longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        this.startActivity(intent);
    }
}