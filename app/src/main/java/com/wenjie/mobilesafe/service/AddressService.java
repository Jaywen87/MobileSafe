package com.wenjie.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.wenjie.mobilesafe.R;
import com.wenjie.mobilesafe.db.dao.NumberAddressQueryUtils;

public class AddressService extends Service {

    private TelephonyManager tm;
    private MyPhoneStateListener listener;
    private CallOutReceiver callOutReceiver;
    private WindowManager wm;
    private View view;

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
        tv_address.setText(address);

        //设置背景图片 半透明 活力橙 卫士兰 金属灰 苹果绿
        int ids[] = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue,R.drawable.call_locate_gray,R.drawable.call_locate_green};
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        int which = sp.getInt("which", 0);
        tv_address.setBackgroundResource(ids[which]);

        //设置窗体的参数
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;

        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        wm.addView(view,params);

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
