package com.wenjie.mobilesafe.engine;


import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.wenjie.mobilesafe.R;
import com.wenjie.mobilesafe.domain.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 提供手机里面的进程信息
 */
public class TaskInfoProvider {
    /**
     * 获取所有进程信息
     * @param context
     * @return
     */
    public static List<TaskInfo> getTaskInfos(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        List<TaskInfo> taskInfos = new ArrayList<>();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            TaskInfo taskInfo = new TaskInfo();
            String packagename = processInfo.processName;
            taskInfo.setPackagename(packagename);
            Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[] {processInfo.pid} );
            long memsize = memoryInfos[0].getTotalPrivateDirty() * 1024;
            taskInfo.setMemsize(memsize);
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(packagename, 0);
                Drawable icon = applicationInfo.loadIcon(pm);
                taskInfo.setIcon(icon);
                String name  =  applicationInfo.loadLabel(pm).toString();
                taskInfo.setName(name);
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    taskInfo.setUserTask(true);
                } else {
                    taskInfo.setUserTask(false);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_default));
                taskInfo.setName(packagename);
            }
            taskInfos.add(taskInfo);
        }
        return  taskInfos;
    }
}
