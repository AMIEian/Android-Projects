package sysnotion.tagid.tagsmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import co.kr.bluebird.sled.BTReader;
import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;
import okhttp3.OkHttpClient;
import sysnotion.tagid.tagsmart.adapter.CategoryAdapter;
import sysnotion.tagid.tagsmart.adapter.InventoryAdapter;
import sysnotion.tagid.tagsmart.adapter.SearchAdapter;
import sysnotion.tagid.tagsmart.adapter.SubcategoryAdapter;
import sysnotion.tagid.tagsmart.db.DBManager;
import sysnotion.tagid.tagsmart.model.EPCBarcodeInventory;
import sysnotion.tagid.tagsmart.model.EPCInventory;
import sysnotion.tagid.tagsmart.model.Inventory;
import sysnotion.tagid.tagsmart.nLevel.NLevelAdapter;
import sysnotion.tagid.tagsmart.nLevel.NLevelItem;
import sysnotion.tagid.tagsmart.nLevel.NLevelView;
import sysnotion.tagid.tagsmart.nLevel.SomeObject;
import sysnotion.tagid.tagsmart.utils.Constants;
import sysnotion.tagid.tagsmart.utils.PermissionHelper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class InventoryStockTakeBActivity  extends AppCompatActivity {

    private static final String TAG = InventoryStockTakeBActivity.class.getSimpleName();

    SharedPreferences pref;
    private DBManager dbManager;
    JSONObject locationObject;
    TextView StoreIdShopTV, progressTV, precentTV, timeTV, totalScanTV, batteryStatusTV;
    Button inventoryScanStartBtn, finishScanBtn ;
    ListView listView;
    String treeJSON="";
    Button expandButton, collapseButton;
    ImageButton internetStatusIB;

    List<NLevelItem> list;
    NLevelAdapter adapter;

    int[] colors ;
    int counter=0;
    int parentId=-1;
    JSONArray arr;
    int maxLevel=0;
    ArrayList<String> selectedBarcodeList = new ArrayList<String>();

    int totalQty=0;
    int scannedQty = 0;
    String LastSelectedCategory ="";
    private SoundPool mSoundPool;
    private int mSoundId;
    private TimerTask mLocateTimerTask;
    private Timer mClearLocateTimer;

    private boolean mSoundFileLoadState;

    private InventoryStockTakeBActivity.SoundTask mSoundTask;

    //for storing category values
    java.util.HashMap<String,Integer> hashMapBarcode=new HashMap<String,Integer>();
    java.util.HashMap<String,Integer> hashMapBarcodePosition=new HashMap<String,Integer>();
    java.util.HashMap<String,Integer> hashMapBarcodeQty=new HashMap<String,Integer>();

    CopyOnWriteArrayList<EPCBarcodeInventory> categoryArray= new CopyOnWriteArrayList<EPCBarcodeInventory>();
    CopyOnWriteArrayList<EPCBarcodeInventory> shortListedEPCArray= new CopyOnWriteArrayList<EPCBarcodeInventory>();


    //timer
    private Timer scanTimer;
    int  time =0;
    int isTimerPause=0;
    public boolean timerstate = false;

    //reader
    private BTReader mReader;
    private final InventoryStockTakeBActivity.BarcodeHandler mBarcodeHandler = new InventoryStockTakeBActivity.BarcodeHandler(this);
    private boolean mMask = false;
    private boolean mInventory = false;
    private boolean mIsTurbo = true;
    private boolean mIgnorePC = true;
    private boolean mFile = false;

    int bt_notes_blink = 0;
    final Handler handlerButton = new Handler();
    String location_id="",isSessionNew="0";
    String sublocation_id="", sessionId="0";
    int scanFinished = 0;
    int uiUpdateFinished = 0;
    int totalScannedQty = 0;

    int isFinsihButtonPressed = 0;
    CopyOnWriteArrayList<String> uniqueEPCList= new CopyOnWriteArrayList<String>();
    InventoryStockTakeBActivity.ProcessEPCAndUpdateUIAsyncTask processEPCAndUpdateUIAsyncTask;
    JSONArray barcodePositionArray = new JSONArray();
    int lastLevel = 0;
    private ProgressDialog pDialog;
    String barcodesString= new String();
    int UIJSONLength=0;
    ArrayList<String> barcodeList = new ArrayList<String>();
    int EPCAnchorPosition=0;

    //New code
    ArrayList<String> dataBarcodeList = new ArrayList<String>();
    ArrayList<String> dataEPCList = new ArrayList<String>();

    //New code

    /*//test
    int testDataCounter = 0;
    String testEPC[] ={"30361F84CC3BA380000007D9",
"30361FAF541C59800000003E",
        "30361F84CC3BA38000000310",
        "30361F84CC3BB480000003D5",
        "30361F84CC1BF4C000000041",
        "30361F84CC3F398000000156",
        "30361F84CC5441800000008E",
        "30361F84CC3BBE40000F91DF",
        "30361F84CC32E640000000B0",
        "30361F84CC404200000001DE",
        "30361F84CC3BA30000000001",
        "30361F84CC1D36000000027A",
        "30361FAF540FB58000000386",
        "30361F84CC364D40000F920F",
        "30361F84CC3BBA000000012B",
        "30361F84CC3BB38000000838",
        "30361F84CC4F34C0000003E0",
        "30361F84CC3BBC0000000035",
        "30361F84CC4042C00000017A",
        "30361F84CC3F7BC0000001B1",
        "30361F84CC3F86C000000040",
        "30361F84CC5DD540000007D8",
        "30361F84CC3F95400000016D",
        "30361F84CC3F3A00000005E5",
        "30361F84CC3BBD800000011C",
        "30361F84CC60D040000000E5",
        "30361FAF540FB540000F9215",
        "30361F84CC18FDC0000001C1",
        "30361F84CC455CC0000000EB",
        "30161FAF54670600000000EB",
        "30361F84CC1D7E0000000275",
        "30161FAF549C45000000032D",
        "30361FAF549C69C00000017D",
        "30361F84CC40670000000089",
        "30361F84CC3BA340000002BB",
        "30361F84CC5CAAC00000085C",
        "30361FAF541C5A800000045A",
        "30361F84CC1A36C0000000A4",
        "30361F84CC3BA340000001B9",
        "30361F84CC1D518000000169",
        "30361FABEC0C4B40000F91D1",
        "30361F84CC1929C00000016B",
        "30361FABEC0C4940000F9233",
        "30361F84CC364F80000F9227",
        "30361F84CC3BB700000F91CF",
        "30361F84CC3BB88000000018",
        "30361FABEC2D27000000011E",
        "30361F84CC1DB1000000028F",
        "30361F84CC3BBC400000071C",
        "30361F84CC3F868000000305",
        "30361F84CC5AFE000000006E",
        "30361F84CC191800000000C1",
        "30361F84CC3BBE4000000159",
        "30361F84CC3BB8C00000020D",
        "30361F84CC3BA2C0000005B3",
        "30361F84CC3BA38000000334",
        "30361F84CC3F040000000287",
        "30161FAF5466F68000000066",
        "30361FAF545D1A80000F91EC",
        "30361FAF541C5A0000000507",
        "30361F84CC1E7C40000006F4",
        "30361F84CC3BA380000007CF",
        "30361F84CC3BA38000000338",
        "30361F84CC1918400000003F",
        "30361F84CC1CB08000000480",
        "30361F84CC3BB70000000150",
        "30161F84CE834200000000AE",
        "30361F84CC3F71C000000204",
        "30361F84CC40654000000692",
        "30361F84CC364FC0000F924B",
        "30361F84CC3F8A40000001E1",
        "30361F84CE792040000000B3",
        "30361F84CC5AE9C00000003D",
        "30361F84CC409D8000000181",
        "30361F84CC1D3AC000000264",
        "30361F84CC3BB84000000534",
        "30361F84CC3BB68000000723",
        "30361F84CC406C0000000ECD",
        "30361F84CC192A0000000253",
        "30361F84CC1BFA00000004EC",
        "30361FAF54E0FD40000001E7",
        "30361F84CC19188000000226",
        "30361F84CC3BB74000000227",
        "30361F84CC3BB840000003A2",
        "30361F84CC3BB380000002A1",
        "30361F84CC406A8000000638",
        "30361FAF544EEBC000000356",
        "30361F84CC3BB380000006B7",
        "30361F84CC1D99C000000200",
        "30361F84CC409D4000000042",
        "30361F84CC3BB34000000666",
        "30361F84CC364CC0000F9232",
        "30161FAF5466F08000000266",
        "30361F84CC3F6B0000000139",
        "30361FAF541FF18000000266",
        "30361F84CC1CB180000000D9",
        "30361F84CC365200000F91F5",
        "30361F84CC1A29C000000075",
        "30361F84CC468FC0000F91FD",
        "30361F84CC3D4B4000000096",
        "30361FAF540FB0C000000740",
        "30361F84CC4932C0000F91DA",
        "30361F84CC3F044000000075",
        "30361FABEC3DAA4000000466",
        "30361F84CC3BBAC00000026F",
        "30361F84CC3D4A80000F9204",
        "30361FAF54188400000F91DE",
        "30361F84CC3D49C00000085A",
        "30361FABEC0C4CC0000F9209",
        "30361F84CC3F3C80000F9214",
        "30361F84CC364C80000F923C",
        "30361F84CC3BA38000000353",
        "30361F84CE78E740000001B4",
        "30361F84CC406780000004E4",
        "30361F84CC406580000006F9",
        "30361F84CC19070000000220",
        "30361F84CC1DB300000F91D7",
        "30361F84CC3F3EC000000288",
        "30361F84CC46E08000000137",
        "30361F84CC3F79C000000181",
        "30161F84CE7516C0000001CE",
        "30361F84CC1D8A80000000F3",
        "30361F84CC3BA2C000000333",
        "30361F84CC1D4B40000003C6",
        "30361F84CC3BBAC0000000D5",
        "30161F84CE83498000000355",
        "30361FAF54392980000006D0",
        "30361F84CC468FC0000003FA",
        "30361F84CC18FE40000000BA",
        "30161F84CE83420000000063",
        "30361F84CC5AFDC00000007B",
        "30361F84CE7918C000000375",
        "30361F84CC364CC0000F91FB",
        "30361F84CC3BA340000003BC",
        "30361F84CC3F3C40000002D3",
        "30361F84CC3F0BC0000001F2",
        "30361F84CC42D940000001D2",
        "30361FAF549C63C0000001E2",
        "30361F84CC3BB380000F921D",
        "30361F84CC5B014000000242",
        "30361F84CC3BBD4000000208",
        "30361F84CC5AE2C000000021",
        "30361F84CC3F7C0000000264",
        "30361F84CC46908000000179",
        "30361FABEC3DA6C0000F91DC",
        "30361FAF54188400000F920C",
        "30361F84CC3F15C0000003A2",
        "30361F84CC42A6C0000F9220",
        "30361F84CC3BB38000000246",
        "30361F84CC364C80000F91F8",
        "30361F84CC191800000000EA",
        "30361F84CC3F938000000006",
        "30361F84CC364FC0000F9206",
        "30361F84CC3BBAC000000392",
        "30361F84CC3D49C00000083F",
        "30361F84CC4067C000000811",
        "30361F84CC54414000000401",
        "30361F84CC19FBC00000008C",
        "30361F84CC3EF6800000007D",
        "30361F84CC3BBC4000000701",
        "30361F84CC1FCC0000000080",
        "30361F84CC3BA18000000009",
        "30361F84CC1D51800000013C",
        "30361F84CC3BA380000007DB",
        "30361F84CC5441C0000001AC",
        "30361F84CC3F4080000F91D9",
        "30361F84CC3BA3400000067B",
        "30361F84CC4F340000000236",
        "30361F84CC364C80000F9210",
        "30361F84CC40670000000DA4",
        "30361F84CC3BA34000000312",
        "30361F84CC5AE300000001BF",
        "30361F84CC3BA3C000000010",
        "30361F84CC4069C000000AAB",
        "30161F84CE834D00000000E8",
        "30361F84CC5AF840000F91FA",
        "30361F84CC3BBD4000000002",
        "30361F84CC1D6940000F9222",
        "30361F84CC3BB880000000B9",
        "30361F84CC3BA1C00000047E",
        "30361F84CC1D3FC0000003E2",
        "30361F84CC364F80000F91C6",
        "30361F84CC3BBC400000035E",
        "30361F84CC3BCC00000000AA",
        "30361FAF540FB180000001E3",
        "30361F84CC364C80000F921C",
        "30361FAF541FF1C0000002C1",
        "30361F84CC304A00000002A3",
        "30361F84CC3F3BC000000374",
        "30361F84CC365180000F9231",
        "30361F84CC1D6C00000000BC",
        "30361F84CC1DB100000F9216",
        "30361F84CC5CAB000000038D",
        "30361F84CC3BBEC0000000BE",
        "30361FAF541FF1800000016A",
        "30361F84CC3BBC8000000279",
        "30361F84CC3F8B00000001AD",
        "30361F84CC406C0000000ED0",
        "30361F84CC364CC0000F9212",
        "30361F84CC3BA34000000225",
        "30361F84CC406C0000000ECF",
        "30361FAF549C548000000366",
        "30361F84CC3080000000047E",
        "30361F84CC3F6B4000000484",
        "30361F84CC3F7DC00000011B",
        "30361F84CC193FC000000097",
        "30361F84CC3BB6C00000029C",
        "30361F84CC5AE04000000292",
        "30361F84CC3BBD40000001C1",
        "30361F84CC5CAB400000021B",
        "30361FAF5439298000000125",
        "30361FAF540FB5400000056A",
        "30161F84CE834040000001C2",
        "30361FABEC582EC0000F9243",
        "30361F84CC3BB38000000834",
        "30361FABEC582F00000F923E",
        "30361F84CC3BCC80000006E4",
        "30361F84CC006FC00000042E",
        "30361F84CC3F8A00000003FF",
        "30361FABEC2D27800000016C",
        "30361FAF544EE280000F924F",
        "30361F84CC469040000005CE",
        "30361F84CC406740000F922E",
        "30361F84CC421CC000000069",
        "30361F84CC365200000F9219",
        "30361F84CC364D40000F9223",
        "30361FAF541FE3C0000F91E6",
        "30361F84CC4069C00000092C",
        "30361F84CC406D0000000614",
        "30361FABEC0C4940000F91C3",
        "30361F84CC3BB50000000110",
        "30361F84CC1906800000026A",
        "30361FAF54182600000F924A",
        "30361F84CC365240000F922F",
        "30361F84CC4067400000050E",
        "30361F84CC3BB4C0000F91FE",
        "30361F84CC40BD0000000211",
        "30361F84CC40670000000D2C",
        "30361F84CC3653C00000029C",
        "30361F84CC364F80000F91D6",
        "30361F84CC4F348000000041",
        "30361F84CC3BCB4000000020",
        "30161F84CE445180000000D7",
        "30361F84CC3D0900000004AB",
        "30361FAF540FB58000000177",
        "30361F84CC3F9FC000000250",
        "30361F84CC5AF78000000030",
        "30361F84CC4041C0000000F6",
        "30361F84CC3BA14000000609",
        "30161F84CE8343800000020D",
        "30161FAF54670440000002AA",
        "30361F84CC3BBE4000000035",
        "30361FAF540FB0C0000000AA",
        "30361FAF54673D40000083C7",
        "30361F84CC364D00000F91C2",
        "30361F84CC406800000006AD",
        "30361F84CC3BB4800000008C",
        "30361F84CC40678000000525",
        "30361F84CC421C40000002C9",
        "30161FAF5467000000000461",
        "30361F84CC3D4B00000F91D5",
        "30361F84CC3BCC8000000625",
        "30361F84CC364CC0000F91F9",
        "30361F84CC364D00000F91F4",
        "30361F84CC190680000000C1",
        "30361F84CC5AFD40000000DE",
        "30361F84CC3F1B0000000309",
        "30361F84CC4064C000000AAC",
        "30361FAF541FF1C0000001F6",
        "30161F84CE834040000004D7",
        "30361F84CC3EE3C0000000E6",
        "30361F84CC3BA2400000006D",
        "30361F84CC3BB780000007AB",
        "30361F84CC1D5100000F91FC",
        "30361F84CC3F38C000000181",
        "30361F84CC3BB5400000081A",
        "30161FAF5467078000000085",
        "30361F84CC1A1EC0000000A4",
        "30361F84CC3BBA000000005C",
        "30161F84CE444B8000000A45",
        "30361F84CC5442000000009E",
        "30361F84CC5CAAC0000007C0",
        "30361F84CC3BA40000000273",
        "30361F84CC364FC0000F9234",
        "30361F84CC3F3C80000F922A",
        "30361F84CC3BA34000000787",
        "30361F84CC3BBE4000000125",
        "30361F84CC3FAE000000002A",
        "30361FAF540FB54000000443",
        "30361F84CC3F514000000289",
        "30361F84CC455BC0000000AE",
        "30361F84CC40AFC00000018A",
        "30361F84CC406C0000000EA1",
        "30361F84CC3F8CC0000000C3",
        "30361F84CC3F6B800000057E",
        "30361FABEC0C3A00000F91CE",
        "30361F84CC192A800000003C",
        "30361F84CC191880000000F5",
        "30361F84CC3D0A000000061A",
        "30361F84CC406500000001EC",
        "30361F84CC364F80000F9205",
        "30361F84CC3BB38000000C4A",
        "30361F84CC3F6BC000000122",
        "30361F84CC3BB780000F9203",
        "30361F84CC364FC0000F91EA",
        "30361F84CC3D0E400000030F",
        "30361F84CC365200000F91ED",
        "30361FAF540FB3C0000000B8",
        "30361F84CC3F42C000000080",
        "30361F84CC3BB9C0000002D6",
        "30361F84CC3F3C00000000D2",
        "30361F84CC406C0000000EC7",
        "30361F84CC3F418000000603",
        "30361F84CC46904000000068",
        "30361F84CC406CC000000655",
        "30361F84CC468740000000BF",
        "30361FAF540FB480000001D3",
        "30361F84CC364DC0000F91F0",
        "30361F84CC1FCD40000F91F7",
        "30361F84CC468FC000000081",
        "30361F84CC40710000000153",
        "30361F84CC3BB740000F9217",
        "30161F84CE75424000000185",
        "30361F84CC5AE5400000023F",
        "30361FAF543927C0000002EC",
        "30361F84CC40414000000151",
        "30161F84CE444B80000004B1",
        "30361F84CC3D0E800000074C",
        "30361F84CC40650000000472",
        "30361F84CC3BCE000000007E",
        "30361F84CC3BBC400000074D",
        "30361F84CC3BA140000005C0",
        "30161F84CE4449C0000002BB",
        "30161F84CE1F4F8000000729",
        "30361F84CC3BB64000000644",
        "30361F84CC18FDC0000001A5",
        "30361F84CC4F38C0000003C3",
        "30361F84CC364D00000F9242",
        "30361F84CC3F91C00000007A",
        "30161FAF549CE9C000000087",
        "30361FAF544EE2400000012B",
        "30361F84CC3FAD800000011C",
        "30361F84CC3BBC4000000373",
        "30361F84CC3BA340000002D3",
        "30361FAF542EA200000020D4",
        "30361F84CC365000000F91CA",
        "30161FAF54670BC000000092",
        "30361F84CC3BB40000000408",
        "30361F84CC3D4780000008CB",
        "30361F84CC406C80000004D7",
        "30361FAF549C548000000359",
        "30361F84CC3D0E00000004EB",
        "30361F84CC3BBE40000003CD",
        "30361F84CC3BA1C0000003A0",
        "30361F84CC455D0000000229",
        "30361F84CC406D4000000414",
        "30361FAF543927C000000259",
        };
        */




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_stock_take_a);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        dbManager = new DBManager(InventoryStockTakeBActivity.this);
        dbManager.open();
        dbManager.turcateStockTakeChecksum();

        pDialog = new ProgressDialog(InventoryStockTakeBActivity.this);

        sessionId = Constants.getSessionId(InventoryStockTakeBActivity.this);
        isSessionNew = "-1";
        // Log.d("InventoryA","sessionId "+sessionId);
        internetStatusIB=(ImageButton) findViewById(R.id.internetStatusIB); //written by raj on 01-09-21

        batteryStatusTV =(TextView) findViewById(R.id.batteryStatusTV);
        StoreIdShopTV =(TextView) findViewById(R.id.StoreIdShopTV);
        totalScanTV =(TextView) findViewById(R.id.totalScanTV);
        totalScanTV.setText("0");

        try
        {
            locationObject = new JSONObject(getIntent().getStringExtra("location_JSON"));
            StoreIdShopTV.setText("Store ID: "+pref.getString(Constants.STORE_NUMBER,"0")+" / "+locationObject.getString("location")+" - "+locationObject.getString("sublocation"));
            location_id=locationObject.getString("location_id");
            sublocation_id=locationObject.getString("sub_location_id");

            // Log.v("InventoryA","Location JSON "+locationObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        lastLevel = Integer.parseInt(pref.getString(Constants.MAX_LEVEL,"0"));

        //Log.d("InventoryA","lastLevel "+lastLevel);
        selectedBarcodeList =  Constants.getArrayList(Constants.CATEGORY_ARRAY_JSON, InventoryStockTakeBActivity.this);
        colors= getResources().getIntArray(R.array.colors_hex_code);

        if(Constants.isNetworkAvailable(InventoryStockTakeBActivity.this)) {
            new InventoryStockTakeBActivity.LoadTreeAsyncTask(InventoryStockTakeBActivity.this).execute(Constants.DOMAIN_URL+"product_tree_by_barcode.php");
        }
        else
        {
            Constants.internetAlert(InventoryStockTakeBActivity.this);
        }

        progressTV =(TextView) findViewById(R.id.progressTV);


        precentTV =(TextView) findViewById(R.id.precentTV);

        timeTV =(TextView) findViewById(R.id.timeTV);
        timeTV.setText("00:00:00");


        expandButton =(Button) findViewById(R.id.expandButton);
        collapseButton =(Button) findViewById(R.id.collapseButton);

        collapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new InventoryStockTakeBActivity.ExpandCollapseAllAsyncTask(InventoryStockTakeBActivity.this,false).execute();
            }
        });

        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new InventoryStockTakeBActivity.ExpandCollapseAllAsyncTask(InventoryStockTakeBActivity.this,true).execute();
            }
        });

        inventoryScanStartBtn =(Button) findViewById(R.id.inventoryScanStartBtn);
        finishScanBtn =(Button) findViewById(R.id.finishScanBtn);


        inventoryScanStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalScanTV.setText("0");
                uniqueEPCList.clear();
                totalScannedQty = 0;
                scanFinished = 0;
                uiUpdateFinished = 0;
                inventoryScanStartBtn.setEnabled(false);
                bt_notes_blink = 0;
                isFinsihButtonPressed = 0;

                //button blink
                handlerButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int DrawableImage[] = {R.drawable.circle_dark, R.drawable.circle_light};
                                Resources res = getApplicationContext().getResources();
                                inventoryScanStartBtn.setCompoundDrawablesWithIntrinsicBounds(res.getDrawable(DrawableImage[bt_notes_blink]), null, null,null );
                                inventoryScanStartBtn.setCompoundDrawablePadding(4);
                                bt_notes_blink++;
                                if (bt_notes_blink == 2) { bt_notes_blink = 0; }
                                handlerButton.postDelayed(this, 500);
                            }
                        });
                    }
                }, 0);



                int ret;
                // starting timer
                if(timerstate == true) {
                    scanTimer.cancel();
                    scanTimer.purge();
                    time =0;
                    timerstate =false;
                }
                else
                {
                    scanTimer = new Timer();
                    scanTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            TimerMethod();
                        }

                    }, 0, 1000);
                }

                if (!mInventory) {
                    //set mode
                    mReader.SD_SetTriggerMode(SDConsts.SDTriggerMode.RFID);
                    //setting max power
                    mReader.RF_SetRadioPowerState(30);
                    ret = mReader.RF_PerformInventoryWithLocating(mIsTurbo, mMask, mIgnorePC);
                    if (ret == SDConsts.RFResult.SUCCESS) {
                        startStopwatch();
                        mInventory = true;




                    } else if (ret == SDConsts.RFResult.MODE_ERROR)
                        Toast.makeText(InventoryStockTakeBActivity.this, "Start Inventory failed, Please check RFR900 MODE", Toast.LENGTH_SHORT).show();
                    else if (ret == SDConsts.RFResult.LOW_BATTERY)
                        Toast.makeText(InventoryStockTakeBActivity.this, "Start Inventory failed, LOW_BATTERY", Toast.LENGTH_SHORT).show();
                    else
                        Log.d(TAG, "Start Inventory failed");
                }

            }
        });

        finishScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log.d(TAG, "shortListedEPCArray size "+shortListedEPCArray.size());

                final int ret = mReader.RF_StopInventory();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        InventoryStockTakeBActivity.this);

                // set title
                alertDialogBuilder.setTitle("Select Action");

                // set dialog message
                alertDialogBuilder
                        .setMessage("What do you want?")
                        .setCancelable(false)
                        .setPositiveButton("Abandon Scan", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                scanFinished = 1;
                                uiUpdateFinished = 0;
                                shortListedEPCArray.clear();
                                totalScanTV.setText("0");
                                uniqueEPCList.clear();
                                totalScannedQty = 0;
                                dbManager.turcateStockTakeChecksum();
                                handlerButton.removeCallbacksAndMessages(null);
                                inventoryScanStartBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null,null );

                                finishScanBtn.setEnabled(false);
                                //resetting timer
                                time =0;
                                scanTimer.cancel();
                                scanTimer.purge();
                                timerstate =false;

                                if(Constants.isNetworkAvailable(InventoryStockTakeBActivity.this)) {
                                    new InventoryStockTakeBActivity.RollbackStockTakeAsyncTask(InventoryStockTakeBActivity.this).execute(Constants.BASE_URL+"RollBackStockTake.php");
                                }
                                else
                                {
                                    Constants.internetAlert(InventoryStockTakeBActivity.this);
                                }

                                if (ret == SDConsts.RFResult.SUCCESS || ret == SDConsts.RFResult.NOT_INVENTORY_STATE) {
                                    mInventory = false;
                                    pauseStopwatch();
                                    //UI Update
                                    //resetFilterBtn.performClick();

                                    // Log.d("tag", "Response is " + result.toString());
                                    //enabling scan and stop scan button
                                    inventoryScanStartBtn.setEnabled(true);
                                    finishScanBtn.setEnabled(true);

                                } else if (ret == SDConsts.RFResult.STOP_FAILED_TRY_AGAIN)
                                    Toast.makeText(InventoryStockTakeBActivity.this, "Stop Inventory failed", Toast.LENGTH_SHORT).show();

                                dialog.cancel();


                            }
                        })
                        .setNegativeButton("Submit Stock Take", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                scanFinished = 1;
                                uiUpdateFinished = 1;
                                isFinsihButtonPressed = 1;
                                handlerButton.removeCallbacksAndMessages(null);
                                inventoryScanStartBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null,null );

                                finishScanBtn.setEnabled(false);
                                //resetting timer
                                time =0;
                                scanTimer.cancel();
                                scanTimer.purge();

                                if (ret == SDConsts.RFResult.SUCCESS || ret == SDConsts.RFResult.NOT_INVENTORY_STATE) {
                                    mInventory = false;
                                    pauseStopwatch();
                                    inventoryScanStartBtn.setEnabled(true);
                                    finishScanBtn.setEnabled(true);

                                    if(Constants.isNetworkAvailable(InventoryStockTakeBActivity.this)) {
                                        new InventoryStockTakeBActivity.ServerUpdateAsyncTask().execute(Constants.BASE_URL + "UpdateStockTake.php");
                                    }
                                    else
                                    {
                                        Constants.internetAlert(InventoryStockTakeBActivity.this);
                                    }

                                } else if (ret == SDConsts.RFResult.STOP_FAILED_TRY_AGAIN)
                                    Toast.makeText(InventoryStockTakeBActivity.this, "Stop Inventory failed", Toast.LENGTH_SHORT).show();

                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }


        });



    }
    private void resetRFIDScanQTY()
    {
        for (int i = 0; i < list.size(); i++) {

            list.get(i).setScanQty(0);
        }
    }

    private void NLevelExpandableListView(){

        //Log.d("StockTakeOptionA","MaxLevel "+maxLevel);
        barcodeList.clear();

        listView = (ListView) findViewById(R.id.listView1);
        list = new ArrayList<NLevelItem>();
        final LayoutInflater inflater = LayoutInflater.from(this);
        nestedLoop(treeJSON, null, inflater, 0);

        adapter = new NLevelAdapter(list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                ((NLevelAdapter)listView.getAdapter()).toggle(arg2);
                ((NLevelAdapter)listView.getAdapter()).getFilter().filter();
            }
        });



    }

    private void nestedLoop(String levelList, NLevelItem nLevelItem, final LayoutInflater inflater, int level){

        try{
            maxLevel = level;
            JSONArray jsonArrayStringList = new JSONArray(levelList);
            int length = jsonArrayStringList.length();
            for (int i=0; i<length; i++){

                JSONObject itemObject = jsonArrayStringList.getJSONObject(i);

                //System.out.println("category0000  "+itemObject.getString("name"));
                if(itemObject.has("children"))
                {

                    int childrenSize = itemObject.getJSONArray("children").length();
                    //System.out.println("category111  "+itemObject.getString("name")+" no.of child "+childrenSize);

                    NLevelItem Parent = itemView(counter,i, itemObject.getString("name"), nLevelItem, inflater, level, !(childrenSize>0));
                    if(level==0)
                    {
                        parentId = -1;
                        //System.out.println("parent ID11 "+parentId);
                    }
                    else
                    {
                        parentId = nLevelItem.getId();
                        // System.out.println("parent ID22 "+parentId);
                    }

                    Parent.setParentId(parentId);
                    Parent.setId(counter);
                    Parent.setLevel(level);
                    Parent.setTotalQty(0);
                    Parent.setScanQty(0);
                    //  Parent.toggle();
                    list.add(Parent);
                    if(level == (lastLevel -1 ))//i.e. maxlevel  -1
                    {

                        //System.out.println("## no child found ############ "+((SomeObject)Parent.getWrappedObject()).getName());
                        if( ((SomeObject) Parent.getWrappedObject()).getName().trim().length() > 0)
                        {
                            hashMapBarcodePosition.put((String) ((SomeObject) Parent.getWrappedObject()).getName(),counter);
                            hashMapBarcode.put((String) ((SomeObject) Parent.getWrappedObject()).getName(),0);

                            barcodeList.add((String) ((SomeObject) Parent.getWrappedObject()).getName());
                        }

                    }
                    parentId = counter;
                    counter++;

                    if(childrenSize>0){
                        nestedLoop(itemObject.getJSONArray("children").toString(), Parent, inflater, level+1);
                    }
                }
                else
                {
                    NLevelItem Parent = list.get(list.size() -1);
                    Parent.setTotalQty(Integer.parseInt(itemObject.getString("name")));
                    hashMapBarcodeQty.put((String) ((SomeObject) Parent.getWrappedObject()).getName(),Integer.parseInt(itemObject.getString("name")));
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private NLevelItem itemView(final int position,final int itemRow, final String Title, final NLevelItem nLevelItem, final LayoutInflater inflater, final int level, final boolean isLast){

        NLevelItem superChild = new NLevelItem(new SomeObject(Title), nLevelItem, new NLevelView() {
            @Override
            public View getView(final  NLevelItem item) {
                View view = inflater.inflate(R.layout.nlevel_list_item_without_chkbx, null);
                TextView tv = (TextView) view.findViewById(R.id.textView);
                String name = (String) ((SomeObject) item.getWrappedObject()).getName();

                tv.setText(name);

                TextView tv2 = (TextView) view.findViewById(R.id.qtytextView);
                tv2.setText(item.getScanQty()+" / "+item.getTotalQty());

                LinearLayout listItemContainer = (LinearLayout)view.findViewById(R.id.listItemContainer);
                listItemContainer.setBackgroundColor(colors[level]);

                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) tv.getLayoutParams();
                mlp.setMargins(level*50, 5, 5, 5);

                if(isLast){
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(InventoryStockTakeBActivity.this, "Clicked on: "+Title, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                return view;
            }
        });

        return superChild;
    }



    private void createSoundPool() {
        boolean b = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            b = createNewSoundPool();
        else
            b = createOldSoundPool();


        if (b) {
            AudioManager audioManager = (AudioManager)  getSystemService( AUDIO_SERVICE);
            float actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            Constants.mSoundVolume = actVolume / maxVolume;
            SoundLoadListener();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean createNewSoundPool(){
        AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
        mSoundPool = new SoundPool.Builder().setAudioAttributes(attributes).setMaxStreams(5).build();
        if (mSoundPool != null)
            return true;
        return false;
    }

    @SuppressWarnings("deprecation")
    private boolean createOldSoundPool(){
        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        if (mSoundPool != null)
            return true;
        return false;
    }

    private void SoundLoadListener() {
        if (mSoundPool != null) {
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    // TODO Auto-generated method stub
                    mSoundFileLoadState = true;
                }
            });
            mSoundId = mSoundPool.load(InventoryStockTakeBActivity.this, R.raw.beep, 1);
        }
    }

    private class SoundTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (Constants.mSoundPlay) {
                try {
                    if (mSoundFileLoadState) {
                        if (mSoundPool != null) {
                            mSoundPool.play(mSoundId, Constants.mSoundVolume, Constants.mSoundVolume, 0, 0, (48000.0f / 44100.0f));
                            try {
                                Thread.sleep(25);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (java.lang.NullPointerException e) {
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    };
    private void startLocateTimer() {
        stopLocateTimer();

        mLocateTimerTask = new TimerTask() {
            @Override
            public void run() {

            }
        };
        mClearLocateTimer = new Timer();
        mClearLocateTimer.schedule(mLocateTimerTask, 500);
    }

    private void stopLocateTimer() {
        if (mClearLocateTimer != null ) {
            mClearLocateTimer.cancel();
            mClearLocateTimer = null;
        }
    }

    @Override
    protected void onStart() {
        createSoundPool();
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        boolean openResult = false;
        mReader = BTReader.getReader(this, mBarcodeHandler);
        if (mReader != null)
            openResult = mReader.SD_Open();
        if (openResult == SDConsts.RF_OPEN_SUCCESS) {
            if (mReader.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED)
            {
                int value = mReader.SD_GetBatteryStatus();
                batteryStatusTV.setText(value+" % ");
                Toast.makeText(getBaseContext(), mReader.BT_GetConnectedDeviceName() + "\n" + mReader.BT_GetConnectedDeviceAddr(), Toast.LENGTH_SHORT).show();
            }
            else
                showMessageDialogWithBluetoothActivity("Bluetooth Status", "Bluetooth not connected");

        }
        mInventory = false;


        super.onStart();
    }

    public static String leftPad(String str, int length, String padChar){
        String pad = "";
        for (int i = 0; i < length; i++) {
            pad += padChar;
        }
        return pad.trim()+ str.trim();
    }

    @Override
    public void onStop() {
        mReader.RF_StopInventory();
        mInventory = false;

        if (mSoundPool != null)
            mSoundPool.release();
        mSoundFileLoadState = false;

        if (processEPCAndUpdateUIAsyncTask != null) {
            processEPCAndUpdateUIAsyncTask.cancel(true);
        }

        scanFinished =1;
        dismissProgressDialog();
        dbManager.close();

        super.onStop();
    }


    private static class BarcodeHandler extends Handler {
        private final WeakReference<InventoryStockTakeBActivity> mExecutor;
        public BarcodeHandler(InventoryStockTakeBActivity f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            InventoryStockTakeBActivity executor = mExecutor.get();
            if (executor != null) {
                executor.handleInventoryHandler(msg);
            }
        }
    }

    public void handleInventoryHandler(Message m) {
        // Log.d(TAG, "mInventoryHandler");
        //Log.d(TAG, "m arg1 = " + m.arg1 + " arg2 = " + m.arg2);
        switch (m.what) {
            case SDConsts.Msg.SDMsg:
                switch(m.arg1) {
                    case SDConsts.SDCmdMsg.TRIGGER_PRESSED:

                        break;

                    case SDConsts.SDCmdMsg.SLED_INVENTORY_STATE_CHANGED:

                        SpannableString spannablecontent=new SpannableString(" "+m.arg2+" % ");
                        spannablecontent.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.red)),
                                0,spannablecontent.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        batteryStatusTV.setText(spannablecontent);
                        break;

                    case SDConsts.SDCmdMsg.TRIGGER_RELEASED:

                        break;
                    //You can receive this message every a minute. SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED
                    case SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED:
                        //Toast.makeText(InventoryStockTakeBActivity.this, "Battery state = " + m.arg2, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Battery state = " + m.arg2);
                        batteryStatusTV.setText(m.arg2+" %");
                        break;
                    case SDConsts.SDCmdMsg.SLED_HOTSWAP_STATE_CHANGED:
                        if (m.arg2 == SDConsts.SDHotswapState.HOTSWAP_STATE)
                            Toast.makeText(InventoryStockTakeBActivity.this, "HOTSWAP STATE CHANGED = HOTSWAP_STATE", Toast.LENGTH_SHORT).show();
                        else if (m.arg2 == SDConsts.SDHotswapState.NORMAL_STATE)
                            Toast.makeText(InventoryStockTakeBActivity.this, "HOTSWAP STATE CHANGED = NORMAL_STATE", Toast.LENGTH_SHORT).show();

                        break;
                }
                break;

            case SDConsts.Msg.RFMsg:
                switch(m.arg1) {
                    case SDConsts.RFCmdMsg.INVENTORY:
                    case SDConsts.RFCmdMsg.READ:
                        if (m.arg2 == SDConsts.RFResult.SUCCESS) {
                            String EPCNumber = new String();
                            try {
                                if (m.obj != null && m.obj instanceof String) {
                                    String data = (String) m.obj;
                                    if (data != null && isFinsihButtonPressed == 0) {
                                        if (data.contains(";")) {

                                            //full tag example(with optional value)
                                            //1) RF_PerformInventory => "3000123456783333444455556666;rssi:-54.8"
                                            //2) RF_PerformInventoryWithLocating => "3000123456783333444455556666;loc:64"
                                            int infoTagPoint = data.indexOf(';');
                                            data = data.substring(0, infoTagPoint);
                                            //Log.d(TAG, "data tag = " + data);
                                            EPCNumber = data.trim();


                                           /* //test
                                            if (testDataCounter == (testEPC.length - 1))
                                                testDataCounter = 0;

                                            EPCNumber = testEPC[testDataCounter];
                                            testDataCounter++;
                                            ////// test over*/


                                            if (uniqueEPCList.addIfAbsent(EPCNumber)) {
                                                // System.out.println("ProcessEPCAndUpdateUIAsyncTask EPCNumber " + EPCNumber);
                                                // System.out.println("ProcessEPCAndUpdateUIAsyncTask testDataCounter " + testDataCounter);
                                                if (EPCNumber.length() == 24) {
                                                   // String barcode = Constants.convertEPCToBarcode(EPCNumber);


                                                    //System.out.println("ProcessEPCAndUpdateUIAsyncTask barcode " + barcode);
                                                    String barcode = "";
                                                    if (dataEPCList.contains(EPCNumber)) {

                                                        barcode = dataBarcodeList.get(dataEPCList.indexOf(EPCNumber));

                                                        if (hashMapBarcode.containsKey(barcode)) {
                                                            int value = hashMapBarcode.get(barcode);
                                                            value = value + 1;
                                                            hashMapBarcode.put(barcode, value);
                                                            //System.out.println("ProcessEPCAndUpdateUIAsyncTask rfid_qty " + value);

                                                            if (hashMapBarcodePosition.containsKey(barcode)) {
                                                                scannedQty = scannedQty + 1;
                                                                int position = hashMapBarcodePosition.get(barcode);

                                                                if (list.size() >= position) {

                                                                    //System.out.println("ProcessEPCAndUpdateUIAsyncTask position " + position);

                                                                    NLevelItem Parent = list.get(position);

                                                                    Parent.setScanQty(value);
                                                                    int qty = Parent.getScanQty();
                                                                    int ll = Parent.getLevel();
                                                                    int parentId = Parent.getParentId();

                                                                    while (ll != -1 & parentId != -1) {
                                                                        NLevelItem superParent = list.get(parentId);

                                                                        int qty2 = 1 + superParent.getScanQty();
                                                                        superParent.setScanQty(qty2);
                                                                        ll = superParent.getLevel();
                                                                        parentId = superParent.getParentId();
                                                                    }

                                                                }

                                                            }
                                                            adapter.notifyDataSetChanged();
                                                            if (scannedQty == 0) {
                                                                progressTV.setText(scannedQty + " / " + totalQty);
                                                                precentTV.setText("0 %");
                                                            } else {
                                                                progressTV.setText(scannedQty + " / " + totalQty);
                                                                float per = (scannedQty * 100) / totalQty;
                                                                precentTV.setText(per + " %");
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            totalScanTV.setText(uniqueEPCList.size() + "");

                                            if (mSoundTask == null) {
                                                mSoundTask = new  SoundTask();
                                                mSoundTask.execute();
                                            } else {
                                                if (mSoundTask.getStatus() == AsyncTask.Status.FINISHED) {
                                                    mSoundTask.cancel(true);
                                                    mSoundTask = null;
                                                    mSoundTask = new  SoundTask();
                                                    mSoundTask.execute();
                                                }
                                            }

                                        }
                                    }

                                }
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();

                                if(Constants.isNetworkAvailable(InventoryStockTakeBActivity.this)) {
                                    StringWriter sw = new StringWriter();
                                    e.printStackTrace(new PrintWriter(sw));
                                    String exceptionAsString = sw.toString();
                                    new  AppCrashLogsAsyncTask("EPC: "+(String) m.obj+" "+exceptionAsString).execute(Constants.BASE_URL+"AppLogs.php");
                                }

                            }
                        }
                        break;
                    case SDConsts.RFCmdMsg.LOCATE:
                        if (m.arg2 == SDConsts.RFResult.SUCCESS) {

                        }
                        break;
                }
                break;
            case SDConsts.Msg.BTMsg:
                if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_STATE_CHANGED) {
                    Log.d(TAG, "SLED_BT_CONNECTION_STATE_CHANGED = " + m.arg2);
                    if (mReader.BT_GetConnectState() != SDConsts.BTConnectState.CONNECTED) {
                        /*if (mInventory) {
                            pauseStopwatch();
                            mInventory = false;
                        }*/
                        showMessageDialog("Sled Connection Status",  "Sled connection state change"+ m.arg2);
                    }
                }
                else if  (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_DISCONNECTED || m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_LOST) {
                   /* if (mInventory) {
                        pauseStopwatch();
                        mInventory = false;
                    }*/
                    showMessageDialog("Sled Connection Status",  "Sled connection lost");
                }
                break;
        }
    }

    private void startStopwatch()
    {
        isTimerPause = 0;
    }
    private void pauseStopwatch()
    {
        isTimerPause = 1;
    }

    private void TimerMethod()
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable() {
        public void run() {

            //This method runs in the same thread as the UI.

            //Do something to the UI thread here
            timeTV.setText(getDurationString(time));
            timerstate = true;
            if(isTimerPause == 0)
            {
                time += 1;
            }

        }
    };

    private String getDurationString(int seconds) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }

    private String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

    //disabling the return key press
    @Override
    public void onBackPressed() {
        //disabling the return key press
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.inventory_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                showMessageDialog();
                return true;

            case R.id.bluetooth_settings: //Your task
                Intent intent = new Intent(InventoryStockTakeBActivity.this, BTConnectivityActivity.class);
                startActivity(intent);
                return true;

            case R.id.sound: //Your task
                Intent intent2 = new Intent(InventoryStockTakeBActivity.this, SoundManagerActivity.class);
                startActivity(intent2);

                return true;

            case R.id.serial_reader: //Your task
                Intent intent3 = new Intent(InventoryStockTakeBActivity.this, SerialInventoryStockTakeBActivity.class);
                intent3.putExtra("location_JSON",locationObject.toString());
                startActivity(intent3);
                finish();

                return true;

            default:return super.onOptionsItemSelected(item);
        }
    }


    private void showMessageDialog()
    {
        try {
            if (isFinsihButtonPressed == 1) {
                finish();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(InventoryStockTakeBActivity.this);

                dialog.setTitle("Exit Inventory")
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("Your last scan data would be lost. Are you sure you want to exit scan?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                dialoginterface.cancel();
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                dialoginterface.cancel();
                                finish();
                            }
                        }).show();
            }
        }catch (WindowManager.BadTokenException e) {
            //use a log message
        }
    }

    private void showMessageDialogWithBluetoothActivity(String title, String message)
    {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(InventoryStockTakeBActivity.this);
            dialog.setTitle(title)
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage(message)
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            dialoginterface.cancel();
                        }
                    })
                    .setPositiveButton("Bluetooth Settings", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {

                            Intent intent = new Intent(InventoryStockTakeBActivity.this, BTConnectivityActivity.class);
                            startActivity(intent);
                        }
                    }).show();
        }catch (WindowManager.BadTokenException e) {
            //use a log message
        }
    }

    private void showMessageDialog(String title, String message)
    {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(InventoryStockTakeBActivity.this);
            dialog.setTitle(title)
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage(message)
//     .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//      public void onClick(DialogInterface dialoginterface, int i) {
//          dialoginterface.cancel();
//          }})
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                        }
                    }).show();
        }catch (WindowManager.BadTokenException e) {
            //use a log message
        }
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (requestCode) {
                case PermissionHelper.REQ_PERMISSION_CODE:
                    if (permissions != null) {
                        boolean hasResult = false;
                        for (String p : permissions) {
                            if (p.equals(PermissionHelper.mStoragePerms[0])) {
                                hasResult = true;
                                break;
                            }
                        }
                        if (hasResult) {
                            if (grantResults != null && grantResults.length != 0 &&
                                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
                                mFile = true;
                            else
                                mFile = false;
                        }
                    }
                    break;
            }

        }
    }



    class ExpandCollapseAllAsyncTask extends AsyncTask<String, String, String> {



        private Context context;
        boolean status;

        public ExpandCollapseAllAsyncTask(Context con, boolean s) {

            context = con;

            status = s;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Updating UI...");

            pDialog.show();
        }

        @Override
        protected String doInBackground(String... string) {
            String response = null;
            try {
                if(list.size() ==0)
                {
                    return null;
                }
                //Log.d("StockTakeOptionA","((NLevelAdapter)listView.getAdapter()).getCount() "+list.size());
                for(int c= 1 ; c< list.size(); c++)
                {
                    list.get(c).set_isExpanded(status);

                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            listView.performItemClick(listView.getChildAt(0), 0, listView.getItemIdAtPosition(0));
            dismissProgressDialog();

        }
    }

    class LoadTreeAsyncTask extends AsyncTask<String, String, String> {



        private Context context;

        public LoadTreeAsyncTask(Context con) {

            context = con;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Loading data...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... string) {
            String response = null;
            try {
                URL url = new URL(string[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(90000);
                conn.setConnectTimeout(90000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("X-Environment", "android");

                String customer_id = pref.getString(Constants.CUSTOMER_ID,"0");
                JSONArray jsArray = new JSONArray(selectedBarcodeList);
                String user_id = pref.getString(Constants.USER_ID,"0");
                String store_id = pref.getString(Constants.STORE_ID,"0");

                String urlParameters = "customer_id=" + customer_id+"&session_id="+sessionId+"&is_new_session="+isSessionNew+"&json_barcode="+jsArray+"&user_id="+user_id+"&store_id="+store_id+"&location="+location_id+"&sublocation="+sublocation_id;
                // System.out.println("urlParameters " + urlParameters);

                OutputStream wr = conn.getOutputStream();
                wr.write(urlParameters.getBytes("UTF-8"));
                wr.flush();
                wr.close();
                // Add any data you wish to post here

                conn.connect();
                int responseCode = conn.getResponseCode();
                System.out.println("responseCode " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the input stream into a String
                    InputStream inputStream = conn.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        return null;
                    }

                    treeJSON = buffer.toString();

                    //System.out.println("query "+treeJSON);
                    return  treeJSON;
                } else {
                    return null;

                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            NLevelExpandableListView();

            // Date d1 = new Date();

            updateQuantityOfCategorySubcategory();

           /* Date d2 = new Date();
            System.out.println("millisec: "+(d2.getTime()-d1.getTime()) ); //gives the time difference in milliseconds.
            System.out.println("sec: "+(d2.getTime()-d1.getTime())/1000);*/

            dismissProgressDialog();
            if (Constants.isNetworkAvailable(InventoryStockTakeBActivity.this))
            {
                new InventoryStockTakeBActivity.FetchBarcodeAndEPCAsyncTask(InventoryStockTakeBActivity.this).execute(Constants.BASE_URL + "Get_Ethnicity_Bar_EPC_Data.php");

                internetStatusIB.setBackground(getResources().getDrawable(R.drawable.worldwide));
            } else {

                Constants.internetAlert(InventoryStockTakeBActivity.this);

                internetStatusIB.setBackground(getResources().getDrawable(R.drawable.no_internet));
            }


        }
    }

    private void updateQuantityOfCategorySubcategory()
    {
        try
        {
            scannedQty = scannedQty + 0;
            totalQty = 0;
            for (Integer pos : hashMapBarcodePosition.values()) {

                int position = pos;

                //System.out.println("barcode " + obj.getString("barcode") + " Position " + obj.getString("position") );
                if(list.size()>= position)
                {

                    NLevelItem Parent = list.get(position);
                    int qty = Parent.getTotalQty();
                    int ll = Parent.getLevel();
                    int parentId = Parent.getParentId();
                    totalQty = totalQty +qty;
                    //System.out.println("barcode " + obj.getString("barcode") +" totalQty " + totalQty + " qty " + qty );
                    while(ll != -1 & parentId != -1)
                    {
                        NLevelItem superParent = list.get(parentId);

                        int qty2 = qty+superParent.getTotalQty();
                        superParent.setTotalQty(qty2);
                        ll = superParent.getLevel();
                        parentId = superParent.getParentId();
                    }

                }
            }

            //update UI of treelistview
            adapter.notifyDataSetChanged();
            if(scannedQty == 0)
            {
                progressTV.setText(scannedQty+" / "+totalQty);
                precentTV.setText("0 %");
            }
            else
            {
                progressTV.setText(scannedQty+" / "+totalQty);
                float per = (scannedQty*100) / totalQty;
                precentTV.setText(per+" %");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }


    class ProcessEPCAndUpdateUIAsyncTask extends AsyncTask<String, String, String> {

        public ProcessEPCAndUpdateUIAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... string) {
            String response = null;

            try {

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            //System.out.println("ProcessEPCAndUpdateUIAsyncTask Resp " + result);
            /*System.out.println("currentTimeMillis after " + System.currentTimeMillis());*/

            scanFinished =0;
            try {
                adapter.notifyDataSetChanged();
                if(scannedQty == 0)
                {
                    progressTV.setText(scannedQty+" / "+totalQty);
                    precentTV.setText("0 %");
                }
                else
                {
                    progressTV.setText(scannedQty+" / "+totalQty);
                    float per = (scannedQty*100) / totalQty;
                    precentTV.setText(per+" %");
                }

            }catch (Exception e) {
                e.printStackTrace();
            }



        }
    }


    class ServerUpdateAsyncTask extends AsyncTask<String, String, String> {

        public ServerUpdateAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Uploading data...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... string) {
            String response = null;

            try {
                URL url = new URL(string[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(90000);
                conn.setConnectTimeout(90000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("X-Environment", "android");

                String customer_id = pref.getString(Constants.CUSTOMER_ID,"0");
                String user_id = pref.getString(Constants.USER_ID,"0");
                JSONArray jsArray = new JSONArray(selectedBarcodeList);

                ArrayList<String>EPCArrayList = new ArrayList<String>();
                for(int s =0; s<uniqueEPCList.size(); s++)
                {
                    EPCArrayList.add(uniqueEPCList.get(s));
                }

                JSONArray epcArray = new JSONArray(EPCArrayList);

                Date date = new Date();
                String modifiedDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

                String time = sdf.format(cal.getTime());
                String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");
                String store_id = pref.getString(Constants.STORE_ID,"0");
                String urlParameters = "customer_id=" +customer_id+"&user_id="+user_id+"&session_id="+sessionId+"&json_barcode="+jsArray+"&json_epc="+epcArray+"&location_id="+location_id+"&sub_location_id="+sublocation_id+"&stock_date="+modifiedDate+"&stock_time="+time+"&stock_table_name="+stock_table_name+"&is_scan_finish="+isFinsihButtonPressed+"&store_id="+store_id+"&location="+location_id+"&sublocation="+sublocation_id;
                // System.out.println("currentTimeMillis before " + System.currentTimeMillis());

                OutputStream wr = conn.getOutputStream();
                wr.write(urlParameters.getBytes("UTF-8"));
                wr.flush();
                wr.close();
                // Add any data you wish to post here

                conn.connect();
                int responseCode = conn.getResponseCode();
                System.out.println("responseCode " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the input stream into a String
                    InputStream inputStream = conn.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        return null;
                    }
                    return  buffer.toString();
                } else {
                    return null;

                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dismissProgressDialog();
            Toast.makeText(InventoryStockTakeBActivity.this, "Stock take data uploaded successfully", Toast.LENGTH_LONG).show();
            finish();

            //System.out.println("ServerUpdateUIAsyncTask Resp " + result);
            /*System.out.println("currentTimeMillis after " + System.currentTimeMillis());*/

            /*try {
                Intent intent = new Intent(InventoryStockTakeBActivity.this , BTStockTakeASummaryActivity.class);
                intent.putExtra("location_JSON",locationObject.toString());
                intent.putExtra("scanned",totalScanTV.getText().toString());
                intent.putExtra("progress",progressTV.getText().toString());
                intent.putExtra("percentage",precentTV.getText().toString());
                intent.putExtra("time",timeTV.getText().toString());
                intent.putExtra("hashmap_barcode_qty", hashMapBarcodeQty);

                startActivity(intent);

                finish();

            }catch (Exception e) {
                e.printStackTrace();
            }*/



        }
    }




    class RollbackStockTakeAsyncTask extends AsyncTask<String, String, String> {
        private Context context;
        public RollbackStockTakeAsyncTask(Context c) {
            context =c;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Aborting scan data...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... string) {
            String response = null;

            try {
                resetRFIDScanQTY();
                for (String barcode : hashMapBarcode.keySet())
                {
                    hashMapBarcode.put(barcode,0) ;
                }

                return null;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dismissProgressDialog();
            adapter.notifyDataSetChanged();

            System.out.println("response abort scan " + result );
            try
            {
                scannedQty =0;
                //reseting
                progressTV.setText("0");
                precentTV.setText("0 %");
                totalScanTV.setText("0");
                timeTV.setText("00:00:00");

                Toast.makeText(InventoryStockTakeBActivity.this, "Scan Aborted Successfully", Toast.LENGTH_LONG).show();
            }catch(Exception e)
            {
                e.printStackTrace();;
            }


        }
    }


    class AppCrashLogsAsyncTask extends AsyncTask<String, String, String> {
        String logs;

        public AppCrashLogsAsyncTask(String l) {
            logs = l;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... string) {
            String response = null;

            try {
                URL url = new URL(string[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(90000);
                conn.setConnectTimeout(90000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("X-Environment", "android");

                String customer_id = pref.getString(Constants.CUSTOMER_ID,"0");
                String user_id = pref.getString(Constants.USER_ID,"0");


                Date date = new Date();
                String modifiedDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

                String time = sdf.format(cal.getTime());
                String store_id = pref.getString(Constants.STORE_ID,"0");
                String urlParameters = "customer_id=" +customer_id+"&user_id="+user_id+"&stock_date="+modifiedDate+"&stock_time="+time+ "&error_logs="+logs+"&store_id="+store_id ;
                // System.out.println("currentTimeMillis before " + System.currentTimeMillis());

                OutputStream wr = conn.getOutputStream();
                wr.write(urlParameters.getBytes("UTF-8"));
                wr.flush();
                wr.close();
                // Add any data you wish to post here

                conn.connect();
                int responseCode = conn.getResponseCode();
                System.out.println("responseCode " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the input stream into a String
                    InputStream inputStream = conn.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        return null;
                    }
                    return  buffer.toString();
                } else {
                    return null;

                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
        }
    }
    class FetchBarcodeAndEPCAsyncTask extends AsyncTask<String, String,String> {
        private Context context;
        public FetchBarcodeAndEPCAsyncTask(Context con) {
            context = con;
        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Fetching data...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... string) {
            String response = null;
            try {
                URL url = new URL(string[0]);
                HttpURLConnection conn = (HttpURLConnection)
                        url.openConnection();
                conn.setReadTimeout(90000);
                conn.setConnectTimeout(90000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("X-Environment", "android");
                String customer_id =
                        pref.getString(Constants.CUSTOMER_ID, "0");
                JSONArray jsArray = new JSONArray(selectedBarcodeList);
                String user_id = pref.getString(Constants.USER_ID, "0");
                String store_id = pref.getString(Constants.STORE_ID,
                        "0");
                String urlParameters = "customer_id=" + customer_id +
                        "&session_id=" + sessionId + "&is_new_session=" + isSessionNew +
                        "&json_barcode=" + jsArray + "&user_id=" + user_id + "&store_id=" +
                        store_id + "&location=" + location_id + "&sublocation=" + sublocation_id;
                // System.out.println("urlParameters " + urlParameters);
                OutputStream wr = conn.getOutputStream();
                wr.write(urlParameters.getBytes("UTF-8"));
                wr.flush();
                wr.close();
                // Add any data you wish to post here
                conn.connect();
                int responseCode = conn.getResponseCode();
                System.out.println("responseCode " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the input stream into a String
                    InputStream inputStream = conn.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    BufferedReader reader = new BufferedReader(new
                            InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    treeJSON = buffer.toString();
                    //System.out.println("query "+treeJSON);
                    return treeJSON;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dismissProgressDialog();
            /**** store the result in array for comparing ***********/
            dataBarcodeList.clear();
            dataEPCList.clear();
            if (result != null) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int c = 0; c < jsonArray.length(); c++) {
                        JSONObject jObj = jsonArray.getJSONObject(c);
                        dataBarcodeList.add(jObj.getString("barcode"));
                        dataEPCList.add(jObj.getString("epc"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}