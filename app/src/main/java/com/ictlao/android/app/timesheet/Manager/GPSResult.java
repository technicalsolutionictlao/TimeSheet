package com.ictlao.android.app.timesheet.Manager;

import android.location.Location;

public interface GPSResult {
    void onLocationChange(Location location);
    void onGPSDisabled(boolean enable);
}
