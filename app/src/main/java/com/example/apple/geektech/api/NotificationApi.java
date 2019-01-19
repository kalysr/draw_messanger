package com.example.apple.geektech.api;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.apple.geektech.MainActivity;
import com.example.apple.geektech.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class NotificationApi {

    private static DatabaseReference notificationReference;

    public static void send(Context context, Data data) {
        if (notificationReference == null) {
            notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
            notificationReference.keepSynced(true);
        }
        notificationReference.push().setValue(data.toMap());
    }
    public static void send(String receiverToken,int type, Map data) {
        if (notificationReference == null) {
            notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
            notificationReference.keepSynced(true);
        }
        data.put("receiverToken", receiverToken);
        data.put("type", String.valueOf(type));
        notificationReference.push().setValue(data);
    }

    public static class Data {
        private String title;
        private String body;
        private String recieverToken;
        private String senderToken;

        public Data(String title, String body, String recieverToken, String senderToken) {
            this.title = title;
            this.body = body;
            this.recieverToken = recieverToken;
            this.senderToken = senderToken;
        }

        public Map toMap() {
            Map data = new HashMap();
            data.put("title", title);
            data.put("body", body);
            data.put("receiverToken", recieverToken);
            return data;
        }
    }
}
