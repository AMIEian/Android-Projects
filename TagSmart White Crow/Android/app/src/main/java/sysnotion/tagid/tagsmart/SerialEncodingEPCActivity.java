package sysnotion.tagid.tagsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;
import okhttp3.OkHttpClient;
import sysnotion.tagid.tagsmart.db.DBManager;
import sysnotion.tagid.tagsmart.utils.Constants;
import sysnotion.tagid.tagsmart.utils.TCPClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SerialEncodingEPCActivity extends AppCompatActivity {
    EditText barcodeScanTV, rfidScanTV, epcNumberTV;
    TextView sessionCountTV;
    ImageButton barcodeScanBtn, rfidScanBtn,epcScanBtn;
    Button encodeEPCBtn, ReadEPCBtn;
    Reader reader=null;
    private final BarcodeHandler mBarcodeHandler = new BarcodeHandler(this);
    boolean isConnected;
    String readTagValue= new String("");
    int readFlag = 0;
    private DBManager dbManager;
    String currentDate="";
    String currentEPCNumber="";
    String memoryType="";
    SharedPreferences pref;
    private TCPClient mTcpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_encoding_epc);
        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Date date = new Date();
        currentDate= new SimpleDateFormat("yyyy-MM-dd").format(date);

        dbManager = new DBManager(SerialEncodingEPCActivity.this);
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

        barcodeScanTV = (EditText) findViewById(R.id.barcodeScanTV);
       // rfidScanTV = (EditText) findViewById(R.id.rfidScanTV);
        epcNumberTV = (EditText) findViewById(R.id.epcNumberTV);
       // barcodeScanTV.setInputType(InputType.TYPE_NULL);
       // rfidScanTV.setInputType(InputType.TYPE_NULL);
        epcNumberTV.setInputType(InputType.TYPE_NULL);

        barcodeScanBtn = (ImageButton) findViewById(R.id.barcodeScanBtn);
        epcScanBtn = (ImageButton) findViewById(R.id.epcScanBtn);
      //  rfidScanBtn = (ImageButton) findViewById(R.id.rfidScanBtn);

        encodeEPCBtn = (Button) findViewById(R.id.encodeEPCBtn);
        ReadEPCBtn = (Button) findViewById(R.id.ReadEPCBtn);

