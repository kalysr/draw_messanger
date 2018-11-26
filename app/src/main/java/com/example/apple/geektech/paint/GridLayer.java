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

        for (int i = 120; i <= paintView.getWidth(); i+=120) {
                mPath.moveTo(i,0);
                mPath.lineTo(i,paintView.getHeight());
            for (int j = 120; j <= paintView.getHeight() ; j+=120) {

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
