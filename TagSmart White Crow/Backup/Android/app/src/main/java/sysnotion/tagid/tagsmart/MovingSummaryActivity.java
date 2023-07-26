package sysnotion.tagid.tagsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SelectionCriterias;
import sysnotion.tagid.tagsmart.adapter.InventoryAdapter;
import sysnotion.tagid.tagsmart.adapter.MissingEPCAdapter;
import sysnotion.tagid.tagsmart.db.DBManager;
import sysnotion.tagid.tagsmart.model.BarcodeEPCSearch;
import sysnotion.tagid.tagsmart.model.Inventory;
import sysnotion.tagid.tagsmart.utils.Constants;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MovingSummaryActivity extends AppCompatActivity {
    SharedPreferences pref;
    private DBManager dbManager;
    JSONObject locationObject;
    TextView StoreIdShopTV, progressTV, precentTV, timeTV, totalScanTV;
    ListView categoryListView, summaryListView;
    ArrayList<BarcodeEPCSearch> categoryList = new ArrayList<BarcodeEPCSearch>();
    MissingEPCAdapter mAdapter;
    JSONArray missingEPCBarcodeObj;

    ProgressBar vprogressbar;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moving_summary);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        dbManager = new DBManager(MovingSummaryActivity.this);

        pDialog = new ProgressDialog(MovingSummaryActivity.this);

        StoreIdShopTV =(TextView) findViewById(R.id.StoreIdShopTV);
        try {
            locationObject = new JSONObject(getIntent().getStringExtra("location_JSON"));
            location_id=locationObject.getString("location_id");
            sublocation_id=locationObject.getString("sub_location_id");

            StoreIdShopTV.setText("Store ID: "+pref.getString(Constants.STORE_NUMBER,"0")+" / "+locationObject.getString("location")+" - "+locationObject.getString("sublocation"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        totalScanTV =(TextView) findViewById(R.id.totalScanTV);
        totalScanTV.setText(getIntent().getStringExtra("scanned"));
        progressTV =(TextView) findViewById(R.id.progressTV);
        progressTV.setText(getIntent().getStringExtra("progress"));

        precentTV =(TextView) findViewById(R.id.precentTV);
        precentTV.setText(getIntent().getStringExtra("percentage"));

        timeTV =(TextView) findViewById(R.id.timeTV);
        timeTV.setText(getIntent().getStringExtra("time"));
        summaryListView = (ListView) findViewById(R.id.summaryListView);
        if(Constants.isNetworkAvailable(MovingSummaryActivity.this)) {
            new MovingSummaryActivity.StockTakeSummaryAsyncTask(MovingSummaryActivity.this).execute(Constants.BASE_URL+"MovingSummary.php");
        }
        else
        {
            Constants.internetAlert(MovingSummaryActivity.this);
        }
    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        dbManager.close();
        super.onDestroy();
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
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    class StockTakeSummaryAsyncTask extends AsyncTask<String, String, String> {

        public StockTakeSummaryAsyncTask(Activity activity) {


        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Generating Moving Summary...");

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
                String sessionId = Constants.getSessionId(MovingSummaryActivity.this);

                String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");
                String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");

                String urlParameters = "user_id="+user_id+"&stock_table_name=" + stock_table_name+"&product_table_name=" + product_table_name+"&customer_id="+customer_id+"&location_id="+location_id+"&sub_location_id="+sublocation_id+"&stock_date="+modifiedDate ;


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
                    //summary UI
                    inventoryJsonArray = jObj.getJSONArray("epcList");
                    for (int i=0 ;i< inventoryJsonArray.length(); i++)
                    {
                        JSONObject obj = inventoryJsonArray.getJSONObject(i);
                        String name = obj.getString("category");
                        String percentage, quantity;
                        quantity = obj.getString("scan_qty")+" / "+obj.getString("total_qty");
                        int sq = Integer.valueOf(obj.getString("scan_qty"));
                        int tq = Integer.valueOf(obj.getString("total_qty"));
                        if(sq==0)
                        {
                            percentage = "0";
                        }
                        else
                        {
                            float f = (sq/tq )*100;
                            percentage = f+"";
                        }
                        Inventory in = new Inventory(name, percentage, quantity);
                        inList.add(in);
                    }
                    InventoryAdapter inAdapter = new InventoryAdapter(MovingSummaryActivity.this,inList);
                    summaryListView.setAdapter(inAdapter);



                } catch (Exception e) {

                    // e.printStackTrace();
                    Toast.makeText(MovingSummaryActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                }


            } else {

                Toast.makeText(MovingSummaryActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
            }
        }
    }

}