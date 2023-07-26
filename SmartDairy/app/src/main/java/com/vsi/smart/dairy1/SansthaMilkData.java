package com.vsi.smart.dairy1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SansthaMilkData extends AppCompatActivity {
    public Calendar c = Calendar.getInstance();
    public final int mYear = c.get(Calendar.YEAR);
    public  final int mMonth = c.get(Calendar.MONTH);
    public final  int mDay = c.get(Calendar.DAY_OF_MONTH);
    public int i;
    public  int pday;
    public int pmonth;
    public int pyear;
    public int buttonState=2;
    public TableLayout table1;
    public int primarycolor;
    public InterstitialAd mInterstitialAd;
    public Button btnmorning,btnevening;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_sanstha_milk_data);

        final TextView date=(TextView)findViewById(com.vsi.smart.dairy1.R.id.date);
        final TextView date2=(TextView)findViewById(com.vsi.smart.dairy1.R.id.date2);
        ImageButton datebtn=(ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate);
        ImageButton datebtn2=(ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate2);

        TextView idcustname=(TextView)findViewById(com.vsi.smart.dairy1.R.id.id_custname1);
        idcustname.setText(ApplicationRuntimeStorage.pname);

        final TextView pro=(TextView)findViewById(com.vsi.smart.dairy1.R.id.lbl_pro);
        final TextView kami=(TextView)findViewById(com.vsi.smart.dairy1.R.id.lbl_kami);
        final TextView nash=(TextView)findViewById(com.vsi.smart.dairy1.R.id.lbl_nash);
        final TextView japt=(TextView)findViewById(com.vsi.smart.dairy1.R.id.lbl_japt);

        if(ApplicationRuntimeStorage.ORG_TYPE==1){
            pro.setText("डिग्री");
            kami.setText("दर");
            nash.setText("रक्कम");
            japt.setText("");
        }

        btnmorning=(Button) findViewById(com.vsi.smart.dairy1.R.id.btnmorning);
        btnevening=(Button) findViewById(com.vsi.smart.dairy1.R.id.btnevening);
        primarycolor=Color.parseColor("#ff14b590");
        table1=(TableLayout)findViewById(com.vsi.smart.dairy1.R.id.table1);



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


            AdView mAdView = (AdView) findViewById(R.id.adView_dash122);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);


        }catch (Exception r){

        }


        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DatePickerDialog datePickerDialog = new DatePickerDialog(SansthaMilkData.this,
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
                                Date date112 = new Date(year-1900, (monthOfYear), dayOfMonth);
                                String currdate12="";
                                currdate12=df.format(date112.getTime());
                                date.setText(currdate12);
                                buttonState = 2;


                                btnmorning.setBackgroundColor(primarycolor);
                                btnevening.setBackgroundColor(primarycolor);
//
////                                Toast.makeText(Birthday.this,pyear+ "-"+pmonth+"-"+pday, Toast.LENGTH_SHORT).show();
////                                getWebData(date.getText().toString());
//
                            }
                        },mYear,mMonth,mDay);

                datePickerDialog.show();
            }
        });
        datebtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DatePickerDialog datePickerDialog = new DatePickerDialog(SansthaMilkData.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth)
                            {
                                date2.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                pyear=year;
                                pmonth=monthOfYear;
                                pday=dayOfMonth;
                                SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
                                Date date111 = new Date(year-1900, (monthOfYear), dayOfMonth);
                                String currdate12="";
                                currdate12=df.format(date111.getTime());
                                date2.setText(currdate12);
                                buttonState = 2;


                                btnmorning.setBackgroundColor(primarycolor);
                                btnevening.setBackgroundColor(primarycolor);
//
////                                Toast.makeText(Birthday.this,pyear+ "-"+pmonth+"-"+pday, Toast.LENGTH_SHORT).show();
////                                getWebData(date.getText().toString());
//
                            }
                        },mYear,mMonth,mDay);

                datePickerDialog.show();
            }
        });



        btnmorning.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                buttonState = 0;

                btnmorning.setBackgroundColor(Color.GREEN);
                btnevening.setBackgroundColor(primarycolor);
                String str1 =   date.getText().toString();
                String str2 =   date2.getText().toString();
                getWebData(str1,str2);
            }
        });

        btnevening.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                buttonState = 1;
                btnmorning.setBackgroundColor(primarycolor);
                btnevening.setBackgroundColor(Color.GREEN);
                String str1 =   date.getText().toString();
                String str2 =   date2.getText().toString();
                getWebData(str1,str2);
            }
        });
    }

    private void getWebData(String datestr,String datestr1) {

        try{
            AdView mAdView = (AdView) findViewById(R.id.adView_dash122);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);


        }catch (Exception r){}

        try {
            table1=(TableLayout)findViewById(com.vsi.smart.dairy1.R.id.table1);
            int cnter = table1.getChildCount();
            for (int i = 0; i < cnter; i++) {
                View child = table1.getChildAt(i);
                if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
            }
            CallSoap cs = new CallSoap();
            String JSON_RESULT = cs.GetMilkCollectionData_Sanstha(ApplicationRuntimeStorage.COMPANYID, buttonState, ApplicationRuntimeStorage.pcode, datestr,datestr1,ApplicationRuntimeStorage.ORG_TYPE);
            try {
                JSONArray jsonarray = new JSONArray(JSON_RESULT);
                double tltr=0;
                double tlow=0;
                double tnash=0;
                double tjapt=0;
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);

                    String  receiptDate = jsonobject.getString("receiptDate");
                    String  liters = jsonobject.getString("liters");
                    double good_ltr = Double.parseDouble(liters); tltr = tltr+good_ltr;
                    String  fat= jsonobject.getString("fat");
                    String  snf= jsonobject.getString("snf");
                    String  proteen= jsonobject.getString("proteen");
                    String  lowqlycanltr= jsonobject.getString("lowqlycanltr");
                    double low_ltr =  Double.parseDouble(lowqlycanltr);tlow = tlow+low_ltr;
                    String nashcanltr=jsonobject.getString("nashcanltr");
                    double nash_ltr =  Double.parseDouble(nashcanltr);tnash = tnash+nash_ltr;
                    String japtcanltr=jsonobject.getString("japtcanltr");
                    double japt_ltr = Double.parseDouble(japtcanltr);tjapt = tjapt+japt_ltr;
                    TableRow row= new TableRow(SansthaMilkData.this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);

                    TextView receiptDate1 = new TextView(SansthaMilkData.this);
                    receiptDate1.setText("\n\t\t"+receiptDate.substring(0,5));
                    row.addView(receiptDate1);

                    TextView liters1 = new TextView(SansthaMilkData.this);
                    liters1.setText("\n\t\t"+liters);
                    row.addView(liters1);

                    TextView fat1 = new TextView(SansthaMilkData.this);
                    fat1.setText("\n\t\t"+fat);
                    row.addView(fat1);

                    TextView snf1 = new TextView(SansthaMilkData.this);
                    snf1.setText("\n\t\t"+snf);
                    row.addView(snf1);


//                    TextView proteen1 = new TextView(SansthaMilkData.this);
//                    proteen1.setText("\n\t\t"+proteen);
//                    row.addView(proteen1);

                    TextView lowqlycanltr1 = new TextView(SansthaMilkData.this);
                    lowqlycanltr1.setText("\n\t\t"+lowqlycanltr);
                    row.addView(lowqlycanltr1);

                    TextView nashcanltr1 = new TextView(SansthaMilkData.this);
                    nashcanltr1.setText("\n\t"+nashcanltr);
                    row.addView(nashcanltr1);

//                    if(ApplicationRuntimeStorage.ORG_TYPE==0){
//
//
//                        TextView japtcanltr1 = new TextView(SansthaMilkData.this);
//                        japtcanltr1.setText("\n\t"+japtcanltr);
//                        row.addView(japtcanltr1);
//                    }else{
//
//
//
//                        TextView japtcanltr1 = new TextView(SansthaMilkData.this);
//                        japtcanltr1.setText("\n\t");
//                        row.addView(japtcanltr1);
//                    }






                    table1.addView(row);
                }
                DecimalFormat f = new DecimalFormat("##.00");
                 if(ApplicationRuntimeStorage.ORG_TYPE==0) {

                    TextView tliter1=(TextView) findViewById(com.vsi.smart.dairy1.R.id.tliter);tliter1.setText(""+Math.round( tltr));
                    TextView tlow1=(TextView) findViewById(com.vsi.smart.dairy1.R.id.tlow);tlow1.setText(""+Math.round(tlow));
                    TextView tnash1=(TextView) findViewById(com.vsi.smart.dairy1.R.id.tnash);tnash1.setText(""+Math.round(tnash));


                    TextView tjapt1 = (TextView) findViewById(com.vsi.smart.dairy1.R.id.tjapt);
                    tjapt1.setText("" + Math.round(tjapt));
                }else{
                     TextView tliter1=(TextView) findViewById(com.vsi.smart.dairy1.R.id.tliter);tliter1.setText(""+f.format( tltr));
                     TextView tlow1=(TextView) findViewById(com.vsi.smart.dairy1.R.id.tlow);tlow1.setText(""+f.format(tnash/tltr));
                     TextView tnash1=(TextView) findViewById(com.vsi.smart.dairy1.R.id.tnash);tnash1.setText(""+f.format(tnash));

                     TextView tjapt1 = (TextView) findViewById(com.vsi.smart.dairy1.R.id.tjapt);
                    tjapt1.setText("" );

                }

            } catch (Exception e) {
            }
        } catch (Exception rr) {
        }
    }
    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(SansthaMilkData.this,Login.class);
            startActivity(intent);
        }else{}

        try{
            AdView mAdView = (AdView) findViewById(R.id.adView_dash122);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);


        }catch (Exception r){}
        super.onResume();
    }
}
