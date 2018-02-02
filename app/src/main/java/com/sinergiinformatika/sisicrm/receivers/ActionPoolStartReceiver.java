package com.sinergiinformatika.sisicrm.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sinergiinformatika.sisicrm.services.ActionPoolService;


public class ActionPoolStartReceiver extends BroadcastReceiver {
    public ActionPoolStartReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, ActionPoolService.class);
        //TODO set action
        //service.setAction(ActionPoolService.ACTION_PUSH_SURVEY);
        context.startService(service);
    }
}
