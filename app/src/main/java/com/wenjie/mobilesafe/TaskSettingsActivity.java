package com.wenjie.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;

import com.wenjie.mobilesafe.service.AutoCleanService;
import com.wenjie.mobilesafe.utils.ServiceUtils;

public class TaskSettingsActivity extends AppCompatActivity {

    private static final String TAG = "TaskSettingsActivity";
    private SharedPreferences sp;
    private CheckBox cb_auto_clean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        boolean showSystem = sp.getBoolean("showSystem", false);
        setContentView(R.layout.activity_task_setting);
        CheckBox cb_show_system = findViewById(R.id.cb_show_system);
        cb_show_system.setChecked(showSystem);
        cb_show_system.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean("showSystem",isChecked);
                edit.commit();
            }
        });

        //锁屏清理的菜单
        cb_auto_clean = findViewById(R.id.cb_auto_clean);
        cb_auto_clean.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(TaskSettingsActivity.this, AutoCleanService.class);
                Log.i(TAG, "onCheckedChanged: ");
                if (isChecked) {
                    startService(intent);
                } else {
                    stopService(intent);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean serviceRunning = ServiceUtils.isServiceRunning(this, "com.wenjie.mobilesafe.service.AutoCleanService");
        cb_auto_clean.setChecked(serviceRunning);

    }
}