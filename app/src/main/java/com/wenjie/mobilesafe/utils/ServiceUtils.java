package com.wenjie.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class ServiceUtils {
    /**
     * 检查某个服务是否还在运行
     * @param context 上下文
     * @param servicename 服务的名称
     * @return result
     */
    public static boolean isServiceRunning(Context context, String servicename){
        boolean result = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : serviceInfos) {
            String name = info.service.getClassName();
            if (name.equals(servicename)) {
                return true;
            }
        }
        return  result;
    }
}
