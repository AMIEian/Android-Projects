package com.vsi.smart.dairy1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Databasehelper1 extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "MilkEntry";
    // Table columns
    public static final String _ID = "_ID";
    public static final String code = "code";
    public static final String lit = "lit";
    public static final String fat = "fat";
    public static final String snf = "snf";
    public static final String clr = "clr";
    public static final String rate = "rate";
    public static final String amount = "amount";
    public static final String datefrom = "datefrom";
    public static final String flagtime = "flagtime";
    public static final String flaganimal = "flaganimal";
    public static final String puramount = "puramount";
    public static final String flagsaleorpur = "flagsaleorpur";
    public static final String companyid = "companyid";
    public static final String userid = "userid";
    public static final String orderid = "orderid";

    // Database Information
    static final String DB_NAME = "Rajhans_Activities.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + code + " TEXT, " + lit + " TEXT, " + fat +
            " TEXT, " + snf + " TEXT, " + clr + " TEXT, " + rate + " TEXT, " + amount
            + " TEXT, " + datefrom + " TEXT, " + flagtime + " TEXT, " + flaganimal + " TEXT, " +
            puramount + " TEXT, " + flagsaleorpur + " TEXT, " + companyid + " TEXT, " + userid + " TEXT, " + orderid + " TEXT);";

    public Databasehelper1(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getAllData(String currdate1, String userid1) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME
                        + " WHERE " + datefrom+ " = '" + currdate1+"'"+" AND "+userid+ " = '" + userid1+"'"
                ,null);
        return res;
    }

    public Cursor getAllData1(String userid1) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME
                        + " WHERE "
                        //   + currdate+ " = '" + currdate1+"'"+" AND "
                        +userid+ " = '" + userid1+"'"
                ,null);
        return res;
    }

    public boolean insertData(String code1,String lit1, String fat1,String snf1,String clr1,
                              String rate1,String amount1, String datefrom1,String flagtime1,String flaganimal1,
                              String puramount1,String flagsaleorpur1, String userid1,String companyid1,String orderid1)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(code,code1);
        contentValues.put(lit,lit1);
        contentValues.put(fat,fat1);
        contentValues.put(snf,snf1);
        contentValues.put(clr,clr1);
        contentValues.put(rate,rate1);
        contentValues.put(amount,amount1);
        contentValues.put(datefrom,datefrom1);
        contentValues.put(flagtime,flagtime1);
        contentValues.put(flaganimal,flaganimal1);
        contentValues.put(puramount,puramount1);
        contentValues.put(flagsaleorpur,flagsaleorpur1);
        contentValues.put(userid,userid1);
        contentValues.put(companyid,companyid1);
        contentValues.put(orderid,orderid1);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

//    public boolean updateData(String id,String VisitPlace1,String Name1,String photo1,String Workdetails1,
//                              String Results1) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(_ID,id);
//        contentValues.put(VisitPlace,VisitPlace1);
//        contentValues.put(Name,Name1);
//        contentValues.put(photo,photo1);
//        contentValues.put(Workdetails,Workdetails1);
//        contentValues.put(Results,Results1);
//        db.update(TABLE_NAME, contentValues, "_ID = ?",new String[] { id });
//        return true;
//    }
//
    public boolean updateid(String id,String orderid1) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(_ID,id);
        contentValues.put(orderid,orderid1);
        db.update(TABLE_NAME, contentValues, "_ID = ?",new String[] { id });
        return true;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "_ID = ?",new String[] {id});
    }
}

