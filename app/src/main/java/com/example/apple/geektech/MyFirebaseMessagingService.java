package com.example.apple.geektech;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.apple.geektech.Utils.UserObject;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.example.apple.geektech.FriendsActivity.contactList;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData()!=null) {
            String phone = remoteMessage.getData().toString().substring(7, 20);
            String name = getUserName(phone);

//            String body = remoteMessage.getNotification().getBody()
  //                  .substring(13, remoteMessage.getNotification().getBody().length());

            sendNotification(name + " wants to send a message.", "Title" );

            Log.e("TAG", "onMessageReceived: " + remoteMessage.getData().toString().substring(7, 20));
        }

    }

    private void sendNotification(String messageBody, String title) {

        Log.e("TAG", "sendNotification: "  );

        Intent intent = new Intent(this, MainActivity.class);

        Intent intentAccept = new Intent(this, MainActivity.class);
        intentAccept.putExtra("accepted", true);
        Intent intentDecline = new Intent(this, MainActivity.class);
        intentDecline.putExtra("accepted", false);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        PendingIntent pendingAcceptIntent = PendingIntent.getActivity(this, 10 /* Request code */, intentAccept,
                PendingIntent.FLAG_ONE_SHOT);
        PendingIntent pendingDeclineIntent = PendingIntent.getActivity(this, 11 /* Request code */, intentDecline,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp_icon)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setChannelId(channelId)
                        .addAction(R.mipmap.ic_launcher, "Accept", pendingAcceptIntent)
                        .addAction(R.mipmap.ic_launcher, "Decline", pendingDeclineIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public static String getUserName(String phone) {
        for (UserObject mContactIterator : contactList)
            if (mContactIterator.getPhone().equals(phone))
                return mContactIterator.getName();

        return phone;

    }

}
