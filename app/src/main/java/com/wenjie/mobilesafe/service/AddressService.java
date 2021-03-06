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
import android.os.SystemClock;
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
    private long [] mHits= new long [2];

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
            //?????????????????????????????????????????????
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
            //???????????????????????????
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
                        Log.i(TAG, "???????????? x:" + newX + " ,Y:" + newY);

                        //??????????????????????????????
                        int dx = newX - startX;
                        int dy = newY - startY;
                        Log.i(TAG, "???????????????dx:" + dx + ",dy:" + dy);
                        params.x += dx;
                        params.y += dy;
                        //??????????????????
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

                        //?????????????????????????????????
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
                return false;
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1,mHits,0,mHits.length-1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= SystemClock.uptimeMillis() - 500) {
                    params.x = wm.getDefaultDisplay().getWidth() /2 - view.getWidth()/2;
                    wm.updateViewLayout(view,params);

                    SharedPreferences.Editor edit = sp.edit();
                    edit.putInt("lastX", params.x);
                    edit.putInt("lastY", params.y);
                    edit.commit();
                }
            }
        });


        //?????????????????? ????????? ????????? ????????? ????????? ?????????
        int ids[] = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue,R.drawable.call_locate_gray,R.drawable.call_locate_green};
        sp = getSharedPreferences("config", MODE_PRIVATE);
        int which = sp.getInt("which", 0);
        tv_address.setBackgroundResource(ids[which]);
        tv_address.setTextColor(Color.WHITE);
        tv_address.setText(address);

        //?????????????????????
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //??????????????????????????????
        params.gravity = Gravity.TOP + Gravity.START;
        //????????????????????????????????????100??????

        params.x = sp.getInt("lastX",100);
        params.y = sp.getInt("lastY",100);
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE  ??????????????????????????????????????????
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
//        params.type = WindowManager.LayoutParams.TYPE_TOAST;  ???????????????????????????????????????????????????
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE; //??????????????????
        wm.addView(view, params);

    }

    @Override
    public void onCreate() {
        super.onCreate();

        // ????????????????????????????????????
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyPhoneStateListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        //???????????????????????????????????????????????????
        callOutReceiver = new CallOutReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(callOutReceiver,intentFilter);

        //???????????????
        wm  = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // ????????????
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
        listener = null;

        //?????????????????????????????????????????????
        unregisterReceiver(callOutReceiver);
        callOutReceiver = null;
    }
}
