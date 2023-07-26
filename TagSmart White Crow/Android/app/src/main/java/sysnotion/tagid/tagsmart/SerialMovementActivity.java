package sysnotion.tagid.tagsmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;
import sysnotion.tagid.tagsmart.db.DBManager;
import sysnotion.tagid.tagsmart.model.EPCBarcodeInventory;
import sysnotion.tagid.tagsmart.nLevel.NLevelAdapter;
import sysnotion.tagid.tagsmart.nLevel.NLevelItem;
import sysnotion.tagid.tagsmart.nLevel.NLevelView;
import sysnotion.tagid.tagsmart.nLevel.SomeObject;
import sysnotion.tagid.tagsmart.utils.Constants;
import sysnotion.tagid.tagsmart.utils.PermissionHelper;

import android.annotation.TargetApi;
import android.app.AlertDialog;
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
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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
import java.util.concurrent.CopyOnWriteArrayList;

public class SerialMovementActivity extends AppCompatActivity {

    private static final String TAG = SerialMovementActivity.class.getSimpleName();
    SharedPreferences pref;
    private DBManager dbManager;
    JSONObject locationObject;
    TextView StoreIdShopTV, progressTV, precentTV, timeTV, totalScanTV;
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
    ArrayList<String> selectedBarcodeList = new ArrayList<String>();

    int totalQty=0;
    int scannedQty = 0;
    String LastSelectedCategory ="";
    private SoundPool mSoundPool;
    private int mSoundId;
    private TimerTask mLocateTimerTask;
    private Timer mClearLocateTimer;

    private boolean mSoundFileLoadState;

    private SerialMovementActivity.SoundTask mSoundTask;

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
    private Reader mReader;
    private final SerialMovementActivity.BarcodeHandler mBarcodeHandler = new SerialMovementActivity.BarcodeHandler(this);
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
    SerialMovementActivity.ProcessEPCAndUpdateUIAsyncTask processEPCAndUpdateUIAsyncTask;
    JSONArray barcodePositionArray = new JSONArray();
    int lastLevel = 0;
    private ProgressDialog pDialog;
    String barcodesString= new String();
    SerialMovementActivity.UpdateUIAsyncTask updateUIAsyncTask;
    int UIJSONLength=0;
    ArrayList<String> barcodeList = new ArrayList<String>();
    int EPCAnchorPosition=0;

