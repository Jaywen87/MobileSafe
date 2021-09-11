package com.wenjie.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.wenjie.mobilesafe.ui.SettingItemView;
import com.wenjie.mobilesafe.utils.PermissionGoup;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class Setup2Activity extends BaseSetupActivity implements EasyPermissions.PermissionCallbacks{


    private SettingItemView siv_bind;
    private TelephonyManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);


        siv_bind = findViewById(R.id.siv_bind);
        String sim = sp.getString("sim", null);

        if(TextUtils.isEmpty(sim)) {
            siv_bind.setChecked(false);
        } else {
            siv_bind.setChecked(true);
        }
        siv_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = sp.edit();
                if(siv_bind.isChecked()) {
                    siv_bind.setChecked(false);
                    edit.putString("sim",null);
                } else {
                    if (EasyPermissions.hasPermissions(Setup2Activity.this, PermissionGoup.PHONE)) {
                        String simSerialNumber = tm.getSimSerialNumber();
                        siv_bind.setChecked(true);
                        edit.putString("sim",simSerialNumber);

                    } else {
                        EasyPermissions.requestPermissions(Setup2Activity.this, "需要获取SIM信息",
                                PermissionGoup.PHONE_CODE, PermissionGoup.PHONE);
                    }
                }
                edit.commit();
            }
        });



    }

    @Override
    public void showNext() {
        Intent intent = new Intent(Setup2Activity.this, Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in,R.anim.tran_out);
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(Setup2Activity.this, Setup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in_pre,R.anim.tran_out_pre);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        SharedPreferences.Editor edit = sp.edit();
        String simSerialNumber = tm.getSimSerialNumber();
        siv_bind.setChecked(true);
        edit.putString("sim",simSerialNumber);
        edit.commit();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            Toast.makeText(this,"开启此功能必须有获取SIM信息权限",Toast.LENGTH_SHORT).show();
        }
    }
}