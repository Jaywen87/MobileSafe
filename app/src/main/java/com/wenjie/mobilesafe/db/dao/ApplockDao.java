package com.wenjie.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wenjie.mobilesafe.db.ApplockDBOpenHelp;

import java.util.ArrayList;
import java.util.List;

/**
 * 程序锁dao
 */
public class ApplockDao {
    private ApplockDBOpenHelp helper;
    private Context context;


    /**
     * 构造方法
     * @param context
     */
    public ApplockDao(Context context) {
         helper = new ApplockDBOpenHelp(context);
         this.context = context;
    }

    public void add(String packname) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packname", packname);
        db.insert("applock", null, values);
        db.close();
        //发送广播，提醒数据改变
        Intent intent = new Intent();
        intent.setAction("com.wenjie.mobilesafe.applockchange");
        context.sendBroadcast(intent);
    }
    public void delete(String packname) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("applock", "packname=?", new String[]{packname});
        db.close();
        //发送广播，提醒数据改变
        Intent intent = new Intent();
        intent.setAction("com.wenjie.mobilesafe.applockchange");
        context.sendBroadcast(intent);
    }

    public boolean find(String packname) {
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("applock", new String[]{"packname"}, "packname=?", new String[]{packname}, null, null, null);
        while (cursor.moveToNext()) {
            result = true;
            break;
        }
        cursor.close();
        db.close();
        return result;
    }

    public List<String> findAll() {
        List<String> protectPackName = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("applock", new String[]{"packname"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            protectPackName.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return protectPackName;
    }
}
