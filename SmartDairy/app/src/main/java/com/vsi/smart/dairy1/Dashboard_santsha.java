package com.vsi.smart.dairy1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Dashboard_santsha extends AppCompatActivity {
    int primarycolor;
    int buttonState = 0;
    TextView litercow,fatcow,snfcow,ratecow,literbuff,fatbuff,snfbuff,ratebuff,amtcow,amtbuff;
    Calendar c = Calendar.getInstance();
    public final int mYear = c.get(Calendar.YEAR);
    public  final int mMonth = c.get(Calendar.MONTH);
    public final  int mDay = c.get(Calendar.DAY_OF_MONTH);
    public int i;
    public  int pday;
    public int pmonth;
    public int pyear;
    int flagper,flagcomp,btncompare,result;
    BarChart chart6;
    InterstitialAd mInterstitialAd;
    Button btnall,btnmorning,btnevening,btnpersonal,btncompany;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_dashboard_santsha);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Assessment");

        TextView idorgname=(TextView)findViewById(com.vsi.smart.dairy1.R.id.id_orgname);
        idorgname.setText(ApplicationRuntimeStorage.ORG_NAME);
        TextView idcustname=(TextView)findViewById(com.vsi.smart.dairy1.R.id.id_custname);
        idcustname.setText(ApplicationRuntimeStorage.CUST_NAME);


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


            AdView mAdView = (AdView) findViewById(R.id.adView_dash1);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);


        }catch (Exception r){

        }
        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Dashboard_santsha.this,Login.class);
                startActivity(intent);            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        litercow=(TextView)findViewById(com.vsi.smart.dairy1.R.id.cowliter);
        fatcow=(TextView)findViewById(com.vsi.smart.dairy1.R.id.cowfat);
        snfcow=(TextView)findViewById(com.vsi.smart.dairy1.R.id.cowsnf);
        ratecow=(TextView)findViewById(com.vsi.smart.dairy1.R.id.cowrate);
        amtcow=(TextView)findViewById(com.vsi.smart.dairy1.R.id.cowamt);

        literbuff=(TextView)findViewById(com.vsi.smart.dairy1.R.id.buffliter);
        fatbuff=(TextView)findViewById(com.vsi.smart.dairy1.R.id.bufffat);
        snfbuff=(TextView)findViewById(com.vsi.smart.dairy1.R.id.buffsnf);
        ratebuff=(TextView)findViewById(com.vsi.smart.dairy1.R.id.buffrate);
        amtbuff=(TextView)findViewById(com.vsi.smart.dairy1.R.id.buffamt);

        final TextView date=(TextView)findViewById(com.vsi.smart.dairy1.R.id.date);
        ImageButton datebtn=(ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate);
        btnpersonal=(Button) findViewById(com.vsi.smart.dairy1.R.id.btnpersonal);
        btncompany=(Button) findViewById(com.vsi.smart.dairy1.R.id.btncompany);
        btnall=(Button) findViewById(com.vsi.smart.dairy1.R.id.btnall);
        btnmorning=(Button) findViewById(com.vsi.smart.dairy1.R.id.btnmorning);
        btnevening=(Button) findViewById(com.vsi.smart.dairy1.R.id.btnevening);
        primarycolor= Color.parseColor("#ff14b590");
        result=1;
        if(ApplicationRuntimeStorage.AccessRights.length() > 0) {
            String[] rulesArr =ApplicationRuntimeStorage.AccessRights.split(",");
            for (String rule:rulesArr) {
                if("RULE_DASHBOARD_SUMMARY".equalsIgnoreCase(rule))
                {
                    flagcomp=1;

                }

                if("RULE_DASHBOARD".equalsIgnoreCase(rule))
                {
                    flagper=1;


                }
               // chart6.setVisibility(View.INVISIBLE);
                if(flagcomp==1 && flagper==1)
                {
                    btncompany.setVisibility(View.VISIBLE);
                    btnpersonal.setVisibility(View.VISIBLE);

                    if(ApplicationRuntimeStorage.ORG_TYPE==0){
                      //  chart6.setVisibility(View.VISIBLE);
                    }


                    btncompany.setBackgroundColor(Color.GREEN); //default selected
                    result=0;
                }

            }
        }
        btnall.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                buttonState = 0;
                refreshData();
                btnall.setBackgroundColor(Color.GREEN);
                btnmorning.setBackgroundColor(primarycolor);
                btnevening.setBackgroundColor(primarycolor);
            }
        });

        btncompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                btnpersonal.setBackgroundColor(primarycolor);
                btncompany.setBackgroundColor(Color.GREEN);
                btncompare=0;
                result=0;
               // chart6.setVisibility(View.VISIBLE);
                getWebData(date.getText().toString());
