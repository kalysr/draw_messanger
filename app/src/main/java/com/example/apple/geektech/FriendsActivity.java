package com.example.apple.geektech;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.apple.geektech.Utils.CountryToPhonePrfix;
import com.example.apple.geektech.Utils.UserListAdapter;
import com.example.apple.geektech.Utils.UserObject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int MY_PERMISSIONS_REQUEST_CODE = 1;
    private static final String TAG = "TAG" ;
    private RecyclerView mUserList;
    private RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManager;
    public Context context;
    ArrayList<UserObject> userList, contactList;

    private String sender_user_id,receiver_user_id;
    private DatabaseReference notificationsReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        context = getApplicationContext();
        userList = new ArrayList<>();
        contactList = new ArrayList<>();
        initializerRecycleView();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            getPermissions();
        } else {
            getContactList();
        }

    }


    void getContactList() {

        String IOSprefix = getCountryISO();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace(")", "");
            phone = phone.replace("(", "");

            if (!String.valueOf(phone.charAt(0)).equals("+"))
                phone = IOSprefix + phone.substring(1);

            UserObject mContact = new UserObject(name, phone);

            getUserDetails(mContact);
            contactList.add(mContact);

        }
    }

    private void getUserDetails(final UserObject mContact) {

        final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("users");
        mUserDB.keepSynced(true);

        Query query = mUserDB.orderByChild("phone").equalTo(mContact.getPhone());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String phone = "",
                            name = "",
                            key = "";

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if (childSnapshot.child("phone").getValue() != null)
                            phone = childSnapshot.child("phone").getValue().toString();
                        if (childSnapshot.child("name").getValue() != null) {
                            name = childSnapshot.child("name").getValue().toString();
                            key = childSnapshot.getKey();
                        }

                        UserObject mUser = new UserObject(name, phone,key );

                        if(mContact.getPhone().equals(phone)){
                            for (UserObject mContactIterator : contactList)
                                if (mContactIterator.getPhone().equals(mUser.getPhone())) {
                                    mUser.setName(mContactIterator.getName());
                                }
                        }

                        userList.add(mUser);
                        mUserListAdapter.notifyDataSetChanged();

                        return;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContactList();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(context, "PERSMISSION DENIED", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, MY_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void initializerRecycleView() {
        mUserList = findViewById(R.id.userlistRV);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);


        mUserListAdapter = new UserListAdapter(context,userList);
        mUserList.setAdapter(mUserListAdapter);

        mUserListLayoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
        mUserList.setLayoutManager(mUserListLayoutManager);
    }

    private String getCountryISO() {
        String iso = null;

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);

        if (telephonyManager.getNetworkCountryIso() != null)
            if (!telephonyManager.getNetworkCountryIso().toString().equals(""))
                iso = telephonyManager.getNetworkCountryIso().toString();


        return CountryToPhonePrfix.getPhone(iso);
    }

    @Override
    public void onClick(View v) {

    }
}
