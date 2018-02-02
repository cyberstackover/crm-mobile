package com.sinergiinformatika.sisicrm.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.MapActivity;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.adapters.DistributorArrayAdapter;
import com.sinergiinformatika.sisicrm.adapters.LabelValueAdapter;
import com.sinergiinformatika.sisicrm.data.models.Distributor;
import com.sinergiinformatika.sisicrm.data.models.LabelValue;
import com.sinergiinformatika.sisicrm.data.models.Store;
import com.sinergiinformatika.sisicrm.data.models.StoreCategory;
import com.sinergiinformatika.sisicrm.db.tables.StoreTable;
import com.sinergiinformatika.sisicrm.dialogs.ActionButtonsDialog;
import com.sinergiinformatika.sisicrm.managers.ImageCacheManager;
import com.sinergiinformatika.sisicrm.utils.Formater;
import com.sinergiinformatika.sisicrm.utils.GPSTracker;
import com.sinergiinformatika.sisicrm.utils.ImageDecodeUtil;
import com.sinergiinformatika.sisicrm.utils.MediaStoreUtil;
import com.sinergiinformatika.sisicrm.utils.User;
import com.sinergiinformatika.sisicrm.utils.WidgetUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreInfoFragment extends Fragment implements View.OnClickListener,
                                                           AdapterView.OnItemSelectedListener {

    private static final String TAG = StoreInfoFragment.class.getSimpleName();

    public EditText mStoreNameView;
    public EditText mNfcIdView;
    public EditText mCoordinateView;
    public EditText mCapacityView;
    public Spinner mStoreCategoryView;
    public Spinner distributorSpinner;
    public ImageView mStorePhotoView;
    public String mImagePath;

    private String distributorId = null, location;
    private Context context;
    private View mRootView;
    private LabelValueAdapter mStoreCategoryAdapter;
    private LabelValueAdapter mEmptyAdapter;
    private ActionButtonsDialog mButtonsDialog;
    private Uri mImageFileUri;
    private Button mButtonRemovePhoto, mButtonOpenMap;
    private DistributorArrayAdapter distributorAdapter;
    private List<Distributor> distributors;
    private Store store;

    public StoreInfoFragment() {
        // Required empty public constructor
    }

    public static StoreInfoFragment newInstance(Store s) {
        StoreInfoFragment fragment = new StoreInfoFragment();

        Bundle args = new Bundle();
        args.putParcelable(Constants.EXTRA_STORE, s);

        fragment.setArguments(args);

        return fragment;
    }

    public String getDistributorId() {
        if (TextUtils.isEmpty(distributorId)) {
            Distributor distributor =
                    distributorAdapter.getItem(distributorSpinner.getSelectedItemPosition());

            if (distributor != null) {
                distributorId = distributor.getId();
            }
        }

        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<LabelValue> labelValues = new ArrayList<>();
        labelValues.add(new LabelValue(getString(R.string.label_store_category),
                                       getString(R.string.label_store_category), true));
        mEmptyAdapter =
                new LabelValueAdapter(context, android.R.layout.simple_spinner_dropdown_item,
                                      labelValues);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (Constants.DEBUG) {
            Log.d(TAG, "onCreateView");
        }

        mRootView = inflater.inflate(R.layout.fragment_store_info, container, false);
        mStoreCategoryAdapter = new LabelValueAdapter(context, 0, getStoreCategories());
        mStoreNameView = (EditText) mRootView.findViewById(R.id.store_edit_store_name);
        mStoreCategoryView = (Spinner) mRootView.findViewById(R.id.store_edit_store_category);
        mStoreCategoryView.setAdapter(mStoreCategoryAdapter);
        mNfcIdView = (EditText) mRootView.findViewById(R.id.store_edit_nfc_id);
        mCoordinateView = (EditText) mRootView.findViewById(R.id.store_edit_coordinate);
        mCapacityView = (EditText) mRootView.findViewById(R.id.store_edit_warehouse_capacity);
        mStorePhotoView = (ImageView) mRootView.findViewById(R.id.store_edit_photo);
        mStorePhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> actions = new ArrayList<>();
                List<String> labels = new ArrayList<>();

                actions.add(Constants.ACTION_GALLERY);
                labels.add(getString(R.string.label_btn_gallery));

                actions.add(Constants.ACTION_CAMERA);
                labels.add(getString(R.string.label_btn_camera));

                mButtonsDialog =
                        ActionButtonsDialog.newInstance(0, actions, labels, StoreInfoFragment.this);
                mButtonsDialog.show(getChildFragmentManager(), Constants.TAG_ACTION_BUTTONS);
            }
        });

        distributorAdapter = new DistributorArrayAdapter(context, 0, 0, distributors,
                                                         context.getString(
                                                                 R.string.label_distributor_name));
        distributorSpinner = (Spinner) mRootView.findViewById(R.id.store_edit_distributor_spinner);
        distributorSpinner.setAdapter(distributorAdapter);
        distributorSpinner.setOnItemSelectedListener(this);

        if (distributors.size() > 0) {
            distributorSpinner.setSelection(distributors.size() - 1);
        }

        if (!TextUtils.isEmpty(distributorId)) {
            int pos = distributorAdapter.getPositionByDistributorId(distributorId);
            if (pos >= 0) {
                distributorSpinner.setSelection(pos);
            }
        }

        if (User.getInstance(context).getRoleName().equalsIgnoreCase(Constants.ROLE_NAME_AM)) {
            distributorSpinner.setVisibility(View.VISIBLE);
        } else {
            distributorSpinner.setVisibility(View.GONE);
        }

        mButtonRemovePhoto = (Button) mRootView.findViewById(R.id.btn_remove_photo);
        mButtonRemovePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStorePhotoView.setImageBitmap(null);
                mImagePath = null;
                mStorePhotoView.setImageResource(R.drawable.no_image);
                mButtonRemovePhoto.setVisibility(View.GONE);
            }
        });

        mButtonOpenMap = (Button) mRootView.findViewById(R.id.button_open_map);
        mButtonOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

