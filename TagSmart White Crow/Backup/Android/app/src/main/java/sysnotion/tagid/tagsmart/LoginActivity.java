package sysnotion.tagid.tagsmart;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;
import sysnotion.tagid.tagsmart.utils.Constants;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;

public class LoginActivity extends AppCompatActivity {
    private TextView forgetPasswordTV;
    private EditText editTextTextPassword, editTextTextEmailAddress;
    private Button liBtn;
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pref = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        editTextTextEmailAddress =(EditText) findViewById(R.id.editTextTextEmailAddress);
        editTextTextPassword =(EditText) findViewById(R.id.editTextTextPassword);
        liBtn= (Button)findViewById(R.id.liBtn);
        liBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextTextEmailAddress.getText().length() >0 && editTextTextPassword.getText().length()>0) {

                    if(Constants.isNetworkAvailable(LoginActivity.this)) {
                        new UserValidationAsyncTask(LoginActivity.this).execute(Constants.BASE_URL + "UserLogin.php");
                    }
                    else
                    {
                        Constants.internetAlert(LoginActivity.this);
                    }

                }
                else
                {

                    Toast.makeText(LoginActivity.this,"Please enter credentials", Toast.LENGTH_LONG).show();
                }
            }
        });

        forgetPasswordTV =(TextView) findViewById(R.id.forgetPasswordTV);
        forgetPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class );

                startActivity(intent);
                finish();
            }
        });
    }


    class UserValidationAsyncTask extends AsyncTask<String, String, String> {

        OkHttpClient client;
        private ProgressDialog dialog;

        public UserValidationAsyncTask(Activity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog.setMessage("Validating Credentails...");

            dialog.show();
        }

        @Override
        protected String doInBackground(String... string) {
            String response = null;
            try {
                String username = editTextTextEmailAddress.getText().toString();
                String password = editTextTextPassword.getText().toString();
                URL url = new URL(string[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(7000);
                conn.setConnectTimeout(7000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("X-Environment", "android");


                String urlParameters = "user_email=" + username+"&password="+password;
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
            System.out.println("Response UserValidationAsyncTask "+result);

            if (result != null) {
                try
                {
                    JSONObject jObj = new JSONObject(result);

                    if(jObj.getString("user_id").compareToIgnoreCase("0") !=0) {
                        SharedPreferences.Editor edit = pref.edit();
                        edit.clear();
                        edit.putString(Constants.USER_ID, jObj.getString("user_id"));
                        edit.putString(Constants.CUSTOMER_ID, jObj.getString("customer_id"));
                        edit.putString(Constants.STORE_ID, jObj.getString("store_id"));
                        edit.putString(Constants.CUSTOMER_NAME, jObj.getString("customer_name"));
                        edit.putString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME, jObj.getString("customer_stock_data_table_name"));
                        edit.putString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME, jObj.getString("customer_product_data_table_name"));
                        edit.putString(Constants.STORE_NUMBER, jObj.getString("store_number"));
                        edit.putString(Constants.ADDRESS, jObj.getString("address"));

                        edit.commit();

                        Intent intent = new Intent(LoginActivity.this, MediatorActivity.class);
                        startActivity(intent);

                        finish();

                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {

                    // e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                }


            } else {

                Toast.makeText(LoginActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
            }
        }
    }


}