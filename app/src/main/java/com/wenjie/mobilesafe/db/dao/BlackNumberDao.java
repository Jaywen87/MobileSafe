package com.wenjie.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wenjie.mobilesafe.db.BlackNumberDBOpenHelper;
import com.wenjie.mobilesafe.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 黑名单数据库的增删改查业务类
 */
public class BlackNumberDao {
    private BlackNumberDBOpenHelper helper;

    /**
     * 构造方法
     * @param context
     */
    public BlackNumberDao(Context context) {
        helper = new BlackNumberDBOpenHelper(context);
    }

    /**
     * 通过号码查询是否在黑名单中
     * @param number
     * @return
     */
    public boolean find(String number) {
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from blacknumber where number=?", new String[]{number});
        while (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        db.close();
        return  result;
    }

    /**
     * 通过号码查询到模式
     * @param number
     * @return 返回号码的拦截模式，如果不是黑名单好就返回空
     */
    public String findMode(String number) {
        String result = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select mode from blacknumber where number=?", new String[]{number});
        while (cursor.moveToNext()) {
            result = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return  result;
    }

    public List<BlackNumberInfo> findAll() {
//        try {
//            Thread.sleep(3000); //测试用
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        List<BlackNumberInfo> result = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber", null);
        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setNumber((cursor.getString(0)));
            blackNumberInfo.setMode(cursor.getString(1));
            result.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return  result;
    }

    /**
     * 查找部分黑名单
     * @param offset 从那个位置开始获取数据
     * @param maxnuber 一次最多获取多少条
     * @return
     */
    public List<BlackNumberInfo> findPart(int offset, int maxnuber) {
//        try {
//            //Thread.sleep(1); //测试用
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        List<BlackNumberInfo> result = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc limit? offset?", new String[]{String.valueOf(maxnuber),String.valueOf(offset)});
        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setNumber((cursor.getString(0)));
            blackNumberInfo.setMode(cursor.getString(1));
            result.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return  result;
    }

    /**
     * 添加黑名单号码
     * @param number
     * @param mode 拦截模式 1.电话拦截 2.短信拦截 3.全部拦截
     */
    public boolean add(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("number",number);
        contentValues.put("mode",mode);
        long id = db.insert("blacknumber", null, contentValues);
        db.close();
        return id != -1?true:false;
    }

    /**
     * 修改黑名单号码的拦截模式
     * @param number
     * @param mode 拦截模式 1.电话拦截 2.短信拦截 3.全部拦截
     */
    public void update(String number,String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("mode", mode);
        db.update("blacknumber", contentValues, "number=?", new String[]{number});
        db.close();
    }

    /**
     * 删除黑名单号码
     * @param number
     */
    public void delete(String number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("blacknumber", "number=?", new String[]{number});
        db.close();
    }

}
