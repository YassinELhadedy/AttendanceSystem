package com.hady.attendancesystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Hussein Hady on 18/03/2016.
 */
public class AttendanceDataBase extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "MEMBERS";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "NAME";
    private static final String COL_3 = "GRADE";
    private static final String COL_4 = "DEPARTMENT";
    private static final String COL_5 = "ATTENDANCE_TIMES";
    private static final String COL_6 = "DATES";

    public AttendanceDataBase(Context context, String dbname) {
        super(context, dbname, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " + COL_1 + " INTEGER PRIMARY KEY, "
                + COL_2 + " TEXT, " + COL_3 + " TEXT, " + COL_4 + " TEXT, " + COL_5 + " INTEGER, " + COL_6 + " TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public boolean insertData(Integer id, String name, String grade, String department, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, grade);
        contentValues.put(COL_4, department);
        contentValues.put(COL_5, 1);
        contentValues.put(COL_6 , date);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean updateData(Integer id, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Integer att = 0;
        String dates = null;
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE ID = " + id, null);
            while (cursor.moveToNext()) {
                att = cursor.getInt(4);
                dates = cursor.getString(5);
            }
            contentValues.put(COL_5, att+1);
            contentValues.put(COL_6, dates+'\n'+date);
            db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{String.valueOf(id)});
            return true;
        } catch (Exception e) {
            return  false;
        }
    }

    public Integer deleteData(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[] {String.valueOf(id)});
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " +TABLE_NAME, null);
        return cursor;
    }

}