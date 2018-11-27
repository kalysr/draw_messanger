package com.example.apple.geektech.paint;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import yuku.ambilwarna.AmbilWarnaDialog;

public class PaintView extends View {

    private float mCurrX = 0f;
    private float mCurrY = 0f;
    private float mStartX = 0f;
    private float mStartY = 0f;
    private ILayer selfLayer;
    private Map<String,ILayer> users = new HashMap<>(0);
    private GridLayer gLayer = new GridLayer();

    //region Constructors

    public PaintView(Context context) {
        super(context);
        init();
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //endregions

    private void init() {
    }

    public void setSelfLayer(UserPath selfUser) {
        this.selfLayer = selfUser;
    }

    public void addLayer(ILayer layer){
        users.put(layer.getId(),layer);
        invalidate();
    }

    public void removeLayer(ILayer layer){

        try {
            users.remove(layer.getId());
        } catch (NullPointerException e){

        }

        invalidate();

    }

    public ILayer getLayer(String key){
        return users.get(key);
    }

    public ILayer getSelfLayer() {
        return selfLayer;
    }


    public void openColorPicker(){
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(getContext(), gLayer.penColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                gLayer.penColor = color;
            }
        });
        colorPicker.show();
        invalidate();


    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(selfLayer.getPath(), selfLayer.getPaint());
        for(Map.Entry<String, ILayer> entry : users.entrySet()) {
            ILayer layer = entry.getValue();
            canvas.drawPath(layer.getPath(), layer.getPaint());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = x;
                mStartY = y;
                actionDown(x, y);
                break;

            case MotionEvent.ACTION_UP:
                actionUp();
                break;

            case MotionEvent.ACTION_MOVE:
                actionMove(x, y);
                break;
        }
        invalidate();
        return true;
    }

    private void actionMove(float x, float y) {
        selfLayer.addFrame(new Frame(
                mCurrX,
                mCurrY,
                (x + mCurrX) / 2,
                (y + mCurrY) / 2
        ));
        mCurrX = x;
        mCurrY = y;
    }

    private void actionUp() {
        selfLayer.addFrame(new Frame(
                mCurrX,
                mCurrY,
                Frame.LINE_TO
        ));
        if (mStartY == mCurrY && mStartX == mCurrX) {
            selfLayer.addFrame(new Frame(
                    mCurrX,
                    mCurrY,
                    Frame.CIRCLE
            ));
        }
    }

    private void actionDown(float x, float y) {
        selfLayer.addFrame(new Frame(x, y, Frame.MOVE_TO));
        mCurrX = x;
        mCurrY = y;
    }

    public void clearAllUserCanvas() {
        for(Map.Entry<String, ILayer> entry : users.entrySet()) {
            ILayer userPath = entry.getValue();
            userPath._clearCanvas();
        }
    }

    public void clearAllUserFrames() {
        for(Map.Entry<String, ILayer> entry : users.entrySet()) {
            ILayer userPath = entry.getValue();
            userPath._clearFrames();
        }
    }

    public static class Frame implements Serializable {
        public float x1, y1, x2, y2;
        int type = 0;

        final static int LINE_TO = 1;
        final static int MOVE_TO = 2;
        final static int CIRCLE = 3;

        public Frame(float x1, float y1, float x2, float y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public Frame(float x1, float y1, int type) {
            this.x1 = x1;
            this.y1 = y1;
            this.type = type;
        }

        @Override
        public String toString() {
            return "x1 : " + x1 + "\ny1 : " + y1 + "\nx2 : " + x2
                    + "\ny2 : " + y2 + "\ntype : " + type;
        }

    }
}
