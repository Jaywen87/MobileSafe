package com.wenjie.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.wenjie.mobilesafe.R;
import com.wenjie.mobilesafe.service.GPSService;

import java.util.Objects;


public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        Object[] objs = (Object[]) Objects.requireNonNull(intent.getExtras()).get("pdus");
        String foramt = intent.getStringExtra("foramt");
        for(Object b : Objects.requireNonNull(objs)) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) b, foramt);
            String sender = sms.getOriginatingAddress();
            String messageBody = sms.getMessageBody();

            String safenumber = sp.getString("safenumber", "");
            if(safenumber != null && sender != null &&sender.contains(safenumber)) {
                DevicePolicyManager dpm;
                if("#*location*#".equals(messageBody)) {
                    Intent intent1 = new Intent(context, GPSService.class);
                    context.startService(intent1);
                    String lastlaction = sp.getString("lastlaction", null);
                    if (TextUtils.isEmpty(lastlaction)) {
                        SmsManager.getDefault().sendTextMessage(sender,null,"get location...",null,null);
                    } else {
                        SmsManager.getDefault().sendTextMessage(sender,null,lastlaction,null,null);
                    }
                    abortBroadcast();

                } else if("#*alarm*#".equals(messageBody)){
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setVolume(1.0f,1.0f);
                    mediaPlayer.start();
                    abortBroadcast();

                } else if("#*wipedata*#".equals(messageBody)){
                    dpm =  (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    dpm.wipeData(0);
                    abortBroadcast();

                    abortBroadcast();
                } else if("#*locksreen*#".equals(messageBody)){
                    dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    dpm.lockNow();
                    abortBroadcast();
                }
            }
        }


    }
}
