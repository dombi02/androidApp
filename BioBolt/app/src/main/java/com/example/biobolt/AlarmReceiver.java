package com.example.biobolt;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new ErtesitesHandler(context).send("Magyarország Bio termékei csak rád várnak a BioBolt-ban! :)");
        }
}