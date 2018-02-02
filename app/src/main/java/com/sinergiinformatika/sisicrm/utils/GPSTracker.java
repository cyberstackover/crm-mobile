package com.sinergiinformatika.sisicrm.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by wendi on 19-Dec-14.
 * <p/>
 * http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial/
 */
public class GPSTracker implements LocationListener {

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private static String TAG = GPSTracker.class.getSimpleName();
    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */

    private static boolean showing = false;
    private final Context mContext;
    // Declaring a Location Manager
    protected LocationManager locationManager;
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude


    public GPSTracker(Context context) {
        this.mContext = context;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        getLocation();
    }

    public Location getLocation() {
        return getLocation(true);
    }

    public Location getLocation(boolean showSettingAlert) {
        try {
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled && showSettingAlert) {
                showSettingsAlert();
            } else {

                this.canGetLocation = true;
                String provider = null;

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    Log.d(TAG, "GPS Enabled");
                    updateLocation(LocationManager.GPS_PROVIDER);
                }

                if (isNetworkEnabled && location == null) {
                    Log.d(TAG, "Network Enabled");
                    updateLocation(LocationManager.NETWORK_PROVIDER);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return location;
    }

    private void updateLocation(String provider) {
        if (provider != null) {

            locationManager.requestLocationUpdates(
                    provider,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        canGetLocation = isGPSEnabled || isNetworkEnabled;
        return this.canGetLocation;
    }

    public void showSettingsAlert() {

        if (showing) {
            return;
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);

                showing = false;
                dialog.dismiss();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showing = false;
                dialog.cancel();
            }
        });

        /*alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                showing = false;
                Log.d(TAG, "setOnDismissListener");
                dialog.dismiss();
            }
        });*/

        showing = true;
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER))
            isGPSEnabled = false;
        if (provider.equals(LocationManager.NETWORK_PROVIDER))
            isNetworkEnabled = false;
    }

    @Override
    public void onProviderEnabled(String provider) {
        getLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
