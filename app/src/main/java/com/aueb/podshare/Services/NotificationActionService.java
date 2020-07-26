package com.aueb.podshare.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationActionService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("EPISODES_EPISODES")
                .putExtra("action_name", intent.getAction()));
    }
}
