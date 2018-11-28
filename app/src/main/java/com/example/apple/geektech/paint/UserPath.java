package com.example.apple.geektech.paint;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;

public class UserPath implements ILayer {
    public static final String ACTION_CLEAR_CANVAS = "ACTION_CLEAR_CANVAS";
    public static final String ACTION_CLEAR_FRAMES = "ACTION_CLEAR_FRAMES";
    public static final String ACTION_UNDO = "ACTION_UNDO";
    private final Paint mPaint = new Paint();
    private Path mPath = new Path();
    private ArrayList<Path> paths = new ArrayList<>(0);
    private ArrayList<PaintView.Frame> frames = new ArrayList<>(0);
    private int current_position = 0;

    private String id;
    private Listener listener;
    private float circleSize = 3f;
    private float strokeWidth = 8f;
    private int penColor = Color.BLACK;
    private PaintView paintView;


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
        mPaint.setStrokeWidth(strokeWidth);
        paintView.invalidate();
        if (listener != null) {
            listener.onStrokeWidthChanged(strokeWidth);
        }
    }

    public void setPenColor(int penColor) {
        this.penColor = penColor;
        mPaint.setColor(penColor);
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


    public void unDo(){

    }



    private void init() {
        mPaint.setColor(penColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setAntiAlias(true);
        paths.add(mPath);
    }

    public Paint getPaint() {
        return mPaint;
    }

    public ArrayList<PaintView.Frame> getFrames() {
        return frames;
    }

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
        mPath.reset();
        paths = new ArrayList<>(0);
        paths.add(mPath);
        current_position = 0;
        paintView.invalidate();
    }

    public void _clearFrames() {
        frames = new ArrayList<>(0);
        mPath.reset();
        paths = new ArrayList<>(0);
        paths.add(mPath);
        current_position = 0;
        paintView.invalidate();
    }

    public void redrawFrames() {
        current_position = 0;
        try {
            clearCanvas();
            _redrawFrames();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void undo() {
        if (paths.size() > 0) {
            paths.remove(paths.size() - 1);
            if (paths.size() > 0) {
                mPath = paths.get(paths.size() - 1);
            } else {
                _clearCanvas();
            }
        }
        mPath.reset();
        paintView.invalidate();
        if (listener != null) {
            listener.onUndo();
        }
    }

    private void _redrawFrames() throws InterruptedException {

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (frames.size() > current_position) {
                            switch (frames.get(current_position).type) {
                                case PaintView.Frame.LINE_TO:
                                    mPath.lineTo(frames.get(current_position).x1, frames.get(current_position).y1);
                                    break;
                                case PaintView.Frame.CIRCLE:
                                    mPath.addCircle(frames.get(current_position).x1, frames.get(current_position).y1, circleSize, Path.Direction.CW);
                                    break;
                                case PaintView.Frame.MOVE_TO:
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

    public void addFrame(final PaintView.Frame frame) {
        if (listener != null) {
            listener.onAddFrame(frame);
        }
        frames.add(frame);
        if (frame != null) {
            switch (frame.type) {
                case PaintView.Frame.LINE_TO:
                    mPath.lineTo(frame.x1, frame.y1);
                    mPath = new Path();
                    paths.add(mPath);
                    break;
                case PaintView.Frame.CIRCLE:
                    mPath.addCircle(frame.x1, frame.y1, circleSize, Path.Direction.CW);
                    break;
                case PaintView.Frame.MOVE_TO:
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
        }
    }

    @Override
    public ArrayList<Path> getPaths() {
        return paths;
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