/*
        barcodeScanTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(barcodeScanTV.getText().toString().trim().length()> 0 && rfidScanTV.getText().toString().trim().length()>0)
                {
                    if(barcodeScanTV.getText().toString().trim().length()==13)
                    {
                        String strencodedEpc=  eanEncoding(barcodeScanTV.getText().toString(),rfidScanTV.getText().toString());
                        epcNumberTV.setText(strencodedEpc);
                        currentEPCNumber = strencodedEpc;
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

        });

        rfidScanTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(barcodeScanTV.getText().toString().trim().length()> 0 && rfidScanTV.getText().toString().trim().length()> 0)
                {
                    if(barcodeScanTV.getText().toString().trim().length()==13)
                    {
                        String strencodedEpc=  eanEncoding(barcodeScanTV.getText().toString(),rfidScanTV.getText().toString());
                        epcNumberTV.setText(strencodedEpc);
                        currentEPCNumber = strencodedEpc;
                        Log.d("barcodeScan", "barcodeScan "+barcodeScanTV.getText().toString());
                        Log.d("currentEPCNumber", "currentEPCNumber "+currentEPCNumber);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

        });

        ReadEPCBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(SerialEncodingEPCActivity.this);
                dialog.setTitle("Read Or Verify")
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("Do you want to read the tag only, or do you want to verify? Please select the option you wish to do.")
                        .setNegativeButton("Read", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                dialoginterface.cancel();

                                readFlag = 0;
                                readTag();
                            }})
                        .setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                if(barcodeScanTV.getText().toString().trim().length() ==13)
                                {
                                    readFlag = 1;
                                   // readTag();
                                    //call API
                                    if (Constants.isNetworkAvailable(SerialEncodingEPCActivity.this))
                                    {
                                        // new EncodingEPCActivity.EPCBarcodeDataAsyncTask(EncodingEPCActivity.this).execute(Constants.BASE_URL + "InsertEpcBarcodeValues.php");
                                        new SerialEncodingEPCActivity.GetEPCBarcodeDataAsyncTask(SerialEncodingEPCActivity.this).execute(Constants.BASE_URL+"GetBarcode.php");
                                    } else
                                    {
                                        Constants.internetAlert(SerialEncodingEPCActivity.this);
                                    }
                                    //call API

                                }
                                else
                                {
                                    showMessageDialog("Warning", "13 Digit barcode not allowed");
                                }

                            }
                        }).show();


            }
        });
*/
        ReadEPCBtn.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              //new SerialEncodingEPCActivity.GetMethodDemo(SerialEncodingEPCActivity.this).execute("https://tagidapi.smart-iam.com/tag/tag_info?epc=f6431cf8-8b9c-4fb4-b666-6d20e0be62e2");
                                              //new SerialEncodingEPCActivity.GetEPCBarcodeDataAsyncTask_2(SerialEncodingEPCActivity.this).execute("https://tagidapi.smart-iam.com/tag/tag_info?");
                                              new SerialEncodingEPCActivity.WebService(SerialEncodingEPCActivity.this).execute("https://tagidapi.smart-iam.com/tag/tag_info?epc=f6431cf8-8b9c-4fb4-b666-6d20e0be62e2");
                                              new SerialEncodingEPCActivity.WebService(SerialEncodingEPCActivity.this).execute("https://tagidapi.smart-iam.com/tag/verify_tag?epc=f6431cf8-8b9c-4fb4-b666-6d20e0be62e2&barcode=205000003693501\n");
                                          }
                                      });

        encodeEPCBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    int result = -1000;
                    //Dummy Data
                    //barcodeScanTV.setText("8907733726455");
                    //rfidScanTV.setText("10008");
                    //Dummy Data
                    //epcNumberTV
                    //  if(barcodeScanTV.getText().toString().trim().length()> 0 && rfidScanTV.getText().toString().trim().length()> 0)
                    if(barcodeScanTV.getText().toString().trim().length()> 0 && epcNumberTV.getText().toString().trim().length()> 0)
                    {
                        /*if(barcodeScanTV.getText().toString().trim().length()==13)
                        {*/
                            /* old code for epc convertion
                            String strencodedEpc=  eanEncoding(barcodeScanTV.getText().toString(),rfidScanTV.getText().toString());
                            epcNumberTV.setText(strencodedEpc);
                            currentEPCNumber = strencodedEpc;
                            */
                        if(epcNumberTV.getText().toString().trim().length()==24)
                        {
                            int  mode = reader.SD_GetTriggerMode();
                            if (mode == SDConsts.SDTriggerMode.BARCODE)
                            {
                                reader.SD_SetTriggerMode(SDConsts.SDTriggerMode.RFID);
                            }

                            if (reader.SD_GetConnectState() == SDConsts.SDConnectState.CONNECTED)
                            {
                                result=reader.RF_SetRadioPowerState(6);
                                if(result==SDConsts.RFResult.SUCCESS)
                                {
                                    result = reader.RF_WRITE(SDConsts.RFMemType.EPC, 2, epcNumberTV.getText().toString().trim(), "00000000",  false);
                                    //result = reader.RF_WRITE(SDConsts.RFMemType.EPC, 2, etbar.getText().toString().trim(), "00000000",  false);
                                    //result = reader.RF_WRITE(SDConsts.RFMemType.EPC, 2, "111122223333444455556666", "00000000",  false);
                                    if (result == SDConsts.RFResult.SUCCESS)
                                    {
                                        // Toast.makeText(EncodingEPCActivity.this, "Writting....", Toast.LENGTH_SHORT).show();

                                        //call API
                                        if (Constants.isNetworkAvailable(SerialEncodingEPCActivity.this))
                                        {
                                            // new EncodingEPCActivity.EPCBarcodeDataAsyncTask(EncodingEPCActivity.this).execute(Constants.BASE_URL + "InsertEpcBarcodeValues.php");
                                            new SerialEncodingEPCActivity.EPCBarcodeDataAsyncTask(SerialEncodingEPCActivity.this).execute(Constants.BASE_URL+"InsertEpcBarcodeValues.php");
                                        } else
                                        {
                                            Constants.internetAlert(SerialEncodingEPCActivity.this);
                                        }
                                        //call API

                                    }else{
                                        Toast.makeText(SerialEncodingEPCActivity.this, "Write failed ", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }else{
                                Toast.makeText(SerialEncodingEPCActivity.this, "SD not connected. ", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            showMessageDialog("Warning", "EPC Should be the 24 character in length.");
                        }




                       /* }else{

                         //   showMessageDialog("Warning", "13 Digit barcode not allowed");
                        }*/
                    }else{
                        showMessageDialog("Warning", "Textbox can not left blank.");
                    }

                }catch(Exception enderr){
                    showMessageDialog("Error", enderr.toString());

                }
            }
        });
  /*      rfidScanBtn.setOnClickListener(new View.OnClickListener()
        {
            int ret;
            @Override
            public void onClick(View v) {
                rfidScanTV.requestFocus();

                int  mode = reader.SD_GetTriggerMode();
                if (mode == SDConsts.SDTriggerMode.RFID)
                {
                    reader.SD_SetTriggerMode(SDConsts.SDTriggerMode.BARCODE);
                }

                if(rfidScanTV.getText().toString().length()==0)
                {
                    ret = reader.BC_SetTriggerState(true);
                }else{
                    //Toast.makeText(getBaseContext(),"Please clear textbox.",Toast.LENGTH_SHORT).show();
                    rfidScanTV.setText("");
                }

                if (ret == SDConsts.RFResult.SUCCESS)
                {
                    //Toast.makeText(getBaseContext(),"Ret Success",Toast.LENGTH_SHORT).show();
                } else if (ret == SDConsts.RFResult.MODE_ERROR){
                    Toast.makeText(SerialEncodingEPCActivity.this, "Start Inventory failed, Please check RFR900 MODE", Toast.LENGTH_SHORT).show();
                }else if (ret == SDConsts.RFResult.LOW_BATTERY){
                    Toast.makeText(SerialEncodingEPCActivity.this, "Start Inventory failed, LOW_BATTERY", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SerialEncodingEPCActivity.this, "Start Inventory failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
*/
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
                    Toast.makeText(SerialEncodingEPCActivity.this, "Start Inventory failed, Please check RFR900 MODE", Toast.LENGTH_SHORT).show();
                }else if (ret == SDConsts.RFResult.LOW_BATTERY){
                    Toast.makeText(SerialEncodingEPCActivity.this, "Start Inventory failed, LOW_BATTERY", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SerialEncodingEPCActivity.this, "Start Inventory failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        epcScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //epcNumberTV.requestFocus();
                readFlag = 0;
                //readTag();
                memoryType="TID";
                readTID_FromTag();
            }
        });
        // connect to the TCP server
        new connectTask().execute("");

    }

    private void updateDatabaseValues(String EPCNumber, String barcode)
    {
        JSONObject sessionObject = new JSONObject();
        try{

            sessionObject = dbManager.getSessionDetailsByDateWithInsert(currentDate);
            String customer_id = pref.getString(Constants.CUSTOMER_ID,"0");
            if(sessionObject.has("session_count"))
            {
                int count = Integer.parseInt(sessionObject.getString("session_count"));
                int newCount = (count+1);
                sessionCountTV.setText("Session Count: "+newCount );

                //update local sqlite DB Session Details tables
                dbManager.updateSessionDetails(currentDate,String.valueOf(newCount),sessionObject.getString("session_id"));

                //insert data in local sqlite DB, Tagsmart Stock Details
                String tagsmart_id = dbManager.insertTagsmartStockDetails("0",customer_id,EPCNumber,barcode);//insert data in local sqlite DB, Tagsmart Stock Details

                //insert data in local sqlite DB, EPC Location Date time table
                dbManager.insertEPCLocationDetails(tagsmart_id,"0","0","0");

                //insert data in local sqlite DB, Stock Details Checksum table
                dbManager.insertStockDetailsChecksum(EPCNumber);

                //Updating values on server and delete epc entery in local stock checksum table
                if(Constants.isNetworkAvailable(SerialEncodingEPCActivity.this)) {
                    new UpdateTagSmartDetailsAsyncTask(SerialEncodingEPCActivity.this,barcode,EPCNumber).execute(Constants.BASE_URL + "InsertTagSmartStockValues.php");
                }

            }

            //sends the message to the server
         /*   JSONObject jobj = new JSONObject();
            jobj.put("EPC", currentEPCNumber);
            jobj.put("barcode",barcodeScanTV.getText().toString());
            Log.d("TCp MSG", "JSON "+jobj.toString());
            if (mTcpClient != null) {
                mTcpClient.sendMessage(jobj.toString());
            }*/

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void verifyTag(String strEAN, String EPCNumber, String rfidSerialNumber)
    {
        String header="00110000";
        String filter="001";
        String partition="101";

        String company_prefix=Long.toBinaryString(Long.parseLong(strEAN.substring(0,7).trim()));

        Log.d("EANActivity", "company_prefix "+company_prefix);
        if (company_prefix.length()<24) {
            company_prefix =leftPad(company_prefix, (24-company_prefix.length()), "0");
            Log.d("EANActivity", "24company_prefixless "+company_prefix);

        }

        String item_reference=Long.toBinaryString(Long.parseLong("0"+strEAN.substring(7,strEAN.length()-1).trim()));
        Log.d("EANActivity", "item_reference "+item_reference);
        if(item_reference.length()<20){
            item_reference =leftPad(item_reference, (20-item_reference.length()), "0");
            Log.d("EANActivity", "24 item_reference "+item_reference);

        }


        String strComBinary=header.trim()+filter.trim()+partition.trim()+company_prefix.trim()+item_reference.trim();

        System.out.println("EPCNumber   "+EPCNumber) ;
        String biString = new BigInteger(EPCNumber,16).toString(2);

        System.out.println("Read EPC   "+biString) ;
        System.out.println("strComBinary   "+strComBinary) ;
        biString = "00"+biString;
        System.out.println("EPC   "+biString) ;
        if(biString.contains(strComBinary))
        {
            showMessageDialog("EPC Number Verification Status", "Correct EPC Number");
        }
        else
        {
            showMessageDialogEPCUpdate("EPC Number Verification Status", "Wrong EPC Number",EPCNumber );
        }

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
                        Toast.makeText(SerialEncodingEPCActivity.this, "Reading......", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        showMessageDialog("Status", "Read failed");
                    }

                }else{

                    showMessageDialog("Status", "Power not changed");
                }

            }else {
                showMessageDialog("SD Status", "SD Not Connected");
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

                    if (result == SDConsts.RFResult.SUCCESS)
                    {
                        Toast.makeText(SerialEncodingEPCActivity.this, "Reading......", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        showMessageDialog("Status", "Read failed");
                    }

                }else{

                    showMessageDialog("Status", "Power not changed");
                }

            }else {
                showMessageDialog("SD Status", "SD Not Connected");
            }
        }catch (Exception btnreaderr){
            showMessageDialog("Error", btnreaderr.toString());
        }
        //Reading a particular RFID tag
    }

    @Override
    protected void onStart() {

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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



    public String hexToBin(String hex){
        hex=hex.toUpperCase();
        hex = hex.replaceAll("0", "0000");
        hex = hex.replaceAll("1", "0001");
        hex = hex.replaceAll("2", "0010");
        hex = hex.replaceAll("3", "0011");
        hex = hex.replaceAll("4", "0100");
        hex = hex.replaceAll("5", "0101");
        hex = hex.replaceAll("6", "0110");
        hex = hex.replaceAll("7", "0111");
        hex = hex.replaceAll("8", "1000");
        hex = hex.replaceAll("9", "1001");
        hex = hex.replaceAll("A", "1010");
        hex = hex.replaceAll("B", "1011");
        hex = hex.replaceAll("C", "1100");
        hex = hex.replaceAll("D", "1101");
        hex = hex.replaceAll("E", "1110");
        hex = hex.replaceAll("F", "1111");
        return hex;
    }
    public Number binToDec(String binaryString)
    {
        return (binaryString!="")? new BigInteger(binaryString, 2):0;
    }

    public static String leftPad(String str, int length, String padChar){
        String pad = "";
        for (int i = 0; i < length; i++) {
            pad += padChar;
        }
        return pad.trim()+ str.trim();
    }

    public String eanEncoding(String strEAN,String strSRNO)
    {

        String header="00110000";
        String filter="001";
        String partition="101";

        String company_prefix=Long.toBinaryString(Long.parseLong(strEAN.substring(0,7).trim()));

        Log.d("EANActivity", "company_prefix "+company_prefix);
        if (company_prefix.length()<24) {
            company_prefix =leftPad(company_prefix, (24-company_prefix.length()), "0");
            Log.d("EANActivity", "24company_prefixless "+company_prefix);

        }

        String item_reference=Long.toBinaryString(Long.parseLong("0"+strEAN.substring(7,strEAN.length()-1).trim()));
        Log.d("EANActivity", "item_reference "+item_reference);
        if(item_reference.length()<20){
            item_reference =leftPad(item_reference, (20-item_reference.length()), "0");
            Log.d("EANActivity", "24 item_reference "+item_reference);

        }
        String srno="";
        try{
            srno=Long.toBinaryString(Long.parseLong(strSRNO.trim()));
        }
        catch(Exception e)
        {
            showMessageDialog("Error", e.toString());
        }


        Log.d("EANActivity", "srno "+srno);
        //  String srno=hexToBin(strSRNO);
        if(srno.length()<38){
            srno =leftPad(srno, (38-srno.length()), "0");

            Log.d("EANActivity", "38srno "+srno);
        }

        String strComBinary=header.trim()+filter.trim()+partition.trim()+company_prefix.trim()+item_reference.trim()+srno.trim();
        Log.d("EANActivity", "strComBinary "+strComBinary);

        String strHexa=(strComBinary.length()==96)? new BigInteger(strComBinary, 2).toString(16):"More than 96 Bits.";
        Log.d("EANActivity", "strHexa "+strHexa);
        return strHexa;
    }

    private static class BarcodeHandler extends Handler {
        private final WeakReference<SerialEncodingEPCActivity> mExecutor;
        public BarcodeHandler(SerialEncodingEPCActivity f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            SerialEncodingEPCActivity executor = mExecutor.get();
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
                                Log.d("EANActivity", "EPC1111 "+data);

                                //if(data.length()>=24)
                                if(data.length()>0 )
                                {
                                    //readTagValue = data.substring(0,24);
                                    readTagValue = data;

                                    if(readFlag == 0)
                                    {
                                        //showMessageDialog("EPC Status", "Tag EPC: "+readTagValue);
                                        if(memoryType=="TID")
                                        {
                                            epcNumberTV.setText(readTagValue);
                                            showMessageDialog("TID Status", "Tag TID: "+readTagValue);
                                        }else{
                                            showMessageDialog("EPC Status", "Tag EPC: "+readTagValue);
                                        }
                                    }
                                    else
                                    {
                                        verifyTag(barcodeScanTV.getText().toString(),readTagValue,rfidScanTV.getText().toString());
                                        readFlag = 0;
                                    }
                                }
                                else
                                {
                                    showMessageDialog("EPC Reading Status", "Invalid EPC");
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

    private void showMessageDialogEPCUpdate(String title, String message, final String wrongEPCNumber )
    {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(SerialEncodingEPCActivity.this);
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

                            if (barcodeScanTV.getText().toString().trim().length() > 0 && rfidScanTV.getText().toString().trim().length() > 0) {
                                if (barcodeScanTV.getText().toString().trim().length() == 13) {
                                    String strencodedEpc = eanEncoding(barcodeScanTV.getText().toString(), rfidScanTV.getText().toString());
                                    epcNumberTV.setText(strencodedEpc);
                                    currentEPCNumber = strencodedEpc;
                                    //update EPC on sqlite
                                    dbManager.updateTagsmartStockEPC(wrongEPCNumber, currentEPCNumber);
                                    //update EPC on server
                                    new UpdateEPCAsyncTask(SerialEncodingEPCActivity.this, wrongEPCNumber, currentEPCNumber).execute(Constants.BASE_URL + "UpdateEPCTagSmartStockValues.php");

                                } else {
                                    showMessageDialog("Warning", "13 Digit barcode not allowed");
                                }
                            }


                        }
                    }).show();
        }catch (WindowManager.BadTokenException e) {
            //use a log message
        }
    }

    private void showMessageDialog(String title, String message)
    {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(SerialEncodingEPCActivity.this);
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

    class UpdateTagSmartDetailsAsyncTask extends AsyncTask<String, String, String> {
        String barcode,EPC;
        public UpdateTagSmartDetailsAsyncTask(Activity activity, String ean, String epc) {
            barcode =ean;
            EPC = epc;
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

                String customer_id=pref.getString(Constants.CUSTOMER_ID,"0");
                String store_id = pref.getString(Constants.STORE_ID,"-1");
                String customer_stock_data_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"-1");
                String customer_product_data_table_name =pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"-1");
                URL url = new URL(string[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(60000);
                conn.setConnectTimeout(60000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("X-Environment", "android");


                String urlParameters = "barcode=" + barcode+"&EPC="+EPC+"&customer_id="+customer_id+"&store_id="+store_id+"&customer_stock_data_table_name="+customer_stock_data_table_name+"&customer_product_data_table_name="+customer_product_data_table_name;
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

            System.out.println("Response "+result);

            if (result != null) {
                try
                {
                    JSONObject jObj = new JSONObject(result);
                    if(jObj.has("EPC"))
                    {
                       // System.out.println("Response22222 "+result);
                        //delete data from local sqlite DB, Stock Details Checksum table
                        dbManager.deleteStockDetailsChecksum(jObj.getString("EPC"));
                    }




                } catch (Exception e) {

                    // e.printStackTrace();
                    Toast.makeText(SerialEncodingEPCActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                }


            } else {

                Toast.makeText(SerialEncodingEPCActivity.this, "No response from server", Toast.LENGTH_LONG).show();
            }
        }
    }

    class UpdateEPCAsyncTask extends AsyncTask<String, String, String> {
        String wrongEPC,CorrectEPC;
        private ProgressDialog dialog;
        public UpdateEPCAsyncTask(Activity activity, String wEPC, String cEPC) {
            wrongEPC =wEPC;
            CorrectEPC = cEPC;
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog.setMessage("Processing...");

            dialog.show();

        }

        @Override
        protected String doInBackground(String... string) {
            String response = null;
            try {

                String customer_id=pref.getString(Constants.CUSTOMER_ID,"0");
                String store_id = pref.getString(Constants.STORE_ID,"-1");
                String customer_stock_data_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"-1");
                String customer_product_data_table_name =pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"-1");
                URL url = new URL(string[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(60000);
                conn.setConnectTimeout(60000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("X-Environment", "android");


                String urlParameters = "wrong_EPC=" + wrongEPC+"&new_EPC="+CorrectEPC;
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
            dialog.dismiss();
            System.out.println("Response "+result);

            if (result != null) {
                try
                {
                    JSONObject jObj = new JSONObject(result);
                    if(jObj.has("msg"))
                    {
                        showMessageDialog("EPC Status", jObj.getString("msg"));
                    }

                } catch (Exception e) {

                    // e.printStackTrace();
                    Toast.makeText(SerialEncodingEPCActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                }


            } else {

                Toast.makeText(SerialEncodingEPCActivity.this, "No response from server", Toast.LENGTH_LONG).show();
            }
        }
    }


    public class connectTask extends AsyncTask<String,String,TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {
            String hostname = pref.getString(Constants.HOSTNAME,"");
            String port = pref.getString(Constants.PORT_NUMER,"0");
            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            } ,SerialEncodingEPCActivity.this);
            mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.sound:
                Intent intent2 = new Intent(SerialEncodingEPCActivity.this, SoundManagerActivity.class);
                startActivity(intent2);
                return true;

            case R.id.bluetooth_reader:
                Intent intent3 = new Intent(SerialEncodingEPCActivity.this, EncodingEPCActivity.class);
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

    //To Store EPC and Barcode into EPC_Barcode_Master
    class EPCBarcodeDataAsyncTask extends AsyncTask<String, String, String> {

        OkHttpClient client;
        private ProgressDialog dialog;

        public EPCBarcodeDataAsyncTask(Activity activity) {
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

               // String urlParameters = "barcode=" + barcodeScanTV.getText().toString().trim()+"&epc="+epcNumberTV.getText().toString().trim();
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
                try {
                    System.out.println("result---------");
                    JSONObject jsonObject = new JSONObject(result);
                    System.out.println("jsonObject---------");
                    if(jsonObject.getInt("output")==1)
                    {
                        Toast.makeText(SerialEncodingEPCActivity.this, "Data Added Successfully.", Toast.LENGTH_LONG).show();
                    }else if(jsonObject.getInt("output")==2)
                    {
                        Toast.makeText(SerialEncodingEPCActivity.this, "EPC Already EXIST.", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(SerialEncodingEPCActivity.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(SerialEncodingEPCActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                    finish();
                }


            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(SerialEncodingEPCActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    //For very EPC
    class GetEPCBarcodeDataAsyncTask extends AsyncTask<String, String, String> {

        OkHttpClient client;
        private ProgressDialog dialog;

        public GetEPCBarcodeDataAsyncTask(Activity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog.setMessage("Fetching Inward Data...");

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
                String urlParameters = "epc="+readTagValue.trim();
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
                try {

                    if(result.trim().equals(barcodeScanTV.getText().toString().trim()))
                    {
                        showMessageDialog("EPC Number Verification Status", "Correct EPC Number and Barcode is "+result.trim());
                        //Toast.makeText(EncodingEPCActivity.this, "Correct EPC Number.", Toast.LENGTH_LONG).show();
                    }

                 /*
                    if(jsonObject.getInt("output")==1)
                    {
                        Toast.makeText(EncodingEPCActivity.this, "Data Added Successfully.", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(EncodingEPCActivity.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
                    }
                */

                } catch (Exception e) {
                    e.printStackTrace();
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(SerialEncodingEPCActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                    finish();
                }


            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(SerialEncodingEPCActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    //To Store EPC and Barcode into EPC_Barcode_Master
    class EPCBarcodeDataAsyncTask_2 extends AsyncTask<String, String, String> {

        OkHttpClient client;
        private ProgressDialog dialog;

        public EPCBarcodeDataAsyncTask_2(Activity activity) {
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

                // String urlParameters = "barcode=" + barcodeScanTV.getText().toString().trim()+"&epc="+epcNumberTV.getText().toString().trim();
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
                try {
                    System.out.println("result---------");
                    JSONObject jsonObject = new JSONObject(result);
                    System.out.println("jsonObject---------");
                    if(jsonObject.getInt("output")==1)
                    {
                        Toast.makeText(SerialEncodingEPCActivity.this, "Data Added Successfully.", Toast.LENGTH_LONG).show();
                    }else if(jsonObject.getInt("output")==2)
                    {
                        Toast.makeText(SerialEncodingEPCActivity.this, "EPC Already EXIST.", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(SerialEncodingEPCActivity.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(SerialEncodingEPCActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                    finish();
                }


            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(SerialEncodingEPCActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    //For very EPC
    class GetEPCBarcodeDataAsyncTask_2 extends AsyncTask<String, String, String> {

        OkHttpClient client;
        private ProgressDialog dialog;

        public GetEPCBarcodeDataAsyncTask_2(Activity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog.setMessage("Fetching Inward Data...");

            dialog.show();
        }

        @Override
        protected String doInBackground(String... string) {
            String response = null;
            try {
                String store_id = pref.getString(Constants.STORE_ID, "0");

                String urlParameters = "epc=f6431cf8-8b9c-4fb4-b666-6d20e0be62e2";//+readTagValue.trim();
                System.out.println("urlParameters " + urlParameters);
                URL url = new URL(string[0] + urlParameters);
                System.out.println("url: " + url.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(7000);
                conn.setConnectTimeout(7000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                //conn.setRequestProperty("Accept", "application/json");
                //conn.setRequestProperty("X-Environment", "android");

                //  String customer_id = pref.getString(Constants.CUSTOMER_ID,"0");


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
                System.out.println("Response: " + result);
                /*
                try {

                    if(result.trim().equals(barcodeScanTV.getText().toString().trim()))
                    {
                        showMessageDialog("EPC Number Verification Status", "Correct EPC Number and Barcode is "+result.trim());
                        //Toast.makeText(EncodingEPCActivity.this, "Correct EPC Number.", Toast.LENGTH_LONG).show();
                    }


                    if(jsonObject.getInt("output")==1)
                    {
                        Toast.makeText(EncodingEPCActivity.this, "Data Added Successfully.", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(EncodingEPCActivity.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(SerialEncodingEPCActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                    finish();
                }
            */

            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(SerialEncodingEPCActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public class GetMethodDemo extends AsyncTask<String , Void ,String> {
        String server_response;

        public GetMethodDemo(Activity activity) {

        }

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection urlConnection = null;

            try {
                //String urlParameters = "epc=f6431cf8-8b9c-4fb4-b666-6d20e0be62e2";//+readTagValue.trim();
                //System.out.println("urlParameters " + urlParameters);
                //url = new URL(strings[0] + urlParameters);
                //System.out.println("url: " + url.toString());
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    server_response = readStream(urlConnection.getInputStream());
                    Log.v("CatalogClient", server_response);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Response", "" + server_response);
        }
    }

// Converting InputStream to String

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    public class WebService extends AsyncTask<String, Void, String> {

        private Context mContext;
        private OnTaskDoneListener onTaskDoneListener;
        private String urlStr = "";

        public WebService(Context context) {
            this.mContext = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                URL mUrl = new URL(strings[0]);
                HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.setRequestProperty("Content-length", "0");
                httpConnection.setUseCaches(false);
                httpConnection.setAllowUserInteraction(false);
                httpConnection.setConnectTimeout(100000);
                httpConnection.setReadTimeout(100000);

                httpConnection.connect();

                int responseCode = httpConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println(sb.toString());
                    return sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            /*
            if (onTaskDoneListener != null && s != null) {
                onTaskDoneListener.onTaskDone(s);
            } else
                onTaskDoneListener.onError();
             */
        }
    }

    public interface OnTaskDoneListener {
        void onTaskDone(String responseData);

        void onError();
    }

}