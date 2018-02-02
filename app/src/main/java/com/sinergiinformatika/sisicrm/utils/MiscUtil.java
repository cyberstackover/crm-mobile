package com.sinergiinformatika.sisicrm.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.SplashActivity;

/**
 * Created by Mark on 05-Nov-15.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class MiscUtil {
    /**
     * Simple method to compare numerical version strings. Version string format should be x.y.z
     * with section length (dot) as many as needed and each section contains
     * greater-than-or-equal-to-zero numerical value only. Should one version contains more
     * sections than the other, it will be treated as a sub-version of the other, applying 0 to
     * the missing sections.
     *
     * @param v1 First version string to compare
     * @param v2 Second version string to compare
     * @return - zero if the two versions are equal
     * - positive integer if the first version is numerically greater
     * - negative integer if the second version is numerically greater
     */
    public static int versionCompare(String v1, String v2) throws NumberFormatException {
        String[] ver1 = v1.split("\\.");
        String[] ver2 = v2.split("\\.");

        int i = 0;
        while (i < ver1.length && i < ver2.length
               && Integer.parseInt(ver1[i]) == Integer.parseInt(ver2[i])) {
            i++;
        }

        if (i < ver1.length && i < ver2.length) {
            return Integer.parseInt(ver1[i]) - Integer.parseInt(ver2[i]);
        } else {
            if (ver1.length == ver2.length) {
                return 0;
            }

            if (ver1.length > ver2.length) {
                for (int idx = i; idx < ver1.length; i++) {
                    if (Integer.parseInt(ver1[idx]) > 0) {
                        return 1;
                    }
                }

                return 0;
            } else {
                for (int idx = i; idx < ver2.length; i++) {
                    if (Integer.parseInt(ver2[idx]) > 0) {
                        return -1;
                    }
                }

                return 0;
            }
        }
    }

    public static void showNotificationMessage(Context mContext, String title, String content,
                                               NotificationCompat.InboxStyle inboxStyle) {
        Intent notificationIntent = new Intent(
                mContext.getApplicationContext(), SplashActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.logo_white_small)
                        .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),
                                                                   R.drawable.ic_launcher))
                        .setContentTitle(title)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setOnlyAlertOnce(true)
                        .setContentIntent(contentIntent);
        if (!TextUtils.isEmpty(content)) {
            builder.setContentText(content);
        }

        if (inboxStyle != null) {
            builder.setStyle(inboxStyle);
        }

        NotificationManager manager = (NotificationManager) mContext.getSystemService(
                Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }
}
