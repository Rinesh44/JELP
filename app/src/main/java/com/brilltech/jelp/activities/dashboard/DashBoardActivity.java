package com.brilltech.jelp.activities.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.brilltech.jelp.R;
import com.brilltech.jelp.activities.base.BaseActivity;
import com.brilltech.jelp.api.Endpoints;
import com.brilltech.jelp.entities.JelpProto;
import com.brilltech.jelp.utils.AppUtils;
import com.brilltech.jelp.utils.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.brilltech.jelp.JelpApp.getMyApplication;

public class DashBoardActivity extends BaseActivity implements View.OnClickListener, DashBoardView {
    private static final String TAG = "DashBoardActivity";
    public static final int PERMISSION_ID = 444;
    @Inject
    Endpoints endpoints;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nv)
    NavigationView mNavigationView;
    @BindView(R.id.btn_assault)
    RelativeLayout mAssault;
    @BindView(R.id.btn_medical)
    RelativeLayout mMedical;

    private ActionBarDrawerToggle actionBarToggle;
    private String token;
    private SharedPreferences preferences;
    private DashBoardPresenter presenter;
    private FusedLocationProviderClient mFusedLocationClient;
    public static double lat, lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        ButterKnife.bind(this);

        initialize();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        mAssault.setOnClickListener(this);
        mMedical.setOnClickListener(this);
    }

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        mToolbarTitle.setText("Dashboard");

        actionBarToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(actionBarToggle);
        actionBarToggle.syncState();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        getMyApplication(this).getAppComponent().inject(this);

        presenter = new DashBoardPresenterImpl(this, endpoints);
        token = preferences.getString(Constants.TOKEN, "");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_assault:
                showLoading();
                presenter.emergency(token, JelpProto.RequestType.ASSAULT, lat, lng);
                break;

            case R.id.btn_medical:
                showLoading();
                presenter.emergency(token, JelpProto.RequestType.MEDICAL, lat, lng);
                break;
        }
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    AppUtils.showLog(TAG, "lat: " + location.getLatitude());
                                    AppUtils.showLog(TAG, "lng: " + location.getLongitude());
                                    lat = location.getLatitude();
                                    lng = location.getLongitude();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            AppUtils.showLog(TAG, "lat callback: " + mLastLocation.getLatitude());
            AppUtils.showLog(TAG, "lng callback: " + mLastLocation.getLongitude());

            lat = mLastLocation.getLatitude();
            lng = mLastLocation.getLongitude();
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    @Override
    public void onAssaultNotifySuccess() {
        AppUtils.showLog(TAG, "send assault success");
    }

    @Override
    public void onAssaultNotifyFail(String msg) {
        showMessage(msg);
    }

    @Override
    public void onMedicalNotifySuccess() {
        AppUtils.showLog(TAG, "send medical success");

    }

    @Override
    public void onMedicalNotifyFail(String msg) {
        showMessage(msg);
    }
}
