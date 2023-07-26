package sysnotion.tagid.tagsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import sysnotion.tagid.tagsmart.nLevel.NLevelAdapter;
import sysnotion.tagid.tagsmart.nLevel.NLevelItem;
import sysnotion.tagid.tagsmart.nLevel.NLevelView;
import sysnotion.tagid.tagsmart.nLevel.SomeObject;
import sysnotion.tagid.tagsmart.utils.Constants;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PickupActivity extends AppCompatActivity {
    SharedPreferences pref;
    private ProgressDialog dialog;
    ArrayList<String> selectedBarcodeList = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup);
        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        dialog = new ProgressDialog(PickupActivity.this);



        if(Constants.isNetworkAvailable(PickupActivity.this)) {
            new PickupActivity.LoadBarcodesAsyncTask(PickupActivity.this).execute(Constants.DOMAIN_URL+"product_tree_pickup_by_customer.php");
        }
        else
        {
            Constants.internetAlert(PickupActivity.this);
        }
    }

    private void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    class LoadBarcodesAsyncTask extends AsyncTask<String, String, String> {


        private Context context;

        public LoadBarcodesAsyncTask(Context con) {

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
                    return buffer.toString();
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
            //UI Generating loop
            dismissProgressDialog();

            ArrayList<String>mArrayProducts = new ArrayList<String>();
            if (result != null) {
                try {
                    JSONArray jArray = new JSONArray(result);
                    for (int i=0;i<jArray.length();i++){
                        mArrayProducts.add((String) jArray.get(i));
                    }

                    if(mArrayProducts.size() ==0)
                    {
                        Toast.makeText(getApplicationContext(), "No Data Found." , Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else
                    {
                        //saving to shared preferences
                        Constants.saveArrayList(mArrayProducts,Constants.CATEGORY_ARRAY_JSON, PickupActivity.this);

                        Intent intent = new Intent(PickupActivity.this , SerialPickupSearchActivity.class);
                        startActivity(intent);
                        finish();
                    }


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong. JSON error." , Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "Something went wrong. No response from the server." , Toast.LENGTH_LONG).show();
            }


        }
    }


}