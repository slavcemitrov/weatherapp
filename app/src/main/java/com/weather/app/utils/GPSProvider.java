package com.weather.app.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

public class GPSProvider {
    private LocationManager lmGPS;
    private LocationListener locationListener;
    private double mLatitude = 0.0;
    private double mLongitude = 0.0;
    private static Context myCtx;
    public boolean mbUpdatesStopped = true;//PP

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    private static GPSProvider mGetLatLongFromGPS;

    public static GPSProvider getinstance(Context ctx) {
        myCtx = ctx;
        if (mGetLatLongFromGPS == null)
            mGetLatLongFromGPS = new GPSProvider(ctx);
        return mGetLatLongFromGPS;
    }

    public GPSProvider(Context ctx) {
        myCtx = ctx;
        lmGPS = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
    }

    public void startGPS() {

        if (lmGPS == null && myCtx == null) {
            assert myCtx != null;
            lmGPS = (LocationManager) myCtx.getSystemService(Context.LOCATION_SERVICE);
        }

        if (locationListener == null)
            locationListener = new MyLocationListener();

        if (ActivityCompat.checkSelfPermission(myCtx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location loc = null;
        lmGPS.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 100, locationListener);
        if(lmGPS.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lmGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, locationListener);
            loc = lmGPS.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (loc == null)
            loc = lmGPS.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        updateLocation(loc);
        mbUpdatesStopped = false;
    }

    public void stopLocationListening() {
        if (lmGPS != null) {
            lmGPS.removeUpdates(locationListener);
            mbUpdatesStopped = true;
        }
    }

    private class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location loc) {
            Location curLoc = loc;

            if (curLoc.hasAccuracy()) {
                updateLocation(curLoc);

            } else {
                loc = null;
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    private void updateLocation(Location loc) {
        if (loc != null) {
            mLatitude = loc.getLatitude();
            mLongitude = loc.getLongitude();
        } else {
            mLatitude = 0.0;
            mLongitude = 0.0;
        }
    }
}
