package sysnotion.tagid.tagsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import co.kr.bluebird.sled.BTReader;
import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;
import co.kr.bluebird.sled.SelectionCriterias;
import sysnotion.tagid.tagsmart.db.DBManager;
import sysnotion.tagid.tagsmart.nLevel.SomeObject;
import sysnotion.tagid.tagsmart.utils.Constants;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SerialLocationScanActivity  extends AppCompatActivity {
    String TAG = "SerialLocationScanActivity";
    TextView divisionTV, qtyTV, closenessTV;
    Button doneBtn,startScanBtn, stopScanBtn;
    ProgressBar vprogressbar;
    private Reader reader;
    private final SerialLocationScanActivity.BarcodeHandler mBarcodeHandler = new SerialLocationScanActivity.BarcodeHandler(this);
    private SoundPool mSoundPool;
    private int mLocateValue;
    private int mSoundId;
    private TimerTask mLocateTimerTask;
    private Timer mClearLocateTimer;

    private boolean mSoundFileLoadState;

    private SerialLocationScanActivity.SoundTask mSoundTask;
    String mLocateEPC = new String();
    private ArrayList<String> epcs= new ArrayList<>();
    List<String> barcodeList= new ArrayList<>();
    private String nearestEPC="", barcodeString="", divison="", qty ="" ;
    Animation anim;
   boolean mLocate = false;
    ProgressDialog pDialog;
    SharedPreferences pref;
    ArrayList<String> dataBarcodeList = new ArrayList<String>();
    java.util.HashMap<String, Integer> hashMapEPCPosition = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_scan);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        pDialog = new ProgressDialog(SerialLocationScanActivity.this);

        divisionTV =(TextView) findViewById(R.id.divisionTV);
        divisionTV.setText(getIntent().getStringExtra("divison"));
        qtyTV =(TextView) findViewById(R.id.qtyTV);
        qtyTV.setText(getIntent().getStringExtra("qty"));
        closenessTV =(TextView) findViewById(R.id.closenessTV);
        doneBtn =(Button) findViewById(R.id.doneBtn);
        vprogressbar =(ProgressBar) findViewById(R.id.vprogressbar);
        vprogressbar.setProgress(0);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
        barcodeString = getIntent().getStringExtra("barcode");
        divison = getIntent().getStringExtra("divison");
        qty =  getIntent().getStringExtra("qty")   ;
        String str = getIntent().getStringExtra("barcode");
        barcodeList = Arrays.asList(str.split("\\s*,\\s*"));

        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000); //You can manage the time of the blink with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        startScanBtn=(Button) findViewById(R.id.startScanBtn);
        startScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLocate = false;
                int ret;
                //setting max power
                reader.RF_SetRadioPowerState(30);

                ret= reader.RF_PerformInventoryWithLocating(true, false, true);

                if (ret == SDConsts.RFResult.SUCCESS) {
                    // Toast.makeText(SerialLocationScanActivity.this, "Scan Started", Toast.LENGTH_SHORT).show();
                    closenessTV.setText("Searching....");
                    closenessTV.startAnimation(anim);
                } else if (ret == SDConsts.RFResult.MODE_ERROR)
                    Toast.makeText(SerialLocationScanActivity.this, "Scan failed, Please check RFR900 MODE", Toast.LENGTH_SHORT).show();
                else if (ret == SDConsts.RFResult.LOW_BATTERY)
                    Toast.makeText(SerialLocationScanActivity.this, "Scan   failed, LOW_BATTERY", Toast.LENGTH_SHORT).show();
                else
                    System.out.println("Scan Failed");

            }
        });

        stopScanBtn=(Button) findViewById(R.id.stopScanBtn);
        stopScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ret;
                ret = reader.RF_StopInventory();
                closenessTV.setText("");
                if (ret == SDConsts.RFResult.SUCCESS || ret == SDConsts.RFResult.NOT_INVENTORY_STATE) {

                } else if (ret == SDConsts.RFResult.STOP_FAILED_TRY_AGAIN)
                    Toast.makeText(SerialLocationScanActivity.this, "Stop Inventory failed", Toast.LENGTH_SHORT).show();
            }
        });

        if (Constants.isNetworkAvailable(SerialLocationScanActivity.this)) {
            new  FetchBarcodeAndEPCAsyncTask(SerialLocationScanActivity.this).execute(Constants.BASE_URL + "GetEPCBarcodeMasterData.php");

        } else {
            Constants.internetAlert(SerialLocationScanActivity.this);

        }



    }


    public static String leftPad(String str, int length, String padChar){
        String pad = "";
        for (int i = 0; i < length; i++) {
            pad += padChar;
        }
        return pad.trim()+ str.trim();
    }
