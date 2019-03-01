package com.example.apple.geektech.paint;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.apple.geektech.activities.LoginActivity.TAG;

public class UserPath implements ILayer {
    public static final String ACTION_CLEAR_CANVAS = "ACTION_CLEAR_CANVAS";
    public static final String ACTION_CLEAR_FRAMES = "ACTION_CLEAR_FRAMES";
    public static final String ACTION_UNDO = "ACTION_UNDO";
    private Context context;
    private Line mLine;

    private ArrayList<Line> lines = new ArrayList<>(0);
    private ArrayList<PaintView.Frame> frames = new ArrayList<>(0);
    public static ArrayList<PaintView.Frame> frames4repeat = new ArrayList<>(0);
    private int current_position = 0;

    private String id;
    private Listener listener;
    private float circleSize = 3f;
    private float strokeWidth = 8f;
    private int penColor = Color.BLACK;
    private PaintView paintView;
    int lastPoint = 0;


    public float getCircleSize() {
        return circleSize;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public int getPenColor() {
        return penColor;
    }

    public void setCircleSize(float circleSize) {
        this.circleSize = circleSize;
        paintView.invalidate();
        if (listener != null) {
            listener.onCircleSizeChanged(circleSize);
        }
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        mLine.getPaint().setStrokeWidth(strokeWidth);
        paintView.invalidate();
        if (listener != null) {
            listener.onStrokeWidthChanged(strokeWidth);
        }
    }

    public void setPenColor(int penColor) {
        this.penColor = penColor;
        mLine.getPaint().setColor(penColor);
        paintView.invalidate();
        if (listener != null) {
            listener.onColorChanged(penColor);
        }
    }

    public String getId() {
        return id;
    }

    public UserPath(String id, PaintView paintView) {
        this.id = id;
        this.paintView = paintView;
        init();
    }
    public UserPath() {
        init();
    }

    public UserPath(String id, PaintView paintView, Context context) {
        this.id = id;
        this.paintView = paintView;
        this.context = context;
        init();
    }


    private void init() {
        Path mPath = new Path();
        mLine = new Line(mPath, getClonePaint());
        lines.add(mLine);

    }

    private Paint getClonePaint() {
        Paint mPaint = new Paint();
        mPaint.setColor(penColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setAntiAlias(true);
        return mPaint;
    }

    public ArrayList<PaintView.Frame> getFrames() {
        return frames;
    }
    public ArrayList<PaintView.Frame> getFrames4repeat() { return frames4repeat; }

    public void clearCanvas() {
        if (listener != null) {
            listener.onClearCanvas();
        }
        _clearCanvas();
    }

    public void clearFrames() {
        if (listener != null) {
            listener.onClearFrames();
        }
        _clearFrames();
    }

    public void _clearCanvas() {
        mLine.getPath().reset();
        lines = new ArrayList<>(0);
        lines.add(mLine);
        current_position = 0;
        paintView.invalidate();
    }

    public void _clearFrames() {
        frames = new ArrayList<>(0);
        frames4repeat = new ArrayList<>(0);
        mLine.getPath().reset();
        lines = new ArrayList<>(0);
        lines.add(mLine);
        current_position = 0;
        paintView.invalidate();
    }

    public void redrawFrames(boolean first) {
        Log.e(TAG, "redrawFrames: " + first);
        if (first){
        lastPoint = frames.size();
                 } else
//        try {
            clearCanvas();
                current_position = lastPoint;
                for (int i = lastPoint; i < frames.size() ; i++) {
                    frames4repeat.add(frames.get(i));
                }
                Log.e(TAG, "redrawFrames: " + Arrays.toString(frames4repeat.toArray()) );
//            _redrawFrames();
//        } catch (InterruptedException e) {
//            e.printStackTrace();

//        }
    }
    public void redrawThis(ArrayList<PaintView.Frame> frames) {

        Log.e(TAG, "redrawThis: Frames " + frames );
        _clearFrames();
        try {
            _redrawThis(frames);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void undo() {
        if (lines.size() > 0) {
            lines.remove(lines.size() - 1);
            if (lines.size() > 0) {
                mLine = lines.get(lines.size() - 1);
            } else {
                _clearCanvas();
            }
        }
        mLine.getPath().reset();
        paintView.invalidate();
        if (listener != null) {
            listener.onUndo();
        }
    }

    private void _redrawFrames() throws InterruptedException {

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        Log.e("tag", "run: cur pos " + current_position + " frame size " + frames.size());
                        if (frames.size() > current_position) {
                            switch (frames.get(current_position).type) {
                                case PaintView.Frame.LINE_TO:
                                    mLine.getPath().lineTo(frames.get(current_position).x1, frames.get(current_position).y1);
                                    break;
                                case PaintView.Frame.CIRCLE:
                                    mLine.getPath().addCircle(frames.get(current_position).x1, frames.get(current_position).y1, circleSize, Path.Direction.CW);
                                    break;
                                case PaintView.Frame.MOVE_TO:
                                    mLine.getPath().moveTo(frames.get(current_position).x1, frames.get(current_position).y1);
                                    break;
                                default:
                                    mLine.getPath().quadTo(
                                            frames.get(current_position).x1,
                                            frames.get(current_position).y1,
                                            frames.get(current_position).x2,
                                            frames.get(current_position).y2
                                    );
                            }

                            paintView.invalidate();

                            current_position++;
                            try {
                                _redrawFrames();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, 10);
    }
    private void _redrawThis(final ArrayList<PaintView.Frame> frames) throws InterruptedException {

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        Log.e("tag", "run: cur pos " + current_position + " frame size " + frames.size());
                        if (frames.size() > current_position) {
                            switch (frames.get(current_position).type) {
                                case PaintView.Frame.LINE_TO:
                                    mLine.getPath().lineTo(frames.get(current_position).x1, frames.get(current_position).y1);
                                    break;
                                case PaintView.Frame.CIRCLE:
                                    mLine.getPath().addCircle(frames.get(current_position).x1, frames.get(current_position).y1, circleSize, Path.Direction.CW);
                                    break;
                                case PaintView.Frame.MOVE_TO:
                                    mLine.getPath().moveTo(frames.get(current_position).x1, frames.get(current_position).y1);
                                    break;
                                default:
                                    mLine.getPath().quadTo(
                                            frames.get(current_position).x1,
                                            frames.get(current_position).y1,
                                            frames.get(current_position).x2,
                                            frames.get(current_position).y2
                                    );
                            }

                            paintView.invalidate();

                            current_position++;
                            try {
                                _redrawThis(frames);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, 10);
    }

    public void addFrame(final PaintView.Frame frame) {
        if (listener != null) {
            listener.onAddFrame(frame);
        }
        frames.add(frame);

        if (frame != null) {

            switch (frame.type) {
                case PaintView.Frame.LINE_TO:
                    mLine.getPath().lineTo(frame.x1, frame.y1);
                    mLine = new Line(new Path(), getClonePaint());
                    lines.add(mLine);
                    break;
                case PaintView.Frame.CIRCLE:
                    mLine.getPath().addCircle(frame.x1, frame.y1, circleSize, Path.Direction.CW);
                    break;
                case PaintView.Frame.MOVE_TO:
                    mLine.getPath().moveTo(frame.x1, frame.y1);
                    break;
                default:
                    mLine.getPath().quadTo(
                            frame.x1,
                            frame.y1,
                            frame.x2,
                            frame.y2
                    );
            }
        }
    }

    @Override
    public ArrayList<Line> getLines() {
        return lines;
    }


    public void drawFrame(final PaintView.Frame frame) {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        addFrame(frame);
                        paintView.invalidate();
                    }
                }, 0);
    }

    public UserPath setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    public static interface Listener {
        public void onAddFrame(PaintView.Frame frame);

        public void onClearCanvas();

        public void onClearFrames();

        public void onColorChanged(int color);

        public void onCircleSizeChanged(float circleSize);

        public void onStrokeWidthChanged(float strokeWidth);

        public void onUndo();
    }
}
