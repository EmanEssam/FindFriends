package com.appswarrior.www.findfriends.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appswarrior.www.findfriends.R;
import com.appswarrior.www.findfriends.model.api.Friend;
import com.appswarrior.www.findfriends.model.data.FriendModel;
import com.appswarrior.www.findfriends.myfriends.MyFriends;
import com.appswarrior.www.findfriends.adapter.FriendsListAdapter;
import com.appswarrior.www.findfriends.utils.Application;
import com.appswarrior.www.findfriends.utils.ConnectivityReceiver;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, HomeView, OnMapReadyCallback, FriendsListAdapter.OnListClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerView2;
    private ProgressBar mFriendListLoader;
    private Button mCallBtn, mMsgBtn, mMailBtn;
    private List<Friend> mFriendsList = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    RealmResults<FriendModel> friends;
    HomePresenterImpl homePresenter = new HomePresenterImpl();
    Realm realm = Realm.getDefaultInstance();

    private GoogleMap mMap;

    private FriendsListAdapter mFriendsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkInternetConnection();
        friends = realm.where(FriendModel.class)
                .findAll();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE); // create location manager
        homePresenter.setView(this);
        initViews();
        if (friends != null && friends.size() > 0) {
            mFriendsAdapter = new FriendsListAdapter(this, friends, this);
        }
        mRecyclerView.setAdapter(mFriendsAdapter);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) { // check the gps permission
            Toast.makeText(this, R.string.turn_on_gps, Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser(); //  ask the user for permission
        }
        if (mGoogleApiClient == null) { // google api client callbacks
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        homePresenter.setFriendsList();
        mCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCall();
            }
        });
        mMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        mMailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

    }

    private void checkInternetConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {

            message = getString(R.string.connect_to_internet);
            color = Color.WHITE;
        } else {
            message = getString(R.string.not_connected);
            color = Color.RED;
        }
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();


    }


    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.enable_gps)
                .setCancelable(false)
                .setPositiveButton(R.string.go_to_gps_settings,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
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
                Intent myFriendsIntent = new Intent(this, MyFriends.class);
                startActivity(myFriendsIntent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
    }


    void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.friends_list);
        mCallBtn = (Button) findViewById(R.id.call_btn);
        mMsgBtn = (Button) findViewById(R.id.msg_btn);
        mMailBtn = (Button) findViewById(R.id.email_btn);
        mFriendListLoader = (ProgressBar) findViewById(R.id.friends_list_loader);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void showLoader() {
        mFriendListLoader.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void hideLoader() {
        mFriendListLoader.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void sendMessage() {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"));
        startActivity(sendIntent);
    }

    @Override
    public void sendEmail() {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"to@email.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Text");
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    @Override
    public void startCall() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:0123456789"));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Application.getInstance().setConnectivityListener(this);

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void requestFriendsList(List<Friend> friendsList) {
        mFriendsList = friendsList;
        mFriendsAdapter = new FriendsListAdapter(this, friendsList, this);
        mFriendsAdapter.notifyDataSetChanged();
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
    }

    Location startPoint = new Location("");

    @Override
    public void onListClickListener(int position) {
        Location endPoint = new Location("");
        double distance;
        if (friends != null) {
            friends.get(position);
            LatLng current = new LatLng(startPoint.getLatitude(), startPoint.getLongitude());
            endPoint.setLatitude(friends.get(position).getLat());
            endPoint.setLongitude(friends.get(position).getLng());
            distance = startPoint.distanceTo(endPoint);
            mMap.addMarker(new MarkerOptions().position(current).title(friends.get(position).getFirstName() + " " + (friends.get(position).getLastName())).snippet("is far away :" + distance + "Meters"));

        } else {
            mFriendsList.get(position);
            LatLng current = new LatLng(startPoint.getLatitude(), startPoint.getLongitude());
            endPoint.setLatitude(mFriendsList.get(position).getLocation().get(0));
            endPoint.setLongitude(mFriendsList.get(position).getLocation().get(1));
            distance = startPoint.distanceTo(endPoint);
            mMap.addMarker(new MarkerOptions().position(current).title(mFriendsList.get(position).getFirstName() + " " + (mFriendsList.get(position).getLastName())).snippet("is far away :" + distance + "Meters"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            Location userCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (userCurrentLocation != null) {
                MarkerOptions currentUserLocation = new MarkerOptions();
                LatLng currentUserLatLang = new LatLng(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
                currentUserLocation.position(currentUserLatLang);
                mMap.addMarker(currentUserLocation);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserLatLang, 16));
                startPoint = userCurrentLocation;

            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        checkInternetConnection();
    }
}
