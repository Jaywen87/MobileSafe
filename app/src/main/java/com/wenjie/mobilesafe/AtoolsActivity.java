package com.wenjie.mobilesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AtoolsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }

    /**
     * 点击事件，进入号码归属地查询
     * @param view
     */
    public void numberQuery(View view) {
        Intent intent = new Intent(AtoolsActivity.this, NumberAddressQueryActivity.class);
        startActivity(intent);
    }
}