    //test
   /* int testDataCounter = 0;
    String testEPC[] ={"30361f84cc3f4100000f90ae","30361f84cc3f4100000faabb","30361f84cc57ab40000f90b1","30361f84cc57ab40000f90a7","30361f84cc57ab40000f90a9","30361f84cc57ab40000f90a0","30361f84cc3f4100000f906f","30361f84cc3f4100000f909f","30361f84cc57ab40000f90a5","30361f84cc3f4100000f90af","30361f84cc57ab40000f90a6","30361f84cc3f4100000f90b5","30361f84cc3f4100000f906d","30361f84cc57ab40000f90aa","30361f84cc3f4100000f906e","30361f84cc57ab40000f907e","30361f84cc3f4100000f90ad","30361f84cc57ab40000f90b4","30361f84cc3f4100000f90b0","30361f84cc3f4100000f90a4","30361f84cc57ab40000f907d","30361f84cc57ab40000f9090","30361f84cc57ab40000f90b8","30361f84cc57ab40000f90a8","30361f84cc1c6040000f90ab","30361f84cc57ab40000f90ac","30361f84cc3f4100000f9061"
    };*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_movement);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        dbManager = new DBManager(SerialMovementActivity.this);
        dbManager.open();
        dbManager.turcateStockTakeChecksum();

        pDialog = new ProgressDialog(SerialMovementActivity.this);

        sessionId = Constants.getSessionId(SerialMovementActivity.this);
        isSessionNew = Constants.isSessionNew(SerialMovementActivity.this);
        // Log.d("InventoryA","sessionId "+sessionId);

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
        selectedBarcodeList =  Constants.getArrayList(Constants.CATEGORY_ARRAY_JSON, SerialMovementActivity.this);
        colors= getResources().getIntArray(R.array.colors_hex_code);

        if(Constants.isNetworkAvailable(SerialMovementActivity.this)) {
            new SerialMovementActivity.LoadTreeAsyncTask(SerialMovementActivity.this).execute(Constants.DOMAIN_URL+"product_tree_by_barcode.php");
        }
        else
        {
            Constants.internetAlert(SerialMovementActivity.this);
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
                new SerialMovementActivity.ExpandCollapseAllAsyncTask(SerialMovementActivity.this,false).execute();
            }
        });

        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SerialMovementActivity.ExpandCollapseAllAsyncTask(SerialMovementActivity.this,true).execute();
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
                                            processEPCAndUpdateUIAsyncTask = new SerialMovementActivity.ProcessEPCAndUpdateUIAsyncTask();
                                            processEPCAndUpdateUIAsyncTask.execute(Constants.BASE_URL + "UpdateMoving.php");
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
                                            updateUIAsyncTask = new SerialMovementActivity.UpdateUIAsyncTask();
                                            updateUIAsyncTask.execute(Constants.BASE_URL + "GetRFIDQuantity.php");
                                        }

                                    }
                                });
                            }
                        };
                        timer2.schedule(task2, 7000, 7000);



                    } else if (ret == SDConsts.RFResult.MODE_ERROR)
                        Toast.makeText(SerialMovementActivity.this, "Start Inventory failed, Please check RFR900 MODE", Toast.LENGTH_SHORT).show();
                    else if (ret == SDConsts.RFResult.LOW_BATTERY)
                        Toast.makeText(SerialMovementActivity.this, "Start Inventory failed, LOW_BATTERY", Toast.LENGTH_SHORT).show();
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
                        SerialMovementActivity.this);

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

                                if(Constants.isNetworkAvailable(SerialMovementActivity.this)) {
                                    new  RollbackStockTakeAsyncTask(SerialMovementActivity.this).execute(Constants.BASE_URL+"RollBackStockTake.php");
                                }
                                else
                                {
                                    Constants.internetAlert(SerialMovementActivity.this);
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
                                    Toast.makeText(SerialMovementActivity.this, "Stop Inventory failed", Toast.LENGTH_SHORT).show();

                                dialog.cancel();


                            }
                        })
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
                                    Toast.makeText(SerialMovementActivity.this, "Stop Inventory failed", Toast.LENGTH_SHORT).show();

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
                            JSONObject jObj = new JSONObject();
                            jObj.put("barcode",(String) ((SomeObject) Parent.getWrappedObject()).getName());
                            jObj.put("position",counter);
                            barcodePositionArray.put(jObj);
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
                            Toast.makeText(SerialMovementActivity.this, "Clicked on: "+Title, Toast.LENGTH_SHORT).show();
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
            mSoundId = mSoundPool.load(SerialMovementActivity.this, R.raw.beep, 1);
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
        mReader = Reader.getReader(this, mBarcodeHandler);
        if (mReader != null)
            openResult = mReader.SD_Open();
        if (openResult == SDConsts.SD_OPEN_SUCCESS)
        {
            if(mReader.SD_Wakeup()==SDConsts.SDResult.SUCCESS)
            {

                if (mReader.SD_GetConnectState() == SDConsts.SDConnectState.CONNECTED)
                {
                    Toast.makeText(getBaseContext(),"SD Already Connected",Toast.LENGTH_SHORT).show();
                }else{
                    if(mReader.SD_Connect()==SDConsts.SDResult.SUCCESS)
                    {

                        Toast.makeText(getBaseContext(),"SD Connected",Toast.LENGTH_SHORT).show();
                    }else{

                        showMessageDialog("SD Status", "SD Not Connected");
                    }
                }


            }
        }else if (openResult == SDConsts.RF_OPEN_FAIL){
            showMessageDialog("SD Status", "SD Open Fail");
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
        if (updateUIAsyncTask!= null) {
            updateUIAsyncTask.cancel(true);
        }
        scanFinished =1;
        dismissProgressDialog();
        dbManager.close();

        super.onStop();
    }


    private static class BarcodeHandler extends Handler {
        private final WeakReference<SerialMovementActivity> mExecutor;
        public BarcodeHandler(SerialMovementActivity f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            SerialMovementActivity executor = mExecutor.get();
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
                        Toast.makeText(SerialMovementActivity.this, "Inventory Stopped reason : " + m.arg2, Toast.LENGTH_SHORT).show();

                        break;

                    case SDConsts.SDCmdMsg.TRIGGER_RELEASED:

                        break;
                    //You can receive this message every a minute. SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED
                    case SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED:
                        //Toast.makeText(SerialMovementActivity.this, "Battery state = " + m.arg2, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Battery state = " + m.arg2);
                        break;
                    case SDConsts.SDCmdMsg.SLED_HOTSWAP_STATE_CHANGED:
                        if (m.arg2 == SDConsts.SDHotswapState.HOTSWAP_STATE)
                            Toast.makeText(SerialMovementActivity.this, "HOTSWAP STATE CHANGED = HOTSWAP_STATE", Toast.LENGTH_SHORT).show();
                        else if (m.arg2 == SDConsts.SDHotswapState.NORMAL_STATE)
                            Toast.makeText(SerialMovementActivity.this, "HOTSWAP STATE CHANGED = NORMAL_STATE", Toast.LENGTH_SHORT).show();

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

                                        /*
                                        //test
                                        if(testDataCounter == (testEPC.length -1))
                                            testDataCounter =0;

                                        EPCNumber = testEPC[ testDataCounter];
                                        testDataCounter++;
                                        ////// test over
                                        */

