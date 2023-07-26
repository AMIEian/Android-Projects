package com.vsi.smart.dairy1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Databasehelperlogin extends SQLiteOpenHelper
{
    public static final String TABLE_NAME = "Userdetailslogin";
    public static final String _ID = "_ID";
    public static final String username = "username";
    public static final String password = "password";
    public static final String societyCode = "societyCode";
    public static final String pcode = "pcode";
    public static final String lbtno = "lbtno";
    public static final String stype = "stype";
    public static final String ads = "ads";
    public static final String SocietyName = "SocietyName";
    public static final String pname = "pname";
    public static final String accessrigts = "accessrigts";
    public static final String deviceid = "deviceid";
    public static final String loginid = "loginid";

    // Database Information
    static final String DB_NAME = "User_Details.DB";
    // database version
    static final int DB_VERSION = 1;
    // Creating table query
    private static final String CREATE_TABLE  = "create table if not exists " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + username + " TEXT, " + password + " TEXT, " + societyCode +
            " TEXT, " + pcode + " TEXT, " + lbtno + " TEXT, " + stype + " TEXT, " + ads
            + " TEXT, " + SocietyName + " TEXT, " + pname + " TEXT, " + accessrigts + " TEXT, " +
            deviceid + " TEXT, " + loginid + " TEXT);";

    public Databasehelperlogin(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public boolean isTableExists(String tableName, boolean openDb) {
        SQLiteDatabase db = null;
        if(openDb) {
            if(db == null || !db.isOpen())
            {
                db = getReadableDatabase();
            }
            if(!db.isReadOnly())
            {
                db.close();
                db = getReadableDatabase();
            }
        }

        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+TABLE_NAME+"'",
                null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME
//               + " WHERE " + currdate+ " = '" + currdate1+"'"+" AND "+userid+ " = '" + userid1+"'"
                ,null);
        return res;
    }

    public boolean insertData(String username1, String password1, String societyCode1,
                              String pcode1, String lbtno1, String stype1, String ads1, String SocietyName1,
                              String pname1, String accessrigts1, String deviceid1, String loginid1) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(username,username1);
        contentValues.put(password,password1);
        contentValues.put(societyCode,societyCode1);
        contentValues.put(pcode,pcode1);
        contentValues.put(lbtno,lbtno1);
        contentValues.put(stype,stype1);
        contentValues.put(ads,ads1);
        contentValues.put(SocietyName,SocietyName1);
        contentValues.put(pname,pname1);
        contentValues.put(accessrigts,accessrigts1);
        contentValues.put(deviceid,deviceid1);
        contentValues.put(loginid,loginid1);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean updateData(String id,String username1, String password1, String societyCode1,
                              String pcode1, String lbtno1, String stype1, String ads1, String SocietyName1,
                              String pname1, String accessrigts1, String deviceid1, String loginid1)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(_ID,id);
        contentValues.put(username,username1);
        contentValues.put(password,password1);
        contentValues.put(societyCode,societyCode1);
        contentValues.put(pcode,pcode1);
        contentValues.put(lbtno,lbtno1);
        contentValues.put(stype,stype1);
        contentValues.put(ads,ads1);
        contentValues.put(SocietyName,SocietyName1);
        contentValues.put(pname,pname1);
        contentValues.put(accessrigts,accessrigts1);
        contentValues.put(deviceid,deviceid1);
        contentValues.put(loginid,loginid1);
        db.update(TABLE_NAME, contentValues, "_ID = ?",new String[] { id });
        return true;
    }

}
