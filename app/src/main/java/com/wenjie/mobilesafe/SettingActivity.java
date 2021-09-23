package com.wenjie.mobilesafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wenjie.mobilesafe.service.AddressService;
import com.wenjie.mobilesafe.service.CallSmsSafeService;
import com.wenjie.mobilesafe.service.WatchDogService;
import com.wenjie.mobilesafe.ui.SettingClickView;
import com.wenjie.mobilesafe.ui.SettingItemView;
import com.wenjie.mobilesafe.utils.PermissionGoup;
import com.wenjie.mobilesafe.utils.ServiceUtils;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


public class SettingActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "SettingActivity" ;
    private SharedPreferences sp;

    //设置开启自动更新
    private SettingItemView siv_update;
    //private TextView tv_dsec;
    //private CheckBox cb_status;

    //设置号码归属显示
    private SettingItemView siv_show_address;
    private Intent showAddressIntent;

    //设置号码归属地背景
    private SettingClickView scv_bg;

    //设置黑名单
    private SettingItemView siv_callsms_safe;
    private Intent callSmsmSafeIntent;

    //设置应用锁
    private SettingItemView siv_watch_dog;
    private Intent watchDogIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sp = getSharedPreferences("config", MODE_PRIVATE);
        Boolean update = sp.getBoolean("update",false);
        Log.i(TAG, "onCreate: update=" + update);

        //TextView tv_title = findViewById(R.id.tv_title);

        //使用自定义组合空间  开启自动下载更新
        siv_update = (SettingItemView)findViewById(R.id.siv_update);
        if(update) {
            //siv_update.setDescText("自动下载更新已开启");
            if(EasyPermissions.hasPermissions(this, PermissionGoup.STORAGE)){
                siv_update.setChecked(true);
            } else {
                siv_update.setChecked(false);
            }
        } else {
            //siv_update.setDescText("自动下载更新已关闭");
            siv_update.setChecked(false);
        }
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = sp.edit();
                if(siv_update.isChecked()) {
                    Log.i(TAG, "onClick: " + siv_update.isChecked());
                    //siv_update.setDescText("自动下载更新已关闭");
                    siv_update.setChecked(false);
                    edit.putBoolean("update",false);
                } else {
                    Log.i(TAG, "onClick: " + siv_update.isChecked());
                    //siv_update.setDescText("自动下载更新已开启");
                    if(EasyPermissions.hasPermissions(SettingActivity.this, PermissionGoup.STORAGE)){
                        siv_update.setChecked(true);
                        edit.putBoolean("update",true);
                    } else {
                        EasyPermissions.requestPermissions(SettingActivity.this,"此功能需要用到此权限",PermissionGoup.STORAGE_CODE,PermissionGoup.STORAGE);
                    }

                }
                edit.commit();
            }
        });


//        cb_status = (CheckBox)findViewById(R.id.cb_status);
//        tv_dsec = findViewById(R.id.tv_desc);
//        if(update) {
//            tv_dsec.setText("自动下载更新已开启");
//            cb_status.setChecked(true);
//        } else {
//            tv_dsec.setText("自动下载更新已关闭");
//            cb_status.setChecked(false);
//        }
//
//
//
//        cb_status.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedPreferences.Editor edit = sp.edit();
//
//                if(cb_status.isChecked()) {
//                    tv_dsec.setText("自动下载更新已开启");
//                    edit.putBoolean("update",true);
//                } else {
//                    tv_dsec.setText("自动下载更新已关闭");
//                    edit.putBoolean("update",false);
//                }
//                edit.commit();
//            }
//        });


        //设置是否开启来电显示
        siv_show_address = (SettingItemView)findViewById(R.id.siv_show_address);
        boolean isRunning = ServiceUtils.isServiceRunning(this,"com.wenjie.mobilesafe.service.AddressService");
        if(isRunning) {
            siv_show_address.setChecked(true);
        } else {
            siv_show_address.setChecked(false);
        }
        showAddressIntent = new Intent(this, AddressService.class);
        siv_show_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(siv_show_address.isChecked()) {
                    siv_show_address.setChecked(false);
                    stopService(showAddressIntent);
                } else {
                    siv_show_address.setChecked(true);
                    startService(showAddressIntent);
                }

            }
        });


        //设置号码归属地的背景
        scv_bg = findViewById(R.id.scv_changeBg);
        scv_bg.setTitleText("归属地提示框风格");
        final String [] items = {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
        int which = sp.getInt("which",0);
        scv_bg.setDescText(items[which]);
        scv_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id  = sp.getInt("which",0);
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("归属地提示框风格");
                builder.setSingleChoiceItems(items, id, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor  = sp.edit();
                        editor.putInt("which",which);
                        editor.commit();
                        scv_bg.setDescText(items[which]);
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.show();

            }
        });

        //设置黑名单
        siv_callsms_safe = (SettingItemView)findViewById(R.id.siv_callsms_safe);
        boolean isCallSmsRunning = ServiceUtils.isServiceRunning(this,"com.wenjie.mobilesafe.service.CallSmsSafeService");
        if(isCallSmsRunning) {
            siv_callsms_safe.setChecked(true);
        } else {
            siv_callsms_safe.setChecked(false);
        }
        callSmsmSafeIntent = new Intent(this, CallSmsSafeService.class);
        siv_callsms_safe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(siv_callsms_safe.isChecked()) {
                    siv_callsms_safe.setChecked(false);
                    stopService(callSmsmSafeIntent);
                } else {
                    siv_callsms_safe.setChecked(true);
                    startService(callSmsmSafeIntent);
                }

            }
        });


        //设置是否开启应用锁
        siv_watch_dog = (SettingItemView)findViewById(R.id.siv_watch_dog);
        boolean isWatchDogRunning = ServiceUtils.isServiceRunning(this,"com.wenjie.mobilesafe.service.WatchDogService");
        if(isWatchDogRunning) {
            siv_watch_dog.setChecked(true);
        } else {
            siv_watch_dog.setChecked(false);
        }
        watchDogIntent = new Intent(this, WatchDogService.class);
        siv_watch_dog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(siv_watch_dog.isChecked()) {
                    siv_watch_dog.setChecked(false);
                    stopService(watchDogIntent);
                } else {
                    siv_watch_dog.setChecked(true);
                    startService(watchDogIntent);
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //设置是否开启来电显示
        boolean isRunning = ServiceUtils.isServiceRunning(this,"com.wenjie.mobilesafe.service.AddressService");
        if(isRunning) {
            siv_show_address.setChecked(true);
        } else {
            siv_show_address.setChecked(false);
        }

        //设置黑名单
        boolean isCallSmsRunning = ServiceUtils.isServiceRunning(this,"com.wenjie.mobilesafe.service.CallSmsSafeService");
        if(isCallSmsRunning) {
            siv_callsms_safe.setChecked(true);
        } else {
            siv_callsms_safe.setChecked(false);
        }

        //设置是否开启应用锁
        boolean isWatchDogRunning = ServiceUtils.isServiceRunning(this,"com.wenjie.mobilesafe.service.WatchDogService");
        if(isWatchDogRunning) {
            siv_watch_dog.setChecked(true);
        } else {
            siv_watch_dog.setChecked(false);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }
}