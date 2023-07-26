package sysnotion.tagid.tagsmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import co.kr.bluebird.sled.BTReader;
import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;
import sysnotion.tagid.tagsmart.utils.Constants;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.math.BigInteger;

public class SerialVerifyTagActivity  extends AppCompatActivity {
    TextView StoreIdTV;
    Button scanRFIDBtn, scanBarcodeBtn, GoToEncodeBtn;
    SharedPreferences pref;
    Reader reader=null;
    String barcode= new String(), rfidSerial = new String();

    private final BarcodeHandler mBarcodeHandler = new BarcodeHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_tag);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        scanRFIDBtn  = (Button) findViewById(R.id.scanRFIDBtn);
        scanBarcodeBtn  = (Button) findViewById(R.id.scanBarcodeBtn);
        GoToEncodeBtn = (Button)  findViewById(R.id.GoToEncodeBtn);

        StoreIdTV =(TextView) findViewById(R.id.StoreIdTV);
        StoreIdTV.setText("Store ID: "+pref.getString(Constants.STORE_NUMBER,"0"));

        scanRFIDBtn.setOnClickListener(new View.OnClickListener() {
            int ret;
            @Override
            public void onClick(View view) {

                if(barcode.trim().length() > 0 )
                {
                    readTag();
                }
                else
                {
                    showMessageDialog("Error", "Please scan Barcode first");
                }

            }
        });

        scanBarcodeBtn.setOnClickListener(new View.OnClickListener() {
            int ret;
            @Override
            public void onClick(View view) {

                int  mode = reader.SD_GetTriggerMode();
                if (mode == SDConsts.SDTriggerMode.RFID)
                {
                    reader.SD_SetTriggerMode(SDConsts.SDTriggerMode.BARCODE);
                }
                barcode="";
                if(barcode.length()==0)
                {
                    ret = reader.BC_SetTriggerState(true);
                }


                if (ret == SDConsts.RFResult.SUCCESS)
                {
                    //Toast.makeText(getBaseContext(),"Ret Success",Toast.LENGTH_SHORT).show();
                } else if (ret == SDConsts.RFResult.MODE_ERROR){
                    Toast.makeText(SerialVerifyTagActivity.this, "Start Inventory failed, Please check RFR900 MODE", Toast.LENGTH_SHORT).show();
                }else if (ret == SDConsts.RFResult.LOW_BATTERY){
                    Toast.makeText(SerialVerifyTagActivity.this, "Start Inventory failed, LOW_BATTERY", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SerialVerifyTagActivity.this, "Start Inventory failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        GoToEncodeBtn.setOnClickListener(new View.OnClickListener() {
            int ret;
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SerialVerifyTagActivity.this , SerialEncodingEPCActivity.class);

                startActivity(intent);
                finish();
            }
        });



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

            result=reader.RF_SetRadioPowerState(7);
            if(result==SDConsts.RFResult.SUCCESS)
            {
                result = reader.RF_READ(SDConsts.RFMemType.EPC, 2, 7, "00000000", false);

                if (result == SDConsts.RFResult.SUCCESS)
                {
                    Toast.makeText(SerialVerifyTagActivity.this, "Reading......", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    showMessageDialog("Status", "Read failed");
                }

            }else{

                showMessageDialog("Status", "Power not changed");
            }


        }catch (Exception btnreaderr){
            showMessageDialog("Error", btnreaderr.toString());
        }
        //Reading a particular RFID tag
    }



    public static String leftPad(String str, int length, String padChar){
        String pad = "";
        for (int i = 0; i < length; i++) {
            pad += padChar;
        }
        return pad.trim()+ str.trim();
    }

    private void verifyTag(String strEAN, String EPCNumber)
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

            showMessageDialog("EPC Number Verification Status", "Incorrect EPC Number");
        }

    }

    @Override
    protected void onStart() {

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        boolean openResult = false;
        reader = Reader.getReader(this, mBarcodeHandler);
        if (reader != null)
            openResult = reader.SD_Open();
        if (openResult == SDConsts.SD_OPEN_SUCCESS)
        {
            if(reader.SD_Wakeup()==SDConsts.SDResult.SUCCESS)
            {

                if (reader.SD_GetConnectState() == SDConsts.SDConnectState.CONNECTED)
                {
                    Toast.makeText(getBaseContext(),"SD Already Connected",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(reader.SD_Connect()==SDConsts.SDResult.SUCCESS)
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

        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
   /* @Override
    protected void onStop() {
        Log.d(TAG, " onStop");
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //reader = BTReader.getReader(this, mBarcodeHandler);
        //if (reader != null && reader.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED) {
            //reader.BT_Disconnect();
       // }
       // reader.SD_Close();
        super.onStop();
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("Tag Info", "onRequestPermissionsResult");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.sound:
                Intent intent2 = new Intent(SerialVerifyTagActivity.this, SoundManagerActivity.class);
                startActivity(intent2);
                return true;

            case R.id.bluetooth_reader:
                Intent intent3 = new Intent(SerialVerifyTagActivity.this, VerifyTagActivity.class);
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

    private static class BarcodeHandler extends Handler {
        private final WeakReference<SerialVerifyTagActivity> mExecutor;
        public BarcodeHandler(SerialVerifyTagActivity f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            SerialVerifyTagActivity executor = mExecutor.get();
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
                                Log.d("EANActivity", "EPC "+data);
                                verifyTag(barcode,data);


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



                }else if (m.arg1 == SDConsts.BCCmdMsg.BARCODE_READ)
                {
                    if (m.arg2 == SDConsts.BCResult.SUCCESS)
                    {
                        data = (String) m.obj;
                        if (data.length()!=0)
                        {
                            int infoTagPoint = data.indexOf(';');
                            data = data.substring(0, infoTagPoint);
                            barcode = data;
                            showMessageDialog("Barcode", data);
                        }
                    }else if (m.arg2 == SDConsts.BCResult.ACCESS_TIMEOUT){

                        try {
                            showMessageDialog("Error", "Access Timeout");
                        }
                        catch (WindowManager.BadTokenException e) {
                            //use a log message
                        }
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
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

    private void showMessageDialogWithBluetoothActivity(String title, String message)
    {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
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

                            Intent intent = new Intent(SerialVerifyTagActivity.this, BTConnectivityActivity.class);
                            startActivity(intent);
                        }
                    }).show();
        }catch (WindowManager.BadTokenException e) {
            //use a log message
        }
    }
}