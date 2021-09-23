package com.wenjie.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ApplockDBOpenHelper extends SQLiteOpenHelper {

    /**
     * 数据库的构造方法，创建一个名叫applock.db的数据库
     * @param context
     */
    public ApplockDBOpenHelper(@Nullable Context context) {
        super(context, "applock.db", null, 1);
    }

    /**
     * 初始化applock表
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table applock (_id integer primary key autoincrement,packname varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
