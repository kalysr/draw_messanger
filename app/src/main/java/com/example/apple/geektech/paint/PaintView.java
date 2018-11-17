package com.example.apple.geektech.paint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class PaintView extends View {

    private final Paint mPaint = new Paint();
    private final Path mPath = new Path();
    private ArrayList<Frame> frames = new ArrayList<>(0);
    public int current_position = 0;

    private float mCurrX = 0f;
    private float mCurrY = 0f;
    private float mStartX = 0f;
    private float mStartY = 0f;

    int Starttime = 0;
    int EndTime = 0;


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
        mPaint.setStrokeWidth(16f);
        mPaint.setAntiAlias(true);
    }

    public void clearCanvas() {
        mPath.reset();
        current_position = 0;
        invalidate();
    }

    public void clearFrames() {
        frames = new ArrayList<>(0);
        clearCanvas();
    }

    public void redrawFrames() throws InterruptedException {
        //  clearCanvas();

        // if (frames.size() > 0) {
        //     for (int i = 0; i < frames.size(); i++) {
        //         current_position = i;
        //         Log.d("BUG", "redrawFrames: " + frames.get(i).time);
        //         Thread.sleep(33);

//                for (int t = Starttime; t <= EndTime; t++) {
//                if (frames.get(i).time == t)


        /*

         */


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        if (frames.size() > current_position) {
                            switch (frames.get(current_position).type) {
                                case Frame.LINE_TO:
                                    mPath.lineTo(frames.get(current_position).x1, frames.get(current_position).y1);
                                    break;
                                case Frame.CIRCLE:
                                    mPath.addCircle(frames.get(current_position).x1, frames.get(current_position).y1, 5f, Path.Direction.CW);
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

                            /*

                            current_position++;
                            try {
                                redrawFrames();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        } else {
                            current_position = 0;
                        }


               */

//                        Thread.sleep(1);
//                }
        //               invalidate();
        //           }
        //      }
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

        frames.add(new Frame(
                mCurrX,
                mCurrY,
                (x + mCurrX) / 2,
                (y + mCurrY) / 2, time()
        ));

        mCurrX = x;
        mCurrY = y;

    }

    private void actionUp() {
        EndTime = Integer.parseInt(String.valueOf(System.currentTimeMillis()).substring(6));
        mPath.lineTo(mCurrX, mCurrY);
        frames.add(new Frame(
                mCurrX,
                mCurrY,
                Frame.LINE_TO, EndTime
        ));
        if (mStartY == mCurrY && mStartX == mCurrX) {
            mPath.addCircle(mCurrX, mCurrY, 5f, Path.Direction.CW);
            frames.add(new Frame(
                    mCurrX,
                    mCurrY,
                    Frame.CIRCLE, EndTime
            ));
        }
    }

    private void actionDown(float x, float y) {
//        Starttime = System.currentTimeMillis();
        Starttime = Integer.parseInt(String.valueOf(System.currentTimeMillis()).substring(6));

        mPath.moveTo(x, y);
        frames.add(new Frame(x, y, Frame.MOVE_TO, Starttime));
        mCurrX = x;
        mCurrY = y;
    }

    public int time() {
        int time = (int) (((System.currentTimeMillis() / 100) / 60) / 60);
        return time;
    }

    public class Frame {
        float x1, y1, x2, y2;
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

    }

}
