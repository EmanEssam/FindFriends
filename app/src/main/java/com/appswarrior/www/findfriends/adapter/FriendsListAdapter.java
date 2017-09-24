package com.appswarrior.www.findfriends.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appswarrior.www.findfriends.R;
import com.appswarrior.www.findfriends.home.MainActivity;
import com.appswarrior.www.findfriends.model.api.Friend;
import com.appswarrior.www.findfriends.model.data.FriendModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by hp on 20/09/2017.
 */

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendViewHolder> {
    private Context context;
    private int selectedItemPosition = 0;
    private List<Friend> friendList = new ArrayList<>();
    private RealmResults<FriendModel> friendListModels;
    private List<FriendModel> friends = new ArrayList<>();

    private OnListClickListener onListClickListener;

    public FriendsListAdapter() {
    }

    public FriendsListAdapter(Context context, List<Friend> friendList, OnListClickListener onListClickListener) {
        this.context = context;
        this.friendList = friendList;
        this.onListClickListener = onListClickListener;
    }

    public FriendsListAdapter(Context context, RealmResults<FriendModel> friendListModels, OnListClickListener onListClickListener) {
        this.context = context;
        this.friendListModels = friendListModels;
        this.onListClickListener = onListClickListener;
        friends = friendListModels;
    }


//    public FriendsListAdapter(){}

    @Override
    public FriendsListAdapter.FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_row, parent, false);
        FriendViewHolder vh = new FriendViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final FriendsListAdapter.FriendViewHolder holder, final int position) {
        if (friendList != null && friendList.size() > 0) {
            holder.friend_tv.setText(friendList.get(position).getFirstName());
            if (position == (friendList.size() / 2)) {
                holder.friend_tv.setBackgroundResource(R.drawable.friend_item_clicked_shape);
                holder.friend_tv.setTextColor(context.getResources().getColor(R.color.selected_text_color));
            } else {
                holder.friend_tv.setBackgroundResource(R.drawable.friend_item_shape);
                holder.friend_tv.setTextColor(context.getResources().getColor(R.color.text_primary));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onListClickListener.onListClickListener(position);
                    friendList.set((friendList.size() / 2), friendList.get(position));
                    selectedItemPosition = position;
                    notifyDataSetChanged();
                }
            });

        } else {
            holder.friend_tv.setText(friends.get(position).getFirstName());
            if (position == (friends.size() / 2)) {
                holder.friend_tv.setBackgroundResource(R.drawable.friend_item_clicked_shape);
                holder.friend_tv.setTextColor(context.getResources().getColor(R.color.selected_text_color));
            } else {
                holder.friend_tv.setBackgroundResource(R.drawable.friend_item_shape);
                holder.friend_tv.setTextColor(context.getResources().getColor(R.color.text_primary));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onListClickListener.onListClickListener(position);
//                    friends.set((friends.size() / 2), friends.get(position));
//                    friends.get(friends.size()/2)=friends.get(position);
                    selectedItemPosition = position;
                    notifyDataSetChanged();
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        if (friendListModels != null) {
            return friendListModels.size();
        } else {
            return friendList.size();
        }
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        public TextView friend_tv;

        public FriendViewHolder(View itemView) {
            super(itemView);
            friend_tv = (TextView) itemView.findViewById(R.id.friend_tv);
        }
    }

    public interface OnListClickListener {

        void onListClickListener(int position);
    }


}
