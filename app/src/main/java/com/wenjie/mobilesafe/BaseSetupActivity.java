package com.wenjie.mobilesafe;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseSetupActivity extends AppCompatActivity {
    //1.定义一个手势识别器
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //2.实例化一个手势识别器
        gestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(e1.getRawX() -e2.getRawX()>200) {
                    //显示下一个页面 从右向左滑动
                    showNext();
                    return true;

                } else if(e2.getRawX()-e1.getRawX()>200) {
                    //显示上一个页面 从左向右滑动
                    showPre();
                    return true;//处理过后返回true
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    public abstract void showNext();
    public abstract void showPre();

    /**
     * 下一步
     * @param v
     */
    public void next(View v) {
        showNext();
    }

    /**
     * 上一步
     * @param v
     */
    public void pre(View v) {
        showPre();
    }

    //3.使用手势识别器
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
