package com.example.onwaier.game2048pic;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Onwaier on 2018/4/13.
 */

public class MyDBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Onwaiers.db";
    public static final String TABLE_NAME = "Rank";
    private SQLiteDatabase db;
    private Cursor cursor;

    public MyDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create table Orders(Id integer primary key, CustomName text, OrderPrice integer, Country text);
        String sql = "create table if not exists " + TABLE_NAME + " (Rank integer, Id text primary key, Score integer, LastTime text)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }


    public String getDbName(){
        return DB_NAME;
    }

    public String getTableName(){
        return TABLE_NAME;
    }

    public Boolean isOverRank(int rank, int score, String lastTime){
        int cnt = 1;
        db = this.getReadableDatabase();
        cursor = db.rawQuery("select * from " + MyDBHelper.TABLE_NAME + " where Rank = " +  rank + " order by Score desc, LastTime", null);
        if (score != 0 && cursor.getCount() < 10) {
            return true;
        }
        else{
            while (cursor.moveToNext()) {
                if(cnt <= 10 && (score > cursor.getInt(2) || (score == cursor.getInt(2) && lastTime.compareTo(cursor.getString(3)) < 0))){
                    return true;
                }
                ++cnt;
                if(cnt > 10){
                    return false;
                }

            }
        }
        return false;
    }

}
