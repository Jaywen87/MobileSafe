package com.wenjie.mobilesafe.utils;

import android.os.Message;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    /**
     * md5加密方法
     * @param password
     * @return
     */
    public static String md5Password(String password) {
        try {
            //得到一个信息摘要器
            MessageDigest md5 = MessageDigest.getInstance("md5");
            byte[] reslut = md5.digest(password.getBytes());
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : reslut ) {
                int number = b & 0xff; //加盐
                String str = Integer.toHexString(number);
                if(str.length() == 1) {
                    stringBuffer.append(0);
                }
                stringBuffer.append(str);
            }
            return stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
