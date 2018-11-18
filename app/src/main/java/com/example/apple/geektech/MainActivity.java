package com.example.apple.geektech;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.apple.geektech.paint.PaintView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    PaintView paintView;
    Button clearButton, redrawBtn, clearFramesBtn;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initEvents();
    }

    private void init() {

        mDatabase = FirebaseDatabase.getInstance().getReference("frames");
        paintView = findViewById(R.id.main_paint_view);
        clearButton = findViewById(R.id.clear_canvas);
        redrawBtn = findViewById(R.id.redraw);
        clearFramesBtn = findViewById(R.id.clear_frames);
    }

    private void initEvents() {
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.clearCanvas();
            }
        });

        paintView.setListener(new PaintView.Listener() {
            @Override
            public void onDraw(PaintView.Frame frame) {
                String data = SerializationUtil.objectToString(frame);
                mDatabase.child("tmp_frame").child("data").setValue(data);
            }

            @Override
            public void onClearCanvas() {
                mDatabase.child("tmp_frame").child("action").setValue(PaintView.ACTION_CLEAR_CANVAS);
                mDatabase.child("tmp_frame").child("action").setValue("");
            }

            @Override
            public void onClearFrames() {
                mDatabase.child("tmp_frame").child("action").setValue(PaintView.ACTION_CLEAR_FRAMES);
                mDatabase.child("tmp_frame").child("action").setValue("");
            }
        });

        mDatabase.child("tmp_frame").child("data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object object =  dataSnapshot.getValue();
                if(object != null){
                    PaintView.Frame frame = (PaintView.Frame) SerializationUtil.stringToObject(object.toString());
                    if(frame != null) {
                        //Log.d("KS", frame.toString());
                        paintView.addFrame(frame);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("tmp_frame").child("action").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object object =  dataSnapshot.getValue();
                if(object != null){
                    String action = object.toString();
                    Log.d("KS", "action:"+action);
                    switch (action){
                        case PaintView.ACTION_CLEAR_CANVAS:
                            paintView._clearCanvas();
                            break;
                        case PaintView.ACTION_CLEAR_FRAMES:
                            paintView._clearFrames();
                            break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        redrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    paintView.clearCanvas();
                    paintView.redrawFrames();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        clearFramesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.clearFrames();
            }
        });

        clearFramesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.clearFrames();
            }
        });
    }
}
