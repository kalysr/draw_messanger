package com.example.apple.geektech.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.apple.geektech.R;
import com.example.apple.geektech.activities.MainActivity;
import com.example.apple.geektech.models.DrawRecords;
import com.example.apple.geektech.paint.PaintView;
import com.example.apple.geektech.paint.UserPath;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.apple.geektech.activities.LoginActivity.TAG;

public class RecordsListAdapter extends RecyclerView.Adapter<RecordsListAdapter.RecordsViewHolder> {

    ArrayList<DrawRecords> records;
    Context context;
    UserPath userPath;

    public RecordsListAdapter(ArrayList<DrawRecords> records, Context context) {
        this.records = records;
        this.context = context;
    }

    @NonNull
    @Override
    public RecordsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_records,viewGroup,false);
        return new RecordsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordsViewHolder recordsViewHolder, final int i) {
        recordsViewHolder.date.setText(records.get(i).getDate());
        recordsViewHolder.name.setText(records.get(i).getName());
        recordsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: "+ records.size() );
                Log.e(TAG, "onClick: "+ records.get(i).getFrames4repeat() );
//                Log.e(TAG, "onClick: "+ records.get(0).getFrames4repeat() );
                Log.e(TAG, "onClick: "+ i );
//                ArrayList<PaintView.Frame> frame = new ArrayList<>(0);
//                frame = records.get(i).getFrames4repeat();
                userPath.redrawThis(records.get(i).getFrames4repeat());

                Intent intent = new Intent(context, MainActivity.class);
//                intent.pu
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (records== null) {
            return 0;
        }
        return records.size();
    }

    public class RecordsViewHolder extends RecyclerView.ViewHolder {
        TextView date,name;
        public RecordsViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.recordDateTV);
            name = itemView.findViewById(R.id.recordNameTV);
        }
    }
}
