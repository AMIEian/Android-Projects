package com.vsi.smart.dairy1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONObject;

public class Chapterlist extends AppCompatActivity {
    private Context mContext;
    String data, searchstring;
    long typeid,companyid=1,pageno=1;
    TextView etxsearch;
    ImageButton imgBtn;
    private InterstitialAd mInterstitialAd;
    Button btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9,btn10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_chapterlist);

        btn1=(Button)findViewById(com.vsi.smart.dairy1.R.id.btn1);
        btn2=(Button)findViewById(com.vsi.smart.dairy1.R.id.btn2);
        btn3=(Button)findViewById(com.vsi.smart.dairy1.R.id.btn3);
        btn4=(Button)findViewById(com.vsi.smart.dairy1.R.id.btn4);
        btn5=(Button)findViewById(com.vsi.smart.dairy1.R.id.btn5);
        btn6=(Button)findViewById(com.vsi.smart.dairy1.R.id.btn6);
        btn7=(Button)findViewById(com.vsi.smart.dairy1.R.id.btn7);
        btn8=(Button)findViewById(com.vsi.smart.dairy1.R.id.btn8);
        btn9=(Button)findViewById(com.vsi.smart.dairy1.R.id.btn9);
        btn10=(Button)findViewById(com.vsi.smart.dairy1.R.id.btn10);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                pageno=1;webcall(); btn1.setBackgroundColor(Color.RED); mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                pageno=2;webcall(); btn2.setBackgroundColor(Color.RED); mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                pageno=3;webcall(); btn3.setBackgroundColor(Color.RED); mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                pageno=4;webcall(); btn4.setBackgroundColor(Color.RED); mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                pageno=5;webcall(); btn5.setBackgroundColor(Color.RED);
                if(ApplicationRuntimeStorage.ORG_ADS==0) {
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                pageno=6;webcall(); btn6.setBackgroundColor(Color.RED);
                if(ApplicationRuntimeStorage.ORG_ADS==0) {
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }
            }
        });
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                pageno=7;webcall(); btn7.setBackgroundColor(Color.RED);
                if(ApplicationRuntimeStorage.ORG_ADS==0) {
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }
            }
        });
        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                pageno=8;webcall(); btn8.setBackgroundColor(Color.RED);
                if(ApplicationRuntimeStorage.ORG_ADS==0) {
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }
            }
        });
        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                pageno=9;webcall(); btn9.setBackgroundColor(Color.RED);
                if(ApplicationRuntimeStorage.ORG_ADS==0) {
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }
            }
        });
        btn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                pageno=10;webcall(); btn10.setBackgroundColor(Color.RED);
                if(ApplicationRuntimeStorage.ORG_ADS==0) {
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }
            }
        });

        try{

            MobileAds.initialize(this, "ca-app-pub-4635245989767728~1737887578");

            //  MobileAds.initialize(this, "ca-app-pub-4635245989767728~8551648691");
            MobileAds.initialize(this, "ca-app-pub-4635245989767728~1737887578");

            mInterstitialAd = new InterstitialAd(this);
            // mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
            //mInterstitialAd.setAdUnitId("ca-app-pub-4635245989767728/6256418879");
            mInterstitialAd.setAdUnitId("ca-app-pub-4635245989767728/8934044839");

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


            AdView mAdView = (AdView) findViewById(R.id.adView_chapterlist);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);


        }catch (Exception r){

        }

        etxsearch=(TextView)findViewById(com.vsi.smart.dairy1.R.id.etxsearch);
        imgBtn=(ImageButton)findViewById(com.vsi.smart.dairy1.R.id.btn_imgsearch);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                pageno=1;webcall(); btn1.setBackgroundColor(Color.RED);
            }
        });
        Intent intent=getIntent();
        if(null !=intent)
        {
            data=intent.getStringExtra("Subcode");
        }
        typeid=Long.parseLong(data);

        mContext = getApplicationContext();
        pageno = 1;
        webcall();
        btn1.setBackgroundColor(Color.RED);
    }

    private void resetAllbtnColor(){
        int primarycolor= Color.TRANSPARENT;
        btn1.setBackgroundColor(primarycolor);
        btn2.setBackgroundColor(primarycolor);
        btn3.setBackgroundColor(primarycolor);
        btn4.setBackgroundColor(primarycolor);
        btn5.setBackgroundColor(primarycolor);
        btn6.setBackgroundColor(primarycolor);
        btn7.setBackgroundColor(primarycolor);
        btn8.setBackgroundColor(primarycolor);
        btn9.setBackgroundColor(primarycolor);
        btn10.setBackgroundColor(primarycolor);
    }

    private void webcall(){
        resetAllbtnColor();
        // Get the widgets reference from XML layout
        LinearLayout mLinearLayout = (LinearLayout) findViewById(com.vsi.smart.dairy1.R.id.id_lin_layout1);

        mLinearLayout.removeAllViews();
        mLinearLayout.invalidate();

        searchstring=etxsearch.getText().toString();





        CallSoap cs=new CallSoap();
        String JSON_RESULT= cs.GetSubjectTopicList(companyid,typeid,searchstring,pageno); // Web Call to populate JSON ItemList
        try{
            JSONArray jsonarray = new JSONArray(JSON_RESULT);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String  title = jsonobject.getString("title");
                String  sdetail= jsonobject.getString("sdetail");
                //String  fdetail= jsonobject.getString("fdetail");
                final String  imageid= jsonobject.getString("ImageId");

                // Initialize a new CardView
                CardView card = new CardView(mContext);

                // Set the CardView layoutParams
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT

                );
                params.setMargins(10,20,10,0);
                card.setLayoutParams(params);

                // Set CardView corner radius
                card.setRadius(12);
                LinearLayout lay = new LinearLayout(mContext);

                // Set the CardView layoutParams
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params1.setMargins(10,10,10,10);
                lay.setLayoutParams(params1);
                lay.setOrientation(LinearLayout.VERTICAL);

                // Initialize a new TextView to put in CardView
                TextView tv = new TextView(mContext);
                tv.setLayoutParams(params1);
                tv.setText(title);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                tv.setTextColor(Color.BLACK);
                // Put the TextView in CardView
                lay.addView(tv);

                // Initialize a new TextView to put in CardView
                TextView tv1 = new TextView(mContext);
                tv1.setLayoutParams(params1);
                tv1.setText(sdetail);
                tv1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                tv1.setTextColor(Color.GRAY);
                // Put the TextView in CardView
                lay.addView(tv1);

                card.addView(lay);
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent=new Intent(Chapterlist.this,Chapterdetails.class);
                        intent.putExtra("ImageId",imageid);
                        intent.putExtra("typeid", ""+typeid);
                        startActivity(intent);
                    }
                });


                mLinearLayout.addView(card);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Chapterlist.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
