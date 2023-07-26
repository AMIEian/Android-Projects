package sysnotion.tagid.tagsmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import co.kr.bluebird.sled.BTReader;
import co.kr.bluebird.sled.SDConsts;
import sysnotion.tagid.tagsmart.adapter.SearchAdapter;
import sysnotion.tagid.tagsmart.model.Inventory;
import sysnotion.tagid.tagsmart.utils.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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
import java.util.Iterator;

public class SearchItemActivity extends AppCompatActivity {
    private static final String TAG = SearchItemActivity.class.getSimpleName();
    EditText barcodeScanTV, textET ;
    ImageButton barcodeScanBtn ;
    BTReader reader=null;
    private final BarcodeHandler mBarcodeHandler = new  BarcodeHandler(this);
    boolean isConnected;
    int readFlag = 0;
    SharedPreferences pref;
    ListView categoryListView;
    ArrayList<Inventory> categoryList = new ArrayList<Inventory>();
    SearchAdapter mAdapter;
    ArrayList<String> barcodeList = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_item);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        categoryListView = (ListView) findViewById(R.id.categoryList);
        mAdapter = new SearchAdapter(this,  categoryList);
        categoryListView.setAdapter(mAdapter);

        barcodeScanTV = (EditText) findViewById(R.id.barcodeScanTV);
        textET = (EditText) findViewById(R.id.textET);
        textET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(textET.getText().toString().trim().length()>=4 )
                {
                    String txt = textET.getText().toString();

                    new  SearchByTextAsyncTask(SearchItemActivity.this,txt).execute(Constants.BASE_URL + "GetDetailsByText.php");

                }
                else{
                    categoryList.clear();
                    mAdapter.notifyDataSetChanged();
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


        barcodeScanBtn = (ImageButton) findViewById(R.id.barcodeScanBtn);
        barcodeScanTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(barcodeScanTV.getText().toString().trim().length()>= 4)
                {

                        String ean = barcodeScanTV.getText().toString();

                        new SearchByBarcodeAsyncTask(SearchItemActivity.this,ean).execute(Constants.BASE_URL + "GetDetailsByBarcode.php");

                }
                else{
                    categoryList.clear();
                    mAdapter.notifyDataSetChanged();
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
                    Toast.makeText(SearchItemActivity.this, "Start Inventory failed, Please check RFR900 MODE", Toast.LENGTH_SHORT).show();
                }else if (ret == SDConsts.RFResult.LOW_BATTERY){
                    Toast.makeText(SearchItemActivity.this, "Start Inventory failed, LOW_BATTERY", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SearchItemActivity.this, "Start Inventory failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        categoryListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Inventory item = (Inventory) categoryListView.getItemAtPosition(position);
                if(position <= barcodeList.size())
                {
                    String barcodeString = barcodeList.get(position);

                    Log.d("SearchItem","barcodeString "+barcodeString);
                    Intent intent = new Intent(SearchItemActivity.this , LocationScanActivity.class);
                    intent.putExtra("barcode",barcodeString);
                    intent.putExtra("divison",item.getCategory());
                    intent.putExtra("qty",item.getQuantity());
                    startActivity(intent);

                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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


    private static class BarcodeHandler extends Handler {
        private final WeakReference<SearchItemActivity> mExecutor;
        public BarcodeHandler(SearchItemActivity f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            SearchItemActivity executor = mExecutor.get();
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

                break;
            case SDConsts.Msg.RFMsg:
                switch (m.arg1) {
                    //RF_Read callback message
                    case SDConsts.RFCmdMsg.INVENTORY:
                    case SDConsts.RFCmdMsg.READ:

                        break;
                    case SDConsts.RFCmdMsg.WRITE:

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


    private void showMessageDialogWithBluetoothActivity(String title, String message)
    {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(SearchItemActivity.this);
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

                            Intent intent = new Intent(SearchItemActivity.this, BTConnectivityActivity.class);
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(SearchItemActivity.this);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.bluetooth_settings:
                Intent intent = new Intent(SearchItemActivity.this, BTConnectivityActivity.class);
                startActivity(intent);
                return true;

            case R.id.sound:
                Intent intent2 = new Intent(SearchItemActivity.this, SoundManagerActivity.class);
                startActivity(intent2);
                return true;

            case R.id.serial_reader:
                Intent intent3 = new Intent(SearchItemActivity.this, SerialSearchItemActivity.class);
                startActivity(intent3);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.inventory_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    class SearchByBarcodeAsyncTask extends AsyncTask<String, String, String> {
        String barcode;
        private ProgressDialog dialog;
        public SearchByBarcodeAsyncTask(Activity activity, String ean ) {
            barcode =ean;
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


                String urlParameters = "barcode=" + barcode+"&customer_id="+customer_id+"&store_id="+store_id+"&customer_stock_data_table_name="+customer_stock_data_table_name+"&customer_product_data_table_name="+customer_product_data_table_name+"&user_id="+user_id;
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
            barcodeList.clear();
            System.out.println("Response "+result);

            if (result != null) {
                try
                {
                    JSONObject jObj = new JSONObject(result);
                    if (jObj.has("barcode_details")) {
                        JSONArray jArray = jObj.getJSONArray("barcode_details");
                        for(int y= 0; y< jArray.length(); y++)
                        {
                            JSONObject jsonObject = jArray.getJSONObject(y);
                            Inventory invObj =  new Inventory();

                            Iterator<String> keys = jsonObject.keys();
                            // get some_name_i_wont_know in str_Name
                            String str_Name=keys.next();
                            // get the value i care about
                            String value = jsonObject.optString(str_Name);

                            invObj.setCategory(value);
                            invObj.setQuantity(jsonObject.getString("quantity"));

                            String ean = barcodeScanTV.getText().toString();
                            barcodeList.add(ean);

                            categoryList.add(invObj);
                        }
                        mAdapter.notifyDataSetChanged();

                        Toast.makeText(SearchItemActivity.this, "Click row to activate the RFID tag locator", Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                        Toast.makeText(SearchItemActivity.this, "No item found", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {

                    // e.printStackTrace();
                    Toast.makeText(SearchItemActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                }


            } else {

                Toast.makeText(SearchItemActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
            }
        }
    }


    class SearchByTextAsyncTask extends AsyncTask<String, String, String> {
        String keyword;
        private ProgressDialog dialog;
        public SearchByTextAsyncTask(Activity activity, String k ) {
            keyword =k;
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


                String urlParameters = "keyword=" + keyword+"&customer_id="+customer_id+"&store_id="+store_id+"&customer_stock_data_table_name="+customer_stock_data_table_name+"&customer_product_data_table_name="+customer_product_data_table_name+"&user_id="+user_id;
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
            mAdapter.notifyDataSetChanged();
            barcodeList.clear();
            System.out.println("Response "+result);

            if (result != null) {
                try
                {
                    JSONObject jObj = new JSONObject(result);
                    if (jObj.has("barcode_details")) {
                        JSONArray jArray = jObj.getJSONArray("barcode_details");
                        for(int y= 0; y< jArray.length(); y++)
                        {
                            JSONObject jsonObject = jArray.getJSONObject(y);
                            Inventory invObj =  new Inventory();

                            Iterator<String> keys = jsonObject.keys();
                            // get some_name_i_wont_know in str_Name
                            String str_Name=keys.next();
                            // get the value i care about
                            String value = jsonObject.optString(str_Name);

                            invObj.setCategory(value);
                            invObj.setQuantity(jsonObject.getString("quantity"));

                            String ean = barcodeScanTV.getText().toString();
                            barcodeList.add(ean);

                            categoryList.add(invObj);
                        }
                        mAdapter.notifyDataSetChanged();

                        Toast.makeText(SearchItemActivity.this, "Click row to activate the RFID tag locator", Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                        Toast.makeText(SearchItemActivity.this, "No item found", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {

                    e.printStackTrace();
                    Toast.makeText(SearchItemActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                }


            } else {

                Toast.makeText(SearchItemActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
            }
        }
    }

}