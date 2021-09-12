package com.wenjie.mobilesafe;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.EditText;
import android.widget.TextView;


import com.wenjie.mobilesafe.db.dao.NumberAddressQueryUtils;

public class NumberAddressQueryActivity extends AppCompatActivity {

    private static final String TAG = "NumberAddQueryActivity";
    private EditText et_phone;
    private TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_address_query);

        tv_result = findViewById(R.id.tv_result);
        et_phone = findViewById(R.id.et_phone);
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s!=null && s.length() >= 3) {
                    //查询数据库，并且显示结果
                    String address = NumberAddressQueryUtils.queryNumber(s.toString());
                    tv_result.setText(address);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void numberAddressQuery(View view) {
        String phone = et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
            findViewById(R.id.et_phone).startAnimation(animation);
            //Toast.makeText(this, "号码为空", Toast.LENGTH_SHORT).show();
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(2000);
            long[] pattern = {100,200,100,200,50,50};
            vibrator.vibrate(pattern, 1);
            return;
        } else {
            //去数据库查询号码归属地
            Log.i(TAG, "numberAddressQuery: 你要查询的电话号码=" + phone);
            String result = NumberAddressQueryUtils.queryNumber(phone);
            tv_result.setText(result);
        }
    }
}