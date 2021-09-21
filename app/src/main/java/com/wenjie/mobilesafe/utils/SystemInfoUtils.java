package com.wenjie.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.net.RouteInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;

public class SystemInfoUtils {
    /**
     * 获取正在运行的进程数量
     * @return
     */
    public static int getRuuningProcessCount(Context context) {
        //PackageManager
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();

        return infos.size();
    }

    /**
     * 获取手机的可用内存
     * @param context
     * @return
     */
    public static long getAvailMem(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }

    /**
     * 获取手机的所有内存
     * @param context
     * @return
     */
    public static long getTotalMem(Context context) {
       /* ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.totalMem;*/

        File file = new File("/proc/meminfo");
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br  = new BufferedReader(new InputStreamReader(fis));
            String  line  = br.readLine();
            StringBuffer sb = new StringBuffer();
            for (char c : line.toCharArray()) {
                if ((c >= '0') && (c <= '9')) {
                    sb.append(c);
                }
            }
            return  Long.parseLong(sb.toString()) * 1024;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }
}