//        draw();

        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        distributors = (new StoreTable(context.getContentResolver())).getDistributors(null, null);
        if (distributors.isEmpty()) {
            Toast.makeText(
                    context,
                    context.getString(R.string.error_distributor_list),
                    Toast.LENGTH_LONG).show();
        }

        if (getArguments() != null) {
            store = getArguments().getParcelable(Constants.EXTRA_STORE);
        } else {
            store = new Store();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        int categoryIdx = 0, distributorIdx = -1;
        LabelValueAdapter categoryAdapter =
                (LabelValueAdapter) mStoreCategoryView.getAdapter();
        DistributorArrayAdapter distributorAdapter =
                (DistributorArrayAdapter) distributorSpinner.getAdapter();

        for (int i = 0; i < categoryAdapter.getCount(); i++) {
            if (Integer.valueOf(categoryAdapter.getItem(i).getValue())
                       .equals(store.getCategoryCode())) {
                categoryIdx = i;
            }
        }

        if (!TextUtils.isEmpty(store.getDistributorId())) {
            Log.d(TAG, "store distributor is " + store.getDistributorId());
            for (int i = 0; i < distributorAdapter.getCount(); i++) {
                if (distributorAdapter.getItem(i).getId().equals(store.getDistributorId())) {
                    distributorIdx = i;
                }
            }
        } else {
            Log.d(TAG, "store distributor is empty");
        }

        if (distributorIdx >= 0) {
            distributorSpinner.setSelection(distributorIdx);
            setDistributorId(
                    ((Distributor) distributorSpinner.getSelectedItem())
                            .getId());
        }

        if (!TextUtils.isEmpty(store.getPhoto())) {
            File imageFile = new File(store.getPhoto());
            Bitmap imageBitmap = null;

            if (imageFile.exists()) {
                imageBitmap = ImageDecodeUtil
                        .decodeFile(context, imageFile.getAbsolutePath(),
                                    imageFile.getAbsolutePath());
            } else if (store.getPhoto().trim().length() > "http://".length()) {
                imageBitmap = ImageDecodeUtil
                        .decodeUrl(context,
                                   ImageDecodeUtil.getImageIdFromUri(store.getPhoto()),
                                   ImageCacheManager.IMAGE_TYPE_ITEM_THUMB,
                                   store.getPhoto());
            }

            if (imageBitmap != null) {
                mStorePhotoView.setImageBitmap(imageBitmap);
            }
        }

        if (!TextUtils.isEmpty(store.getName())) {
            mStoreNameView.setText(store.getName());
        }

        if (store.getLongitude() != 0.0 && store.getLatitude() != 0.0) {
            mCoordinateView.setText(String.format("%s,%s", store.getLongitude(),
                                                  store.getLatitude()));
        }

        if (!TextUtils.isEmpty(location)) {
            mCoordinateView.setText(location);
        }

        if (store.getCapacity() > 0) {
            mCapacityView.setText(String.format("%d", store.getCapacity()));
        }

        mStoreCategoryView.setSelection(categoryIdx);
        mImagePath = store.getPhoto();

        draw();
    }

    @Override
    public void onPause() {
        store.setName(WidgetUtil.getValue(mStoreNameView));
        store.setCategoryCode(Integer.valueOf(
                ((LabelValue) mStoreCategoryView.getSelectedItem()).getValue()));
        store.setCategoryLabel(StoreCategory.getLabel(store.getCategoryCode()));
        try {
            store.setCapacity(Integer.parseInt(WidgetUtil.getValue(
                    mCapacityView)));
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        if (!TextUtils.isEmpty(getDistributorId())) {
            store.setDistributorId(getDistributorId());
        }

        store.setNfcId(WidgetUtil.getValue(mNfcIdView));
        location = WidgetUtil.getValue(mCoordinateView);

        Double lng = null, lat = null;
        try {
            String[] locationTokens = location.split(",");
            lng = Formater.doubleValue(locationTokens[0]);
            lat = Formater.doubleValue(locationTokens[1]);
        } catch (Exception e) {
            //Log.e(TAG, e.getMessage(), e);
        }

        store.setLongitude(lng);
        store.setLatitude(lat);
        store.setPhoto(mImagePath);

        super.onPause();
    }

    private List<StoreCategory> getStoreCategories() {
        List<StoreCategory> categories = new ArrayList<>();
        //categories.add(new StoreCategory("0", getString(R.string.label_store_category), true));
        categories.add(StoreCategory.PLATINUM);
        categories.add(StoreCategory.GOLD);
        categories.add(StoreCategory.SILVER);
        return categories;
    }

    @Override
    public void onClick(View view) {
        String action = (String) view.getTag(R.string.tag_action);

        if (Constants.ACTION_GALLERY.equals(action)) {
            openGallery();
        } else if (Constants.ACTION_CAMERA.equals(action)) {
            captureImage();
        }

        if (mButtonsDialog != null) {
            mButtonsDialog.dismiss();
            mButtonsDialog = null;
        }
    }

    private void captureImage() {
        mImageFileUri = MediaStoreUtil.getOutputImageFileUri(null);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageFileUri);
        startActivityForResult(intent, Constants.REQ_CODE_CAMERA);
    }

    private void openGallery() {
        Intent intent =
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constants.REQ_CODE_GALLERY);
    }

    private void openMap() {
        GPSTracker gps = new GPSTracker(context);
        if (gps.canGetLocation()) {
            Intent intent = new Intent(context, MapActivity.class);
            startActivityForResult(intent, Constants.REQ_CODE_LOCATION);
            gps.stopUsingGPS();
        }/* else {
            gps.showSettingsAlert();
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Constants.DEBUG) {
            Log.d(TAG, "request code: " + requestCode);
            Log.d(TAG, "data is null: " + (data == null));
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQ_CODE_CAMERA
                || requestCode == Constants.REQ_CODE_GALLERY) {

                mImagePath = null;

                if (Constants.DEBUG) Log.d(TAG, "image URI is null: " + (mImageFileUri == null));

                if (data != null) {
                    mImagePath = MediaStoreUtil.getPath(context, data.getData());
                }

                if (mImagePath == null && mImageFileUri != null) {
                    mImagePath = mImageFileUri.getPath();
                    mImageFileUri = null;

                    if (Constants.DEBUG) Log.d(TAG, "image path from camera: " + mImagePath);

                    // Now, compress the image
                    Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
                    FileOutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(mImagePath);
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }

                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
                }

                if (mImagePath != null) {
//                    draw();
                    store.setPhoto(mImagePath);
                } else {
                    Log.e(TAG, "no image taken");
                }
            } else if (requestCode == Constants.REQ_CODE_LOCATION) {
                if (data != null && data.hasExtra(Constants.EXTRA_LOCATION)) {
                    location = data.getExtras().getString(Constants.EXTRA_LOCATION);
                    if (Constants.DEBUG) Log.d(TAG, "location: " + location);
                } else {
                    Toast.makeText(context, getString(R.string.error_can_not_get_location),
                                   Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void draw() {
        if (Constants.DEBUG) {
            Log.d(TAG, "mImagePath = " + mImagePath);
        }
        if (mImagePath != null) {
            File file = new File(mImagePath);
            Bitmap bitmap = null;

            if (file.exists()) {
                bitmap = ImageDecodeUtil.decodeFile(context, mImagePath, mImagePath);
            } else if (mImagePath.trim().length() > "http://".length()) {
                bitmap = ImageDecodeUtil
                        .decodeUrl(context, ImageDecodeUtil.getImageIdFromUri(store.getPhoto()),
                                   ImageCacheManager.IMAGE_TYPE_ITEM_THUMB,
                                   store.getPhoto());
            }

            if (bitmap != null) {
                mStorePhotoView.setImageBitmap(bitmap);
                mButtonRemovePhoto.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Distributor distributor = distributorAdapter.getItem(position);
        if (distributor != null) {
            distributorId = distributor.getId();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
