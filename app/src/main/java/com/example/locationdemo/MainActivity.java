package com.example.locationdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 请求权限
     * @param v
     */
    public void onTest1(View v) {
        XXPermissions.with(this)
                // 申请单个权限
                .permission(Permission.ACCESS_COARSE_LOCATION)
                // 申请多个权限
                .permission(Permission.ACCESS_FINE_LOCATION)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            Toast.makeText(MainActivity.this, "位置权限获取成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "获取部分权限成功，但部分权限未正常授予", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            Toast.makeText(MainActivity.this, "被永久拒绝授权，请手动授予录音和日历权限", Toast.LENGTH_SHORT).show();
                            XXPermissions.startPermissionActivity(MainActivity.this, permissions);
                        } else {
                            Toast.makeText(MainActivity.this, "获取录音和日历权限失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * 获取位置信息
     * @param v
     */
    public void onTest2(View v) {
        startActivity(new Intent(this, LocationActivity.class));
    }

    /**
     * 测试TestLocationManager
     * @param v
     */
    public void onTest3(View v) {
        startActivity(new Intent(this, LocationTestActivity.class));
    }

}