package com.vsi.smart.dairy1;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;


public class options extends AppCompatActivity {
  //  InterstitialAd mInterstitialAd;
    FloatingActionButton fab;String option;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_options);
        List<String> list = Arrays.asList(ApplicationRuntimeStorage.AccessRights.split(","));

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                option= "1";
            } else {
                option= extras.getString("option");
            }
        } else {
            option= (String) savedInstanceState.getSerializable("option");
        }


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Menu");

//        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(R.id.imgbutton);
//        imgbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//               Intent intent=new Intent(options.this,Mainoptions.class);
//                startActivity(intent);
//            }
//        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

//        MobileAds.initialize(this, "ca-app-pub-4635245989767728/7160452698");
//        AdView mAdView = (AdView) findViewById(R.id.adView_opt);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

       // fab =(FloatingActionButton) findViewById(R.id.fab);

        Button id_btncomplaint =(Button) findViewById(com.vsi.smart.dairy1.R.id.btncomplaint);
        Button id_btnNotification =(Button) findViewById(com.vsi.smart.dairy1.R.id.btnNotification);
        Button id_btnMilkreport=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnMilkreport);
        Button id_btnNewsfeed=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnNewsfeed);
        Button id_btn_aboutUs=(Button)findViewById(com.vsi.smart.dairy1.R.id.btn_aboutUs);
        Button id_btn_payments=(Button)findViewById(com.vsi.smart.dairy1.R.id.btn_payments);
        Button id_btn_Converter=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnConverters);
        Button id_btnsankalan=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnsankalan);
        Button id_btnsale=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnsale);
        Button id_btnchalan=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnchalan);
        Button id_btnpashu=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnpashu);
        Button id_btnpashupurchase=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnpashupurchase);
        Button btncustlist=(Button)findViewById(com.vsi.smart.dairy1.R.id.btncustlist);
        Button id_btn_ledger_bal=(Button)findViewById(com.vsi.smart.dairy1.R.id.btn_ledgerandbal);
        Button btnratelist=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnratelist);
        Button btnitemlist=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnitemlist);
        Button btnledgerlist=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnledgerlist);
        Button btnuploadmcollectonexcel=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnuploadmcollectonexcel);

        btnuploadmcollectonexcel.setVisibility(View.GONE);

        if(option.equals("1"))
        {
            if (ApplicationRuntimeStorage.AccessRights.contains("MCOLLECTION")) {
                id_btnMilkreport.setVisibility(View.VISIBLE);
            }

            if (ApplicationRuntimeStorage.AccessRights.contains("MPAYMENT")) {
                id_btn_payments.setVisibility(View.VISIBLE);
            }

            if (ApplicationRuntimeStorage.AccessRights.contains("MPURCHASE")) {
                id_btnsankalan.setVisibility(View.VISIBLE);
                btnuploadmcollectonexcel.setVisibility(View.VISIBLE);
            }

            if (ApplicationRuntimeStorage.AccessRights.contains("MSALE")) {
                id_btnsale.setVisibility(View.VISIBLE);
            }

            id_btn_ledger_bal.setVisibility(View.GONE);
            id_btnpashupurchase.setVisibility(View.GONE);
            id_btnpashu.setVisibility(View.GONE);
            id_btnchalan.setVisibility(View.GONE);
            btncustlist.setVisibility(View.GONE);
            btnratelist.setVisibility(View.GONE);
            btnitemlist.setVisibility(View.GONE);
            btnledgerlist.setVisibility(View.GONE);

        }else if(option.equals("2"))
        {
            if (ApplicationRuntimeStorage.AccessRights.contains("IPURCHASE")) {
                id_btnpashupurchase.setVisibility(View.VISIBLE);
            }

            if (ApplicationRuntimeStorage.AccessRights.contains("ISALE")) {
                id_btnpashu.setVisibility(View.VISIBLE);
            }
            btnuploadmcollectonexcel.setVisibility(View.GONE);
            id_btnMilkreport.setVisibility(View.GONE);
            id_btn_payments.setVisibility(View.GONE);
            id_btnsankalan.setVisibility(View.GONE);
            id_btnsale.setVisibility(View.GONE);
            id_btn_ledger_bal.setVisibility(View.GONE);
            id_btnchalan.setVisibility(View.GONE);
            btncustlist.setVisibility(View.GONE);
            btnratelist.setVisibility(View.GONE);
            btnitemlist.setVisibility(View.GONE);
            btnledgerlist.setVisibility(View.GONE);

        }else if(option.equals("3"))
        {
            if (ApplicationRuntimeStorage.AccessRights.contains("LEDGER")) {
                id_btn_ledger_bal.setVisibility(View.VISIBLE);
            }

            if (ApplicationRuntimeStorage.AccessRights.contains("IVOUCHER")) {
                id_btnchalan.setVisibility(View.VISIBLE);
            }
            btnuploadmcollectonexcel.setVisibility(View.GONE);
            id_btnpashupurchase.setVisibility(View.GONE);
            id_btnpashu.setVisibility(View.GONE);
            id_btnMilkreport.setVisibility(View.GONE);
            id_btn_payments.setVisibility(View.GONE);
            id_btnsankalan.setVisibility(View.GONE);
            id_btnsale.setVisibility(View.GONE);
            btncustlist.setVisibility(View.GONE);
            btnratelist.setVisibility(View.GONE);
            btnitemlist.setVisibility(View.GONE);
            btnledgerlist.setVisibility(View.GONE);

        }else if(option.equals("4"))
        {
            btncustlist.setVisibility(View.VISIBLE);
            btnratelist.setVisibility(View.VISIBLE);
            btnitemlist.setVisibility(View.VISIBLE);
            btnledgerlist.setVisibility(View.VISIBLE);

            btnuploadmcollectonexcel.setVisibility(View.GONE);
            id_btn_ledger_bal.setVisibility(View.GONE);
            id_btnchalan.setVisibility(View.GONE);
            id_btnpashupurchase.setVisibility(View.GONE);
            id_btnpashu.setVisibility(View.GONE);
            id_btnMilkreport.setVisibility(View.GONE);
            id_btn_payments.setVisibility(View.GONE);
            id_btnsankalan.setVisibility(View.GONE);
            id_btnsale.setVisibility(View.GONE);
        }

