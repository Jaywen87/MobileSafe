package com.wenjie.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {
    /**
     * 创建一个blacknumber.db的数据库
     * @param context
     */
    public BlackNumberDBOpenHelper(@Nullable Context context) {
        super(context, "blacknumber.db", null, 1);
    }

    /**
     * 初始化数据库的表结构
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacknumber (_id integer primary key autoincrement,number varchar(20) NOT NULL UNIQUE,mode varchar(2))");
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
