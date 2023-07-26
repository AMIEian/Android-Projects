package sysnotion.tagid.tagsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import okhttp3.OkHttpClient;
import sysnotion.tagid.tagsmart.adapter.InwardAdapter;
import sysnotion.tagid.tagsmart.adapter.MultiSelectionAdapter;
import sysnotion.tagid.tagsmart.db.DBManager;
import sysnotion.tagid.tagsmart.model.Inward;
import sysnotion.tagid.tagsmart.utils.Constants;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.view.View.TEXT_ALIGNMENT_CENTER;

public class InwardDataActivity extends AppCompatActivity {
    TextView StoreIdTV;
    SharedPreferences pref;
    DBManager dbManager;
    ArrayList<Inward> categoryArray= new ArrayList<Inward>();
    ListView inwardListView;
    InwardAdapter<String> mAdapter;
    int maxLevel=0;
    String  sessionId="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inward_data);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        dbManager = new DBManager(InwardDataActivity.this);
        dbManager.open();
        dbManager.turcateInwardData();

        StoreIdTV = (TextView) findViewById(R.id.StoreIdTV);
        StoreIdTV.setText("Store ID: " + pref.getString(Constants.STORE_NUMBER, "0"));
        inwardListView = (ListView) findViewById(R.id.inwardList);


        inwardListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter1, View view, int position, long id) {
                Inward iwObject = categoryArray.get(position);
                ArrayList<String> mArrayProducts = dbManager.getInwardDataByDispatchOrder(iwObject.getDispatch_order());
                //System.out.println("Response mArrayProducts "+mArrayProducts);
                if(mArrayProducts.size()> 0)
                {
                    //store MAX_LEVEL &  dispatch_order values
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString(Constants.MAX_LEVEL, String.valueOf(maxLevel));
                    edit.putString(Constants.DISPATCH_ORDER,iwObject.getDispatch_order());
                    edit.commit();
                    //saving to shared preferences
                    Constants.saveArrayList(mArrayProducts,Constants.CATEGORY_ARRAY_JSON, InwardDataActivity.this);

                    //session details
                    Constants.saveSessionID(sessionId,InwardDataActivity.this);


                    Intent intent = new Intent(InwardDataActivity.this , SerialProcessInwardDataActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please select categories " , Toast.LENGTH_LONG).show();
                }


            }
        });


    }

    @Override
    public void onResume(){
        super.onResume();
        if (Constants.isNetworkAvailable(InwardDataActivity.this)) {
            new InwardDataActivity.InwardDataAsyncTask(InwardDataActivity.this).execute(Constants.BASE_URL + "GetInwardingByStoreId.php");
        } else {
            Constants.internetAlert(InwardDataActivity.this);
        }

    }


    class InwardDataAsyncTask extends AsyncTask<String, String, String> {

        OkHttpClient client;
        private ProgressDialog dialog;

        public InwardDataAsyncTask(Activity activity) {
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

                String customer_id = pref.getString(Constants.CUSTOMER_ID,"0");
                String urlParameters = "store_id=" + store_id+"&customer_id="+customer_id;
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

                    JSONObject jsonObject = new JSONObject(result);
                    maxLevel = Integer.parseInt(jsonObject.getString("max_level") );
                    sessionId =jsonObject.getString("session_id");

                    System.out.println("Response max_level "+maxLevel);
                    System.out.println("Response session_id "+sessionId);
                    System.out.println( jsonObject.getJSONArray("details"));

                    JSONArray json = jsonObject.getJSONArray("details");
                    if(json.length() > 0)
                    {
                        dbManager.insertInwardData(json);
                        categoryArray = dbManager.getInwardData();
                        mAdapter = new InwardAdapter<String>(InwardDataActivity.this, categoryArray);
                        inwardListView.setAdapter(mAdapter);

                    }
                    else
                    {
                        Toast.makeText(InwardDataActivity.this, "No data found", Toast.LENGTH_LONG).show();
                    }




                } catch (Exception e) {
                    e.printStackTrace();
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(InwardDataActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                    finish();
                }


            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(InwardDataActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {

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
}