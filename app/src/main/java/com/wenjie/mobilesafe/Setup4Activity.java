package com.wenjie.mobilesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class Setup4Activity extends AppCompatActivity {

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        sp = getSharedPreferences("config",MODE_PRIVATE);

    }

    public void pre(View v) {
        Intent intent = new Intent(Setup4Activity.this, Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in_pre,R.anim.tran_out_pre);
    }

    public void next(View v) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("configed",true);
        edit.commit();
        Intent intent = new Intent(Setup4Activity.this, LostFindActivity.class);
        startActivity(intent);
        finish();
    }
}