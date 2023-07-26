package com.vsi.smart.dairy1;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class ShowDeductionsActivity extends AppCompatActivity {


    InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_deductions);

        TextView displayname = (TextView) findViewById(R.id.displayname);
        displayname.setText("Deductions");

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


            AdView mAdView = (AdView) findViewById(R.id.adView_dash1221);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);


        }catch (Exception r){

        }


        WebView webView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("http://4234-31282.el-alt.com/Inventory/MobilePages/Deduction.aspx?companyid="+ApplicationRuntimeStorage.COMPANYID+"&userid="+ApplicationRuntimeStorage.USERID);
    }
}
