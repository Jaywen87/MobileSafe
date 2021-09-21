package com.wenjie.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.wenjie.mobilesafe.domain.TaskInfo;
import com.wenjie.mobilesafe.engine.TaskInfoProvider;

import java.util.List;

public class AutoCleanService extends Service {

    private static final String TAG = "AutoCleanService";
    private ScreenOffReceiver mScreenOffReceiver;
    private ActivityManager am;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mScreenOffReceiver == null) {
            mScreenOffReceiver = new ScreenOffReceiver();
        }
        Log.i(TAG, "onCreate: ");
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        registerReceiver(mScreenOffReceiver,new IntentFilter("Intent.ACTION_SCREEN_OFF"));
    }

    @Override
    public void onDestroy() {
        if (mScreenOffReceiver != null) {
            unregisterReceiver(mScreenOffReceiver);
            mScreenOffReceiver = null;
        }
        super.onDestroy();
    }

    public class ScreenOffReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: 锁屏了" );
            List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
                am.killBackgroundProcesses(processInfo.processName);
            }
        }
    }
}
