package com.example.apple.geektech;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.apple.geektech.paint.GridLayer;
import com.example.apple.geektech.paint.PaintView;
import com.example.apple.geektech.paint.UserPath;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    PaintView paintView;
    ImageButton clearButton,redrawBtn,clearFramesBtn,colorPickerBtn;
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
        getSupportActionBar().hide();
        paintView = findViewById(R.id.main_paint_view);
        clearButton = findViewById(R.id.clear_canvas);
        redrawBtn = findViewById(R.id.redraw);
        clearFramesBtn = findViewById(R.id.clear_frames);
        colorPickerBtn = findViewById(R.id.color_picker);
    }

    private void initEvents() {

        final DatabaseReference mDatabase = FirebaseHelper.getInstance().getDatabase();

        selfUserPath = new UserPath(UserId, paintView);
        paintView.setSelfLayer(selfUserPath);
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

            @Override
            public void onColorChanged(int color) {
                mDatabase.child("users").child(UserId).child("config").child("color").setValue(color);
            }

            @Override
            public void onCircleSizeChanged(float circleSize) {
                mDatabase.child("users").child(UserId).child("config").child("circle_size").setValue(circleSize);
            }

            @Override
            public void onStrokeWidthChanged(float strokeWidth) {
                mDatabase.child("users").child(UserId).child("config").child("stroke_width").setValue(strokeWidth);
            }
        });

        FirebaseHelper.getInstance().getDatabase().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (!dataSnapshot.getKey().equals(UserId))
                    addUserEvents(dataSnapshot.getKey(), dataSnapshot);
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
                paintView.clearAllUserCanvas();
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
                paintView.clearAllUserFrames();
            }
        });

        colorPickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*ColorPickerDialog.newBuilder()
                        .setColor(Color.BLACK)
                        .show(MainActivity.this);*/
            }
        });
    }

    private void addUserEvents(final String userId, DataSnapshot dataSnapshot) {
        final DatabaseReference mDatabase = FirebaseHelper.getInstance().getDatabase();

        final UserPath userPath = new UserPath(userId, paintView);
        paintView.addLayer(userPath);
        paintView.addLayer(new GridLayer("grid",paintView));


        mDatabase.child("users").child(userId).child("action").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object object = dataSnapshot.getValue();
                if (object != null) {
                    String action = object.toString();
                    switch (action) {
                        case UserPath.ACTION_CLEAR_CANVAS:
                            userPath._clearCanvas();
                            selfUserPath._clearCanvas();
                            break;
                        case UserPath.ACTION_CLEAR_FRAMES:
                            userPath._clearFrames();
                            selfUserPath._clearFrames();
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("users").child(userId).child("data").setValue("");
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

        mDatabase.child("users").child(userId).child("config").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("color").getValue() != null &&
                        dataSnapshot.child("circle_size").getValue() != null &&
                        dataSnapshot.child("stroke_width").getValue() != null) {
                    userPath.setPenColor((int) dataSnapshot.child("color").getValue());
                    userPath.setCircleSize((float) dataSnapshot.child("circle_size").getValue());
                    userPath.setStrokeWidth((float) dataSnapshot.child("stroke_width").getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
