package com.example.apple.geektech;

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

import com.example.apple.geektech.Utils.SharedPreferenceHelper;
import com.example.apple.geektech.activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String sender_token="";

    public static final int TYPE_INVITE_REQUEST = 1;
    public static final int TYPE_NOTIFICATION = 2;
    public static final int TYPE_INVITE_ACCEPTED = 3;
    public static final int TYPE_INVITE_DECLINED = 4;

    String phone,name,body;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData() != null) {

            Map data = remoteMessage.getData();
            int type = Integer.valueOf(data.get("type").toString());
            phone = data.get("phone").toString();
            switch (type){
                case TYPE_INVITE_REQUEST:
                    phone = data.get("phone").toString();
                    name = SharedPreferenceHelper.getString(getApplicationContext(), phone, phone);
                    body = name + " wants to send a message.";
                    sender_token = data.get("sender_token").toString();
//                    Log.e("TAG", "onMessageReceived: not  "+body );
                    sendNotification(body, "Notification",TYPE_INVITE_REQUEST);
                    break;
                case TYPE_INVITE_ACCEPTED:
                    phone = data.get("phone").toString();
                    name = SharedPreferenceHelper.getString(getApplicationContext(), phone, phone);
                    body = name + " has been accepted.";
                    sendNotification(body, "Notification", TYPE_INVITE_ACCEPTED);
                    break;
                case TYPE_INVITE_DECLINED:
                    phone = data.get("phone").toString();
                    name = SharedPreferenceHelper.getString(getApplicationContext(), phone, phone);
                    body = name + " has been declined.";
                    sendNotification(body, "Notification",TYPE_INVITE_ACCEPTED);
                    break;
                case TYPE_NOTIFICATION:
                    sendNotification(data.get("body").toString(), data.get("title").toString(),TYPE_NOTIFICATION);
                    break;
            }
        }else {
            Log.e("TAG", "onMessageReceived: data  NULL" );
        }
    }

    private void sendNotification(String messageBody, String title, int type) {


        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("phone",phone);

        Intent intentAccept = new Intent(this, MainActivity.class);
        intentAccept.putExtra("accepted", true);
        intentAccept.putExtra("sender_token", sender_token);
        Intent intentDecline = new Intent(this, MainActivity.class);
        intentDecline.putExtra("accepted", false);
        intentDecline.putExtra("sender_token", sender_token);

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
                        .setChannelId(channelId);
        if (type == 1 || type == 2){
            notificationBuilder
                    .addAction(R.mipmap.ic_launcher, "Accept", pendingAcceptIntent)
                    .addAction(R.mipmap.ic_launcher, "Decline", pendingDeclineIntent);
        }



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

}
