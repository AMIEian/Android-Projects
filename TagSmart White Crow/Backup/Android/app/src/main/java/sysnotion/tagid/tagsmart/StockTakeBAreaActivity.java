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
import android.widget.Button;
import android.widget.LinearLayout;
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

import static android.view.View.TEXT_ALIGNMENT_CENTER;

public class StockTakeBAreaActivity  extends AppCompatActivity {

    TextView StoreIdTV;
    SharedPreferences pref;
    LinearLayout buttonAreaLL;
    JSONArray locationJArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_take_b_area);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        StoreIdTV =(TextView) findViewById(R.id.StoreIdTV);
        StoreIdTV.setText("Store ID: "+pref.getString(Constants.STORE_NUMBER,"0"));

        buttonAreaLL =(LinearLayout) findViewById(R.id.buttonAreaLL);

        if(Constants.isNetworkAvailable(StockTakeBAreaActivity.this)) {
            new  StoreLocationDataAsyncTask(StockTakeBAreaActivity.this).execute(Constants.BASE_URL + "GetStoreLocation.php");
        }
        else
        {
            Constants.internetAlert(StockTakeBAreaActivity.this);
        }
    }


    class StoreLocationDataAsyncTask extends AsyncTask<String, String, String> {

        OkHttpClient client;
        private ProgressDialog dialog;

        public StoreLocationDataAsyncTask(Activity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog.setMessage("Fetching Location...");

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
            //System.out.println("Response TABLE_DATA_JSON "+result);
            if(dialog.isShowing())
            {
                dialog.dismiss();
            }

            if (result != null) {
                try
                {

                    locationJArray = new JSONArray(result);

                    for(int loc =0 ; loc< locationJArray.length(); loc++)
                    {
                        JSONObject jObj = locationJArray.getJSONObject(loc);
                        LinearLayout.LayoutParams layoutParams = new  LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 80);
                        layoutParams.setMargins(19, 19, 19, 19); // left, top, right, bottom
                        layoutParams.gravity = Gravity.CENTER;
                        Button button=new Button(StockTakeBAreaActivity.this);
                        button.setText(jObj.getString("sublocation"));
                        button.setId(loc);
                        button.setPadding(59,0,59,0);
                        button.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
                        button.setTextAlignment( TEXT_ALIGNMENT_CENTER);
                        button.setBackground(getResources().getDrawable(R.drawable.button_blue_selector));
                        button.setTextColor(getResources().getColor(R.color.rainbow_white));

                        button.setLayoutParams(layoutParams);

                        button.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                int index  = view.getId();
                                try {
                                    JSONObject jObj = locationJArray.getJSONObject(index);
                                    Intent intent = new Intent(StockTakeBAreaActivity.this , SerialInventoryStockTakeBActivity.class);
                                    intent.putExtra("location_JSON",jObj.toString());
                                    startActivity(intent);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        buttonAreaLL.addView(button);
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                    if(dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    Toast.makeText(StockTakeBAreaActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                    finish();
                }


            } else {
                if(dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(StockTakeBAreaActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
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