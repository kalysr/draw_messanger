package com.example.apple.geektech;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.apple.geektech.paint.PaintView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    PaintView paintView;
    Button clearButton,redrawBtn,clearFramesBtn;
    FirebaseDatabase database;
    DatabaseReference framesCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initEvents();
    }

    private void init() {
        database = FirebaseDatabase.getInstance();
        framesCollection = database.getReference("frames");

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
                framesCollection.child("tmp").setValue(frame);
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
                //paintView.clearFrames();

                PaintView.Frame dataValueObject = new PaintView.Frame(5f,5f,5f,5f,0);

                try {
                    String data = SerializationUtil.objectToString(dataValueObject);
                    PaintView.Frame object = (PaintView.Frame) SerializationUtil.stringToObject(data);
                    System.out.println(object.toString());
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }
}
