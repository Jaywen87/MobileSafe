package com.wenjie.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.android.internal.telephony.ITelephony;
import com.wenjie.mobilesafe.db.dao.BlackNumberDao;

import java.lang.reflect.Method;


public class CallSmsSafeService extends Service {
    private static final String TAG = "CallSmsSafeService" ;
    private InnerSmsReceiver receiver;
    private BlackNumberDao dao;
    private TelephonyManager telephonyManager;
    private MyListener myListener;

    public CallSmsSafeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class InnerSmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: " + "接收到广播");
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : objs) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
                String sender = smsMessage.getOriginatingAddress();
                String mode = dao.findMode(sender);
                if ("2".equals(mode) || "3".equals(mode)) {
                    Log.i(TAG, "onReceive: sender:" + sender + ",mode:" + mode);
                    abortBroadcast();
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //监听电话状态
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        myListener = new MyListener();
        telephonyManager.listen(myListener,PhoneStateListener.LISTEN_CALL_STATE );

        //注册短信广播接收者
        receiver = new InnerSmsReceiver();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY );
        registerReceiver(receiver,intentFilter);
        dao = new BlackNumberDao(getApplicationContext());
        
    }

    private class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    String mode = dao.findMode(phoneNumber);
                    if ("1".equals(mode) || "3".equals(mode)) {
                        Log.i(TAG, "onCallStateChanged: 挂断电话");

                        //deleteCallLog(phoneNumber);
                        //先注册一个通话记录的内容观察者
                        Uri uri = Uri.parse("content://call_log/calls");
                        CallLogObserver callLogObserver = new CallLogObserver(new Handler(), phoneNumber);
                        getContentResolver().registerContentObserver(uri,true,callLogObserver);

                        //挂断电话之后就通话记录的数据库就会增加，这个时候内容观察者就可以观察到数据库的改变
                        endCall();


                    }
                    break;
            }
        }
    }

    private class CallLogObserver extends ContentObserver {
        private String incomingNumber;
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public CallLogObserver(Handler handler,String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            getContentResolver().unregisterContentObserver(this);
            deleteCallLog(incomingNumber);

        }
    }


    private void deleteCallLog(String phoneNumber) {
        ContentResolver resolver = getContentResolver();
        Uri uri = Uri.parse("content://call_log/calls");
        resolver.delete(uri, "number=?", new String[]{phoneNumber});
    }

    public void endCall() {
        try {
            Class<?> clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
            Method method = clazz.getDeclaredMethod("getService", String.class);
            IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            ITelephony.Stub.asInterface(ibinder).endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        telephonyManager.listen(myListener,PhoneStateListener.LISTEN_NONE );
    }

}
