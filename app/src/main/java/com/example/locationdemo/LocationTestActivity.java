package com.example.locationdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LocationTestActivity extends AppCompatActivity {

    private TextView tvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_test);
        tvLocation = findViewById(R.id.tv_location);

    }

    /**
     * 获取位置信息
     * @param v
     */
    public void onTest1(View v) {
        TestLocationManager.getInstance(this).getLocationInfo();
    }

    /**
     * 设置监听器，周期性获取定位信息
     * @param v
     */
    public void onTest2(View v) {
        tvLocation.setText("经度: " + TestLocationManager.getInstance(this).getLongitude()
                + "   纬度：" + TestLocationManager.getInstance(this).getLatitude()
                + "   国家代码：" + TestLocationManager.getInstance(this).getCountryCode());
    }

}