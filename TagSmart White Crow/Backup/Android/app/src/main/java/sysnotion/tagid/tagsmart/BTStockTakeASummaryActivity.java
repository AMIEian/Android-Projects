package sysnotion.tagid.tagsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import co.kr.bluebird.sled.BTReader;
import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;
import co.kr.bluebird.sled.SelectionCriterias;
import sysnotion.tagid.tagsmart.adapter.InventoryAdapter;
import sysnotion.tagid.tagsmart.adapter.MissingEPCAdapter;
import sysnotion.tagid.tagsmart.db.DBManager;
import sysnotion.tagid.tagsmart.model.BarcodeEPCSearch;
import sysnotion.tagid.tagsmart.model.Inventory;
import sysnotion.tagid.tagsmart.nLevel.NLevelAdapter;
import sysnotion.tagid.tagsmart.nLevel.NLevelItem;
import sysnotion.tagid.tagsmart.nLevel.NLevelView;
import sysnotion.tagid.tagsmart.nLevel.SomeObject;
import sysnotion.tagid.tagsmart.utils.Constants;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BTStockTakeASummaryActivity extends AppCompatActivity {
    SharedPreferences pref;
    private DBManager dbManager;
    JSONObject locationObject;
    AlertDialog.Builder dialog;
    TextView StoreIdShopTV, progressTV, precentTV, timeTV, totalScanTV;
    ListView categoryListView, listView;
    ArrayList<BarcodeEPCSearch> categoryList = new ArrayList<BarcodeEPCSearch>();
    MissingEPCAdapter mAdapter;
    JSONArray missingEPCBarcodeObj;
    private BTReader reader;
    private final  BarcodeHandler mBarcodeHandler = new  BarcodeHandler(this);
    private SoundPool mSoundPool;
    private int mLocateValue;
    private int mSoundId;
    private TimerTask mLocateTimerTask;
    private Timer mClearLocateTimer;

    private boolean mSoundFileLoadState;

    private  SoundTask mSoundTask;
    ProgressBar vprogressbar;
    Button stopScanBtn, startScanBtn, closeBtn;
    String mLocateEPC = new String();
    String mLocateBarcode = new String();
    int mPosition= -1;
    PopupWindow popupWindow;
    TextView searchTextView;
    String location_id="";
    String sublocation_id="";

    private ProgressDialog pDialog;
    private SlidingUpPanelLayout mLayout;
    TextView slidingTitle;
    InventoryAdapter inAdapter;
    ArrayList<Inventory> inList = new ArrayList<Inventory>();
    JSONArray inventoryJsonArray;
    java.util.HashMap<String,Integer> hashMapBarcodeQty=new HashMap<String,Integer>();
    String treeJSON="";
    Button expandButton, collapseButton;
    int[] colors ;
    int lastLevel = 0;
    List<NLevelItem> list;
    NLevelAdapter adapter;
    int counter=0;
    int parentId=-1;
    int maxLevel=0;

    //for storing category values
    java.util.HashMap<String,Integer> hashMapBarcode=new HashMap<String,Integer>();
    java.util.HashMap<String,Integer> hashMapBarcodePosition=new HashMap<String,Integer>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_take_a_summary);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        dbManager = new DBManager(BTStockTakeASummaryActivity.this);
        pDialog = new ProgressDialog(BTStockTakeASummaryActivity.this);
        StoreIdShopTV =(TextView) findViewById(R.id.StoreIdShopTV);

        lastLevel = Integer.parseInt(pref.getString(Constants.MAX_LEVEL,"0"));
        colors= getResources().getIntArray(R.array.colors_hex_code);
        hashMapBarcodeQty = (HashMap<String, Integer>)getIntent().getSerializableExtra("hashmap_barcode_qty");
        expandButton =(Button) findViewById(R.id.expandButton);
        collapseButton =(Button) findViewById(R.id.collapseButton);

        collapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ExpandCollapseAllAsyncTask(BTStockTakeASummaryActivity.this,false).execute();
            }
        });

        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new ExpandCollapseAllAsyncTask(BTStockTakeASummaryActivity.this,true).execute();
            }
        });

        try {
            locationObject = new JSONObject(getIntent().getStringExtra("location_JSON"));
            location_id=locationObject.getString("location_id");
            sublocation_id=locationObject.getString("sub_location_id");

            StoreIdShopTV.setText("Store ID: "+pref.getString(Constants.STORE_NUMBER,"0")+" / "+locationObject.getString("location")+" - "+locationObject.getString("sublocation"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        slidingTitle = (TextView) findViewById(R.id.titleSliding);
        slidingTitle.setText("Slide to view missing Barcode");

        totalScanTV =(TextView) findViewById(R.id.totalScanTV);
        totalScanTV.setText(getIntent().getStringExtra("scanned"));
        progressTV =(TextView) findViewById(R.id.progressTV);
        progressTV.setText(getIntent().getStringExtra("progress"));

        precentTV =(TextView) findViewById(R.id.precentTV);
        precentTV.setText(getIntent().getStringExtra("percentage"));

        timeTV =(TextView) findViewById(R.id.timeTV);
        timeTV.setText(getIntent().getStringExtra("time"));

        categoryListView = (ListView) findViewById(R.id.categoryList);

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                // Log.i("Side", "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                //  Log.i("Side", "onPanelStateChanged " + newState);
            }


        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        if(Constants.isNetworkAvailable(BTStockTakeASummaryActivity.this)) {
            new StockTakeSummaryAsyncTask(BTStockTakeASummaryActivity.this).execute(Constants.BASE_URL+"StockTakeSummary.php");
        }
        else
        {
            Constants.internetAlert(BTStockTakeASummaryActivity.this);
        }
    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    public void showPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window_search, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        closeBtn =(Button) popupView.findViewById(R.id.closeBtn);
        stopScanBtn =(Button) popupView.findViewById(R.id.stopScanBtn);
        stopScanBtn.setVisibility(Button.INVISIBLE);
        startScanBtn =(Button) popupView.findViewById(R.id.startScanBtn);
        vprogressbar =(ProgressBar) popupView.findViewById(R.id.progressBar);
        vprogressbar.setProgress(0);
        searchTextView = (TextView) popupView.findViewById(R.id.searchTextView);
        startScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchTextView.setText("Searching.....");

                int ret;


                ret= reader.RF_PerformInventoryForLocating(mLocateEPC);

                if (ret == SDConsts.RFResult.SUCCESS) {
                    // Toast.makeText(SerialLocationScanActivity.this, "Scan Started", Toast.LENGTH_SHORT).show();
                } else if (ret == SDConsts.RFResult.MODE_ERROR)
                    Toast.makeText(BTStockTakeASummaryActivity.this, "Scan failed, Please check RFR900 MODE", Toast.LENGTH_SHORT).show();
                else if (ret == SDConsts.RFResult.LOW_BATTERY)
                    Toast.makeText(BTStockTakeASummaryActivity.this, "Scan   failed, LOW_BATTERY", Toast.LENGTH_SHORT).show();
                else
                    System.out.println("Scan Failed");
            }
        });
        stopScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ret;
                ret = reader.RF_StopInventory();
                searchTextView.setText("Search Completed");
                popupWindow.dismiss();

                //update to server
                if(Constants.isNetworkAvailable(BTStockTakeASummaryActivity.this)) {
                    new UpdateEPCAsyncTask(BTStockTakeASummaryActivity.this).execute(Constants.BASE_URL + "UpdateEPCBarcodeStockTake.php");
                }
                else
                {
                    Constants.internetAlert(BTStockTakeASummaryActivity.this);
                }

                if (ret == SDConsts.RFResult.SUCCESS || ret == SDConsts.RFResult.NOT_INVENTORY_STATE) {

                } else if (ret == SDConsts.RFResult.STOP_FAILED_TRY_AGAIN)
                    Toast.makeText(BTStockTakeASummaryActivity.this, "Stop Inventory failed", Toast.LENGTH_SHORT).show();
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                int ret;
                ret = reader.RF_StopInventory();
            }
        });
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
            mSoundId = mSoundPool.load(BTStockTakeASummaryActivity.this, R.raw.beep, 1);
        }
    }

    private static class BarcodeHandler extends Handler {
        private final WeakReference<BTStockTakeASummaryActivity> mExecutor;
        public BarcodeHandler(BTStockTakeASummaryActivity f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            BTStockTakeASummaryActivity executor = mExecutor.get();
            if (executor != null) {
                executor.handleInventoryHandler(msg);
            }
        }
    }

    public void handleInventoryHandler(Message m) {
        //Log.d(TAG, "mInventoryHandler");
        // Log.d(TAG, "m arg1 = " + m.arg1 + " arg2 = " + m.arg2);
        switch (m.what) {
            case SDConsts.Msg.SDMsg:
                switch(m.arg1) {
                    case SDConsts.SDCmdMsg.TRIGGER_PRESSED:


                        break;

                    case SDConsts.SDCmdMsg.SLED_INVENTORY_STATE_CHANGED:

                        // In case of low battery on inventory, reason value is LOW_BATTERY
                        Toast.makeText(BTStockTakeASummaryActivity.this, "Inventory Stopped reason : " + m.arg2, Toast.LENGTH_SHORT).show();
                        break;

                    case SDConsts.SDCmdMsg.TRIGGER_RELEASED:
                        if (reader.RF_StopInventory() == SDConsts.SDResult.SUCCESS) {

                        }
                        break;
                    //You can receive this message every a minute. SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED
                    case SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED:
                        //Toast.makeText(BTStockTakeASummaryActivity.this, "Battery state = " + m.arg2, Toast.LENGTH_SHORT).show();
                        Log.d("serial summary", "Battery state = " + m.arg2);

                        break;
                    case SDConsts.SDCmdMsg.SLED_HOTSWAP_STATE_CHANGED:
                        if (m.arg2 == SDConsts.SDHotswapState.HOTSWAP_STATE)
                            Toast.makeText(BTStockTakeASummaryActivity.this, "HOTSWAP STATE CHANGED = HOTSWAP_STATE", Toast.LENGTH_SHORT).show();
                        else if (m.arg2 == SDConsts.SDHotswapState.NORMAL_STATE)
                            Toast.makeText(BTStockTakeASummaryActivity.this, "HOTSWAP STATE CHANGED = NORMAL_STATE", Toast.LENGTH_SHORT).show();

                        break;
                }
                break;

            case SDConsts.Msg.RFMsg:
                switch(m.arg1) {
                    case SDConsts.RFCmdMsg.INVENTORY:
                    case SDConsts.RFCmdMsg.READ:
                        if (m.arg2 == SDConsts.RFResult.SUCCESS) {

                        }
                        break;
                    case SDConsts.RFCmdMsg.LOCATE:
                        if (m.arg2 == SDConsts.RFResult.SUCCESS) {
                            if (m.obj != null  && m.obj instanceof Integer)
                                stopScanBtn.setVisibility(Button.VISIBLE);
                            processExactLocationData((int) m.obj);
                        }
                        break;
                }
                break;
            case SDConsts.Msg.BTMsg:
                if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_STATE_CHANGED) {
                    if (reader.BT_GetConnectState() != SDConsts.BTConnectState.CONNECTED) {

                    }
                }
                else if  (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_DISCONNECTED || m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_LOST) {

                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        dbManager.close();
        super.onDestroy();
    }

    private class SoundTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            vprogressbar.setProgress(mLocateValue);
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
                // TODO Auto-generated method stub
                locateTimeout();
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

    private void locateTimeout() {
        vprogressbar.setProgress(0);
    }

    private void processExactLocationData(int data) {
        //Log.d("LocationScan","processExactLocationData "+data);
        startLocateTimer();
        mLocateValue = data;
        vprogressbar.setProgress(data);
        searchTextView.setText(data+" %");
        if (mSoundTask == null) {
            mSoundTask = new BTStockTakeASummaryActivity.SoundTask();
            mSoundTask.execute();
        }
        else {
            if (mSoundTask.getStatus() == AsyncTask.Status.FINISHED) {
                mSoundTask.cancel(true);
                mSoundTask = null;
                mSoundTask = new BTStockTakeASummaryActivity.SoundTask();
                mSoundTask.execute();
            }
        }

    }

    @Override
    protected void onStart() {

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        createSoundPool();
        reader = BTReader.getReader(this, mBarcodeHandler);

        if (reader != null && reader.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED) {

            Toast.makeText(getBaseContext(), reader.BT_GetConnectedDeviceName() + "\n" + reader.BT_GetConnectedDeviceAddr(), Toast.LENGTH_SHORT).show();
            reader.SD_SetTriggerMode(SDConsts.SDTriggerMode.RFID);
        }
        else
            showMessageDialogWithBluetoothActivity("Bluetooth Status", "Bluetooth not connected");



        super.onStart();
    }

    private void showMessageDialogWithBluetoothActivity(String title, String message)
    {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(BTStockTakeASummaryActivity.this);
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

                            Intent intent = new Intent(BTStockTakeASummaryActivity.this, BTConnectivityActivity.class);
                            startActivity(intent);
                        }
                    }).show();
        }catch (WindowManager.BadTokenException e) {
            //use a log message
        }
    }


    @Override
    public void onStop() {
        reader.RF_StopInventory();

        if (mSoundPool != null)
            mSoundPool.release();
        mSoundFileLoadState = false;
        super.onStop();
    }


    private void showMessageDialog(String title, String message)
    {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(BTStockTakeASummaryActivity.this);
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;

            case R.id.sound: //Your task
                Intent intent2 = new Intent(BTStockTakeASummaryActivity.this, SoundManagerActivity.class);
                startActivity(intent2);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.serial_inventory_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    class StockTakeSummaryAsyncTask extends AsyncTask<String, String, String> {

        public StockTakeSummaryAsyncTask(Activity activity) {


        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Generating Stock Take Summary...");

            pDialog.show();

        }


        @Override
        protected String doInBackground(String... string) {
            String response = null;
            try {
                String customer_id = pref.getString(Constants.CUSTOMER_ID,"0");

                URL url = new URL(string[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(900000);
                conn.setConnectTimeout(900000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("X-Environment", "android");

                Gson gson = new GsonBuilder().create();

                Date date = new Date();
                String modifiedDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

                String time = sdf.format(cal.getTime());
                String user_id = pref.getString(Constants.USER_ID,"0");
                String sessionId = Constants.getSessionId(BTStockTakeASummaryActivity.this);
                String store_id = pref.getString(Constants.STORE_ID,"0");
                String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");
                String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");

                String urlParameters = "user_id="+user_id+"&session_id="+sessionId+"&stock_table_name=" + stock_table_name+"&product_table_name=" + product_table_name+"&customer_id="+customer_id+"&store_id="+store_id ;


                Log.d("StockTakeSummaryAsy", "urlParameters "+ urlParameters);

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

                    response = buffer.toString();
                    return  response;
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
            System.out.println("Response "+result);

            if (result != null) {
                try
                {
                    JSONObject jObj = new JSONObject(result);

                    //missing EPC
                    missingEPCBarcodeObj  = jObj.getJSONArray("missingEPCList");

                    for(int j=0 ; j< missingEPCBarcodeObj.length(); j++)
                    {
                        BarcodeEPCSearch beObj = new BarcodeEPCSearch();
                        beObj.setBarcode(missingEPCBarcodeObj.getJSONObject(j).getString("EAN"));
                        beObj.setEpc(missingEPCBarcodeObj.getJSONObject(j).getString("EPC"));
                        categoryList.add(beObj);
                    }
                    mAdapter = new MissingEPCAdapter(BTStockTakeASummaryActivity.this,  categoryList);
                    categoryListView.setAdapter(mAdapter);

                    categoryListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mPosition = position;
                            BarcodeEPCSearch item = (BarcodeEPCSearch) categoryListView.getItemAtPosition(position);
                            mLocateEPC = item.getEpc();
                            mLocateBarcode= item.getBarcode();
                            //setting max power
                            reader.RF_SetRadioPowerState(30);

                            SelectionCriterias s = new SelectionCriterias();
                            s.makeCriteria(SelectionCriterias.SCMemType.EPC, mLocateEPC,
                                    0, mLocateEPC.length() * 4,
                                    SelectionCriterias.SCActionType.ASLINVA_DSLINVB);
                            reader.RF_SetSelection(s);
                            showPopupWindowClick(view);
                        }
                    });


                    //summary UI
                    inventoryJsonArray = jObj.getJSONArray("epcList");

                    treeJSON =inventoryJsonArray.toString();

                    // Log.i("stocktake summary", "treeJSON " + treeJSON);



                    NLevelExpandableListView();

                    // Date d1 = new Date();

                    updateQuantityOfCategorySubcategory();



                } catch (Exception e) {

                    // e.printStackTrace();
                    Toast.makeText(BTStockTakeASummaryActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                }


            } else {

                Toast.makeText(BTStockTakeASummaryActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void NLevelExpandableListView(){

        listView = (ListView) findViewById(R.id.summaryListView);
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

            //System.out.println("jsonArrayStringList  "+jsonArrayStringList);
            //System.out.println("levelList  "+levelList);
            for (int i=0; i<length; i++){

                JSONObject itemObject = jsonArrayStringList.getJSONObject(i);

                // System.out.println("category0000  "+itemObject.getString("name"));
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
                    // System.out.println("category111  "+itemObject.getString("name"));
                    NLevelItem Parent = list.get(list.size() -1);
                    String scanQty = itemObject.getString("name");
                    Parent.setScanQty(Integer.parseInt(scanQty));
                    String ean = (String) ((SomeObject) Parent.getWrappedObject()).getName();
                    if(hashMapBarcodeQty.containsKey(ean) )
                    {
                        int totalQty =  hashMapBarcodeQty.get(ean);
                        Parent.setTotalQty(totalQty);
                    }
                    else
                    {
                        Parent.setTotalQty(0);
                    }


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



                return view;
            }
        });

        return superChild;
    }


    private void updateQuantityOfCategorySubcategory()
    {
        try
        {

            for (Integer pos : hashMapBarcodePosition.values()) {

                int position = pos;

                //System.out.println("barcode " + obj.getString("barcode") + " Position " + obj.getString("position") );
                if(list.size()>= position)
                {

                    NLevelItem Parent = list.get(position);
                    int qty = Parent.getTotalQty();
                    int scanQty = Parent.getScanQty();
                    int ll = Parent.getLevel();
                    int parentId = Parent.getParentId();
                    //System.out.println("barcode " + obj.getString("barcode") +" totalQty " + totalQty + " qty " + qty );
                    while(ll != -1 & parentId != -1)
                    {
                        NLevelItem superParent = list.get(parentId);

                        int qty2 = qty+superParent.getTotalQty();
                        int scanQty2 = scanQty+ superParent.getScanQty();
                        superParent.setTotalQty(qty2);
                        superParent.setScanQty(scanQty2);
                        ll = superParent.getLevel();
                        parentId = superParent.getParentId();
                    }

                }
            }

            //update UI of treelistview
            adapter.notifyDataSetChanged();

        }
        catch (Exception e)
        {
            e.printStackTrace();
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


    class UpdateEPCAsyncTask extends AsyncTask<String, String, String> {

        public UpdateEPCAsyncTask(Activity activity) {


        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Updating data...");

            pDialog.show();

        }


        @Override
        protected String doInBackground(String... string) {
            String response = null;
            try {
                String customer_id = pref.getString(Constants.CUSTOMER_ID,"0");

                URL url = new URL(string[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(900000);
                conn.setConnectTimeout(900000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("X-Environment", "android");

                Gson gson = new GsonBuilder().create();

                Date date = new Date();
                String modifiedDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

                String time = sdf.format(cal.getTime());
                String user_id = pref.getString(Constants.USER_ID,"0");
                String sessionId = Constants.getSessionId(BTStockTakeASummaryActivity.this);

                String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");

                String urlParameters = "user_id="+user_id+"&session_id="+sessionId+"&barcode=" + mLocateBarcode+"&epc=" + mLocateEPC+"&location_id="+location_id+"&sub_location_id="+sublocation_id+"&stock_date="+modifiedDate+"&stock_time="+time+"&stock_table_name="+stock_table_name+"&customer_id="+customer_id ;


                Log.d("UpdateEPCAsyncTask", "urlParameters "+ urlParameters);

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

                    response = buffer.toString();
                    return  response;
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
            System.out.println("Response "+result);

            if (result != null) {
                try
                {
                    JSONObject jObj = new JSONObject(result);
                    if( jObj.getString("status").compareTo("1") == 0)
                    {
                        categoryList.remove(mPosition);
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(BTStockTakeASummaryActivity.this, "Data updated", Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                        Toast.makeText(BTStockTakeASummaryActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {

                    // e.printStackTrace();
                    Toast.makeText(BTStockTakeASummaryActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                }


            } else {

                Toast.makeText(BTStockTakeASummaryActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
            }
        }
    }

}