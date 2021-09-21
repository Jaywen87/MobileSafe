package com.wenjie.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.wenjie.mobilesafe.R;
import com.wenjie.mobilesafe.db.dao.NumberAddressQueryUtils;

public class AddressService extends Service {

    private static final String TAG = "AddressService";
    private TelephonyManager tm;
    private MyPhoneStateListener listener;
    private CallOutReceiver callOutReceiver;
    private WindowManager wm;
    private View view;
    private WindowManager.LayoutParams params;
    private SharedPreferences sp;

    public AddressService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class  MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    String address = NumberAddressQueryUtils.queryNumber(phoneNumber);
                    //Toast.makeText(getApplicationContext(),address,Toast.LENGTH_LONG).show();
                    myToast(address);
                    break;

                //case TelephonyManager.CALL_STATE_OFFHOOK:
                case TelephonyManager.CALL_STATE_IDLE:
                    if (view != null) {
                        wm.removeView(view);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    public class CallOutReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //这里我们可以拿到拨出的电话号码
            String phone = getResultData();
            String address = NumberAddressQueryUtils.queryNumber(phone);
            //Toast.makeText(context,result,Toast.LENGTH_LONG).show();
            myToast(address);
        }
    }

    public void  myToast(String address ) {
        view = View.inflate(getApplicationContext(), R.layout.address_show,null);
        TextView tv_address = (TextView) view.findViewById(R.id.tv_address);
        view.setOnTouchListener(new View.OnTouchListener() {
            //定义手指的初始位置
            int startX;
            int startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int newX = (int) event.getRawX();
                        int newY = (int) event.getRawY();
                        Log.i(TAG, "新的位置 x:" + newX + " ,Y:" + newY);

                        //计算得到手指移动距离
                        int dx = newX - startX;
                        int dy = newY - startY;
                        Log.i(TAG, "手指偏移量dx:" + dx + ",dy:" + dy);
                        params.x += dx;
                        params.y += dy;
                        //考虑边界问题
                        if(params.x < 0){
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > wm.getDefaultDisplay().getWidth() - view.getWidth()) {
                            params.x = wm.getDefaultDisplay().getWidth() - view.getWidth();
                        }
                        if (params.y > wm.getDefaultDisplay().getHeight() - view.getHeight()) {
                            params.y = wm.getDefaultDisplay().getHeight() - view.getHeight();
                        }

                        wm.updateViewLayout(view,params);

                        //重新初始化手指开始位置
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putInt("lastX", params.x);
                        edit.putInt("lastY", params.y);
                        edit.commit();
                        break;
                    default:
                        break;

                }
                return true;
            }
        });


        //设置背景图片 半透明 活力橙 卫士兰 金属灰 苹果绿
        int ids[] = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue,R.drawable.call_locate_gray,R.drawable.call_locate_green};
        sp = getSharedPreferences("config", MODE_PRIVATE);
        int which = sp.getInt("which", 0);
        tv_address.setBackgroundResource(ids[which]);
        tv_address.setTextColor(Color.WHITE);
        tv_address.setText(address);

        //设置窗体的参数
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //指定窗体与左上角对齐
        params.gravity = Gravity.TOP + Gravity.START;
        //指定窗体距离左边，上边的100像素

        params.x = sp.getInt("lastX",100);
        params.y = sp.getInt("lastY",100);
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE  窗体要能够触摸，需要移除此处
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
//        params.type = WindowManager.LayoutParams.TYPE_TOAST;  此类型不能聚焦，不能点击，不应响应
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE; //需要添加权限
        wm.addView(view, params);

    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 注册一个电话状态的监听器
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyPhoneStateListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        //用代码注册一个拨打电话的广播接收者
        callOutReceiver = new CallOutReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(callOutReceiver,intentFilter);

        //实例化窗体
        wm  = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消监听
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
        listener = null;

        //用代码取消拨打电话的广播接收者
        unregisterReceiver(callOutReceiver);
        callOutReceiver = null;
    }
}
