package com.example.apple.geektech.paint;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;

public class GridLayer implements ILayer{
    private final Paint mPaint = new Paint();
    private final Path mPath = new Path();
    private String id;
    private float strokeWidth = 4f;
    private int penColor = Color.GRAY;
    private PaintView paintView;


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
        mPath.moveTo(0,0);
        mPath.lineTo(0,paintView.getHeight());

        mPath.moveTo(50,0);
        mPath.lineTo(50,paintView.getHeight());

        mPath.moveTo(100,0);
        mPath.lineTo(100,paintView.getHeight());
    }

    private void init() {
        mPaint.setColor(penColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setAntiAlias(true);
    }
}
