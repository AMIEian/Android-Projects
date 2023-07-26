package com.vsi.smart.dairy1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Dashboard extends AppCompatActivity {
    int primarycolor;
    int buttonState = 0;
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
        setContentView(com.vsi.smart.dairy1.R.layout.activity_dashboard);

        chart6=(BarChart)findViewById(com.vsi.smart.dairy1.R.id.chart6);
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
        AdView mAdView = (AdView) findViewById(com.vsi.smart.dairy1.R.id.adView_dash);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Dashboard.this,Login.class);
                startActivity(intent);            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        final TextView date=(TextView)findViewById(com.vsi.smart.dairy1.R.id.date);
        ImageButton datebtn=(ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate);
        btnpersonal=(Button) findViewById(com.vsi.smart.dairy1.R.id.btnpersonal);
        btncompany=(Button) findViewById(com.vsi.smart.dairy1.R.id.btncompany);
        btnall=(Button) findViewById(com.vsi.smart.dairy1.R.id.btnall);
        btnmorning=(Button) findViewById(com.vsi.smart.dairy1.R.id.btnmorning);
        btnevening=(Button) findViewById(com.vsi.smart.dairy1.R.id.btnevening);
        primarycolor=Color.parseColor("#ff14b590");
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
                chart6.setVisibility(View.INVISIBLE);
                if(flagcomp==1 && flagper==1)
                {
                    btncompany.setVisibility(View.VISIBLE);
                    btnpersonal.setVisibility(View.VISIBLE);

                    if(ApplicationRuntimeStorage.ORG_TYPE==0){
                        chart6.setVisibility(View.VISIBLE);
                    }


                    btncompany.setBackgroundColor(Color.GREEN); //default selected
                    result=0;
                }
              //  result=0;
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
                chart6.setVisibility(View.VISIBLE);
                getWebData(date.getText().toString());
//                            0 for company data

            }
        });
        btnpersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnpersonal.setBackgroundColor(Color.GREEN);
                btncompany.setBackgroundColor(primarycolor);
                chart6.setVisibility(View.INVISIBLE);
                btncompare=1;
                result=1;
                getWebData(date.getText().toString());


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
                DatePickerDialog datePickerDialog = new DatePickerDialog(Dashboard.this,
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


                                getWebData(date.getText().toString());


                            }
                        },mYear,mMonth,mDay);

                datePickerDialog.show();

            }
        });
        btnall.setBackgroundColor(Color.GREEN); //default selected
        getWebData(date.getText().toString());
        refreshData();

        Button menu2=(Button) findViewById(com.vsi.smart.dairy1.R.id.btn_menu2);
        //click on Order placed button
        menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(Dashboard.this,options.class);
                startActivity(intent);
            }
        });
