package com.wenjie.mobilesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wenjie.mobilesafe.ui.SettingItemView;


public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "SettingActivity" ;
    private SharedPreferences sp;
    private SettingItemView siv_update;
    //private TextView tv_dsec;
    //private CheckBox cb_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sp = getSharedPreferences("config", MODE_PRIVATE);
        Boolean update = sp.getBoolean("update",false);
        Log.i(TAG, "onCreate: update=" + update);

        //TextView tv_title = findViewById(R.id.tv_title);

        //使用自定义组合空间
        siv_update = (SettingItemView)findViewById(R.id.siv_update);
        if(update) {
            siv_update.setDescText("自动下载更新已开启");
            siv_update.setChecked(true);
        } else {
            siv_update.setDescText("自动下载更新已关闭");
            siv_update.setChecked(false);
        }
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = sp.edit();
                if(siv_update.isChecked()) {
                    Log.i(TAG, "onClick: " + siv_update.isChecked());
                    siv_update.setDescText("自动下载更新已关闭");
                    siv_update.setChecked(false);
                    edit.putBoolean("update",false);
                } else {
                    Log.i(TAG, "onClick: " + siv_update.isChecked());
                    siv_update.setDescText("自动下载更新已开启");
                    siv_update.setChecked(true);
                    edit.putBoolean("update",true);
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
    }

//    public void onClick() {
//        SharedPreferences.Editor edit = sp.edit();
//        if(siv_update.isChecked()) {
//            Log.i(TAG, "onClick: " + siv_update.isChecked());
//            siv_update.setDescText("自动下载更新已开启");
//            edit.putBoolean("update",true);
//        } else {
//            Log.i(TAG, "onClick: " + siv_update.isChecked());
//            siv_update.setDescText("自动下载更新已关闭");
//            edit.putBoolean("update",false);
//        }
//        edit.commit();
//    }
}