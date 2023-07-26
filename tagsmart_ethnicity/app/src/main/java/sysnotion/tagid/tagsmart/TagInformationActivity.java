package sysnotion.tagid.tagsmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import co.kr.bluebird.sled.BTReader;
import co.kr.bluebird.sled.SDConsts;
import sysnotion.tagid.tagsmart.adapter.InventoryAdapter;
import sysnotion.tagid.tagsmart.adapter.SearchAdapter;
import sysnotion.tagid.tagsmart.db.DBManager;
import sysnotion.tagid.tagsmart.model.Inventory;
import sysnotion.tagid.tagsmart.utils.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
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

public class TagInformationActivity extends AppCompatActivity {
    TextView StoreIdTV;
    Button scanTagBtn;
    ListView categoryListView;
    ArrayList<Inventory> categoryList = new ArrayList<Inventory>();
    SearchAdapter mAdapter;
    SharedPreferences pref;
    BTReader reader=null;
    private final  BarcodeHandler mBarcodeHandler = new  BarcodeHandler(this);
    TableLayout  mainTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_information);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        StoreIdTV =(TextView) findViewById(R.id.StoreIdTV);
        StoreIdTV.setText("Store ID: "+pref.getString(Constants.STORE_NUMBER,"0"));

        mainTable = (TableLayout) findViewById(R.id.headerTL);

        scanTagBtn =(Button) findViewById(R.id.scanTagBtn);
        scanTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readTag();
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

            if (reader.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED)
            {
                result=reader.RF_SetRadioPowerState(7);
                if(result==SDConsts.RFResult.SUCCESS)
                {
                    result = reader.RF_READ(SDConsts.RFMemType.EPC, 2, 7, "00000000", false);

                    if (result == SDConsts.RFResult.SUCCESS)
                    {
                        Toast.makeText(TagInformationActivity.this, "Reading......", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        showMessageDialog("Status", "Read failed");
                    }

                }else{

                    showMessageDialog("Status", "Power not changed");
                }

            }else {
                showMessageDialogWithBluetoothActivity("Bluetooth Status", "Bluetooth Not Connected");
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
        reader = BTReader.getReader(this, mBarcodeHandler);
        if (reader != null)
            openResult = reader.SD_Open();
        if (openResult == SDConsts.RF_OPEN_SUCCESS) {
            if (reader.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED)
                Toast.makeText(getBaseContext(), reader.BT_GetConnectedDeviceName() + "\n" + reader.BT_GetConnectedDeviceAddr(), Toast.LENGTH_SHORT).show();
            else
                showMessageDialogWithBluetoothActivity("Bluetooth Status", "Bluetooth not connected");

        }

        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            case R.id.bluetooth_settings:
                Intent intent = new Intent(TagInformationActivity.this, BTConnectivityActivity.class);
                startActivity(intent);
                return true;

            case R.id.sound:
                Intent intent2 = new Intent(TagInformationActivity.this, SoundManagerActivity.class);
                startActivity(intent2);
                return true;

            case R.id.serial_reader:
                Intent intent3 = new Intent(TagInformationActivity.this, SerialTagInformationActivity.class);
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
        private final WeakReference<TagInformationActivity> mExecutor;
        public BarcodeHandler(TagInformationActivity f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            TagInformationActivity executor = mExecutor.get();
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

                                //String barcode = Constants.convertEPCToBarcode(data);

                                new SearchByBarcodeAsyncTask(TagInformationActivity.this, data.substring(0, 24)).execute(Constants.BASE_URL + "GetDetailsByBarcode.php");


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
        AlertDialog.Builder dialog = new AlertDialog.Builder(TagInformationActivity.this);
        dialog.setTitle(title )
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
    }

    private void showMessageDialogWithBluetoothActivity(String title, String message)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(TagInformationActivity.this);
        dialog.setTitle(title )
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(message)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        dialoginterface.cancel();
                    }})
                .setPositiveButton("Bluetooth Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {

                        Intent intent = new Intent(TagInformationActivity.this, BTConnectivityActivity.class);
                        startActivity(intent);
                    }
                }).show();
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

    class SearchByBarcodeAsyncTask extends AsyncTask<String, String, String> {
        String EPC;
        private ProgressDialog dialog;
        public SearchByBarcodeAsyncTask(Activity activity, String ean ) {
            EPC =ean;
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
                String user_id=pref.getString(Constants.USER_ID,"0");
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


                String urlParameters = "epc=" + EPC+"&customer_id="+customer_id+"&store_id="+store_id+"&customer_stock_data_table_name="+customer_stock_data_table_name+"&customer_product_data_table_name="+customer_product_data_table_name+"&user_id="+user_id;
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
            categoryList.clear();
            System.out.println("Response "+result);

            if (result != null) {
                try
                {
                    JSONObject barcodeDetailsObj = new JSONObject(result);
                    JSONArray jArray = barcodeDetailsObj.getJSONArray("barcode_details");
                    JSONArray categoryHeaderArray = barcodeDetailsObj.getJSONArray("category_details");
                    TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.0f);
                    int leftMargin=4;
                    int topMargin=2;
                    int rightMargin=4;
                    int bottomMargin=2;

                    tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

                    JSONObject jobj = jArray.getJSONObject(0);

                    // System.out.println("jobj "+jobj.toString());

                    //Adding Barcode details
                    /* create a header table row */
                    TableRow tableRowBarcode = new TableRow(TagInformationActivity.this);
                    tableRowBarcode.setLayoutParams(tableRowParams);
                    tableRowBarcode.setBackgroundResource(R.drawable.platinum_color_rect);
                    tableRowBarcode.setPadding(4,9,4,9);
                    /* create cell element - textview */
                    TextView tvBarcode = new TextView(TagInformationActivity.this);

                    tvBarcode.setText("Barcode");

                    TableRow.LayoutParams childBarcodeParam = new TableRow.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT);
                    childBarcodeParam.weight = 0.5f;

                    /* set params for cell elements */
                    tvBarcode.setLayoutParams(childBarcodeParam);
                    tvBarcode.setGravity(Gravity.LEFT);
                    tvBarcode.setPadding(4, 9, 4, 9);
                    tvBarcode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                    tvBarcode.setTextColor(getResources().getColor(R.color.iridium));

                    /* add views to the row */
                    tableRowBarcode.addView(tvBarcode);
                    /* create cell element - textview */
                    TextView tvBarcodeVal = new TextView(TagInformationActivity.this);

                    SpannableString spannablecontent=new SpannableString(" "+jobj.getString("barcode" )+" ");
                    spannablecontent.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.orange_red)),
                            0,spannablecontent.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    tvBarcodeVal.setText(spannablecontent);

                    /* set params for cell elements */
                    tvBarcodeVal.setLayoutParams(childBarcodeParam);
                    tvBarcodeVal.setGravity(Gravity.LEFT);
                    tvBarcodeVal.setPadding(4, 9, 4, 9);
                    tvBarcodeVal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                    tvBarcodeVal.setTextColor(getResources().getColor(R.color.rainbow_white));


                    /* add views to the row */
                    tableRowBarcode.addView(tvBarcodeVal);

                    mainTable.addView(tableRowBarcode);

                    for (int j = 0; j < categoryHeaderArray.length(); j++) {

                        /* create a header table row */
                        TableRow tableRow = new TableRow(TagInformationActivity.this);
                        tableRow.setLayoutParams(tableRowParams);
                        tableRow.setBackgroundResource(R.drawable.platinum_color_rect);
                        tableRow.setPadding(4,9,4,9);
                        /* create cell element - textview */
                        TextView tv = new TextView(TagInformationActivity.this);

                        tv.setText(toCamelCase(categoryHeaderArray.getJSONObject(j).getString("category_column_name").replaceAll("_", " ")));

                        TableRow.LayoutParams childParam1 = new TableRow.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT);
                        childParam1.weight = 0.5f;

                        /* set params for cell elements */
                        tv.setLayoutParams(childParam1);
                        tv.setGravity(Gravity.LEFT);
                        tv.setPadding(4, 9, 4, 9);
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                        tv.setTextColor(getResources().getColor(R.color.iridium));

                        /* add views to the row */
                        tableRow.addView(tv);


                        /* create cell element - textview */
                        TextView tv2 = new TextView(TagInformationActivity.this);

                        tv2.setText( jobj.getString(categoryHeaderArray.getJSONObject(j).getString("category_column_name") ) );

                        /* set params for cell elements */
                        tv2.setLayoutParams(childParam1);
                        tv2.setGravity(Gravity.LEFT);
                        tv2.setPadding(4, 9, 4, 9);
                        tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                        tv2.setTextColor(getResources().getColor(R.color.iridium));

                        /* add views to the row */
                        tableRow.addView(tv2);

                        mainTable.addView(tableRow);

                    }
                    JSONArray lepcArray = jobj.getJSONArray("location_epc");
                    for(int k=0; k <lepcArray.length(); k++)
                    {
                        JSONObject tempObj =  lepcArray.getJSONObject(k);

                        /* create a header table row */
                        TableRow tableRowLocation = new TableRow(TagInformationActivity.this);
                        tableRowLocation.setLayoutParams(tableRowParams);
                        tableRowLocation.setBackgroundResource(R.drawable.platinum_color_rect);
                        tableRowLocation.setPadding(4,9,4,9);
                        /* create cell element - textview */
                        TextView tvLocation = new TextView(TagInformationActivity.this);
                        tvLocation.setText(tempObj.getString("sublocation"));

                        TableRow.LayoutParams childParam1 = new TableRow.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT);
                        childParam1.weight = 0.5f;

                        /* set params for cell elements */
                        tvLocation.setLayoutParams(childParam1);
                        tvLocation.setGravity(Gravity.LEFT);
                        tvLocation.setPadding(4, 9, 4, 9);
                        tvLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                        tvLocation.setTextColor(getResources().getColor(R.color.iridium));

                        /* add views to the row */
                        tableRowLocation.addView(tvLocation);


                        /* create cell element - textview */
                        TextView tvLocationVal = new TextView(TagInformationActivity.this);

                        JSONArray ja = tempObj.getJSONArray("epc");
                        tvLocationVal.setText( ja.length()+" qty" );

                        /* set params for cell elements */
                        tvLocationVal.setLayoutParams(childParam1);
                        tvLocationVal.setGravity(Gravity.LEFT);
                        tvLocationVal.setPadding(4, 9, 4, 9);
                        tvLocationVal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                        tvLocationVal.setTextColor(getResources().getColor(R.color.iridium));

                        /* add views to the row */
                        tableRowLocation.addView(tvLocationVal);

                        mainTable.addView(tableRowLocation);

                    }

                    JSONArray optionsColumnsNamesArray = barcodeDetailsObj.getJSONArray("options_columns_names");
                    JSONArray optionsArray = barcodeDetailsObj.getJSONArray("options");

                    /* create a value table row */
                    TableRow tableRowVariantAvailable = new TableRow(TagInformationActivity.this);
                    tableRowVariantAvailable.setLayoutParams(tableRowParams);
                    tableRowVariantAvailable.setBackgroundResource(R.drawable.light_blue_color_rect);
                    tableRowVariantAvailable.setPadding(4,9,4,9);
                    /* create cell element - textview */
                    TextView tableRowHeaderAvailable = new TextView(TagInformationActivity.this);

                    tableRowHeaderAvailable.setText("Similar Items Availability");

                    /* set params for cell elements */
                    tableRowHeaderAvailable.setLayoutParams(childBarcodeParam);
                    tableRowHeaderAvailable.setGravity(Gravity.LEFT);
                    tableRowHeaderAvailable.setPadding(4, 9, 4, 9);
                    tableRowHeaderAvailable.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                    tableRowHeaderAvailable.setTextColor(getResources().getColor(R.color.iridium));
                    tableRowHeaderAvailable.setTypeface(null, Typeface.BOLD);

                    /* add views to the row */
                    tableRowVariantAvailable.addView(tableRowHeaderAvailable);
                    /* create cell element - textview /
                    TextView tableRowHeaderQty = new TextView(TagInformationActivity.this);

                    tableRowHeaderQty.setText("");

                    /* set params for cell elements /
                    tableRowHeaderQty.setLayoutParams(childBarcodeParam);
                    tableRowHeaderQty.setGravity(Gravity.LEFT);
                    tableRowHeaderQty.setPadding(4, 9, 4, 9);
                    tableRowHeaderQty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                    tableRowHeaderQty.setTextColor(getResources().getColor(R.color.iridium));


                    /* add views to the row /
                    tableRowVariantAvailable.addView(tableRowHeaderQty);*/



                    String header = new String();

                    if(optionsArray.length() > 0)
                    {
                        mainTable.addView(tableRowVariantAvailable);

                        for(int p= 0 ; p< optionsColumnsNamesArray.length(); p++)
                        {
                            if(p == 0)
                            {
                                header = header + toCamelCase(optionsColumnsNamesArray.getJSONObject(p).getString("option_db_column").replaceAll("_", " "));
                            }
                            else
                            {
                                header = header +" / " + toCamelCase(optionsColumnsNamesArray.getJSONObject(p).getString("option_db_column").replaceAll("_", " "));
                            }

                        }


                        //Adding Variant header details
                        /* create a header table row */
                        TableRow tableRowVariant = new TableRow(TagInformationActivity.this);
                        tableRowVariant.setLayoutParams(tableRowParams);
                        tableRowVariant.setBackgroundResource(R.drawable.light_blue_color_rect);
                        tableRowVariant.setPadding(4,9,4,9);
                        /* create cell element - textview */
                        TextView tableRowVariantName = new TextView(TagInformationActivity.this);

                        tableRowVariantName.setText(header);

                        /* set params for cell elements */
                        tableRowVariantName.setLayoutParams(childBarcodeParam);
                        tableRowVariantName.setGravity(Gravity.LEFT);
                        tableRowVariantName.setPadding(4, 9, 4, 9);
                        tableRowVariantName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                        tableRowVariantName.setTextColor(getResources().getColor(R.color.iridium));

                        /* add views to the row */
                        tableRowVariant.addView(tableRowVariantName);
                        /* create cell element - textview */
                        TextView tableRowVariantQty = new TextView(TagInformationActivity.this);

                        tableRowVariantQty.setText("Qty");

                        /* set params for cell elements */
                        tableRowVariantQty.setLayoutParams(childBarcodeParam);
                        tableRowVariantQty.setGravity(Gravity.LEFT);
                        tableRowVariantQty.setPadding(4, 9, 4, 9);
                        tableRowVariantQty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                        tableRowVariantQty.setTextColor(getResources().getColor(R.color.iridium));


                        /* add views to the row */
                        tableRowVariant.addView(tableRowVariantQty);

                        mainTable.addView(tableRowVariant);

                        for(int v= 0 ; v< optionsArray.length(); v++)
                        {
                            /* create a value table row */
                            TableRow tableRowVariantDesc = new TableRow(TagInformationActivity.this);
                            tableRowVariantDesc.setLayoutParams(tableRowParams);
                            tableRowVariantDesc.setBackgroundResource(R.drawable.light_blue_color_rect);
                            tableRowVariantDesc.setPadding(4,9,4,9);
                            /* create cell element - textview */
                            TextView tableRowVariantDescName = new TextView(TagInformationActivity.this);
                            String variant = new String();
                            for(int p= 0 ; p< optionsColumnsNamesArray.length(); p++)
                            {
                                if(p == 0)
                                {
                                    variant = variant + optionsArray.getJSONObject(v).getString(optionsColumnsNamesArray.getJSONObject(p).getString("option_db_column")) ;
                                }
                                else
                                {
                                    variant = variant +" / " + optionsArray.getJSONObject(v).getString(optionsColumnsNamesArray.getJSONObject(p).getString("option_db_column")) ;
                                }

                            }
                            tableRowVariantDescName.setText(variant);

                            /* set params for cell elements */
                            tableRowVariantDescName.setLayoutParams(childBarcodeParam);
                            tableRowVariantDescName.setGravity(Gravity.LEFT);
                            tableRowVariantDescName.setPadding(4, 9, 4, 9);
                            tableRowVariantDescName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                            tableRowVariantDescName.setTextColor(getResources().getColor(R.color.iridium));

                            /* add views to the row */
                            tableRowVariantDesc.addView(tableRowVariantDescName);
                            /* create cell element - textview */
                            TextView tableRowVQty = new TextView(TagInformationActivity.this);

                            tableRowVQty.setText(optionsArray.getJSONObject(v).getString("scan_qty"));

                            /* set params for cell elements */
                            tableRowVQty.setLayoutParams(childBarcodeParam);
                            tableRowVQty.setGravity(Gravity.LEFT);
                            tableRowVQty.setPadding(4, 9, 4, 9);
                            tableRowVQty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                            tableRowVQty.setTextColor(getResources().getColor(R.color.iridium));


                            /* add views to the row */
                            tableRowVariantDesc.addView(tableRowVQty);

                            mainTable.addView(tableRowVariantDesc);
                        }
                    }


                } catch (Exception e) {

                    e.printStackTrace();
                    Toast.makeText(TagInformationActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                }


            } else {

                Toast.makeText(TagInformationActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
            }
        }
    }


}