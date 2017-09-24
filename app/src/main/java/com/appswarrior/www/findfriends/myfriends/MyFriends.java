package com.appswarrior.www.findfriends.myfriends;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appswarrior.www.findfriends.R;
import com.appswarrior.www.findfriends.adapter.adapter;
import com.appswarrior.www.findfriends.home.HomePresenterImpl;
import com.appswarrior.www.findfriends.home.HomeView;
import com.appswarrior.www.findfriends.home.MainActivity;
import com.appswarrior.www.findfriends.model.api.Friend;
import com.appswarrior.www.findfriends.model.data.FriendModel;
import com.appswarrior.www.findfriends.utils.ConnectivityReceiver;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MyFriends extends AppCompatActivity implements HomeView, ConnectivityReceiver.ConnectivityReceiverListener {
    private RecyclerView mFriendsList;
    private ProgressBar mMyFriendsLoader;
    private adapter mFriendsAdapter;
    HomePresenterImpl homePresenter = new HomePresenterImpl();
    String message = "";
    int color;
    private Snackbar mSnackbar;
    Realm realm = Realm.getDefaultInstance();
    RealmResults<FriendModel> friends;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        initViews();
        homePresenter.setView(this);
        checkConnection();


    }

    void checkConnection() {
        if (ConnectivityReceiver.isConnected()) {
            homePresenter.setFriendsList();
            message = getString(R.string.connect_to_internet);
            color = Color.WHITE;
        } else {
            friends = realm.where(FriendModel.class)
                    .findAll();
            if (friends != null) {
                mFriendsAdapter = new adapter(friends, this);
                mFriendsList.setAdapter(mFriendsAdapter);
            }
            mMyFriendsLoader.setVisibility(View.GONE);
            message = getString(R.string.not_connected);
            color = Color.RED;
        }
        mSnackbar = Snackbar
                .make(findViewById(R.id.activity_my_friends), message, Snackbar.LENGTH_LONG);
        View sbView = mSnackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        mSnackbar.show();
    }

    private void initViews() {
        mFriendsList = (RecyclerView) findViewById(R.id.myFriends);
        mMyFriendsLoader = (ProgressBar) findViewById(R.id.myfriends_loader);
        mFriendsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                homePresenter.setFriendsList();
                break;
            case R.id.action_switch:
                Intent myFriendsIntent = new Intent(this, MainActivity.class);
                startActivity(myFriendsIntent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showLoader() {
        mMyFriendsLoader.setVisibility(View.VISIBLE);
        mFriendsList.setVisibility(View.GONE);
    }

    @Override
    public void hideLoader() {
        mMyFriendsLoader.setVisibility(View.GONE);
        mFriendsList.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        com.appswarrior.www.findfriends.utils.Application.getInstance().setConnectivityListener(this);

    }

    @Override
    public void sendMessage() {

    }

    @Override
    public void sendEmail() {

    }

    @Override
    public void startCall() {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void requestFriendsList(List<Friend> friendsList) {
        mFriendsAdapter = new adapter(this, friendsList);
        mFriendsList.setAdapter(mFriendsAdapter);
        FriendModel friendModel = new FriendModel();
        realm.beginTransaction();
        realm.deleteAll();
        for (Friend friend : friendsList) {
            friendModel.setFirstName(friend.getFirstName());
            friendModel.setLastName(friend.getLastName());
            friendModel.setCall(friend.getCall());
            friendModel.setEmail(friend.getEmail());
            friendModel.setId(friend.getId());
            friendModel.setLat(friend.getLocation().get(0));
            friendModel.setLng(friend.getLocation().get(1));
            realm.insert(friendModel);
        }
        realm.commitTransaction();
        mFriendsAdapter.notifyDataSetChanged();


    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        checkConnection();
    }
}
