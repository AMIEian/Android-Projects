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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import  com.vsi.smart.dairy1.R;
public class Distancecompare extends AppCompatActivity {
    InterstitialAd mInterstitialAd;
    EditText etxvalue;
    TextView txtvalue;
    Spinner spnfrom,spnto;
    Button convert;
    String from,to;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distancecompare);
        spnfrom=(Spinner)findViewById(R.id.spncompare);
        spnto=(Spinner)findViewById(R.id.spncompareto);

        etxvalue=(EditText)findViewById(R.id.editvalue);
        txtvalue=(TextView)findViewById(R.id.txtvalue);

        convert=(Button)findViewById(R.id.btnconvert);


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
        AdView mAdView = (AdView) findViewById(R.id.adView_distancec);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        spnfrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(position==0)
                {
                    from="Kilometer";
                }
                else if(position==1)
                {
                    from="Meter";
                }
                else if(position==2)
                {
                    from="Centimeters";
                }
                else if(position==3)
                {
                    from="Millimeters";
                }
                else if(position==4)
                {
                    from="Feet";
                }
                else if(position==5)
                {
                    from="Inches";
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
                    to="Kilometer";
                }
                else if(position==1)
                {
                    to="Meter";
                }
                else if(position==2)
                {
                    to="Centimeters";
                }
                else if(position==3)
                {
                    to="Millimeters";
                }
                else if(position==4)
                {
                    to="Feet";
                }
                else if(position==5)
                {
                    to="Inches";
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
                else if("Kilometer"==from && "Meter"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint*1000;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Kilometer"==from &&"Centimeter"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint*100000;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Kilometer"==from &&"Millimeter"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint*1000000;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Kilometer"==from &&"Feet"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint*3280;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Kilometer"==from &&"Inches"==to)
                {
                    String value=etxvalue.getText().toString();
                    int valueint=Integer.parseInt(value);
                    double result=valueint*39370;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Meter"==from && "Kilometer"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint/1000;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Meter"==from && "Centimeter"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint*100;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Meter"==from && "Millimeter"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint*1000;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Meter"==from && "Feet"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint*3;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Meter"==from && "Inches"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint*39;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Centimeter"==from && "Kilometer"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint/100000;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Centimeter"==from && "Meter"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint/100;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Centimeter"==from && "Millimeter"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint*10;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Centimeter"==from && "Feet"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result= valueint*0.394;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Centimeter"==from && "Inches"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint*0.393;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Millimeter"==from && "Kilometer"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint/1000000;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Millimeter"==from && "Meter"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint/100;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Millimeter"==from && "Centimeter"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint/10;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Millimeter"==from && "Feet"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint*0.0032;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Millimeter"==from && "Inches"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint*0.0394;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Feet"==from && "Kilometer"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint/3280;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Feet"==from && "Meter"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint/3;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Feet"==from && "Centimeter"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint/0.394;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Feet"==from && "Millimeter"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint/0.0032;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Feet"==from && "Inches"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint*12;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Inches"==from && "Kilometer"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint/39370;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Inches"==from && "Meter"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint/39;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Inches"==from && "Millimeter"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint/0.0394;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Inches"==from && "Centimeter"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint/0.393;
                    String resultstring=String.valueOf(result);
                    txtvalue.setText(resultstring);
                }
                else if("Inches"==from && "Feet"==to)
                {
                    String value=etxvalue.getText().toString();
                    double valueint=Double.parseDouble(value);
                    double result=valueint/12;
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
            Intent intent=new Intent(Distancecompare.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
