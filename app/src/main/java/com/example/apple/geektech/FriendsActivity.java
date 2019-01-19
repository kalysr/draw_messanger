package com.example.apple.geektech;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.apple.geektech.Utils.CountryToPhonePrfix;
import com.example.apple.geektech.Utils.SharedPreferenceHelper;
import com.example.apple.geektech.Utils.UserListAdapter;
import com.example.apple.geektech.Utils.UserObject;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.util.SharedPreferencesUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int MY_PERMISSIONS_REQUEST_CODE = 1;
    private static final String TAG = "TAG";
    private RecyclerView mUserList;
    private static RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManager;
    public Context context;
    static ArrayList<UserObject> userList;
    static ArrayList<UserObject> contactList;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        context = getApplicationContext();
        userList = new ArrayList<>();
        contactList = new ArrayList<>();
//        sharedPref = getCallingActivity()


        initializerRecycleView();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            getPermissions();
        } else {
            updateUserStatus("online");
            getContactList();
            userFromDb();

        }

    }


    public void getContactList() {
        String IOSprefix = "";

        try {
            IOSprefix = getCountryISO();
            SharedPreferenceHelper.setString(this, "code", IOSprefix);
        } catch (NullPointerException e) {

        }

        IOSprefix = SharedPreferenceHelper.getString(this, "code", null);

        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);


        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace(")", "");
            phone = phone.replace("(", "");

            if (!String.valueOf(phone.charAt(0)).equals("+") && phone.length() > 6)
                phone = IOSprefix + phone.substring(1);

            UserObject mContact = new UserObject(name, phone);
//                getUserDetails(mContact);

            contactList.add(mContact);

        }

    }

    //START SECTION

/*
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

                        UserObject mUser = new UserObject(name, phone, key);


                        if (mContact.getPhone().equals(phone)) {
                            for (UserObject mContactIterator : contactList)
                                if (mContactIterator.getPhone().equals(mUser.getPhone())) {
                                    mUser.setName(mContactIterator.getName());
                                }
                        }


                        userList.add(mUser);


                        mUserListAdapter.notifyDataSetChanged();

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
*/


//END SECTION

    private void userFromDb() {
        final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("users");
        mUserDB.keepSynced(true);

        mUserDB.orderByChild("phone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String phone = "",
                            name = "",
                            key = "",
                            lastSeen = "offline",
                            lastSeenTime = "",
                            lastSeenDate = "";

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if (childSnapshot.child("phone").getValue() != null)
                            phone = childSnapshot.child("phone").getValue().toString();
                        if (childSnapshot.child("name").getValue() != null) {
                            name = childSnapshot.child("name").getValue().toString();
                            key = childSnapshot.child("device_token").getValue().toString();

                            if (childSnapshot.child("userState").getValue() != null) {
                                Log.e(TAG, "onDataChange: User " + phone);
                                lastSeenDate = (String) childSnapshot.child("userState").child("date").getValue();
                                Log.e(TAG, "onDataChange: LastSeenDate  " + lastSeenDate);
                                lastSeen = (String) childSnapshot.child("userState").child("state").getValue();

                                lastSeenTime = (String) childSnapshot.child("userState").child("time").getValue();

                                if (lastSeen.equals("offline")) {
                                    lastSeen = lastSeenTime + " " + lastSeenDate;
                                }
                            } else lastSeen = "offline";

                        }

                        UserObject mUser = new UserObject(name, phone, key, lastSeen);

                        boolean contactExist = false;
                        for (UserObject mContactIterator : contactList) {
                            if (mUser.getPhone().equals(mContactIterator.getPhone())) {
                                contactExist = true;
                                mUser.setName(mContactIterator.getName());
                                SharedPreferenceHelper.setString(FriendsActivity.this, mUser.getPhone(), mUser.getName());
                            }
                        }

                        boolean exist = false;
                        for (UserObject mContactIterator : userList) {
                            if (mUser.getPhone().equals(mContactIterator.getPhone())) {
                                exist = true;
                                break;
                            }
                        }

                        if (!exist && contactExist) {
                            userList.add(mUser);
                        }
                    }
                    mUserListAdapter.notifyDataSetChanged();

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


        mUserListAdapter = new UserListAdapter(this, userList);
        mUserList.setAdapter(mUserListAdapter);

        mUserListLayoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
        mUserList.setLayoutManager(mUserListLayoutManager);

    }

    private String getCountryISO() {
        String iso = null;

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);

        if (telephonyManager.getNetworkCountryIso() != null)
            if (!telephonyManager.getNetworkCountryIso().equals(""))
                iso = telephonyManager.getNetworkCountryIso();


        return CountryToPhonePrfix.getPhone(iso);
    }

    @Override
    public void onClick(View v) {

    }

    public void updateUserStatus(String state) {
        String saveCurrentTime, saveCurrentDate;
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd:MM:yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineState = new HashMap<>();
        onlineState.put("time", saveCurrentTime);
        onlineState.put("date", saveCurrentDate);
        onlineState.put("state", state);

        rootRef.child("users").child(userId).child("userState").
                updateChildren(onlineState);


    }

    @Override
    protected void onStart() {
        super.onStart();

        updateUserStatus("online");
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (userId != null) {
            updateUserStatus("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userId != null) {
            updateUserStatus("offline");
        }
    }
}
