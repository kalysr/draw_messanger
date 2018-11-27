package com.example.apple.geektech.paint;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;

public class GridLayer implements ILayer{
    public static final int GRID_SIZE = 200;
    public final Paint mPaint = new Paint();
    private final Path mPath = new Path();
    private ArrayList<Path> paths = new ArrayList<>(0);
    private String id;
    private float strokeWidth = 4f;
    public int penColor = Color.GRAY;
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
    public ArrayList<Path> getPaths() {
        return paths;
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
        paths.add(mPath);
        init();
        drawGrid();

    }

    private void drawGrid() {

        for (int i = GRID_SIZE; i <= paintView.getWidth(); i+= GRID_SIZE) {
                mPath.moveTo(i,0);
                mPath.lineTo(i,paintView.getHeight());
            for (int j = GRID_SIZE; j <= paintView.getHeight() ; j+= GRID_SIZE) {
                mPath.moveTo(0,j);
                mPath.lineTo(paintView.getWidth(),j);
            }
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
