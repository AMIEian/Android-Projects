package sysnotion.tagid.tagsmart;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;



public class ForgotPasswordActivity extends AppCompatActivity {
    private Button fpBtn;
    private EditText editTextTextEmailAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        editTextTextEmailAddress =(EditText) findViewById(R.id.editTextTextEmailAddress);
        fpBtn= (Button)findViewById(R.id.fpBtn);
        fpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextTextEmailAddress.getText().length() >0 ) {

                    if(Constants.isNetworkAvailable(ForgotPasswordActivity.this)) {
                        new ForgetPasswordAsyncTask(ForgotPasswordActivity.this).execute(Constants.BASE_URL + "ForgotPassword.php");
                    }
                    else
                    {
                        Constants.internetAlert(ForgotPasswordActivity.this);
                    }

                }
                else
                {

                    Toast.makeText(ForgotPasswordActivity.this,"Please enter credentials", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    class ForgetPasswordAsyncTask extends AsyncTask<String, String, String> {


        private ProgressDialog dialog;

        public ForgetPasswordAsyncTask(Activity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog.setMessage("Processing E-Mail...");

            dialog.show();
        }

        @Override
        protected String doInBackground(String... string) {

            String response = null;
            try {
                String username = editTextTextEmailAddress.getText().toString();
                URL url = new URL(string[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(7000);
                conn.setConnectTimeout(7000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("X-Environment", "android");

                String urlParameters = "user_email=" + username ;
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

            if (result != null) {
                try
                {
                    JSONObject jObj = new JSONObject(result);

                    if(jObj.getString("status").compareToIgnoreCase("1") ==0) {

                        Toast.makeText(ForgotPasswordActivity.this, "Please check your email. Password is sent on your e-mail address.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);

                        finish();

                    }
                    else
                    {
                        Toast.makeText(ForgotPasswordActivity.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {

                    // e.printStackTrace();
                    Toast.makeText(ForgotPasswordActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                }


            } else {

                Toast.makeText(ForgotPasswordActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
            }
        }
    }

}