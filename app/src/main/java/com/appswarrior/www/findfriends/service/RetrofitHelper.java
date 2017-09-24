package com.appswarrior.www.findfriends.service;

import com.appswarrior.www.findfriends.model.api.FriendsResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by hp on 21/09/2017.
 */

public interface RetrofitHelper {
    @GET("friends.txt")
    Call<FriendsResponse> getFriendsList();
}
