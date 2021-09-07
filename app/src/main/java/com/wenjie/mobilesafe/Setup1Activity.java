package com.wenjie.mobilesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class Setup1Activity extends BaseSetupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

//    /**
//     * 下一步点击事件
//     * @param v
//     */
//    public void next(View v){
//        showNext();
//    }

    public void showNext() {

        Intent intent = new Intent(Setup1Activity.this, Setup2Activity.class);
        startActivity(intent);
        finish();
        //要求在finish或者startActivity的后面执行
        overridePendingTransition(R.anim.tran_in,R.anim.tran_out);
    }

    @Override
    public void showPre() {

    }
}