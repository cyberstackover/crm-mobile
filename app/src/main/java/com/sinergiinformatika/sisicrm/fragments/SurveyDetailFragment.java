package com.sinergiinformatika.sisicrm.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.adapters.SurveyPagerAdapter;
import com.sinergiinformatika.sisicrm.data.models.ItemCompetitor;
import com.sinergiinformatika.sisicrm.data.models.ItemComplain;
import com.sinergiinformatika.sisicrm.data.models.ItemImage;
import com.sinergiinformatika.sisicrm.data.models.ItemNote;
import com.sinergiinformatika.sisicrm.data.models.ItemPrice;
import com.sinergiinformatika.sisicrm.data.models.Survey;
import com.sinergiinformatika.sisicrm.db.tables.SurveyTable;
import com.sinergiinformatika.sisicrm.dialogs.LoadingDialog;
import com.sinergiinformatika.sisicrm.utils.ImageDecodeUtil;
import com.sinergiinformatika.sisicrm.utils.MediaStoreUtil;
import com.sinergiinformatika.sisicrm.utils.TabFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mark on 12/24/2014.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class SurveyDetailFragment extends BaseFragment
        implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener,
        View.OnClickListener {

    private static final String TAG = SurveyDetailFragment.class.getSimpleName();

    public int mCurrentPosition = 0;
    public List<ItemPrice> mPrices;
    public Multimap<String, ItemComplain> mComplains;
    public Map<String, ItemNote> mComplainNotes;
    public Multimap<String, ItemCompetitor> mCompetitorPrograms;
    public Map<String, ItemNote> mCompetitorNotes;
    public List<ItemImage> mImages;
    public SurveyPagerAdapter mPagerAdapter;
    public Survey mSurveyData;
    public SurveyPriceFragment mPriceFragment;
    public SurveyImageFragment mImageFragment;
    public SurveyComplainFragment mComplainFragment;
    public SurveyCompetitorFragment mRivalFragment;
    public Button mBtnSave;
    public Button mBtnCancel;
    public Button mBtnNext;
    public Button mBtnPrev;
    public ViewPager mViewPager;
    private boolean mReadOnly = true;
    private Context context;
    private TabHost mTabHost;
    private View mRootView;
    private TextView mTitlePageView;
    private LoadingDialog loading;
    private SurveyTable mSurveyTable;

    public SurveyDetailFragment() {
    }

    public static SurveyDetailFragment newInstance(Survey surveyData, boolean readOnly) {
        SurveyDetailFragment fragment = new SurveyDetailFragment();
        Bundle args = new Bundle();

        args.putParcelable(Constants.ARG_DATA, surveyData);
        args.putBoolean(Constants.ARG_READ_ONLY, readOnly);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        Bundle args = getArguments();
        if (args != null) {
            mReadOnly = args.getBoolean(Constants.ARG_READ_ONLY, false);
            mSurveyData = args.getParcelable(Constants.ARG_DATA);
        }

        mSurveyTable = new SurveyTable(this.context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_survey_detail, container, false);
        mTitlePageView = (TextView) mRootView.findViewById(R.id.row_title_fragment_survey);

        mBtnSave = (Button) mRootView.findViewById(R.id.btn_save);
        mBtnCancel = (Button) mRootView.findViewById(R.id.btn_cancel);
        mBtnNext = (Button) mRootView.findViewById(R.id.btn_next);
        mBtnPrev = (Button) mRootView.findViewById(R.id.btn_prev);

        if (mSurveyData != null) {
            mTitlePageView.setText(mSurveyData.getStoreName());
        } else {
            mTitlePageView.setText(getString(R.string.label_survey));
        }

        loading = new LoadingDialog();

        //TODO perlu di cek lagi nih coding nya
        if (!mReadOnly) {
            mComplains = HashMultimap.create();
            if (mSurveyData.getComplains() != null && mSurveyData.getComplains().size() > 0) {
                for (ItemComplain ic : mSurveyData.getComplains()) {
                    if (ic.getProductId() != null)
                        mComplains.put(ic.getProductId(), ic);
                }
            }

            mCompetitorPrograms = HashMultimap.create();
            List<ItemCompetitor> tempCompetitor = mSurveyData.getCompetitorPrograms();
            if (tempCompetitor != null && tempCompetitor.size() > 0) {
                for (ItemCompetitor ic : tempCompetitor) {
                    if (ic.getProductId() != null)
                        mCompetitorPrograms.put(ic.getProductId(), ic);
                }
            }

            mCompetitorNotes = new HashMap<>();
            mComplainNotes = new HashMap<>();
            List<ItemNote> tempNotes = mSurveyData.getNotes();
            if (tempNotes != null && tempNotes.size() > 0) {
                for (ItemNote in : tempNotes) {
                    if (in.getNoteType().equals(Constants.NOTE_TYPE_COMPETITOR)) {
                        mCompetitorNotes.put(in.getProductId(), in);
                    } else if (in.getNoteType().equals(Constants.NOTE_TYPE_COMPLAIN)) {
                        mComplainNotes.put(in.getProductId(), in);
                    }
                }
            }
        }

        if (mReadOnly) {
            View btnHolder = mRootView.findViewById(R.id.survey_detail_button_holder);
            btnHolder.setVisibility(View.GONE);

            mSurveyData = mSurveyTable.getById(mSurveyData.getId());

        }

        initTab();
        initViewPager();

        return mRootView;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mTabHost.setCurrentTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //TODO perlu direfactoring karna harus ngubah code lagi kalau posisi tab nya diubah
    //misalnya tab foto poisisinya diubah dari 1 menjadi 0
    @Override
    public void onTabChanged(String s) {

        int position = mTabHost.getCurrentTab();

        if (Constants.DEBUG) {
            Log.d(TAG, "onTabChanged, position = " + position);
        }

        changePage(position);

    }

    private void initViewPager() {

        List<ItemPrice> prices = new ArrayList<>();
        List<ItemImage> images = new ArrayList<>();
        List<ItemComplain> complains = new ArrayList<>();
        List<ItemCompetitor> competitorPrograms = new ArrayList<>();
        List<ItemNote> complainNotes = new ArrayList<>();
        List<ItemNote> competitorNotes = new ArrayList<>();

        if (mSurveyData != null) {
            prices = mSurveyData.getPrices();
            images = mSurveyData.getImages();
            complains = mSurveyData.getComplains();
            competitorPrograms = mSurveyData.getCompetitorPrograms();

            List<ItemNote> notes = mSurveyData.getNotes();
            if (notes != null)
                for (ItemNote note : notes) {
                    if (note.getNoteType().equals(Constants.NOTE_TYPE_COMPETITOR)) {
                        competitorNotes.add(note);
                    } else {
                        complainNotes.add(note);
                    }
                }

            if (competitorPrograms == null)
                competitorPrograms = new ArrayList<>();
            if (complains == null)
                complains = new ArrayList<>();
        }

        List<Fragment> fragments = new ArrayList<>();
        mPriceFragment = SurveyPriceFragment.newInstance(prices, mReadOnly);
        mImageFragment = SurveyImageFragment.newInstance(images, mReadOnly);
        mComplainFragment = SurveyComplainFragment.newInstance(
                new ArrayList<>(complains), new ArrayList<>(complainNotes), mReadOnly);
        mRivalFragment = SurveyCompetitorFragment.newInstance(
                new ArrayList<>(competitorPrograms), new ArrayList<>(competitorNotes), mReadOnly);

        fragments.add(mPriceFragment);
        fragments.add(mImageFragment);
        fragments.add(mComplainFragment);
        fragments.add(mRivalFragment);

        mPagerAdapter = new SurveyPagerAdapter(getChildFragmentManager(), fragments);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.survey_detail_view_pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
    }

    private void initTab() {
        mTabHost = (TabHost) mRootView.findViewById(R.id.survey_detail_tab_host);
        mTabHost.setup();
        TabHost.TabSpec priceTab = mTabHost.newTabSpec(getString(R.string.tag_price));
        priceTab.setContent(new TabFactory(context));
        priceTab.setIndicator(TabFactory.createTabIndicator(context, getString(R.string.icon_list)));
        TabHost.TabSpec imageTab = mTabHost.newTabSpec(getString(R.string.tag_image));
        imageTab.setContent(new TabFactory(context));
        imageTab.setIndicator(TabFactory.createTabIndicator(context, context.getString(R.string.icon_image)));
        TabHost.TabSpec complainsTab = mTabHost.newTabSpec(getString(R.string.tag_complaints));
        complainsTab.setContent(new TabFactory(context));
        complainsTab.setIndicator(TabFactory.createTabIndicator(context, getString(R.string.icon_comment)));
        TabHost.TabSpec rivalTab = mTabHost.newTabSpec(context.getString(R.string.tag_competitor_program));
        rivalTab.setContent(new TabFactory(context));
        rivalTab.setIndicator(TabFactory.createTabIndicator(context, context.getString(R.string.icon_user_secret)));

        mTabHost.addTab(priceTab);
        mTabHost.addTab(imageTab);
        mTabHost.addTab(complainsTab);
        mTabHost.addTab(rivalTab);
        mTabHost.setOnTabChangedListener(this);
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
                SurveyImageFragment fragment = (SurveyImageFragment) mPagerAdapter.getItem(1);
                String imagePath = null;
                Uri imageFileUri = fragment.getImageFileUri();

                if (Constants.DEBUG) Log.d(TAG, "image URI is null: " + (imageFileUri == null));

                if (data != null) {
                    imagePath = MediaStoreUtil.getPath(context, data.getData());
                    if (Constants.DEBUG) Log.d(TAG, "image path from gallery: " + imagePath);
                }

                // This condition means the image was taken from camera
                if (imagePath == null && imageFileUri != null) {
                    imagePath = imageFileUri.getPath();
                    fragment.setImageFileUri(null);

                    if (Constants.DEBUG) Log.d(TAG, "image path from camera: " + imagePath);

                    // Now, compress the image
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    FileOutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(imagePath);
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

                if (imagePath != null) {

                    try {

                        fragment.addImage(new ItemImage(null, imagePath));

                        View child = LayoutInflater.from(context).inflate(R.layout.row_image, fragment.mImageHolderView, false);
                        ImageView imageView = (ImageView) child.findViewById(R.id.row_image_view);
                        imageView.setImageBitmap(ImageDecodeUtil.decodeFile(context, imagePath, imagePath));

                        Button removeBtn = (Button) child.findViewById(R.id.row_image_remove_btn);
                        removeBtn.setTag(R.string.tag_extra_id, imagePath);
                        removeBtn.setOnClickListener(this);

                        fragment.mImageHolderView.addView(child);

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }


                } else {
                    Log.e(TAG, "no image taken");
                    Toast.makeText(context, R.string.error_cannot_get_image, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        String extraId = (String) view.getTag(R.string.tag_extra_id);
        if (extraId != null) {
            SurveyImageFragment fragment = (SurveyImageFragment) mPagerAdapter.getItem(1);
            fragment.removeImageByPath(extraId);
            View parent = (View) view.getParent();
            fragment.mImageHolderView.removeView(parent);
        }
    }

    /*public void showLoading() {
        if (loading != null) {
            loading.setCancelable(false);
            loading.show(getChildFragmentManager(), "loading");
        }
    }

    public void hideLoading() {
        if (loading != null) {
            loading.dismiss();
        }

    }*/

    public void changePage(int position){

        if (!mReadOnly) {
            switch (mCurrentPosition) {
                case 0:
                    mPrices = mPriceFragment.getPrices();
                    break;
                case 1:
                    mImages = mImageFragment.getImages();
                    break;
                case 2:
                    mComplains = HashMultimap.create(mComplainFragment.getComplains());
                    mComplainNotes = new HashMap<>(mComplainFragment.getComplainNotes());
                    break;
                case 3:
                    mCompetitorPrograms = HashMultimap.create(mRivalFragment.getProductProgramsMap());
                    mCompetitorNotes = new HashMap<>(mRivalFragment.getCompetitorNotes());
                    break;
            }

            if (mCurrentPosition != position) {
                mCurrentPosition = position;
            }

            mViewPager.setCurrentItem(position);

            switch (position) {
                case 0:
                    List<ItemPrice> currentPrices = mPriceFragment.getPrices();
                    int currentSize = currentPrices == null ? 0 : currentPrices.size();
                    if (mPrices != null && currentSize == 0) {
                        for (ItemPrice p : mPrices) {
                            mPriceFragment.addToContainer(p);
                        }
                    }
                    break;
                case 1:
                    //mImageFragment.addAllToContainer();
                    break;
                case 2:
                    mComplainFragment.resetForm();
                    mComplainFragment.setComplainMap(mComplains);
                    mComplainFragment.setComplainNotes(mComplainNotes);
                    mComplainFragment.updateState();
                    break;
                case 3:
                    mRivalFragment.resetForm();
                    mRivalFragment.setProductProgramsMap(mCompetitorPrograms);
                    mRivalFragment.setCompetitorNotes(mCompetitorNotes);
                    mRivalFragment.updateState();
                    break;
            }
        } else {
            mViewPager.setCurrentItem(position);

            if (mCurrentPosition != position) {
                mCurrentPosition = position;
            }

            if (position == 0 && mPriceFragment.isVisible() && mPriceFragment.getChildCount() == 0) {
                mPriceFragment.addAllToContainer();
            }
        }


        //set visibility of buttons
        switch (mCurrentPosition){
            case 0:
                mBtnCancel.setVisibility(View.VISIBLE);
                mBtnPrev.setVisibility(View.GONE);
                mBtnNext.setVisibility(View.VISIBLE);
                mBtnSave.setVisibility(View.GONE);
                break;
            case 1:
                mBtnCancel.setVisibility(View.GONE);
                mBtnPrev.setVisibility(View.VISIBLE);
                mBtnNext.setVisibility(View.VISIBLE);
                mBtnSave.setVisibility(View.GONE);
                break;
            case 2:
                mBtnCancel.setVisibility(View.GONE);
                mBtnPrev.setVisibility(View.VISIBLE);
                mBtnNext.setVisibility(View.VISIBLE);
                mBtnSave.setVisibility(View.GONE);
                break;
            case 3:
                mBtnCancel.setVisibility(View.GONE);
                mBtnPrev.setVisibility(View.VISIBLE);
                mBtnNext.setVisibility(View.GONE);
                mBtnSave.setVisibility(View.VISIBLE);
                break;
        }

    }

}

