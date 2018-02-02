package com.sinergiinformatika.sisicrm.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.data.providers.CRMContentProvider;
import com.sinergiinformatika.sisicrm.services.AuthenticatorService;

/**
 * Created by Mark on 2/18/2015.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class SyncUtil {
    private static final long SYNC_FREQUENCY = 60 * 60;  // change to 1 hour (in seconds)
    private static final String PREF_SETUP_COMPLETE = "setup_complete";

    public static void createSyncAccount(Context context) {
        boolean newAccount = false;
        boolean setupComplete = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_SETUP_COMPLETE, false);

        // Create account, if it's missing. (Either first run, or user has deleted account.)
        Account account = AuthenticatorService.getAccount();
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, CRMContentProvider.AUTHORITY, 1);
            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, CRMContentProvider.AUTHORITY, true);
            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.SYNC_KEY_PERIODIC, true);
            ContentResolver
                    .addPeriodicSync(account, CRMContentProvider.AUTHORITY, bundle, SYNC_FREQUENCY);
            newAccount = true;
        }

        // Schedule an initial sync if we detect problems with either our account or our local
        // data has been deleted. (Note that it's possible to clear app data WITHOUT affecting
        // the account list, so wee need to check both.)
        if (newAccount || !setupComplete) {
            triggerRefresh();
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putBoolean(PREF_SETUP_COMPLETE, true);
        }
    }

    public static void triggerRefresh() {
        Bundle b = new Bundle();
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        b.putBoolean(Constants.SYNC_KEY_PERIODIC, true);
        ContentResolver.requestSync(AuthenticatorService.getAccount(), CRMContentProvider.AUTHORITY, b);
    }

    public static void stopSync() {
        ContentResolver.cancelSync(AuthenticatorService.getAccount(), CRMContentProvider.AUTHORITY);
        /*if (ContentResolver.isSyncPending(AuthenticatorService.getAccount(), CRMContentProvider
        .AUTHORITY) ||
            ContentResolver.isSyncActive(AuthenticatorService.getAccount(), CRMContentProvider
            .AUTHORITY)) {
            ContentResolver.cancelSync(AuthenticatorService.getAccount(), CRMContentProvider
            .AUTHORITY);
            ContentResolver.setIsSyncable(AuthenticatorService.getAccount(),
                                          CRMContentProvider.AUTHORITY, 0);

            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false);

            ContentResolver.removePeriodicSync(
                    AuthenticatorService.getAccount(), CRMContentProvider.AUTHORITY, bundle);
        }*/
    }
}
