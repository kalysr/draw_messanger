package com.example.apple.geektech.paint;

import android.graphics.Paint;
import android.graphics.Path;

public interface ILayer {
    public void _clearCanvas();
    public void _clearFrames();
    public void addFrame(PaintView.Frame frame);
    public Path getPath();
    public Paint getPaint();
    public String getId();
}
