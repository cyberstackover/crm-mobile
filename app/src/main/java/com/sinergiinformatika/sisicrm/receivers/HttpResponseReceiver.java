package com.sinergiinformatika.sisicrm.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by wendi on 06-Jan-15.
 */
public class HttpResponseReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("HttpResponseReceiver","onReceive");
        CharSequence intentData = intent.getCharSequenceExtra("message");
        //Toast.makeText(context, intentData, Toast.LENGTH_LONG).show();
    }
}
