package com.example.apple.geektech.paint;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.widget.Toast;

import com.example.apple.geektech.MainActivity;

import java.util.ArrayList;

public class GridLayer implements ILayer{
    public final Paint mPaint = new Paint();
    private final Path mPath = new Path();
    private String id;
    private float strokeWidth = 4f;
    public int penColor = Color.GRAY;
    private PaintView paintView;

    public GridLayer() {

    }


    @Override
    public void _clearCanvas() {

    }

    @Override
    public void _clearFrames() {

    }

    @Override
    public void addFrame(PaintView.Frame frame) {

    }

    @Override
    public Path getPath() {
        return mPath;
    }

    @Override
    public Paint getPaint() {
        return mPaint;
    }

    public String getId() {
        return id;
    }

    public GridLayer(String id,PaintView paintView) {
        this.id = id;
        this.paintView = paintView;
        init();
        drawGrid();

    }

    private void drawGrid() {

        for (int i = 150; i <= paintView.getWidth(); i+=150) {
                mPath.moveTo(i,0);
                mPath.lineTo(i,paintView.getHeight());
            for (int j = 150; j <= paintView.getHeight() ; j+=150) {

                mPath.moveTo(0,j);
                mPath.lineTo(paintView.getWidth(),j);
            }
//

        }

    }

    private void init() {
        mPaint.setColor(penColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setAntiAlias(true);
    }
}
