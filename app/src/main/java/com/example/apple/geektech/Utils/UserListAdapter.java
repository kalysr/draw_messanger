package com.example.apple.geektech.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.apple.geektech.activities.ProfileActivity;
import com.example.apple.geektech.R;
import com.google.firebase.database.collection.LLRBNode;

import java.util.ArrayList;
import java.util.Random;

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
        userListViewHolder.mlastSeen.setText(userList.get(i).getStatus());
        userListViewHolder.imageView.setImageDrawable(getDrawable(userList.get(i).getName()));

        userListViewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext,ProfileActivity.class);
                intent.putExtra("name",userList.get(i).getName());
                intent.putExtra("receiver_id",userList.get(i).getToken());
                intent.putExtra("uid",userList.get(i).getUid());

                mContext.startActivity(intent);
            }
        });


    }

    private Drawable getDrawable(String name) {
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .fontSize(100)
                .bold()
                .textColor(Color.WHITE)
                .endConfig()
                .buildRoundRect(name.substring(0,1) , getRandomColor() ,100);

        return drawable;
    }

    private int getRandomColor() {
        Integer[] colors = new Integer[]{
        Color.CYAN, Color.BLUE,Color.GREEN,Color.MAGENTA,Color.LTGRAY
        };
        Random random = new Random();
//        random.nextInt()
        return colors[random.nextInt(4)];
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder {
        TextView mName, mPhone, mlastSeen; ImageView imageView;
        LinearLayout parentLayout;
        public UserListViewHolder(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.userNameTV);
            mPhone = itemView.findViewById(R.id.userPhoneTV);
            mlastSeen = itemView.findViewById(R.id.lastSeen_TV);
            parentLayout = itemView.findViewById(R.id.userItem);
            imageView = itemView.findViewById(R.id.userPhotoIV);
        }
    }



}
