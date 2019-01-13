package com.example.apple.geektech.Utils;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Offline_functions extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
