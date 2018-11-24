package com.example.apple.geektech.paint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaintView extends View {

    public static final String ACTION_CLEAR_CANVAS = "ACTION_CLEAR_CANVAS";
    public static final String ACTION_CLEAR_FRAMES = "ACTION_CLEAR_FRAMES";
    private float mCurrX = 0f;
    private float mCurrY = 0f;
    private float mStartX = 0f;
    private float mStartY = 0f;
    private UserPath selfUser;
    private Map<String,UserPath> users = new HashMap<>(0);

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

    public void setSelfUser(UserPath selfUser) {
        this.selfUser = selfUser;
    }

    public void addUser(UserPath userPath){
        users.put(userPath.getId(),userPath);
    }

    public UserPath getSelfUser() {
        return selfUser;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(selfUser.getPath(), selfUser.getPaint());
        for(Map.Entry<String, UserPath> entry : users.entrySet()) {
            //String id = entry.getKey();
            UserPath userPath = entry.getValue();
            canvas.drawPath(userPath.getPath(), userPath.getPaint());
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
        selfUser.addFrame(new Frame(
                mCurrX,
                mCurrY,
                (x + mCurrX) / 2,
                (y + mCurrY) / 2
        ));
        mCurrX = x;
        mCurrY = y;
    }

    private void actionUp() {
        selfUser.addFrame(new Frame(
                mCurrX,
                mCurrY,
                Frame.LINE_TO
        ));
        if (mStartY == mCurrY && mStartX == mCurrX) {
            selfUser.addFrame(new Frame(
                    mCurrX,
                    mCurrY,
                    Frame.CIRCLE
            ));
        }
    }

    private void actionDown(float x, float y) {
        selfUser.addFrame(new Frame(x, y, Frame.MOVE_TO));
        mCurrX = x;
        mCurrY = y;
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
            String value = "x1 : " + x1 + "\ny1 : " + y1 + "\nx2 : " + x2
                    + "\ny2 : " + y2 + "\ntype : " + type;
            return value;
        }

    }
}
