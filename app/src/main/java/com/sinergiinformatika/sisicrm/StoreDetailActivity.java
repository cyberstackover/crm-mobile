package com.sinergiinformatika.sisicrm;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sinergiinformatika.sisicrm.conn.RestClient;
import com.sinergiinformatika.sisicrm.conn.RestResponseHandler;
import com.sinergiinformatika.sisicrm.data.models.Store;
import com.sinergiinformatika.sisicrm.data.models.StoreCategory;
import com.sinergiinformatika.sisicrm.db.tables.StoreTable;
import com.sinergiinformatika.sisicrm.dialogs.ImageViewDialog;
import com.sinergiinformatika.sisicrm.dialogs.LoadingDialog;
import com.sinergiinformatika.sisicrm.fragments.WorkaroundMapFragment;
import com.sinergiinformatika.sisicrm.managers.ImageCacheManager;
import com.sinergiinformatika.sisicrm.utils.GPSTracker;
import com.sinergiinformatika.sisicrm.utils.ImageDecodeUtil;
import com.sinergiinformatika.sisicrm.utils.LocaleUtil;
import com.sinergiinformatika.sisicrm.utils.ServerErrorUtil;
import com.sinergiinformatika.sisicrm.utils.User;
import com.sinergiinformatika.sisicrm.utils.WidgetUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.regex.Pattern;


public class StoreDetailActivity extends FragmentActivity {

    private static final String TAG = StoreDetailActivity.class.getSimpleName();
    private static final float DEFAULT_MAP_ZOOM = 15;

