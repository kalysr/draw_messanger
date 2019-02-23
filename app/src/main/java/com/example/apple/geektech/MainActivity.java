package com.example.apple.geektech;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.apple.geektech.Utils.FirebaseHelper;
import com.example.apple.geektech.Utils.SerializationUtil;
import com.example.apple.geektech.Utils.SharedPreferenceHelper;
import com.example.apple.geektech.api.NotificationApi;
import com.example.apple.geektech.paint.GridLayer;
import com.example.apple.geektech.paint.PaintView;
import com.example.apple.geektech.paint.UserPath;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import yuku.ambilwarna.AmbilWarnaDialog;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    PaintView paintView;
    ImageButton clearButton, redrawBtn, undoButton, colorPickerBtn, gridBtn, contactBtn, historyBtn, onlineContactsBtn, signOut;
    String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String sender_id = "";
    DatabaseReference rootRef =
            FirebaseDatabase.getInstance().getReference();
    UserPath selfUserPath = null;
    public static String USER_ID = "USER_ID";
    boolean pressed = true;
    private Integer sender_height = 0;
    private Integer sender_width = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseHelper.init(this);
        init();
        initUserId();
        getIncomingIntent();
        initEvents();


    }

    private void initUserId() {

        this.UserId = SharedPreferenceHelper.getString(this, USER_ID, null);
        this.UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (this.UserId == null) {
            this.UserId = FirebaseAuth.getInstance().getUid();
            SharedPreferenceHelper.setString(this, USER_ID, this.UserId);
        }
    }


    private void init() {
        paintView = findViewById(R.id.main_paint_view);
        clearButton = findViewById(R.id.clear_canvas);
        redrawBtn = findViewById(R.id.redrawBtn);
        undoButton = findViewById(R.id.undo_button);
        colorPickerBtn = findViewById(R.id.color_picker);
        gridBtn = findViewById(R.id.gridBtn);
        contactBtn = findViewById(R.id.contactBtn);
        onlineContactsBtn = findViewById(R.id.onlineContactsBtn);
        historyBtn = findViewById(R.id.historyBtn);
        signOut = findViewById(R.id.signOut);

        clearButton.setOnClickListener(this);
        colorPickerBtn.setOnClickListener(this);
        contactBtn.setOnClickListener(this);
        gridBtn.setOnClickListener(this);
        historyBtn.setOnClickListener(this);
        redrawBtn.setOnClickListener(this);
        onlineContactsBtn.setOnClickListener(this);
        signOut.setOnClickListener(this);
        undoButton.setOnClickListener(this);

    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra("name")) {
            setTitle(getIntent().getStringExtra("name"));
            sender_id = getIntent().getStringExtra("receiver_id");
        }
        if (getIntent().hasExtra("accepted")) {
            Map data = new HashMap();
            data.put("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
            data.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());

            boolean accepted = getIntent().getBooleanExtra("accepted", false);
            int type = 0;
            if (accepted) {
                type = MyFirebaseMessagingService.TYPE_INVITE_ACCEPTED;
            } else {
                type = MyFirebaseMessagingService.TYPE_INVITE_DECLINED;
            }

            Toast.makeText(this, "Friend request has been " + (accepted ? "accepted" : "declined"), Toast.LENGTH_LONG).show();
            String receiverToken = getIntent().getStringExtra("sender_token");
            NotificationApi.send(receiverToken, type, data);
        }
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
                mDatabase.child("users").child(UserId).child("config").child("color").setValue(String.valueOf(color));
            }

            @Override
            public void onCircleSizeChanged(float circleSize) {
                mDatabase.child("users").child(UserId).child("config").child("circle_size").setValue(circleSize);
            }

            @Override
            public void onStrokeWidthChanged(float strokeWidth) {
                mDatabase.child("users").child(UserId).child("config").child("stroke_width").setValue(strokeWidth);
            }

            @Override
            public void onUndo() {
                mDatabase.child("users").child(UserId).child("action").setValue(UserPath.ACTION_UNDO);
                mDatabase.child("users").child(UserId).child("action").setValue("");
            }
        });

        addUserEvents(sender_id);

        mDatabase.child("users").child(UserId).child("config").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("color").getValue() != null && !dataSnapshot.child("color").getValue().equals("")) {
                    int color = Integer.valueOf(dataSnapshot.child("color").getValue().toString());
                    selfUserPath.setPenColor(color);
                    changeButtonColor(color);
                }
                if (dataSnapshot.child("circle_size").getValue() != null && !dataSnapshot.child("circle_size").getValue().equals("")) {
                    selfUserPath.setCircleSize((float) dataSnapshot.child("circle_size").getValue());
                }
                if (dataSnapshot.child("stroke_width").getValue() != null && !dataSnapshot.child("stroke_width").getValue().equals("")) {
                    selfUserPath.setStrokeWidth((float) dataSnapshot.child("stroke_width").getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("users").child(UserId).child("connected_uid").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object object = dataSnapshot.getValue();
                if (object != null) {
                    Log.d("TAG", "onDataChange: " + object.toString());
                } else {
                    Log.d("TAG", "onDataChange: object is null");
                }
                if (object != null && !object.toString().equals(UserId)) {
                    addUserEvents(object.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, selfUserPath.getPenColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                selfUserPath.setPenColor(color);
                changeButtonColor(color);
            }
        });
        colorPicker.show();
    }

    public void changeButtonColor(int color) {

        ShapeDrawable footerBackground = new ShapeDrawable();
        footerBackground.setShape(new OvalShape());
        footerBackground.getPaint().setColor(color);
        double y = (299 * Color.red(color) + 587 * Color.green(color) + 114 * Color.blue(color)) / 1000;
        if (y >= 128) {
            colorPickerBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_colorize_black_24dp));
        } else {
            colorPickerBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_colorize_white_24dp));
        }
        colorPickerBtn.setBackgroundDrawable(footerBackground);
    }

    private void addUserEvents(final String userId) {
        final DatabaseReference mDatabase = FirebaseHelper.getInstance().getDatabase();
        final UserPath userPath = new UserPath(userId, paintView);
        paintView.addLayer(userPath);

        Map<String, Object> paintViewSizes = new HashMap<>();
        paintViewSizes.put("width", paintView.getWidth());
        paintViewSizes.put("height", paintView.getHeight());

        final Map<String, Object> paintViewSize = new HashMap<>();
        paintViewSize.put("paintViewSize", paintViewSizes);

        mDatabase.child("users").child(UserId).updateChildren(paintViewSize);


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
                        case UserPath.ACTION_UNDO:
                            userPath.undo();
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && (sender_width == 0 && sender_height == 0)) {
                    if (dataSnapshot.hasChild(sender_id) && dataSnapshot.child(sender_id).hasChild("paintViewSize")) {
                        sender_height = Integer.valueOf(dataSnapshot.child(sender_id).child("paintViewSize").child("height").getValue().toString());
                        sender_width = Integer.valueOf(dataSnapshot.child(sender_id).child("paintViewSize").child("width").getValue().toString());
                        Log.d("Responsive tst first tm", String.valueOf(sender_width) + " " + String.valueOf(sender_height));
                    }
                } else {
                    // Log.d("Responsive tst already", String.valueOf(sender_width) + " " + String.valueOf(sender_height));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // {Of2N2UzSNrTuqrSlHmxojZ3xpgF2={phone=+996703328363, name=+996703328363, device_token=cvwbr-idSFE:APA91bH-7XE7kioH4MdtoClGXOgzfN4t0MCkBMBhwNysTnsaL6d2yHXl7rAAur409BoriLhNE32IMV-YLrbIVLyQlzXs0N0_oD0FAzeMSlz7qxXRLOm8z9Tslpe5aqXGw1bkbGtVrTBZ, action=, data=}, connected_uid=Rqk1NMhp2cUs5FftNkeibisMZsI3, data=, DLA0lWIz0vRY3HTqVcbHY24rBYR2={userState={time=07:37 PM, date=17:02:2019, state=offline}, connected_uid=Rqk1NMhp2cUs5FftNkeibisMZsI3, action=, data=, phone=+996559969960, name=+996559969960, config={color=-16711917}, device_token=el7pNgt8ya0:APA91bHFerUPzRLieer-7FBorgxqtgtMsVJP4j-fzHrowshwJ5fidEx4NtipH7kWya0sTCaWIgrmmgjaWln6ql_za7LZj2r0lYsxsXAub5V5k9BONebtIcSBZIoS-jXmLIrqK5FzzHNH}, -LVUFaAnUSSyeuSoYmS1={action=, data=}, Rqk1NMhp2cUs5FftNkeibisMZsI3={userState={time=10:36 PM, date=17:02:2019, state=online}, connected_uid=r3Ne2rSAFZPAa2DCOvTQfmvM9ND2, action=, data=, phone=+996709770095, name=+996709770095, config={color=-1043179}, resolution={width=1080, height=1920}, device_token=f5RG6SjPqRQ:APA91bFI-ojBaOeAH8FplRIJoe8j_Xu7NhFquG0IpwtsI4yDGbP8nwehT3CwJ0AcD-Azd16u2tC6IV5Az50-k-uI4C36cRgoOJHuYGwFAMrW1qI4020Zp-j58hmmYM5PWDl2ONlxn0X1}, 9kNXJOLIdlPDb0AOfkjQKy8rlBq1={userState={time=07:06 PM, date=17:02:2019, state=offline}, connected_uid=r3Ne2rSAFZPAa2DCOvTQfmvM9ND2, action=, data=, phone=+996701766680, name=+996701766680, config={color=-1179648}, resolution={width=1080, height=2030}, device_token=cyG96cW99b8:APA91bFpKQvy6TY2xhH4HLfL2kmtOfgMLfYROagh3O4NQFmbeWKy-X-KOMYAdbZvzcEiyW33j8J4aXK1gYwH42cIZGSTNXc1o71KmkBnV5DoB-DB_jgkIAjtyW7I4qc81x5NBX8MZdnF}, r3Ne2rSAFZPAa2DCOvTQfmvM9ND2={userState={time=10:36 PM, date=17:02:2019, state=offline}, connected_uid=Rqk1NMhp2cUs5FftNkeibisMZsI3, action=, data=rO0ABXNyADBjb20uZXhhbXBsZS5hcHBsZS5nZWVrdGVjaC5wYWludC5QYWludFZpZXckRnJhbWUvhu35yiBnVgIABUkABHR5cGVGAAJ4MUYAAngyRgACeTFGAAJ5MnhwAAAAA0SEIAAAAAAARKqgAAAAAAA, phone=+996700472663, name=+996700472663, config={color=-3328}, resolution={width=1080, height=2030}, device_token=cC8HrK0sMi0:APA91bHgZGSfFIhBQ83ruUs5YxOBzPoCs5JfPD-F3-xWes6jj7Rn_FkFF32THwThBlSE9_bJnbpezRdZDtW58vISF562EmxNZpDDhoNgmTvu9VtnYoEKtk9rUi6srCT_0GWwLaLliuAb}}

        mDatabase.child("users").child(userId).child("data").setValue("");
        mDatabase.child("users").child(userId).child("connected_uid").setValue(this.UserId);
        mDatabase.child("users").child(userId).child("data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object object = dataSnapshot.getValue();
                if (object != null) {
                    PaintView.Frame frame = (PaintView.Frame) SerializationUtil.stringToObject(object.toString());
                    if (frame != null) {
                        //   Log.d("Responsive res", String.valueOf(paintView.getWidth()) + " " + String.valueOf(paintView.getHeight()));

                           /* Display display = getWindowManager().getDefaultDisplay();
                            Point size = new Point();
                            display.getSize(size);
                            int width = size.x;
                            int height = size.y;*/

                            int width = paintView.getWidth();
                            int height = paintView.getHeight();


                            //  Log.d("Responsive res", String.valueOf(width) + " " + String.valueOf(height));

                            if (sender_height > 0) {
                                Log.d("Responsive before", frame.toString());

                                float h_percent = frame.y1 * 100 / sender_height;
                                frame.y1 = (float) height / 100 * h_percent;

                                h_percent = frame.y2 * 100 / sender_height;
                                frame.y2 = (float) height / 100 * h_percent;

                                Log.d("Responsive after", frame.toString());

                            }
                            if (sender_width > 0) {
                                float w_percent = frame.x1 * 100 / sender_width;
                                frame.x1 = (float) width / 100 * w_percent;

                                w_percent = frame.x2 * 100 / sender_width;
                                frame.x2 = (float) width / 100 * w_percent;
                            }

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
                if (dataSnapshot.child("color").getValue() != null && !dataSnapshot.child("color").getValue().equals("")) {
                    userPath.setPenColor(Integer.valueOf(dataSnapshot.child("color").getValue().toString()));
                }
                if (dataSnapshot.child("circle_size").getValue() != null && !dataSnapshot.child("circle_size").getValue().equals("")) {
                    userPath.setCircleSize((float) dataSnapshot.child("circle_size").getValue());
                }
                if (dataSnapshot.child("stroke_width").getValue() != null && !dataSnapshot.child("stroke_width").getValue().equals("")) {
                    userPath.setStrokeWidth((float) dataSnapshot.child("stroke_width").getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gridBtn:
                if (pressed) {
                    pressed = false;
                    gridBtn.setImageResource(R.drawable.ic_grid_off_black_24dp);
                    paintView.addLayer(new GridLayer("grid", paintView));

                } else {
                    pressed = true;
                    gridBtn.setImageResource(R.drawable.ic_grid_on_black_24dp);
                    paintView.removeLayer(paintView.getLayer("grid"));
                }
                break;
            case R.id.contactBtn:
                startActivity(new Intent(MainActivity.this, FriendsActivity.class));
                break;
            case R.id.signOut:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            case R.id.clear_canvas:
                selfUserPath.clearCanvas();
                selfUserPath.clearFrames();
                paintView.clearAllUserCanvas();
                break;
            case R.id.redrawBtn:
                selfUserPath.redrawFrames();
                break;
            case R.id.undo_button:
                selfUserPath.undo();
                break;
            case R.id.color_picker:
                openColorPicker();
                break;
            case R.id.historyBtn:
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                break;
        }
    }
}
