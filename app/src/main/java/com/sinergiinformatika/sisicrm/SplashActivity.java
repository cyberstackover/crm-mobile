package com.sinergiinformatika.sisicrm;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.newrelic.agent.android.NewRelic;
import com.nineoldandroids.animation.ObjectAnimator;
import com.sinergiinformatika.sisicrm.conn.RestClient;
import com.sinergiinformatika.sisicrm.conn.RestResponseHandler;
import com.sinergiinformatika.sisicrm.utils.LocaleUtil;
import com.sinergiinformatika.sisicrm.utils.MiscUtil;
import com.sinergiinformatika.sisicrm.utils.User;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class SplashActivity extends Activity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private TextView statusText;
    private ProgressBar statusProgress;
    private AsyncHttpResponseHandler buildDownloadHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            if (Constants.DEBUG) Log.d(TAG, "file downloaded");
            File file = new File(SplashActivity.this.getExternalCacheDir(), "update.apk");
            try {
                FileOutputStream outputStream = new FileOutputStream(file, false);
                outputStream.write(bytes);
                outputStream.close();

                promptInstall(file.getAbsolutePath());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
                versionCheckFailed("Gagal mengunduh file update");
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Log.e(TAG, throwable.getMessage(), throwable);
            versionCheckFailed("Gagal mengunduh file update");
        }

        @Override
        public void onStart() {
            super.onStart();

            statusText.setText(R.string.message_downloading);
            statusText.setVisibility(View.VISIBLE);

            statusProgress.setIndeterminate(false);
            statusProgress.setVisibility(View.VISIBLE);
        }

        @Override
        public void onProgress(int bytesWritten, int totalSize) {
            super.onProgress(bytesWritten, totalSize);

            int progress = (int) ((bytesWritten * 100.0f) / totalSize);
//            statusProgress.setProgress(progress);
            ObjectAnimator animator = ObjectAnimator.ofInt(statusProgress, "progress", progress);
            animator.setDuration(500);
            animator.setInterpolator(new LinearInterpolator());
            animator.start();
            if (Constants.DEBUG) {
                Log.d(TAG, "progress: " + progress + "% -> " + bytesWritten + " of " + totalSize);
            }
        }
    };

    private void promptInstall(String absolutePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(
                Uri.parse("file://" + absolutePath), "application/vnd.android.package-archive");
        startActivity(intent);
//        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Constants.DEBUG) {
            NewRelic.withApplicationToken(
                    "AA8296b4f276929d7d9a2b5a83130fc937295211a7"
            ).start(this.getApplication());
        }

        LocaleUtil.changeLocale(getApplicationContext(), Constants.DEFAULT_LOCALE);

        setContentView(R.layout.activity_splash);

        statusText = (TextView) findViewById(R.id.splash_status_text);
        statusProgress = (ProgressBar) findViewById(R.id.splash_progress);

        checkNewVersion();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RestClient.cancelRequests(this);
    }

    private void checkNewVersion() {
        RestClient.getInstance(this, new RestResponseHandler(this) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (RestResponseHandler.isSuccess(response)) {
                        JSONObject data = getData(response);
                        String version = data.getString("version");

                        if (MiscUtil.versionCompare(version, BuildConfig.VERSION_NAME) > 0) {
                            RestClient.getInstance(SplashActivity.this, buildDownloadHandler)
                                      .getLatestBuild(data.getString("url"), true);
                        } else {
                            checkLog();
                        }
                    } else {
                        throw new IllegalArgumentException("Response status error");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "response: " + response.toString());
                    Log.e(TAG, e.getMessage(), e);

                    versionCheckFailed("Gagal mengecek versi");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                versionCheckFailed("Gagal mengecek versi");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                versionCheckFailed("Gagal mengecek versi");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                versionCheckFailed("Gagal mengecek versi");
            }

            @Override
            public void onStart() {
                super.onStart();

                statusText.setText(R.string.message_version_check);
                statusText.setVisibility(View.VISIBLE);

                statusProgress.setIndeterminate(true);
                statusProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                super.onFinish();

                statusText.setVisibility(View.INVISIBLE);
                statusProgress.setVisibility(View.GONE);
            }
        }).getLatestVersion(true);
    }

    private void versionCheckFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        checkLog();
    }

    private void checkLog() {
        if (User.getInstance(this).isLoggedIn()) {
            invokeMainApp();
        } else {
            invokeLogin();
        }
    }

    private void invokeLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void invokeMainApp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