//        Button menu1=(Button) findViewById(R.id.btn_menu1);
//        //click on Order placed button
//        menu1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                Intent intent=new Intent(Dashboard.this,options.class);
//                startActivity(intent);
//            }
//        });
    }
    private void refreshData(){
        setData1();
        setData2();
        setData3();
        setData4();
        setData5();
        setData6();
        AdView mAdView = (AdView) findViewById(com.vsi.smart.dairy1.R.id.adView_dash);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        if(ApplicationRuntimeStorage.ORG_ADS==0) {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
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
    private void setData6() {
        BarChart mChart = (BarChart) findViewById(com.vsi.smart.dairy1.R.id.chart6);
        float start = 1f;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        if(buttonState==0){
            yVals1.add(new BarEntry(0, MilkData.buff_good_all+MilkData.cow_good_all,"चांगले दुध लिटर"));
            yVals1.add(new BarEntry(1, MilkData.buff_low_all+ MilkData.cow_low_all,"कमी प्रत लिटर"));
            yVals1.add(new BarEntry(2, MilkData.buff_nash_all+ MilkData.cow_nash_all,"नाश लिटर"));
            yVals1.add(new BarEntry(3, MilkData.buff_japt_all+MilkData.cow_japt_all,"जप्त लिटर"));
        }else  if(buttonState==1){
            yVals1.add(new BarEntry(0, MilkData.buff_good_mor+MilkData.cow_good_mor,"चांगले दुध लिटर"));
            yVals1.add(new BarEntry(1, MilkData.buff_low_mor+ MilkData.cow_low_mor,"कमी प्रत लिटर"));
            yVals1.add(new BarEntry(2, MilkData.buff_nash_mor+ MilkData.cow_nash_mor,"नाश लिटर"));
            yVals1.add(new BarEntry(3, MilkData.buff_japt_mor+MilkData.cow_japt_mor,"जप्त लिटर"));
        }else  if(buttonState==2){
            yVals1.add(new BarEntry(0, MilkData.buff_good_eve+MilkData.cow_good_eve,"चांगले दुध लिटर"));
            yVals1.add(new BarEntry(1, MilkData.buff_low_eve+ MilkData.cow_low_eve,"कमी प्रत लिटर"));
            yVals1.add(new BarEntry(2, MilkData.buff_nash_eve+ MilkData.cow_nash_eve,"नाश लिटर"));
            yVals1.add(new BarEntry(3, MilkData.buff_japt_eve+MilkData.cow_japt_eve,"जप्त लिटर"));
        }else {
            yVals1.add(new BarEntry(0, 0, "चांगले दुध लिटर"));
            yVals1.add(new BarEntry(1, 0, "कमी प्रत लिटर"));
            yVals1.add(new BarEntry(2, 0, "नाश लिटर"));
            yVals1.add(new BarEntry(3, 0, "जप्त लिटर"));
        }
        BarDataSet set1;
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.animateXY(2000, 2000);
            mChart.invalidate();
        } else {

            set1 = new BarDataSet(yVals1, "");
            set1.setDrawIcons(false);
            set1.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSets.add(set1);

            BarData data = new BarData( dataSets);
            data.setValueTextSize(10f);
            //data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);


            mChart.setData(data);
            mChart.getXAxis().setValueFormatter(new LabelFormatter(set1));
            XAxis xAxis = mChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);
            Description desc = new Description();
            desc.setText("एकुण दुध संकलन समरी ");
            mChart.setDescription(desc );
            mChart.animateXY(2000, 2000);
            mChart.invalidate();
        }
    }
    private void setData5() {
        BarChart mChart = (BarChart) findViewById(com.vsi.smart.dairy1.R.id.chart5);
        float start = 1f;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        if(buttonState==0){
            yVals1.add(new BarEntry(0, MilkData.fat_all,"सरासरी फॅट"+"("+MilkData.fat_all+")"));
            yVals1.add(new BarEntry(1, MilkData.snf_all,"एस एन एफ"+"("+ MilkData.snf_all+")"));
            yVals1.add(new BarEntry(2, MilkData.pro_all,"प्रोटीन"+"("+MilkData.pro_all+")"));

        }else  if(buttonState==1){
            yVals1.add(new BarEntry(0, MilkData.fat_mor,"सरासरी फॅट"+"("+MilkData.fat_mor+")"));
            yVals1.add(new BarEntry(1, MilkData.snf_mor,"एस एन एफ"+"("+ MilkData.snf_mor+")"));
            yVals1.add(new BarEntry(2, MilkData.pro_mor,"प्रोटीन"+"("+MilkData.pro_mor+")"));
        }else  if(buttonState==2){
            yVals1.add(new BarEntry(0, MilkData.fat_eve,"सरासरी फॅट"+"("+MilkData.fat_eve+")"));
            yVals1.add(new BarEntry(1, MilkData.snf_eve,"एस एन एफ"+"("+MilkData.snf_eve+")"));
            yVals1.add(new BarEntry(2, MilkData.pro_eve,"प्रोटीन"+"("+MilkData.pro_eve+")"));
        }else {
            yVals1.add(new BarEntry(0, 0,"सरासरी फॅट"));
            yVals1.add(new BarEntry(1, 0,"एस एन एफ"));
            yVals1.add(new BarEntry(2, 0,"प्रोटीन"));
        }
        BarDataSet set1;
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.animateXY(2000, 2000);
            mChart.invalidate();
        } else {

            set1 = new BarDataSet(yVals1, "");
            set1.setDrawIcons(false);
            set1.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSets.add(set1);

            BarData data = new BarData( dataSets);
            data.setValueTextSize(12f);
            //data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);


            mChart.setData(data);
            mChart.getXAxis().setValueFormatter(new LabelFormatter(set1));
            XAxis xAxis = mChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);
            Description desc = new Description();
            desc.setText("सरासरी  फॅट / एस एन एफ / प्रोटीन ");
            mChart.setDescription(desc );
            mChart.animateXY(2000, 2000);
            mChart.invalidate();
        }
    }

    private void setData4() {
        BarChart mChart = (BarChart) findViewById(com.vsi.smart.dairy1.R.id.chart4);
        float start = 1f;
        String  good_label = "म्हैस चांगले";
        String  low_label = "कमी प्रत ";
        String  nash_label = "नाश";
        String  japt_label = "जप्त";
        if(result==1 && ApplicationRuntimeStorage.ORG_TYPE==1){

            good_label = "म्हैस लिटर";
            low_label = "फॅट";
            nash_label = "SNF";
            japt_label = "दर";
        }
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        if(buttonState==0){
            yVals1.add(new BarEntry(0, MilkData.buff_good_all,good_label+"("+ MilkData.buff_good_all+")"));
            yVals1.add(new BarEntry(1, MilkData.buff_low_all,low_label+"("+ MilkData.buff_low_all+")"));
            yVals1.add(new BarEntry(2, MilkData.buff_nash_all,nash_label+"("+ MilkData.buff_nash_all+")"));
            yVals1.add(new BarEntry(3, MilkData.buff_japt_all,japt_label+"("+ MilkData.buff_japt_all+")"));
        }else  if(buttonState==1){
            yVals1.add(new BarEntry(0, MilkData.buff_good_mor,good_label+"("+ MilkData.buff_good_mor+")"));
            yVals1.add(new BarEntry(1, MilkData.buff_low_mor,low_label+"("+ MilkData.buff_low_mor+")"));
            yVals1.add(new BarEntry(2, MilkData.buff_nash_mor,nash_label+"("+ MilkData.buff_nash_mor+")"));
            yVals1.add(new BarEntry(3, MilkData.buff_japt_mor,japt_label+"("+ MilkData.buff_japt_mor+")"));
        }else  if(buttonState==2){
            yVals1.add(new BarEntry(0, MilkData.buff_good_eve,good_label+"("+ MilkData.buff_good_eve+")"));
            yVals1.add(new BarEntry(1, MilkData.buff_low_eve,low_label+"("+ MilkData.buff_low_eve+")"));
            yVals1.add(new BarEntry(2, MilkData.buff_nash_eve,nash_label+"("+ MilkData.buff_nash_eve+")"));
            yVals1.add(new BarEntry(3, MilkData.buff_japt_eve,japt_label+"("+ MilkData.buff_japt_eve+")"));
        }else {
            yVals1.add(new BarEntry(0, 0, good_label));
            yVals1.add(new BarEntry(1, 0, low_label));
            yVals1.add(new BarEntry(2, 0, nash_label));
            yVals1.add(new BarEntry(3, 0, japt_label));
        }
        BarDataSet set1;
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.animateXY(2000, 2000);
            mChart.invalidate();
        } else {

            set1 = new BarDataSet(yVals1, "");
            set1.setDrawIcons(false);
            set1.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSets.add(set1);

            BarData data = new BarData( dataSets);
            data.setValueTextSize(10f);
            //data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);


            mChart.setData(data);
            mChart.getXAxis().setValueFormatter(new LabelFormatter(set1));
            XAxis xAxis = mChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);
            Description desc = new Description();
            desc.setText(" म्हैस दुध ");
            mChart.setDescription(desc );
            mChart.animateXY(2000, 2000);
            mChart.invalidate();
        }
    }
    private void setData3() {
        BarChart mChart = (BarChart) findViewById(com.vsi.smart.dairy1.R.id.chart3);
        float start = 1f;

        String  good_label = "गाय चांगले";
        String  low_label = "कमी प्रत ";
        String  nash_label = "नाश";
        String  japt_label = "जप्त";
        if(result==1 && ApplicationRuntimeStorage.ORG_TYPE==1){

             good_label = "गाय लिटर";
             low_label = "फॅट";
             nash_label = "SNF";
             japt_label = "दर";
        }


        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        if(buttonState==0){
            yVals1.add(new BarEntry(0, MilkData.cow_good_all,good_label+"("+ MilkData.cow_good_all+")"));
            yVals1.add(new BarEntry(1, MilkData.cow_low_all,low_label+"("+ MilkData.cow_low_all+")"));
            yVals1.add(new BarEntry(2, MilkData.cow_nash_all,nash_label+"("+ MilkData.cow_nash_all+")"));
            yVals1.add(new BarEntry(3, MilkData.cow_japt_all,japt_label+"("+ MilkData.cow_japt_all+")"));
        }else  if(buttonState==1){
            yVals1.add(new BarEntry(0, MilkData.cow_good_mor,good_label+"("+ MilkData.cow_good_mor+")"));
            yVals1.add(new BarEntry(1, MilkData.cow_low_mor,low_label+"("+ MilkData.cow_low_mor+")"));
            yVals1.add(new BarEntry(2, MilkData.cow_nash_mor,nash_label+"("+ MilkData.cow_nash_mor+")"));
            yVals1.add(new BarEntry(3, MilkData.cow_japt_mor,japt_label+"("+ MilkData.cow_japt_mor+")"));
        }else  if(buttonState==2){
            yVals1.add(new BarEntry(0, MilkData.cow_good_eve,good_label+"("+ MilkData.cow_good_eve+")"));
            yVals1.add(new BarEntry(1, MilkData.cow_low_eve,low_label+"("+ MilkData.cow_low_eve+")"));
            yVals1.add(new BarEntry(2, MilkData.cow_nash_eve,nash_label+"("+ MilkData.cow_nash_eve+")"));
            yVals1.add(new BarEntry(3, MilkData.cow_japt_eve,japt_label+"("+ MilkData.cow_japt_eve+")"));
        }else {
            yVals1.add(new BarEntry(0, 0, good_label));
            yVals1.add(new BarEntry(1, 0, low_label));
            yVals1.add(new BarEntry(2, 0, nash_label));
            yVals1.add(new BarEntry(3, 0, japt_label));
        }
        BarDataSet set1;
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.animateXY(2000, 2000);
            mChart.invalidate();
        } else {

            set1 = new BarDataSet(yVals1, "");
            set1.setDrawIcons(false);
            set1.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSets.add(set1);

            BarData data = new BarData( dataSets);
            data.setValueTextSize(10f);
            //data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);


            mChart.setData(data);
            mChart.getXAxis().setValueFormatter(new LabelFormatter(set1));
            XAxis xAxis = mChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);
            Description desc = new Description();
            desc.setText(" गाय दुध ");
            mChart.setDescription(desc );
            mChart.animateXY(2000, 2000);
            mChart.invalidate();
        }
    }
    private void setData2() {
        BarChart mChart = (BarChart) findViewById(com.vsi.smart.dairy1.R.id.chart2);
        float start = 1f;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();



        if(buttonState==0){
            yVals1.add(new BarEntry(0, MilkData.cow_good_all,"गाय लिटर"+"("+MilkData.cow_good_all+")"));
            yVals1.add(new BarEntry(1, MilkData.buff_good_all,"म्हैस लिटर"+"("+MilkData.buff_good_all+")"));
        }else  if(buttonState==1){
            yVals1.add(new BarEntry(0, MilkData.cow_good_mor,"गाय लिटर"+"("+MilkData.cow_good_mor+")"));
            yVals1.add(new BarEntry(1, MilkData.buff_good_mor,"म्हैस लिटर"+"("+MilkData.buff_good_mor+")"));
        }else  if(buttonState==2){
            yVals1.add(new BarEntry(0, MilkData.cow_good_eve,"गाय लिटर"+"("+MilkData.cow_good_eve+")"));
            yVals1.add(new BarEntry(1, MilkData.buff_good_eve,"म्हैस लिटर"+"("+MilkData.buff_good_eve+")"));
        }else {
            yVals1.add(new BarEntry(0, 0,"गाय"));
            yVals1.add(new BarEntry(1, 0,"म्हैस"));
        }



        BarDataSet set1;
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.animateXY(2000, 2000);
            mChart.invalidate();
        } else {

            set1 = new BarDataSet(yVals1, "");
            set1.setDrawIcons(false);
            set1.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSets.add(set1);

            BarData data = new BarData( dataSets);
            data.setValueTextSize(10f);
            //data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);


            mChart.setData(data);
            mChart.getXAxis().setValueFormatter(new LabelFormatter(set1));
            XAxis xAxis = mChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);
            Description desc = new Description();
            desc.setText(" गाय / म्हैस ");
            mChart.setDescription(desc );
            mChart.animateXY(2000, 2000);
            mChart.invalidate();
        }
    }
    private void setData1() {
        BarChart mChart = (BarChart) findViewById(com.vsi.smart.dairy1.R.id.chart1);
        float start = 1f;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        String sangh_label = "संघ";
        String adp_label = "एडीपी";
        String parisar_label = "परीसर";
        if(result==1 && ApplicationRuntimeStorage.ORG_TYPE==1){
            sangh_label = "गाय दुध रक्कम ";
            adp_label = "म्हैस दुध रक्कम ";
            parisar_label = "";
        }
        if(buttonState==0){
            yVals1.add(new BarEntry(0, MilkData.sangh_all,sangh_label+"("+MilkData.sangh_all+")"));
            yVals1.add(new BarEntry(1, MilkData.adp_all,adp_label+"("+MilkData.adp_all+")"));
            if(result==0)
            yVals1.add(new BarEntry(2, MilkData.parisar_all,parisar_label+"("+MilkData.parisar_all+")"));
        }else  if(buttonState==1){
            yVals1.add(new BarEntry(0, MilkData.sangh_mor,sangh_label+"("+MilkData.sangh_mor+")"));
            yVals1.add(new BarEntry(1, MilkData.adp_mor,adp_label+"("+MilkData.adp_mor+")"));
            if(result==0)
            yVals1.add(new BarEntry(2, MilkData.parisar_mor,parisar_label+"("+MilkData.parisar_mor+")"));
        }else  if(buttonState==2){
            yVals1.add(new BarEntry(0, MilkData.sangh_eve,sangh_label+"("+MilkData.sangh_eve+")"));
            yVals1.add(new BarEntry(1, MilkData.adp_eve,adp_label+"("+MilkData.adp_eve+")"));
            if(result==0)
            yVals1.add(new BarEntry(2, MilkData.parisar_eve,parisar_label+"("+MilkData.parisar_eve+")"));
        }else {
            yVals1.add(new BarEntry(0, 0,sangh_label));
            yVals1.add(new BarEntry(1, 0,adp_label));
            if(result==0)
            yVals1.add(new BarEntry(2, 0,parisar_label));
        }


        BarDataSet set1;
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.animateXY(2000, 2000);
            mChart.invalidate();
        } else {

            set1 = new BarDataSet(yVals1, "");
            set1.setDrawIcons(false);
            set1.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSets.add(set1);

            BarData data = new BarData( dataSets);
            data.setValueTextSize(10f);
            //data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);


            mChart.setData(data);
            mChart.getXAxis().setValueFormatter(new LabelFormatter(set1));
            XAxis xAxis = mChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);
            Description desc = new Description();
            desc.setText(sangh_label+" / "+adp_label+" / "+parisar_label);
            mChart.setDescription(desc );
            mChart.animateXY(2000, 2000);
            mChart.invalidate();
        }
    }

    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Dashboard.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
