package com.wenjie.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 短信的工具类
 */
public class SmsUtils {
    private static final String TAG = "SmsUtils";

    /**
     * 备份短信的回调接口
     */
    public interface BackupCallBack {
        /**
         * 备份之前设置最大值
         * @param max
         */
        public void beforeBackup(int max);

        /**
         * 备份过程中，增加进度
         * @param progress
         */
        public void onSmsBackup(int progress);
    }


    /**
     * 短信还原的回调接口
     */
    public interface RestoreCallBack {
        /**
         * 短信还原的设置最大值
         * @param max
         */
        public void beforeRestore(int max);

        /**
         * 短信还原中的进度
         * @param progress
         */
        public void onSmsRestore(int progress);
    }

    /**
     * 备份用户的短信
     * @param context
     * @throws FileNotFoundException
     */
    public static void backupSms(Context context,BackupCallBack callBack) throws Exception {
        ContentResolver resolver = context.getContentResolver();
        File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
        FileOutputStream fos = new FileOutputStream(file);
        //把用户的短信一条一条的对出来，按照一定的格式写到文件里
        XmlSerializer serializer = Xml.newSerializer();
        //初始化生成器
        serializer.setOutput(fos, "utf-8");
        serializer.startDocument("utf-8", true);
        serializer.startTag(null, "smss");
        Uri uri = Uri.parse("content://sms/");
        Cursor cursor = resolver.query(uri, new String[]{"body", "address", "type", "date"}, null, null, null);
        int max = cursor.getCount();
        int progress = 0;
        callBack.beforeBackup(max);
        serializer.attribute(null, "max", max+"");
        while (cursor.moveToNext()) {
            Thread.sleep(500);
            String body = cursor.getString(0);
            String address = cursor.getString(1);
            String type = cursor.getString(2);
            String date = cursor.getString(3);
            serializer.startTag(null, "sms");
            serializer.startTag(null, "body");
            serializer.text(body);
            serializer.endTag(null, "body");
            serializer.startTag(null, "address");
            serializer.text(address);
            serializer.endTag(null, "address");
            serializer.startTag(null, "type");
            serializer.text(type);
            serializer.endTag(null, "type");
            serializer.startTag(null, "date");
            serializer.text(date);
            serializer.endTag(null, "date");
            serializer.endTag(null, "sms");
            progress++;
            callBack.onSmsBackup(progress);
        }
        cursor.close();
        serializer.endTag(null, "smss");
        serializer.endDocument();
        fos.close();
    }

    public static void restoreSms(Context context, boolean flag, RestoreCallBack callBack) throws Exception {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://sms/");
        if (flag) {
            resolver.delete(uri, null, null);
        }
        File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
        FileInputStream fis = new FileInputStream(file);
        XmlPullParser xmlPullParser = Xml.newPullParser();
        xmlPullParser.setInput(fis, "utf-8");
        ContentValues values = new ContentValues();
        int progress = 0;
        int eventType = xmlPullParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name = xmlPullParser.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if ("smss".equals(name)) {
                        String max = xmlPullParser.getAttributeValue(null, "max");
                        callBack.beforeRestore(Integer.parseInt(max));
                    } else if ("body".equals(name)) {
                        String body = xmlPullParser.nextText();
                        values.put("body", body);
                    } else if ("address".equals(name)) {
                        String address = xmlPullParser.nextText();
                        values.put("address", address);
                    } else if ("type".equals(name)) {
                        String smsType  = xmlPullParser.nextText();
                        values.put("type", smsType);
                    } else if ("date".equals(name)) {
                        String date  = xmlPullParser.nextText();
                        values.put("date",date);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (xmlPullParser.getName().equals("sms")) {
                        resolver.insert(uri,values);
                        progress ++;
                        Log.i(TAG, "restoreSms: " +  progress + "短信内容：" + values.toString());
                        callBack.onSmsRestore(progress);
                        values.clear();
                        Log.i(TAG, "restoreSms: " +  progress + "短信内容：" + values.toString());
                        Thread.sleep(500);
                    }
                    break;
            }
            eventType = xmlPullParser.next();
        }
    }

}
