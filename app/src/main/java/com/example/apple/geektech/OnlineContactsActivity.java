package com.example.apple.geektech;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;


import com.example.apple.geektech.Utils.UserListAdapter;
import com.example.apple.geektech.Utils.UserObject;

import java.util.ArrayList;

public class OnlineContactsActivity extends AppCompatActivity {

    private RecyclerView mUserList;
    private RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManager;

    ArrayList<UserObject> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_contacts);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .7));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

        userList = new ArrayList<>();
        initializerRecycleView();
        getContactList();


    }


    void getContactList() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);


        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String maskedPhone = phone.replace("+996", "0");
//            try {





//                if (user != null) {

                    UserObject mContact = new UserObject(name, phone);
                    userList.add(mContact);
                    mUserListAdapter.notifyDataSetChanged();
//                }
//            } catch (FirebaseAuthException e) {
//                e.printStackTrace();
//            }


        }


    }


    private void initializerRecycleView() {
        mUserList = findViewById(R.id.onlineUserListRV);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);

        mUserListLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        mUserList.setLayoutManager(mUserListLayoutManager);

        mUserListAdapter = new UserListAdapter(getApplicationContext(),userList);

        mUserList.setAdapter(mUserListAdapter);
    }
}
