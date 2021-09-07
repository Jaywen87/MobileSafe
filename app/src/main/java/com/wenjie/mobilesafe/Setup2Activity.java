package com.wenjie.mobilesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wenjie.mobilesafe.ui.SettingItemView;

public class Setup2Activity extends AppCompatActivity {

    private SharedPreferences sp;
    private SettingItemView siv_bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        sp = getSharedPreferences("config", MODE_PRIVATE);
        siv_bind = findViewById(R.id.siv_bind);
        boolean isBind = sp.getBoolean("bindSIM", false);
        siv_bind.setChecked(isBind);
        siv_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = sp.edit();
                if(siv_bind.isChecked()) {
                    siv_bind.setChecked(false);
                    edit.putBoolean("bindSIM",false);
                } else {
                    siv_bind.setChecked(true);
                    edit.putBoolean("bindSIM",true);
                }
                edit.commit();
            }
        });



    }

    public void pre(View v) {
        Intent intent = new Intent(Setup2Activity.this, Setup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in_pre,R.anim.tran_out_pre);
    }

    public void next(View v) {
        Intent intent = new Intent(Setup2Activity.this, Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in,R.anim.tran_out);
    }
}