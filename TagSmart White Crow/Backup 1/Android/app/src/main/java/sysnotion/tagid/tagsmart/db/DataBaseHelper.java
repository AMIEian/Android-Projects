package sysnotion.tagid.tagsmart.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import sysnotion.tagid.tagsmart.utils.Constants;

public class DataBaseHelper extends SQLiteOpenHelper {
    // database version
    static final int DB_VERSION = 1;
    SharedPreferences pref;

    // table tagsmart_stock_details
    public String TABLE_TAGSMART_STOCK_DETAILS = "tagsmart_stock_details";
    public String TAGSMART_ID = "tagsmart_id";
    public String STOCK_ID = "stock_id";
    public String CUSTOMER_ID = "customer_id";
    public String EPC = "epc";
    public String BARCODE= "barcode";

    private final String CREATE_TABLE_TAGSMART_STOCK_DETAILS = "create table " + TABLE_TAGSMART_STOCK_DETAILS + "(" + TAGSMART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + STOCK_ID + " TEXT, "+CUSTOMER_ID+" TEXT, "+EPC+" TEXT, "+BARCODE+" TEXT);";

    // table epc_location_date_time
    public String TABLE_EPC_LOCATION_DATE_TIME = "epc_location_date_time";
    public String SLDT_ID = "sldt_id";
    public String ST_TAGSMART_ID = "tagsmart_id";
    public String LOCATION_ID = "location_id";
    public String SUB_LOCATION_ID = "sub_location_id";
    public String STOCK_DATE = "stock_date";
    public String STOCK_TIME= "stock_time";
    public String IS_SOLD = "is_sold";
    public String IS_SSE= "is_sse";

    private final String CREATE_TABLE_EPC_LOCATION_DATE_TIME = "create table " + TABLE_EPC_LOCATION_DATE_TIME + "(" + SLDT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ST_TAGSMART_ID + " TEXT, "+LOCATION_ID+" TEXT, "+SUB_LOCATION_ID+" TEXT, "+STOCK_DATE+" TEXT, "+STOCK_TIME+" TEXT, "+IS_SOLD+" TEXT, "+IS_SSE+" TEXT);";

    // table session_details
    public String TABLE_SESSION_DETAILS = "session_details";
    public String SESSION_ID = "session_id";
    public String SESSION_DATE = "session_date";
    public String SESSION_COUNT = "session_count";

    private final String CREATE_TABLE_SESSION_DETAILS = "create table " + TABLE_SESSION_DETAILS + "(" + SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SESSION_DATE+" TEXT, "+SESSION_COUNT+" INTEGER);";

    // table session_details
    public String TABLE_STOCK_DETAILS_CHECKSUM = "stock_details_checksum";
    public String SDC_ID = "sdc_id";
    public String SDC_EPC = "sdc_epc";
    private final String CREATE_TABLE_STOCK_DETAILS_CHECKSUM = "create table " + TABLE_STOCK_DETAILS_CHECKSUM + "(" + SDC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +SDC_EPC+"  TEXT);";

    // table stock Take checksum
    public String TABLE_STOCK_TAKE_CHECKSUM = "stock_take_checksum";
    public String STC_ID = "stc_id";
    public String STC_EPC = "stc_epc";
    public String STC_CATEGORY = "stc_category";

    private final String CREATE_TABLE_STOCK_TAKE_CHECKSUM = "create table " + TABLE_STOCK_TAKE_CHECKSUM + "(" + STC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +STC_EPC+" Text, "+STC_CATEGORY+" TEXT);";

    // table inward_data
    public String TABLE_INWARD_DATA = "inward_data";
    public String INWARD_ID = "inward_id";
    public String DISPATCH_ORDER_ID = "dispatch_order_id";
    public String ID_STORE_ID = "store_id";
    public String INWARD_DATE = "inward_date";
    public String ID_BARCODE = "barcode";
    public String ID_QUANTITY= "quantity";
    public String ID_RFID_QUANTITY = "rfid_quantity";

    private final String CREATE_TABLE_INWARD_DATA = "create table " + TABLE_INWARD_DATA + "(" + INWARD_ID + " TEXT, " + DISPATCH_ORDER_ID + " TEXT, "+ID_STORE_ID+" TEXT, "+INWARD_DATE+" TEXT, "+ID_BARCODE+" TEXT, "+ID_QUANTITY+" TEXT, "+ID_RFID_QUANTITY+" TEXT);";

    // table replenishment_data
    public String TABLE_REPLENISHMENT_DATA = "replenishment_data";
    public String REPLENISHMENT_ID = "replenishment_id";
    public String REP_STORE_ID = "store_id";
    public String REP_BARCODE = "barcode";
    public String REPLENISHMENT_QUANTITY= "replenishment_quantity";
    public String MAN_REPLENISHMENT_QUANTITY= "manual_replenishment_quantity";
    public String REPLENISHMENT_DATE = "replenishment_date";

