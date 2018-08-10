package com.bws.harsley.Firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bws.harsley.Commons.Common;
import com.bws.harsley.PushNotificationActivity;
import com.bws.harsley.R;
import com.bws.harsley.Utils.DatabaseHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    DatabaseHelper db = DatabaseHelper.getInstance(this);

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

       // UserModel userModel = new UserModel();
       // db.insertUserDetils(userModel);

        // Check if message contains a data payload.
        /*if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }*/

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        if(remoteMessage != null){
            Common.notificationCount = Common.notificationCount + 1;
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Use for start notification activity if click on notification
        Intent intent = new Intent(this, PushNotificationActivity.class);
        Intent intentArr[] = {intent};
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, intentArr, 0);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_push);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentView.setTextViewText(R.id.title, "StarLab");
       // contentView.setTextViewText(R.id.textDate, "16-5-18");
        contentView.setTextViewText(R.id.text, "Change in schedule for maintenance activity\n" +
                "New schedule maintenance at 4:30 p.m");

        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setCustomBigContentView(contentView);


        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(1, notification);

    }
}