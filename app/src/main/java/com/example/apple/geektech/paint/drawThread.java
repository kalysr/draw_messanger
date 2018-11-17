package com.example.apple.geektech.paint;

import android.util.Log;

import com.example.apple.geektech.R;


public class drawThread extends Thread {
    public final String TAG = "BUG";
    public PaintView paintView;


    public void run() {
        Log.d(TAG, "Mой поток запущен...");

        try {
      //      paintView.redrawFrames();

            Log.i(TAG, "Второй поток: ");
//                Thread.sleep(33);

        } catch (Exception e) {
            Log.i(TAG, "Второй поток прерван");
        }
    }
}
