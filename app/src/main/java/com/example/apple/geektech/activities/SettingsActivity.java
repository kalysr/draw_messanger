package com.example.apple.geektech.activities;

import android.Manifest;
import android.app.Application;
import android.os.Build;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.apple.geektech.R;
import com.example.apple.geektech.Utils.SharedPreferenceHelper;

public class SettingsActivity extends AppCompatActivity {

    Switch switchScreenOn,switchCacheDrawRecords;
    public final String KEEP_SCREEN = "keep_screen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //getActionBar().setTitle("Settings");
        initComponents();
        setSettingsFromSharedPref();

    }

    private void setSettingsFromSharedPref() {
        boolean checked = SharedPreferenceHelper.getBoolean(SettingsActivity.this,KEEP_SCREEN,false);
        switchScreenOn.setChecked(checked);
    }

    private void initComponents() {
        switchScreenOn = findViewById(R.id.switch_keep_screen_on);
        switchScreenOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchScreenOn.setChecked(isChecked);
//                PowerManager manager = (PowerManager) getSystemService(POWER_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    setTurnScreenOn(isChecked);

                }
                SharedPreferenceHelper.setBoolean(SettingsActivity.this,KEEP_SCREEN,isChecked);
            }
        });
        switchCacheDrawRecords = findViewById(R.id.switch_cache_records);
    }

}
