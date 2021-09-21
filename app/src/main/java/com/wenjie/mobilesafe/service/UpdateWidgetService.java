package com.wenjie.mobilesafe.service;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.wenjie.mobilesafe.R;
import com.wenjie.mobilesafe.receiver.MyWidget;
import com.wenjie.mobilesafe.utils.SystemInfoUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateWidgetService extends Service {

    private static final String TAG = "UpdateWidgetService";
    private AppWidgetManager awm;
    private Timer timer;
    private TimerTask timerTask;
    private ScreenOffReceiver mScreenOffReceiver;
    private ScreenOnReceiver mScreenOnReceiver;

    public UpdateWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        awm = AppWidgetManager.getInstance(this);
        mScreenOffReceiver = new ScreenOffReceiver();
        mScreenOnReceiver = new ScreenOnReceiver();
        registerReceiver(mScreenOnReceiver,new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(mScreenOffReceiver,new IntentFilter(Intent.ACTION_SCREEN_OFF));
        startTimer();
    }

    public class ScreenOffReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: 屏幕锁屏了" );
            stopTimer();
        }
    }

    public class ScreenOnReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: 屏幕解锁了" );
            startTimer();
        }
    }

    private void startTimer() {
        if (timer == null & timerTask == null) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    ComponentName provider = new ComponentName(UpdateWidgetService.this, MyWidget.class);
                    RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
                    remoteViews.setTextViewText(R.id.process_count,"正在运行的软件："+ SystemInfoUtils.getRuuningProcessCount(getApplicationContext()) +"个");
                    remoteViews.setTextViewText(R.id.process_memory,"可用内存：" + Formatter.formatFileSize(getApplicationContext(),SystemInfoUtils.getAvailMem(getApplicationContext())));

                    Intent intent = new Intent();
                    intent.setAction("com.wenjie.mobilesafe.killall");
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    remoteViews.setOnClickPendingIntent(R.id.btn_clear,pendingIntent);
                    awm.updateAppWidget(provider,remoteViews);
                }
            };
            timer.schedule(timerTask,0,3000);
        }
    }

    @Override
    public void onDestroy() {
        stopTimer();
        unregisterReceiver(mScreenOffReceiver);
        unregisterReceiver(mScreenOnReceiver);
        mScreenOffReceiver = null;
        mScreenOnReceiver = null;
        super.onDestroy();
    }

    private void stopTimer() {
        if(timer!= null && timerTask != null) {
            timer.cancel();
            timerTask.cancel();
            timer = null;
            timerTask = null;
        }
    }
}
