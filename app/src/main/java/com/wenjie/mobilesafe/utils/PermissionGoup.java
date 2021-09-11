package com.wenjie.mobilesafe.utils;

public class PermissionGoup {

    //日历
    public static final String[] CALENDAR = {
            "android.permission.READ_CALENDAR",
            "android.permission.WRITE_CALENDAR"};
    //相机
    public static final String[] CAMERA = {"android.permission.CAMERA"};
    //通讯录
    public static final String[] CONTACTS = {
            "android.permission.READ_CONTACTS",
           /* "android.permission.WRITE_CONTACTS",
            "android.permission.GET_ACCOUNTS"*/};
    //定位
    public static final String[] LOCATION = {
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION"};
    //麦克风
    public static final String[] RECORD_AUDIO = {"android.permission.RECORD_AUDIO"};
    //电话
    public static final String[] PHONE = {
            "android.permission.READ_PHONE_STATE",
            /*"android.permission.CALL_PHONE",
            "android.permission.READ_CALL_LOG",
            "android.permission.WRITE_CALL_LOG",
            "com.android.voicemail.permission.ADD_VOICEMAIL",
            "android.permission.USE_SIP",
            "android.permission.PROCESS_OUTGOING_CALLS"*/};
    //传感器
    public static final String[] SENSORS = {"android.permission.BODY_SENSORS"};
    //短信
    public static final String[] SMS = {
           /* "android.permission.READ_SMS",*/
            "android.permission.SEND_SMS",
            "android.permission.RECEIVE_SMS",
            /*"android.permission.RECEIVE_WAP_PUSH",
            "android.permission.RECEIVE_MMS"*/};
    //存储
    public static final String[] STORAGE = {
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"};

    public static final String[] SMS_AND_LOCATION = {
            "android.permission.SEND_SMS",
            "android.permission.RECEIVE_SMS",
            "android.permission.ACCESS_FINE_LOCATION"};


    /**
     * 对应的权限组请求码
     */
    public static final int CALENDAR_CODE = 1;
    public static final int CAMERA_CODE = 2;
    public static final int CONTACTS_CODE = 3;
    public static final int LOCATION_CODE = 4;
    public static final int RECORD_AUDIO_CODE = 5;
    public static final int PHONE_CODE = 6;
    public static final int SENSORS_CODE = 7;
    public static final int SMS_CODE = 8;
    public static final int STORAGE_CODE = 9;
    public static final int SMS_AND_LOCATION_CODE = 10;

}
