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
import sysnotion.tagid.tagsmart.utils.FileManager;
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
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.riyagayasen.easyaccordion.AccordionView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

import static android.view.View.TEXT_ALIGNMENT_CENTER;

public class InventoryStockTakeAActivity  extends AppCompatActivity {

    private static final String TAG = InventoryStockTakeAActivity.class.getSimpleName();
    SharedPreferences pref;
    private DBManager dbManager;
    JSONObject locationObject;
    TextView StoreIdShopTV, progressTV, precentTV, timeTV, totalScanTV, batteryStatusTV;
    Button inventoryScanStartBtn, finishScanBtn ;
    ListView listView;
    String treeJSON="";
    Button expandButton, collapseButton;


    List<NLevelItem> list;
    NLevelAdapter adapter;

    int[] colors ;
    int counter=0;
    int parentId=-1;
    JSONArray arr;
    int maxLevel=0;
    //ArrayList<String> selectedBarcodeList = new ArrayList<String>();

    int totalQty=0;
    int scannedQty = 0;
    String LastSelectedCategory ="";
    private SoundPool mSoundPool;
    private int mSoundId;
    private TimerTask mLocateTimerTask;
    private Timer mClearLocateTimer;

    private boolean mSoundFileLoadState;

    private InventoryStockTakeAActivity.SoundTask mSoundTask;

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
    private final InventoryStockTakeAActivity.BarcodeHandler mBarcodeHandler = new InventoryStockTakeAActivity.BarcodeHandler(this);
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
    ArrayList<String> selectedBarcodeList = new ArrayList<String>();
    int isFinsihButtonPressed = 0;
    CopyOnWriteArrayList<String> uniqueEPCList= new CopyOnWriteArrayList<String>();
    InventoryStockTakeAActivity.ProcessEPCAndUpdateUIAsyncTask processEPCAndUpdateUIAsyncTask;
    //JSONArray barcodePositionArray = new JSONArray();
    int lastLevel = 0;
    private ProgressDialog pDialog;
    int UIJSONLength=0;
    ArrayList<String> barcodeList = new ArrayList<String>();
    private TeamCountEPCAndUpdateUIAsyncTask teamEPCAndUpdateUIAsyncTask;
    int EPCAnchorPosition = 0;
    private AccordionView teamCountAV;
    private LinearLayout teamCountLL;
    private ImageButton internetStatusIB;
    private TextView teamCountTV;

    //New code
    ArrayList<String> dataBarcodeList = new ArrayList<String>();
    ArrayList<String> dataEPCList = new ArrayList<String>();

    //New code

    /*//test
    int testDataCounter = 0;
    String testEPC[] ={"E2801170200005A2A4E50A0E",
"E2801170200005A5A4E70A0E",
"E2801170200014AFA4E10A0E",
"E2801170200004AEA4E10A0E",
"E2801170200015ADA4E80A0E",
"E280117020001496A4E80A0E",
"E2801170200015A7A4E40A0E",
"E2801170200014A9A4E40A0E",
"E2801170200005A9A4E70A0E",
"E2801170200004A4A4E20A0E",
"E2801170200004AFA4E50A0E",
"E2801170200014AEA4E50A0E",
"E2801170200014ADA4E50A0E",
"E2801170200004A8A4E20A0E",
"E280117020001493A4E10A0E",
"E2801170200014A2A4E30A0E",
"E2801170200004ACA4E60A0E",
"E2801170200015AEA4E40A0E",
"E2801170200014A1A4E60A0E"
        };*/

    private Runnable Timer_Tick = new Runnable() {
        public void run() {

            //This method runs in the same thread as the UI.

            //Do something to the UI thread here
            timeTV.setText(getDurationString(time));
            timerstate = true;
            if (isTimerPause == 0) {
                time += 1;
            }

        }
    };

    public static String leftPad(String str, int length, String padChar) {
        String pad = "";
        for (int i = 0; i < length; i++) {
            pad += padChar;
        }
        return pad.trim() + str.trim();
    }


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
        dbManager = new DBManager(InventoryStockTakeAActivity.this);
        dbManager.open();
        dbManager.turcateStockTakeChecksum();

        pDialog = new ProgressDialog(InventoryStockTakeAActivity.this);

