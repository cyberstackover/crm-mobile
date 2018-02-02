package com.sinergiinformatika.sisicrm.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.managers.ImageCacheManager;
import com.sinergiinformatika.sisicrm.utils.ImageDecodeUtil;
import com.sinergiinformatika.sisicrm.views.TouchImageView;

import java.io.File;

/**
 * Created by Mark on 1/22/2015.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class ImageViewDialog extends DialogFragment {
    private static final String TAG = ImageViewDialog.class.getSimpleName();
    private static final String IMAGE_ID = "image_id";
    private static final String IMAGE_URL = "image_url";

    private String imageId, imageUrl;
    private Context context;
    private ProgressBar imageProgress;
    private TouchImageView imageView;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.DEBUG) Log.i(TAG, "image downloaded");

            String id = intent.getStringExtra(Constants.IMAGE_ID_EXTRA);
            if (Constants.DEBUG) Log.d(TAG, "image id: " + id);
            if (imageId != null && id.startsWith(imageId)) {
                int type = intent.getIntExtra(Constants.IMAGE_TYPE_EXTRA, 0);
                Bitmap bitmap = ImageDecodeUtil.decodeUrl(
                        context, imageId, ImageCacheManager.IMAGE_TYPE_ITEM_LARGE, null);

                if (bitmap != null && type == ImageCacheManager.IMAGE_TYPE_ITEM_LARGE
                        && imageView != null) {
                    if (imageProgress != null)
                        imageProgress.setVisibility(View.GONE);

                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @NonNull
    public static ImageViewDialog newInstance(String imageId, String imageUrl) {
        ImageViewDialog dialog = new ImageViewDialog();
        Bundle args = new Bundle();

        args.putString(IMAGE_ID, imageId);
        args.putString(IMAGE_URL, imageUrl);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;

        Bundle args = getArguments();
        imageId = args.getString(IMAGE_ID);
        imageUrl = args.getString(IMAGE_URL);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(context).registerReceiver(
                receiver, new IntentFilter(Constants.INTENT_ACTION_IMAGE_DOWNLOADED));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_image_view);
        dialog.getWindow().getAttributes().width = RelativeLayout.LayoutParams.MATCH_PARENT;
        dialog.getWindow().getAttributes().height = RelativeLayout.LayoutParams.MATCH_PARENT;

        imageView = (TouchImageView) dialog.findViewById(R.id.image_view);
        imageProgress = (ProgressBar) dialog.findViewById(R.id.image_progress);
        Bitmap bitmap = null;

        try {
            File f = new File(imageUrl);
            if(f.exists()){
                bitmap = ImageDecodeUtil.decodeFile(context, imageId, imageUrl);
            }else{
                bitmap = ImageDecodeUtil.decodeUrl(
                        context, imageId, ImageCacheManager.IMAGE_TYPE_ITEM_LARGE, imageUrl);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        if (bitmap != null) {
            imageProgress.setVisibility(View.GONE);
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
        }

        return dialog;
    }
}
