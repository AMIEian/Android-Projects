package com.vsi.smart.dairy1;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class Addmilkentry extends AppCompatActivity {

    String code,lit,fat,snf,clr,rate,amount,puramount;
    EditText etxcode,etxlit,etxfat,etxsnf,etxclr,etxrate,etxamount,etxpuramount;
    TextView txtdate,txtcustcode;
    String datefrom;
    long flaganimal,flagtime,flagsms=1,flagcmethod;
    RadioButton rdcow,rdbuffalo,rdmorning,rdevening,rdprevfatsnf,rd10dayavg,rdna;
    RadioGroup grptime,grpanimal,grpsms,grp_collection_method,grp_prev_quality_method;
    Button save,buttonrate; JSONArray jsonArray;
    LinearLayout mLinearLayout;
    String pageType="A0";
    Databasehelper1 mydb;

    int prevqualitymethod= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_addmilkentry);
        ActivityCompat.requestPermissions(Addmilkentry.this,new String[]{Manifest.permission.SEND_SMS}, 1);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view = getSupportActionBar().getCustomView();
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar, null);
        TextView tittle = (TextView) mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Milk Purchase Entry");

        txtdate=(TextView)findViewById(com.vsi.smart.dairy1.R.id.txtdate);
        java.util.Calendar c = java.util.Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formatteddate = df.format(c.getTime());
        txtdate.setText(formatteddate);

        mydb = new Databasehelper1(this);

        try {
            Bundle bundle = getIntent().getExtras();
            pageType = bundle.getString("pageType");
            txtdate.setText(bundle.getString("Date",txtdate.getText().toString()));
            flagtime = bundle.getLong("ME",0);


        }catch (Exception er){
            pageType = "A0";

        }
        if("A1".equalsIgnoreCase(pageType)) {
            tittle.setText("Milk Sale Entry");
        }
        ImageButton imgbutton = (ImageButton) mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setImageResource(com.vsi.smart.dairy1.R.drawable.ic_event_note_black_24dp);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLinearLayout.getVisibility()==View.GONE){
                    expand();
                }else{
                    collapse();
                }
            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        grpanimal=(RadioGroup)findViewById(com.vsi.smart.dairy1.R.id.grpanimal);
        grptime=(RadioGroup)findViewById(com.vsi.smart.dairy1.R.id.grptime);
        grp_collection_method=(RadioGroup)findViewById(com.vsi.smart.dairy1.R.id.collection_method);

        grp_prev_quality_method = (RadioGroup)findViewById(com.vsi.smart.dairy1.R.id.grp_prev_quality_method);
        grpsms=(RadioGroup)findViewById(com.vsi.smart.dairy1.R.id.grpsendsms);
        RadioButton btn_rdevening=(RadioButton)findViewById(com.vsi.smart.dairy1.R.id.rdevening);
        RadioButton btn_rdmorning=(RadioButton)findViewById(com.vsi.smart.dairy1.R.id.rdmorning);



        if(flagtime==1)
        {
            btn_rdevening.setChecked(true);
            btn_rdevening.setVisibility(View.VISIBLE);
            btn_rdmorning.setVisibility(View.INVISIBLE);
        }else {
            btn_rdmorning.setChecked(true);
            btn_rdmorning.setVisibility(View.VISIBLE);
            btn_rdevening.setVisibility(View.INVISIBLE);
        }



        rdna=(RadioButton)findViewById(R.id.id_NA);
        rdprevfatsnf=(RadioButton)findViewById(R.id.id_prevfatsnf);
        rd10dayavg=(RadioButton)findViewById(R.id.id_avgfatsnf);

        if(rdna.isChecked()){
            prevqualitymethod = 0;
        }else if(rd10dayavg.isChecked()){
            prevqualitymethod = 2;
        }else if(rdprevfatsnf.isChecked()){
            prevqualitymethod = 1;
        }


        // grptime.setEnabled(false);

        mLinearLayout = (LinearLayout) findViewById(com.vsi.smart.dairy1.R.id.expandable);
        //set visibility to GONE
        mLinearLayout.setVisibility(View.GONE);

        tittle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { }
        });

        datefrom=txtdate.getText().toString();
        etxcode=(EditText)findViewById(com.vsi.smart.dairy1.R.id.etxcode);
        etxlit=(EditText)findViewById(com.vsi.smart.dairy1.R.id.etxlit);
        etxfat=(EditText)findViewById(com.vsi.smart.dairy1.R.id.etxfat);
        etxsnf=(EditText)findViewById(com.vsi.smart.dairy1.R.id.etxsnf);
        etxclr=(EditText)findViewById(com.vsi.smart.dairy1.R.id.etxclr);
        etxrate=(EditText)findViewById(com.vsi.smart.dairy1.R.id.etxrate);
        etxamount=(EditText)findViewById(com.vsi.smart.dairy1.R.id.etxamount);
        etxpuramount=(EditText)findViewById(com.vsi.smart.dairy1.R.id.etxpuramount);

        buttonrate=(Button) findViewById(com.vsi.smart.dairy1.R.id.buttonrate);

        if("A0".equalsIgnoreCase(pageType)) {

            etxrate.setEnabled(false);
            etxsnf.setEnabled(false);
        }
        etxamount.setEnabled(false);
        save=(Button)findViewById(com.vsi.smart.dairy1.R.id.btn_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new LongOperation_save().execute("");
                   // call save asynk task
            }
        });

        grpsms.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId)
            {
                View radiobutton=grpsms.findViewById(checkedId);
                int index=grpsms.indexOfChild(radiobutton);
                if (index==0)
                {
                    flagsms=0;
                }
                else
                {
                    flagsms=1;
                }
            }
        });

        grp_collection_method.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId)
            {
                View radiobutton=grp_collection_method.findViewById(checkedId);
                int index=grp_collection_method.indexOfChild(radiobutton);
                if (index==0)
                {
                    flagcmethod=0;
                    etxsnf.setEnabled(false);
                    etxclr.setEnabled(true);
                }
                else
                {
                    flagcmethod=1;
                    etxsnf.setEnabled(true);
                    etxclr.setEnabled(false);
                }
            }
        });


        grp_prev_quality_method.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId)
            {
                View radiobutton=grp_prev_quality_method.findViewById(checkedId);
                int index=grp_prev_quality_method.indexOfChild(radiobutton);

                if (index==0)
                {
                    prevqualitymethod=0;
                    etxfat.setEnabled(true);
                    if(flagcmethod==0){
                        etxsnf.setEnabled(false);
                        etxclr.setEnabled(true);
                    }else  if(flagcmethod==0){
                        etxsnf.setEnabled(true);
                        etxclr.setEnabled(false);
                    }

                }else  if (index==1)
                {
                    prevqualitymethod=1;
                    etxfat.setEnabled(false);
                    etxsnf.setEnabled(false);
                    etxclr.setEnabled(false);
                }
                else
                {
                    prevqualitymethod=2;
                    etxfat.setEnabled(false);
                    etxsnf.setEnabled(false);
                    etxclr.setEnabled(false);
                }
            }
        });
        grptime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId)
            {
                View radiobutton=grptime.findViewById(checkedId);
                int index=grptime.indexOfChild(radiobutton);
                if (index==0)
                {
                    flagtime=0;
                }
                else
                {
                    flagtime=1;
                }
            }
        });

        grpanimal.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId)
            {
                View radiobutton=grpanimal.findViewById(checkedId);
                int index=grpanimal.indexOfChild(radiobutton);
                if (index==0)
                {
                    flaganimal=0;
                }
                else
                {
                    flaganimal=1;
                }
            }
        });

        txtcustcode=(TextView) findViewById(com.vsi.smart.dairy1.R.id.txtcustcode);
        etxcode.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //here is your code
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                Searchname();
            }
        });


        buttonrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                long code =0  ;try{code= Long.parseLong(etxcode.getText().toString());}catch (Exception e){}
                double liter =0  ;try{liter= Double.parseDouble(etxlit.getText().toString());}catch (Exception e){}
                double fat =0  ;try{fat= Double.parseDouble(etxfat.getText().toString());}catch (Exception e){}
                double clr =0  ;
                double snf =0  ;
                DecimalFormat f = new DecimalFormat("##.00");

                if(flagcmethod==0) {
                    try{clr= Double.parseDouble(etxclr.getText().toString());}catch (Exception e){}
                    etxsnf.setText("" + getSNF(clr, fat));
                }else{
                    try{snf= Double.parseDouble(etxsnf.getText().toString());}catch (Exception e){}
                    etxclr.setText("" + getCLR(snf, fat));
                    try{clr= Double.parseDouble(etxclr.getText().toString());}catch (Exception e){}
                }


                try{snf= Double.parseDouble(etxsnf.getText().toString());}catch (Exception e){}

                etxamount.setText("0");
                new LongOperation().execute(""+code,""+fat,""+snf,""+clr);
                if("A0".equalsIgnoreCase(pageType)) {
                    save.setVisibility(View.INVISIBLE);
                }
            }
        });

        new LongOperation_1().execute("");

        etxlit.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //here is your code
                }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                calculateAmt();
            }
        });

        etxfat.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //here is your code
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                calculateRate();
            }
        });

        etxsnf.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //here is your code
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if(flagcmethod==1){
                    calculateRate();
                }else {
                    calculateAmt();
                }
            }
        });

        etxrate.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //here is your code
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s)
            {
                // TODO Auto-generated method stub
                    calculateAmt();
            }
        });

        etxclr.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //here is your code
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s)
            {
                // TODO Auto-generated method stub
                if(flagcmethod==1){
                    calculateAmt();
                }else {
                    calculateRate();
                }
            }
        });
        try
        {
            etxcode.requestFocus();
        }catch (Exception e){ }
    }

    private void Searchname()
    {

    }

    private void expand()
    {
        //set Visible
        mLinearLayout.setVisibility(View.VISIBLE);
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mLinearLayout.measure(widthSpec, heightSpec);

        ValueAnimator mAnimator = slideAnimator(0, mLinearLayout.getMeasuredHeight());
        mAnimator.start();
    }

    private void collapse() {
        int finalHeight = mLinearLayout.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                mLinearLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }

        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end)
    {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mLinearLayout.getLayoutParams();
                layoutParams.height = value;
                mLinearLayout.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private class LongOperation_1 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if("A0".equalsIgnoreCase(pageType)) {
                try {
                    CallSoap cs = new CallSoap();
                    String JSON_RESULT = cs.CustomerList(ApplicationRuntimeStorage.COMPANYID,ApplicationRuntimeStorage.USERID,"0");
                    return JSON_RESULT;
                }catch (Exception ert){
                    return  "0";
                }
            }else
                {
                try {
                    CallSoap cs = new CallSoap();
                    String JSON_RESULT = cs.CustomerList(ApplicationRuntimeStorage.COMPANYID,ApplicationRuntimeStorage.USERID,"2,5");
                    return JSON_RESULT;
                }catch (Exception ert){
                    return  "0";
                }
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            try{
                List<MilkCustomer> list = ApplicationRuntimeStorage.LIST_CUSTOMERS;
                list.clear();
                JSONArray jsonarray = new JSONArray(result);
                for (int i = 0; i < jsonarray.length(); i++) {
                    MilkCustomer cust=  new MilkCustomer();
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String code = jsonobject.getString("code");
                    String cname = jsonobject.getString("cname");
                    String mobile = jsonobject.getString("mobile");
                    cust.cname = cname;
                    cust.code=code;
                    cust.mobile=mobile;

                    list.add(cust);
                }
            }catch (Exception e) {e.printStackTrace();}
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if("A0".equalsIgnoreCase(pageType))
            {
                try {
                    long code = 0;
                    try {
                        code = Long.parseLong(params[0]);
                    } catch (Exception e) {
                    }
                    double fat = 0;
                    try {
                        fat = Double.parseDouble(params[1]);
                    } catch (Exception e) {
                    }
                    double snf = 0;
                    try {
                        snf = Double.parseDouble(params[2]);
                    } catch (Exception e) {
                    }
                    double clr = 0;
                    try {
                        clr = Double.parseDouble(params[3]);
                    } catch (Exception e) {
                    }

                    jsonArray = new JSONArray();
                    JSONObject student2 = new JSONObject();
                    try {
                        student2.put("companyid", ApplicationRuntimeStorage.COMPANYID);
                        student2.put("userid", ApplicationRuntimeStorage.USERID);
                        student2.put("acccode",""+code);
                        student2.put("fat",""+fat);
                        student2.put("snf",""+snf);
                        student2.put("clr",""+clr);
                        student2.put("bysnf","0");
                        student2.put("cb",flaganimal );
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                        jsonArray.put(student2);

                    CallSoap cs = new CallSoap();
//                    String result = cs.GetMilkRateForQuality_New(ApplicationRuntimeStorage.COMPANYID,
//                            ApplicationRuntimeStorage.USERID,
//                            ""+code, ""+fat,""+ snf, ""+clr, ""+0, ""+flaganimal);

                    if(prevqualitymethod == 0) {  //as per entered quality
                        String result = cs.GetMilkRateForQuality("1", jsonArray.toString());
//                     String result = cs.GetMilkRateForQuality_New("1");

                        double rate = 0;
                        try {
                            rate = Double.parseDouble(result);
                        } catch (Exception e) {
                            rate = 0;
                        }

                        if (rate != 0) {
                            return "" + rate;
                            // Toast.makeText(Addmilkentry.this,code+ "  -  "+result, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Addmilkentry.this, code + "  -  " + result, Toast.LENGTH_SHORT).show();
                            return "0";
                        }
                    }else{
                        String result = "";
                        if(prevqualitymethod==1) {
                            result = cs.GetCustomerPreviousFatSNF(ApplicationRuntimeStorage.COMPANYID, ""+code, ApplicationRuntimeStorage.CENTERID, 1);
                        }else{
                            result = cs.GetCustomerPreviousFatSNF(ApplicationRuntimeStorage.COMPANYID, ""+code, ApplicationRuntimeStorage.CENTERID, 10);
                        }

                           if(result == null || result.contains("Exception") || !result.contains("#") ){
                               return "0";

                           } else {
                                String[] values = result.split("#");
                                String fat_ =  values[0];
                                etxfat.setText(fat_);
                               String snf_ =  values[1];
                               etxsnf.setText(snf_);

                               return ""+values[2];
                           }


                    }
                } catch (Exception ert) {
                    return "0";
                }
            } else { return "0"; }
        }

        @Override
        protected void onPostExecute(String result)
        {
           // EditText etxrate = (EditText) findViewById(com.vsi.smart.dairy1.R.id.etxrate);
            etxrate.setText(result);
            double rate =0  ;try{rate= Double.parseDouble(result);}catch (Exception e){}

            if(rate > 0) {
                Button save = (Button) findViewById(com.vsi.smart.dairy1.R.id.btn_save);
               // if("A0".equalsIgnoreCase(pageType)) {
                    save.setVisibility(View.VISIBLE);
            }
            calculateAmt();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private void calculateRate()
    {
        long code =0  ;try{code= Long.parseLong(etxcode.getText().toString());}catch (Exception e){}
        double liter =0  ;try{liter= Double.parseDouble(etxlit.getText().toString());}catch (Exception e){}
        double fat =0  ;try{fat= Double.parseDouble(etxfat.getText().toString());}catch (Exception e){}
        double clr =0  ;
        double snf =0  ;
        DecimalFormat f = new DecimalFormat("##.00");
        if(flagcmethod==0) {
            try{clr= Double.parseDouble(etxclr.getText().toString());}catch (Exception e){}
            etxsnf.setText("" + getSNF(clr, fat));
        }else{
            try{snf= Double.parseDouble(etxsnf.getText().toString());}catch (Exception e){}
            etxclr.setText("" + getCLR(snf, fat));
            try{clr= Double.parseDouble(etxclr.getText().toString());}catch (Exception e){}
        }
        try{snf= Double.parseDouble(etxsnf.getText().toString());}catch (Exception e){}
        etxamount.setText("0");
        if("A0".equalsIgnoreCase(pageType)) {
            save.setVisibility(View.INVISIBLE);
        }
      //  new LongOperation().execute(""+code,""+fat,""+snf,""+clr);
    }

    private void calculateAmt()
    {
        long code =0  ;try{code= Long.parseLong(etxcode.getText().toString());}catch (Exception e){}
        double liter =0  ;try{liter= Double.parseDouble(etxlit.getText().toString());}catch (Exception e){}
        double fat =0  ;try{fat= Double.parseDouble(etxfat.getText().toString());}catch (Exception e){}
        double snf =0  ;try{snf= Double.parseDouble(etxsnf.getText().toString());}catch (Exception e){}
        double clr =0  ;try{clr= Double.parseDouble(etxclr.getText().toString());}catch (Exception e){}
        //etxrate.setText("23.20");
        double rate =0  ;try{rate= Double.parseDouble(etxrate.getText().toString());}catch (Exception e){}
        DecimalFormat f = new DecimalFormat("##.00");
        etxamount.setText(""+f.format(rate*liter));
    }

    private String getSNF(double clr,double fat){
        DecimalFormat f = new DecimalFormat("##.00");
        double snf = 0;
        try {
            double R_method_snf = (double) ((clr / 4) + (0.21 * fat) + 0.36);

            double zeelSNF = (double) ((clr * (0.25)) + (0.2 * fat) + 0.50);
            snf = (double) ((zeelSNF + R_method_snf) / 2);

            String indata = ("" + snf);
            int po = indata.indexOf(".");
            String ssd = "";
            String ssd1 = "";
            if (po > 0) {
                ssd = indata.substring(po, po + 2);
                ssd1 = indata.substring(0, 2);
            } else {
                ssd1 = indata;
            }
            String ssss = (ssd1 + ssd).replace("..", ".");
            if (ssss.startsWith(".")) return "0";
            if (ssss.contains(".")) {
                snf = Double.parseDouble(ssss);

            } else {
                try { snf = Double.parseDouble(ssss); } catch(Exception e) { }
            }
        }catch (Exception e){ }

        return ""+snf;
    }

    private double getCLR(double snf,double fat)
    {
        try
        {
            DecimalFormat f = new DecimalFormat("##.00");
            double R_method_clr = (snf - 0.36 - (0.21 * fat)) * 4;
            double zeelSNF_clr = 0;// (snf - 0.50 - (0.2 * fat)) / 0.25;
            double clr_1 = Double.parseDouble(f.format( (R_method_clr+0.0001))); //Math.round((double) (R_method_clr) * 100) / 100; //(zeelSNF_clr + R_method_clr) / 2

            if (clr_1 < 0) clr_1 = 0;
            return clr_1;

        }
        catch (Exception er){ }

        return 0;
    }

    private class LongOperation_save extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String RESULT = "";
            return RESULT;
        }

        @Override
        protected void onPostExecute(String result)
        {
            try
            {
                long code =0  ;try{code= Long.parseLong(etxcode.getText().toString());}catch (Exception e){}
                double lit =0  ;try{lit= Double.parseDouble(etxlit.getText().toString());}catch (Exception e){}
                double fat =0  ;try{fat= Double.parseDouble(etxfat.getText().toString());}catch (Exception e){}
                double snf =0  ;try{snf= Double.parseDouble(etxsnf.getText().toString());}catch (Exception e){}
                double clr =0  ;try{clr= Double.parseDouble(etxclr.getText().toString());}catch (Exception e){}
                double rate =0  ;try{rate= Double.parseDouble(etxrate.getText().toString());}catch (Exception e){}
                double amount =0  ;try{amount= Double.parseDouble(etxamount.getText().toString());}catch (Exception e){}
                double puramount =0  ;try{puramount= Double.parseDouble(etxpuramount.getText().toString());}catch (Exception e){}

                CallSoap cs = new CallSoap();
                if("A0".equalsIgnoreCase(pageType))
                {
                    String RESULT = cs.AddMilkEntry(ApplicationRuntimeStorage.COMPANYID,
                            ApplicationRuntimeStorage.USERID,
                            code, lit, fat, snf, clr, rate, amount, datefrom, flagtime, flaganimal, puramount);
//                    boolean isInserted = false;
//                    try {
//                        isInserted = mydb.insertData(""+code, ""+lit,""+fat ,""+snf, ""+clr,""+ rate,
//                                ""+amount,datefrom,""+flagtime, ""+flaganimal,""+puramount,"A0",
//                                ApplicationRuntimeStorage.USERID,""+ApplicationRuntimeStorage.COMPANYID,"0");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if (isInserted == true)
//                    {
//                        Toast.makeText(Addmilkentry.this, "Data Saved", Toast.LENGTH_LONG).show();
//                        //onBackPressed();
//                    }
//                    else
//                    {
//                        Toast.makeText(Addmilkentry.this, "Data not Saved", Toast.LENGTH_LONG).show();
//                    }

//                    if (isInserted == true) {
//                    //    Toast.makeText(Addmilkentry.this, code + "  -  " + RESULT, Toast.LENGTH_LONG).show();

                    if (flagsms == 0)
                    {
                            SmsManager sms = SmsManager.getDefault();
                            for (MilkCustomer mc : ApplicationRuntimeStorage.LIST_CUSTOMERS) {
                                long code11 = 0;
                                try
                                {
                                    code11 = Long.parseLong(mc.code);
                                } catch (Exception e) { }

                                if (code11 == code)
                                {
                                    String mestreng = "MOR";
                                    if (flagtime == 1) {
                                        mestreng = "EVE";
                                    }
                                    String smsText = mc.code + " - " + mestreng + " - " + datefrom + "( Liter : " + lit + ", FAT : " + fat + ", SNF : " + snf + ", Rate : " + rate + ", Amt : " + amount + " )";

                                    String mobile11 = mc.mobile;
                                    if (mobile11.length() == 10) {
                                        //  sms.sendTextMessage(mobile11, null, smsText, null, null);
                                        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("content://com.android.contacts/data/" + mobile11));
                                        i.setType("text/plain");
                                        i.setPackage("com.whatsapp");           // so that only Whatsapp reacts and not the chooser
                                        i.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                                        i.putExtra(Intent.EXTRA_TEXT, smsText);
                                        startActivity(i);
                                    }

                                }
                        }

                    }
                    else {
                  //      Toast.makeText(Addmilkentry.this, code + "  -  " + RESULT, Toast.LENGTH_LONG).show();
                      //  Toast.makeText(Addmilkentry.this, code + " - No Watsapp", Toast.LENGTH_LONG).show();
                    }

                    etxcode.setText("");
                    etxlit.setText("");
                    etxfat.setText("");
                    etxsnf.setText("");
                    etxclr.setText("");
                    etxrate.setText("");
                    etxamount.setText("");
                    etxpuramount.setText("");
                    etxcode.requestFocus();

                }else{
//                    boolean isInserted = false;
//                    try {
//                        isInserted = mydb.insertData(""+code, ""+lit,""+fat ,""+snf, ""+clr,""+ rate,
//                                ""+amount,datefrom,""+flagtime, ""+flaganimal,""+puramount,"A1",
//                                ApplicationRuntimeStorage.USERID,""+ApplicationRuntimeStorage.COMPANYID,"0");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if (isInserted == true)
//                    {
//                        Toast.makeText(Addmilkentry.this, "Data Saved", Toast.LENGTH_LONG).show();
//                        //onBackPressed();
//                    }
//                    else
//                    {
//                        Toast.makeText(Addmilkentry.this, "Data not Saved", Toast.LENGTH_LONG).show();
//                    }
//
                    String RESULT = cs.AddMilkEntry_sale(ApplicationRuntimeStorage.COMPANYID, ApplicationRuntimeStorage.USERID, code, lit, fat, snf, clr, rate, amount, datefrom, flagtime, flaganimal, puramount);
//                    if (isInserted==true) {
//                      //  Toast.makeText(Addmilkentry.this, code + "  -  " + RESULT, Toast.LENGTH_LONG).show();

                        if (flagsms == 0) {
                            SmsManager sms = SmsManager.getDefault();

                            for (MilkCustomer mc : ApplicationRuntimeStorage.LIST_CUSTOMERS) {
                                long code11 = 0;
                                try {
                                    code11 = Long.parseLong(mc.code);
                                } catch (Exception e) { }

                                if (code11 == code)
                                {
                                    String mestreng = "MOR";
                                    if (flagtime == 1) {
                                        mestreng = "EVE";
                                    }
                                    String smsText = mc.code + " - " + mestreng + " - " + datefrom + "( Liter : " + lit + ", FAT : " + fat + ", SNF : " + snf + ", Rate : " + rate + ", Amt : " + amount + " )";

                                    String mobile11 = mc.mobile;
                                    if (mobile11.length() == 10) {
     //                                   sms.sendTextMessage(mobile11, null, smsText, null, null);
                                        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("content://com.android.contacts/data/" + mobile11));
                                        i.setType("text/plain");
                                        i.setPackage("com.whatsapp");           // so that only Whatsapp reacts and not the chooser
                                        i.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                                        i.putExtra(Intent.EXTRA_TEXT, smsText);
                                        startActivity(i);
                                    }
                            }
                        }


                    } else {
                      //  Toast.makeText(Addmilkentry.this, code + "  -  " + RESULT, Toast.LENGTH_LONG).show();
                      //  Toast.makeText(Addmilkentry.this, code + " - No watsapp", Toast.LENGTH_LONG).show();
                    }

                    etxcode.setText("");
                    etxlit.setText("");
                    etxfat.setText("");
                    etxsnf.setText("");
                    etxclr.setText("");
                    etxrate.setText("");
                    etxamount.setText("");
                    etxpuramount.setText("");
                    etxcode.requestFocus();

                }
            }catch (Exception e) {e.printStackTrace();}
            //hide the dialog
            asyncDialog.dismiss();

            super.onPostExecute(result);
        }
        ProgressDialog asyncDialog = new ProgressDialog(Addmilkentry.this);
        @Override
        protected void onPreExecute()
        {
            //set message of the dialog
            asyncDialog.setMessage("Saving...");
            //show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

//    private class LongOperation_rate extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            String RESULT = "";
//            return RESULT;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            try {
//
//            }catch (Exception e){e.printStackTrace();}
//            //hide the dialog
//            asyncDialog.dismiss();
//
//            super.onPostExecute(result);
//        }
//        ProgressDialog asyncDialog = new ProgressDialog(Addmilkentry.this);
//        @Override
//        protected void onPreExecute()
//        {
//            //set message of the dialog
//            asyncDialog.setMessage("Saving...");
//            //show dialog
//            asyncDialog.show();
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... values) {}
//    }


    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Addmilkentry.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
