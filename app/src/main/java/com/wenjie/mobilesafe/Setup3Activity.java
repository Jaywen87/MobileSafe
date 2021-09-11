package com.wenjie.mobilesafe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wenjie.mobilesafe.utils.PermissionGoup;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class Setup3Activity extends BaseSetupActivity implements EasyPermissions.PermissionCallbacks{

    private EditText et_contact;
    private final static String TAG = "Setup3Activity4444";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);

        Button btn_select = findViewById(R.id.btn_select);
        String safenumber = sp.getString("safenumber", "");
        et_contact = findViewById(R.id.et_contact);
        et_contact.setText(safenumber);
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(Setup3Activity.this, PermissionGoup.CONTACTS)) {
                    Log.i(TAG, "onClick: 有权限");
                    Intent intent = new Intent(Setup3Activity.this, SelectContactActivity.class);
                    startActivityForResult(intent,0);
                } else {
                    Log.i(TAG, "onClick: 没有权限");
                    EasyPermissions.requestPermissions(Setup3Activity.this, "查找电话号码需要读取联系人权限",
                            PermissionGoup.CONTACTS_CODE, PermissionGoup.CONTACTS);
//            EasyPermissions.requestPermissions(
//                    new PermissionRequest.Builder(Setup3Activity.this, RC_CONTACTS, perms)
//                            .setRationale("查找电话号码需要读取联系人权限")
//                            .setPositiveButtonText("确定")
//                            .setNegativeButtonText("取消")
//                            .setTheme(R.style.my_fancy_style)
//                            .build());

                }

            }
        });
    }

    @Override
    public void showNext() {
        String phone = et_contact.getText().toString().trim();
        if(TextUtils.isEmpty(phone)) {
            Toast.makeText(this,"安全号码还没有设置",Toast.LENGTH_LONG).show();
            return;
        }
        //保存安全号码
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("safenumber",phone);
        edit.commit();

        //进入下一页面
        Intent intent = new Intent(Setup3Activity.this, Setup4Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in,R.anim.tran_out);
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(Setup3Activity.this, Setup2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in_pre,R.anim.tran_out_pre);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null) {
            return;
        }
        String phone = data.getStringExtra("phone").replace("-","");
        et_contact.setText(phone);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
        Log.i(TAG, "onRequestPermissionsResult: "+ grantResults);


    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.i(TAG, "onPermissionsGranted: 权限申请成功");
        //权限申请陈功之后，进入联系人界面
        Intent intent = new Intent(Setup3Activity.this, SelectContactActivity.class);
        startActivityForResult(intent,0);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.i(TAG, "onPermissionsDenied: 权限申请失败");
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            Toast.makeText(this,"查看联系人必须有获取联系人权限",Toast.LENGTH_SHORT).show();
        }



    }
}