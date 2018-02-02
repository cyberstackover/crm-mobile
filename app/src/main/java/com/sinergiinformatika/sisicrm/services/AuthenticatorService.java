package com.sinergiinformatika.sisicrm.services;

import android.accounts.Account;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.sinergiinformatika.sisicrm.Authenticator;
import com.sinergiinformatika.sisicrm.Constants;

public class AuthenticatorService extends Service {

    private static final String TAG = AuthenticatorService.class.getSimpleName();
    private Authenticator mAuthenticator;

    public AuthenticatorService() {
    }

    public static Account getAccount() {
        if (Constants.DEBUG) Log.d(TAG, "getting account for sync");
        final String accountName = Authenticator.ACCOUNT_NAME;
        return (new Account(accountName, Authenticator.ACCOUNT_TYPE));
    }

    @Override
    public void onCreate() {
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
