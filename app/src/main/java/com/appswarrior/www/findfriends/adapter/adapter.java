package com.appswarrior.www.findfriends.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appswarrior.www.findfriends.R;
import com.appswarrior.www.findfriends.home.MainActivity;
import com.appswarrior.www.findfriends.model.api.Friend;
import com.appswarrior.www.findfriends.model.data.FriendModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 20/09/2017.
 */

public class adapter extends RecyclerView.Adapter<adapter.FriendViewHolder> {
    Context context;
    List<Friend> friends = new ArrayList<>();
    List<FriendModel> savedFriends = new ArrayList<>();

    public adapter() {
    }


    public adapter( List<FriendModel> savedFriends,Context context) {
        this.savedFriends = savedFriends;
        this.context = context;
    }
    public adapter(Context context, List<Friend> friends) {
        this.friends = friends;
        this.context = context;
    }

    @Override
    public adapter.FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myfrinds_row, parent, false);
        FriendViewHolder vh = new FriendViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final adapter.FriendViewHolder holder, int position) {
        if (savedFriends!=null&savedFriends.size()>0){

            if (position == 1) {
                holder.friend_tv.setText(savedFriends.get(position).getFirstName());
                holder.friend_tv.setEnabled(true);
                holder.friend_tv.setBackgroundResource(R.drawable.big_shape);
            } else {
                holder.friend_tv.setText(savedFriends.get(position).getFirstName());
                holder.friend_tv.setEnabled(false);
                holder.friend_tv.setBackgroundResource(R.drawable.myfriend_shape);
                holder.params.setMargins(0, 50, 0, 0);
                holder.friend_tv.setLayoutParams(holder.params);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);

                }
            });
        }else {
        if (friends.size() >= 2) {
            if (position == 1) {
                holder.friend_tv.setText(friends.get(position).getFirstName());
                holder.friend_tv.setEnabled(true);
                holder.friend_tv.setBackgroundResource(R.drawable.big_shape);
            } else {
                holder.friend_tv.setText(friends.get(position).getFirstName());
                holder.friend_tv.setEnabled(false);
                holder.friend_tv.setBackgroundResource(R.drawable.myfriend_shape);
                holder.params.setMargins(0, 50, 0, 0);
                holder.friend_tv.setLayoutParams(holder.params);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);

                }
            });
        }}

    }

    @Override
    public int getItemCount() {
        if (friends.size() > 2) {
            return 3;
        } else
            return friends.size();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        public TextView friend_tv;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        public FriendViewHolder(View itemView) {
            super(itemView);
            friend_tv = (TextView) itemView.findViewById(R.id.friend_tv);


        }
    }
}
