package com.vsi.smart.dairy1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

public class Paymentreport extends AppCompatActivity {

    public Calendar c = Calendar.getInstance();
    public final int mYear = c.get(Calendar.YEAR);
    public  final int mMonth = c.get(Calendar.MONTH);
    public final  int mDay = c.get(Calendar.DAY_OF_MONTH);
    public int i;
    public  int pday;
    public int pmonth;
    public int pyear;
    InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_paymentreport);

        final TextView date=(TextView)findViewById(com.vsi.smart.dairy1.R.id.date);
        final TextView date2=(TextView)findViewById(com.vsi.smart.dairy1.R.id.date2);
        ImageButton datebtn=(ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate);
        ImageButton datebtn2=(ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate2);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Milk Payment Report");

        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Paymentreport.this,Dashboard_santsha.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


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


            AdView mAdView = (AdView) findViewById(R.id.adView_dash121);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);


        }catch (Exception r){

        }


        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Paymentreport.this,
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
                            }
                        },mYear,mMonth,mDay);

                datePickerDialog.show();
            }
        });
        datebtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Paymentreport.this,
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

                                getWebData(date.getText().toString(),date2.getText().toString());
                            }
                        },mYear,mMonth,mDay);

                datePickerDialog.show();
            }
        });


    }

    private void getWebData(String fromdt,String todt){

        try{
            AdView mAdView = (AdView) findViewById(R.id.adView_dash121);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

        }catch (Exception r){}

        try{
            TableLayout  table1=(TableLayout)findViewById(com.vsi.smart.dairy1.R.id.table1);
            TextView liters1=(TextView)findViewById(com.vsi.smart.dairy1.R.id.id_liters);
             TextView pamt1=(TextView)findViewById(com.vsi.smart.dairy1.R.id.id_pamt);
             TextView damt1=(TextView)findViewById(com.vsi.smart.dairy1.R.id.id_damt);
             TextView namt1=(TextView)findViewById(com.vsi.smart.dairy1.R.id.id_namt);



            CallSoap cs = new CallSoap();
            String JSON_RESULT = cs.GetMilkPayment(ApplicationRuntimeStorage.COMPANYID,ApplicationRuntimeStorage.pcode,fromdt,todt );
            try {
                JSONArray jsonarray = new JSONArray(JSON_RESULT);
                double tltr=0;
                double pamt_1=0;
                DecimalFormat f = new DecimalFormat("##.00");
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);

                    String  did = jsonobject.getString("did");
                    String  dname = jsonobject.getString("dname");
                    String  amt = jsonobject.getString("amt");

                    int didd = Integer.parseInt(did);
                    if(didd == 0) {
                        String liters = jsonobject.getString("liters");
                        liters1.setText(liters);
                        String pamt = jsonobject.getString("pamt");
                        pamt1.setText(pamt);
                        String namt = jsonobject.getString("namt");
                        namt1.setText(namt);
                        String damt = jsonobject.getString("damt");
                        damt1.setText(damt);
                        pamt_1 = Double.parseDouble(pamt);
                        continue;
                    }


                    double good_ltr = Double.parseDouble(amt); tltr = tltr+good_ltr;



                    TableRow row= new TableRow(Paymentreport.this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);

                    TextView did11 = new TextView(Paymentreport.this);
                    did11.setText("\n\t\t"+did);
                    row.addView(did11);

                    TextView receiptDate1 = new TextView(Paymentreport.this);
                    receiptDate1.setText("\n\t\t"+dname);
                    row.addView(receiptDate1);

                    TextView cname1 = new TextView(Paymentreport.this);
                    cname1.setText("\n\t\t"+amt);
                    row.addView(cname1);



                    table1.addView(row);
                }

                namt1.setText(f.format( pamt_1-tltr));
                TextView tliter1=(TextView) findViewById(com.vsi.smart.dairy1.R.id.tliter);tliter1.setText(""+f.format( tltr));


            } catch (Exception e) {
            }
        }catch (Exception rr){
            //Toast.makeText(this,""+rr.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }
    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Paymentreport.this,Login.class);
            startActivity(intent);
        }else{}

        try{

            AdView mAdView = (AdView) findViewById(R.id.adView_dash121);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }catch (Exception er){}
        super.onResume();
    }
}
