package com.happymeteo.utils;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class LocationManagerHelper implements LocationListener {
	private Location location;

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        Log.i(Const.TAG, "location: "+Double.toString(location.getLatitude())+" "+Double.toString(location.getLongitude()));
    }

    @Override
    public void onProviderDisabled(String provider) {
    	Log.i(Const.TAG, "onProviderDisabled: "+provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
    	Log.i(Const.TAG, "onProviderEnabled: "+provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    	Log.i(Const.TAG, "onStatusChanged: "+provider+" "+status);

    }

    public Location getLocation() {
        return location;
    }
}