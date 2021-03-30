package com.zlm.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private static Integer Version = 1;
    public MySQLiteOpenHelper(Context context,String name)
    {
        this(context, name, Version);
    }
    public MySQLiteOpenHelper(Context context,String name,int version)
    {
        this(context,name,null,version);
    }
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                              int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table light(id int primary key,name varchar(200),cct varchar(100),dim varchar(100),ev varchar(100))";
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("更新数据库版本为:"+newVersion);
    }

}
