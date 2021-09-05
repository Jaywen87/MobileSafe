package com.wenjie.mobilesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;


public class SettingActivity extends AppCompatActivity {

    private SharedPreferences sp;
    private TextView tv_dsec;
    private CheckBox cb_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sp = getSharedPreferences("config", MODE_PRIVATE);
        Boolean update = sp.getBoolean("update",true);

        TextView tv_title = findViewById(R.id.tv_title);
        cb_status = (CheckBox)findViewById(R.id.cb_status);
        tv_dsec = findViewById(R.id.tv_desc);
        if(update) {
            tv_dsec.setText("自动下载更新已开启");
            cb_status.setChecked(true);
        } else {
            tv_dsec.setText("自动下载更新已关闭");
            cb_status.setChecked(false);
        }



        cb_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = sp.edit();

                if(cb_status.isChecked()) {
                    tv_dsec.setText("自动下载更新已开启");
                    edit.putBoolean("update",true);
                } else {
                    tv_dsec.setText("自动下载更新已关闭");
                    edit.putBoolean("update",false);
                }
                edit.commit();
            }
        });





    }
}