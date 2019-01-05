package com.example.apple.geektech;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.apple.geektech.paint.GridLayer;
import com.example.apple.geektech.paint.PaintView;
import com.example.apple.geektech.paint.UserPath;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import yuku.ambilwarna.AmbilWarnaDialog;


public class MainActivity extends AppCompatActivity {

    PaintView paintView;
    ImageButton clearButton, redrawBtn, undoButton, colorPickerBtn
            ,gridBtn, contactBtn,historyBtn,onlineContactsBtn, signOut;
    String UserId = null;
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
        undoButton = findViewById(R.id.undo_button);
        colorPickerBtn = findViewById(R.id.color_picker);
        gridBtn = findViewById(R.id.gridBtn);
        contactBtn = findViewById(R.id.contactBtn);
        onlineContactsBtn = findViewById(R.id.onlineContactsBtn);
        historyBtn = findViewById(R.id.historyBtn);
        signOut = findViewById(R.id.signOut);

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


        gridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pressed) {
                    pressed = false;
                    gridBtn.setImageResource(R.drawable.ic_grid_off_black_24dp);
                    paintView.addLayer(new GridLayer("grid", paintView));

                } else {
                    pressed = true;
                    gridBtn.setImageResource(R.drawable.ic_grid_on_black_24dp);
                    paintView.removeLayer(paintView.getLayer("grid"));

                }
            }
        });


        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FriendsActivity.class));
            }
        });


        onlineContactsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,OnlineContactsActivity.class));
            }
        });


        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selfUserPath.clearCanvas();
                selfUserPath.clearFrames();
                paintView.clearAllUserCanvas();
            }
        });

        redrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selfUserPath.redrawFrames();
            }
        });

        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selfUserPath.undo();
            }
        });

        colorPickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,HistoryActivity.class));
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
        }else {
            colorPickerBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_colorize_white_24dp));
        }
        colorPickerBtn.setBackgroundDrawable(footerBackground);
    }

    private void addUserEvents(final String userId, DataSnapshot dataSnapshot) {
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
}
