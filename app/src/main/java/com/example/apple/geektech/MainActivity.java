package com.example.apple.geektech;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.apple.geektech.paint.PaintView;

public class MainActivity extends AppCompatActivity {

    PaintView paintView;
    Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initEvents();
    }

    private void init(){
        paintView = (PaintView) findViewById(R.id.main_paint_view);
        clearButton = (Button)  findViewById(R.id.clear_canvas);
    }

    private void initEvents() {
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.clearCanvas();
            }
        });
    }
}
