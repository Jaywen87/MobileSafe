package com.wenjie.mobilesafe;

import androidx.annotation.NonNull;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.Toast;

import com.wenjie.mobilesafe.utils.PermissionGoup;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class Setup4Activity extends BaseSetupActivity implements EasyPermissions.PermissionCallbacks{

    private CheckBox cb_protecting;
    private String TAG = "Setup4Activity444444";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        boolean protecting = sp.getBoolean("protecting", false);

        cb_protecting = findViewById(R.id.cb_protecting);
        if (protecting) {
            cb_protecting.setChecked(true);
            cb_protecting.setText("手机防盗已开启");
        } else {
            cb_protecting.setChecked(false);
            cb_protecting.setText("手机防盗已关闭");
        }
        cb_protecting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_protecting.isChecked()) {
                    Log.i(TAG, "onClick: " + cb_protecting.isChecked());
                    return;
                }
                if (EasyPermissions.hasPermissions(Setup4Activity.this, PermissionGoup.SMS_AND_LOCATION)) {
                    Log.i(TAG, "onClick: " + "已有权限不作处理");

                } else {
                    cb_protecting.setChecked(false);
                    cb_protecting.setText("手机防盗已关闭");
                    EasyPermissions.requestPermissions(Setup4Activity.this, "打开此功能必须要下面的权限",
                            PermissionGoup.SMS_AND_LOCATION_CODE, PermissionGoup.SMS_AND_LOCATION);
                }
            }
        });
        cb_protecting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_protecting.setText("手机防盗已开启");
                } else {
                    cb_protecting.setText("手机防盗已关闭");
                }
                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean("protecting",isChecked);
                edit.commit();
                Log.i(TAG, "onCheckedChanged:protecting =  " + isChecked);
            }
        });
    }

    @Override
    public void showNext() {
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("configed",true);
        edit.commit();
        Intent intent = new Intent(Setup4Activity.this, LostFindActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(Setup4Activity.this, Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in_pre,R.anim.tran_out_pre);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.i(TAG, "onPermissionsGranted: requestCode:" + requestCode + "list:" + perms + "size:" + perms.size());
        if(perms.size() == 3) {
            cb_protecting.setChecked(true);
            cb_protecting.setText("手机防盗已开启");
        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            Toast.makeText(this,"手机防盗必须有sms和位置服务的权限",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }
}