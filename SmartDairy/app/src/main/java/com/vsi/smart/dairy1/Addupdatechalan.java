package com.vsi.smart.dairy1;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class Addupdatechalan extends AppCompatActivity {

    String  glcode,custacccode,narration,amt,cname,glcname,fromdate;
    TextView spncodename,spncodename_gl,txtdate;
    EditText Narration,Amount;
    Calendar c = Calendar.getInstance();
    public final int mYear = c.get(Calendar.YEAR);
    public final int mMonth = c.get(Calendar.MONTH);
    public final int mDay = c.get(Calendar.DAY_OF_MONTH);
    public int pday;
    public int pmonth;
    public int pyear;
    long flagsms =1;
    RadioGroup grpsms;
    Button save, income, expence,delete;
    String vno,acccode;
    public int btn_state = 0;
    ArrayList<String> codelist = new ArrayList<>();
    ArrayList<String> namelist=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_addupdatechalan);
        ActivityCompat.requestPermissions(Addupdatechalan.this,new String[]{Manifest.permission.SEND_SMS}, 1);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view = getSupportActionBar().getCustomView();
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar, null);
        TextView tittle = (TextView) mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Update Transaction");

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                glcode= null;
                custacccode=null;
                narration=null;
                amt=null;
                cname=null;
                glcname=null;fromdate=null;vno=null;

            } else {
                glcode= extras.getString("glcode");
                custacccode= extras.getString("custacccode");
                narration=extras.getString("narration");
                amt=extras.getString("amt");
                cname=extras.getString("cname");
                glcname=extras.getString("glcname");
                fromdate=extras.getString("fromdate");
                vno=extras.getString("vno");
            }
        } else {
            glcode= (String) savedInstanceState.getSerializable("glcode");
            custacccode= (String) savedInstanceState.getSerializable("custacccode");
            narration=(String)savedInstanceState.getSerializable("narration");
            amt=(String)savedInstanceState.getSerializable("amt");
            cname=(String)savedInstanceState.getSerializable("cname");
            glcname=(String)savedInstanceState.getSerializable("glcname");
            fromdate=(String)savedInstanceState.getSerializable("fromdate");
            vno=(String)savedInstanceState.getSerializable("vno");

        }

        Narration=(EditText)findViewById(com.vsi.smart.dairy1.R.id.etxnarr);
        Narration.setText(narration);

        Amount=(EditText)findViewById(com.vsi.smart.dairy1.R.id.etxamnt);
        Amount.setText(amt);

        spncodename = (TextView) findViewById(com.vsi.smart.dairy1.R.id.spncodename);
        spncodename_gl = (TextView) findViewById(com.vsi.smart.dairy1.R.id.spncodename_gl);

        spncodename_gl.setText(glcode+"_"+glcname);
        spncodename.setText(custacccode+"_"+cname);

        txtdate=(TextView)findViewById(com.vsi.smart.dairy1.R.id.txtdate);
        txtdate.setText(fromdate);

        income = (Button) findViewById(com.vsi.smart.dairy1.R.id.btn_income);
        income.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                btn_state = 0;
                income.setBackgroundColor(getResources().getColor(com.vsi.smart.dairy1.R.color.colorPrimary));
                expence.setBackgroundColor(Color.TRANSPARENT);
            }
        });
        expence = (Button) findViewById(com.vsi.smart.dairy1.R.id.btn_expences);
        expence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                btn_state = 1;
                expence.setBackgroundColor(getResources().getColor(com.vsi.smart.dairy1.R.color.colorPrimary));
                income.setBackgroundColor(Color.TRANSPARENT);

            }
        });
        if (btn_state == 0) {
            income.setBackgroundColor(getResources().getColor(com.vsi.smart.dairy1.R.color.colorPrimary));
            expence.setBackgroundColor(Color.TRANSPARENT);

        }
        grpsms=(RadioGroup)findViewById(com.vsi.smart.dairy1.R.id.grpsendsms);
        grpsms.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

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

        delete = (Button) findViewById(com.vsi.smart.dairy1.R.id.btnid_Deletebtn);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Longoperationdelete().execute(custacccode, vno, fromdate, glcode);
              }
        });

        save = (Button) findViewById(com.vsi.smart.dairy1.R.id.btnid_Submitbtn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int vtype = 1;
                if (btn_state == 0) {
                    vtype = 4;  //credit // Income
                } else {
                    vtype = 1;//debit // expenses
                }

                String vdate = txtdate.getText().toString();
                if (vdate.length() == 0) {
                    Toast.makeText(Addupdatechalan.this, "Please Enter Transaction Date", Toast.LENGTH_LONG).show();
                    return;
                }
                String glnme = spncodename_gl.getText().toString();
                String glcode = glnme.split("_")[0];

                String itemnm = spncodename.getText().toString();
                acccode = itemnm.split("_")[0];
                if (null == itemnm) {

                    Toast.makeText(Addupdatechalan.this, "Please Select Transaction name Category", Toast.LENGTH_LONG).show();
                    return;
                }
                if (acccode == "-1") {

                    Toast.makeText(Addupdatechalan.this, "Please Select Transaction Category", Toast.LENGTH_LONG).show();
                    return;
                }

                final EditText narra = (EditText) findViewById(com.vsi.smart.dairy1.R.id.etxnarr);
                String narration = narra.getText().toString();
                if (narra.length() == 0) {
                    Toast.makeText(Addupdatechalan.this, "Please Enter Narration", Toast.LENGTH_LONG).show();
                    return;
                }

                final EditText amt = (EditText) findViewById(com.vsi.smart.dairy1.R.id.etxamnt);
                String amt1 = amt.getText().toString();

                double a = 0;
                try {
                    a = Double.parseDouble(amt1);
                } catch (Exception dg) {
                    a = 0;
                }
                if (amt1.length() == 0) {
                    Toast.makeText(Addupdatechalan.this, "Please Enter Amount", Toast.LENGTH_LONG).show();
                    return;
                }

                if (a < 0) {
                    Toast.makeText(Addupdatechalan.this, "Please Enter positive number Amount ", Toast.LENGTH_LONG).show();
                    return;
                }
                if (a == 0) {
                    Toast.makeText(Addupdatechalan.this, "Please Enter Amount greater than 0", Toast.LENGTH_LONG).show();
                    return;
                }
                String glname11 = glnme.substring(1+glnme.indexOf("_"),glnme.length());
                // CallSoap cs = new CallSoap();
                // companyid,  userid,  acccode,  vno,  vdate,  cddr,  amt,  narration,  glcode
                // String RESULT = cs.SaveTransaction("" + ApplicationRuntimeStorage.COMPANYID, ApplicationRuntimeStorage.USERID, "" + acccode, vno, vdate, "" + vtype, "" + amt1, narration, "" + glcode);
                new LongOperation_savetrans().execute("" + acccode.trim(), vno, vdate, "" + vtype, "" + amt1, narration, "" + glcode,""+glname11.trim());
            }
        });


        if(ApplicationRuntimeStorage.AccessRights.contains("RULE_UPDATE_CHALAN")) {
            save.setVisibility(View.VISIBLE);
        }else{
            save.setVisibility(View.INVISIBLE);
        }
        if(ApplicationRuntimeStorage.AccessRights.contains("RULE_DELETE_CHALAN")) {
            delete.setVisibility(View.VISIBLE);
        }else{
            delete.setVisibility(View.INVISIBLE);
        }


    }
    private class Longoperationdelete extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... params)
        {
            CallSoap cs1=new CallSoap();
            String result=cs1.DeleteTransaction(ApplicationRuntimeStorage.COMPANYID,ApplicationRuntimeStorage.USERID,params[0],params[1],params[2],params[3]);
            return result;
        }

        ProgressDialog asyncDialog = new ProgressDialog(Addupdatechalan.this);
        @Override
        protected void onPreExecute() {
            //set message of the dialog
            asyncDialog.setMessage("Deleting...");
            //show dialog
            asyncDialog.show();
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result)
        {
            //hide the dialog
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }
    }
    private class LongOperation_savetrans extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            CallSoap cs=new CallSoap();

            // companyid,  userid,  acccode,  vno,  vdate,  cddr,  amt,  narration,  glcode
            String RESULT = cs.SaveTransaction("" + ApplicationRuntimeStorage.COMPANYID, ApplicationRuntimeStorage.USERID, "" + params[0], params[1], params[2], params[3], params[4], params[5], params[6],params[7]);

            return RESULT;
        }

        @Override
        protected void onPostExecute(String RESULT) {
            try {
                final EditText narra = (EditText) findViewById(com.vsi.smart.dairy1.R.id.etxnarr);
                final EditText amt = (EditText) findViewById(com.vsi.smart.dairy1.R.id.etxamnt);
                String amt1 = amt.getText().toString();
                String glnme = spncodename_gl.getText().toString();
                Toast.makeText(Addupdatechalan.this, RESULT, Toast.LENGTH_LONG).show();

                int vtype = 1;
                if (btn_state == 0) {
                    vtype = 4;  //credit // Income
                } else {
                    vtype = 1;//debit // expenses
                }

                if ("SUCCESS".equalsIgnoreCase(RESULT)) {
                    Toast.makeText(Addupdatechalan.this,RESULT, Toast.LENGTH_LONG).show();


                    if (flagsms == 0) {
                        SmsManager sms = SmsManager.getDefault();
                        long acccode_curr=0;
                        try{
                            acccode_curr = Integer.parseInt(acccode.trim());

                        }catch (Exception ew){

                        }
                        String glname11 = glnme.substring(1+glnme.indexOf("_"),glnme.length());
                        for (MilkCustomer mc : ApplicationRuntimeStorage.LIST_CUSTOMERS) {
                            long code11 = 0;
                            try {
                                code11 = Long.parseLong(mc.code.trim());
                            } catch (Exception e) {
                            }
                            if (code11 == acccode_curr) {

                                String vtypestr = "जमा केली आहे.";
                                if(vtype==1){
                                    vtypestr = "नावे टाकली आहे.";
                                }
                                String smsText = " प्रिय ग्राहक - "+mc.cname.trim() + " - आपली रक्कम रु " + amt1.trim()+ ", " + glnme.trim()+ " ला "+narra.getText().toString().trim()+" प्रमाणे आपले समक्ष " + vtypestr.trim() + ", धन्यवाद .";

                                String mobile11 = mc.mobile;
                                if (mobile11.length() == 10) {
                                    sms.sendTextMessage(mobile11, null, smsText, null, null);
                                }

                            }
                        }
                    }
                    amt.setText("");
                    narra.setText("");
                } else {
                    Toast.makeText(Addupdatechalan.this, RESULT, Toast.LENGTH_LONG).show();
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            //hide the dialog
            asyncDialog.dismiss();
            finish();
            super.onPostExecute(RESULT);
        }
        ProgressDialog asyncDialog = new ProgressDialog(Addupdatechalan.this);
        @Override
        protected void onPreExecute() {
            //set message of the dialog
            asyncDialog.setMessage("Saving transaction...");
            //show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Addupdatechalan.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}