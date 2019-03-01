package com.example.apple.geektech.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.apple.geektech.R;
import com.example.apple.geektech.SaveRecordDialog;
import com.example.apple.geektech.Utils.RecordsListAdapter;
import com.example.apple.geektech.models.DrawRecords;
import com.example.apple.geektech.paint.UserPath;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class HistoryActivity extends AppCompatActivity {
    RelativeLayout relativeLayout;
    RecyclerView recyclerRecords;
    ArrayList<DrawRecords> records;
    RecordsListAdapter adapter;
    String saveName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initComponents();
        getIncomingExtras();

//        applyName(saveName);


    }

    private void getIncomingExtras() {
        if (getIntent().hasExtra("name")){
            saveName = getIntent().getStringExtra("name");
            applyName(saveName);
        }

    }

    private void initComponents() {
        recyclerRecords = findViewById(R.id.recyvlerForRecords);
        recyclerRecords.setHasFixedSize(false);
        recyclerRecords.setItemViewCacheSize(20);
        recyclerRecords.setNestedScrollingEnabled(true);

        recyclerRecords.setLayoutManager(new LinearLayoutManager(this));
    }


    public void applyName(String name) {

        Log.e("TAG", "applyName: in"  );
        String saveCurrentTime, saveCurrentDate;
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd:MM:yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        UserPath userPath = new UserPath();

        Log.e("TAG", "applyName: userPath Frames " + userPath.getFrames4repeat() );
//        records.add(new DrawRecords(name,saveCurrentDate +" " +saveCurrentTime,userPath.getFrames  4repeat()));

//        Log.e("TAG", "applyName: " + Arrays.toString(UserPath.frames4repeat.toArray()) );
        Log.e("TAG", "applyName: " + records.size() );
//        Log.e("TAG", "applyName: " + records.get(0).getFrames4repeat() );
        Log.e("TAG", "applyName: " + records.get(0).getFrames4repeat() );
        adapter = new RecordsListAdapter(records,this);
        recyclerRecords.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}
