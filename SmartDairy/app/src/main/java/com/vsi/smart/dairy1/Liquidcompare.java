package com.vsi.smart.dairy1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class Liquidcompare extends AppCompatActivity {
    EditText etxvalue;
    TextView txtvalue;
    Spinner spnfrom,spnto;
    Button convert;
    String from,to;
    InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_liquidcompare);
        spnfrom=(Spinner)findViewById(com.vsi.smart.dairy1.R.id.spncompare);
        spnto=(Spinner)findViewById(com.vsi.smart.dairy1.R.id.spncompareto);

        etxvalue=(EditText)findViewById(com.vsi.smart.dairy1.R.id.editvalue);
        txtvalue=(TextView)findViewById(com.vsi.smart.dairy1.R.id.txtvalue);

        convert=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnconvert);

        MobileAds.initialize(this, "ca-app-pub-4635245989767728/7160452698");
        if(ApplicationRuntimeStorage.ORG_ADS==0) {
            MobileAds.initialize(this, "ca-app-pub-4635245989767728~8551648691");

            mInterstitialAd = new InterstitialAd(this);
            // mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
            mInterstitialAd.setAdUnitId("ca-app-pub-4635245989767728/6256418879");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                    // Load the next interstitial.


                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when the ad is displayed.
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when when the interstitial ad is closed.
                    // Load the next interstitial.
//                try{
//                    Thread.sleep(30000);
//                }catch (Exception ee){}

                }
            });
        }
        AdView mAdView = (AdView) findViewById(com.vsi.smart.dairy1.R.id.adView_liquadc);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        spnfrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(position==0)
                {
                    from="Liter";
                }
                else
                {
                    Toast.makeText(Liquidcompare.this, ""+position, Toast.LENGTH_SHORT).show();
                    from="Milliliter";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    to="Liter";
                }
                else
                {
                    to="Milliliter";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        convert.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(from==to)
                {
                    String value=etxvalue.getText().toString();
                    txtvalue.setText(value);
                }
                else if("Liter"==from && "Milliliter"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint*1000;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Milliliter"==from &&"Liter"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint/1000;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
            }
        });
    }
    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Liquidcompare.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
