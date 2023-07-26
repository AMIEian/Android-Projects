package com.vsi.smart.dairy1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONObject;

import Decoder.BASE64Decoder;

public class Chapterdetails extends AppCompatActivity {
    ImageView imgnot;
    long imageid=0;
    long typeid=0;
    Drawable drawable;
    Bitmap bitmap;
    String ImagePath;
    Uri URI;
    long companyid=1;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_chapterdetails);

        imgnot = (ImageView) findViewById(com.vsi.smart.dairy1.R.id.imgnot);

        try {
            Bundle bundle = getIntent().getExtras();
            imageid = Long.parseLong(bundle.getString("ImageId"));
            typeid = Long.parseLong(bundle.getString("typeid"));
            getIntent().putExtra("ImageId", "");
            getIntent().putExtra("typeid", "");
        } catch (Exception er) {
            imageid = 0;
        }


        try{

            MobileAds.initialize(this, "ca-app-pub-4635245989767728~1737887578");

            //  MobileAds.initialize(this, "ca-app-pub-4635245989767728~8551648691");
            MobileAds.initialize(this, "ca-app-pub-4635245989767728~1737887578");
//
//            mInterstitialAd = new InterstitialAd(this);
//            // mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
//            //mInterstitialAd.setAdUnitId("ca-app-pub-4635245989767728/6256418879");
//            mInterstitialAd.setAdUnitId("ca-app-pub-4635245989767728/8934044839");

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


            AdView mAdView = (AdView) findViewById(R.id.adView_chapterdetails);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);


        }catch (Exception r){

        }

        CallSoap cs=new CallSoap();
        String JSON_RESULT= cs.GetSubjectTopicDetails(companyid,imageid,typeid); // Web Call to populate JSON ItemList
        try{
            JSONArray jsonarray = new JSONArray(JSON_RESULT);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String  title = jsonobject.getString("title");
                String  sdetail= jsonobject.getString("sdetail");
                String  fdetail= jsonobject.getString("fdetail");
                String  ImageData= jsonobject.getString("ImageData");

                byte[] imageByte;

                BASE64Decoder decoder = new BASE64Decoder();
                imageByte = decoder.decodeBuffer(ImageData);
                Bitmap decodebyte= BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);

                ImageView imgnot1=(ImageView) findViewById(com.vsi.smart.dairy1.R.id.imgnot);
                imgnot1.setImageBitmap(decodebyte);

                TextView nottitle1 = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtnottittle);
                nottitle1.setText(title);

                TextView notsdetail1 = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtnotsdetail);
                notsdetail1.setText(sdetail);

                TextView notfdetail1 = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtnotfdetail);
                notfdetail1.setText(fdetail);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        imgnot.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                drawable = getResources().getDrawable(com.vsi.smart.dairy1.R.drawable.logo11);
                bitmap = ((BitmapDrawable)imgnot.getDrawable()).getBitmap();
                ImagePath = MediaStore.Images.Media.insertImage(
                        getContentResolver(),
                        bitmap,
                        "demo_image",
                        "demo_image"
                );
                URI = Uri.parse(ImagePath);
                Toast.makeText(Chapterdetails.this, "Image Saved Successfully", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Chapterdetails.this,Login.class);
            startActivity(intent);
        }else{ }
        super.onResume();
    }
}
