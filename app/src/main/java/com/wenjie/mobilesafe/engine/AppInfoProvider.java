package com.wenjie.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.wenjie.mobilesafe.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 业务方法，提供手机里面安装的所有应用信息
 */
public class AppInfoProvider {
    public static List<AppInfo> getAppInfos(Context context){
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        List<AppInfo> appInfos = new ArrayList<>();
        for(PackageInfo packageInfo : packageInfos) {
            String packageName = packageInfo.packageName;
            Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
            String name = packageInfo.applicationInfo.loadLabel(pm).toString();
            int flags = packageInfo.applicationInfo.flags;//应用程序信息标签
            AppInfo appInfo = new AppInfo();
            appInfo.setName(name);
            appInfo.setPacakagename(packageName);
            appInfo.setIcon(icon);
            if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                appInfo.setUserApp(false);
            } else {
                appInfo.setUserApp(true);
            }
            if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) !=0) {
                appInfo.setInRom(false);
            } else {
                appInfo.setInRom(true);
            }
            appInfos.add(appInfo);
        }
        return  appInfos;
    }
}
