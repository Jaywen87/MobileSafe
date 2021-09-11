package com.wenjie.mobilesafe.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class GPSService extends Service {

    private LocationManager lm;
    private MyLocationListener myLocationListener;

    public GPSService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        //注册监听位置服务
        myLocationListener = new MyLocationListener();

        // 给位置提供者设置条件
        Criteria criteria = new Criteria();

        //设置精确度 ACCURACY_FINE 好  > ACCURACY_COARSE 粗略的
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        //设置参数细化
//        criteria.setAltitudeRequired(false);//false 不要求海拔信息
//        criteria.setHorizontalAccuracy(Criteria.ACCURACY_LOW);//水平的 ACCURACY_HIGH高(100m之内）  > ACCURACY_MEDIUM 中等（100m-500m）  > ACCURACY_LOW 低（>500m）
//        criteria.setVerticalAccuracy(Criteria.ACCURACY_LOW);//垂直的 水平的 ACCURACY_HIGH高(100m之内）  > ACCURACY_MEDIUM 中等（100m-500m）  > ACCURACY_LOW 低（>500m）
//        criteria.setBearingRequired(false);// 不要求方位信息
//        criteria.setCostAllowed(false);//是否允许付费
//        criteria.setPowerRequirement(Criteria.POWER_LOW);//对电量的要求

        String bestProvider = lm.getBestProvider(criteria, true);
        lm.requestLocationUpdates(bestProvider, 0, 0, myLocationListener);

    }

    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(@NonNull Location location) {
           String longitude = "j:" + location.getLongitude() + "\n";
           String latitude = "w:" + location.getLatitude() + "\n";
           String accuracy = "a" + location.getAccuracy() + "\n";

            SharedPreferences sp = getSharedPreferences("config",MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            edit.putString("lastlocation",longitude +  latitude + accuracy);
            edit.commit();


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lm.removeUpdates(myLocationListener);
        myLocationListener = null;
    }
}
