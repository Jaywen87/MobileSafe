package com.wenjie.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.wenjie.mobilesafe.EnterPwdActivity;
import com.wenjie.mobilesafe.db.dao.ApplockDao;

import java.util.List;

public class WatchDogService extends Service {
    private ActivityManager am;
    private boolean flag;
    private ApplockDao dao;
    private InnerReceiver innerReceiver;
    private String tempStopProtectpackname;
    private ScreenOffReceiver mScreenOffReceiver;
    private DataChangeReceiver dataChangeReceiver;
    private List<String> protectPackName;

    public WatchDogService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    @Override
    public void onCreate() {

        if (innerReceiver == null) {
            innerReceiver = new InnerReceiver();
            registerReceiver(innerReceiver,new IntentFilter("com.wenjie.mobilesafe.tempstop"));
        }
        if (mScreenOffReceiver == null) {
            mScreenOffReceiver = new ScreenOffReceiver();
            registerReceiver(mScreenOffReceiver,new IntentFilter(Intent.ACTION_SCREEN_OFF));
        }
        if (dataChangeReceiver == null) {
            dataChangeReceiver = new DataChangeReceiver();
            registerReceiver(dataChangeReceiver,new IntentFilter("com.wenjie.mobilesafe.applockchange"));
        }

        dao = new ApplockDao(this);
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        flag = true;
        new Thread(){
            @Override
            public void run() {
                protectPackName = dao.findAll();
                while (flag) {
                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
                    String packageName = runningTasks.get(0).topActivity.getPackageName();
                    //if (dao.find(packageName)) {//查询数据太慢了，我们查询内存
                    if(protectPackName.contains(packageName)) {
                        if (packageName.equals(tempStopProtectpackname)) {

                        } else {
                            Intent intent = new Intent(getApplicationContext(), EnterPwdActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("packname",packageName);
                            startActivity(intent);
                        }
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
        super.onCreate();
    }

    private class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            tempStopProtectpackname = intent.getStringExtra("packname");
        }
    }

    private class ScreenOffReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            tempStopProtectpackname = null;
        }
    }

    private class DataChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            protectPackName = dao.findAll();
        }
    }



    @Override
    public void onDestroy() {
        flag = false;
        if (innerReceiver != null) {
            unregisterReceiver(innerReceiver);
            innerReceiver = null;
        }
        if (mScreenOffReceiver != null) {
            unregisterReceiver(mScreenOffReceiver);
            mScreenOffReceiver = null;
        }
        if (dataChangeReceiver != null) {
            unregisterReceiver(dataChangeReceiver);
            dataChangeReceiver = null;
        }
        super.onDestroy();
    }

}