//                            0 for company data

            }
        });
        btnpersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnpersonal.setBackgroundColor(Color.GREEN);
                btncompany.setBackgroundColor(primarycolor);
                //chart6.setVisibility(View.INVISIBLE);
                btncompare=1;
                result=1;
                getWebData_new(date.getText().toString());


//                            1 for personal data

            }
        });

        btnmorning.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                buttonState = 1;
                refreshData();
                btnall.setBackgroundColor(primarycolor);
                btnmorning.setBackgroundColor(Color.GREEN);
                btnevening.setBackgroundColor(primarycolor);
            }
        });

        btnevening.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                buttonState = 2;
                refreshData();
                btnall.setBackgroundColor(primarycolor);
                btnmorning.setBackgroundColor(primarycolor);
                btnevening.setBackgroundColor(Color.GREEN);
            }
        });

        SimpleDateFormat df1=new SimpleDateFormat("dd/MM/yyyy");
        String currdate="";
        Calendar c= Calendar.getInstance();
        currdate=df1.format(c.getTime());
        date.setText(currdate);

        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Dashboard_santsha.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth)
                            {
                                date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                pyear=year;
                                pmonth=monthOfYear;
                                pday=dayOfMonth;
                                SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
                                Date date1 = new Date(year-1900, (monthOfYear), dayOfMonth);
                                String currdate="";
                                currdate=df.format(date1.getTime());
                                date.setText(currdate);

//                                Toast.makeText(Birthday.this,pyear+ "-"+pmonth+"-"+pday, Toast.LENGTH_SHORT).show();


                                if(result==0 && btncompare==0) {
                                    getWebData(date.getText().toString());
                                }else{
                                    getWebData_new(date.getText().toString());

                                }


                            }
                        },mYear,mMonth,mDay);

                datePickerDialog.show();

            }
        });
        btnall.setBackgroundColor(Color.GREEN); //default selected
        if(result==0 && btncompare==0) {
            getWebData(date.getText().toString());
        }else{
            getWebData_new(date.getText().toString());

        }
        refreshData();

        Button menu2=(Button) findViewById(com.vsi.smart.dairy1.R.id.btn_menu2);
        //click on Order placed button
        menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ApplicationRuntimeStorage.ORG_TYPE = 1;
                Intent intent=new Intent(Dashboard_santsha.this,SansthaMilkData.class);
                startActivity(intent);
            }
        });
        Button menu4=(Button) findViewById(com.vsi.smart.dairy1.R.id.btn_menu4);
        //click on Order placed button
        menu4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ApplicationRuntimeStorage.ORG_TYPE = 1;
                Intent intent=new Intent(Dashboard_santsha.this,Paymentreport.class);
                startActivity(intent);
            }
        });

        Button menu5=(Button) findViewById(com.vsi.smart.dairy1.R.id.btn_menu5);
        //click on Order placed button
        menu5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ApplicationRuntimeStorage.ORG_TYPE = 1;
                Intent intent=new Intent(Dashboard_santsha.this,ShowPashukhadyaActivity.class);
                startActivity(intent);
            }
        });

        Button menu221=(Button) findViewById(com.vsi.smart.dairy1.R.id.btn_menu221);
        //click on Order placed button
        menu221.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ApplicationRuntimeStorage.ORG_TYPE = 1;
                Intent intent=new Intent(Dashboard_santsha.this,ShowDeductionsActivity.class);
                startActivity(intent);
            }
        });



        Button menu421=(Button) findViewById(com.vsi.smart.dairy1.R.id.btn_menu421);
        //click on Order placed button
        menu421.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ApplicationRuntimeStorage.ORG_TYPE = 1;
                Intent intent=new Intent(Dashboard_santsha.this,CowManagementActivity.class);
                startActivity(intent);
            }
        });

        Button menu521=(Button) findViewById(com.vsi.smart.dairy1.R.id.btn_menu521);
        //click on Order placed button
        menu521.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ApplicationRuntimeStorage.ORG_TYPE = 1;
                Intent intent=new Intent(Dashboard_santsha.this,GridMenu.class);
                startActivity(intent);
            }
        });
    }



    private void getWebData(String datestr){
        try{
            CallSoap cs = new CallSoap();
            String JSON_RESULT = cs.GetMilkData(ApplicationRuntimeStorage.COMPANYID,datestr,result,ApplicationRuntimeStorage.USERID );
            try{

                JSONObject jsonobject = new JSONObject(JSON_RESULT);

                MilkData.sangh_all = (float)jsonobject.getDouble("sangh_all");
                MilkData.adp_all = (float)jsonobject.getDouble("adp_all");
                MilkData.parisar_all = (float)jsonobject.getDouble("parisar_all");
                MilkData.cow_good_all = (float)jsonobject.getDouble("cow_good_all");
                MilkData.cow_low_all = (float)jsonobject.getDouble("cow_low_all");
                MilkData.cow_nash_all = (float)jsonobject.getDouble("cow_nash_all");
                MilkData.cow_japt_all = (float)jsonobject.getDouble("cow_japt_all");
                MilkData.buff_good_all = (float)jsonobject.getDouble("buff_good_all");
                MilkData.buff_low_all = (float)jsonobject.getDouble("buff_low_all");
                MilkData.buff_nash_all = (float)jsonobject.getDouble("buff_nash_all");
                MilkData.buff_japt_all = (float)jsonobject.getDouble("buff_japt_all");
                MilkData.fat_all = (float)jsonobject.getDouble("fat_all");
                MilkData.snf_all = (float)jsonobject.getDouble("snf_all");
                MilkData.pro_all = (float)jsonobject.getDouble("pro_all");


                MilkData.sangh_mor = (float)jsonobject.getDouble("sangh_mor");
                MilkData.adp_mor = (float)jsonobject.getDouble("adp_mor");
                MilkData.parisar_mor = (float)jsonobject.getDouble("parisar_mor");
                MilkData.cow_good_mor = (float)jsonobject.getDouble("cow_good_mor");
                MilkData.cow_low_mor = (float)jsonobject.getDouble("cow_low_mor");
                MilkData.cow_nash_mor = (float)jsonobject.getDouble("cow_nash_mor");
                MilkData.cow_japt_mor = (float)jsonobject.getDouble("cow_japt_mor");
                MilkData.buff_good_mor = (float)jsonobject.getDouble("buff_good_mor");
                MilkData.buff_low_mor = (float)jsonobject.getDouble("buff_low_mor");
                MilkData.buff_nash_mor = (float)jsonobject.getDouble("buff_nash_mor");
                MilkData.buff_japt_mor = (float)jsonobject.getDouble("buff_japt_mor");
                MilkData.fat_mor = (float)jsonobject.getDouble("fat_mor");
                MilkData.snf_mor = (float)jsonobject.getDouble("snf_mor");
                MilkData.pro_mor = (float)jsonobject.getDouble("pro_mor");

                MilkData.sangh_eve = (float)jsonobject.getDouble("sangh_eve");
                MilkData.adp_eve = (float)jsonobject.getDouble("adp_eve");
                MilkData.parisar_eve = (float)jsonobject.getDouble("parisar_eve");
                MilkData.cow_good_eve = (float)jsonobject.getDouble("cow_good_eve");
                MilkData.cow_low_eve = (float)jsonobject.getDouble("cow_low_eve");
                MilkData.cow_nash_eve = (float)jsonobject.getDouble("cow_nash_eve");
                MilkData.cow_japt_eve = (float)jsonobject.getDouble("cow_japt_eve");
                MilkData.buff_good_eve = (float)jsonobject.getDouble("buff_good_eve");
                MilkData.buff_low_eve = (float)jsonobject.getDouble("buff_low_eve");
                MilkData.buff_nash_eve = (float)jsonobject.getDouble("buff_nash_eve");
                MilkData.buff_japt_eve = (float)jsonobject.getDouble("buff_japt_eve");
                MilkData.fat_eve = (float)jsonobject.getDouble("fat_eve");
                MilkData.snf_eve = (float)jsonobject.getDouble("snf_eve");
                MilkData.pro_eve = (float)jsonobject.getDouble("pro_eve");



            }catch (Exception e) {
                //Toast.makeText(this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }catch (Exception rr){
            //Toast.makeText(this,""+rr.getMessage(), Toast.LENGTH_SHORT).show();

        }
        refreshData();
    }



    private void getWebData_new(String datestr){
        try{
            CallSoap cs = new CallSoap();
            String JSON_RESULT = cs.GetMilkCollectionCustomerData(ApplicationRuntimeStorage.COMPANYID,datestr,ApplicationRuntimeStorage.pcode );
            try{

                JSONObject jsonobject = new JSONObject(JSON_RESULT);

                MilkData.sangh_all =(float)jsonobject.getDouble("allcowamount");   //cow total amt
                MilkData.adp_all = (float)jsonobject.getDouble("allbuffamount");     //buff total amt
                MilkData.parisar_all =0;// (float)jsonobject.getDouble("parisar_all");
                MilkData.cow_good_all = (float)jsonobject.getDouble("allcowliter");// cow total liter
                MilkData.cow_low_all = (float)jsonobject.getDouble("allcowfat");// cow total fat
                MilkData.cow_nash_all = (float)jsonobject.getDouble("allcowsnf");// cow total snf
                MilkData.cow_japt_all =(float)jsonobject.getDouble("allcowerate");// cow total rate
                MilkData.buff_good_all = (float)jsonobject.getDouble("allbuffliter");// buff total liter
                MilkData.buff_low_all = (float)jsonobject.getDouble("allbufffat");// buff total fat
                MilkData.buff_nash_all = (float)jsonobject.getDouble("allbuffsnf");// buff total snf
                MilkData.buff_japt_all = (float)jsonobject.getDouble("allbufferate");// buff total rate
                MilkData.fat_all = 0;//(float)jsonobject.getDouble("fat_all");
                MilkData.snf_all = 0;//(float)jsonobject.getDouble("snf_all");
                MilkData.pro_all = 0;//(float)jsonobject.getDouble("pro_all");


                MilkData.sangh_mor = (float)jsonobject.getDouble("morcowamount");
                MilkData.adp_mor = (float)jsonobject.getDouble("morbuffamount");
                MilkData.parisar_mor = 0;//(float)jsonobject.getDouble("parisar_mor");
                MilkData.cow_good_mor = (float)jsonobject.getDouble("morcowliter");//cow mor ltr
                MilkData.cow_low_mor = (float)jsonobject.getDouble("morcowfat");//cow mor fat
                MilkData.cow_nash_mor =(float)jsonobject.getDouble("morcowsnf");//cow mor snf
                MilkData.cow_japt_mor =(float)jsonobject.getDouble("morcowerate");//cow mor rate
                MilkData.buff_good_mor = (float)jsonobject.getDouble("morbuffliter");   //buff mor ltr
                MilkData.buff_low_mor = (float)jsonobject.getDouble("morbufffat");    //buff mor fat
                MilkData.buff_nash_mor = (float)jsonobject.getDouble("morbuffsnf");   //buff mor snf
                MilkData.buff_japt_mor = (float)jsonobject.getDouble("morbufferate");   //buff mor rate
                MilkData.fat_mor = 0;//(float)jsonobject.getDouble("fat_mor");
                MilkData.snf_mor = 0;//(float)jsonobject.getDouble("snf_mor");
                MilkData.pro_mor = 0;//(float)jsonobject.getDouble("pro_mor");

                MilkData.sangh_eve = (float)jsonobject.getDouble("evecowamount");
                MilkData.adp_eve = (float)jsonobject.getDouble("evebuffamount");
                MilkData.parisar_eve =0;// (float)jsonobject.getDouble("parisar_eve");
                MilkData.cow_good_eve = (float)jsonobject.getDouble("evecowliter");//cow eve ltr
                MilkData.cow_low_eve = (float)jsonobject.getDouble("evecowfat");//cow eve fat
                MilkData.cow_nash_eve = (float)jsonobject.getDouble("evecowsnf");//cow eve snf
                MilkData.cow_japt_eve = (float)jsonobject.getDouble("evecowerate");//cow eve rate
                MilkData.buff_good_eve = (float)jsonobject.getDouble("evebuffliter");//buff eve ltr
                MilkData.buff_low_eve = (float)jsonobject.getDouble("evebufffat");//buff eve fat
                MilkData.buff_nash_eve =(float)jsonobject.getDouble("evebuffsnf");//buff eve snf
                MilkData.buff_japt_eve =(float)jsonobject.getDouble("evebufferate");//buff eve rate
                MilkData.fat_eve = 0;//(float)jsonobject.getDouble("fat_eve");
                MilkData.snf_eve = 0;//(float)jsonobject.getDouble("snf_eve");
                MilkData.pro_eve =0;// (float)jsonobject.getDouble("pro_eve");



            }catch (Exception e) {
                //Toast.makeText(this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }catch (Exception rr){
            //Toast.makeText(this,""+rr.getMessage(), Toast.LENGTH_SHORT).show();

        }
        refreshData();
    }



    private void refreshData()
    {
        setData3();
        setData4();
        try {
            AdView mAdView = (AdView) findViewById(R.id.adView_dash1);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }catch (Exception er    ){}
//        if(ApplicationRuntimeStorage.ORG_ADS==0) {
//            mInterstitialAd.loadAd(new AdRequest.Builder().build());
//        }
    }
    private void setData4() {

        if(buttonState==0){
            literbuff.setText(""+MilkData.buff_good_all);
            fatbuff.setText(""+MilkData.buff_low_all);
            snfbuff.setText(""+MilkData.buff_nash_all);
            ratebuff.setText(""+MilkData.buff_japt_all);
            amtbuff.setText(""+MilkData.adp_all);
        }else  if(buttonState==1){
            literbuff.setText(""+MilkData.buff_good_mor);
            fatbuff.setText(""+MilkData.buff_low_mor);
            snfbuff.setText(""+MilkData.buff_nash_mor);
            ratebuff.setText(""+MilkData.buff_japt_mor);
            amtbuff.setText(""+MilkData.adp_mor);
        }else  if(buttonState==2){
            literbuff.setText(""+MilkData.buff_good_eve);
            fatbuff.setText(""+MilkData.buff_low_eve);
            snfbuff.setText(""+MilkData.buff_nash_eve);
            ratebuff.setText(""+MilkData.buff_japt_eve);
            amtbuff.setText(""+MilkData.adp_eve);
        }else
        {
            literbuff.setText("0");
            fatbuff.setText("0");
            snfbuff.setText("0");
            ratebuff.setText("0");
            amtbuff.setText("0");
        }
    }
    private void setData3() {

        if(buttonState==0){
            litercow.setText(""+MilkData.cow_good_all);
            fatcow.setText(""+MilkData.cow_low_all);
            snfcow.setText(""+MilkData.cow_nash_all);
            ratecow.setText(""+MilkData.cow_japt_all);
            amtcow.setText(""+MilkData.sangh_all);
        }else  if(buttonState==1){
            litercow.setText(""+MilkData.cow_good_mor);
            fatcow.setText(""+MilkData.cow_low_mor);
            snfcow.setText(""+MilkData.cow_nash_mor);
            ratecow.setText(""+MilkData.cow_japt_mor);
            amtcow.setText(""+MilkData.sangh_mor);
        }else  if(buttonState==2){
            litercow.setText(""+MilkData.cow_good_eve);
            fatcow.setText(""+MilkData.cow_low_eve);
            snfcow.setText(""+MilkData.cow_nash_eve);
            ratecow.setText(""+MilkData.cow_japt_eve);
            amtcow.setText(""+MilkData.sangh_eve);
        }else
        {
            litercow.setText("0");
            fatcow.setText("0");
            snfcow.setText("0");
            ratecow.setText("0");
            amtcow.setText("0");
        }
    }
    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Dashboard_santsha.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
