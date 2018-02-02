package com.sinergiinformatika.sisicrm.fragments;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.data.models.ItemImage;
import com.sinergiinformatika.sisicrm.dialogs.ImageViewDialog;
import com.sinergiinformatika.sisicrm.managers.ImageCacheManager;
import com.sinergiinformatika.sisicrm.utils.ImageDecodeUtil;
import com.sinergiinformatika.sisicrm.utils.MediaStoreUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SurveyImageFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = SurveyImageFragment.class.getSimpleName();
    public LinearLayout mImageHolderView;
    public LinearLayout mBtnHolderView;
    private boolean mReadOnly;
    private List<ItemImage> mImages;
    private Uri mImageFileUri;
    private View mViewFragment;
    private ViewStub mViewLoading;
    private Button mBtnGalleryView;
    private Button mBtnCameraView;
    private Button mBtnResetView;
    private Context context;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String imageId = intent.getStringExtra(Constants.IMAGE_ID_EXTRA);
            int type = intent.getIntExtra(
                    Constants.IMAGE_TYPE_EXTRA, ImageCacheManager.IMAGE_TYPE_ITEM_THUMB);

            if (type == ImageCacheManager.IMAGE_TYPE_ITEM_THUMB) {
                Bitmap bitmap = ImageDecodeUtil.decodeUrl(context, imageId, type, null);
//            String filePath = intent.getStringExtra(Constants.IMAGE_FILENAME);
//            Bitmap bitmap = ImageDecodeUtil.decodeFile(context, imageId, filePath);

                Log.d(TAG, "image received for: " + imageId);

                if (bitmap != null) {
                    mViewLoading.setVisibility(View.GONE);
                    addToContainer(imageId, bitmap);
                }
            } else {
                if (Constants.DEBUG) Log.d(TAG, "image received for another purpose");
            }
        }
    };
    private boolean errorLoadImage = false;

    public SurveyImageFragment() {
        // Required empty public constructor
    }

    public static SurveyImageFragment newInstance(List<ItemImage> images, boolean readOnly) {
        SurveyImageFragment fragment = new SurveyImageFragment();
        Bundle args = new Bundle();

        if (images != null)
            args.putParcelableArrayList(Constants.ARG_DATA, new ArrayList<Parcelable>(images));
        args.putBoolean(Constants.ARG_READ_ONLY, readOnly);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Bundle args = getArguments();
        if (args != null) {
            mReadOnly = args.getBoolean(Constants.ARG_READ_ONLY, false);
            if (mImages == null) {
                mImages = args.getParcelableArrayList(Constants.ARG_DATA);
            }

        }

        if (mImages == null)
            mImages = new ArrayList<>();

        this.context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mViewFragment = inflater.inflate(R.layout.fragment_survey_image, container, false);
        mImageHolderView = (LinearLayout) mViewFragment.findViewById(R.id.img_holder);
        mBtnHolderView = (LinearLayout) mViewFragment.findViewById(R.id.row_btn_holder);
        mBtnGalleryView = (Button) mViewFragment.findViewById(R.id.survey_image_gallery_btn);
        mBtnCameraView = (Button) mViewFragment.findViewById(R.id.survey_image_camera_btn);
        mBtnResetView = (Button) mViewFragment.findViewById(R.id.survey_image_reset_btn);
        mViewLoading = (ViewStub) mViewFragment.findViewById(R.id.row_loading);

        if (!mReadOnly) {
            mBtnGalleryView.setOnClickListener(this);
            mBtnCameraView.setOnClickListener(this);
            mBtnResetView.setOnClickListener(this);
            checkImageCount();
        } else {
            mBtnHolderView.setVisibility(View.GONE);
            mBtnResetView.setVisibility(View.INVISIBLE);
        }

        addAllToContainer();

        return mViewFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(context).registerReceiver(
                receiver, new IntentFilter(Constants.INTENT_ACTION_IMAGE_DOWNLOADED));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }

    private void resetForm() {
        mImageHolderView.removeAllViews();
    }

    private void addToContainer(final String imageId, Bitmap bitmap) {
        if (Constants.DEBUG) Log.d(TAG, "attempting to add image to root view");

        String temp = null;
        for (ItemImage image : mImages) {
            String url = image.getImageUri();
            String id = getImageIdFromUri(url);
            if (imageId.equalsIgnoreCase(id)) {
                temp = url;
                break;
            }
        }
        final String imageUri = temp;
        View child = LayoutInflater.from(context).inflate(R.layout.row_image, mImageHolderView, false);
        ImageView imageView = (ImageView) child.findViewById(R.id.row_image_view);
        imageView.setImageBitmap(bitmap);

        if (!TextUtils.isEmpty(imageUri)) {
            if (Constants.DEBUG) Log.d(TAG, "adding click handler to image");
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageViewDialog dialog = ImageViewDialog.newInstance(imageId, imageUri);
                    dialog.show(getChildFragmentManager(), "image");
                }
            });
        } else {
            if (Constants.DEBUG) Log.e(TAG, "empty image URL");
        }

        Button removeBtn = (Button) child.findViewById(R.id.row_image_remove_btn);
        if (mReadOnly) {
            removeBtn.setVisibility(View.GONE);
        }

        if (mImageHolderView != null) {
            if (Constants.DEBUG) Log.d(TAG, "adding image to root view");
            mImageHolderView.addView(child);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.survey_image_gallery_btn:
                openGallery();
                break;
            case R.id.survey_image_camera_btn:
                captureImage();
                break;
            case R.id.survey_image_reset_btn:
                resetForm();
                break;
            default:
                break;
        }
    }

    private void captureImage() {
        mImageFileUri = MediaStoreUtil.getOutputImageFileUri(null);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageFileUri);
        getParentFragment().startActivityForResult(intent, Constants.REQ_CODE_CAMERA);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getParentFragment().startActivityForResult(intent, Constants.REQ_CODE_GALLERY);
    }

    private boolean checkImageCount() {
        if (mImages != null && mImages.size() == Constants.MAX_IMAGE_COUNT) {
            mBtnCameraView.setEnabled(false);
            mBtnGalleryView.setEnabled(false);
            return false;
        }

        return true;
    }

    public List<ItemImage> getImages() {
        return mImages;
    }

    public void addImage(ItemImage image) {
        if (checkImageCount()) {
            mImages.add(image);
        }
    }

    public Uri getImageFileUri() {
        return mImageFileUri;
    }

    public void setImageFileUri(Uri mImageFileUri) {
        this.mImageFileUri = mImageFileUri;
    }

    public void removeImageByPath(String imageUri) {
        for (ItemImage image : mImages) {
            if (image.getImageUri().equals(imageUri)) {
                mImages.remove(image);
                break;
            }
        }
    }

    public void addAllToContainer() {
        if (mImages != null) {

            if (Constants.DEBUG) {
                Log.d(TAG, "################ mImages.size = " + mImages.size());
            }

            for (ItemImage im : mImages) {
                String imageId = getImageIdFromUri(im.getImageUri());
                if (Constants.DEBUG) Log.d(TAG, "image id: " + imageId);
//                new LoadImage().execute(im.getImageUri(), im.getImageId());
                loadImage(imageId, im.getImageUri());
            }
        }
    }

    private byte[] downloadImage(String url) {

        Log.d(TAG, "url = " + url);

        // Initialize the default HTTP client object
        final DefaultHttpClient client = new DefaultHttpClient();

        //forming a HttpGet request
        final HttpGet getRequest = new HttpGet(url);

        try {
            HttpResponse response = client.execute(getRequest);
            //check 200 OK for success
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.e("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url);
                return null;
            }


            Log.d(TAG, "############ 1");

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                Log.d(TAG, "############ 2");
                InputStream inputStream = null;
                try {
                    // getting contents from the stream
                    inputStream = entity.getContent();

                    return getBytes(inputStream);

                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            getRequest.abort();
            Log.e(getString(R.string.app_name), "Error " + e.toString());
        }
        return null;
    }

    private byte[] getBytes(InputStream is) throws IOException {

        int len;
        int size = 3 * 1024;
        byte[] buf;

        if (is instanceof ByteArrayInputStream) {
            size = is.available();
            buf = new byte[size];
            len = is.read(buf, 0, size);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            buf = new byte[size];
            while ((len = is.read(buf, 0, size)) != -1)
                bos.write(buf, 0, len);
            buf = bos.toByteArray();
        }
        return buf;
    }

    private String getImageIdFromUri(String imageUri) {
        if (Constants.DEBUG) Log.d(TAG, "image URL: " + imageUri);

        String imageId = null;
        int lastSlashIdx = imageUri.lastIndexOf('/');

        if (lastSlashIdx > -1 && lastSlashIdx < imageUri.length() - 1) {
            String temp = imageUri.substring(lastSlashIdx + 1);
            if (!TextUtils.isEmpty(temp) && temp.length() > 4 && temp.contains("."))
                imageId = temp;
        }

        return imageId;
    }

    private void loadImage(String imageId, String imageUri) {
        Bitmap bitmap;
        File f = new File(imageUri);
        if (f.exists()) {
            bitmap = ImageDecodeUtil.decodeFile(context, imageId, imageUri);
        } else {
            bitmap = ImageDecodeUtil.decodeUrl(
                    context, imageId, ImageCacheManager.IMAGE_TYPE_ITEM_THUMB, imageUri);
        }

        if (bitmap != null) {
            addToContainer(imageId, bitmap);
        } else {
            if (mImageHolderView != null) {
                mViewLoading.setVisibility(View.VISIBLE);
            }
        }
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        String imageId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            errorLoadImage = false;
            mViewLoading.setVisibility(View.VISIBLE);
        }

        protected Bitmap doInBackground(String... args) {
            Bitmap bitmap = null;
            try {

                String imageUri = args[0];
                imageId = getImageIdFromUri(imageUri);

                File f = new File(imageUri);
                if (f.exists()) {
                    bitmap = ImageDecodeUtil.decodeFile(context, imageId, imageUri);
                } else {
                    bitmap = ImageDecodeUtil.decodeUrl(
                            context, imageId, ImageCacheManager.IMAGE_TYPE_ITEM_THUMB, imageUri);
                    /*int imageType = ImageCacheManager.IMAGE_TYPE_ITEM_LARGE;
                    String imageName = ImageCacheManager.generateImageFileName(imageType, imageId, null);
                    ImageCacheManager icm = ImageCacheManager.getInstance(context);
                    bitmap = icm.getImageFile(imageName);
                    if(bitmap == null){
                        byte[] imageBytes = downloadImage(imageUri);
                        if(imageBytes != null){
                            icm.saveImageFile(imageName, imageBytes);
                            bitmap = icm.getImageFile(imageName);
                        }
                    }*/
                }

            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                errorLoadImage = true;
            } catch (OutOfMemoryError e) {
                Log.e(TAG, e.getMessage(), e);
                errorLoadImage = true;
            } catch (Throwable e) {
                Log.e(TAG, e.getMessage(), e);
                errorLoadImage = true;
            }

            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            mViewLoading.setVisibility(View.GONE);

            if (image != null) {
                addToContainer(imageId, image);
            } else {
                if (errorLoadImage && context != null) {
                    Toast.makeText(context, getString(R.string.error_can_not_load_image), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
