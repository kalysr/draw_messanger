package com.example.apple.geektech;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    UserPath selfUserPath = null;
    public static String USER_ID = "USER_ID";
    boolean pressed = true;


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
        mDatabase.child("users").child(userId).child("data").setValue("");
        mDatabase.child("users").child(userId).child("connected_uid").setValue(this.UserId);
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
