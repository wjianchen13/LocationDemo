package com.example.locationdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;

public class LocationActivity extends AppCompatActivity {

    private static final String TAG = "ceshi";
    private static final int LOCATION_REQUEST_CODE = 1;
    private static final int LISTENER_REQUEST_CODE = 2;

    private TextView tvLocation;


    private String[] permissionList = new String[]{
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    };


    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onProviderEnabled(@NonNull String provider) {
            Log.i(TAG, "onProviderEnabled: ");
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            updateToNewLocation(null);
            Log.i(TAG, "onProviderDisabled: ");
        }

        @Override
        public void onLocationChanged(@NonNull Location location) {
            Log.i(TAG, "onLocationChanged: 经纬度发生变化");
            //调用更新位置
            updateToNewLocation(location);
        }
    };


    //获取位置服务
    private LocationManager locationManager;

    //用来在屏幕上显示位置信息
    private StringBuilder locationInfo = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        tvLocation = findViewById(R.id.tv_location);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * 获取位置信息
     * @param v
     */
    public void onTest1(View v) {
        //查看支持获取经纬度的方式有哪些
        StringBuilder supportWay = new StringBuilder();
        //passive：被动的从其他程序获取
        List<String> providerList = locationManager.getProviders(true);
        if(providerList != null && providerList.size() > 0) {
            for(int i = 0; i <providerList.size(); i ++) {
                String provider = providerList.get(i);
                supportWay.append(provider + "\t");
            }
            locationInfo.append("支持的获取经纬度的方式有: " + supportWay).append("\n");
        }

        Log.i(TAG, "支持的获取经纬度的方式有: " + supportWay);

        getLocationInfo();

    }

    /**
     * 设置监听器，周期性获取定位信息
     * @param v
     */
    public void onTest2(View v) {
        locationMonitor();
    }

    /**
     * 跳转到系统设置处来打开权限
     * @param v
     */
    public void onTest3(View v) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 887);
    }

    /**
     * 判断谷歌服务是否支持
     * @param v
     */
    public void onTest4(View v) {
        isSupportGoogleService();
    }


    /**
     * 查看手机是否支持谷歌服务()
     */
    private void isSupportGoogleService() {

    }

    /**
     * 判断定位服务是否开启
     */
    private boolean isLocationServiceOpen() {
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        //有一个开启就可
        return gps || network;
    }

    /**
     * 获取地理位置信息
     */
    @SuppressLint("MissingPermission")
    private void getLocationInfo() {

        //判断是否开启位置服务（二）
        if (isLocationServiceOpen()) {
            //获取所有支持的provider（三）
            List<String> providers = locationManager.getProviders(true);
            //存储最优的结果
            Location betterLocation = null;
            for(int i = 0; i < providers.size(); i ++) {
                String provider = providers.get(i);
                Location location = locationManager.getLastKnownLocation(provider);
                if(location != null) {
                    Log.i(TAG, provider + " 精度为：" + location.getAccuracy());
                    locationInfo.append(provider + " 精度为："  + location.getAccuracy() + "\n");
                    if (betterLocation == null) {
                        betterLocation = location;
                    } else {
                        //因为半径等于精度，所以精度越低代表越准确
                        if (location.getAccuracy() < betterLocation.getAccuracy())
                            betterLocation = location;
                    }
                }
                if (location == null) {
                    Log.i(TAG, provider + " 获取到的位置为null");
                    locationInfo.append(provider + " 获取到的位置为null \n");
                }
            }

            if(betterLocation != null){
                Log.i(TAG, "精度最高的获取方式：" + betterLocation.getProvider() + "经度：" + betterLocation.getLongitude() + " 纬度：" + betterLocation.getLatitude());
                locationInfo.append("精度最高的获取方式：" + betterLocation.getProvider() + "经度：" + betterLocation.getLongitude() + " 纬度：" + betterLocation.getLatitude() + "\n");
                getLocationCountryName(this, betterLocation.getLatitude(), betterLocation.getLongitude());
            }
            //（四）若所支持的定位获取方式获取到的都是空，则开启连续定位服务
            if (betterLocation == null) {

                List<String> all = locationManager.getProviders(true);
                for(int i = 0; i < all.size(); i ++) {
                    locationMonitor(all.get(i));
                }
                Log.i(TAG, "getLocationInfo: 获取到的经纬度均为空，已开启连续定位监听");
                locationInfo.append("getLocationInfo: 获取到的经纬度均为空，已开启连续定位监听 \n");
            }
            tvLocation.setText(locationInfo);


        } else {
            Toast.makeText(this, "请跳转到系统设置中打开定位服务", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 连续位置监听
     * 获取连续的点位信息
     * 定位模式、更新的时间单位(毫秒)、更新的距离单位(米)、位置信息的监听
     */
    @SuppressLint("MissingPermission")
    private void locationMonitor() {

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                3000,
                0,
                locationListener
        );
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                3000,
                0,
                locationListener
        );
    }

    @SuppressLint("MissingPermission")
    private void locationMonitor(String provider) {
        Log.i(TAG, "locationMonitor: 开启了连续定位 " + provider);
        locationManager.requestLocationUpdates(
                provider,
                3000,
                0,
                locationListener
        );
    }

    /**
     * 更新位置信息
     */
    private void  updateToNewLocation(Location location) {
        //纬度
        String lat = "";
        //经度
        String lng = "";
        if (location != null) {
            lat = location.getLatitude() + "";
            lng = location.getLongitude() + "";
            Log.i(TAG, "updateToNewLocation: 经度为：" + lng);
            Log.i(TAG, "updateToNewLocation: 纬度为：" + lat);
            locationInfo.append("updateToNewLocation经度为：" + lng + "  纬度为：" + lat +"\n");
            tvLocation.setText(locationInfo);
        } else {
            Log.i(TAG, "updateToNewLocation: location为空");
        }
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        } else {
            Toast.makeText(this, "无法获取地理信息，请确认已开启定位权限并选择定位模式为GPS、WLAN和移动网络", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 根据经度纬度 获取国家
     */
    public static String getLocationCountryName(Context context, double latitude, double longitude) {
        String countryCode = "";
        List<Address> addList = null;
        Geocoder ge = new Geocoder(context);
        try {
            addList = ge.getFromLocation(latitude, longitude, 1);
            if (addList != null && addList.size() > 0) {
                Address ad = addList.get(0);
                countryCode = ad.getCountryCode();
                if (TextUtils.isEmpty(countryCode) || countryCode.equals("0")){
                    countryCode =  ad.getLocale().getCountry();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("getLocationCountryName","countryCode:" + countryCode);
        return countryCode;
    }

}