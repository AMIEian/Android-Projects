package sysnotion.tagid.tagsmart;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import sysnotion.tagid.tagsmart.db.DBManager;
import sysnotion.tagid.tagsmart.utils.Constants;

public class DataImporterActivity extends AppCompatActivity {
    private DBManager dbManager;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_importer);
        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        dbManager = new DBManager(DataImporterActivity.this);

        //important to set category level to 1
        Constants.CATEGORY_LEVEL = 0;


        if (Constants.isNetworkAvailable(DataImporterActivity.this)) {
            new SQLiteTableMetaDataAsyncTask(DataImporterActivity.this).execute(Constants.BASE_URL + "GetSqliteTableMetadata.php");
        } else {
            Constants.internetAlert(DataImporterActivity.this);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }

    class SQLiteTableMetaDataAsyncTask extends AsyncTask<String, String, String> {

        OkHttpClient client;
        private ProgressDialog dialog;

        public SQLiteTableMetaDataAsyncTask(Activity activity) {
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
                String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME, "");
                String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME, "");
                URL url = new URL(string[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(120000);
                conn.setConnectTimeout(120000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("X-Environment", "android");


                String urlParameters = "product_table_name=" + product_table_name + "&stock_table_name=" + stock_table_name;
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
            dialog.dismiss();
            System.out.println("Response SQLiteTableMetaDataAsyncTask " + result);

            if (result != null) {
                try {
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString(Constants.TABLE_METADATA_JSON, result);

                    edit.commit();

                    Intent intent = new Intent(DataImporterActivity.this, HomeActivity.class);
                    startActivity(intent);

                    finish();


                } catch (Exception e) {

                    // e.printStackTrace();
                    Toast.makeText(DataImporterActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                }


            } else {

                Toast.makeText(DataImporterActivity.this, "No response from server.", Toast.LENGTH_LONG).show();
            }
        }
    }
}

/*

    class SQLiteTableDataAsyncTask extends AsyncTask<String, String, String> {

        OkHttpClient client;
        private ProgressDialog dialog;

        public SQLiteTableDataAsyncTask(Activity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog.setMessage("Importing Data...");

            dialog.show();
        }

        @Override
        protected String doInBackground(String... string) {
            String response = null;
            try {
                String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
                String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");
                String store_id = pref.getString(Constants.STORE_ID,"0");
                String customer_id = pref.getString(Constants.CUSTOMER_ID,"0");

                URL url = new URL(string[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(120000);
                conn.setConnectTimeout(120000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("X-Environment", "android");


                String urlParameters = "product_table_name=" + product_table_name+"&stock_table_name="+stock_table_name+"&customer_id=" + customer_id+"&store_id="+store_id;
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
            System.out.println("Response SQLiteTableDataAsyncTask "+result);

            if (result != null) {
                try
                {
                    dbManager.open();

                    //trucating tables
                    dbManager.turcateCategoryMaster();
                    dbManager.turcateStockDetailsChecksum();
                    dbManager.turcateStockTakeChecksum();
                    dbManager.turcateCustomerProductDetails();
                    dbManager.turcateCustomerStockDetails();
                    dbManager.turcateTagsmartStockEPC();
                    dbManager.turcateEPCLocationDetails();

                    //inserting Data in the tables

                    JSONObject jObj = new JSONObject(result);
                    JSONArray productJArray = new JSONArray();
                    JSONArray stockJArray = new JSONArray();
                    if(jObj.has("category_data"))
                    {
                        //Processing category data
                        JSONArray catArray = jObj.getJSONArray("category_data");
                        //Inserting data into category master
                        dbManager.insertCategoryMaster(catArray );


                        if(jObj.has("product_stock_data"))
                        {
                            JSONArray prostoArray = jObj.getJSONArray("product_stock_data");
                            for(int ps = 0; ps< prostoArray.length(); ps++)
                            {

                                //splitting single record into product and stock data
                                JSONObject tempObj = prostoArray.getJSONObject(ps);
                                JSONObject productjson = new JSONObject();
                                JSONObject stockjson = new JSONObject();
                                int prodFlag = 0;
                                int stockCounter = 0;
                                for(int i = 0; i<tempObj.names().length(); i++){
                                    //splitting the data into stock data


                                    if(tempObj.names().getString(i).compareTo("stock_id") == 0)
                                    {

                                        prodFlag = 1;

                                        stockjson.put(tempObj.names().getString(i), tempObj.get(tempObj.names().getString(i)));
                                    }
                                    if(prodFlag == 0)
                                    {
                                        productjson.put(tempObj.names().getString(i), tempObj.get(tempObj.names().getString(i)));
                                    }
                                    if(prodFlag == 1)
                                    {
                                        stockCounter++;
                                        stockjson.put(tempObj.names().getString(i), tempObj.get(tempObj.names().getString(i)));
                                    }
                                    if(prodFlag == 1 && stockCounter == 3)
                                    {
                                        //adding barcode on the fourth place
                                        stockjson.put(tempObj.names().getString(1), tempObj.get(tempObj.names().getString(1)));

                                        stockjson.put(tempObj.names().getString(i), tempObj.get(tempObj.names().getString(i)));
                                    }

                                   // Log.v("import", "key = " + tempObj.names().getString(i) + " value = " + tempObj.get(tempObj.names().getString(i)));
                                }

                                productJArray.put(productjson);
                                stockJArray.put(stockjson);

                            }
                            //Log.v("import", "productjson = " + productJArray);
                            //Log.v("import", "stockjson = " + stockJArray);

                            //Inserting data into product data
                            dbManager.insertCustomerProduct(productJArray );
                            //Inserting data into stock data
                            dbManager.insertCustomerStockDetails(stockJArray );


                            if(jObj.has("tagsmart_epc_data"))
                            {
                                JSONArray tagEPCArray = jObj.getJSONArray("tagsmart_epc_data");
                                JSONArray tagJArray = new JSONArray();
                                JSONArray epcJArray = new JSONArray();
                                for(int ps = 0; ps< tagEPCArray.length(); ps++)
                                {
                                    //splitting single record into product and stock data
                                    JSONObject tempObj = tagEPCArray.getJSONObject(ps);
                                    JSONObject tagjson = new JSONObject();
                                    JSONObject epcjson = new JSONObject();
                                    int prodFlag = 0;
                                    for(int i = 0; i<tempObj.names().length(); i++){
                                        //splitting the data into stock data
                                        if(tempObj.names().getString(i).compareTo("sldt_id") == 0)
                                        {
                                            prodFlag = 1;

                                            epcjson.put(tempObj.names().getString(i), tempObj.get(tempObj.names().getString(i)));
                                            //adding tagsmart_id
                                            epcjson.put(tempObj.names().getString(1), tempObj.get(tempObj.names().getString(1)));


                                        }
                                        if(prodFlag == 0)
                                        {
                                            tagjson.put(tempObj.names().getString(i), tempObj.get(tempObj.names().getString(i)));
                                        }
                                        if(prodFlag == 1)
                                        {
                                            epcjson.put(tempObj.names().getString(i), tempObj.get(tempObj.names().getString(i)));
                                        }

                                        // Log.v("import", "key = " + tempObj.names().getString(i) + " value = " + tempObj.get(tempObj.names().getString(i)));
                                    }

                                    tagJArray.put(tagjson);
                                    epcJArray.put(epcjson);

                                }

                                //Inserting data into Tagsmart data
                                dbManager.insertTagsmartStockDetailsByID(tagJArray );
                                //Inserting data into EPC data
                                dbManager.insertEPCLocationDetailsBYID(epcJArray );

                            }
                        }

                    }

                    if(dialog.isShowing())
                    {
                        dialog.dismiss();
                    }







                } catch (Exception e) {
                     e.printStackTrace();
                    if(dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    Toast.makeText(DataImporterActivity.this, "No response from server", Toast.LENGTH_LONG).show();
                    finish();
                }


            } else {
                if(dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(DataImporterActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


}

 */