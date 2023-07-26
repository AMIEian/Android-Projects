package sysnotion.tagid.tagsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import co.kr.bluebird.sled.BTReader;
import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;
import sysnotion.tagid.tagsmart.adapter.ExpandableListAdapter;
import sysnotion.tagid.tagsmart.model.EPCBarcodeInventory;
import sysnotion.tagid.tagsmart.utils.Constants;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
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
import java.util.concurrent.CopyOnWriteArrayList;

public class ReplenishmentMoveActivity  extends AppCompatActivity {
    private static final String TAG = ReplenishmentMoveActivity.class.getSimpleName();
    SharedPreferences pref;
    TextView StoreIdShopTV, progressTV, precentTV, timeTV, totalScanTV;
    Button inventoryScanStartBtn, finishScanBtn ;
    ExpandableListView categoryProgressList;
    String treeJSON="";
    Button expandButton, collapseButton;

    int[] colors ;
    int counter=0;
    int parentId=-1;
    JSONArray arr;
    int maxLevel=0;

    int totalQty=0;
    int scannedQty = 0;
    String LastSelectedCategory ="";
    private SoundPool mSoundPool;
    private int mSoundId;
    private TimerTask mLocateTimerTask;
    private Timer mClearLocateTimer;

    private boolean mSoundFileLoadState;

    private ReplenishmentMoveActivity.SoundTask mSoundTask;

    //for storing category values
    java.util.HashMap<String,String> hashMap=new HashMap<String,String>();

    CopyOnWriteArrayList<EPCBarcodeInventory> categoryArray= new CopyOnWriteArrayList<EPCBarcodeInventory>();
    CopyOnWriteArrayList<EPCBarcodeInventory> shortListedEPCArray= new CopyOnWriteArrayList<EPCBarcodeInventory>();


    //timer
    private Timer scanTimer;
    int  time =0;
    int isTimerPause=0;
    public boolean timerstate = false;

    //reader
    private BTReader mReader;
    private final ReplenishmentMoveActivity.BarcodeHandler mBarcodeHandler = new ReplenishmentMoveActivity.BarcodeHandler(this);
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
    ReplenishmentMoveActivity.ProcessEPCAndUpdateUIAsyncTask processEPCAndUpdateUIAsyncTask;
    JSONArray barcodePositionArray = new JSONArray();
    private ProgressDialog pDialog;
    ReplenishmentMoveActivity.UpdateUIAsyncTask updateUIAsyncTask;
    int UIJSONLength=0;
    int EPCAnchorPosition=0;
    ExpandableListAdapter inventoryAdapter;
    List<String> expandableTitleList = new ArrayList<String>();
    HashMap<String, List<String>> expandableDetailList = new HashMap<String, List<String>>();
    String timeStamp ="";
    ArrayList<String> selectedEPCList = new ArrayList<String>();


