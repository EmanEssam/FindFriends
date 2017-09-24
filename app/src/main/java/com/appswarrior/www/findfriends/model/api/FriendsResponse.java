package com.appswarrior.www.findfriends.model.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hp on 20/09/2017.
 */

public class FriendsResponse {
    @SerializedName("version")
    private Integer version;
    @SerializedName("friends")
    private List<Friend> friends = null;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }
}