/*
    private boolean verifyTag(String strEAN, String EPCNumber)
    {
        String header="00110000";
        String filter="001";
        String partition="101";

        String company_prefix=Long.toBinaryString(Long.parseLong(strEAN.substring(0,7).trim()));

        //  Log.d("EANActivity", "company_prefix "+company_prefix);
        if (company_prefix.length()<24) {
            company_prefix =leftPad(company_prefix, (24-company_prefix.length()), "0");
            //    Log.d("EANActivity", "24company_prefixless "+company_prefix);

        }

        String item_reference=Long.toBinaryString(Long.parseLong("0"+strEAN.substring(7,strEAN.length()-1).trim()));
        //Log.d("EANActivity", "item_reference "+item_reference);
        if(item_reference.length()<20){
            item_reference =leftPad(item_reference, (20-item_reference.length()), "0");
            //Log.d("EANActivity", "24 item_reference "+item_reference);

        }


        String strComBinary=header.trim()+filter.trim()+partition.trim()+company_prefix.trim()+item_reference.trim();

        //  System.out.println("EPCNumber   "+EPCNumber) ;
        String biString = new BigInteger(EPCNumber,16).toString(2);

        // System.out.println("Read EPC   "+biString) ;
        // System.out.println("strComBinary   "+strComBinary) ;
        biString = "00"+biString;
        // System.out.println("EPC   "+biString) ;
        if(biString.contains(strComBinary))
        {

            //Correct EPC Number
            return true;
        }
        else
        {
            //Wrong EPC Number
            return false;

        }

    }*/

    private void showMessageDialog(String title, String message)
    {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(SerialLocationScanActivity.this);
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
            mSoundId = mSoundPool.load(SerialLocationScanActivity.this, R.raw.beep, 1);
        }
    }

    private static class BarcodeHandler extends Handler {
        private final WeakReference<SerialLocationScanActivity> mExecutor;
        public BarcodeHandler(SerialLocationScanActivity f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            SerialLocationScanActivity executor = mExecutor.get();
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
                        Toast.makeText(SerialLocationScanActivity.this, "Inventory Stopped reason : " + m.arg2, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SerialLocationScanActivity.this, "HOTSWAP STATE CHANGED = HOTSWAP_STATE", Toast.LENGTH_SHORT).show();
                        else if (m.arg2 == SDConsts.SDHotswapState.NORMAL_STATE)
                            Toast.makeText(SerialLocationScanActivity.this, "HOTSWAP STATE CHANGED = NORMAL_STATE", Toast.LENGTH_SHORT).show();

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
                                if (data != null && !mLocate)
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
                                        //Log.d(TAG, "info tag = " + info);
                                        data = data.substring(0, infoTagPoint);
                                        //Log.d(TAG, "data tag = " + data);

                                        processLocateData((int) Integer.parseInt(info) , data);

                                    }
                                }

                            }
                        }
                        break;
                    case SDConsts.RFCmdMsg.LOCATE:
                        if (m.arg2 == SDConsts.RFResult.SUCCESS) {
                            if (m.obj != null  && m.obj instanceof Integer)
                                processExactLocationData((int) m.obj);
                        }
                        break;
                }
                break;
            case SDConsts.Msg.BTMsg:
                if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_STATE_CHANGED) {
                    if (reader.SD_GetConnectState() != SDConsts.BTConnectState.CONNECTED) {

                    }
                }
                else if  (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_DISCONNECTED || m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_LOST) {

                }
                break;
        }
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

    private void processLocateData(int data, String epc) {
        //Log.d("LocationScan","processLocateData "+data);
        startLocateTimer();
        mLocateValue = data;

        for(int y = 0 ; y< barcodeList.size(); y++)
        {
            if (hashMapEPCPosition.containsKey(epc)) {
                int position = hashMapEPCPosition.get(epc);
                String barcode =dataBarcodeList.get(position);

                if (barcodeList.get(y).trim().compareToIgnoreCase(barcode) == 0) {
                    closenessTV.clearAnimation(); // cancel blink animation
                    closenessTV.setAlpha(1.0f);
                    mLocate = true;

                    vprogressbar.setProgress(data);
                    closenessTV.setText(data + " %");
                    if (mSoundTask == null) {
                        mSoundTask = new SerialLocationScanActivity.SoundTask();
                        mSoundTask.execute();
                    } else {
                        if (mSoundTask.getStatus() == AsyncTask.Status.FINISHED) {
                            mSoundTask.cancel(true);
                            mSoundTask = null;
                            mSoundTask = new SerialLocationScanActivity.SoundTask();
                            mSoundTask.execute();
                        }
                    }

                    mLocateEPC = epc;
                    int ret = reader.RF_StopInventory();

                    if (ret == SDConsts.RFResult.SUCCESS || ret == SDConsts.RFResult.NOT_INVENTORY_STATE) {
                        SelectionCriterias s = new SelectionCriterias();
                        s.makeCriteria(SelectionCriterias.SCMemType.EPC, mLocateEPC,
                                0, mLocateEPC.length() * 4,
                                SelectionCriterias.SCActionType.ASLINVA_DSLINVB);
                        reader.RF_SetSelection(s);

                        ret = reader.RF_PerformInventoryForLocating(mLocateEPC);


                    }

                    return;
                }
            }
        }

    }

    private void processExactLocationData(int data) {
        //Log.d("LocationScan","processExactLocationData "+data);
        startLocateTimer();
        mLocateValue = data;
        vprogressbar.setProgress(data);
        closenessTV.setText(data+" %");
        if (mSoundTask == null) {
            mSoundTask = new SerialLocationScanActivity.SoundTask();
            mSoundTask.execute();
        }
        else {
            if (mSoundTask.getStatus() == AsyncTask.Status.FINISHED) {
                mSoundTask.cancel(true);
                mSoundTask = null;
                mSoundTask = new SerialLocationScanActivity.SoundTask();
                mSoundTask.execute();
            }
        }

    }

    @Override
    protected void onStart() {

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        createSoundPool();
        boolean openResult = false;
        reader = Reader.getReader(this, mBarcodeHandler);
        if (reader != null){
            openResult = reader.SD_Open();
        }
        if (openResult == SDConsts.SD_OPEN_SUCCESS)
        {
            if(reader.SD_Wakeup()==SDConsts.SDResult.SUCCESS)
            {

                if (reader.SD_GetConnectState() == SDConsts.SDConnectState.CONNECTED)
                {
                    reader.SD_SetTriggerMode(SDConsts.SDTriggerMode.RFID);
                    Toast.makeText(getBaseContext(),"SD Already Connected",Toast.LENGTH_SHORT).show();
                }else{
                    if(reader.SD_Connect()==SDConsts.SDResult.SUCCESS)
                    {
                        reader.SD_SetTriggerMode(SDConsts.SDTriggerMode.RFID);
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


    @Override
    public void onStop() {
        reader.RF_StopInventory();

        if (mSoundPool != null)
            mSoundPool.release();
        mSoundFileLoadState = false;
        super.onStop();
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
                Intent intent2 = new Intent(SerialLocationScanActivity.this, SoundManagerActivity.class);
                startActivity(intent2);
                return true;
            case R.id.bluetooth_reader:
                Intent intent3 = new Intent(SerialLocationScanActivity.this, LocationScanActivity.class);
                intent3.putExtra("barcode",barcodeString);
                intent3.putExtra("divison",divison);
                intent3.putExtra("qty",qty);
                startActivity(intent3);
                finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.serial_inventory_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void showMessageDialogWithBluetoothActivity(String title, String message)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(SerialLocationScanActivity.this);
        dialog.setTitle(title )
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(message)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        dialoginterface.cancel();
                    }})
                .setPositiveButton("Bluetooth Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {

                        Intent intent = new Intent(SerialLocationScanActivity.this, BTConnectivityActivity.class);
                        startActivity(intent);
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }


    class FetchBarcodeAndEPCAsyncTask extends AsyncTask<String, String, String> {
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

                String urlParameters = "customer_id=" + customer_id  + "&user_id=" + user_id + "&store_id=" + store_id ;
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
            System.out.println("FetchBarcodeAndEPCAsyncTask Resp " + result);
            dismissProgressDialog();

            /**** store the result in array for comparing ***********/
            dataBarcodeList.clear();
            hashMapEPCPosition.clear();
            if (result != null) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int c = 0; c < jsonArray.length(); c++)
                    {
                        JSONObject jObj = jsonArray.getJSONObject(c);
                        dataBarcodeList.add(jObj.getString("barcode"));
                        hashMapEPCPosition.put(jObj.getString("epc"), c);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }



}