package com.example.apple.geektech.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.geektech.MainActivity;
import com.example.apple.geektech.R;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    ArrayList<UserObject> userList;
    Context mContext;


    public UserListAdapter(Context context,ArrayList<UserObject> userList) {
        this.userList = userList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        UserListViewHolder rcv = new UserListViewHolder(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserListViewHolder userListViewHolder, final int i) {
        userListViewHolder.mName.setText(userList.get(i).getName());
        userListViewHolder.mPhone.setText(userList.get(i).getPhone());


        userListViewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(mContext,ProfileActivity.class);
                intent.putExtra("name",userList.get(i).name);
                intent.putExtra("receiver_id",userList.get(i).ref_key);

                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder {
        TextView mName, mPhone;
        LinearLayout parentLayout;
        public UserListViewHolder(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.userNameTV);
            mPhone = itemView.findViewById(R.id.userPhoneTV);
            parentLayout = itemView.findViewById(R.id.userItem);
        }
    }

}
