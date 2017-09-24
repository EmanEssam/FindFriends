package com.appswarrior.www.findfriends.utils;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by hp on 23/09/2017.
 */

public class Application extends android.app.Application {
    private static Application mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        mInstance = this;
    }

    public static synchronized Application getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}