package com.wenjie.mobilesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Setup3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
    }

    public void pre(View v) {
        Intent intent = new Intent(Setup3Activity.this, Setup2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in_pre,R.anim.tran_out_pre);
    }

    public void next(View v) {
        Intent intent = new Intent(Setup3Activity.this, Setup4Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in,R.anim.tran_out);
    }
}