        sessionId = Constants.getSessionId(InventoryStockTakeAActivity.this);
        isSessionNew = Constants.isSessionNew(InventoryStockTakeAActivity.this);
        // Log.d("InventoryA","sessionId "+sessionId);
        internetStatusIB=(ImageButton) findViewById(R.id.internetStatusIB); //written by raj on 01-09-21
        batteryStatusTV = (TextView) findViewById(R.id.batteryStatusTV);
        StoreIdShopTV = (TextView) findViewById(R.id.StoreIdShopTV);
        totalScanTV = (TextView) findViewById(R.id.totalScanTV);
        totalScanTV.setText("0");


        teamCountTV  = (TextView) findViewById(R.id.teamCountTV);
        teamCountTV.setText("0");

        try {
            locationObject = new JSONObject(getIntent().getStringExtra("location_JSON"));
            StoreIdShopTV.setText("Store ID: " + pref.getString(Constants.STORE_NUMBER, "0") + " / " + locationObject.getString("location") + " - " + locationObject.getString("sublocation"));
            location_id = locationObject.getString("location_id");
            sublocation_id = locationObject.getString("sub_location_id");

            // Log.v("InventoryA","Location JSON "+locationObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        lastLevel = Integer.parseInt(pref.getString(Constants.MAX_LEVEL, "0"));

        //Log.d("InventoryA","lastLevel "+lastLevel);
        selectedBarcodeList = Constants.getArrayList(Constants.CATEGORY_ARRAY_JSON, InventoryStockTakeAActivity.this);
        colors = getResources().getIntArray(R.array.colors_hex_code);

        if (Constants.isNetworkAvailable(InventoryStockTakeAActivity.this)) {
            new InventoryStockTakeAActivity.LoadTreeAsyncTask(InventoryStockTakeAActivity.this).execute(Constants.DOMAIN_URL + "product_tree_by_barcode.php");
            internetStatusIB.setBackground(getResources().getDrawable(R.drawable.worldwide));
        } else {
            Constants.internetAlert(InventoryStockTakeAActivity.this);
            internetStatusIB.setBackground(getResources().getDrawable(R.drawable.no_internet));
        }

        progressTV = (TextView) findViewById(R.id.progressTV);


        precentTV = (TextView) findViewById(R.id.precentTV);

        timeTV = (TextView) findViewById(R.id.timeTV);
        timeTV.setText("00:00:00");


        expandButton = (Button) findViewById(R.id.expandButton);
        collapseButton = (Button) findViewById(R.id.collapseButton);

        collapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new InventoryStockTakeAActivity.ExpandCollapseAllAsyncTask(InventoryStockTakeAActivity.this, false).execute();
            }
        });

        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new InventoryStockTakeAActivity.ExpandCollapseAllAsyncTask(InventoryStockTakeAActivity.this, true).execute();
            }
        });

        inventoryScanStartBtn = (Button) findViewById(R.id.inventoryScanStartBtn);
        finishScanBtn = (Button) findViewById(R.id.finishScanBtn);


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
                                inventoryScanStartBtn.setCompoundDrawablesWithIntrinsicBounds(res.getDrawable(DrawableImage[bt_notes_blink]), null, null, null);
                                inventoryScanStartBtn.setCompoundDrawablePadding(4);
                                bt_notes_blink++;
                                if (bt_notes_blink == 2) {
                                    bt_notes_blink = 0;
                                }
                                handlerButton.postDelayed(this, 500);
                            }
                        });
                    }
                }, 0);


                int ret;
                // starting timer
                if (timerstate == true) {
                    scanTimer.cancel();
                    scanTimer.purge();
                    time = 0;
                    timerstate = false;
                } else {
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
                   // ret = mReader.RF_PerformInventoryCustom(1, 2, 6,"00000000",false);
                    if (ret == SDConsts.RFResult.SUCCESS) {
                        teamCountTV.setText("1");
                        startStopwatch();
                        mInventory = true;

                        //periodically call read EPC Array asyntask for simultaneous sending and updating team count
                        final Handler handler = new Handler();
                        Timer timer = new Timer();
                        TimerTask task;
                        task = new TimerTask() {
                            @Override
                            public void run() {
                                handler.post(new Runnable() {
                                    public void run() {
                                        teamEPCAndUpdateUIAsyncTask = new InventoryStockTakeAActivity.TeamCountEPCAndUpdateUIAsyncTask();
                                       // teamEPCAndUpdateUIAsyncTask.execute(Constants.BASE_URL + "UpdateAndGetTeamCountDetails.php");
                                        teamEPCAndUpdateUIAsyncTask.execute(Constants.BASE_URL + "UpdateAndGetTeamCountDetails.php");


                                    }
                                });
                            }
                        };
                        timer.schedule(task, 7000, 7000);


                    } else if (ret == SDConsts.RFResult.MODE_ERROR)
                        Toast.makeText(InventoryStockTakeAActivity.this, "Start Inventory failed, Please check RFR900 MODE", Toast.LENGTH_SHORT).show();
                    else if (ret == SDConsts.RFResult.LOW_BATTERY)
                        Toast.makeText(InventoryStockTakeAActivity.this, "Start Inventory failed, LOW_BATTERY", Toast.LENGTH_SHORT).show();
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
                        InventoryStockTakeAActivity.this);

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
                                inventoryScanStartBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                                finishScanBtn.setEnabled(false);
                                //resetting timer
                                time = 0;
                                scanTimer.cancel();
                                scanTimer.purge();
                                timerstate = false;

                                if (Constants.isNetworkAvailable(InventoryStockTakeAActivity.this)) {
                                    new InventoryStockTakeAActivity.RollbackStockTakeAsyncTask(InventoryStockTakeAActivity.this).execute(Constants.BASE_URL + "RollBackStockTake.php");
                                } else {
                                    Constants.internetAlert(InventoryStockTakeAActivity.this);
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
                                    Toast.makeText(InventoryStockTakeAActivity.this, "Stop Inventory failed", Toast.LENGTH_SHORT).show();

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
                                inventoryScanStartBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                                finishScanBtn.setEnabled(false);
                                //resetting timer
                                time = 0;
                                scanTimer.cancel();
                                scanTimer.purge();

                                if (ret == SDConsts.RFResult.SUCCESS || ret == SDConsts.RFResult.NOT_INVENTORY_STATE) {
                                    mInventory = false;
                                    pauseStopwatch();
                                    inventoryScanStartBtn.setEnabled(true);
                                    finishScanBtn.setEnabled(true);

                                    if (Constants.isNetworkAvailable(InventoryStockTakeAActivity.this)) {
                                        //new InventoryStockTakeAActivity.ServerUpdateAsyncTask().execute(Constants.BASE_URL + "UpdateStockTake.php");
                                        new InventoryStockTakeAActivity.ServerUpdateAsyncTask().execute(Constants.BASE_URL + "UpdateStockTake.php");
                                        internetStatusIB.setBackground(getResources().getDrawable(R.drawable.worldwide));
                                    } else {
                                        Constants.internetAlert(InventoryStockTakeAActivity.this);
                                        internetStatusIB.setBackground(getResources().getDrawable(R.drawable.no_internet));
                                    }

                                } else if (ret == SDConsts.RFResult.STOP_FAILED_TRY_AGAIN)
                                    Toast.makeText(InventoryStockTakeAActivity.this, "Stop Inventory failed", Toast.LENGTH_SHORT).show();

                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }


        });


        teamCountAV = (AccordionView) findViewById(R.id.teamCountAV);
        teamCountLL = (LinearLayout) findViewById(R.id.teamCountLL);
    }

    private void resetRFIDScanQTY() {
        for (int i = 0; i < list.size(); i++) {

            list.get(i).setScanQty(0);
        }
    }

    private void NLevelExpandableListView() {

        // Log.d("StockTakeOptionA","treeJSON "+treeJSON);
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
                ((NLevelAdapter) listView.getAdapter()).toggle(arg2);
                ((NLevelAdapter) listView.getAdapter()).getFilter().filter();
            }
        });


    }

    private void nestedLoop(String levelList, NLevelItem nLevelItem, final LayoutInflater inflater, int level) {

        try {
            maxLevel = level;
            JSONArray jsonArrayStringList = new JSONArray(levelList);
            int length = jsonArrayStringList.length();

            //System.out.println("jsonArrayStringList  "+jsonArrayStringList);
            //System.out.println("levelList  "+levelList);
            for (int i = 0; i < length; i++) {

                JSONObject itemObject = jsonArrayStringList.getJSONObject(i);

                // System.out.println("category0000  "+itemObject.getString("name"));
                if (itemObject.has("children")) {

                    int childrenSize = itemObject.getJSONArray("children").length();
                    //System.out.println("category111  "+itemObject.getString("name")+" no.of child "+childrenSize);

                    NLevelItem Parent = itemView(counter, i, itemObject.getString("name"), nLevelItem, inflater, level, !(childrenSize > 0));
                    if (level == 0) {
                        parentId = -1;
                        //System.out.println("parent ID11 "+parentId);
                    } else {
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
                    if (level == (lastLevel - 1))//i.e. maxlevel  -1
                    {

                        //System.out.println("## no child found ############ "+((SomeObject)Parent.getWrappedObject()).getName());
                        if (((SomeObject) Parent.getWrappedObject()).getName().trim().length() > 0) {

                            barcodeList.add((String) ((SomeObject) Parent.getWrappedObject()).getName());


                            hashMapBarcodePosition.put((String) ((SomeObject) Parent.getWrappedObject()).getName(), counter);
                            hashMapBarcode.put((String) ((SomeObject) Parent.getWrappedObject()).getName(), 0);
                        }

                    }


                    parentId = counter;
                    counter++;

                    if (childrenSize > 0) {
                        nestedLoop(itemObject.getJSONArray("children").toString(), Parent, inflater, level + 1);
                    }
                } else {
                    // System.out.println("category111  "+itemObject.getString("name"));
                    NLevelItem Parent = list.get(list.size() - 1);
                    Parent.setTotalQty(Integer.parseInt(itemObject.getString("name")));
                    // System.out.println("category222  "+(String) ((SomeObject) Parent.getWrappedObject()).getName());
                    hashMapBarcodeQty.put((String) ((SomeObject) Parent.getWrappedObject()).getName(), Integer.parseInt(itemObject.getString("name")));
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private NLevelItem itemView(final int position, final int itemRow, final String Title, final NLevelItem nLevelItem, final LayoutInflater inflater, final int level, final boolean isLast) {

        NLevelItem superChild = new NLevelItem(new SomeObject(Title), nLevelItem, new NLevelView() {
            @Override
            public View getView(final NLevelItem item) {
                View view = inflater.inflate(R.layout.nlevel_list_item_without_chkbx, null);
                TextView tv = (TextView) view.findViewById(R.id.textView);
                String name = (String) ((SomeObject) item.getWrappedObject()).getName();

                tv.setText(name);

                TextView tv2 = (TextView) view.findViewById(R.id.qtytextView);
                tv2.setText(item.getScanQty() + " / " + item.getTotalQty());

                LinearLayout listItemContainer = (LinearLayout) view.findViewById(R.id.listItemContainer);
                listItemContainer.setBackgroundColor(colors[level]);

                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) tv.getLayoutParams();
                mlp.setMargins(level * 50, 5, 5, 5);

                if (isLast) {
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(InventoryStockTakeAActivity.this, "Clicked on: " + Title, Toast.LENGTH_SHORT).show();
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
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            float actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            Constants.mSoundVolume = actVolume / maxVolume;
            SoundLoadListener();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
        mSoundPool = new SoundPool.Builder().setAudioAttributes(attributes).setMaxStreams(5).build();
        if (mSoundPool != null)
            return true;
        return false;
    }

    @SuppressWarnings("deprecation")
    private boolean createOldSoundPool() {
        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        if (mSoundPool != null)
            return true;
        return false;
    }

    ;

    private void SoundLoadListener() {
        if (mSoundPool != null) {
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    // TODO Auto-generated method stub
                    mSoundFileLoadState = true;
                }
            });
            mSoundId = mSoundPool.load(InventoryStockTakeAActivity.this, R.raw.beep, 1);
        }
    }

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
        if (mClearLocateTimer != null) {
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
        if (teamEPCAndUpdateUIAsyncTask != null) {
            teamEPCAndUpdateUIAsyncTask.cancel(true);
        }

        scanFinished = 1;
        dismissProgressDialog();
        dbManager.close();

        super.onStop();
    }

    public void handleInventoryHandler(Message m) {
        // Log.d(TAG, "mInventoryHandler");
        //Log.d(TAG, "m arg1 = " + m.arg1 + " arg2 = " + m.arg2);
        switch (m.what) {
            case SDConsts.Msg.SDMsg:
                switch (m.arg1) {
                    case SDConsts.SDCmdMsg.TRIGGER_PRESSED:

                        break;

                    case SDConsts.SDCmdMsg.SLED_INVENTORY_STATE_CHANGED:
                        SpannableString spannablecontent = new SpannableString(" " + m.arg2 + " % ");
                        spannablecontent.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.red)),
                                0, spannablecontent.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        batteryStatusTV.setText(spannablecontent);

                        break;

                    case SDConsts.SDCmdMsg.TRIGGER_RELEASED:

                        break;
                    //You can receive this message every a minute. SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED
                    case SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED:
                        //Toast.makeText(InventoryStockTakeAActivity.this, "Battery state = " + m.arg2, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Battery state = " + m.arg2);
                        batteryStatusTV.setText(m.arg2 + " % ");
                        break;
                    case SDConsts.SDCmdMsg.SLED_HOTSWAP_STATE_CHANGED:
                        if (m.arg2 == SDConsts.SDHotswapState.HOTSWAP_STATE)
                            Toast.makeText(InventoryStockTakeAActivity.this, "HOTSWAP STATE CHANGED = HOTSWAP_STATE", Toast.LENGTH_SHORT).show();
                        else if (m.arg2 == SDConsts.SDHotswapState.NORMAL_STATE)
                            Toast.makeText(InventoryStockTakeAActivity.this, "HOTSWAP STATE CHANGED = NORMAL_STATE", Toast.LENGTH_SHORT).show();

                        break;
                }
                break;

            case SDConsts.Msg.RFMsg:
                switch (m.arg1) {
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
                                            ////// test over */


                                            if (uniqueEPCList.addIfAbsent(EPCNumber)) {
                                                // System.out.println("ProcessEPCAndUpdateUIAsyncTask EPCNumber " + EPCNumber);
                                                // System.out.println("ProcessEPCAndUpdateUIAsyncTask testDataCounter " + testDataCounter);
                                                if (EPCNumber.length() == 24) {
                                                    //String barcode = Constants.convertEPCToBarcode(EPCNumber);


                                                    //System.out.println("ProcessEPCAndUpdateUIAsyncTask barcode " + barcode);
                                                    String barcode = "";
                                                    if (dataEPCList.contains(EPCNumber)) {

                                                        barcode = dataBarcodeList.get(dataEPCList.indexOf(EPCNumber));

                                                        if (hashMapBarcode.containsKey(barcode)) {
                                                            int value = hashMapBarcode.get(barcode);
                                                            value = value + 1;
                                                            hashMapBarcode.put(barcode, value);
                                                            // System.out.println("ProcessEPCAndUpdateUIAsyncTask rfid_qty " + value);

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
                                                      /*  if (scannedQty == 0) {
                                                            progressTV.setText(scannedQty + " / " + totalQty);
                                                            precentTV.setText("0 %");
                                                        } else {
                                                            progressTV.setText(scannedQty + " / " + totalQty);
                                                            float per = (scannedQty * 100) / totalQty;
                                                            precentTV.setText(per + " %");
                                                        }*/
                                                        }
                                                    }
                                                }

                                            }
                                            totalScanTV.setText(uniqueEPCList.size() + "");

                                            if (mSoundTask == null) {
                                                mSoundTask = new InventoryStockTakeAActivity.SoundTask();
                                                mSoundTask.execute();
                                            } else {
                                                if (mSoundTask.getStatus() == AsyncTask.Status.FINISHED) {
                                                    mSoundTask.cancel(true);
                                                    mSoundTask = null;
                                                    mSoundTask = new InventoryStockTakeAActivity.SoundTask();
                                                    mSoundTask.execute();
                                                }
                                            }

                                        }
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();


                                StringWriter sw = new StringWriter();
                                e.printStackTrace(new PrintWriter(sw));
                                String exceptionAsString = sw.toString();

                                Toast.makeText(getBaseContext(), "Reader Scan value: " + (String) m.obj + " " + exceptionAsString, Toast.LENGTH_SHORT).show();

                                if (Constants.isNetworkAvailable(InventoryStockTakeAActivity.this)) {

                                    new InventoryStockTakeAActivity.AppCrashLogsAsyncTask("Reader Scan value: " + (String) m.obj + " " + exceptionAsString).execute(Constants.BASE_URL + "AppLogs.php");
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
                       /* if (mInventory) {
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

    private void startStopwatch() {
        isTimerPause = 0;
    }

    private void pauseStopwatch() {
        isTimerPause = 1;
    }

    private void TimerMethod() {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(Timer_Tick);
    }

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
                Intent intent = new Intent(InventoryStockTakeAActivity.this, BTConnectivityActivity.class);
                startActivity(intent);
                return true;

            case R.id.sound: //Your task
                Intent intent2 = new Intent(InventoryStockTakeAActivity.this, SoundManagerActivity.class);
                startActivity(intent2);

                return true;

            case R.id.serial_reader: //Your task
                Intent intent3 = new Intent(InventoryStockTakeAActivity.this, SerialInventoryStockTakeAActivity.class);
                intent3.putExtra("location_JSON",locationObject.toString());
                startActivity(intent3);
                finish();

                return true;

            default:return super.onOptionsItemSelected(item);
        }
    }

    private void showMessageDialog() {
        try {
            if (isFinsihButtonPressed == 1) {
                finish();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(InventoryStockTakeAActivity.this);

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
        } catch (WindowManager.BadTokenException e) {
            //use a log message
        }
    }

    private void showMessageDialogWithBluetoothActivity(String title, String message) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(InventoryStockTakeAActivity.this);
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

                            Intent intent = new Intent(InventoryStockTakeAActivity.this, BTConnectivityActivity.class);
                            startActivity(intent);
                        }
                    }).show();
        } catch (WindowManager.BadTokenException e) {
            //use a log message
        }
    }

    private void showMessageDialog(String title, String message) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(InventoryStockTakeAActivity.this);
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
        } catch (WindowManager.BadTokenException e) {
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

    private void updateQuantityOfCategorySubcategory() {
        try {
            scannedQty = scannedQty + 0;
            totalQty = 0;
            for (Integer pos : hashMapBarcodePosition.values()) {

                int position = pos;

                //System.out.println("barcode " + obj.getString("barcode") + " Position " + obj.getString("position") );
                if (list.size() >= position) {

                    NLevelItem Parent = list.get(position);
                    int qty = Parent.getTotalQty();
                    int ll = Parent.getLevel();
                    int parentId = Parent.getParentId();
                    totalQty = totalQty + qty;
                    //System.out.println("barcode " + obj.getString("barcode") +" totalQty " + totalQty + " qty " + qty );
                    while (ll != -1 & parentId != -1) {
                        NLevelItem superParent = list.get(parentId);

                        int qty2 = qty + superParent.getTotalQty();
                        superParent.setTotalQty(qty2);
                        ll = superParent.getLevel();
                        parentId = superParent.getParentId();
                    }

                }
            }

            //update UI of treelistview
            adapter.notifyDataSetChanged();
            /*if (scannedQty == 0) {
                progressTV.setText(scannedQty + " / " + totalQty);
                precentTV.setText("0 %");
            } else {
                progressTV.setText(scannedQty + " / " + totalQty);
                float per = (scannedQty * 100) / totalQty;
                precentTV.setText(per + " %");
            }*/

            progressTV.setText(0 + "");
            precentTV.setText("0 / " + totalQty + " (0 %)");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    private static class BarcodeHandler extends Handler {
        private final WeakReference<InventoryStockTakeAActivity> mExecutor;

        public BarcodeHandler(InventoryStockTakeAActivity f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            InventoryStockTakeAActivity executor = mExecutor.get();
            if (executor != null) {
                executor.handleInventoryHandler(msg);
            }
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
    }

    class ExpandCollapseAllAsyncTask extends AsyncTask<String, String, String> {


        boolean status;
        private Context context;

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
                if (list.size() == 0) {
                    return null;
                }
                //Log.d("StockTakeOptionA","((NLevelAdapter)listView.getAdapter()).getCount() "+list.size());
                for (int c = 1; c < list.size(); c++) {
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

                String customer_id = pref.getString(Constants.CUSTOMER_ID, "0");
                JSONArray jsArray = new JSONArray(selectedBarcodeList);
                String user_id = pref.getString(Constants.USER_ID, "0");
                String store_id = pref.getString(Constants.STORE_ID, "0");

                String urlParameters = "customer_id=" + customer_id + "&session_id=" + sessionId + "&is_new_session=" + isSessionNew + "&json_barcode=" + jsArray + "&user_id=" + user_id + "&store_id=" + store_id + "&location=" + location_id + "&sublocation=" + sublocation_id;
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
            NLevelExpandableListView();

            // Date d1 = new Date();

            updateQuantityOfCategorySubcategory();

           /* Date d2 = new Date();
            System.out.println("millisec: "+(d2.getTime()-d1.getTime()) ); //gives the time difference in milliseconds.
            System.out.println("sec: "+(d2.getTime()-d1.getTime())/1000);*/

            dismissProgressDialog();
            if (Constants.isNetworkAvailable(InventoryStockTakeAActivity.this))
            {
                new FetchBarcodeAndEPCAsyncTask(InventoryStockTakeAActivity.this).execute(Constants.BASE_URL + "Get_Ethnicity_Bar_EPC_Data.php");

                internetStatusIB.setBackground(getResources().getDrawable(R.drawable.worldwide));
            }
            else
            {

                Constants.internetAlert(InventoryStockTakeAActivity.this);

                internetStatusIB.setBackground(getResources().getDrawable(R.drawable.no_internet));
            }

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

            scanFinished = 0;
            try {
                adapter.notifyDataSetChanged();
                /*if (scannedQty == 0) {
                    progressTV.setText(scannedQty + " / " + totalQty);
                    precentTV.setText("0 %");
                } else {
                    progressTV.setText(scannedQty + " / " + totalQty);
                    float per = (scannedQty * 100) / totalQty;
                    precentTV.setText(per + " %");
                }*/


            } catch (Exception e) {
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

                conn.setReadTimeout(120000);
                conn.setConnectTimeout(120000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("X-Environment", "android");

                String customer_id = pref.getString(Constants.CUSTOMER_ID, "0");
                String user_id = pref.getString(Constants.USER_ID, "0");
                JSONArray jsArray = new JSONArray(selectedBarcodeList);

                ArrayList<String> EPCArrayList = new ArrayList<String>();
                for (int s = 0; s < uniqueEPCList.size(); s++) {
                    EPCArrayList.add(uniqueEPCList.get(s));
                }

                JSONArray epcArray = new JSONArray(EPCArrayList);

                Date date = new Date();
                String modifiedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

                String time = sdf.format(cal.getTime());
                String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME, "");
                String store_id = pref.getString(Constants.STORE_ID, "0");
                String urlParameters = "customer_id=" + customer_id + "&user_id=" + user_id + "&session_id=" + sessionId + "&json_barcode=" + jsArray + "&json_epc=" + epcArray + "&location_id=" + location_id + "&sub_location_id=" + sublocation_id + "&stock_date=" + modifiedDate + "&stock_time=" + time + "&stock_table_name=" + stock_table_name + "&is_scan_finish=" + isFinsihButtonPressed + "&store_id=" + store_id + "&location=" + location_id + "&sublocation=" + sublocation_id;
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
                    return buffer.toString();
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

            Toast.makeText(InventoryStockTakeAActivity.this, "Stock take data uploaded successfully", Toast.LENGTH_LONG).show();
            finish();
            // finish();
            //System.out.println("ServerUpdateUIAsyncTask Resp " + result);
            /*System.out.println("currentTimeMillis after " + System.currentTimeMillis());*/

          /*  try {
                Intent intent = new Intent(InventoryStockTakeAActivity.this, SerialStockTakeASummaryActivity.class);
                intent.putExtra("location_JSON", locationObject.toString());
                intent.putExtra("scanned", totalScanTV.getText().toString());
                intent.putExtra("progress", progressTV.getText().toString());
                intent.putExtra("percentage", precentTV.getText().toString());
                intent.putExtra("time", timeTV.getText().toString());
                intent.putExtra("hashmap_barcode_qty", hashMapBarcodeQty);

                startActivity(intent);

                finish();

            } catch (Exception e) {
                e.printStackTrace();
            }*/


        }
    }

    class RollbackStockTakeAsyncTask extends AsyncTask<String, String, String> {
        private Context context;

        public RollbackStockTakeAsyncTask(Context c) {
            context = c;
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
                for (String barcode : hashMapBarcode.keySet()) {
                    hashMapBarcode.put(barcode, 0);
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

            System.out.println("response abort scan " + result);
            try {
                scannedQty = 0;
                //reseting
                progressTV.setText("0");
                precentTV.setText("0 %");
                totalScanTV.setText("0");
                timeTV.setText("00:00:00");

                Toast.makeText(InventoryStockTakeAActivity.this, "Scan Aborted Successfully", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                ;
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

                String customer_id = pref.getString(Constants.CUSTOMER_ID, "0");
                String user_id = pref.getString(Constants.USER_ID, "0");


                Date date = new Date();
                String modifiedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

                String time = sdf.format(cal.getTime());
                String store_id = pref.getString(Constants.STORE_ID, "0");
                String urlParameters = "customer_id=" + customer_id + "&user_id=" + user_id + "&stock_date=" + modifiedDate + "&stock_time=" + time + "&error_logs=" + logs + "&store_id=" + store_id;
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
                    return buffer.toString();
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


    class TeamCountEPCAndUpdateUIAsyncTask extends AsyncTask<String, String, String> {

        public TeamCountEPCAndUpdateUIAsyncTask() {
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

                String customer_id = pref.getString(Constants.CUSTOMER_ID, "0");
                String user_id = pref.getString(Constants.USER_ID, "0");
                String store_id = pref.getString(Constants.STORE_ID, "0");

                ArrayList<String> EPCArrayList = new ArrayList<String>();
                for (int s = EPCAnchorPosition; s < uniqueEPCList.size(); s++) {
                    EPCArrayList.add(uniqueEPCList.get(s));
                }
                //System.out.println(EPCArrayList);
                EPCAnchorPosition = uniqueEPCList.size();
                JSONArray epcArray = new JSONArray(EPCArrayList);

                JSONArray jsArray = new JSONArray(selectedBarcodeList);
                String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME, "");
                String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME, "");

                String urlParameters = "customer_id=" + customer_id + "&user_id=" + user_id + "&session_id=" + sessionId + "&json_epc=" + epcArray + "&store_id=" + store_id + "&json_barcode=" + jsArray + "&stock_table_name=" + stock_table_name + "&product_table_name=" + product_table_name + "&location_id=" + location_id + "&sub_location_id=" + sublocation_id;
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
                    return buffer.toString();
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

            // System.out.println("TeamEPCAndUpdateUIAsyncTask Resp " + result);
            /*System.out.println("currentTimeMillis after " + System.currentTimeMillis());*/
            teamCountLL.removeAllViews();
            try {
                JSONObject jsonObject = new JSONObject(result);
                int team_count = Integer.parseInt(jsonObject.getString("team_count"));

                if (team_count == 0) {
                    progressTV.setText(0 + "");
                    precentTV.setText("0 / " + totalQty + " (0 %)");
                } else {
                    progressTV.setText(team_count + "");
                    float per = (team_count * 100) / totalQty;
                    precentTV.setText(team_count + " / " + totalQty + " ( " + per + " %)");
                }

                teamCountTV.setText(jsonObject.getString("total_user"));

                JSONArray summaryJSONArray = jsonObject.getJSONArray("summary_team_count");

                for (int t = 0; t < summaryJSONArray.length(); t++)
                {
                    JSONObject tempObj = summaryJSONArray.getJSONObject(t);

                    LinearLayout parent = new LinearLayout(InventoryStockTakeAActivity.this);

                    parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    parent.setOrientation(LinearLayout.VERTICAL);

                    LinearLayout ll2 = new LinearLayout(InventoryStockTakeAActivity.this);

                    ll2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                    ll2.setOrientation(LinearLayout.HORIZONTAL);

                    final TextView child1 = new TextView(InventoryStockTakeAActivity.this);
                    child1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

                    final TextView child2 = new TextView(InventoryStockTakeAActivity.this);
                    child2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);


                    LinearLayout.LayoutParams childParam1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                    childParam1.weight = 0.5f;
                    child1.setLayoutParams(childParam1);

                    LinearLayout.LayoutParams childParam2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                    childParam2.weight = 0.5f;
                    child2.setLayoutParams(childParam2);

                    child1.setText(tempObj.getString("location") + " / " + tempObj.getString("sub_location"));
                    child2.setText(tempObj.getString("qty") + " / " + tempObj.getString("total_qty"));

                    ll2.addView(child1);
                    ll2.addView(child2);

                    ProgressBar progressbar = new ProgressBar(InventoryStockTakeAActivity.this, null, android.R.attr.progressBarStyleHorizontal);
                    progressbar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                    LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutparams.setMargins(0, 9, 0, 0);
                    progressbar.setLayoutParams(layoutparams);
                    progressbar.setMax(Integer.parseInt(tempObj.getString("total_qty")));
                    progressbar.setProgress(Integer.parseInt(tempObj.getString("qty")));

                    parent.addView(ll2);
                    parent.addView(progressbar);
                    teamCountLL.addView(parent);


                }


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(InventoryStockTakeAActivity.this, "Something went wrong during updating epc to server", Toast.LENGTH_LONG).show();
            }


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