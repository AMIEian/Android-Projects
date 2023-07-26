package com.vsi.smart.dairy1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONObject;

public class NewsList extends AppCompatActivity {
    private Context mContext;
    InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_notificationlist);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);

        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("News List");

        ImageButton imgbutton =(ImageButton)mCustomView. findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(NewsList.this,GridMenu.class);
                startActivity(intent);            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        // Get the application context
        mContext = getApplicationContext();



        // Get the widgets reference from XML layout
        LinearLayout mLinearLayout = (LinearLayout) findViewById(com.vsi.smart.dairy1.R.id.id_lin_layout1);

        CallSoap cs=new CallSoap();
        String JSON_RESULT= cs.GetNewsList(ApplicationRuntimeStorage.COMPANYID); // Web Call to populate JSON ItemList
        try{






          // MobileAds.initialize(this, "ca-app-pub-4635245989767728/7160452698");
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
//            AdView mAdView = (AdView) findViewById(R.id.adView_opt3);
//            AdRequest adRequest = new AdRequest.Builder().build();
//            mAdView.loadAd(adRequest);



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
                // Set cardView content padding
//                card.setContentPadding(15, 15, 15, 15);

                // Set a background color for CardView
//                card.setCardBackgroundColor(Color.parseColor("#FFC6D6C3"));

                // Initialize a new CardView
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

                        Intent intent=new Intent(NewsList.this,NewsDetails.class);
                        intent.putExtra("ImageId",imageid);
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
            Intent intent=new Intent(NewsList.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