//        if(ApplicationRuntimeStorage.AccessRights.length() > 0) {
//            String[] rulesArr =ApplicationRuntimeStorage.AccessRights.split(",");
//            for (String rule:rulesArr) {
//                if("RULE_MILK_REPORTS".equalsIgnoreCase(rule)){
//                    id_btnMilkreport.setVisibility(View.VISIBLE);
//                }
//
//                if("RULE_PAYMENT_REPORTS".equalsIgnoreCase(rule)){
//                    id_btn_payments.setVisibility(View.VISIBLE);
//                }
//
//                if("RULE_STATEMENT_REPORTS".equalsIgnoreCase(rule)){
//                    id_btn_ledger_bal.setVisibility(View.VISIBLE);
//                }
//
//                if("RULE_COMPLAINTS".equalsIgnoreCase(rule)){
//                    id_btncomplaint.setVisibility(View.VISIBLE);
//                }
//
//                if("RULE_NOTIFICATION".equalsIgnoreCase(rule)){
//                    id_btnNotification.setVisibility(View.VISIBLE);
//                }
//
//                if("RULE_NEWS".equalsIgnoreCase(rule)){
//                    id_btnNewsfeed.setVisibility(View.VISIBLE);
//                }
//                if("RULE_MILK_PURCHASE".equalsIgnoreCase(rule)){
//                    id_btnsankalan.setVisibility(View.VISIBLE);
//                }
//                if("RULE_MILK_SALE".equalsIgnoreCase(rule)){
//                    id_btnsale.setVisibility(View.VISIBLE);
//                }
//                if("RULE_SALE_PASHUKHADYA".equalsIgnoreCase(rule)){
//                    id_btnpashu.setVisibility(View.VISIBLE);
//                }
//                if("RULE_PURCHASE_PASHUKHADYA".equalsIgnoreCase(rule)){
//                    id_btnpashupurchase.setVisibility(View.VISIBLE);
//                }
//                if("RULE_CHALAN".equalsIgnoreCase(rule)){
//                    id_btnchalan.setVisibility(View.VISIBLE);
//                }
//
//            }
//        }

        btnuploadmcollectonexcel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(options.this, UploadMilkPurchaseExcel.class);
                startActivity(i);
            }
        });

        id_btn_aboutUs.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(options.this, Aboutus.class);
                startActivity(i);
            }
        });

        id_btn_aboutUs.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(options.this, Aboutus.class);
                startActivity(i);
            }
        });

//        fab.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Intent i = new Intent(options.this, Summary1.class);
//                startActivity(i);
//            }
//        });

//        id_btnCounter_entry.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Intent i = new Intent(options.this, Menu_Retail_Sale.class);
//                startActivity(i);
//            }
//        });
//
//
//        id_btnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                Intent i = new Intent(options.this, Searchr.class);
//                startActivity(i);
//            }
//        });


        id_btncomplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(options.this, Complaint.class);
                startActivity(i);
            }
        });

        btncustlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(options.this, Custlist.class);
                startActivity(i);
            }
        });


        id_btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(options.this, Notificationlist.class);
                startActivity(i);
            }
        });


//        id_btndealer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(options.this, DealerOrder.class);
//                startActivity(i);
//            }
//        });
//
//
//        id_vieworder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                Intent i = new Intent(options.this, Orderstatus.class);
//                startActivity(i);
//            }
//        });

        id_btnMilkreport.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(options.this, Milkreportlist.class);
                startActivity(i);
            }
        });
        id_btnNewsfeed.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(options.this, Newsoptions.class);
                startActivity(i);
            }
        });

        id_btn_Converter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(options.this, Converters.class);
                startActivity(i);
            }
        });
        id_btn_ledger_bal.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(options.this, Statementlist.class);
                startActivity(i);
            }
        });

        id_btnchalan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(options.this, Addchalanlist.class);
                startActivity(i);
            }
        });

        id_btnpashu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(options.this, Addpashusalelist.class);
                startActivity(i);
            }
        });

        id_btnpashupurchase.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(options.this, Addpashupurchaselist.class);
                startActivity(i);
            }
        });

        id_btnsankalan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(options.this, Addmilkentrylist.class);
                i.putExtra("pageType","A0");
                startActivity(i);
            }
        });

        id_btnsale.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(options.this, Addmilkentrylist.class);
                i.putExtra("pageType","A1");
                startActivity(i);
            }
        });

//        if(ApplicationRuntimeStorage.ORG_TYPE==2 || ApplicationRuntimeStorage.ORG_TYPE==1){
//
////            id_btnCounter_entry.setVisibility(View.INVISIBLE);
////            id_btndealer.setVisibility(View.INVISIBLE);
////            id_vieworder.setVisibility(View.INVISIBLE);
////            id_btnSearch.setVisibility(View.INVISIBLE);
//            id_btncomplaint.setVisibility(View.INVISIBLE);
//            id_btn_ledger_bal.setVisibility(View.VISIBLE);
//
//            if(ApplicationRuntimeStorage.ORG_TYPE==2){
//
//                id_btnMilkreport.setVisibility(View.INVISIBLE);
//                id_btn_payments.setVisibility(View.INVISIBLE);
//
//            }
//
//        }


    }
    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(options.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }


}
