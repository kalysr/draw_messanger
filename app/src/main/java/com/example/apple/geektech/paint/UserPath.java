package com.example.apple.geektech.paint;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;

public class UserPath implements ILayer{
    public static final String ACTION_CLEAR_CANVAS = "ACTION_CLEAR_CANVAS";
    public static final String ACTION_CLEAR_FRAMES = "ACTION_CLEAR_FRAMES";
    private final Paint mPaint = new Paint();
    private final Path mPath = new Path();
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
        if(listener != null){
            listener.onCircleSizeChanged(circleSize);
        }
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        mPaint.setStrokeWidth(strokeWidth);
        if(listener != null){
            listener.onStrokeWidthChanged(strokeWidth);
        }
    }

    public void setPenColor(int penColor) {
        this.penColor = penColor;
        mPaint.setColor(penColor);
        if(listener != null){
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

    private void init() {
        mPaint.setColor(penColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setAntiAlias(true);
    }

    public Path getPath() {
        return mPath;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public ArrayList<PaintView.Frame> getFrames() {
        return frames;
    }

    public void clearCanvas() {
        mPath.reset();
        current_position = 0;
        if(listener != null){
            listener.onClearCanvas();
        }
        paintView.invalidate();
    }

    public void clearFrames() {
        frames = new ArrayList<>(0);
        mPath.reset();
        current_position = 0;
        if(listener != null){
            listener.onClearFrames();
        }
        paintView.invalidate();
    }

    public void _clearCanvas() {
        mPath.reset();
        current_position = 0;
        paintView.invalidate();
    }

    public void _clearFrames() {
        frames = new ArrayList<>(0);
        mPath.reset();
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

    public void addFrame(final PaintView.Frame frame){
        if(listener != null){
            listener.onAddFrame(frame);
        }
        frames.add(frame);
        if (frame != null) {
            switch (frame.type) {
                case PaintView.Frame.LINE_TO:
                    mPath.lineTo(frame.x1, frame.y1);
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

    public void drawFrame(final PaintView.Frame frame) {
        frames.add(frame);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (frame != null) {
                            switch (frame.type) {
                                case PaintView.Frame.LINE_TO:
                                    mPath.lineTo(frame.x1, frame.y1);
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
                            paintView.invalidate();
                        }
                    }
                },0);
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
    }
}
