package com.vsi.smart.dairy1;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Login extends AppCompatActivity {

    TextView id_devicecode;
    EditText etxuser,etxpassword;
    Switch showpass;
    CheckBox chpass;
    int myperreqloc=99;
    ArrayList<String> accessrightsa = new ArrayList<>();
    Databasehelperlogin mydb;
    ProgressDialog progressDialog;
   // private InterstitialAd mInterstitialAd;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.enableDefaults();
        setContentView(com.vsi.smart.dairy1.R.layout.activity_login);

        mydb = new Databasehelperlogin(this);

        View decorview=getWindow().getDecorView();
        int uioptions=View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorview.setSystemUiVisibility(uioptions);
        ActivityCompat.requestPermissions(Login.this,new String[]{Manifest.permission.READ_PHONE_STATE}, 1);

//        if (!isNetworkAvailable())
//        {
//            AlertDialog.Builder builder = new AlertDialog.Builder(
//                    Login.this);
//            builder.setMessage("Internet Connection Required")
//                    .setCancelable(false)
//                    .setPositiveButton("Retry",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog,int id)
//                                {
//                                   // btnsubmitlogin.setVisibility(View.GONE);// Restart the activity
//                                    finish();
//                                }
//                            });
//            AlertDialog alert = builder.create();
//            alert.show();
//        }
       // MobileAds.initialize(this, "ca-app-pub-4635245989767728/7160452698");
//        MobileAds.initialize(this, "ca-app-pub-4635245989767728~8551648691");
//
//        mInterstitialAd = new InterstitialAd(this);
//       // mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
//        mInterstitialAd.setAdUnitId("ca-app-pub-4635245989767728/6256418879");
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//        mInterstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                // Code to be executed when an ad finishes loading.
//                if (mInterstitialAd.isLoaded()) {
//                    mInterstitialAd.show();
//                }
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                // Code to be executed when an ad request fails.
//                // Load the next interstitial.
//
//
//            }
//
//            @Override
//            public void onAdOpened() {
//                // Code to be executed when the ad is displayed.
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                // Code to be executed when the user has left the app.
//            }
//
//            @Override
//            public void onAdClosed() {
//                // Code to be executed when when the interstitial ad is closed.
//                // Load the next interstitial.
////                try{
////                    Thread.sleep(30000);
////                }catch (Exception ee){}
//
//            }
//        });
//        AdView mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        try{
            id_devicecode = (TextView) findViewById(com.vsi.smart.dairy1.R.id.id_devicecode);
            String androidId = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            if(null != androidId && androidId.length() >= 3) {
                String startStr = androidId.substring(0,3);
                String enStr = androidId.substring(androidId.length()-3);

                id_devicecode.setText((startStr+""+enStr).toUpperCase());
            }

        }catch (Exception r){
            r.printStackTrace();
        }


        etxuser=(EditText) findViewById(com.vsi.smart.dairy1.R.id.etxid_User);
        etxpassword=(EditText) findViewById(com.vsi.smart.dairy1.R.id.etxid_Password);
        chpass=(CheckBox) findViewById(com.vsi.smart.dairy1.R.id.chkrepass);

        try{
            String fcmkey = loginPreferences.getString("FCMKEY", "");
            Toast.makeText(Login.this, " टोकन  >>  - "+fcmkey, Toast.LENGTH_LONG).show();

        }catch (Exception r){
            r.printStackTrace();
        }
        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            etxuser.setText(loginPreferences.getString("username", ""));
            etxpassword.setText(loginPreferences.getString("password", ""));
            chpass.setChecked(true);
        }

        TextView txtuppass=(TextView)findViewById(com.vsi.smart.dairy1.R.id.txtuppass);
        txtuppass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(Login.this,Updatepassword.class);
                startActivity(intent);
            }
        });

        showpass=(Switch) findViewById(com.vsi.smart.dairy1.R.id.togglebtn);
        showpass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if(isChecked)
                {
                    etxpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    etxpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        final Button btnsubmitlogin =(Button) findViewById(com.vsi.smart.dairy1.R.id.btnid_Submitbtn);
        btnsubmitlogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                progressDialog= new ProgressDialog(Login.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        try{
                            Thread.sleep(10000);

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }).start();
                String user = etxuser.getText().toString();
                String pass = etxpassword.getText().toString();
                if (chpass.isChecked()) {
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("username", user);
                    loginPrefsEditor.putString("password", pass);
                    loginPrefsEditor.commit();
                } else {
                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
                }

//                if (!isNetworkAvailable())
//                {
//
//                    boolean res = mydb.isTableExists("Userdetailslogin", true);
//                    if (res == true) {
//                        callWebService();
//                    }
//                    else {
//                        if (!isNetworkAvailable())
//                        {
//                            final AlertDialog.Builder builder = new AlertDialog.Builder(
//                                    Login.this);
//                            builder.setMessage("Sorry Internet Connection Required")
//                                    .setCancelable(true)
//                                    .setPositiveButton("Retry",
//                                            new DialogInterface.OnClickListener() {
//                                                public void onClick(DialogInterface dialog, int id) {
//
//                                                }
//                                            });
//                            AlertDialog alert = builder.create();
//                            alert.show();
//                        }else{
//                            CallSoap cs = new CallSoap();
//                            // String result= cs.AuthenticateUser(user,pass);
//                            new LongOperation_1().execute(user, pass);
//                        }
//                    }
//
//                }else {
                    CallSoap cs = new CallSoap();
                    // String result= cs.AuthenticateUser(user,pass);
                    new LongOperation_1().execute(user, pass);
               // }
            }
        });
    }

    private void callWebService() {
        Cursor res = mydb.getAllData();
        if(res.getCount() == 0) {
            // show message
            Toast.makeText(this, "Sorry Unable to fetch", Toast.LENGTH_SHORT).show();
        } else {
            String username,password;
            //StringBuffer buffer = new StringBuffer();
            if (res.moveToFirst()) {
                do {
                    String _ID = res.getString(res.getColumnIndex("_ID"));
                    ApplicationRuntimeStorage._ID = _ID;
                    username = res.getString(res.getColumnIndex("username"));
                    ApplicationRuntimeStorage.username = username;
                    password = res.getString(res.getColumnIndex("password"));
                    ApplicationRuntimeStorage.password = password;
                    String societyCode = res.getString(res.getColumnIndex("societyCode"));
                    ApplicationRuntimeStorage.societyCode = societyCode;
                    String pcode = res.getString(res.getColumnIndex("pcode"));
                    ApplicationRuntimeStorage.pcode = pcode;
                    String pname = res.getString(res.getColumnIndex("pname"));
                    ApplicationRuntimeStorage.CUST_NAME=pname;
                    String lbtno = res.getString(res.getColumnIndex("lbtno"));
                    ApplicationRuntimeStorage.lbtno = lbtno;
                    String stype = res.getString(res.getColumnIndex("stype"));

                    try {

                        int centerid = res.getInt(res.getColumnIndex("centerid"));
                        ApplicationRuntimeStorage.CENTERID=centerid;
                    }catch (Exception r){


                    }


                    ApplicationRuntimeStorage.stype = stype;
                    String ads = res.getString(res.getColumnIndex("ads"));
                    ApplicationRuntimeStorage.ads = ads;
                    String SocietyName = res.getString(res.getColumnIndex("SocietyName"));
                    ApplicationRuntimeStorage.SocietyName = SocietyName;

                    String accessrigts = res.getString(res.getColumnIndex("accessrigts"));
                    ApplicationRuntimeStorage.AccessRights = accessrigts;
                    String deviceid = res.getString(res.getColumnIndex("deviceid"));
                    ApplicationRuntimeStorage.deviceid = deviceid;
                    String loginid = res.getString(res.getColumnIndex("loginid"));
                    ApplicationRuntimeStorage.loginid = loginid;
                    // add the bookName into the bookTitles ArrayList
                    // move to next row
                } while (res.moveToNext());

                String user = etxuser.getText().toString();
                String pass = etxpassword.getText().toString();
                final List<String> list = Arrays.asList(ApplicationRuntimeStorage.AccessRights.split(","));

                if(user.equals(username) && pass.equals(password)) {
                    if ( list.contains("MILK_PRODUCER")) {
                        Intent intent = new Intent(Login.this, Dashboard_santsha.class);
                        startActivity(intent);
                    }else  if (list.contains("ADMIN")) {

                        Intent intent = new Intent(Login.this, Summary1.class);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(Login.this, Mainoptions.class);
                        startActivity(intent);
                    }
                }else{
                    Toast.makeText(this, "UserName or Password is Invalid!!!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class LongOperation_1 extends AsyncTask<String, Void, String>
    {
        String user,pass;

        @Override
        protected String doInBackground(String... params) {
            CallSoap cs=new CallSoap();
            user = params[0];
            pass = params[1];

            String result= cs.AuthenticateUser(params[0],params[1],id_devicecode.getText().toString());
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            try{
                //hide the dialog
                asyncDialog.dismiss();
            }catch (Exception r){}

            if(null == result || result.contains("Exception")){

                Toast.makeText(Login.this, "Mobile Network Related Issue, Please check internet ", Toast.LENGTH_LONG).show();
                 return;
            }

            String username = null,password= null ,societyCode = null,pcode= null,lbtno= null,stype=null,
                    ads = null,SocietyName = null,
                    pname = null,accessrigts= null,deviceid= null;
            try
            {
                try{
                    //arrayPerson.clear();
                    JSONArray jsonarray = new JSONArray(result);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        //Person p = new Person();
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        username = jsonobject.getString("username");
                        password = jsonobject.getString("password");
                        societyCode = jsonobject.getString("societyCode");
                        pcode = jsonobject.getString("pcode");
                        ApplicationRuntimeStorage.pcode = pcode;
                        String pname1 = jsonobject.getString("pname");
                        ApplicationRuntimeStorage.CUST_NAME=pname1;
                        lbtno = jsonobject.getString("lbtno");
                        stype = jsonobject.getString("stype");
                        try {

                            int centerid = jsonobject.getInt("centerid");
                            ApplicationRuntimeStorage.CENTERID=centerid;
                        }catch (Exception r){


                        }

                        ads = jsonobject.getString("ads");
                        SocietyName = jsonobject.getString("SocietyName");
                        pname = jsonobject.getString("pname");
                        ApplicationRuntimeStorage.pname = pname;
                        deviceid = jsonobject.getString("deviceid");
                        accessrigts = jsonobject.getString("accessrigts");
                        JSONArray jsonarray2 = new JSONArray(accessrigts);
                        for (int j = 0; j < jsonarray2.length(); j++) {
                            //Person p = new Person();
                            JSONObject jsonobject1 = jsonarray2.getJSONObject(j);
                            String name = jsonobject1.getString("name");
                            try{
                                name = name.trim();
                            }catch (Exception r){
                                r.printStackTrace();
                            }
                            accessrightsa.add(name);
                        }
                    }
                    try {
                        ApplicationRuntimeStorage.AccessRights = String.valueOf(accessrightsa);
                        ApplicationRuntimeStorage.AccessRights = ApplicationRuntimeStorage.AccessRights.replace("[", "");
                        ApplicationRuntimeStorage.AccessRights = ApplicationRuntimeStorage.AccessRights.replace("]", "");
                    }catch (Exception r){
                        r.printStackTrace();
                    }
                    if(stype.equals("")){stype="0";}if(ads.equals("")){ads="0";}if(lbtno.equals("")){lbtno="0";}
                    boolean res = mydb.isTableExists("Userdetailslogin", true);
                    final List<String> list = Arrays.asList(ApplicationRuntimeStorage.AccessRights.split(","));

                    if (res == true) {
                        boolean isUpdated = false;
                        try {
                            isUpdated = mydb.updateData("1",username, password, societyCode, pcode, lbtno, stype, ads,
                                    SocietyName, pname, "" + accessrightsa, deviceid, "0");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (isUpdated == true) {
                            Toast.makeText(Login.this, "Data Updated", Toast.LENGTH_LONG).show();
                            if (list.contains("MILK_PRODUCER")) {
                                Intent intent = new Intent(Login.this, Dashboard_santsha.class);
                                startActivity(intent);
                            }else if (list.contains("ADMIN")) {
                                Intent intent = new Intent(Login.this, Summary1.class);
                                startActivity(intent);
                            }else{
                                Intent intent = new Intent(Login.this, Mainoptions.class);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(Login.this, "Data not Saved", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        boolean isInserted = false;
                        try {
                            isInserted = mydb.insertData(username, password, societyCode, pcode, lbtno, stype, ads,
                                    SocietyName, pname, "" + accessrightsa, deviceid, "0");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (isInserted == true) {
                            Toast.makeText(Login.this, "Data Saved", Toast.LENGTH_LONG).show();
                            if (true || list.contains("MILK_PRODUCER")) {
                                Intent intent = new Intent(Login.this, Dashboard_santsha.class);
                                startActivity(intent);
                            }else if (list.contains("ADMIN")) {
                                Intent intent = new Intent(Login.this, Summary1.class);
                                startActivity(intent);
                            }else{
                                Intent intent = new Intent(Login.this, Mainoptions.class);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(Login.this, "Data not Saved", Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Login.this, result, Toast.LENGTH_LONG).show();

                }

                ApplicationRuntimeStorage.ORG_NAME=SocietyName;
                ApplicationRuntimeStorage.ORG_ADS=Long.parseLong(ads);
                ApplicationRuntimeStorage.CUST_NAME=pname;
                ApplicationRuntimeStorage.COMPANYID = Long.parseLong(societyCode);
                ApplicationRuntimeStorage.ORG_TYPE=Long.parseLong(stype);
                ApplicationRuntimeStorage.PASSWORD = pass;
                ApplicationRuntimeStorage.USERID = user;


                int result11=0;
                if(result11 == 1)
                {

                }else{
                    final List<String> list = Arrays.asList(ApplicationRuntimeStorage.AccessRights.split(","));
                    if (list.contains("MILK_PRODUCER")) {
                        Intent intent = new Intent(Login.this, Dashboard_santsha.class);
                        startActivity(intent);
                    }else if (list.contains("ADMIN")) {
                        Intent intent = new Intent(Login.this, Summary1.class);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(Login.this, Mainoptions.class);
                        startActivity(intent);
                    }
                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }


            super.onPostExecute(result);
        }
        ProgressDialog asyncDialog = new ProgressDialog(Login.this);
        @Override
        protected void onPreExecute() {
            //set message of the dialog
            asyncDialog.setMessage("Authenticating...");
            //show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
    public boolean isNetworkAvailable()
    {

        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;

    }

    }
