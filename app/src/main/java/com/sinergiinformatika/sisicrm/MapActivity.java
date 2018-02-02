package com.sinergiinformatika.sisicrm;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sinergiinformatika.sisicrm.utils.Formater;
import com.sinergiinformatika.sisicrm.utils.GPSTracker;
import com.sinergiinformatika.sisicrm.utils.LocaleUtil;


public class MapActivity extends FragmentActivity {

    private static final int ZOOM_LEVEL = 15;
    private static String TAG = MapActivity.class.getSimpleName();
    private SupportMapFragment mSupportMapFragment;
    private GoogleMap mGoogleMap;
    private GPSTracker mGps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtil.changeLocale(getApplicationContext(), Constants.DEFAULT_LOCALE);
        setContentView(R.layout.activity_map);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {

            if(mSupportMapFragment == null){
                mSupportMapFragment = SupportMapFragment.newInstance();
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_map, mSupportMapFragment)
                    .commit();
        }

        mGps = new GPSTracker(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setupMap();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGps != null) {
            mGps.stopUsingGPS();
        }
    }

    private void setupMap() {
        if(mGoogleMap == null){
            mGoogleMap = mSupportMapFragment.getMap();
        }

        mGps.getLocation(false);//update location
        LatLng currentLatLng = new LatLng(mGps.getLatitude(), mGps.getLongitude());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM_LEVEL));
        mGoogleMap.addMarker(new MarkerOptions()
                .position(currentLatLng));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void invokeLocationMap(View view){

        mGps.getLocation();//update location

        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_LOCATION, Formater.longlat(mGps.getLongitude(), mGps.getLatitude()));
        setResult(RESULT_OK, intent);
        super.finish();
    }

}
