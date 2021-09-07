package com.wenjie.mobilesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class LostFindActivity extends AppCompatActivity {


    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        boolean configed = sp.getBoolean("configed", false);
        if(configed) {
            setContentView(R.layout.activity_lost_find);
        } else {
            // 进入设置向导页面
            Intent intent = new Intent(LostFindActivity.this, Setup1Activity.class);
            startActivity(intent);
            //关闭当前页面
            finish();
        }

    }

    public void reEnterSetup(View view) {
        // 进入设置向导页面
        Intent intent = new Intent(LostFindActivity.this, Setup1Activity.class);
        startActivity(intent);
        //关闭当前页面
        finish();
    }
}