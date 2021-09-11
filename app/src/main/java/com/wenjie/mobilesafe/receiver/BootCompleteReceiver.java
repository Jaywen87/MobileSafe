package com.wenjie.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class BootCompleteReceiver extends BroadcastReceiver {

    private TelephonyManager tm;
    private final String TAG = "BootCompleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        boolean protecting = sp.getBoolean("protecting", false);
        if(protecting) {
            tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            String simSerialNumber = tm.getSimSerialNumber();

            String sim = sp.getString("sim", "");
            if(sim.equals(simSerialNumber)) {
                Log.i(TAG, "onReceive: " + "SIM没有改变");
            } else {
                Toast.makeText(context,"SIM已经变更",Toast.LENGTH_LONG).show();
                SmsManager.getDefault().sendTextMessage(sp.getString("safeNumber",""),null,"Sim changed ...",null,null);
            }

        }
    }
}