    private final String CREATE_TABLE_REPLENISHMENT_DATA = "create table " + TABLE_REPLENISHMENT_DATA + "(" + REPLENISHMENT_ID + " TEXT, " + REP_STORE_ID + " TEXT, "+REP_BARCODE+" TEXT, "+REPLENISHMENT_QUANTITY+" TEXT, "+MAN_REPLENISHMENT_QUANTITY+" TEXT, "+REPLENISHMENT_DATE+" TEXT);";

    // table picking_data
    public String TABLE_PICKING_DATA = "picking_data";
    public String PICKING_ID = "picking_id";
    public String PICK_STORE_ID = "store_id";
    public String PICK_BARCODE = "barcode";
    public String PICKING_QUANTITY= "picking_quantity";
    public String PICK_STATUS= "status";
    public String PICKING_DATE = "pickup_date";

    private final String CREATE_TABLE_PICKING_DATA = "create table " + TABLE_PICKING_DATA + "(" + PICKING_ID + " TEXT, " + PICK_STORE_ID + " TEXT, "+PICK_BARCODE+" TEXT, "+PICKING_QUANTITY+" TEXT, "+PICK_STATUS+" TEXT, "+PICKING_DATE+" TEXT);";

    //table category master
    public String TABLE_CUSTOMER_CATEGORY_MASTER ="customer_category_master";
    public String  CC_ID = "cc_id";
    public String CATRGORY_CUSTOMER_ID="customer_id";
    public String CATEGORY_COLUMN_NAME="category_column_name";
    public String CATEGORY_LEVEL="category_level";
    private final String CREATE_TABLE_CUSTOMER_CATEGORY_MASTER = "create table " + TABLE_CUSTOMER_CATEGORY_MASTER + "(" + CC_ID + " TEXT, " + CATRGORY_CUSTOMER_ID + " TEXT, "+CATEGORY_COLUMN_NAME+" TEXT, "+CATEGORY_LEVEL+" TEXT);";



    private String CREATE_TABLE_STOCK_DATA ="";
    private String CREATE_TABLE_PRODUCT_DATA ="";

    public DataBaseHelper(Context context) {
        super(context, Constants.DB_NAME, null, DB_VERSION);
        pref = context.getSharedPreferences(Constants.PREFS_NAME, context.MODE_PRIVATE);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.e("DATABASE==>", "onCreate called");
        sqLiteDatabase.execSQL(CREATE_TABLE_TAGSMART_STOCK_DETAILS);
        sqLiteDatabase.execSQL(CREATE_TABLE_EPC_LOCATION_DATE_TIME);
        sqLiteDatabase.execSQL(CREATE_TABLE_SESSION_DETAILS);
        sqLiteDatabase.execSQL(CREATE_TABLE_STOCK_DETAILS_CHECKSUM);
        sqLiteDatabase.execSQL(CREATE_TABLE_INWARD_DATA);
        sqLiteDatabase.execSQL(CREATE_TABLE_REPLENISHMENT_DATA);
        sqLiteDatabase.execSQL(CREATE_TABLE_PICKING_DATA);
        sqLiteDatabase.execSQL(CREATE_TABLE_CUSTOMER_CATEGORY_MASTER);
        sqLiteDatabase.execSQL(CREATE_TABLE_STOCK_TAKE_CHECKSUM);
        //dynamically creating stock data since metadata is unknown and coming from server
        createStockTableFromMetadata();
        sqLiteDatabase.execSQL(CREATE_TABLE_STOCK_DATA);

        //dynamically creating product data since metadata is unknown and coming from server
        createProductTableFromMetadata();
        sqLiteDatabase.execSQL(CREATE_TABLE_PRODUCT_DATA);


    }

    //getting query string from sharedPreference response
    private void createStockTableFromMetadata()
    {
        try {
            String data = pref.getString(Constants.TABLE_METADATA_JSON, "");
            JSONObject jObj = new JSONObject(data);
            CREATE_TABLE_STOCK_DATA = jObj.getString("stock_table_sqlite_query");
        }
        catch(Exception e)
        {
            Log.e("createStockTable", e.getMessage());
        }
    }

    //getting query string from sharedPreference response
    private void createProductTableFromMetadata()
    {
        try {
            String data = pref.getString(Constants.TABLE_METADATA_JSON, "");
            JSONObject jObj = new JSONObject(data);
            CREATE_TABLE_PRODUCT_DATA = jObj.getString("product_table_sqlite_query");
        }
        catch(Exception e)
        {
            Log.e("createStockTable", e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if(newVersion > oldVersion)
            dropTables(sqLiteDatabase);
    }


    public void exportDB (){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/"+ "sysnotion.tagid.tagsmart" +"/databases/"+Constants.DB_NAME;
        String backupDBPath = Constants.DB_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Log.e("DATABASE==>", "EXPORTED");
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
            Log.e("DATABASE==>", "NOT EXPORTED");
        }
    }

    private void dropTables(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGSMART_STOCK_DETAILS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EPC_LOCATION_DATE_TIME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION_DETAILS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCK_DETAILS_CHECKSUM);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_INWARD_DATA);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_REPLENISHMENT_DATA);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PICKING_DATA);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER_CATEGORY_MASTER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCK_TAKE_CHECKSUM);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.CUSTOMER_STOCK_DATA_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

}