   /*//TEST
    int testDataCounter = 0;
    String testEPC[] ={"30361f84cc3f4100000f906f","30361f84cc3f4100000f909f","30361f84cc3f4100000f906e","30361f84cc57ab40000f90a7","30361f84cc3f4100000faabb"};
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_replenishment_move);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        selectedEPCList =  Constants.getArrayList(Constants.CATEGORY_ARRAY_JSON, ReplenishmentMoveActivity.this);
        System.out.println(selectedEPCList);
        timeStamp = sdf.format(cal.getTime());

        StoreIdShopTV =(TextView) findViewById(R.id.StoreIdShopTV);
        totalScanTV =(TextView) findViewById(R.id.totalScanTV);
        totalScanTV.setText("0");
        StoreIdShopTV.setText("Store ID: "+pref.getString(Constants.STORE_NUMBER,"0"));
        location_id="3"; //shop
        sublocation_id="4"; //shop floor

        pDialog = new ProgressDialog(ReplenishmentMoveActivity.this);

        sessionId = Constants.REPLENISHMENT_SESSION;
        isSessionNew = "-1";
        progressTV =(TextView) findViewById(R.id.progressTV);


        precentTV =(TextView) findViewById(R.id.precentTV);

        timeTV =(TextView) findViewById(R.id.timeTV);
        timeTV.setText("00:00:00");


        expandButton =(Button) findViewById(R.id.expandButton);
        collapseButton =(Button) findViewById(R.id.collapseButton);

        collapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // new  ExpandCollapseAllAsyncTask(ReplenishmentMoveActivity.this,false).execute();
                for(int i=0; i < inventoryAdapter.getGroupCount(); i++)
                    categoryProgressList.collapseGroup(i);
            }
        });

        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // new  ExpandCollapseAllAsyncTask(ReplenishmentMoveActivity.this,true).execute();
                for(int i=0; i < inventoryAdapter.getGroupCount(); i++)
                    categoryProgressList.expandGroup(i);
            }
        });

        inventoryScanStartBtn =(Button) findViewById(R.id.inventoryScanStartBtn);
        finishScanBtn =(Button) findViewById(R.id.finishScanBtn);

        categoryProgressList = (ExpandableListView) findViewById(R.id.categoryProgressList);


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

                        //periodically call read EPC Array asyntask for simultaneous reading
                        final Handler handler = new Handler();
                        Timer timer = new Timer();
                        TimerTask task;
                        task = new TimerTask() {
                            @Override
                            public void run() {
                                handler.post(new Runnable() {
                                    public void run() {
                                        if(scanFinished == 0)
                                        {
                                            scanFinished =1;
                                            Log.d(TAG, "processEPCAndUpdateUIAsyncTask Called scanFinished "+scanFinished);
                                            processEPCAndUpdateUIAsyncTask = new ReplenishmentMoveActivity.ProcessEPCAndUpdateUIAsyncTask();
                                            processEPCAndUpdateUIAsyncTask.execute(Constants.BASE_URL + "UpdateReplenishmentMoving.php");
                                        }

                                    }
                                });
                            }
                        };
                        timer.schedule(task, 3000, 3000);

                        /************UI Update asyn task******/
                        final Handler handler2 = new Handler();
                        Timer timer2 = new Timer();
                        TimerTask task2;
                        task2 = new TimerTask() {
                            @Override
                            public void run() {
                                handler2.post(new Runnable() {
                                    public void run() {
                                        if(uiUpdateFinished == 0)
                                        {
                                            uiUpdateFinished =1;
                                            Log.d(TAG, "updateUIAsyncTask Called uiUpdateFinished "+uiUpdateFinished);
                                            updateUIAsyncTask = new ReplenishmentMoveActivity.UpdateUIAsyncTask();
                                            updateUIAsyncTask.execute(Constants.BASE_URL + "MovingSummary.php");
                                        }

                                    }
                                });
                            }
                        };
                        timer2.schedule(task2, 14000, 14000);



                    } else if (ret == SDConsts.RFResult.MODE_ERROR)
                        Toast.makeText(ReplenishmentMoveActivity.this, "Start Inventory failed, Please check RFR900 MODE", Toast.LENGTH_SHORT).show();
                    else if (ret == SDConsts.RFResult.LOW_BATTERY)
                        Toast.makeText(ReplenishmentMoveActivity.this, "Start Inventory failed, LOW_BATTERY", Toast.LENGTH_SHORT).show();
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
                        ReplenishmentMoveActivity.this);

                // set title
                alertDialogBuilder.setTitle("Select Action");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want?")
                        .setCancelable(false)

                        .setNegativeButton("Submit Stock Scan", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
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

                                } else if (ret == SDConsts.RFResult.STOP_FAILED_TRY_AGAIN)
                                    Toast.makeText(ReplenishmentMoveActivity.this, "Stop Inventory failed", Toast.LENGTH_SHORT).show();

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
            mSoundId = mSoundPool.load(ReplenishmentMoveActivity.this, R.raw.beep, 1);
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
                Toast.makeText(getBaseContext(), mReader.BT_GetConnectedDeviceName() + "\n" + mReader.BT_GetConnectedDeviceAddr(), Toast.LENGTH_SHORT).show();
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
        if (updateUIAsyncTask!= null) {
            updateUIAsyncTask.cancel(true);
        }
        scanFinished =1;
        dismissProgressDialog();

        super.onStop();
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();
    }



    private static class BarcodeHandler extends Handler {
        private final WeakReference<ReplenishmentMoveActivity> mExecutor;
        public BarcodeHandler(ReplenishmentMoveActivity f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            ReplenishmentMoveActivity executor = mExecutor.get();
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
                        mInventory = false;
                        pauseStopwatch();
                        // In case of low battery on inventory, reason value is LOW_BATTERY
                        Toast.makeText(ReplenishmentMoveActivity.this, "Inventory Stopped reason : " + m.arg2, Toast.LENGTH_SHORT).show();

                        break;

                    case SDConsts.SDCmdMsg.TRIGGER_RELEASED:

                        break;
                    //You can receive this message every a minute. SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED
                    case SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED:
                        //Toast.makeText(ReplenishmentMoveActivity.this, "Battery state = " + m.arg2, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Battery state = " + m.arg2);
                        break;
                    case SDConsts.SDCmdMsg.SLED_HOTSWAP_STATE_CHANGED:
                        if (m.arg2 == SDConsts.SDHotswapState.HOTSWAP_STATE)
                            Toast.makeText(ReplenishmentMoveActivity.this, "HOTSWAP STATE CHANGED = HOTSWAP_STATE", Toast.LENGTH_SHORT).show();
                        else if (m.arg2 == SDConsts.SDHotswapState.NORMAL_STATE)
                            Toast.makeText(ReplenishmentMoveActivity.this, "HOTSWAP STATE CHANGED = NORMAL_STATE", Toast.LENGTH_SHORT).show();

                        break;
                }
                break;

            case SDConsts.Msg.RFMsg:
                switch(m.arg1) {
                    case SDConsts.RFCmdMsg.INVENTORY:
                    case SDConsts.RFCmdMsg.READ:
                        if (m.arg2 == SDConsts.RFResult.SUCCESS) {
                            if (m.obj != null  && m.obj instanceof String) {
                                String data = (String) m.obj;
                                if (data != null && isFinsihButtonPressed == 0)
                                {
                                    if (data.contains(";")) {

                                        //full tag example(with optional value)
                                        //1) RF_PerformInventory => "3000123456783333444455556666;rssi:-54.8"
                                        //2) RF_PerformInventoryWithLocating => "3000123456783333444455556666;loc:64"
                                        int infoTagPoint = data.indexOf(';');
                                        data = data.substring(0, infoTagPoint);
                                        //Log.d(TAG, "data tag = " + data);
                                        String EPCNumber  = data.trim();


                                        /*/test
                                        if(testDataCounter == (testEPC.length -1))
                                            testDataCounter =0;

                                        EPCNumber = testEPC[ testDataCounter];
                                        testDataCounter++;
                                        ////// test over*/

                                        if(selectedEPCList.contains(EPCNumber))
                                        {
                                            uniqueEPCList.addIfAbsent(EPCNumber);
                                        }


                                        totalScanTV.setText(uniqueEPCList.size()+"");

                                        if (mSoundTask == null) {
                                            mSoundTask = new ReplenishmentMoveActivity.SoundTask();
                                            mSoundTask.execute();
                                        }
                                        else {
                                            if (mSoundTask.getStatus() == AsyncTask.Status.FINISHED) {
                                                mSoundTask.cancel(true);
                                                mSoundTask = null;
                                                mSoundTask = new ReplenishmentMoveActivity.SoundTask();
                                                mSoundTask.execute();
                                            }
                                        }
                                    }
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
                        if (mInventory) {
                            pauseStopwatch();
                            mInventory = false;
                        }
                    }
                }
                else if  (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_DISCONNECTED || m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_LOST) {
                    if (mInventory) {
                        pauseStopwatch();
                        mInventory = false;
                    }
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

            case R.id.sound: //Your task
                Intent intent2 = new Intent(ReplenishmentMoveActivity.this, SoundManagerActivity.class);
                startActivity(intent2);
                return true;

            case R.id.bluetooth_settings: //Your task
                Intent intent = new Intent(ReplenishmentMoveActivity.this, BTConnectivityActivity.class);
                startActivity(intent);
                return true;

            case R.id.serial_reader:
                Intent intent3 = new Intent(ReplenishmentMoveActivity.this, SerialReplenishmentMoveActivity.class);
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(ReplenishmentMoveActivity.this);

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

    private void showMessageFilterSelection()
    {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ReplenishmentMoveActivity.this);
            dialog.setTitle("Filter Selection Details")
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage(new Gson().toJson(hashMap))
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            dialoginterface.cancel();
                        }
                    }).show();
        }catch (WindowManager.BadTokenException e) {
            //use a log message
        }

    }

    private void showMessageDialogWithBluetoothActivity(String title, String message)
    {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ReplenishmentMoveActivity.this);
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

                            Intent intent = new Intent(ReplenishmentMoveActivity.this, BTConnectivityActivity.class);
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(ReplenishmentMoveActivity.this);
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
            dismissProgressDialog();

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

                ArrayList<String>EPCArrayList = new ArrayList<String>();
                for(int s =EPCAnchorPosition; s<uniqueEPCList.size(); s++)
                {
                    EPCArrayList.add(uniqueEPCList.get(s));
                }
                //System.out.println(EPCArrayList);
                EPCAnchorPosition = uniqueEPCList.size();
                JSONArray epcArray = new JSONArray(EPCArrayList);

                Date date = new Date();
                String modifiedDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

                String time = sdf.format(cal.getTime());
                String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");
                String store_id = pref.getString(Constants.STORE_ID,"0");

                String urlParameters = "customer_id=" +customer_id+"&user_id="+user_id+"&session_id="+sessionId+"&json_epc="+epcArray+"&location_id="+location_id+"&sub_location_id="+sublocation_id+"&stock_date="+modifiedDate+"&stock_time="+time+"&stock_table_name="+stock_table_name+"&is_scan_finish="+isFinsihButtonPressed+"&store_id="+store_id;
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

            //System.out.println("ProcessEPCAndUpdateUIAsyncTask Resp " + result);
            /*System.out.println("currentTimeMillis after " + System.currentTimeMillis());*/

            scanFinished =0;
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                if(status.compareTo("1") == 0)
                {

                }
                else
                {
                    Toast.makeText(ReplenishmentMoveActivity.this, "Something went wrong during updating epc to server", Toast.LENGTH_LONG).show();
                }

            }catch (Exception e) {
                e.printStackTrace();
            }



        }
    }

    class UpdateUIAsyncTask extends AsyncTask<String, String, String> {

        // tempScanQty is used to avoid scanqty number flikering effect on UI

        public UpdateUIAsyncTask() {

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
                String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");
                String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
                Date date = new Date();
                String modifiedDate= new SimpleDateFormat("yyyy-MM-dd").format(date);

                String urlParameters = "customer_id=" +customer_id+"&stock_table_name="+stock_table_name+"&user_id="+user_id+"&session_id="+sessionId+"&stock_date="+modifiedDate+"&product_table_name="+product_table_name+"&min_time_stamp="+timeStamp;
                System.out.println("min_time_stamp " + timeStamp);

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

                    /*********************************/
                    if(buffer.length() != 0){
                        // System.out.println("### buffer " + buffer );

                        JSONObject  jo = new JSONObject(buffer.toString());
                        JSONArray jsonArray = jo.getJSONArray("scan_values");
                        UIJSONLength = jsonArray.length();
                        scannedQty = Integer.parseInt(jo.getString("rfid_total_qty"));
                        totalQty = Integer.parseInt(jo.getString("scan_total_qty"));
                        expandableTitleList.clear();
                        expandableDetailList.clear();

                        for (int u = 0; u < jsonArray.length(); u++)
                        {
                            JSONObject temp = jsonArray.getJSONObject(u);
                            String main_category = temp.getString("main_category");
                            expandableTitleList.add(main_category);
                            JSONArray jsonBarcodeArray = temp.getJSONArray("barcode_details");
                            List<String> tempList = new ArrayList<String>();
                            for (int b = 0; b < jsonBarcodeArray.length(); b++)
                            {
                                JSONObject temp2 = jsonBarcodeArray.getJSONObject(b);
                                String bar = temp2.getString("barcode");
                                String qt = temp2.getString("rfid_qty")+" / "+temp2.getString("total_qty");
                                String val = bar+"|"+qt;
                                tempList.add(val);
                            }
                            expandableDetailList.put(main_category, tempList);

                        }
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
            uiUpdateFinished = 0;
            //Log.d("SerialProcessMovement","UIUpdate response "+result);
            try {

                inventoryAdapter = new ExpandableListAdapter(ReplenishmentMoveActivity.this, expandableTitleList, expandableDetailList);
                categoryProgressList.setAdapter(inventoryAdapter);
                inventoryAdapter.notifyDataSetChanged();

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


}