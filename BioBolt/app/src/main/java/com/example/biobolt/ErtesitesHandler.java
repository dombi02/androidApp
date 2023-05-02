package com.example.biobolt;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class ErtesitesHandler {
    private static final String CHANNEL_ID = "shop_notification_channel";
    public final int NOT_ID = 0;
    public NotificationManager myManager;
    private Context myContext;
    public ErtesitesHandler(Context context){
        this.myContext = context;
        this.myManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();
    }
    private void createChannel(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            return;
        }
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"Shop Notification",NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(Color.rgb(0,255,0));
        channel.setDescription("Értesítések a BioBolt applikációból.");
        this.myManager.createNotificationChannel(channel);
    }
    public void send(String message){
        Intent intent = new Intent(myContext, TermekListaActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(myContext, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(myContext, CHANNEL_ID)
                .setContentTitle("BioBolt kosaradban új termék:")
                .setContentText(message)
                .setSmallIcon(R.drawable.cart)
                .setContentIntent(pendingIntent);

        this.myManager.notify(NOT_ID, builder.build());
    }
    public void cancel() {
        this.myManager.cancel(NOT_ID);
    }
}
