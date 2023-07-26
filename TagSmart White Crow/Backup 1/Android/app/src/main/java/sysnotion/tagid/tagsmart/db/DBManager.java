package sysnotion.tagid.tagsmart.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import sysnotion.tagid.tagsmart.model.EPCBarcodeInventory;
import sysnotion.tagid.tagsmart.model.EPCBarcodeInwarding;
import sysnotion.tagid.tagsmart.model.EPCInventory;
import sysnotion.tagid.tagsmart.model.Inward;
import sysnotion.tagid.tagsmart.utils.Constants;

public class DBManager {
    private DataBaseHelper dbHelper;
    private SQLiteDatabase database;
    private Context context;
    SharedPreferences pref;

    public DBManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(Constants.PREFS_NAME, context.MODE_PRIVATE);

    }

    public DBManager open() throws SQLException {

        dbHelper = new DataBaseHelper(context);
        database = dbHelper.getWritableDatabase();
        //  dbHelper.exportDB();
        return this;
    }


    public void close() {
        if(dbHelper!= null) {
            dbHelper.close();
        }
    }

    //insert Data in session details
    public void insertSessionDetails(String dateStr, String countStr) {

        try {


            ContentValues values = new ContentValues();
            values.put(dbHelper.SESSION_DATE, dateStr);
            values.put(dbHelper.SESSION_COUNT, countStr);
            database.insert(dbHelper.TABLE_SESSION_DETAILS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Update Data in session details
    public void updateSessionDetails(String dateStr, String countStr, String index) {

        try {

            ContentValues values = new ContentValues();
            values.put(dbHelper.SESSION_DATE, dateStr);
            values.put(dbHelper.SESSION_COUNT, countStr);
            database.update(dbHelper.TABLE_SESSION_DETAILS, values, dbHelper.SESSION_ID + "=" + index, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Get Data from session details
    public JSONObject getSessionDetailsByDate(String dateStr)
    {
        String query = null;
        Cursor cursor = null;
        JSONObject sessionJSONObject= new JSONObject();
        try {

            query = "SELECT  * FROM " + dbHelper.TABLE_SESSION_DETAILS+ " where " + dbHelper.SESSION_DATE + " = '" + dateStr + "';";
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                        sessionJSONObject.put(dbHelper.SESSION_ID, cursor.getInt(0));
                        sessionJSONObject.put(dbHelper.SESSION_DATE, cursor.getString(1));
                        sessionJSONObject.put(dbHelper.SESSION_COUNT, cursor.getString(2));

                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return sessionJSONObject;
    }

    //Get Data from session details and insert current date and zero count
    public JSONObject getSessionDetailsByDateWithInsert(String dateStr)
    {
        String query = null;
        Cursor cursor = null;
        JSONObject sessionJSONObject= new JSONObject();
        try {

            query = "SELECT  * FROM " + dbHelper.TABLE_SESSION_DETAILS+ " where " + dbHelper.SESSION_DATE + " = '" + dateStr + "';";
            cursor = database.rawQuery(query, null);
            if (!(cursor.moveToFirst()) || cursor.getCount() == 0) {
                Date date = new Date();
                String modifiedDate= new SimpleDateFormat("yyyy-MM-dd").format(date);

                //insert if no date found then enter current date and count 0;
                insertSessionDetails(modifiedDate, "0");

                // call getSessionDetailsByDate
                sessionJSONObject = getSessionDetailsByDate(modifiedDate);
            }
            else
            {
                if (cursor.moveToFirst()) {
                    do {
                        sessionJSONObject.put(dbHelper.SESSION_ID, cursor.getInt(0));
                        sessionJSONObject.put(dbHelper.SESSION_DATE, cursor.getString(1));
                        sessionJSONObject.put(dbHelper.SESSION_COUNT, cursor.getString(2));

                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return sessionJSONObject;
    }

    //delete Data in Stock details checksum
    public void deleteStockDetailsChecksum(String EPC) {
        try {

            database.delete(dbHelper.TABLE_STOCK_DETAILS_CHECKSUM, dbHelper.SDC_EPC + "= ?"  , new String[] { EPC });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //delete Data in Stock details checksum
    public void turcateStockDetailsChecksum()
    {
        try {

            database.delete(dbHelper.TABLE_STOCK_DETAILS_CHECKSUM, null, null);;
            String query ="delete from sqlite_sequence where name='"+dbHelper.TABLE_STOCK_DETAILS_CHECKSUM+"' ;";
            database.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //insert Data in Stock details checksum
    public void insertStockDetailsChecksum(String EPC) {
        try {

            ContentValues values = new ContentValues();
            values.put(dbHelper.SDC_EPC, EPC);
            long id =database.insert(dbHelper.TABLE_STOCK_DETAILS_CHECKSUM, null, values);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //check EPC from TagsmartStockDetails
    private boolean hasTagsmartStockDetails(String EPC)
    {
        String query = null;
        Cursor cursor = null;
        try {

            query = "SELECT  * FROM " + dbHelper.TABLE_TAGSMART_STOCK_DETAILS+ " where " + dbHelper.EPC + " = '" + EPC + "';";
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                // closing connection
                cursor.close();
                return true;

            }
            else
            {
                // closing connection
                cursor.close();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return false;
    }

    //Update EPC number in Tagsmart Stock details
    public void updateTagsmartStockEPC(String wrongEPC, String newEPC) {

        try {

            ContentValues values = new ContentValues();
            values.put(dbHelper.EPC, newEPC);
            database.update(dbHelper.TABLE_TAGSMART_STOCK_DETAILS, values, dbHelper.EPC + "=" + wrongEPC, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //delete Data in Tagsmart Stock details
    public void turcateTagsmartStockEPC()
    {
        try {

            database.delete(dbHelper.TABLE_TAGSMART_STOCK_DETAILS, null, null);;
            String query ="delete from sqlite_sequence where name='"+dbHelper.TABLE_TAGSMART_STOCK_DETAILS+"' ;";
            database.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //insert Data in Tagsmart Stock details
    public void insertTagsmartStockDetails(JSONArray tarry) {
        String tagsmart_id ="0";

        try {

            for(int s = 0 ; s<tarry.length(); s++) {

                JSONObject sj = tarry.getJSONObject(s);
            //    Log.d("dbmanager","insertTagsmartStockDetails "+sj);

                ContentValues values = new ContentValues();
                values.put(dbHelper.STOCK_ID, sj.getString("stock_id"));
                values.put(dbHelper.CUSTOMER_ID,  sj.getString("customer_id"));
                values.put(dbHelper.EPC,  sj.getString("epc"));
                values.put(dbHelper.BARCODE,  sj.getString("barcode"));
                long id = database.insert(dbHelper.TABLE_TAGSMART_STOCK_DETAILS, null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //insert Data in Tagsmart Stock details
    public void insertTagsmartStockDetailsByID(JSONArray tarry) {
        String tagsmart_id ="0";

        try {

            for(int s = 0 ; s<tarry.length(); s++) {

                JSONObject sj = tarry.getJSONObject(s);
                //    Log.d("dbmanager","insertTagsmartStockDetails "+sj);
                String EPCNumber =  sj.getString("epc");
                if (!checkIfEPCIsPresent(EPCNumber)) {

                    ContentValues values = new ContentValues();
                    values.put(dbHelper.STOCK_ID, sj.getString("stock_id"));
                    values.put(dbHelper.CUSTOMER_ID, sj.getString("customer_id"));
                    values.put(dbHelper.EPC, sj.getString("epc"));
                    values.put(dbHelper.BARCODE, sj.getString("barcode"));
                    long id = database.insert(dbHelper.TABLE_TAGSMART_STOCK_DETAILS, null, values);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //insert Data in Tagsmart Stock details
    public String insertTagsmartStockDetails(String stock_id, String customer_id,  String EPC, String barcode) {
        String tagsmart_id ="0";

        if(!hasTagsmartStockDetails(EPC)) // if EPC is not present then enter record
        {
            try {

                ContentValues values = new ContentValues();
                values.put(dbHelper.STOCK_ID, stock_id);
                values.put(dbHelper.CUSTOMER_ID, customer_id);
                values.put(dbHelper.EPC, EPC);
                values.put(dbHelper.BARCODE, barcode);
                long id =database.insert(dbHelper.TABLE_TAGSMART_STOCK_DETAILS, null, values);
                tagsmart_id = String.valueOf(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return tagsmart_id;
    }

    //check EPC from EPCLocationDetails
    private boolean hasEPCLocationDetails(String tagsmart_id)
    {
        String query = null;
        Cursor cursor = null;
        try {

            query = "SELECT  * FROM " + dbHelper.TABLE_EPC_LOCATION_DATE_TIME+ " where " + dbHelper.ST_TAGSMART_ID + " = '" + tagsmart_id + "';";
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                // closing connection
                cursor.close();
                return true;

            }
            else
            {
                // closing connection
                cursor.close();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return false;
    }

    //insert Data in EPC Location Date time table
    public void insertEPCLocationDetailsBYID(JSONArray epcArray) {

        try {

            for(int s = 0 ; s<epcArray.length(); s++) {

                JSONObject sj = epcArray.getJSONObject(s);
                //  Log.d("dbmanager","insertEPCLocationDetails "+sj);
                String tempStr= sj.getString("sldt_id");
                if((!tempStr.isEmpty() )&& (tempStr.compareTo("null") != 0)){
                    ContentValues values = new ContentValues();
                    values.put(dbHelper.SLDT_ID, sj.getString("sldt_id"));
                    values.put(dbHelper.ST_TAGSMART_ID, sj.getString("tagsmart_id"));
                    values.put(dbHelper.LOCATION_ID, sj.getString("location_id"));
                    values.put(dbHelper.SUB_LOCATION_ID, sj.getString("sub_location_id"));
                    values.put(dbHelper.STOCK_DATE, sj.getString("stock_date"));
                    values.put(dbHelper.STOCK_TIME, sj.getString("stock_time"));
                    values.put(dbHelper.IS_SOLD, sj.getString("is_sold"));
                    values.put(dbHelper.IS_SSE, "1");
                    long id =database.insert(dbHelper.TABLE_EPC_LOCATION_DATE_TIME, null, values);

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //insert Data in EPC Location Date time table
    public void insertEPCLocationDetails(JSONArray epcArray) {

            try {

                for(int s = 0 ; s<epcArray.length(); s++) {

                    JSONObject sj = epcArray.getJSONObject(s);
                  //  Log.d("dbmanager","insertEPCLocationDetails "+sj);
                    ContentValues values = new ContentValues();
                    values.put(dbHelper.ST_TAGSMART_ID, sj.getString("tagsmart_id"));
                    values.put(dbHelper.LOCATION_ID, sj.getString("location_id"));
                    values.put(dbHelper.SUB_LOCATION_ID, sj.getString("sub_location_id"));
                    values.put(dbHelper.STOCK_DATE, sj.getString("stock_date"));
                    values.put(dbHelper.STOCK_TIME, sj.getString("stock_time"));
                    values.put(dbHelper.IS_SOLD, sj.getString("is_sold"));
                    values.put(dbHelper.IS_SSE, "1");
                    long id =database.insert(dbHelper.TABLE_EPC_LOCATION_DATE_TIME, null, values);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    //insert Data in EPC Location Date time table
    public void insertEPCLocationDetails(String tagsmart_id, String location_id,  String sublocation_id, String is_sold) {
        if(!hasEPCLocationDetails(tagsmart_id)) // if tagsmart_id is not present then enter record
        {
            try {

                Date date = new Date();
                String modifiedDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

                String time = sdf.format(cal.getTime());
                ContentValues values = new ContentValues();
                values.put(dbHelper.ST_TAGSMART_ID, tagsmart_id);
                values.put(dbHelper.LOCATION_ID, location_id);
                values.put(dbHelper.SUB_LOCATION_ID, sublocation_id);
                values.put(dbHelper.STOCK_DATE, modifiedDate);
                values.put(dbHelper.STOCK_TIME, time);
                values.put(dbHelper.IS_SOLD, is_sold);
                values.put(dbHelper.IS_SSE, "1");
                long id =database.insert(dbHelper.TABLE_EPC_LOCATION_DATE_TIME, null, values);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //delete Data in EPC Location Date time table
    public void turcateEPCLocationDetails()
    {
        try {

            database.delete(dbHelper.TABLE_EPC_LOCATION_DATE_TIME, null, null);;
            String query ="delete from sqlite_sequence where name='"+dbHelper.TABLE_EPC_LOCATION_DATE_TIME+"' ;";
            database.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //delete Data in Stock table
    public void turcateCustomerStockDetails()
    {
        String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");
        try {

            database.delete(stock_table_name, null, null);;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //insert Data in CustomerStockDetailstime table
    public void insertCustomerStockDetails(JSONArray sArray) {

        String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");
        String data =  pref.getString(Constants.TABLE_METADATA_JSON, "");
        try
        {
            JSONObject jObj = new JSONObject(data);
            JSONArray stockColumnArray = jObj.getJSONArray("stock_metadata");

            for(int s = 0 ; s<sArray.length(); s++)
            {
                JSONObject sj = sArray.getJSONObject(s);
               // Log.d("dbmanager","stock "+sj);
                ArrayList<String> arr=new ArrayList<String>();
                Iterator iter = sj.keys();
                while(iter.hasNext()){
                    String key = (String)iter.next();
                    arr.add(sj.getString(key));
                }

                String []contentArray = arr.toArray(new String[arr.size()]);
              //  Log.d("dbmanager","contentArray.length "+contentArray.length);
              //  Log.d("dbmanager","stockColumnArray.length() "+stockColumnArray.length());
                if(contentArray.length == stockColumnArray.length())
                {
                    try {


                        ContentValues values = new ContentValues();
                        for(int h= 0 ; h <contentArray.length ; h++)
                        {
                            values.put(stockColumnArray.getString(h), contentArray[h]);
                        }

                        long id =database.insert(stock_table_name, null, values);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    //delete Data in product table
    public void turcateCustomerProductDetails()
    {
        String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
        try {

            database.delete(product_table_name, null, null);;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //insert Data in Customer product table
    public void insertCustomerProduct(JSONArray pArray) {

        String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
        String data =  pref.getString(Constants.TABLE_METADATA_JSON, "");

        try
        {
            JSONObject jObj = new JSONObject(data);
            JSONArray productColumnArray = jObj.getJSONArray("product_metadata");


            for(int s = 0 ; s<pArray.length(); s++) {
                JSONObject sj = pArray.getJSONObject(s);
                ArrayList<String> arr = new ArrayList<String>();
                Iterator iter = sj.keys();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    arr.add(sj.getString(key));
                }

                String[] contentArray = arr.toArray(new String[arr.size()]);
                if(contentArray.length == productColumnArray.length())
                {
                    try {


                        ContentValues values = new ContentValues();
                        for(int h= 0 ; h <contentArray.length ; h++)
                        {
                            values.put(productColumnArray.getString(h), contentArray[h]);
                        }

                        long id =database.insert(product_table_name, null, values);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //insert Data in CategoryMaster
    public void insertCategoryMaster(JSONArray catArray) {
        try {

            for(int c=0 ; c<catArray.length(); c++)
            {
                JSONObject catRec = catArray.getJSONObject(c);

                String cc_id, customer_id, category_column_name, category_level;
                cc_id = catRec.getString("cc_id");
                customer_id  = catRec.getString("customer_id");
                category_column_name = catRec.getString("category_column_name");
                category_level  = catRec.getString("category_level");

                //Inserting data into category master
                ContentValues values = new ContentValues();
                values.put(dbHelper.CC_ID, cc_id);
                values.put(dbHelper.CATRGORY_CUSTOMER_ID, customer_id);
                values.put(dbHelper.CATEGORY_COLUMN_NAME, category_column_name);
                values.put(dbHelper.CATEGORY_LEVEL, category_level);

                long id =database.insert(dbHelper.TABLE_CUSTOMER_CATEGORY_MASTER, null, values);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Get category list
    public ArrayList<String> getCategoryData(String categoryName, String productTableName)
    {
        String query = null;
        Cursor cursor = null;
        ArrayList<String>categoryList = new ArrayList<String>();
        try {

            query = "SELECT  DISTINCT "+categoryName+" FROM " + productTableName +";";
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {

                        categoryList.add(cursor.getString(0));
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return categoryList;
    }



    //Get category list
    public JSONArray getSubcategoryAndBarcodeByCategory(String categoryName,String subcategoryName)
    {
        String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
        String query = null;
        Cursor cursor = null;
        JSONArray categoryArray= new JSONArray();
        try {

            query = "SELECT "+categoryName+" , GROUP_CONCAT(DISTINCT("+subcategoryName+") ) as "+subcategoryName+" , GROUP_CONCAT(DISTINCT(barcode)) as barcode  FROM " + product_table_name+" GROUP BY "+categoryName +" ;";
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {

                        JSONObject sessionJSONObject= new JSONObject();
                        sessionJSONObject.put("category", cursor.getString(0));
                        sessionJSONObject.put("subcategory_array", cursor.getString(1));
                        sessionJSONObject.put("barcode_array", cursor.getString(2));

                        categoryArray.put(sessionJSONObject);
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return categoryArray;
    }

    //Get Category and subcategory list w.r.t Category
    public ArrayList<String> getsubcategoryByCategory(String categoryName, String categoryValue, String subcategoryName)
    {
        ArrayList<String>itemlist = new ArrayList<String>();
        String query = null;
        Cursor cursor = null;
        String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
        try {

            query = "SELECT DISTINCT " +subcategoryName+" FROM " + product_table_name + " WHERE "+categoryName+" = '"+categoryValue+"';";

            //Log.v("DBManager","getsubcategoryByCategory query "+query);
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                        //Log.v("DBManager","getsubcategoryByCategory cursor "+cursor.getString(0));
                        itemlist.add(cursor.getString(0));
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }
        return itemlist;
    }

    //Get Data from category master
    public JSONArray getAllCategoryMasterData()
    {
        String query = null;
        Cursor cursor = null;

        JSONArray categoryArray= new JSONArray();
        try {

            query = "SELECT  * FROM " + dbHelper.TABLE_CUSTOMER_CATEGORY_MASTER + " ORDER BY "+dbHelper.CATEGORY_LEVEL+" ASC;";
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                        JSONObject sessionJSONObject= new JSONObject();
                        sessionJSONObject.put(dbHelper.CC_ID, cursor.getInt(0));
                        sessionJSONObject.put(dbHelper.CATRGORY_CUSTOMER_ID, cursor.getString(1));
                        sessionJSONObject.put(dbHelper.CATEGORY_COLUMN_NAME, cursor.getString(2));
                        sessionJSONObject.put(dbHelper.CATEGORY_LEVEL, cursor.getString(3));

                        categoryArray.put(sessionJSONObject);
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return categoryArray;
    }

    //delete Data in category master
    public void turcateCategoryMaster()
    {
        try {

            database.delete(dbHelper.TABLE_CUSTOMER_CATEGORY_MASTER, null, null);;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //delete all Data in Stock Take Checksum table
    public void turcateStockTakeChecksum()
    {
        try {

            database.delete(dbHelper.TABLE_STOCK_TAKE_CHECKSUM, null, null);;
            String query ="delete from sqlite_sequence where name='"+dbHelper.TABLE_STOCK_TAKE_CHECKSUM+"' ;";
            database.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCategoryFilterBaseQuery(String category)
    {

        String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
        String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");
        String baseQuery="SELECT SUM( COALESCE("+stock_table_name+".quantity,'0') )as quantity,  "+product_table_name+"."+category+ " as category FROM "+stock_table_name+" INNER JOIN "+product_table_name+" ON "+product_table_name+".barcode =  "+stock_table_name+".barcode  "  ;
        //Log.v("DBManager","getCategoryFilterBaseQuery "+baseQuery);
        return baseQuery;
    }

    public String getEPCCategoryBaseQuery(String category)
    {

        String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
        String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");
        String baseQuery="SELECT  "+dbHelper.TABLE_TAGSMART_STOCK_DETAILS+"."+dbHelper.EPC+", "+product_table_name+"."+category+ " as category FROM "+stock_table_name+" INNER JOIN "+product_table_name+" ON "+product_table_name+".barcode =  "+stock_table_name+".barcode INNER JOIN "+dbHelper.TABLE_TAGSMART_STOCK_DETAILS+" ON "+dbHelper.TABLE_TAGSMART_STOCK_DETAILS+"."+dbHelper.BARCODE+"  =  "+stock_table_name+".barcode " ;

      //  Log.v("DBManager","getEPCCategoryBaseQuery "+baseQuery);
        return baseQuery;
    }


    public boolean verfiyEPCByCategory(String epc, String mainCategoryWhereClause )
    {
        boolean isPresent= false;

        String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
        String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");
        String baseQuery="SELECT * FROM "+stock_table_name+" INNER JOIN "+product_table_name+" ON "+product_table_name+".barcode =  "+stock_table_name+".barcode LEFT JOIN "+dbHelper.TABLE_TAGSMART_STOCK_DETAILS+" ON "+dbHelper.TABLE_TAGSMART_STOCK_DETAILS+"."+dbHelper.BARCODE+"  =  "+stock_table_name+".barcode  " ;
        String query = null;
        Cursor cursor = null;
        try {

            query = baseQuery+" WHERE  ("+mainCategoryWhereClause+")   AND  "+dbHelper.TABLE_TAGSMART_STOCK_DETAILS+"."+dbHelper.EPC+"='"+epc+"';";

            //Log.v("DBManager","verfiyEPCByCategory "+query);
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                isPresent= true;

            }
            // closing connection
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return isPresent;
    }

    public CopyOnWriteArrayList<EPCBarcodeInventory> getAll58BitBinaryEPCList(  String category)
    {
        String query = null;
        Cursor cursor = null;
        String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
        String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");
        CopyOnWriteArrayList<EPCBarcodeInventory> categoryArray= new CopyOnWriteArrayList<EPCBarcodeInventory>();
        try {

            String baseQuery="SELECT  "+product_table_name+".barcode , "+product_table_name+"."+category+ " , "+stock_table_name+".stock_id FROM "+product_table_name+" INNER JOIN "+stock_table_name+" ON "+product_table_name+".barcode =  "+stock_table_name+".barcode ";

            query = baseQuery+" ;";

            //Log.v("DBManager","getPartialBinaryEPCListByCategory "+query);
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                        // Log.v("DBManager","barcode "+cursor.getString(0).trim());
                        // convert barcode into partial binary EPC of 58 bits
                        String binaryBits58 = get58BitBinaryEPC(cursor.getString(0).trim());

                        EPCBarcodeInventory epcOb = new  EPCBarcodeInventory(binaryBits58,cursor.getString(1),"0",cursor.getString(0),cursor.getString(2));

                        categoryArray.add(epcOb);
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return categoryArray;
    }


    public CopyOnWriteArrayList<EPCBarcodeInventory> get58BitBinaryEPCListByCategory(String whereClausString, String category)
    {
        String query = null;
        Cursor cursor = null;
        String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
        String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");
        CopyOnWriteArrayList<EPCBarcodeInventory> categoryArray= new CopyOnWriteArrayList<EPCBarcodeInventory>();
        try {

            String baseQuery="SELECT  "+product_table_name+".barcode , "+product_table_name+"."+category+ " , "+stock_table_name+".stock_id FROM "+product_table_name+" INNER JOIN "+stock_table_name+" ON "+product_table_name+".barcode =  "+stock_table_name+".barcode ";

            query = baseQuery+" WHERE ("+whereClausString+" ) ;";

            //Log.v("DBManager","getPartialBinaryEPCListByCategory "+query);
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                       // Log.v("DBManager","barcode "+cursor.getString(0).trim());
                       // convert barcode into partial binary EPC of 58 bits
                        String binaryBits58 = get58BitBinaryEPC(cursor.getString(0).trim());

                        EPCBarcodeInventory epcOb = new  EPCBarcodeInventory(binaryBits58,cursor.getString(1),"0",cursor.getString(0),cursor.getString(2));

                        categoryArray.add(epcOb);
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return categoryArray;
    }

    private String leftPad(String str, int length, String padChar){
        String pad = "";
        for (int i = 0; i < length; i++) {
            pad += padChar;
        }
        return pad.trim()+ str.trim();
    }

    public String get58BitBinaryEPC(String strEAN)
    {
        String bitBinaryEPC="";
        String header="00110000";
        String filter="001";
        String partition="101";

        String company_prefix=Long.toBinaryString(Long.parseLong(strEAN.substring(0,7).trim()));

        //Log.d("EANActivity", "company_prefix "+company_prefix);
        if (company_prefix.length()<24) {
            company_prefix =leftPad(company_prefix, (24-company_prefix.length()), "0");
           // Log.d("EANActivity", "24company_prefixless "+company_prefix);

        }

        String item_reference=Long.toBinaryString(Long.parseLong("0"+strEAN.substring(7,strEAN.length()-1).trim()));
        //Log.d("EANActivity", "item_reference "+item_reference);
        if(item_reference.length()<20){
            item_reference =leftPad(item_reference, (20-item_reference.length()), "0");
           // Log.d("EANActivity", "24 item_reference "+item_reference);

        }


        bitBinaryEPC=header.trim()+filter.trim()+partition.trim()+company_prefix.trim()+item_reference.trim();

        //Log.v("DBManager","bitBinaryEPC "+bitBinaryEPC);
        return bitBinaryEPC;
    }

    public CopyOnWriteArrayList<EPCInventory> getEPCListByCategory(String whereClausString, String category)
    {
        String query = null;
        Cursor cursor = null;
        String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");

        CopyOnWriteArrayList<EPCInventory> categoryArray= new CopyOnWriteArrayList<EPCInventory>();
        try {

            query = getEPCCategoryBaseQuery(category)+" WHERE ("+whereClausString+" )   AND  "+dbHelper.TABLE_TAGSMART_STOCK_DETAILS+"."+dbHelper.EPC+" IS NOT NULL ;";

           // Log.v("DBManager","getEPCListByCategory "+query);
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                       // Log.v("DBManager","EPC "+cursor.getString(0).trim());

                        EPCInventory epcOb = new  EPCInventory(cursor.getString(0),cursor.getString(1),"0");

                        categoryArray.add(epcOb);
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return categoryArray;
    }
    public JSONArray getDataByMainCategory(  String category)
    {
        String query = null;
        Cursor cursor = null;
        String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");

        JSONArray categoryArray= new JSONArray();
        try {

            query = getCategoryFilterBaseQuery(category)+ "   GROUP BY  "+product_table_name+"."+category+";";

           // Log.v("DBManager","getDataByMainCategory11 "+query);
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                        //Log.v("DBManager","cursor "+cursor.toString());
                        JSONObject sessionJSONObject= new JSONObject();
                        sessionJSONObject.put("quantity", cursor.getString(0));
                        sessionJSONObject.put("scanned_qty", "0");
                        sessionJSONObject.put("category", cursor.getString(1));


                        categoryArray.put(sessionJSONObject);
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();


            query = "SELECT count("+dbHelper.TABLE_STOCK_TAKE_CHECKSUM+"."+dbHelper.STC_EPC+") as scanned_qty,  "+product_table_name+"."+category+ " as category FROM "+product_table_name+"  LEFT JOIN  "+dbHelper.TABLE_STOCK_TAKE_CHECKSUM+" ON "+product_table_name+".barcode =  "+dbHelper.TABLE_STOCK_TAKE_CHECKSUM+"."+dbHelper.STC_CATEGORY+ "   GROUP BY  "+product_table_name+"."+category+";";

           // Log.v("DBManager","getDataByMainCategory22 "+query);
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                        //Log.v("DBManager","cursor "+cursor.toString());

                        for(int y = 0 ; y< categoryArray.length(); y++)
                        {
                            JSONObject jObject=categoryArray.getJSONObject(y);
                            if(jObject.getString("category").compareTo(cursor.getString(1)) == 0)
                            {
                                jObject.put("scanned_qty", cursor.getString(0));
                                categoryArray.put(y, jObject);
                                break;
                            }
                        }
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return categoryArray;
    }

    public JSONArray getDataByCategory(String whereClausString, String category)
    {
        String query = null;
        Cursor cursor = null;
        String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");

        JSONArray categoryArray= new JSONArray();
        try {

            query = getCategoryFilterBaseQuery(category)+" WHERE "+whereClausString+"   GROUP BY  "+product_table_name+"."+category+";";

            //Log.v("DBManager","getDataByCategory111 "+query);
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                        //Log.v("DBManager","cursor "+cursor.toString());
                        JSONObject sessionJSONObject= new JSONObject();
                        sessionJSONObject.put("quantity", cursor.getString(0));
                        sessionJSONObject.put("scanned_qty", "0");
                        sessionJSONObject.put("category", cursor.getString(1));

                        categoryArray.put(sessionJSONObject);
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();

            query = "SELECT count("+dbHelper.TABLE_STOCK_TAKE_CHECKSUM+"."+dbHelper.STC_EPC+") as scanned_qty,  "+product_table_name+"."+category+ " as category FROM "+product_table_name+"  LEFT JOIN  "+dbHelper.TABLE_STOCK_TAKE_CHECKSUM+" ON "+product_table_name+".barcode =  "+dbHelper.TABLE_STOCK_TAKE_CHECKSUM+"."+dbHelper.STC_CATEGORY+" WHERE "+whereClausString+"   GROUP BY  "+product_table_name+"."+category+";";

            //Log.v("DBManager","getDataByCategory222 "+query);
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                        //Log.v("DBManager","cursor "+cursor.toString());

                        for(int y = 0 ; y< categoryArray.length(); y++)
                        {
                            JSONObject jObject=categoryArray.getJSONObject(y);
                            if(jObject.getString("category").compareTo(cursor.getString(1)) == 0)
                            {
                                jObject.put("scanned_qty", cursor.getString(0));
                                categoryArray.put(y, jObject);
                                break;
                            }
                        }
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return categoryArray;
    }

    public boolean checkIfEPCIsPresent(String epc)
    {
        boolean isEPC = false;
        String query = null;
        Cursor cursor = null;
        try {

            query = "SELECT  * FROM " + dbHelper.TABLE_TAGSMART_STOCK_DETAILS + " WHERE "+dbHelper.EPC+" = '"+epc+"' LIMIT 1 ;";
            cursor = database.rawQuery(query, null);
            if ( cursor.getCount() > 0)
            {
                isEPC = true;

            }
            // closing connection
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return isEPC;
    }

    private boolean EPCIsPresentStockTakeChecksum(String epc)
    {
        boolean isEPC = false;
        String query = null;
        Cursor cursor = null;
        try {

            query = "SELECT  * FROM " + dbHelper.TABLE_STOCK_TAKE_CHECKSUM + " WHERE "+dbHelper.STC_EPC+" = '"+epc+"';";
            cursor = database.rawQuery(query, null);
            if ( cursor.getCount() > 0)
            {
                isEPC = true;

            }
            // closing connection
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return isEPC;
    }

    private String getEANFromStockDataByStockID(String stockId)
    {
        String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");

        String barcode="";

        String query = null;
        Cursor cursor = null;

        try {

            query = "SELECT "+stock_table_name+"."+"barcode FROM " + stock_table_name +"  WHERE  stock_id = '"+stockId+"' LIMIT 1 ;";

          //  Log.d("dbmanager","getEANFromStockDataByStockID query "+query);

            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {

                        barcode = cursor.getString(0);
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return barcode;
    }

    public void checkAndupdateStockTakeA(EPCBarcodeInventory ebiObj, String location_id, String sublocation_id )
    {
        String query = null;
        Cursor cursor = null;
        long tagsmart_id=0;
        //Get Barcode from customer stock data
        String barcode=ebiObj.getBarcode();
        barcode = barcode.trim();

        try {

            query = "SELECT "+dbHelper.TABLE_STOCK_TAKE_CHECKSUM+"."+"* FROM " + dbHelper.TABLE_STOCK_TAKE_CHECKSUM +"  WHERE "+dbHelper.STC_EPC+" = '"+ebiObj.getEpc()+"' LIMIT 1 ;";

           // Log.d("dbmanager","checkAndupdateStockTakeA query "+query);

            cursor = database.rawQuery(query, null);
            if ( cursor.getCount() > 0)
            {
                tagsmart_id = tagsmart_id+1;
            }

            // closing connection
            cursor.close();
            //if tagsmart_id exist i.e. if record exist in tagsmart stock table
            if(tagsmart_id == 0)
            {
                if(barcode.length() >0 )
                {
                     // insert record in Stock Take Checksum table
                    try {

                        ContentValues values = new ContentValues();
                        values.put(dbHelper.STC_EPC,ebiObj.getEpc() );
                        values.put(dbHelper.STC_CATEGORY, barcode);

                        database.insert(dbHelper.TABLE_STOCK_TAKE_CHECKSUM, null, values);

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }
    }



    public void checkAndUpdateInward(EPCBarcodeInwarding ebiObj, String location_id, String sublocation_id )
    {
        String query = null;
        Cursor cursor = null;
        long tagsmart_id=0;
        //Get Barcode from customer stock data
        String barcode=ebiObj.getBarcode();
        barcode = barcode.trim();

        try {

            query = "SELECT "+dbHelper.TABLE_STOCK_TAKE_CHECKSUM+"."+"* FROM " + dbHelper.TABLE_STOCK_TAKE_CHECKSUM +"  WHERE "+dbHelper.STC_EPC+" = '"+ebiObj.getEpc()+"' LIMIT 1 ;";

            // Log.d("dbmanager","checkAndupdateStockTakeA query "+query);

            cursor = database.rawQuery(query, null);
            if ( cursor.getCount() > 0)
            {
                tagsmart_id = tagsmart_id+1;
            }

            // closing connection
            cursor.close();
            //if tagsmart_id exist i.e. if record exist in tagsmart stock table
            if(tagsmart_id == 0)
            {
                if(barcode.length() >0 )
                {
                    // insert record in Stock Take Checksum table
                    try {

                        ContentValues values = new ContentValues();
                        values.put(dbHelper.STC_EPC,ebiObj.getEpc() );
                        values.put(dbHelper.STC_CATEGORY, barcode);

                        database.insert(dbHelper.TABLE_STOCK_TAKE_CHECKSUM, null, values);

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }
    }

    //Get Data from Stock Take Checksum table
    public JSONArray getAllStockTakeChecksum()
    {
        String query = null;
        Cursor cursor = null;

        JSONArray categoryArray= new JSONArray();
        try {

            query = "SELECT "+dbHelper.TABLE_STOCK_TAKE_CHECKSUM+"."+dbHelper.STC_EPC+" , "+dbHelper.TABLE_STOCK_TAKE_CHECKSUM+"."+dbHelper.STC_CATEGORY+" , "+dbHelper.TABLE_EPC_LOCATION_DATE_TIME+"."+dbHelper.LOCATION_ID+" , "+dbHelper.TABLE_EPC_LOCATION_DATE_TIME+"."+dbHelper.SUB_LOCATION_ID+   " FROM " + dbHelper.TABLE_STOCK_TAKE_CHECKSUM + " INNER JOIN "+dbHelper.TABLE_TAGSMART_STOCK_DETAILS+" ON "+dbHelper.TABLE_TAGSMART_STOCK_DETAILS+"."+dbHelper.EPC+" = "+dbHelper.TABLE_STOCK_TAKE_CHECKSUM+"."+dbHelper.STC_EPC+" INNER JOIN "+dbHelper.TABLE_EPC_LOCATION_DATE_TIME+" ON "+dbHelper.TABLE_EPC_LOCATION_DATE_TIME+"."+dbHelper.ST_TAGSMART_ID+" = "+ dbHelper.TABLE_TAGSMART_STOCK_DETAILS+"."+ dbHelper.TAGSMART_ID+";";

          //  Log.d("dbmanager","getAllStockTakeChecksum query "+query);
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                        Date date = new Date();
                        String modifiedDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        String time = sdf.format(cal.getTime());
                        JSONObject sessionJSONObject= new JSONObject();
                        sessionJSONObject.put("epc", cursor.getString(0));
                        sessionJSONObject.put("barcode", cursor.getString(1));
                        sessionJSONObject.put("location_id", cursor.getString(2));
                        sessionJSONObject.put("sub_location_id", cursor.getString(3));
                        sessionJSONObject.put("stock_date", modifiedDate );
                        sessionJSONObject.put("stock_time", time );

                        categoryArray.put(sessionJSONObject);
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return categoryArray;
    }


    //delete Data in Stock take checksum
    public void deleteStockTakeChecksum(JSONArray EPCArray) {
        try {

            for(int c=0 ; c<EPCArray.length(); c++) {
                JSONObject catRec = EPCArray.getJSONObject(c);

                String EPC;
                EPC = catRec.getString("EPC");
                database.delete(dbHelper.TABLE_STOCK_TAKE_CHECKSUM, dbHelper.STC_EPC + "= ?", new String[]{EPC});
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Get category name list belong to category column in product table for stock take B option
    public  ArrayList<String> getEPCByBarcode( String barcode)
    {
        String query = null;
        Cursor cursor = null;
        ArrayList<String> catList= new ArrayList<String>();
        String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");


        try {

            query = "SELECT  DISTINCT "+dbHelper.EPC+" FROM " + dbHelper.TABLE_TAGSMART_STOCK_DETAILS+ " where barcode IN (" + barcode + ");";
            cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    catList.add(cursor.getString(0));

                } while (cursor.moveToNext());
            }
            // closing connection
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return catList;
    }


    //Get category name list belong to category column in product table for stock take B option
    public  ArrayList<String> getCategoryByiCodes(String categoryColumnName, String icodes)
    {
        ArrayList<String> a=new ArrayList<String>(Arrays.asList(icodes.split(" , ")));


        String quote_icodes  ="'"+a.toString().replace("[","").replace("]", "").replace(" ","").replace(",","','")+"'";
        String query = null;
        Cursor cursor = null;
        ArrayList<String> catList= new ArrayList<String>();
        String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");

        try {

            query = "SELECT  DISTINCT "+categoryColumnName+" FROM " + product_table_name+ " where icode IN (" + quote_icodes + ");";
            //Log.d("DBManager", "getCategoryByiCodes " + query);
            cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    catList.add(cursor.getString(0));

                } while (cursor.moveToNext());
            }
            // closing connection
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return catList;
    }

    public JSONArray getDataByKeyword(String keyword, String category, JSONArray mainCatList)
    {
        String query = null;
        Cursor cursor = null;
        String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
        String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");
        String whereClause="";
        String cate="";
        //Building default query for category list data
        for(int u =0 ;u<mainCatList.length(); u++ ) {
            try {
                String mainCategoryName = mainCatList.getJSONObject(u).getString("category_column_name");
                //  Log.v("InventoryA", "mainCatList.get(u) " + mainCatList.get(u));
                if (u < (mainCatList.length() - 1)) {
                    whereClause = whereClause + " " + product_table_name + "." + mainCategoryName + " like '%" +keyword  + "%' OR ";

                } else {
                    whereClause = whereClause + " " + product_table_name + "." + mainCategoryName + " like '%" + keyword + "%'  ";
                }

                cate = cate +product_table_name+"."+mainCategoryName+", ";


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cate = cate + "GROUP_CONCAT("+stock_table_name+". barcode,',') as barcode ";

        JSONArray categoryArray= new JSONArray();
        try {

            query = "SELECT "+stock_table_name+".quantity, "+cate+" FROM "+product_table_name+" INNER JOIN "+stock_table_name+" ON "+stock_table_name+".barcode = "+product_table_name+".barcode  WHERE "+whereClause+ "   GROUP BY  "+product_table_name+"."+category+";";

         //   Log.v("DBManager","getDataByCategory "+query);
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                        //Log.v("DBManager","cursor "+cursor.toString());
                        JSONObject sessionJSONObject= new JSONObject();
                        sessionJSONObject.put("quantity", cursor.getString(0));

                        sessionJSONObject.put("category", cursor.getString(1));

                        sessionJSONObject.put("barcode",  cursor.getString(cursor.getColumnIndex("barcode")));

                        categoryArray.put(sessionJSONObject);
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return categoryArray;
    }

    public JSONArray getDataByBarcode(String ean, String category)
    {
        String query = null;
        Cursor cursor = null;
        String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
        String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");


        JSONArray categoryArray= new JSONArray();
        try {

            query = "SELECT "+stock_table_name+".quantity, "+product_table_name+"."+category+" FROM "+product_table_name+" INNER JOIN "+stock_table_name+" ON "+stock_table_name+".barcode = "+product_table_name+".barcode  WHERE "+product_table_name+".barcode = '"+ean+ "'   GROUP BY  "+product_table_name+"."+category+";";

            //Log.v("DBManager","getDataByCategory "+query);
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                       // Log.v("DBManager","cursor "+cursor.toString());
                        JSONObject sessionJSONObject= new JSONObject();
                        sessionJSONObject.put("quantity", cursor.getString(0));
                        sessionJSONObject.put("category", cursor.getString(1));

                        categoryArray.put(sessionJSONObject);
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return categoryArray;
    }

    public JSONArray getDataByEPC(String epc, String category)
    {
        String query = null;
        Cursor cursor = null;
        String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
        String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");


        JSONArray categoryArray= new JSONArray();
        try {

            query = "SELECT "+stock_table_name+".quantity, "+product_table_name+"."+category+" FROM "+stock_table_name+" INNER JOIN "+dbHelper.TABLE_TAGSMART_STOCK_DETAILS+" ON "+dbHelper.TABLE_TAGSMART_STOCK_DETAILS+"."+dbHelper.STOCK_ID+"  =  "+stock_table_name+".stock_id"+" INNER JOIN "+product_table_name+" ON "+stock_table_name+".barcode = "+product_table_name+".barcode  WHERE "+dbHelper.TABLE_TAGSMART_STOCK_DETAILS+"."+dbHelper.EPC +"= '"+epc+ "' ;";

            //Log.v("DBManager","getDataByCategory "+query);
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                        //Log.v("DBManager","cursor "+cursor.toString());
                        JSONObject sessionJSONObject= new JSONObject();
                        sessionJSONObject.put("quantity", cursor.getString(0));
                        sessionJSONObject.put("category", cursor.getString(1));

                        categoryArray.put(sessionJSONObject);
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return categoryArray;
    }


    //insert Data in CategoryMaster
    public void insertInwardData(JSONArray catArray) {
        try {

            for(int c=0 ; c<catArray.length(); c++)
            {
                JSONObject catRec = catArray.getJSONObject(c);

                String inward_id, dispatch_order_id, store_id, inward_date, barcode, quantity, rfid_quantity;

                inward_id = catRec.getString("inward_id");
                dispatch_order_id = catRec.getString("dispatch_order_id");
                store_id  = catRec.getString("store_id");
                inward_date = catRec.getString("inward_date");
                barcode  = catRec.getString("barcode");
                quantity = catRec.getString("quantity");
                rfid_quantity  = catRec.getString("rfid_quantity");
               // rfid_quantity  = "0";

                //Inserting data into category master
                ContentValues values = new ContentValues();
                values.put(dbHelper.INWARD_ID, inward_id);
                values.put(dbHelper.DISPATCH_ORDER_ID, dispatch_order_id);
                values.put(dbHelper.ID_STORE_ID, store_id);
                values.put(dbHelper.INWARD_DATE, inward_date);
                values.put(dbHelper.ID_BARCODE, barcode);
                values.put(dbHelper.ID_QUANTITY, quantity);
                values.put(dbHelper.ID_RFID_QUANTITY, rfid_quantity);
                long id =database.insert(dbHelper.TABLE_INWARD_DATA, null, values);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //delete all Data in Inward Data table
    public void turcateInwardData()
    {
        try {

            database.delete(dbHelper.TABLE_INWARD_DATA, null, null);;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Inward> getInwardData()
    {
        String query = null;
        Cursor cursor = null;

        ArrayList<Inward> categoryArray= new ArrayList<Inward>();
        try {

            query = "SELECT (SUM("+dbHelper.ID_QUANTITY+")- SUM("+dbHelper.ID_RFID_QUANTITY+")) as diff, "+dbHelper.INWARD_DATE+" , "+dbHelper.DISPATCH_ORDER_ID+"  FROM "+dbHelper.TABLE_INWARD_DATA+"    GROUP BY  "+dbHelper.DISPATCH_ORDER_ID+";";

            Log.v("DBManager","getInwardData "+query);
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                        // Log.v("DBManager","cursor "+cursor.toString());
                        Inward sessionJSONObject= new Inward();
                        sessionJSONObject.setQuantity(cursor.getString(0));
                        sessionJSONObject.setInwarding_date( cursor.getString(1));
                        sessionJSONObject.setDispatch_order( cursor.getString(2));
                        categoryArray.add(sessionJSONObject);
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return categoryArray;
    }

    public void updateBarcodeInwardCount(String barcode)
    {
        String query = null;
        Cursor cursor = null;
        String rfidQty="0";
        try {


            query="SELECT "+dbHelper.ID_RFID_QUANTITY+" FROM "+dbHelper.TABLE_INWARD_DATA+" WHERE "+dbHelper.ID_BARCODE+" ='"+barcode+"' LIMIT 1 ;";

             //Log.v("DBManager","updateBarcodeInwardCount "+query);
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                        rfidQty = cursor.getString(0);
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();

            int incValue = 1+ Integer.parseInt(rfidQty);

            ContentValues values = new ContentValues();
            values.put(dbHelper.ID_RFID_QUANTITY, String.valueOf(incValue));
            database.update(dbHelper.TABLE_INWARD_DATA, values, dbHelper.ID_BARCODE + "=" + barcode, null);



        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

    }

    public JSONArray getInwardCount()
    {
        String query = null;
        Cursor cursor = null;
        JSONArray resultSet = new JSONArray();
        try {


            query="SELECT * FROM "+dbHelper.TABLE_INWARD_DATA+" WHERE "+dbHelper.ID_RFID_QUANTITY+" <>'0' ;";

            //Log.v("DBManager","updateBarcodeInwardCount "+query);
            cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

                int totalColumn = cursor.getColumnCount();
                JSONObject rowObject = new JSONObject();

                for (int i = 0; i < totalColumn; i++) {
                    if (cursor.getColumnName(i) != null) {

                        try {

                            if (cursor.getString(i) != null) {
                                Log.d("TAG_NAME", cursor.getString(i));
                                rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                            } else {
                                rowObject.put(cursor.getColumnName(i), "");
                            }
                        } catch (Exception e) {
                            Log.d("TAG_NAME", e.getMessage());
                        }
                    }

                }

                resultSet.put(rowObject);
                cursor.moveToNext();
            }

            // closing connection
            cursor.close();


        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }
        return resultSet;

    }
    public ArrayList<String> getInwardDataByDispatchOrder(String dispatchOrder)
    {
        String query = null;
        Cursor cursor = null;

        ArrayList<String> mArrayProducts = new ArrayList<String>();
        try {

            query="SELECT "+dbHelper.ID_BARCODE+" as barcode FROM "+dbHelper.TABLE_INWARD_DATA+" WHERE "
                    +dbHelper.TABLE_INWARD_DATA+"."+dbHelper.DISPATCH_ORDER_ID+" = "+dispatchOrder+";";

            // Log.v("DBManager","getDataByInwardList "+query);
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                        //Log.v("DBManager","cursor "+cursor.toString());
                        mArrayProducts.add(cursor.getString(0));

                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();


        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return mArrayProducts;
    }

    public JSONArray getDataByInwardList(  String category, String dispatchOrder)
    {
        String query = null;
        Cursor cursor = null;
        String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");

        JSONArray categoryArray= new JSONArray();
        try {

             String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");
            query="SELECT SUM( COALESCE("+stock_table_name+".quantity,'0') )as quantity,  "+product_table_name+"."+category+ " as category, SUM( COALESCE( "+dbHelper.TABLE_INWARD_DATA+"."+dbHelper.ID_RFID_QUANTITY+",0)) as rfid_quantity FROM "+stock_table_name+" INNER JOIN "+product_table_name+" ON "+product_table_name+".barcode =  "+stock_table_name+".barcode  "
                    +" INNER JOIN "+dbHelper.TABLE_INWARD_DATA+" ON "+dbHelper.TABLE_INWARD_DATA+"."+dbHelper.ID_BARCODE+" = "+stock_table_name+".barcode  WHERE "
                    +dbHelper.TABLE_INWARD_DATA+"."+dbHelper.DISPATCH_ORDER_ID+" = "+dispatchOrder
                    + "   GROUP BY  "+product_table_name+"."+category+";";

            // Log.v("DBManager","getDataByInwardList "+query);
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                        //Log.v("DBManager","cursor "+cursor.toString());
                        JSONObject sessionJSONObject= new JSONObject();
                        sessionJSONObject.put("quantity", cursor.getString(0));
                        sessionJSONObject.put("scanned_qty", cursor.getString(2));
                        sessionJSONObject.put("category", cursor.getString(1));


                        categoryArray.put(sessionJSONObject);
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();


        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return categoryArray;
    }


    public CopyOnWriteArrayList<EPCBarcodeInwarding> getInward58BitBinaryEPCList(String dispatchOrder)
    {
        String query = null;
        Cursor cursor = null;
        String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
        String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");
        CopyOnWriteArrayList<EPCBarcodeInwarding> categoryArray= new CopyOnWriteArrayList<EPCBarcodeInwarding>();
        try {

            query="SELECT "+product_table_name+".barcode  FROM "+stock_table_name+" INNER JOIN "+product_table_name+" ON "+product_table_name+".barcode =  "+stock_table_name+".barcode  "
                    +" INNER JOIN "+dbHelper.TABLE_INWARD_DATA+" ON "+dbHelper.TABLE_INWARD_DATA+"."+dbHelper.ID_BARCODE+" = "+stock_table_name+".barcode  WHERE "
                    +dbHelper.TABLE_INWARD_DATA+"."+dbHelper.DISPATCH_ORDER_ID+" = "+dispatchOrder+ ";";



            Log.v("DBManager","getInward58BitBinaryEPCList "+query);
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                        // Log.v("DBManager","barcode "+cursor.getString(0).trim());
                        // convert barcode into partial binary EPC of 58 bits
                        String binaryBits58 = get58BitBinaryEPC(cursor.getString(0).trim());

                        EPCBarcodeInwarding epcOb = new  EPCBarcodeInwarding(binaryBits58, cursor.getString(0).trim());

                        categoryArray.add(epcOb);
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return categoryArray;
    }


    //For getting data to create Tree structure for n level listview
    public  ArrayList<String> getAllTreeData()
    {
        String query = null;
        Cursor cursor = null;
        ArrayList<String> catList= new ArrayList<String>();
        ArrayList<String> cList= new ArrayList<String>();
        try {
            JSONArray categoryjArray = getAllCategoryMasterData();
            String catStr = "";
            for (int c = 0; c < categoryjArray.length(); c++) {
                JSONObject jobj = categoryjArray.getJSONObject(c);
                cList.add(jobj.getString("category_column_name"));
                if (c == categoryjArray.length() - 1) {
                    catStr = catStr + jobj.getString("category_column_name");
                } else {
                    catStr = catStr + jobj.getString("category_column_name")+",";
                }
            }
            String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
            String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");

            query="SELECT DISTINCT "+catStr+", "+stock_table_name+".barcode, "+stock_table_name+".quantity FROM "+ product_table_name+" LEFT JOIN "+stock_table_name+" ON "+stock_table_name+".barcode = "+product_table_name+".barcode ;";
            Log.d("DBManager","query "+query);
            cursor = database.rawQuery(query, null);
            if (cursor != null &&cursor.moveToFirst()) {
                do {

                    String tempStr = "";
                    for (int c = 0; c < cList.size(); c++) {
                        tempStr = tempStr + cursor.getString(cursor.getColumnIndex(cList.get(c)))+",";
                    }
                    //getbarcode
                    tempStr = tempStr + cursor.getString(cursor.getColumnIndex("barcode"))+",";
                    // get quantity
                    tempStr = tempStr + cursor.getString(cursor.getColumnIndex("quantity"));

                   // Log.d("DBManager","getAllTreeData "+tempStr);
                    catList.add(tempStr);

                } while (cursor.moveToNext());
            }


            // closing connection
            cursor.close();

        }
        catch(Exception e)
        {
            e.printStackTrace();;
        }
        finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return catList;
    }


    //For getting data to create Tree structure for n level listview for selected barcode
    public  ArrayList<String> getTreeDataByBarcodes(String selectedBarcodes)
    {
        String query = null;
        Cursor cursor = null;
        ArrayList<String> catList= new ArrayList<String>();
        ArrayList<String> cList= new ArrayList<String>();
        try {
            JSONArray categoryjArray = getAllCategoryMasterData();
            String catStr = "";
            for (int c = 0; c < categoryjArray.length(); c++) {
                JSONObject jobj = categoryjArray.getJSONObject(c);
                cList.add(jobj.getString("category_column_name"));
                if (c == categoryjArray.length() - 1) {
                    catStr = catStr + jobj.getString("category_column_name");
                } else {
                    catStr = catStr + jobj.getString("category_column_name")+",";
                }
            }
            String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
            String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");

            query="SELECT DISTINCT "+catStr+", "+stock_table_name+".barcode, "+stock_table_name+".quantity FROM "+ product_table_name+" LEFT JOIN "+stock_table_name+" ON "+stock_table_name+".barcode = "+product_table_name+".barcode WHERE "+product_table_name+".barcode IN ("+selectedBarcodes+") ;";
            Log.d("DBManager","query "+query);
            cursor = database.rawQuery(query, null);
            if (cursor != null &&cursor.moveToFirst()) {
                do {

                    String tempStr = "";
                    for (int c = 0; c < cList.size(); c++) {
                        tempStr = tempStr + cursor.getString(cursor.getColumnIndex(cList.get(c)))+",";
                    }
                    //getbarcode
                    tempStr = tempStr + cursor.getString(cursor.getColumnIndex("barcode"))+",";
                    // get quantity
                    tempStr = tempStr + cursor.getString(cursor.getColumnIndex("quantity"));

                    // Log.d("DBManager","getAllTreeData "+tempStr);
                    catList.add(tempStr);

                } while (cursor.moveToNext());
            }


            // closing connection
            cursor.close();

        }
        catch(Exception e)
        {
            e.printStackTrace();;
        }
        finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return catList;
    }

    //For getting data to create Tree structure for n level listview for selected barcode
    public  JSONArray getQuantityByBarcodes(String selectedBarcodes)
    {
        String query = null;
        Cursor cursor = null;
        JSONArray categoryArray = new JSONArray();
        JSONArray scanEANList= new JSONArray();

        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray categoryjArray = getAllCategoryMasterData();
            String catStr = "";
            if(categoryjArray.length() > 0)
            {
                JSONObject jobj = categoryjArray.getJSONObject(0);
                catStr = jobj.getString("category_column_name");

            }

            String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
            String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");

            query="SELECT DISTINCT "+catStr+", SUM("+stock_table_name+".quantity) as total_qty , GROUP_CONCAT("+stock_table_name+".quantity) as qty , GROUP_CONCAT("+stock_table_name+ ".barcode) as barcode FROM "+ product_table_name+" LEFT JOIN "+stock_table_name+" ON "+stock_table_name+".barcode = "+product_table_name+".barcode WHERE "+product_table_name+".barcode IN ("+selectedBarcodes+") Group by "+catStr+" ;";
            //Log.d("DBManager","getQuantityByBarcodes11 "+query);
            cursor = database.rawQuery(query, null);
            if (cursor != null &&cursor.moveToFirst()) {
                do {

                    JSONObject sessionJSONObject= new JSONObject();
                    sessionJSONObject.put("scan_quantity", 0);
                    sessionJSONObject.put("total_quantity", cursor.getString(1));
                    sessionJSONObject.put("category", cursor.getString(0));


                    String[] qtyvalues =  cursor.getString(2).split(",");
                    String[] barcodevalues =  cursor.getString(3).split(",");
                    JSONArray barcodeArray = new JSONArray();
                    for(int h =0 ; h< barcodevalues.length; h++)
                    {
                        JSONObject sessionJSONObject2= new JSONObject();
                        sessionJSONObject2.put("scan_quantity", 0);
                        sessionJSONObject2.put("total_quantity", qtyvalues[h]);
                        sessionJSONObject2.put("category", barcodevalues[h]);
                        barcodeArray.put(sessionJSONObject2);
                    }
                    sessionJSONObject.put("barcode",barcodeArray);
                    categoryArray.put(sessionJSONObject);

                } while (cursor.moveToNext());
            }


            // closing connection
            cursor.close();

            query = "SELECT count("+dbHelper.TABLE_STOCK_TAKE_CHECKSUM+"."+dbHelper.STC_EPC+") as scanned_qty,  "+dbHelper.TABLE_STOCK_TAKE_CHECKSUM+"."+dbHelper.STC_CATEGORY+ " as barcode FROM "+dbHelper.TABLE_STOCK_TAKE_CHECKSUM+" GROUP BY "+dbHelper.TABLE_STOCK_TAKE_CHECKSUM+"."+dbHelper.STC_CATEGORY+";";

           // Log.d("DBManager","getQuantityByBarcodes22 "+query);

            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                        //Log.v("DBManager","cursor "+cursor.toString());
                        if(cursor.getCount() == 1)
                        {
                            if(cursor.getString(1)==null && cursor.getString(0).compareTo("0") ==  0)
                            {
                                Log.d("DBManager","QuantityByBarcodes22:  no data found ");
                                return categoryArray;
                            }
                        }
                        JSONObject tObj = new JSONObject();
                        tObj.put("count",cursor.getString(0));
                        tObj.put("ean",cursor.getString(1));
                        scanEANList.put(tObj);


                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();

            //update scan count

            for(int y = 0 ; y< categoryArray.length(); y++)
            {
                JSONObject jObject=categoryArray.getJSONObject(y);
                int total_qty=0;
                JSONArray barcodeArray = jObject.getJSONArray("barcode");

               // Log.d("DBManager","main category: "+jObject.getString("category"));
                for (int e = 0; e < scanEANList.length(); e++) {

                    for (int b = 0; b < barcodeArray.length(); b++)
                    {
                        JSONObject childObject=barcodeArray.getJSONObject(b);

                       // Log.d("DBManager","Child category11: "+childObject.getString("category"));
                        //Log.d("DBManager","stock check barcode: "+scanEANList.getJSONObject(e).getString("ean"));

                        if(childObject.getString("category").compareTo(scanEANList.getJSONObject(e).getString("ean")) == 0)
                        {
                            //Log.d("DBManager","Child category22: "+childObject.getString("category"));

                            childObject.put("scan_quantity", scanEANList.getJSONObject(e).getString("count"));

                            //Log.d("DBManager","total_qty00: "+total_qty);

                            //Log.d("DBManager","Integer.parseInt(scanEANList.getJSONObject(e).getString(\"count\")): "+Integer.parseInt(scanEANList.getJSONObject(e).getString("count")));

                            total_qty = total_qty+ Integer.parseInt(scanEANList.getJSONObject(e).getString("count"));


                            //Log.d("DBManager","total_qty11: "+total_qty);

                            barcodeArray.put(b, childObject);

                        }

                    }
                }


                //Log.d("DBManager","total_qty22: "+total_qty+"\n\n");
                jObject.put("scan_quantity", total_qty);
                jObject.put("barcode",barcodeArray);
                categoryArray.put(y,jObject);
            }


        }
        catch(Exception e)
        {
            e.printStackTrace();;
        }

        finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return categoryArray;
    }


    public CopyOnWriteArrayList<EPCBarcodeInventory> convertTo58BitBinaryEPCListByCategory(String catStr)
    {
        String query = null;
        Cursor cursor = null;
        String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
        String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");
        CopyOnWriteArrayList<EPCBarcodeInventory> categoryArray= new CopyOnWriteArrayList<EPCBarcodeInventory>();

        try {

            String baseQuery="SELECT  "+product_table_name+".barcode ,  "+stock_table_name+".stock_id FROM "+product_table_name+" INNER JOIN "+stock_table_name+" ON "+product_table_name+".barcode =  "+stock_table_name+".barcode ";

            query = baseQuery+" WHERE "+product_table_name+".barcode IN ("+catStr+");";

            //Log.d("DBManager","convertTo58BitBinaryEPCListByCategory "+query);
            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                        // Log.v("DBManager","barcode "+cursor.getString(0).trim());
                        // convert barcode into partial binary EPC of 58 bits
                        String binaryBits58 = get58BitBinaryEPC(cursor.getString(0).trim());

                        EPCBarcodeInventory epcOb = new  EPCBarcodeInventory(binaryBits58, "","0",cursor.getString(0),cursor.getString(1));

                        categoryArray.add(epcOb);
                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return categoryArray;
    }


    //For getting data fromstock take checksum and stock quantity
    public  JSONArray getRFIDTotalScanQuantity()
    {
        String query = null;
        Cursor cursor = null;
        JSONArray scanEANList= new JSONArray();

        JSONObject jsonObject = new JSONObject();
        try {


            String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
            String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");



            query = "SELECT count("+dbHelper.TABLE_STOCK_TAKE_CHECKSUM+"."+dbHelper.STC_EPC+") as scanned_qty,  "+dbHelper.TABLE_STOCK_TAKE_CHECKSUM+"."+dbHelper.STC_CATEGORY+ " as barcode FROM "+dbHelper.TABLE_STOCK_TAKE_CHECKSUM+ " GROUP BY "+dbHelper.TABLE_STOCK_TAKE_CHECKSUM+"."+dbHelper.STC_CATEGORY+";";

            // Log.d("DBManager","getRFIDTotalScanQuantity "+query);

            cursor = database.rawQuery(query, null);
            if ((cursor.moveToFirst()) || cursor.getCount() > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                        //Log.v("DBManager","cursor "+resetRFIDScanQTYcursor.toString());
                        if(cursor.getCount() == 1)
                        {
                            if(cursor.getString(1)==null && cursor.getString(0).compareTo("0") ==  0)
                            {
                                Log.d("DBManager","getRFIDTotalScanQuantity:  no data found ");
                                return scanEANList;
                            }
                        }
                        JSONObject tObj = new JSONObject();
                        tObj.put("scan",cursor.getString(0));
                        tObj.put("barcode",cursor.getString(1));
                        scanEANList.put(tObj);


                    } while (cursor.moveToNext());
                }

            }
            // closing connection
            cursor.close();




        }
        catch(Exception e)
        {
            e.printStackTrace();;
        }

        finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return scanEANList;
    }

    public  JSONArray getTotalQuantityOnlyBySelectedBarcodes(String selectedBarcodes)
    {
        String query = null;
        Cursor cursor = null;
        int totalQty = 0;
        JSONArray scanEANList= new JSONArray();
        try {

            String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME, "");
            String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME, "");

            query = "SELECT " + stock_table_name + ".quantity, "+stock_table_name+".barcode  FROM " + stock_table_name + " WHERE " + stock_table_name + ".barcode IN (" + selectedBarcodes + ") GROUP BY "+stock_table_name+".barcode  ;";
            Log.d("DBManager","getTotalQuantityOnlyBySelectedBarcodes "+query);
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    JSONObject tObj = new JSONObject();
                    tObj.put("scan","0");
                    tObj.put("barcode",cursor.getString(1));
                    tObj.put("qty",cursor.getString(0));
                    scanEANList.put(tObj);


                } while (cursor.moveToNext());
            }


            // closing connection
            cursor.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return scanEANList;
    }

    public  int getTotalQuantityBySelectedBarcodes(String selectedBarcodes)
    {
        String query = null;
        Cursor cursor = null;
        int totalQty = 0;
        try {

            String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME, "");
            String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME, "");

            query = "SELECT  SUM(" + stock_table_name + ".quantity) as total_qty  FROM " + stock_table_name + " WHERE " + stock_table_name + ".barcode IN (" + selectedBarcodes + ")  ;";
            //Log.d("DBManager","getQuantityByBarcodes11 "+query);
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    totalQty = Integer.parseInt(cursor.getString(0));


                } while (cursor.moveToNext());
            }


            // closing connection
            cursor.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return totalQty;
    }


}
