package com.example.apple.geektech.paint;

import android.graphics.Paint;
import android.graphics.Path;

public class Line implements ILine {
    private Path path;
    private Paint paint;

    public Line(Path path, Paint paint) {
        this.path = path;
        this.paint = paint;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public Paint getPaint() {
        return paint;
    }
}
