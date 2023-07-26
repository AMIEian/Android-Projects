package sysnotion.tagid.tagsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import sysnotion.tagid.tagsmart.utils.Constants;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovementDashboardActivity extends AppCompatActivity {

    TextView StoreIdTV;
    SharedPreferences pref;
    private CardView card_movement,card_inward,card_replenishment,card_picking;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        dialog = new ProgressDialog(MovementDashboardActivity.this);

        StoreIdTV =(TextView) findViewById(R.id.StoreIdTV);
        StoreIdTV.setText("Store ID: "+pref.getString(Constants.STORE_NUMBER,"0"));

        card_movement = (CardView) findViewById(R.id.card_movement);
        card_inward = (CardView) findViewById(R.id.card_inward);
        card_replenishment = (CardView) findViewById(R.id.card_replenishment);
        card_picking = (CardView) findViewById(R.id.card_picking);

        card_picking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovementDashboardActivity.this , PickupActivity.class);

                startActivity(intent);
            }
        });

        card_movement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovementDashboardActivity.this , MovementActivity.class);

                startActivity(intent);
            }
        });

        card_inward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovementDashboardActivity.this , InwardDataActivity.class);

                startActivity(intent);
            }
        });

        card_replenishment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovementDashboardActivity.this , ReplenishmentActivity.class);

                startActivity(intent);
            }
        });

        if (Constants.isNetworkAvailable(MovementDashboardActivity.this)) {
            new ReplenishmentCountAsyncTask(MovementDashboardActivity.this).execute(Constants.DOMAIN_URL+"get_replenishment_count.php");
        } else {
            Constants.internetAlert(MovementDashboardActivity.this);

        }

    }


    class ReplenishmentCountAsyncTask extends AsyncTask<String, String, String> {



        private Context context;

        public ReplenishmentCountAsyncTask(Context con) {

            context = con;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog.setMessage("Loading data...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... string) {
            String response = null;
            try {
                URL url = new URL(string[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(90000);
                conn.setConnectTimeout(90000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("X-Environment", "android");

                String customer_id = pref.getString(Constants.CUSTOMER_ID,"0");
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

                    return  buffer.toString();
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
            TextView qtyTV = findViewById(R.id.qtyTV);
            if(result != null)
            {
                qtyTV.setText(""+result);
            }
            else
            {
                qtyTV.setText("0");
            }
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
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