package sysnotion.tagid.tagsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import co.kr.bluebird.sled.BTReader;
import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;
import okhttp3.OkHttpClient;
import sysnotion.tagid.tagsmart.db.DBManager;
import sysnotion.tagid.tagsmart.utils.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SerialCheckingEPCActivity extends AppCompatActivity {

    EditText barcodeScanTV,epcNumberTV,tidNumberTV;
    TextView sessionCountTV;
    ImageButton barcodeScanBtn,epcScanBtn,tidScanBtn;
    Button verifyEPCBtn, clearBtn;
    Reader reader=null;
    private final BarcodeHandler mBarcodeHandler = new BarcodeHandler(this);
    boolean isConnected;
    int readFlag = 0;
    private DBManager dbManager;
    String readTagValue= new String("");
    String currentDate="";
    String currentEPCNumber="";
    String memoryType="";
    String currentTID="", barcode="";
    SharedPreferences pref;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking_epc);
        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Date date = new Date();
        currentDate= new SimpleDateFormat("yyyy-MM-dd").format(date);

        dbManager = new DBManager(SerialCheckingEPCActivity.this);
        dbManager.open();

        sessionCountTV = (TextView) findViewById(R.id.sessionCountTV);

        JSONObject sessionObject = new JSONObject();
        try{

            sessionObject = dbManager.getSessionDetailsByDateWithInsert(currentDate);
            if(sessionObject.has("session_count"))
            {
                sessionCountTV.setText("Session Count: "+sessionObject.getString("session_count") );
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        //EditTextBox
        barcodeScanTV = (EditText) findViewById(R.id.barcodeScanTV);
        epcNumberTV = (EditText) findViewById(R.id.epcNumberTV);
        // epcNumberTV.setInputType(InputType.TYPE_NULL);
        tidNumberTV = (EditText) findViewById(R.id.tidNumberTV);
        //tidNumberTV.setInputType(InputType.TYPE_NULL);
        //EditTextBox

        //ImageButton
        barcodeScanBtn = (ImageButton) findViewById(R.id.barcodeScanBtn);
        epcScanBtn = (ImageButton) findViewById(R.id.epcScanBtn);
        tidScanBtn = (ImageButton) findViewById(R.id.tidScanBtn);
        //ImageButton
        verifyEPCBtn = (Button) findViewById(R.id.verifyEPCBtn);
        clearBtn = (Button) findViewById(R.id.clearBtn);
        barcodeScanTV.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                barcode  =s.toString();

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });


        barcodeScanBtn.setOnClickListener(new View.OnClickListener()
        {
            int ret;
            @Override
            public void onClick(View v) {
                barcodeScanTV.requestFocus();

                int  mode = reader.SD_GetTriggerMode();
                if (mode == SDConsts.SDTriggerMode.RFID)
                {
                    reader.SD_SetTriggerMode(SDConsts.SDTriggerMode.BARCODE);
                }

                if(barcodeScanTV.getText().toString().length()==0)
                {
                    ret = reader.BC_SetTriggerState(true);
                }else{
                    //Toast.makeText(getBaseContext(),"Please clear textbox.",Toast.LENGTH_SHORT).show();
                    barcodeScanTV.setText("");
                }


                if (ret == SDConsts.RFResult.SUCCESS)
                {
                    //Toast.makeText(getBaseContext(),"Ret Success",Toast.LENGTH_SHORT).show();
                } else if (ret == SDConsts.RFResult.MODE_ERROR){
                    Toast.makeText(SerialCheckingEPCActivity.this, "Start Inventory failed, Please check RFR900 MODE", Toast.LENGTH_SHORT).show();
                }else if (ret == SDConsts.RFResult.LOW_BATTERY){
                    Toast.makeText(SerialCheckingEPCActivity.this, "Start Inventory failed, LOW_BATTERY", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SerialCheckingEPCActivity.this, "Start Inventory failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        epcScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //epcNumberTV.requestFocus();
                readFlag = 0;
                readTag();
                // memoryType="TID";
                //readTID_FromTag();
            }
        });
        tidScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //epcNumberTV.requestFocus();
                readFlag = 0;
                memoryType="TID";
                readTID_FromTag();
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcodeScanTV.setText("");
                epcNumberTV.setText("");
                tidNumberTV.setText("");
                memoryType="";
            }
        });
        verifyEPCBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(barcodeScanTV.getText().toString().trim().length()> 0 && epcNumberTV.getText().toString().trim().length()> 0 && tidNumberTV.getText().toString().trim().length()>0)
                {

                    if(epcNumberTV.getText().toString().trim().equals(tidNumberTV.getText().toString().trim()))
                    {
                        //call API
                        if (Constants.isNetworkAvailable(SerialCheckingEPCActivity.this))
                        {
                            // new EncodingEPCActivity.EPCBarcodeDataAsyncTask(EncodingEPCActivity.this).execute(Constants.BASE_URL + "InsertEpcBarcodeValues.php");
                            new SerialCheckingEPCActivity.VerifyDataAsyncTask(SerialCheckingEPCActivity.this).execute(Constants.BASE_URL+"UpdateEPCBarcodeMaster.php");
                        } else
                        {
                            Constants.internetAlert(SerialCheckingEPCActivity.this);
                        }
                        //call API
                    }else{
                        showMessageDialog("Warning", "EPC and TID should be same for verification");
                    }
                }else{
                    showMessageDialog("Warning", "TextBox can not be blank");
                }



            }
        });
    }

    private void readTID_FromTag()
    {
        try{
            int result = -1000;
            int  mode = reader.SD_GetTriggerMode();
            if (mode == SDConsts.SDTriggerMode.BARCODE)
            {
                reader.SD_SetTriggerMode(SDConsts.SDTriggerMode.RFID);
            }

            if (reader.SD_GetConnectState() == SDConsts.SDConnectState.CONNECTED)
            {
                result=reader.RF_SetRadioPowerState(7);
                if(result==SDConsts.RFResult.SUCCESS)
                {
                    // result = reader.RF_READ(SDConsts.RFMemType.EPC, 2, 7, "00000000", false);
                    // result = reader.RF_READ(SDConsts.RFMemType.TID, 2, 7, "00000000", false);
                    result = reader.RF_READ(SDConsts.RFMemType.TID, 0, 6, "00000000", false);
                    if (result == SDConsts.RFResult.SUCCESS)
                    {
                        Toast.makeText(SerialCheckingEPCActivity.this, "Reading TID......", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        showMessageDialog("Status", "Read failed");
                    }

                }else{

                    showMessageDialog("Status", "Power not changed");
                }

            }else {
                showMessageDialogWithBluetoothActivity("Serial Connection Status", "Reader is not connected");
            }
        }catch (Exception btnreaderr){
            showMessageDialog("Error", btnreaderr.toString());
        }
        //Reading a particular RFID tag
    }


    private void readTag()
    {
        try{
            int result = -1000;
            int  mode = reader.SD_GetTriggerMode();
            if (mode == SDConsts.SDTriggerMode.BARCODE)
            {
                reader.SD_SetTriggerMode(SDConsts.SDTriggerMode.RFID);
            }

            if (reader.SD_GetConnectState() == SDConsts.SDConnectState.CONNECTED)
            {
                result=reader.RF_SetRadioPowerState(7);
                if(result==SDConsts.RFResult.SUCCESS)
                {
                    result = reader.RF_READ(SDConsts.RFMemType.EPC, 2, 7, "00000000", false);
                    // result = reader.RF_READ(SDConsts.RFMemType.TID, 2, 7, "00000000", false);
                    // result = reader.RF_READ(SDConsts.RFMemType.TID, 0, 3, "00000000", false);
                    if (result == SDConsts.RFResult.SUCCESS)
                    {
                        Toast.makeText(SerialCheckingEPCActivity.this, "Reading EPC......", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        showMessageDialog("Status", "Read failed");
                    }

                }else{

                    showMessageDialog("Status", "Power not changed");
                }

            }else {
                showMessageDialogWithBluetoothActivity("Serial Connection Status", "Reader Not Connected");
            }
        }catch (Exception btnreaderr){
            showMessageDialog("Error", btnreaderr.toString());
        }
        //Reading a particular RFID tag
    }

    @Override
    protected void onStart() {

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
                    Toast.makeText(getBaseContext(),"SD Already Connected",Toast.LENGTH_SHORT).show();
                }else{
                    if(reader.SD_Connect()==SDConsts.SDResult.SUCCESS)
                    {
                        isConnected = true;
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
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.sound:
                Intent intent2 = new Intent(SerialCheckingEPCActivity.this, SoundManagerActivity.class);
                startActivity(intent2);
                return true;

            case R.id.bluetooth_reader:
                Intent intent3 = new Intent(SerialCheckingEPCActivity.this, CheckingEPCActivity.class);
                startActivity(intent3);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private static class BarcodeHandler extends Handler {
        private final WeakReference<SerialCheckingEPCActivity> mExecutor;
        public BarcodeHandler(SerialCheckingEPCActivity f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            SerialCheckingEPCActivity executor = mExecutor.get();
            if (executor != null) {
                executor.handleMessage(msg);
            }
        }
    }

    public void handleMessage(Message m) {
        String data = "";
        String messageStr = null;

        switch (m.what) {

            case SDConsts.Msg.SDMsg:

                if (m.arg1 == SDConsts.SDCmdMsg.TRIGGER_PRESSED)
                {
                    int  mode = reader.SD_GetTriggerMode();
                    if (mode == SDConsts.SDTriggerMode.RFID)
                    {
                        reader.SD_SetTriggerMode(SDConsts.SDTriggerMode.BARCODE);
                    }
                    reader.BC_SetTriggerState(true);

                }else if (m.arg1 == SDConsts.SDCmdMsg.TRIGGER_RELEASED)
                {
                    //mMessageTextView.setText(" " + "TRIGGER_RELEASED");
                    // Toast.makeText(getBaseContext(),"TRIGGER_RELEASED:"+etbar.getText().toString(),Toast.LENGTH_SHORT).show();

                }else if (m.arg1 == SDConsts.SDCmdMsg.SLED_MODE_CHANGED)
                {

                }else if (m.arg1 == SDConsts.SDCmdMsg.SLED_UNKNOWN_DISCONNECTED) {

                }
                break;
            case SDConsts.Msg.RFMsg:
                switch (m.arg1) {
                    //RF_Read callback message
                    case SDConsts.RFCmdMsg.INVENTORY:
                    case SDConsts.RFCmdMsg.READ:
                        if (m.arg2 == SDConsts.RFResult.SUCCESS){
                            if (m.obj != null  && m.obj instanceof String){
                                data = (String)m.obj;
                                Log.d("EANActivity", "EPC111 "+data);
                                //if(data.length()>=24)
                                if(data.length()>0 )
                                {
                                    readTagValue = (data.length()>24)?data.substring(0,24):data;
                                    // readTagValue = data;

                                    if(readFlag == 0)
                                    {

                                        if(memoryType=="TID")
                                        {
                                            tidNumberTV.setText(readTagValue.trim());
                                            showMessageDialog("TID Status", "Tag TID: "+readTagValue);
                                        }else{

                                            epcNumberTV.setText(readTagValue.trim());
                                            showMessageDialog("EPC Status", "Tag EPC: "+readTagValue);

                                        }
                                    }
                                    else
                                    {
                                        //  verifyTag(barcodeScanTV.getText().toString(),readTagValue);
                                        readFlag = 0;
                                    }
                                }
                                else
                                {

                                    if(memoryType=="TID")
                                    {
                                        showMessageDialog("TID Reading Status", "Invalid TID");
                                    }else{
                                        showMessageDialog("EPC Reading Status", "Invalid EPC");
                                    }
                                }



                            }
                        }else{
                            showMessageDialog("RFID Reading Status", "RFID not found for reading.");
                        }
                        break;
                    case SDConsts.RFCmdMsg.WRITE:
                        if (m.arg2 == SDConsts.RFResult.SUCCESS)
                        {
                            //list.add(barcodeScanTV.getText().toString());
                            showMessageDialog("Status", "Written Succesfully.");
                        }else{

                            showMessageDialog("Writing Status", "RFID not found for writing.");
                        }
                        break;
                    case SDConsts.RFCmdMsg.WRITE_TAG_ID:
                        //messageStr = "WRITE_TAG_ID result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.WRITE_ACCESS_PASSWORD:
                        //messageStr = "WRITE_ACCESS_PASSWORD result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.WRITE_KILL_PASSWORD:
                        //messageStr = "WRITE_KILL_PASSWORD result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.LOCK:
                        //messageStr = "LOCK result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.KILL:
                        //messageStr = "KILL result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.BLOCK_WRITE:
                        //messageStr = "BLOCK_WRITE result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.BLOCK_PERMALOCK:
                        //messageStr = "BLOCK_PERMALOCK result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.BLOCK_ERASE:
                        //messageStr = "BLOCK_ERASE result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.UPDATE_RF_FW_START:
                       /* messageStr = "UPDATE_RF_FW_START " + result + " " + data;
                        if (result == SDConsts.RFResult.SUCCESS) {
                            Activity activity = getActivity();
                            if (activity != null)
                                createDialog(PROGRESS_DIALOG, activity.getString(R.string.update_rf_firm_str));
                        }
                        */
                        break;
                    case SDConsts.RFCmdMsg.UPDATE_RF_FW:
                        //setProgressState(result);
                        break;
                    case SDConsts.RFCmdMsg.UPDATE_RF_FW_END:
                        //closeDialog();
                        //messageStr = "UPDATE_RF_FW_END " + result + " " + data;
                        break;
                }
                break;
            case SDConsts.Msg.BCMsg:
                //StringBuilder readData = new StringBuilder();
                if(m.arg1 == SDConsts.BCCmdMsg.BARCODE_TRIGGER_PRESSED){
                    // Toast.makeText(getBaseContext(),"bar trigger pressed",Toast.LENGTH_SHORT).show();
                }else if (m.arg1 == SDConsts.BCCmdMsg.BARCODE_TRIGGER_RELEASED){

                    if(barcodeScanTV.getText().length()!=0)
                    {
                        if(barcodeScanTV.getText().length()>0)
                        {
                           /*
                            if(rfidScanTV.getText().length()==0)
                            {
                                rfidScanTV.requestFocus();
                            }
                        */
                            if(epcNumberTV.getText().length()==0)
                            {
                                epcNumberTV.requestFocus();
                            }

                        }else
                        {
                            barcodeScanTV.setText("");
                        }
                    } else if(epcNumberTV.getText().length()!=0)
                    {
                        if(barcodeScanTV.getText().length()==0)
                        {
                            barcodeScanTV.requestFocus();
                        }
                    }
                   /* else if(rfidScanTV.getText().length()!=0)
                    {
                        if(barcodeScanTV.getText().length()==0)
                        {
                            barcodeScanTV.requestFocus();
                        }
                    }*/

                }else if (m.arg1 == SDConsts.BCCmdMsg.BARCODE_READ)
                {
                    if (m.arg2 == SDConsts.BCResult.SUCCESS)
                    {
                        data = (String) m.obj;
                        if (data.length()!=0)
                        {

                        }
                    }else if (m.arg2 == SDConsts.BCResult.ACCESS_TIMEOUT){
                        showMessageDialog("Error", "Access Timeout");
                    }

                }else if (m.arg1 == SDConsts.BCCmdMsg.BARCODE_ERROR) {

                    if (m.arg2 == SDConsts.BCResult.LOW_BATTERY)
                    {
                        showMessageDialog("Battery Status", "BARCODE_ERROR Low battery");
                    }
                    else
                    {
                        showMessageDialog("Error", "BARCODE ERROR");
                    }
                }
                break;
        }
    }

    private void showMessageDialog(String title, String message)
    {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(SerialCheckingEPCActivity.this);
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

    private void showMessageDialogEPCUpdate(String title, String message, final String wrongEPCNumber ) {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(SerialCheckingEPCActivity.this);
            dialog.setTitle(title)
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage(message)
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            dialoginterface.cancel();
                        }
                    })
                    .setPositiveButton("Update EPC", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                    /*
                            if (barcodeScanTV.getText().toString().trim().length() > 0 && rfidScanTV.getText().toString().trim().length() > 0) {
                                if (barcodeScanTV.getText().toString().trim().length() == 13) {
                                    String strencodedEpc = eanEncoding(barcodeScanTV.getText().toString(), rfidScanTV.getText().toString());
                                    epcNumberTV.setText(strencodedEpc);
                                    currentEPCNumber = strencodedEpc;
                                    //update EPC on sqlite
                                    dbManager.updateTagsmartStockEPC(wrongEPCNumber, currentEPCNumber);
                                    //update EPC on server
                                    new SerialCheckingEPCActivity.UpdateEPCAsyncTask(SerialCheckingEPCActivity.this, wrongEPCNumber, currentEPCNumber).execute(Constants.BASE_URL + "UpdateEPCTagSmartStockValues.php");

                                } else {
                                    showMessageDialog("Warning", "13 Digit barcode not allowed");
                                }
                            }
                    */

                        }
                    }).show();
        }catch (WindowManager.BadTokenException e) {
            //use a log message
        }
    }

    private void showMessageDialogWithBluetoothActivity(String title, String message)
    {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(SerialCheckingEPCActivity.this);
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

                            Intent intent = new Intent(SerialCheckingEPCActivity.this, BTConnectivityActivity.class);
                            startActivity(intent);
                        }
                    }).show();
        }catch (WindowManager.BadTokenException e) {
            //use a log message
        }
    }
    class VerifyDataAsyncTask extends AsyncTask<String, String, String> {

        OkHttpClient client;
        private ProgressDialog dialog;

        public VerifyDataAsyncTask(Activity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog.setMessage("Associating tag in the cloud...");

            dialog.show();
        }

        @Override
        protected String doInBackground(String... string) {
            String response = null;
            try {
                String store_id = pref.getString(Constants.STORE_ID, "0");

                URL url = new URL(string[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(7000);
                conn.setConnectTimeout(7000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("X-Environment", "android");

                //  String customer_id = pref.getString(Constants.CUSTOMER_ID,"0");
                String customer_id = pref.getString(Constants.CUSTOMER_ID, "0");
                String user_id = pref.getString(Constants.USER_ID, "0");
                //  String store_id = pref.getString(Constants.STORE_ID, "0");

                String urlParameters = "customer_id=" + customer_id +"&user_id=" + user_id + "&store_id=" + store_id +"&barcode=" + barcodeScanTV.getText().toString().trim()+"&epc="+epcNumberTV.getText().toString().trim();
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

                    response = buffer.toString();
                    return response;
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
            //System.out.println("Response TABLE_DATA_JSON "+result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (result != null) {
                try
                {
                    JSONObject jobj = new JSONObject(result);
                    Toast.makeText(SerialCheckingEPCActivity.this, jobj.getString("msg"), Toast.LENGTH_LONG).show();

                }
                catch (Exception e) {
                    e.printStackTrace();
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(SerialCheckingEPCActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                    finish();
                }


            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(SerialCheckingEPCActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}