    private boolean noStoreImage = true;
    private String imageId, imageUri;
    private ImageView storePhoto;
    private BroadcastReceiver imageDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.DEBUG) Log.i(TAG, "image downloaded");

            String id = intent.getStringExtra(Constants.IMAGE_ID_EXTRA);
            if (Constants.DEBUG) Log.d(TAG, "image id: " + id);
            if (imageId != null && id.startsWith(imageId)) {
                int type = intent.getIntExtra(Constants.IMAGE_TYPE_EXTRA, 0);
                Bitmap bitmap = ImageDecodeUtil.decodeUrl(
                        StoreDetailActivity.this, imageId, type, null);

                if (bitmap != null) {
                    if (type == ImageCacheManager.IMAGE_TYPE_ITEM_THUMB && storePhoto != null) {
                        storePhoto.setImageBitmap(bitmap);
                    }
                }
            }
        }
    };

    private TextView storeName, storeType, storeTypeIcon, storeNFC, storeCapacity, storeAddress,
            storePhone, storeNotes, storeStatus, storeStatusIcon,
            storeOwnerName, storeOwnerMobile, storeOwnerBirthDate, storeOwnerReligion;
    private View mapHolder;
    private GoogleMap storeMap;
    private Store store = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtil.changeLocale(getApplicationContext(), Constants.DEFAULT_LOCALE);
        setContentView(R.layout.activity_store_detail);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        storePhoto = (ImageView) findViewById(R.id.store_detail_image);
        storeName = (TextView) findViewById(R.id.store_detail_name);
        storeType = (TextView) findViewById(R.id.store_detail_type);
        storeTypeIcon = (TextView) findViewById(R.id.store_detail_type_icon);
        storeNFC = (TextView) findViewById(R.id.store_detail_nfc_id);
        storeCapacity = (TextView) findViewById(R.id.store_detail_capacity);
        storeAddress = (TextView) findViewById(R.id.store_detail_store_address);
        storePhone = (TextView) findViewById(R.id.store_detail_phone);
        storeNotes = (TextView) findViewById(R.id.store_detail_notes);
        storeOwnerName = (TextView) findViewById(R.id.store_detail_owner_name);
        storeOwnerMobile = (TextView) findViewById(R.id.store_detail_owner_mobile);
        storeOwnerBirthDate = (TextView) findViewById(R.id.store_detail_owner_birth);
        storeOwnerReligion = (TextView) findViewById(R.id.store_detail_owner_religion);
        storeStatus = (TextView) findViewById(R.id.store_detail_status);
        storeStatusIcon = (TextView) findViewById(R.id.store_detail_status_icon);

        final ScrollView scrollView = (ScrollView) findViewById(R.id.store_detail_scroller);
        WorkaroundMapFragment storeMapView = (WorkaroundMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.store_detail_map);
        storeMapView.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });

        storeMap = storeMapView.getMap();
        mapHolder = findViewById(R.id.store_detail_map_holder);

        View label = findViewById(R.id.store_detail_location_label);
        TextView labelText = (TextView) label.findViewById(R.id.txt_item_header);
        labelText.setText(getString(R.string.label_store_location));
        label = findViewById(R.id.store_detail_contact_label);
        labelText = (TextView) label.findViewById(R.id.txt_item_header);
        labelText.setText(getString(R.string.label_store_contact));

        int storeDbId = getIntent().getIntExtra(Constants.EXTRA_STORE_DB_ID, 0);
        String storeId = getIntent().getStringExtra(Constants.EXTRA_STORE_ID);

        if (storeDbId > 0 || !TextUtils.isEmpty(storeId)) {
            StoreTable storeTable = new StoreTable(this);

            if (storeDbId > 0) {
                store = storeTable.getById(storeDbId);
            }

            if (store == null && !TextUtils.isEmpty(storeId)) {
                store = storeTable.getByStoreId(storeId);
            }

            if (Constants.DEBUG) Log.d(TAG, "store distributor: " + store.getDistributorId());
        }

        if (store != null) {
            setValues(store);

            LatLng latLng = null;
            if (store.getLatitude() != null && store.getLongitude() != null) {
                latLng = new LatLng(store.getLatitude(), store.getLongitude());
            }

            initMap(latLng);
        } else {
            Toast.makeText(this, R.string.error_data_not_found, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                imageDownloadReceiver, new IntentFilter(Constants.INTENT_ACTION_IMAGE_DOWNLOADED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(imageDownloadReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_store, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_edit:
                Intent intent = new Intent(this, StoreActivity.class);
                intent.putExtra(Constants.EXTRA_STORE, (Parcelable) store);

                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQ_CODE_LOCATION) {
            if (resultCode == Activity.RESULT_OK) {
                if (store != null && data != null) {
                    String lngLat = data.getExtras().getString(Constants.EXTRA_LOCATION, "");
                    if (!lngLat.isEmpty()) {
                        String[] tokens = lngLat.split(Pattern.quote(","));
                        double lng = Double.parseDouble(tokens[0]);
                        double lat = Double.parseDouble(tokens[1]);
                        LatLng latLng = new LatLng(lat, lng);

                        updateStoreLocation(latLng);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getParent() == null) {
            setResult(RESULT_OK);
        } else {
            getParent().setResult(RESULT_OK);
        }

        finish();
    }

    public void showFullImage(View view) {
        if (noStoreImage) {
            Toast.makeText(this, R.string.error_no_store_image, Toast.LENGTH_SHORT).show();
        } else {
            ImageViewDialog dialog = ImageViewDialog.newInstance(imageId, imageUri);
            dialog.show(getSupportFragmentManager(), "image");
        }
    }

    public void showLocationPicker(View view) {
        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            Intent intent = new Intent(this, MapActivity.class);
            startActivityForResult(intent, Constants.REQ_CODE_LOCATION);
            gps.stopUsingGPS();
        }/* else {
            gps.showSettingsAlert();
        }*/
    }

    private void setValues(Store store) {
        imageUri = store.getPhoto();
        imageId = null;

        Log.d(TAG, "store.getPhoto() = " + imageUri);

        if (imageUri != null) {
            imageId = ImageDecodeUtil.getImageIdFromUri(imageUri);
        } else {
            if (Constants.DEBUG) Log.d(TAG, "no image URL");
        }

        if (imageId != null) {
            if (Constants.DEBUG) Log.d(TAG, "fetching image");

            noStoreImage = false;

            Bitmap bitmap = null;
            File f = new File(imageUri);
            if (f.exists()) {
                bitmap = ImageDecodeUtil.decodeFile(this, imageId, imageUri);
            }

            if (bitmap == null) {
                bitmap = ImageDecodeUtil
                        .decodeUrl(this, imageId, ImageCacheManager.IMAGE_TYPE_ITEM_THUMB,
                                   imageUri);
            }

            if (bitmap != null) {
                storePhoto.setImageBitmap(bitmap);
            }
        } else {
            if (Constants.DEBUG) Log.d(TAG, "no image");
        }

        WidgetUtil.setValue(storeName, store.getName());
        WidgetUtil.setValue(storeType, store.getCategoryLabel());
        WidgetUtil.setValue(storeNFC, store.getNfcId());
        WidgetUtil.setValue(storeCapacity, store.getCapacity());
        WidgetUtil.setValue(storeAddress, store.getStreet());
        WidgetUtil.concatenateValue(storeAddress, store.getSubdistrict());
        WidgetUtil.concatenateValue(storeAddress, store.getCity());
        WidgetUtil.concatenateValue(storeAddress, store.getProvince());
        WidgetUtil.concatenateValue(storeAddress, store.getZipcode());
        WidgetUtil.setValue(storePhone, store.getPhone());
        WidgetUtil.setValue(storeNotes, store.getInformation());
        WidgetUtil.setValue(storeOwnerName, store.getOwnerName());
        WidgetUtil.setValue(storeOwnerMobile, store.getPhoneMobile());
        WidgetUtil.setValue(storeOwnerBirthDate, store.getOwnerBirthDateLongFmt());
        WidgetUtil.setValue(storeOwnerReligion, store.getOwnerReligionLabel());

        String status = store.getStatus();
        if (Constants.STORE_STATUS_ACTIVE.equalsIgnoreCase(status)) {
            storeStatusIcon.setText(R.string.icon_check_circle);
            storeStatusIcon.setTextColor(getResources().getColor(R.color.icon_status_active));
            storeStatus.setText(R.string.label_store_active);
        } else if (Constants.STORE_STATUS_VERIFIED.equalsIgnoreCase(status)) {
            storeStatusIcon.setText(R.string.icon_check_circle_o);
            storeStatusIcon.setTextColor(getResources().getColor(R.color.icon_status_neutral));
            storeStatus.setText(R.string.label_store_verified);
        } else {
            storeStatusIcon.setText(R.string.icon_circle_o);
            storeStatusIcon.setTextColor(getResources().getColor(R.color.icon_status_neutral));
            storeStatus.setText(R.string.label_store_unverified);
        }

        int typeCode = store.getCategoryCode();

        if (typeCode == 0) {
            if (store.getCategoryLabel().equals(StoreCategory.LABEL_PLATINUM.toLowerCase())) {
                typeCode = StoreCategory.CODE_PLATINUM;
            } else if (store.getCategoryLabel().equals(StoreCategory.LABEL_GOLD.toLowerCase())) {
                typeCode = StoreCategory.CODE_GOLD;
            } else {
                typeCode = StoreCategory.CODE_SILVER;
            }
        }

        switch (typeCode) {
            case Constants.STORE_CATEGORY_CODE_GOLD:
                storeTypeIcon.setText(R.string.icon_bookmark);
                storeTypeIcon.setTextColor(getResources().getColor(R.color.gold));
                break;
            case Constants.STORE_CATEGORY_CODE_PLATINUM:
                storeTypeIcon.setText(R.string.icon_bookmark);
                storeTypeIcon.setTextColor(getResources().getColor(R.color.platinum));
                break;
            case Constants.STORE_CATEGORY_CODE_SILVER:
                storeTypeIcon.setText(R.string.icon_bookmark_o);
//                storeTypeIcon.setTextColor(getResources().getColor(R.color.silver));
                break;
            default:
                break;
        }
    }

    private void initMap(LatLng latLng) {
        if (storeMap != null && latLng != null && latLng.latitude != 0 && latLng.longitude != 0) {
            mapHolder.setVisibility(View.VISIBLE);
            storeMap.addMarker(new MarkerOptions().position(latLng));
            storeMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            storeMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_MAP_ZOOM));
        } else {
            mapHolder.setVisibility(View.GONE);
        }
    }

    private void updateStoreLocation(final LatLng latLng) {
        RestResponseHandler responseHandler = new RestResponseHandler(this) {
            LoadingDialog loadingDialog;

            @Override
            public void onStart() {
                super.onStart();
                loadingDialog = new LoadingDialog();
                loadingDialog.show(getSupportFragmentManager(), Constants.TAG_LOADING);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                loadingDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (isSuccess(response)) {
                        StoreTable storeTable = new StoreTable(getContentResolver());
                        storeTable.updateLocation(store.getId(), latLng.longitude, latLng.latitude);

                        initMap(latLng);
                    } else {
                        String errMsg = ServerErrorUtil.getErrorMessage(
                                StoreDetailActivity.this, getErrorCode(response));
                        Toast.makeText(StoreDetailActivity.this, errMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                Toast.makeText(
                        StoreDetailActivity.this,
                        getString(R.string.error_store_location_update),
                        Toast.LENGTH_LONG).show();
            }
        };

        RestClient.getInstance(this, responseHandler)
                  .postStoreLocation(User.getInstance(this).getToken(), store.getStoreId(), latLng);
    }
}
