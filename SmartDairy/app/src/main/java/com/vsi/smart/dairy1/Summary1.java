package com.vsi.smart.dairy1;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Summary1 extends AppCompatActivity
{
    int val=10;
    RadioGroup grptype,grptime;
    TextView single_textviewliter,single_textviewfat,single_textviewsnf,single_textviewrate,single_textviewamount;
    TextView single_textviewliterbuff,single_textviewfatbuff,single_textviewsnfbuff,single_textviewratebuff,
            single_textviewamountbuff;
    TextView txtdate; ImageButton btndate;Button btnsearch;
    FloatingActionButton fabsumm2,fab;
    public Calendar c = Calendar.getInstance();
    public final int mYear = c.get(Calendar.YEAR);
    public final int mMonth = c.get(Calendar.MONTH);
    public final int mDay = c.get(Calendar.DAY_OF_MONTH);
    public int i;
    long timetype,typetype;
    public int pday;
    public int pmonth;
    public int pyear;
    private ArrayList<Person> arrayPerson = new ArrayList<>();
    private class Person {
        public String branch;
        public String me;
        public String type;
        public String liter;
        public String fat;
        public String snf;
        public String rate;
        public String amount;
        public String ReceiptDate;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_summary1);

        single_textviewliter=(TextView)findViewById(com.vsi.smart.dairy1.R.id.single_textviewliter);
        single_textviewfat=(TextView)findViewById(com.vsi.smart.dairy1.R.id.single_textviewfat);
        single_textviewsnf=(TextView)findViewById(com.vsi.smart.dairy1.R.id.single_textviewsnf);
        single_textviewrate=(TextView)findViewById(com.vsi.smart.dairy1.R.id.single_textviewrate);
        single_textviewamount=(TextView)findViewById(com.vsi.smart.dairy1.R.id.single_textviewamount);
        single_textviewliterbuff=(TextView)findViewById(com.vsi.smart.dairy1.R.id.single_textviewliterbuff);
        single_textviewfatbuff=(TextView)findViewById(com.vsi.smart.dairy1.R.id.single_textviewfatbuff);
        single_textviewsnfbuff=(TextView)findViewById(com.vsi.smart.dairy1.R.id.single_textviewsnfbuff);
        single_textviewratebuff=(TextView)findViewById(com.vsi.smart.dairy1.R.id.single_textviewratebuff);
        single_textviewamountbuff=(TextView)findViewById(com.vsi.smart.dairy1.R.id.single_textviewamountbuff);

        txtdate=(TextView)findViewById(com.vsi.smart.dairy1.R.id.txtdate);
        btndate=(ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate);

        btnsearch=(Button) findViewById(com.vsi.smart.dairy1.R.id.btnsearch);
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListdata();
            }
        });

        fab=(FloatingActionButton)findViewById(com.vsi.smart.dairy1.R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Summary1.this, Mainoptions.class);
                startActivity(intent);
            }
        });

        fabsumm2=(FloatingActionButton)findViewById(com.vsi.smart.dairy1.R.id.fabsumm2);
        fabsumm2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Summary1.this, Summary2.class);
                startActivity(intent);
            }
        });

        grptime=(RadioGroup)findViewById(com.vsi.smart.dairy1.R.id.grptime);
        grptime.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                        View radiobutton=grptime.findViewById(checkedId);
                        int index=grptime.indexOfChild(radiobutton);
                        if (index==0)
                        {
                            timetype=0;
                        }
                        else if(index==1)
                        {
                            timetype=1;
                        }
                    }
                });

        grptype=(RadioGroup)findViewById(com.vsi.smart.dairy1.R.id.grptype);
        grptype.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                        View radiobutton=grptype.findViewById(checkedId);
                        int index=grptype.indexOfChild(radiobutton);
                        if (index==0)
                        {
                            typetype=0;
                        }
                        else if(index==1)
                        {
                            typetype=5;
                        }
                    }
                });


        Calendar c= Calendar.getInstance();
        SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
        String formatteddate=df.format(c.getTime());
        txtdate.setText(formatteddate);

        btndate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Summary1.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                txtdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                pyear = year;
                                pmonth = monthOfYear;
                                pday = dayOfMonth;

                                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                Date date1 = new Date(year - 1900, (monthOfYear), dayOfMonth);
                                String currdate = "";
                                currdate = df.format(date1.getTime());
                                txtdate.setText(currdate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

        @Override
        protected void onResume() {
            if(ApplicationRuntimeStorage.USERID.equals("0"))
            {
                Intent intent=new Intent(Summary1.this,Login.class);
                startActivity(intent);
            }else{showListdata();}
            super.onResume();
        }

    private void showListdata() {
        String todate = txtdate.getText().toString();
        String fromdate = txtdate.getText().toString();
        new LongOperation_Marketing().execute(ApplicationRuntimeStorage.USERID, todate, fromdate);
    }

    private class LongOperation_Marketing extends AsyncTask<String, String, String> {
        String user, pass;

        @Override
        protected String doInBackground(String... params)
        {
            CallSoap cs = new CallSoap();
            String JSON_RESULT = cs.GetMilkReport(ApplicationRuntimeStorage.COMPANYID, ApplicationRuntimeStorage.USERID, txtdate.getText().toString(),
                    txtdate.getText().toString(),"0"); // Web Call to populate JSON ItemList
            return JSON_RESULT;
        }

        @Override
        protected void onPostExecute(String result) {
            asyncDialog.dismiss();
            try {
                ApplicationRuntimeStorage.MARKETING_TEAM_LIST = result;
                listViewUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //  txtcount.setText("Total No of Records = " + arrayPerson.size());
            //hide the dialog

            super.onPostExecute(result);
        }
        ProgressDialog asyncDialog = new ProgressDialog(Summary1.this);
        @Override
        protected void onPreExecute() {
            //set message of the dialog
            asyncDialog.setMessage("Loading...");
            //show dialog
            asyncDialog.show();
            super.onPreExecute();
        }
    }

    private void listViewUpdate()
    {
        String JSON_RESULT = "";
        JSON_RESULT = ApplicationRuntimeStorage.MARKETING_TEAM_LIST;
        arrayPerson.clear();
        try
        {
            arrayPerson.clear();
            JSONArray jsonarray = new JSONArray(JSON_RESULT);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                Person p = new Person();
                final String branch = jsonobject.getString("branch");
                p.branch = branch;
                final String type = jsonobject.getString("type");
                try {
                    p.type =  type;
                }catch (Exception r){

                }
                String liter = jsonobject.getString("liter");
                try {
                    p.liter =String.format("%.02f", Double.parseDouble( liter));
                }catch (Exception r){
                    p.liter = "0";
                }
                String fat = jsonobject.getString("fat");
                try {
                    p.fat = String.format("%.01f", Double.parseDouble( fat));
                }catch (Exception e){
                    p.fat ="0";
                }
                String snf = jsonobject.getString("snf");
                try {
                    p.snf = String.format("%.01f", Double.parseDouble( snf));
                }catch (Exception r){
                    p.snf = "0";
                }

                String amount = jsonobject.getString("amount");
                try {
                    p.amount = String.format("%.02f", Double.parseDouble( amount));
                }catch (Exception r){
                    p.amount = "0";
                }
                String rate = jsonobject.getString("rate");
                try {
                    p.rate = String.format("%.02f", (Double.parseDouble( amount)/Double.parseDouble( liter)));
                }catch (Exception r){
                    p.rate ="0";
                }

                String ReceiptDate = jsonobject.getString("ReceiptDate");
                p.ReceiptDate = ReceiptDate;
                String me = jsonobject.getString("me");
                p.me = me;
               // arrayPerson.add(p);
                try {
                    if ((me.equals("M") && timetype == 0) && (type.equals("Cow") && typetype == 0)) {
                        single_textviewliter.setText("" + p.liter);
                        single_textviewfat.setText("" + p.fat);
                        single_textviewsnf.setText("" + p.snf);
                        single_textviewrate.setText("" + p.rate);
                        single_textviewamount.setText("" + p.amount);
                        single_textviewliterbuff.setText("0");
                        single_textviewfatbuff.setText("0");
                        single_textviewsnfbuff.setText("0");
                        single_textviewratebuff.setText("0");
                        single_textviewamountbuff.setText("0");
                        //val=10;
                    }
                    if ((me.equals("M") && timetype == 0) && (type.equals("Buff") && typetype == 1)) {
                        single_textviewliterbuff.setText("" + p.liter);
                        single_textviewfatbuff.setText("" + p.fat);
                        single_textviewsnfbuff.setText("" + p.snf);
                        single_textviewratebuff.setText("" + p.rate);
                        single_textviewamountbuff.setText("" + p.amount);
                        single_textviewliter.setText("0");
                        single_textviewfat.setText("0");
                        single_textviewsnf.setText("0");
                        single_textviewrate.setText("0");
                        single_textviewamount.setText("0");
                        //val=20;
                    }
                    if ((me.equals("E") && timetype == 1) && (type.equals("Buff") && typetype == 1)) {
                        single_textviewliterbuff.setText("" + p.liter);
                        single_textviewfatbuff.setText("" + p.fat);
                        single_textviewsnfbuff.setText("" + p.snf);
                        single_textviewratebuff.setText("" + p.rate);
                        single_textviewamountbuff.setText("" + p.amount);
                        single_textviewliter.setText("0");
                        single_textviewfat.setText("0");
                        single_textviewsnf.setText("0");
                        single_textviewrate.setText("0");
                        single_textviewamount.setText("0");
                        //val=30;
                    }
                    if ((me.equals("E") && timetype == 1) && (type.equals("Cow") && typetype == 0)) {
                        single_textviewliter.setText("" + p.liter);
                        single_textviewfat.setText("" + p.fat);
                        single_textviewsnf.setText("" + p.snf);
                        single_textviewrate.setText("" + p.rate);
                        single_textviewamount.setText("" + p.amount);
                        single_textviewliterbuff.setText("0");
                        single_textviewfatbuff.setText("0");
                        single_textviewsnfbuff.setText("0");
                        single_textviewratebuff.setText("0");
                        single_textviewamountbuff.setText("0");
                       // val=40;
                    }
                }catch (Exception e){
                    e.printStackTrace();
//                    single_textviewliter.setText("0" + p.liter);
//                    single_textviewfat.setText("0" + p.fat);
//                    single_textviewsnf.setText("0" + p.snf);
//                    single_textviewrate.setText("0" + p.rate);
//                    single_textviewamount.setText("0" + p.amount);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       // listViewAdapter.notifyDataSetChanged();
    }
}
