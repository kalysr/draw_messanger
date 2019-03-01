package com.example.apple.geektech.models;

import com.example.apple.geektech.paint.PaintView;

import java.util.ArrayList;

public class DrawRecords {
    String name;
    String date;
    private ArrayList<PaintView.Frame> frames4repeat;

    public DrawRecords(String name, String date, ArrayList<PaintView.Frame> frames) {
        this.name = name;
        this.date = date;
        this.frames4repeat = frames;
    }
    public DrawRecords(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public ArrayList<PaintView.Frame> getFrames4repeat() {
        return frames4repeat;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
}