                                        uniqueEPCList.addIfAbsent(EPCNumber);

                                        totalScanTV.setText(uniqueEPCList.size()+"");

                                        if (mSoundTask == null) {
                                            mSoundTask = new SerialMovementActivity.SoundTask();
                                            mSoundTask.execute();
                                        }
                                        else {
                                            if (mSoundTask.getStatus() == AsyncTask.Status.FINISHED) {
                                                mSoundTask.cancel(true);
                                                mSoundTask = null;
                                                mSoundTask = new SerialMovementActivity.SoundTask();
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
                    if (mReader.SD_GetConnectState() != SDConsts.BTConnectState.CONNECTED) {
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
        getMenuInflater().inflate(R.menu.serial_inventory_menu,menu);
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
                Intent intent2 = new Intent(SerialMovementActivity.this, SoundManagerActivity.class);
                startActivity(intent2);

                return true;

            case R.id.bluetooth_reader: //Your task
                Intent intent3 = new Intent(SerialMovementActivity.this, BTMovementActivity.class);
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(SerialMovementActivity.this);

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
            AlertDialog.Builder dialog = new AlertDialog.Builder(SerialMovementActivity.this);
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(SerialMovementActivity.this);
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

                            Intent intent = new Intent(SerialMovementActivity.this, BTConnectivityActivity.class);
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(SerialMovementActivity.this);
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
                String urlParameters = "customer_id=" + customer_id+"&session_id=-1&is_new_session=1&json_barcode="+jsArray;
                //System.out.println("urlParameters " + urlParameters);

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



        }
    }

    private void updateQuantityOfCategorySubcategory()
    {
        try
        {
            scannedQty = scannedQty + 0;
            totalQty = 0;
            for (int d = 0; d < barcodePositionArray.length(); d++) {

                JSONObject obj = barcodePositionArray.getJSONObject(d);
                int position = Integer.parseInt(obj.getString("position"));

                //System.out.println("barcode " + obj.getString("barcode") + " Position " + obj.getString("position") );
                if(list.size()>= position)
                {

                    NLevelItem Parent = list.get(position);
                    int qty = Parent.getTotalQty();
                    int ll = Parent.getLevel();
                    int parentId = Parent.getParentId();
                    totalQty = totalQty +qty;
                    //System.out.println("Parent ID " + parentId + " Level " + ll );
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
                progressTV.setText(scannedQty );
                precentTV.setText("0 %");
            }
            else
            {
                progressTV.setText(scannedQty );
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

                String urlParameters = "customer_id=" +customer_id+"&user_id="+user_id+"&session_id=-1&json_barcode="+jsArray+"&json_epc="+epcArray+"&location_id="+location_id+"&sub_location_id="+sublocation_id+"&stock_date="+modifiedDate+"&stock_time="+time+"&stock_table_name="+stock_table_name+"&is_scan_finish=0";
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
                    if (isFinsihButtonPressed == 1) {
                        Intent intent = new Intent(SerialMovementActivity.this , MovingSummaryActivity.class);
                        intent.putExtra("location_JSON",locationObject.toString());
                        intent.putExtra("scanned",totalScanTV.getText().toString());
                        intent.putExtra("progress",progressTV.getText().toString());
                        intent.putExtra("percentage",precentTV.getText().toString());
                        intent.putExtra("time",timeTV.getText().toString());

                        startActivity(intent);

                        finish();
                    }
                }
                else
                {
                    Toast.makeText(SerialMovementActivity.this, "Something went wrong during updating epc to server", Toast.LENGTH_LONG).show();
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
                JSONArray jsArray = new JSONArray(barcodeList);

                String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");

                String urlParameters = "customer_id=" +customer_id+"&json_barcode="+jsArray+"&stock_table_name="+stock_table_name;
                // System.out.println("jsArray " + jsArray);

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
                        scannedQty = Integer.parseInt(jo.getString("scan_total_qty"));
                        resetRFIDScanQTY();

                        for (int u = 0; u < jsonArray.length(); u++)
                        {
                            JSONObject temp = jsonArray.getJSONObject(u);
                            String ean = temp.getString("barcode");
                            int rfidq = Integer.parseInt(temp.getString("rfid_quantity"));

                            try
                            {
                                JSONObject obj = barcodePositionArray.getJSONObject(u);
                                if (rfidq != 0) {

                                    int position = Integer.parseInt(obj.getString("position"));


                                    if (list.size() >= position) {

                                        NLevelItem Parent = list.get(position);

                                        Parent.setScanQty(rfidq);
                                        int qty = Parent.getScanQty();
                                        int ll = Parent.getLevel();
                                        int parentId = Parent.getParentId();

                                        while (ll != -1 & parentId != -1) {
                                            NLevelItem superParent = list.get(parentId);

                                            int qty2 = qty + superParent.getScanQty();
                                            superParent.setScanQty(qty2);
                                            ll = superParent.getLevel();
                                            parentId = superParent.getParentId();
                                        }

                                    }

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
            //  Log.d("StockTakeOptionA","UIUpdate response "+result);
            try {

                adapter.notifyDataSetChanged();
                if(scannedQty == 0)
                {
                    progressTV.setText(scannedQty );
                    precentTV.setText("0 %");
                }
                else
                {
                    progressTV.setText(scannedQty );
                    float per = (scannedQty*100) / totalQty;
                    precentTV.setText(per+" %");
                }


            }catch (Exception e) {
                e.printStackTrace();
            }



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

                String urlParameters = "customer_id=" +customer_id+"&user_id="+user_id+"&session_id="+sessionId+"&stock_table_name="+stock_table_name;
                //System.out.println("epcArray " + epcArray);

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
                    resetRFIDScanQTY();
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
            adapter.notifyDataSetChanged();

            System.out.println("response abort scan " + result );
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("msg");

                scannedQty =0;
                progressTV.setText(scannedQty );
                precentTV.setText("0 %");


                if (status.compareTo("1") == 0) {
                    Toast.makeText(SerialMovementActivity.this, msg, Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(SerialMovementActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }catch(Exception e)
            {
                e.printStackTrace();;
            }


        }
    }

}