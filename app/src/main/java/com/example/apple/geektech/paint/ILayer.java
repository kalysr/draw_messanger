package com.example.apple.geektech.paint;

import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;

public interface ILayer {
    public void _clearCanvas();
    public void _clearFrames();
    public void addFrame(PaintView.Frame frame);
    public ArrayList<Line> getLines();
    public String getId();
}
