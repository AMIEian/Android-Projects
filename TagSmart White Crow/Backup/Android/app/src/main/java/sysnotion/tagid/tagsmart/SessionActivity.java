package sysnotion.tagid.tagsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import okhttp3.OkHttpClient;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import static android.view.View.TEXT_ALIGNMENT_CENTER;

public class SessionActivity extends AppCompatActivity {
    TextView StoreIdTV;
    SharedPreferences pref;
    ListView listViewSession;
    Button newSessionBtn;
    String sessionMasterCopy = new String();
    ArrayList<String>sessionUserList = new ArrayList<String>();
    ArrayList<String>sessionIDList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        StoreIdTV =(TextView) findViewById(R.id.StoreIdTV);
        StoreIdTV.setText("Store ID: "+pref.getString(Constants.STORE_NUMBER,"0"));

        listViewSession  =(ListView) findViewById(R.id.listViewSession);
        newSessionBtn  =(Button) findViewById(R.id.newSessionBtn);
        newSessionBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(Constants.isNetworkAvailable(SessionActivity.this)) {
                    new IncrementSessionAsyncTask(SessionActivity.this).execute(Constants.BASE_URL + "IncrementSession.php");
                }
                else
                {
                    Constants.internetAlert(SessionActivity.this);
                }
            }
        });

        if(Constants.isNetworkAvailable(SessionActivity.this)) {
            new GetSessionListAsyncTask(SessionActivity.this).execute(Constants.BASE_URL + "GetSessionList.php");
        }
        else
        {
            Constants.internetAlert(SessionActivity.this);
        }
    }

    class IncrementSessionAsyncTask extends AsyncTask<String, String, String> {

        OkHttpClient client;
        private ProgressDialog dialog;

        public IncrementSessionAsyncTask(Activity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog.setMessage("Update session Id...");

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


                String urlParameters = "customer_id=" + customer_id;
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
            System.out.println("Response create new session "+result);
            if(dialog.isShowing())
            {
                dialog.dismiss();
            }

            if (result != null) {
                try
                {

                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    sessionMasterCopy = jsonObject.getString("session_master");

                    if(status.compareTo("1") == 0)
                    {
                        Toast.makeText(SessionActivity.this, "Sucessfully created new session "+sessionMasterCopy, Toast.LENGTH_LONG).show();
                        Constants.saveSessionID(sessionMasterCopy,SessionActivity.this);
                        Constants.saveSessionFlag("1",SessionActivity.this);

                        Intent intent = new Intent(SessionActivity.this , StockTakeAAreaActivity.class);

                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(SessionActivity.this, "Something went wrong. Fail to increment session Id", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if(dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    Toast.makeText(SessionActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                    finish();
                }


            } else {
                if(dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(SessionActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }



    class GetSessionListAsyncTask extends AsyncTask<String, String, String> {

        OkHttpClient client;
        private ProgressDialog dialog;

        public GetSessionListAsyncTask(Activity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog.setMessage("Fetching session list...");

            dialog.show();
        }

        @Override
        protected String doInBackground(String... string) {
            String response = null;
            try {
                String customer_id = pref.getString(Constants.CUSTOMER_ID,"0");
                String user_id = pref.getString(Constants.USER_ID, "0");
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


                String urlParameters = "customer_id=" + customer_id+ "&user_id=" + user_id + "&store_id=" + store_id;
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
            System.out.println("Response session "+result);
            if(dialog.isShowing())
            {
                dialog.dismiss();
            }

            if (result != null) {
                try
                {

                    JSONArray jsonArray = new JSONArray(result);
                    sessionMasterCopy = result;

                    sessionUserList.clear();
                    sessionIDList.clear();

                    for(int s=0; s< jsonArray.length(); s++)
                    {
                        JSONObject tempObject = jsonArray.getJSONObject(s);
                        sessionUserList.add(tempObject.getString("email"));
                        sessionIDList.add(tempObject.getString("id"));
                    }
                    String[] array = sessionUserList.toArray(new String[sessionUserList.size()]);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(SessionActivity.this, android.R.layout.simple_list_item_1, array);
                    listViewSession.setAdapter(adapter);
                    listViewSession.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selectedItem = sessionIDList.get(position);
                        Constants.saveSessionID(selectedItem,SessionActivity.this);
                        Constants.saveSessionFlag("0",SessionActivity.this);
                        Intent intent = new Intent(SessionActivity.this , StockTakeAAreaActivity.class);

                        startActivity(intent);
                        finish();

                    }
                });



                } catch (Exception e) {
                    e.printStackTrace();
                    if(dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    Toast.makeText(SessionActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                    finish();
                }


            } else {
                if(dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(SessionActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
                finish();
            }
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
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}