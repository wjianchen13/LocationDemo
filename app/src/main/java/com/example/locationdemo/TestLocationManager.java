package com.example.locationdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hjq.permissions.XXPermissions;

import java.io.IOException;
import java.util.List;

public class TestLocationManager {

    private static final String TAG = TestLocationManager.class.getSimpleName();

    private static TestLocationManager INSTANCE;

    /**
     * 获取位置服务
     */
    private LocationManager locationManager;

    /**
     * 经度
     */
    private double longitude;

    /**
     * 纬度
     */
    private double latitude;

    /**
     * 国家代码
     */
    private String countryCode;

    private Context mContext;

    private String[] locationPermissionList = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };


    public static TestLocationManager getInstance(Context context) {
        if(INSTANCE == null) {
            synchronized (TestLocationManager.class) {
                if(INSTANCE == null)
                    INSTANCE = new TestLocationManager(context);
            }
        }
        return INSTANCE;
    }

    private TestLocationManager(Context context) {
        mContext = context;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getCountryCode() {
        return countryCode;
    }

    @SuppressLint("MissingPermission")
    public void getLocationInfo() {
        if(locationManager == null) {
            locationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
        }
        if(XXPermissions.isGranted(mContext, locationPermissionList)) {
            //判断是否开启位置服务（二）
            if (isLocationServiceOpen() && mContext != null) {
                //获取所有支持的provider（三）
                List<String> providers = locationManager.getProviders(true);
                // 存储最优的结果
                Location betterLocation = null;
                for (int i = 0; i < providers.size(); i++) {
                    String provider = providers.get(i);
                    Location location = locationManager.getLastKnownLocation(provider);
                    if (location != null) {
                        Log.i(TAG, provider + " 精度为：" + location.getAccuracy());
                        if (betterLocation == null) {
                            betterLocation = location;
                        } else {
                            // 因为半径等于精度，所以精度越低代表越准确
                            if (location.getAccuracy() < betterLocation.getAccuracy())
                                betterLocation = location;
                        }
                    }
                    if (location == null) {
                        Log.i(TAG, provider + " 获取到的位置为null");
                    }
                }

                if (betterLocation != null) {
                    Log.i(TAG, "精度最高的获取方式：" + betterLocation.getProvider() + "经度：" + betterLocation.getLongitude() + " 纬度：" + betterLocation.getLatitude());
                    latitude = betterLocation.getLatitude();
                    longitude = betterLocation.getLongitude();
                    countryCode = getLocationCountryName(mContext, betterLocation.getLatitude(), betterLocation.getLongitude());
                    Toast.makeText(mContext, "获取定位信息成功", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(mContext, "请跳转到系统设置中打开定位服务", Toast.LENGTH_SHORT).show();
            }
        }
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
     * 根据经度纬度 获取国家
     * @param latitude 纬度
     * @param longitude 精度
     */
    private String getLocationCountryName(Context context, double latitude, double longitude) {
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
