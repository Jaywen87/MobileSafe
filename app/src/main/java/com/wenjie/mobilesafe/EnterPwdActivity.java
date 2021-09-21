package com.wenjie.mobilesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EnterPwdActivity extends AppCompatActivity {

    private EditText et_password;
    private String packname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pwd);
        et_password = findViewById(R.id.et_password);
        TextView tv_app_name = findViewById(R.id.tv_app_name);
        ImageView iv_app_icon = findViewById(R.id.iv_app_icon);
        Intent intent = getIntent();
        packname = intent.getStringExtra("packname");
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(packname, 0);
            tv_app_name.setText(info.loadLabel(pm));
            iv_app_icon.setImageDrawable(info.loadIcon(pm));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void onClick(View view) {
        String password = et_password.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        } else {
            if ("123".equals(password)) {
                Intent intent = new Intent();
                intent.setAction("com.wenjie.mobilesafe.tempstop");
                intent.putExtra("packname", packname);
                sendBroadcast(intent);
                finish();
            } else {
                Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}