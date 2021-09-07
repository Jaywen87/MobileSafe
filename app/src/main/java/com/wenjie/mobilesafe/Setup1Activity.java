package com.wenjie.mobilesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class Setup1Activity extends AppCompatActivity {

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);

        //创建一个手势识别器
        gestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(e1.getRawX() -e2.getRawX()>200) {
                    //显示下一个页面 从右向左滑动
                    showNext();
                    return true;

                } else if(e2.getRawX()-e1.getRawX()>200) {
                    //显示上一个页面 从左向右滑动
                    return true;//处理过后返回true
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    /**
     * 下一步点击事件
     * @param v
     */
    public void next(View v){
        showNext();
    }

    public void showNext() {

        Intent intent = new Intent(Setup1Activity.this, Setup2Activity.class);
        startActivity(intent);
        finish();
        //要求在finish或者startActivity的后面执行
        overridePendingTransition(R.anim.tran_in,R.anim.tran_out);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}