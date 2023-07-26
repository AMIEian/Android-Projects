package sysnotion.tagid.tagsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import okhttp3.OkHttpClient;
import sysnotion.tagid.tagsmart.adapter.MultiSelectionAdapter;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.List;

public class StockTakeOptionBCategoryActivity extends AppCompatActivity {
    SharedPreferences pref;
    String currentCategory= new String();
    int currentLevel=0;
    TextView StoreIdTV;
    Button nextBtn;
    Spinner auditPlanSelection;
    ArrayAdapter aa;
    ArrayList<String>auditPlanListArray= new ArrayList<String>();
    ArrayList<String>icodeArrayList = new ArrayList<String>();
    ArrayList<String>barcodeArrayList = new ArrayList<String>();
    String icodeListString = new String();
    String mainCategory = new String();
    int maxLevel=0;
    String  sessionId="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_take_option_b_category);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);



        StoreIdTV =(TextView) findViewById(R.id.StoreIdTV);
        StoreIdTV.setText("Store ID: "+pref.getString(Constants.STORE_NUMBER,"0"));

        auditPlanSelection = (Spinner) findViewById(R.id.auditPlanSelection);
        aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,auditPlanListArray);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        auditPlanSelection.setAdapter(aa);
        nextBtn =(Button) findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(icodeArrayList.size() == auditPlanListArray.size()) {


                    icodeListString = icodeArrayList.get(auditPlanSelection.getSelectedItemPosition());
                    String barcodeListString = barcodeArrayList.get(auditPlanSelection.getSelectedItemPosition());
                    //store icodes values
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString(Constants.iCODES, icodeListString);
                    edit.putString(Constants.MAX_LEVEL, String.valueOf(maxLevel));
                    edit.commit();

                    ArrayList<String> mArrayProducts = new ArrayList<String>(Arrays.asList(barcodeListString.split("\\s*,\\s*")));
                    //saving to shared preferences
                    Constants.saveArrayList(mArrayProducts,Constants.CATEGORY_ARRAY_JSON, StockTakeOptionBCategoryActivity.this);

                    //session details
                    Constants.saveSessionID(sessionId,StockTakeOptionBCategoryActivity.this);

                    Log.d(MainActivity.class.getSimpleName(), "Selected barcodeListString: " + barcodeListString.toString());
                    Intent intent = new Intent(StockTakeOptionBCategoryActivity.this , StockTakeBAreaActivity.class);

                    startActivity(intent);




                }
            }
        });

        if(Constants.isNetworkAvailable(StockTakeOptionBCategoryActivity.this)) {
            new GetAuditPlanAsyncTask(StockTakeOptionBCategoryActivity.this).execute(Constants.BASE_URL + "GetAuditPlanByCustomerId.php");
        }
        else
        {
            Constants.internetAlert(StockTakeOptionBCategoryActivity.this);
        }
    }



    class GetAuditPlanAsyncTask extends AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public GetAuditPlanAsyncTask(Activity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog.setMessage("Fetching Data...");

            dialog.show();
        }

        @Override
        protected String doInBackground(String... string) {
            String response = null;
            try {
                String customer_id = pref.getString(Constants.CUSTOMER_ID,"0");
                URL url = new URL(string[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(7000);
                conn.setConnectTimeout(7000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("X-Environment", "android");

                String store_id = pref.getString(Constants.STORE_ID,"0");
                String urlParameters = "customer_id=" + customer_id+"&store_id="+store_id;
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
            icodeArrayList.clear();
            auditPlanListArray.clear();
            barcodeArrayList.clear();


            if (result != null) {
                try
                {
                    JSONObject jsonObject = new JSONObject(result);
                    maxLevel = Integer.parseInt(jsonObject.getString("max_level") );
                    sessionId =jsonObject.getString("session_id");
                    JSONArray jArray = jsonObject.getJSONArray("details");
                    if(jArray.length() > 0)
                    {
                        for(int h=0 ;h< jArray.length(); h++)
                        {
                            JSONObject jObj = jArray.getJSONObject(h);
                            auditPlanListArray.add(jObj.getString("audit_plan_name"));
                            icodeArrayList.add(jObj.getString("icode"));
                            barcodeArrayList.add(jObj.getString("barcodes"));
                        }
                        aa.notifyDataSetChanged();
                    }
                    else
                    {
                        nextBtn.setVisibility(Button.INVISIBLE);

                        AlertDialog.Builder dialog = new AlertDialog.Builder(StockTakeOptionBCategoryActivity.this);
                        dialog.setTitle("Audit Status" )
                                .setIcon(R.mipmap.ic_launcher)
                                .setMessage("No audit data found.")
//     .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//      public void onClick(DialogInterface dialoginterface, int i) {
//          dialoginterface.cancel();
//          }})
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        finish();
                                    }
                                }).show();
                    }



                } catch (Exception e) {

                    // e.printStackTrace();
                    Toast.makeText(StockTakeOptionBCategoryActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                }


            } else {

                Toast.makeText(StockTakeOptionBCategoryActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
            }
        }
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