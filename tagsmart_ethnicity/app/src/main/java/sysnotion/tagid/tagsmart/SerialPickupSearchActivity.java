package sysnotion.tagid.tagsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;
import sysnotion.tagid.tagsmart.adapter.ReplenishSearchAdapter;
import sysnotion.tagid.tagsmart.model.Inventory;
import sysnotion.tagid.tagsmart.utils.Constants;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class SerialPickupSearchActivity extends AppCompatActivity {

        private static final String TAG = SerialPickupSearchActivity.class.getSimpleName();
        SharedPreferences pref;
        ArrayList<String> selectedBarcodeList = new ArrayList<String>();
        CopyOnWriteArrayList<String> selectedEPCList = new CopyOnWriteArrayList<String>();
        TableLayout headerTable, mainTable;
        TextView StoreIdShopTV, searchTextView;
        private ProgressDialog pDialog;

        private SoundPool mSoundPool;
        private int mSoundId;
        private TimerTask mLocateTimerTask;
        private Timer mClearLocateTimer;

        private boolean mSoundFileLoadState;

        private SerialPickupSearchActivity.SoundTask mSoundTask;
        //reader
        private Reader mReader;
        private final SerialPickupSearchActivity.BarcodeHandler mBarcodeHandler = new SerialPickupSearchActivity.BarcodeHandler(this);
        private boolean mMask = false;
        private boolean mInventory = false;
        private boolean mIsTurbo = true;
        private boolean mIgnorePC = true;
        private boolean mFile = false;
        PopupWindow popupWindow;
        ProgressBar vprogressbar;
        Button stopScanBtn, startScanBtn, closeBtn, nextMoveBtn;
        String mLocateEPC = new String();
        ListView listView1;

        JSONArray categoryHeaderArray, barcodeDetailsArray;
        ArrayList<Inventory>epcList = new ArrayList<Inventory>();
        ArrayList<String>tempepcList = new ArrayList<String>();
        ReplenishSearchAdapter adapter;
        int tableRowIndex =-1, NUMBER_OF_COLUMNS=0;

    /*//TEST
    int testDataCounter = 0;
    String testEPC[] ={"30361f84cc3f4100000f906f","30361f84cc3f4100000f909f","30361f84cc3f4100000f906e","30361f84cc57ab40000f90a7","30361f84cc3f4100000faabb"};
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_pickup_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        selectedBarcodeList =  Constants.getArrayList(Constants.CATEGORY_ARRAY_JSON, SerialPickupSearchActivity.this);
        StoreIdShopTV =(TextView) findViewById(R.id.StoreIdShopTV);
        StoreIdShopTV.setText("Store ID: "+pref.getString(Constants.STORE_NUMBER,"0"));
        headerTable = (TableLayout) findViewById(R.id.headerTable);
        mainTable = (TableLayout) findViewById(R.id.mainTable);
        pDialog = new ProgressDialog(SerialPickupSearchActivity.this);

        if(Constants.isNetworkAvailable(SerialPickupSearchActivity.this)) {
            new SerialPickupSearchActivity.FetchBarcodeListAsyncTask(SerialPickupSearchActivity.this).execute(Constants.BASE_URL+"GetPickupByBarcode.php");
        }
        else
        {
            Constants.internetAlert(SerialPickupSearchActivity.this);
        }

        nextMoveBtn = (Button) findViewById(R.id.nextMoveBtn);

        nextMoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedEPCList.size() > 0)
                {
                    ArrayList<String> mArrayProducts = new ArrayList<String>();
                    mArrayProducts.addAll(selectedEPCList);
                    //saving to shared preferences
                    Constants.saveArrayList(mArrayProducts,Constants.CATEGORY_ARRAY_JSON, SerialPickupSearchActivity.this);

                    Intent intent = new Intent(SerialPickupSearchActivity.this , SerialPickupMoveActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(SerialPickupSearchActivity.this, "No EPC searched. Click on the row to search the barcode and asscoiated EPC's", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
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
            mSoundId = mSoundPool.load(SerialPickupSearchActivity.this, R.raw.beep, 1);
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

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        createSoundPool();
        boolean openResult = false;
        mReader = Reader.getReader(this, mBarcodeHandler);
        if (mReader != null){
            openResult = mReader.SD_Open();
        }
        if (openResult == SDConsts.SD_OPEN_SUCCESS)
        {
            if(mReader.SD_Wakeup()==SDConsts.SDResult.SUCCESS)
            {

                if (mReader.SD_GetConnectState() == SDConsts.SDConnectState.CONNECTED)
                {
                    mReader.SD_SetTriggerMode(SDConsts.SDTriggerMode.RFID);
                    Toast.makeText(getBaseContext(),"SD Already Connected",Toast.LENGTH_SHORT).show();
                }else{
                    if(mReader.SD_Connect()==SDConsts.SDResult.SUCCESS)
                    {
                        mReader.SD_SetTriggerMode(SDConsts.SDTriggerMode.RFID);
                        Toast.makeText(getBaseContext(),"SD Connected",Toast.LENGTH_SHORT).show();
                    }else{

                        showMessageDialog("SD Status", "SD Not Connected");
                    }
                }


            }
        }else if (openResult == SDConsts.RF_OPEN_FAIL){
            showMessageDialog("SD Status", "SD Open Fail");
        }


        super.onStart();
    }



    private void showMessageDialog(String title, String message)
    {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(SerialPickupSearchActivity.this);
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
                Intent intent2 = new Intent(SerialPickupSearchActivity.this, SoundManagerActivity.class);
                startActivity(intent2);
                return true;

            case R.id.bluetooth_reader: //Your task
                Intent intent3 = new Intent(SerialPickupSearchActivity.this, PickupSearchActivity.class);
                startActivity(intent3);
                finish();

                return true;

            default:return super.onOptionsItemSelected(item);

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.serial_inventory_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onStop() {
        mReader.RF_StopInventory();

        if (mSoundPool != null)
            mSoundPool.release();
        mSoundFileLoadState = false;
        super.onStop();
    }

    private static class BarcodeHandler extends Handler {
        private final WeakReference<SerialPickupSearchActivity> mExecutor;
        public BarcodeHandler(SerialPickupSearchActivity f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            SerialPickupSearchActivity executor = mExecutor.get();
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
                        Toast.makeText(SerialPickupSearchActivity.this, "Inventory Stopped reason : " + m.arg2, Toast.LENGTH_SHORT).show();
                        break;

                    case SDConsts.SDCmdMsg.TRIGGER_RELEASED:
                        break;
                    //You can receive this message every a minute. SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED
                    case SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED:
                        //Toast.makeText(SerialLocationScanActivity.this, "Battery state = " + m.arg2, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Battery state = " + m.arg2);

                        break;
                    case SDConsts.SDCmdMsg.SLED_HOTSWAP_STATE_CHANGED:
                        if (m.arg2 == SDConsts.SDHotswapState.HOTSWAP_STATE)
                            Toast.makeText(SerialPickupSearchActivity.this, "HOTSWAP STATE CHANGED = HOTSWAP_STATE", Toast.LENGTH_SHORT).show();
                        else if (m.arg2 == SDConsts.SDHotswapState.NORMAL_STATE)
                            Toast.makeText(SerialPickupSearchActivity.this, "HOTSWAP STATE CHANGED = NORMAL_STATE", Toast.LENGTH_SHORT).show();

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
                                if (data != null)
                                {
                                    String info = "";
                                    if (data.contains(";")) {
                                        //Log.d(TAG, "full tag = " + data);
                                        //full tag example(with optional value)
                                        //1) RF_PerformInventory => "3000123456783333444455556666;rssi:-54.8"
                                        //2) RF_PerformInventoryWithLocating => "3000123456783333444455556666;loc:64"
                                        int infoTagPoint = data.indexOf(';');
                                        info = data.substring(infoTagPoint, data.length());
                                        int infoPoint = info.indexOf(':') + 1;
                                        info = info.substring(infoPoint, info.length());
                                        // Log.d(TAG, "info tag = " + info);
                                        data = data.substring(0, infoTagPoint);
                                        // Log.d(TAG, "data tag = " + data);


                                         /*//test
                                        if(testDataCounter == (testEPC.length -1)) {
                                            info= String.valueOf(testDataCounter*3);
                                            testDataCounter = 0;
                                        }

                                        data = testEPC[ testDataCounter];
                                        testDataCounter++;
                                        ////// test over*/

                                        processLocateData((int) Integer.parseInt(info) , data);

                                        if (mSoundTask == null) {
                                            mSoundTask = new SerialPickupSearchActivity.SoundTask();
                                            mSoundTask.execute();
                                        }
                                        else {
                                            if (mSoundTask.getStatus() == AsyncTask.Status.FINISHED) {
                                                mSoundTask.cancel(true);
                                                mSoundTask = null;
                                                mSoundTask = new SerialPickupSearchActivity.SoundTask();
                                                mSoundTask.execute();
                                            }
                                        }
                                    }
                                }

                            }
                        }
                        break;
                    case SDConsts.RFCmdMsg.LOCATE:

                        break;
                }
                break;
            case SDConsts.Msg.BTMsg:
                if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_STATE_CHANGED) {
                    if (mReader.SD_GetConnectState() != SDConsts.BTConnectState.CONNECTED) {

                    }
                }
                else if  (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_DISCONNECTED || m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_LOST) {

                }
                break;
        }
    }


    private void processLocateData(int data, String epc) {
        //Log.d("LocationScan","processLocateData "+data);
        // Log.d("replen search","Location00  "+data+" EPC00 "+epc);
        startLocateTimer();

        for(int y = 0 ; y< tempepcList.size(); y++)
        {

            if(tempepcList.get(y).compareToIgnoreCase(epc.trim().toLowerCase()) == 0)
            {
                // Log.d("replen search","Location11  "+data+" EPC11 "+epc);
                epcList.get(y).setQuantity(data+" %");
                adapter.notifyDataSetChanged();
                vprogressbar.setProgress(data);

                return;
            }
        }

    }





    public static String toCamelCase(final String init) {
        if (init == null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(Character.toUpperCase(word.charAt(0)));
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length() == init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }

    public void showPopupWindowClick(View view, int index) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window_replen_search, null);

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


        epcList.clear();
        tempepcList.clear();
        try {
            JSONObject jobj = barcodeDetailsArray.getJSONObject(index);
            JSONArray lepcArray = jobj.getJSONArray("location_epc");

            for(int k=0; k <lepcArray.length(); k++)
            {
                JSONObject tempObj =  lepcArray.getJSONObject(k);

                JSONArray jArray = tempObj.getJSONArray("epc");
                for (int i=0;i<jArray.length();i++){
                    // Log.d(TAG, "epc " + jArray.getString(i));
                    Inventory invObj = new Inventory();
                    invObj.setCategory(jArray.getString(i));
                    invObj.setQuantity("0 %");
                    epcList.add(invObj);
                    tempepcList.add(jArray.getString(i));
                }

            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        listView1 = (ListView)popupView.findViewById(R.id.listView1);
        adapter = new ReplenishSearchAdapter(this, epcList);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener( new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

                CheckBox checkBox = (CheckBox) view.findViewById(R.id.chkEnable);
                checkBox.performClick();
            }
        });


        startScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTextView.setText("Searching.....");

                int ret;

                //setting max power
                mReader.RF_SetRadioPowerState(30);

                ret= mReader.RF_PerformInventoryWithLocating(true, false, true);


                if (ret == SDConsts.RFResult.SUCCESS) {

                    // Toast.makeText(SerialLocationScanActivity.this, "Scan Started", Toast.LENGTH_SHORT).show();
                } else if (ret == SDConsts.RFResult.MODE_ERROR)
                    Toast.makeText(SerialPickupSearchActivity.this, "Scan failed, Please check RFR900 MODE", Toast.LENGTH_SHORT).show();
                else if (ret == SDConsts.RFResult.LOW_BATTERY)
                    Toast.makeText(SerialPickupSearchActivity.this, "Scan   failed, LOW_BATTERY", Toast.LENGTH_SHORT).show();
                else
                    System.out.println("Scan Failed");
            }
        });
        stopScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ret;
                ret = mReader.RF_StopInventory();
                searchTextView.setText("Search Completed");
                //popupWindow.dismiss();

                if (ret == SDConsts.RFResult.SUCCESS || ret == SDConsts.RFResult.NOT_INVENTORY_STATE) {

                } else if (ret == SDConsts.RFResult.STOP_FAILED_TRY_AGAIN)
                    Toast.makeText(SerialPickupSearchActivity.this, "Stop Inventory failed", Toast.LENGTH_SHORT).show();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(adapter != null) {
                    ArrayList<Inventory> mArrayProducts = adapter.getCheckedItems();
                    if(mArrayProducts.size()> 0)
                    {
                        Log.d("showPopupWindowClick","mArrayProducts size  "+mArrayProducts.size());
                        //setting the value of textview in tablerow of table layout by id
                        TableRow row = (TableRow) mainTable.getChildAt(tableRowIndex+1);
                        TextView foundQtyTextView  = (TextView) row.getChildAt(NUMBER_OF_COLUMNS -1);

                        if (foundQtyTextView != null) {
                            foundQtyTextView.setText(mArrayProducts.size()+"");
                        }


                        for(int h=0; h<mArrayProducts.size(); h++)
                        {
                            selectedEPCList.addIfAbsent(mArrayProducts.get(h).getCategory());
                        }

                        if(selectedEPCList.size()>0)
                        {
                            try{
                                String ean = barcodeDetailsArray.getJSONObject(tableRowIndex).getString("barcode");
                                String qty = barcodeDetailsArray.getJSONObject(tableRowIndex).getString("picking_quantity");

                                new  UpdateStatusAndQuantityAsyncTask(SerialPickupSearchActivity.this,ean ,qty).execute(Constants.BASE_URL+"UpdatePickupStatusAndQuantity.php");


                            }catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(SerialPickupSearchActivity.this, "No EPC's are selected", Toast.LENGTH_SHORT).show();
                    }

                }
                popupWindow.dismiss();
                int ret;
                ret = mReader.RF_StopInventory();
            }
        });
    }


    class UpdateStatusAndQuantityAsyncTask extends AsyncTask<String, String, String> {



        private Context context;
        String barcode, quantity;

        public UpdateStatusAndQuantityAsyncTask(Context con, String ean, String qty) {

            context = con;
            barcode = ean;
            quantity = qty;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Updating status and quantity...");
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
                String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");
                String store_id =pref.getString(Constants.STORE_ID,"0");

                String urlParameters = "customer_id="+ customer_id+"&barcode="+barcode+"&quantity="+quantity+"&stock_table_name="+stock_table_name+"&store_id="+store_id ;
                System.out.println("urlParameters " + urlParameters);

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
                    //System.out.println("query "+treeJSON);
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
        }
    }



    class FetchBarcodeListAsyncTask extends AsyncTask<String, String, String> {



        private Context context;

        public FetchBarcodeListAsyncTask(Context con) {

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
                String store_id = pref.getString(Constants.STORE_ID,"0");

                String urlParameters = "customer_id="+ customer_id+"&json_barcode="+jsArray+"&store_id="+store_id ;
                System.out.println("urlParameters " + urlParameters);

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
                    //System.out.println("query "+treeJSON);
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

            System.out.println("Response "+result);
            ArrayList<String>keyList = new ArrayList<String>();
            if (result != null)
            {
                try {
                    JSONObject jObj = new JSONObject(result);
                    categoryHeaderArray = jObj.getJSONArray("category_details");
                    barcodeDetailsArray = jObj.getJSONArray("barcode_details");
                    LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.0f);
                    /* create a header table row */
                    TableRow tableRow = new TableRow(SerialPickupSearchActivity.this);
                    tableRow.setLayoutParams(tableRowParams);
                    int anchorIndex =0;

                    if(categoryHeaderArray.length() >=2)
                    {
                        anchorIndex =2;
                    }
                    else
                    {
                        anchorIndex =categoryHeaderArray.length();
                    }

                    for (int j = 0; j < anchorIndex; j++) {   /* create cell element - textview */
                        TextView tv = new TextView(SerialPickupSearchActivity.this);
                        tv.setBackgroundResource(R.drawable.table_header_bg);

                        keyList.add(categoryHeaderArray.getJSONObject(j).getString("category_column_name"));
                        tv.setText(toCamelCase(categoryHeaderArray.getJSONObject(j).getString("category_column_name").replaceAll("_", " ")));

                        /* set params for cell elements */
                        tv.setLayoutParams(new
                                TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT));
                        tv.setGravity(Gravity.CENTER);
                        tv.setPadding(4, 19, 4, 19);
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);
                        tv.setTextColor(Color.WHITE);

                        /* add views to the row */
                        tableRow.addView(tv);
                        NUMBER_OF_COLUMNS++;

                    }
                    keyList.add("barcode");
                    NUMBER_OF_COLUMNS++;
                    keyList.add("picking_quantity");
                    NUMBER_OF_COLUMNS++;

                    TextView tv = new TextView(SerialPickupSearchActivity.this);
                    tv.setBackgroundResource(R.drawable.table_header_bg);
                    tv.setText("Barcode");

                    /* set params for cell elements */
                    tv.setLayoutParams(new
                            TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    tv.setGravity(Gravity.CENTER);
                    tv.setPadding(4, 19, 4, 19);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);
                    tv.setTextColor(Color.WHITE);

                    /* add views to the row */
                    tableRow.addView(tv);
                    TextView tv2 = new TextView(SerialPickupSearchActivity.this);
                    tv2.setBackgroundResource(R.drawable.table_header_bg);
                    tv2.setText("Pickup Qty");

                    /* set params for cell elements */
                    tv2.setLayoutParams(new
                            TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    tv2.setGravity(Gravity.CENTER);
                    tv2.setPadding(4, 19, 4, 19);
                    tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);
                    tv2.setTextColor(Color.WHITE);

                    /* add views to the row */
                    tableRow.addView(tv2);
                    TextView tv3 = new TextView(SerialPickupSearchActivity.this);
                    tv3.setBackgroundResource(R.drawable.table_header_bg);
                    tv3.setText("Back Store Qty");
                    tv3.setTextColor(Color.WHITE);

                    /* set params for cell elements */
                    tv3.setLayoutParams(new
                            TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    tv3.setGravity(Gravity.CENTER);
                    tv3.setPadding(4, 19, 4, 19);
                    tv3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);
                    /* add views to the row */
                    tableRow.addView(tv3);
                    NUMBER_OF_COLUMNS++;

                    TextView tv4 = new TextView(SerialPickupSearchActivity.this);
                    tv4.setBackgroundResource(R.drawable.table_header_bg);
                    tv4.setText("Front Store Qty");
                    tv4.setTextColor(Color.WHITE);

                    /* set params for cell elements */
                    tv4.setLayoutParams(new
                            TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    tv4.setGravity(Gravity.CENTER);
                    tv4.setPadding(4, 19, 4, 19);
                    tv4.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);

                    /* add views to the row */
                    tableRow.addView(tv4);
                    NUMBER_OF_COLUMNS++;

                    TextView tv5 = new TextView(SerialPickupSearchActivity.this);
                    tv5.setBackgroundResource(R.drawable.table_header_bg);
                    tv5.setText("Found Qty");
                    tv5.setTextColor(Color.WHITE);

                    /* set params for cell elements */
                    tv5.setLayoutParams(new
                            TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    tv5.setGravity(Gravity.CENTER);
                    tv5.setPadding(4, 19, 4, 19);
                    tv5.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);

                    /* add views to the row */
                    tableRow.addView(tv5);
                    NUMBER_OF_COLUMNS++;

                    /* add the header row to the table */
                    mainTable.addView(tableRow);


                    for (int j = 0; j < barcodeDetailsArray.length(); j++) {
                        /* create a data table row */
                        TableRow tableRow2 = new TableRow(SerialPickupSearchActivity.this);
                        tableRow2.setLayoutParams(tableRowParams);

                        JSONObject jobj = barcodeDetailsArray.getJSONObject(j);
                        JSONArray lepcArray = jobj.getJSONArray("location_epc");
                        for(int i=0; i<keyList.size(); i++ )
                        {
                            TextView tvr = new TextView(SerialPickupSearchActivity.this);
                            tvr.setBackgroundResource(R.drawable.table_row_bg);
                            tvr.setText(jobj.getString(keyList.get(i)));

                            /* set params for cell elements */
                            tvr.setLayoutParams(new
                                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tvr.setGravity(Gravity.CENTER);
                            tvr.setPadding(4, 19, 4, 19);
                            tvr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);

                            /* add views to the row */
                            tableRow2.addView(tvr);


                        }
                        int isBackStoreObjectFound = 0;
                        int isFrontStoreObjectFound = 0;
                        // Finding back store qty and setting to textview
                        for(int k=0; k <lepcArray.length(); k++)
                        {
                            JSONObject tempObj =  lepcArray.getJSONObject(k);
                            if(tempObj.getString("sub_location_id").compareToIgnoreCase("3") == 0)
                            {
                                isBackStoreObjectFound = 1;
                                if(tempObj.has("epc")) {
                                    JSONArray ja = tempObj.getJSONArray("epc");
                                    TextView tvr = new TextView(SerialPickupSearchActivity.this);
                                    tvr.setBackgroundResource(R.drawable.table_row_bg);
                                    tvr.setText(ja.length() + "");

                                    /* set params for cell elements */
                                    tvr.setLayoutParams(new
                                            TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                            TableRow.LayoutParams.WRAP_CONTENT));
                                    tvr.setGravity(Gravity.CENTER);
                                    tvr.setPadding(4, 19, 4, 19);
                                    tvr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);

                                    /* add views to the row */
                                    tableRow2.addView(tvr);
                                }

                            }
                        }
                        // Added zero qty for back store
                        if(isBackStoreObjectFound == 0)
                        {
                            TextView tvr = new TextView(SerialPickupSearchActivity.this);
                            tvr.setBackgroundResource(R.drawable.table_row_bg);
                            tvr.setText( "0");

                            /* set params for cell elements */
                            tvr.setLayoutParams(new
                                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tvr.setGravity(Gravity.CENTER);
                            tvr.setPadding(4, 19, 4, 19);
                            tvr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);

                            /* add views to the row */
                            tableRow2.addView(tvr);
                        }

                        // Finding front store qty and setting to textview
                        for(int k=0; k <lepcArray.length(); k++)
                        {
                            JSONObject tempObj =  lepcArray.getJSONObject(k);
                            if(tempObj.getString("sub_location_id").compareToIgnoreCase("4") == 0)
                            {
                                isFrontStoreObjectFound=1;
                                if(tempObj.has("epc"))
                                {
                                    JSONArray ja = tempObj.getJSONArray("epc");
                                    TextView tvr = new TextView(SerialPickupSearchActivity.this);
                                    tvr.setBackgroundResource(R.drawable.table_row_bg);
                                    tvr.setText(ja.length()+"");

                                    /* set params for cell elements */
                                    tvr.setLayoutParams(new
                                            TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                            TableRow.LayoutParams.WRAP_CONTENT));
                                    tvr.setGravity(Gravity.CENTER);
                                    tvr.setPadding(4, 19, 4, 19);
                                    tvr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);

                                    /* add views to the row */
                                    tableRow2.addView(tvr);
                                }


                            }

                        }

                        // Added zero qty for back store
                        if(isFrontStoreObjectFound == 0)
                        {
                            TextView tvr = new TextView(SerialPickupSearchActivity.this);
                            tvr.setBackgroundResource(R.drawable.table_row_bg);
                            tvr.setText("0");

                            /* set params for cell elements */
                            tvr.setLayoutParams(new
                                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tvr.setGravity(Gravity.CENTER);
                            tvr.setPadding(4, 19, 4, 19);
                            tvr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);

                            /* add views to the row */
                            tableRow2.addView(tvr);
                        }

                        //Setting up the value of Found Qty; initially it is zero
                        TextView tvr = new TextView(SerialPickupSearchActivity.this);
                        tvr.setId(j);
                        tvr.setBackgroundResource(R.drawable.table_row_bg);
                        tvr.setText("0");

                        /* set params for cell elements */
                        tvr.setLayoutParams(new
                                TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT));
                        tvr.setGravity(Gravity.CENTER);
                        tvr.setPadding(4, 19, 4, 19);
                        tvr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);

                        /* add views to the row */
                        tableRow2.addView(tvr);



                        final int index = j;
                        tableRow2. setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                tableRowIndex = index;
                                showPopupWindowClick(v, index);
                            }
                        });
                        /* add the row to the table */
                        mainTable.addView(tableRow2);

                    }

                    Toast.makeText(SerialPickupSearchActivity.this, "Click on the row to search the barcode and asscoiated EPC's", Toast.LENGTH_SHORT).show();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(SerialPickupSearchActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                }
            }



        }
    }

}