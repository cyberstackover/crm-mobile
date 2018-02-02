package com.sinergiinformatika.sisicrm.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.newrelic.agent.android.NewRelic;
import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.adapters.SyncAdapter;

public class SyncService extends Service {

    private static final Object syncAdapterLock = new Object();
    private static SyncAdapter syncAdapter;

    public SyncService() {
    }

    @Override
    public void onCreate() {
        synchronized (syncAdapterLock) {
            if (syncAdapter == null)
                syncAdapter = new SyncAdapter(getApplicationContext(), true);
        }

        if (!Constants.DEBUG) {
            NewRelic.withApplicationToken(
                    "AA8296b4f276929d7d9a2b5a83130fc937295211a7"
            ).start(this.getApplication());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
