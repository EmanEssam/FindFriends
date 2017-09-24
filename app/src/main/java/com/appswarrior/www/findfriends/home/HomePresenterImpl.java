package com.appswarrior.www.findfriends.home;

import android.widget.Toast;

import com.appswarrior.www.findfriends.model.api.FriendsResponse;
import com.appswarrior.www.findfriends.service.RetrofitHelper;
import com.appswarrior.www.findfriends.utils.Constants;

import io.realm.Realm;
import io.realm.RealmModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hp on 22/09/2017.
 */

public class HomePresenterImpl implements HomePresenter {
    HomeView view;
    Realm realm = Realm.getDefaultInstance();

    @Override
    public void setView(HomeView view) {

        this.view = view;
    }

    @Override
    public void setFriendsList() {
        view.showLoader();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitHelper service = retrofit.create(RetrofitHelper.class);
        Call<FriendsResponse> friendsResponseCall = service.getFriendsList();

        friendsResponseCall.enqueue(new Callback<FriendsResponse>() {

            @Override
            public void onResponse(Call<FriendsResponse> call, Response<FriendsResponse> response) {
                view.hideLoader();
                view.requestFriendsList(response.body().getFriends());
            }


            @Override
            public void onFailure(Call<FriendsResponse> call, Throwable t) {
                view.hideLoader();
                Toast.makeText(view.getContext(), "failed!" + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
//        ServiceManager.getFriends(view.getContext());
    }
}
