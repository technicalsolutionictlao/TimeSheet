package com.ictlao.android.app.timesheet.Manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
// Manage the gps location
public class GPSManager implements LocationListener {
    private LocationManager locationManager;
    private GPSResult result = null;
    private Activity activity;
    // the meter in the general
    public static final double METERS_IN_MILE = 1609.344;
    private static final double MAX_NUMBER = 99999999999.0;

    // constructor of this class
    public GPSManager(Activity activity) {
        this.activity = activity;
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    }

    // get the result of the lat and long
    public void onResult(GPSResult result) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 3, this);
        this.result = result;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (result != null) {
            result.onLocationChange(location);
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        boolean b = provider.equals(LocationManager.GPS_PROVIDER);
        if (b) b = false;
        result.onGPSDisabled(b);
        if (provider.equals(LocationManager.PASSIVE_PROVIDER)) onPassiveProvider();
        if (provider.equals(LocationManager.NETWORK_PROVIDER)) onNetworkProvider();
    }

    // get last known location of gps provider
    public Location getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    // check the gps provider enable
    public boolean isGPSEnable(){
       return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        result.onGPSDisabled(provider.equals(LocationManager.GPS_PROVIDER));
        if (ActivityCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        result.onLocationChange(location);
    }

    // set passive provider
    private void onPassiveProvider(){
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 10, 3, this);
    }

    // set network provider
    private void onNetworkProvider(){
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 3, this);
    }

    // get distance in mile from maters
    public double metersToMiles(double meters) {
        return meters / METERS_IN_MILE;
    }

    // get distance in meter from miles
    public double milesToMeters(double miles) {
        return miles * METERS_IN_MILE;
    }

    // get distance int kilometer from meters
    public double meterToKilometer(double meter) {
        return (double) (meter * 0.001);
    }

    // get distance from two locations
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    // dispose this class
    public void dispose(){
        activity = null;
        locationManager = null;
    }
}
