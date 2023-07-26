package com.vsi.smart.dairy1;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class GridMenu extends AppCompatActivity {
    GridView grid;
    String[] web = {
            "",
            "",
            "",
            "",
            ""
    } ;
    int[] imageId = {
            R.drawable.loginpage,
            R.drawable.dairyspecial,
            com.vsi.smart.dairy1.R.drawable.farmerspecial,
            R.drawable.jobs,
            R.drawable.helth

    };
  //  InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.enableDefaults();
        setContentView(com.vsi.smart.dairy1.R.layout.activity_grid_menu);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);

        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("स्मार्ट डेअरी 1.0");

//        ImageButton imgbutton =(ImageButton)mCustomView. findViewById(R.id.imgbutton);
//        imgbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
////                Intent intent=new Intent(GridMenu.this,GridMenu.class);
////                startActivity(intent);
//            }
//        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        ActivityCompat.requestPermissions(GridMenu.this,new String[]{Manifest.permission.INTERNET}, 1);
        ActivityCompat.requestPermissions(GridMenu.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        if (!isNetworkAvailable())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    GridMenu.this);
            builder.setMessage("Internet Connection Required")
                    .setCancelable(false)
                    .setPositiveButton("Retry",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id)
                                {
                                    // btnsubmitlogin.setVisibility(View.GONE);// Restart the activity
                                    //finish();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }

//      MobileAds.initialize(this, "ca-app-pub-4635245989767728/7160452698");

//            MobileAds.initialize(this, "ca-app-pub-4635245989767728~8551648691");
//
//            mInterstitialAd = new InterstitialAd(this);
//            // mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
//            mInterstitialAd.setAdUnitId("ca-app-pub-4635245989767728/6256418879");
//            mInterstitialAd.loadAd(new AdRequest.Builder().build());
//            mInterstitialAd.setAdListener(new AdListener() {
//                @Override
//                public void onAdLoaded() {
//                    // Code to be executed when an ad finishes loading.
//                    if (mInterstitialAd.isLoaded()) {
//                        mInterstitialAd.show();
//                    }
//                }
//
//                @Override
//                public void onAdFailedToLoad(int errorCode) {
//                    // Code to be executed when an ad request fails.
//                    // Load the next interstitial.
//
//
//                }
//
//                @Override
//                public void onAdOpened() {
//                    // Code to be executed when the ad is displayed.
//                }
//
//                @Override
//                public void onAdLeftApplication() {
//                    // Code to be executed when the user has left the app.
//                }
//
//                @Override
//                public void onAdClosed() {
//                    // Code to be executed when when the interstitial ad is closed.
//                    // Load the next interstitial.
////                try{
////                    Thread.sleep(30000);
////                }catch (Exception ee){}
//
//                }
//            });

//        AdView mAdView = (AdView) findViewById(R.id.adView_grid);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        CustomGrid adapter = new CustomGrid(GridMenu.this, web, imageId);
        grid=(GridView)findViewById(com.vsi.smart.dairy1.R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
              // Toast.makeText(GridMenu.this, "You Clicked at " +web[+ position], Toast.LENGTH_SHORT).show();

                if(position==0){ //smart Dairy 1.0

                    Intent intent=new Intent(GridMenu.this,Login.class);
                    startActivity(intent);
                }
                if(position==1){ //dairy

                    Intent intent=new Intent(GridMenu.this,Chapterlist.class);

                    intent.putExtra("Subcode","0");
                    startActivity(intent);
                }
                if(position==2){ //ksk

                    Intent intent=new Intent(GridMenu.this,Chapterlist.class);
                    intent.putExtra("Subcode","1");
                    startActivity(intent);
                }
                if(position==3){ //job

                    Intent intent=new Intent(GridMenu.this,Chapterlist.class);
                    intent.putExtra("Subcode","2");
                    startActivity(intent);
                }
                if(position==4){ //health

                    Intent intent=new Intent(GridMenu.this,Chapterlist.class);
                    intent.putExtra("Subcode","3");
                    startActivity(intent);
                }
//                if(position==5){ //converter
//
//                    Intent intent=new Intent(GridMenu.this,Converters.class);
//                    startActivity(intent);
//                }
//                if(position==6){ //aboutus
//
//                    Intent intent=new Intent(GridMenu.this,Aboutus.class);
//                    startActivity(intent);
//                }
//                if(position==7){ //bajar bhav
//
//                    Toast.makeText(GridMenu.this, " आपल्या लगतच्या कृषी बाजारपेठांचे बाजार भाव घेउन येत आहोत , लवकरच !!! - स्टार्ट डेअरी टीम.", Toast.LENGTH_SHORT).show();
////                    Intent intent=new Intent(GridMenu.this,Aboutus.class);
////                    startActivity(intent);
//                }
              //  mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
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
    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(GridMenu.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
