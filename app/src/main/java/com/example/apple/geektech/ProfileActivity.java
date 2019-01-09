package com.example.apple.geektech;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.geektech.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    Button sendRequestBtn,declineFrienRequestBtn;
    ImageView profilePhoto;
    TextView statusTV,contactNameTV;

    DatabaseReference userReference;
    DatabaseReference friendRequestReference, notificationsReference;
    private String CURRENT_STATE;
    String receiver_id;
    String sender_id;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();

        userReference.child(receiver_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String photo = dataSnapshot.child("user_photo").getValue().toString();
//                String status = dataSnapshot.child("user_status").getValue().toString();

//                statusTV.setText(status);

                friendRequestReference.child(sender_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(receiver_id)){
                                    String req_type = dataSnapshot.child(receiver_id).child("request_type").getValue().toString();
                                    if (req_type.equals("sent")) {
                                        CURRENT_STATE = "request_sent";
                                    }
                                    else CURRENT_STATE = "";
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestBtn.setEnabled(false);

                if (CURRENT_STATE == "not_friend"){
                    sendFriendRequest();
                }
            }
        });

    }

    private void sendFriendRequest() {
        friendRequestReference.child(sender_id).child(receiver_id).child("request_type")
                              .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        friendRequestReference.child(receiver_id).child(sender_id)
                                .child("request_type").setValue("receiver")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){


                                    sendRequestBtn.setEnabled(true);
                                    CURRENT_STATE = "request_sent";
                                    sendRequestBtn.setText("Cancel friend request");



                                    Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
                                    intent.putExtra("name",getIntent().getStringExtra("name"));

                                    startActivity(intent);

                                }
                            }
                        });
                    }
            }
        });
    }

    private void init() {
        sendRequestBtn = findViewById(R.id.send_friend_request_btn);
        declineFrienRequestBtn = findViewById(R.id.decline_friend_request_btn);
        profilePhoto = findViewById(R.id.prifileImage);
        contactNameTV = findViewById(R.id.contactNameTV);
        statusTV = findViewById(R.id.status_TV);

        notificationsReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notificationsReference.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        sender_id = mAuth.getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference().child("users");
        friendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");


        CURRENT_STATE = "not_friend";

        getIntentExtras();



    }

    private void getIntentExtras() {

        if (getIntent().hasExtra("name")){
            contactNameTV.setText(getIntent().getStringExtra("name"));
            receiver_id = getIntent().getStringExtra("receiver_id");

            Toast.makeText(this, receiver_id +"", Toast.LENGTH_SHORT).show();

        }
    }
}
