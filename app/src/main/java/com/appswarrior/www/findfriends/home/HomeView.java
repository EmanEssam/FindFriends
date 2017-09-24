package com.appswarrior.www.findfriends.home;

import android.content.Context;

import com.appswarrior.www.findfriends.model.api.Friend;

import java.util.List;

/**
 * Created by hp on 22/09/2017.
 */

public interface HomeView {

    void showLoader();

    void hideLoader();

    void sendMessage();

    void sendEmail();

    void startCall();

    Context getContext();

    void requestFriendsList(List<Friend> friendsList);
}
