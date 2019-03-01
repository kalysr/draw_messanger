package com.example.apple.geektech.Utils;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

    private static FirebaseHelper instance;

    public DatabaseReference getDatabase() {
        return mDatabase;
    }

    private DatabaseReference mDatabase;

    public FirebaseHelper(Context context){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Context context1 = context;
    }

    public static void init(Context context) {
         instance = new FirebaseHelper(context);
    }

    public static FirebaseHelper getInstance() {
        return instance;
    }

}
