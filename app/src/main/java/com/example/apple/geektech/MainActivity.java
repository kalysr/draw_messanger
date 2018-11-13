package com.example.apple.geektech;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.apple.geektech.paint.PaintView;

public class MainActivity extends AppCompatActivity {

    PaintView paintView;
    Button clearButton,redrawBtn,clearFramesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initEvents();
    }

    private void init() {
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

        redrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.redrawFrames();
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
