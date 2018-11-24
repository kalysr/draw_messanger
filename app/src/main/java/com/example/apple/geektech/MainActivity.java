package com.example.apple.geektech;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.apple.geektech.paint.PaintView;
import com.example.apple.geektech.paint.UserPath;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    PaintView paintView;
    Button clearButton, redrawBtn, clearFramesBtn;
    String UserId = null;
    UserPath selfUserPath = null;
    public static String USER_ID = "USER_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseHelper.init(this);
        init();
        initUserId();
        initEvents();
    }

    private void initUserId() {
        this.UserId = SharedPreferenceHelper.getString(this, USER_ID, null);
        if (this.UserId == null) {
            this.UserId = FirebaseHelper.getInstance().addUser();
            SharedPreferenceHelper.setString(this, USER_ID, this.UserId);
        }
    }

    private void init() {
        paintView = findViewById(R.id.main_paint_view);
        clearButton = findViewById(R.id.clear_canvas);
        redrawBtn = findViewById(R.id.redraw);
        clearFramesBtn = findViewById(R.id.clear_frames);
    }

    private void initEvents() {

        final DatabaseReference mDatabase = FirebaseHelper.getInstance().getDatabase();

        selfUserPath = new UserPath(UserId, paintView);
        paintView.setSelfUser(selfUserPath);
        selfUserPath.setListener(new UserPath.Listener() {
            @Override
            public void onAddFrame(PaintView.Frame frame) {
                String data = SerializationUtil.objectToString(frame);
                mDatabase.child("users").child(UserId).child("data").setValue(data);
            }

            @Override
            public void onClearCanvas() {
                mDatabase.child("users").child(UserId).child("action").setValue(UserPath.ACTION_CLEAR_CANVAS);
                mDatabase.child("users").child(UserId).child("action").setValue("");
            }

            @Override
            public void onClearFrames() {
                mDatabase.child("users").child(UserId).child("action").setValue(UserPath.ACTION_CLEAR_FRAMES);
                mDatabase.child("users").child(UserId).child("action").setValue("");
            }
        });

        FirebaseHelper.getInstance().getDatabase().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                System.out.println("key:"+dataSnapshot.getKey());
                System.out.println("user_id:"+UserId);
                if(!dataSnapshot.getKey().equals(UserId))
                    addUserEvents(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selfUserPath.clearCanvas();
            }
        });

        redrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selfUserPath.redrawFrames();
            }
        });

        clearFramesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selfUserPath.clearFrames();
            }
        });

        clearFramesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selfUserPath.clearFrames();
            }
        });
    }

    private void addUserEvents(final String userId) {
        final DatabaseReference mDatabase = FirebaseHelper.getInstance().getDatabase();

        final UserPath userPath = new UserPath(userId, paintView);
        paintView.addUser(userPath);

        mDatabase.child("users").child(userId).child("action").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object object = dataSnapshot.getValue();
                if (object != null) {
                    String action = object.toString();
                    switch (action) {
                        case UserPath.ACTION_CLEAR_CANVAS:
                            userPath.clearCanvas();
                            break;
                        case UserPath.ACTION_CLEAR_FRAMES:
                            userPath.clearFrames();
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("users").child(userId).child("data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object object = dataSnapshot.getValue();
                if (object != null) {
                    PaintView.Frame frame = (PaintView.Frame) SerializationUtil.stringToObject(object.toString());
                    if (frame != null) {
                        userPath.drawFrame(frame);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
