package com.example.apple.geektech.paint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;

public class PaintView extends View {

    public static final String ACTION_CLEAR_CANVAS = "ACTION_CLEAR_CANVAS";
    public static final String ACTION_CLEAR_FRAMES = "ACTION_CLEAR_FRAMES";
    private final Paint mPaint = new Paint();
    private final Path mPath = new Path();
    private ArrayList<Frame> frames = new ArrayList<>(0);
    public int current_position = 0;

    private float mCurrX = 0f;
    private float mCurrY = 0f;
    private float mStartX = 0f;
    private float mStartY = 0f;
    private Listener listener;
    int Starttime = 0;
    int EndTime = 0;
    private static final float CIRCLE_SIZE = 3f;


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
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(8f);
        mPaint.setAntiAlias(true);
    }

    public void _clearCanvas() {
        mPath.reset();
        current_position = 0;
        invalidate();
    }

    public void clearCanvas() {
        _clearCanvas();
        invalidate();
        if(listener != null){
            listener.onClearCanvas();
        }
    }
    public void _clearFrames() {
        frames = new ArrayList<>(0);
        _clearCanvas();
    }

    public void clearFrames() {
        _clearFrames();
        _clearCanvas();
        if(listener != null){
            listener.onClearFrames();
        }
    }

    public void redrawFrames() throws InterruptedException {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (frames.size() > current_position) {
                            switch (frames.get(current_position).type) {
                                case Frame.LINE_TO:
                                    mPath.lineTo(frames.get(current_position).x1, frames.get(current_position).y1);
                                    break;
                                case Frame.CIRCLE:
                                    mPath.addCircle(frames.get(current_position).x1, frames.get(current_position).y1, CIRCLE_SIZE, Path.Direction.CW);
                                    break;
                                case Frame.MOVE_TO:
                                    mPath.moveTo(frames.get(current_position).x1, frames.get(current_position).y1);
                                    break;
                                default:
                                    mPath.quadTo(
                                            frames.get(current_position).x1,
                                            frames.get(current_position).y1,
                                            frames.get(current_position).x2,
                                            frames.get(current_position).y2
                                    );
                            }

                            invalidate();

                            current_position++;
                            try {
                                redrawFrames();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, 10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);
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
        mPath.quadTo(
                mCurrX,
                mCurrY,
                (x + mCurrX) / 2,
                (y + mCurrY) / 2
        );

        Frame frame = new Frame(
                mCurrX,
                mCurrY,
                (x + mCurrX) / 2,
                (y + mCurrY) / 2, time()
        );
        frames.add(frame);

        if (listener != null) {
            listener.onDraw(frame);
        }

        mCurrX = x;
        mCurrY = y;

    }

    private void actionUp() {
        EndTime = Integer.parseInt(String.valueOf(System.currentTimeMillis()).substring(6));
        mPath.lineTo(mCurrX, mCurrY);
        Frame frame = new Frame(
                mCurrX,
                mCurrY,
                Frame.LINE_TO, EndTime
        );
        frames.add(frame);
        if (listener != null) {
            listener.onDraw(frame);
        }
        if (mStartY == mCurrY && mStartX == mCurrX) {
            mPath.addCircle(mCurrX, mCurrY, CIRCLE_SIZE, Path.Direction.CW);
            Frame frame1 = new Frame(
                    mCurrX,
                    mCurrY,
                    Frame.CIRCLE, EndTime
            );
            frames.add(frame1);
            if (listener != null) {
                listener.onDraw(frame1);
            }
        }
    }

    private void actionDown(float x, float y) {
//        Starttime = System.currentTimeMillis();
        Starttime = Integer.parseInt(String.valueOf(System.currentTimeMillis()).substring(6));

        mPath.moveTo(x, y);
        Frame frame = new Frame(x, y, Frame.MOVE_TO, Starttime);
        frames.add(frame);
        if (listener != null) {
            listener.onDraw(frame);
        }

        mCurrX = x;
        mCurrY = y;
    }

    public int time() {
        int time = (int) (((System.currentTimeMillis() / 100) / 60) / 60);
        return time;
    }

    public static class Frame implements Serializable {
        public float x1, y1, x2, y2;
        int time = 0;
        int type = 0;

        final static int LINE_TO = 1;
        final static int MOVE_TO = 2;
        final static int CIRCLE = 3;

        public Frame(float x1, float y1, float x2, float y2, int time) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.time = time;
        }

        public Frame(float x1, float y1, int type, int time) {
            this.x1 = x1;
            this.y1 = y1;
            this.type = type;
            this.time = time;
        }

        @Override
        public String toString() {
            String value = "x1 : " + x1 + "\ny1 : " + y1 + "\nx2 : " + x2
                    + "\ny2 : " + y2 + "\ntype : " + type;
            return value;
        }

    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void addFrame(final Frame frame) {
        frames.add(frame);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (frame != null) {
                            switch (frame.type) {
                                case Frame.LINE_TO:
                                    mPath.lineTo(frame.x1, frame.y1);
                                    break;
                                case Frame.CIRCLE:
                                    mPath.addCircle(frame.x1, frame.y1, CIRCLE_SIZE, Path.Direction.CW);
                                    break;
                                case Frame.MOVE_TO:
                                    mPath.moveTo(frame.x1, frame.y1);
                                    break;
                                default:
                                    mPath.quadTo(
                                            frame.x1,
                                            frame.y1,
                                            frame.x2,
                                            frame.y2
                                    );
                            }
                            invalidate();
                        }
                    }
                },0);
    }

    public static interface Listener {
        public void onDraw(Frame frame);
        public void onClearCanvas();
        public void onClearFrames();
    }
}
