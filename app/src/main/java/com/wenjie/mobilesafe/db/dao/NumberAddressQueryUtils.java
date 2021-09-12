package com.wenjie.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberAddressQueryUtils {
    private  static  String path = "data/data/com.wenjie.mobilesafe/files/address.db";
    /**
     * 传一个号码进来，返回一个归属地
     * @param number
     * @return
     */
    public static String queryNumber(String number){
        String address  = number;
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        //正则表达式
        if (number.matches("^1[3456789]\\d{9}$")) {
            //手机号码
            Cursor cursor = sqLiteDatabase.rawQuery(
                    "select location from data2 where id = (select outkey from data1 where id = ?)",
                    new String[]{number.substring(0, 7)});
            while (cursor.moveToNext()) {
                String location = cursor.getString(0);
                address = location;
            }
            cursor.close();
        } else {
            //其他号码
            switch (number.length()) {
                case 3:
                    address = "匪警号码";
                    break;
                case 4:
                    address = "模拟器号码";
                    break;
                case 5://10086
                    address = "客服号码";
                    break;
                case 6:
                    break;
                case 7:
                    address = "本地号码";
                    break;
                case 8:
                    address = "本地号码";
                    break;
                default:
                    if (number.length() > 10 && number.startsWith("0")) {

                        //010-5267206
                        Cursor cursor = sqLiteDatabase.rawQuery("select location from data2 where area = ?", new String[]{number.substring(1, 3)});
                        while (cursor.moveToNext()) {
                           String  location = cursor.getString(0);
                           address = location.substring(0,location.length()-2);
                        }
                        cursor.close();

                        //0718-5267206
                        cursor = sqLiteDatabase.rawQuery("select location from data2 where area = ?" ,new String[]{number.substring(1,4)});
                        while (cursor.moveToNext()) {
                            String location  = cursor.getString(0);
                            address = location.substring(0,location.length()-2);
                        }
                        cursor.close();

                    }
                    break;
            }
        }
        sqLiteDatabase.close();
        return address;
    }
}
