package com.vsi.smart.dairy1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Statementlist extends AppCompatActivity
{
    public Calendar c = Calendar.getInstance();
    public final int mYear = c.get(Calendar.YEAR);
    public  final int mMonth = c.get(Calendar.MONTH);
    public final  int mDay = c.get(Calendar.DAY_OF_MONTH);
    public int i;
    public  int pday;
    public int pmonth;
    public int pyear;
    public TableLayout table1;
    String str,str2;
    Spinner stp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_statementlist);

        ImageButton datebtn=(ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate);
        ImageButton datebtn2=(ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate2);
        final TextView date=(TextView)findViewById(com.vsi.smart.dairy1.R.id.date);
        final TextView date2=(TextView)findViewById(com.vsi.smart.dairy1.R.id.date2);

        TextView pname=(TextView)findViewById(com.vsi.smart.dairy1.R.id.id_lbl_pname);
        pname.setText(ApplicationRuntimeStorage.CUST_NAME);

        table1=(TableLayout)findViewById(com.vsi.smart.dairy1.R.id.table1);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Statement");

        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Statementlist.this,options.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Statementlist.this,
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
                                str = date.getText().toString();
                            }
                        },mYear,mMonth,mDay);

                datePickerDialog.show();
            }
        });

         stp = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountercode1);
        addItemsOnSpinner5();
        stp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String itemNameSelected = parentView.getItemAtPosition(position).toString();
                if("PLEASE SELECT".equalsIgnoreCase(itemNameSelected)) return;

                str = date.getText().toString();
                str2 = date2.getText().toString();
               // getWebData(str,str2,itemNameSelected);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                Toast.makeText(Statementlist.this, "Please Select  Name", Toast.LENGTH_SHORT).show();
            }

        });

        datebtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Statementlist.this,
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
                                str2 = date2.getText().toString();
                                String stp_nm = stp.getSelectedItem().toString();
                                if(ApplicationRuntimeStorage.ORG_TYPE==2) {
                                    getWebData(str, str2, ApplicationRuntimeStorage.SALE);
                                }else{
                                    getWebData(str, str2, stp_nm);

                                }
                            }
                        },mYear,mMonth,mDay);
                datePickerDialog.show();
            }
        });



        if(ApplicationRuntimeStorage.ORG_TYPE==2) {
            ((TextView)findViewById(com.vsi.smart.dairy1.R.id.id_lbl_ledgername)).setVisibility(View.INVISIBLE);
            stp.setVisibility(View.INVISIBLE);
        }

    }
    // add items into spinner dynamically
    public void addItemsOnSpinner5(){
        Spinner spinner5 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountercode1);
        List<String> list = new ArrayList<>();
        CallSoap cs=new CallSoap();
        String JSON_RESULT= cs.GetKapatList(ApplicationRuntimeStorage.USERID,ApplicationRuntimeStorage.COMPANYID); // Web Call to populate JSON ItemList
        try{
            JSONArray jsonarray = new JSONArray(JSON_RESULT);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                //String itemCode = jsonobject.getString("Key");
                String itemName = jsonobject.getString("ItemName");
                list.add(itemName);
            }
        }catch (Exception e) {e.printStackTrace();}
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner5.setAdapter(dataAdapter);
    }
    private void getWebData(String str, String str2,String itemNameSelected)
    {


        double tempbalance=0;
        double tcredit=0;
        double tdebit=0;

        table1 = (TableLayout) findViewById(com.vsi.smart.dairy1.R.id.table1);
        int cnter = table1.getChildCount();
        for (int i = 0; i < cnter; i++) {
            View child = table1.getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }



        CallSoap cs = new CallSoap();
        String JSON_RESULT = cs.GetStatement(ApplicationRuntimeStorage.COMPANYID, itemNameSelected, ApplicationRuntimeStorage.USERID, str,str2);
        try {
            JSONArray jsonarray = new JSONArray(JSON_RESULT);
            String  spacing = "\n\t\t\t\t";
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);

                final String  rno = jsonobject.getString("rno");
                final String  billno = jsonobject.getString("billno");
                final String  itemgroupcode = jsonobject.getString("itemGroupcode");

                final String  voucherdate = jsonobject.getString("voucherdate");
                String  voucherno = jsonobject.getString("voucherno");
                String  narration = jsonobject.getString("narration");
                String vtype = jsonobject.getString("vtype");
                final  String receiptname = jsonobject.getString("receiptname");
                String  amt = jsonobject.getString("amt");
                Double value = Double.parseDouble(amt);

                TableRow row= new TableRow(Statementlist.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                TextView voucherdate1 = new TextView(Statementlist.this);
                voucherdate1.setText(spacing+voucherdate);
                row.addView(voucherdate1);

                TextView voucherno1 = new TextView(Statementlist.this);
                voucherno1.setText(spacing+voucherno);
                row.addView(voucherno1);

                TextView narration1 = new TextView(Statementlist.this);
                narration1.setText(spacing+narration+" "+vtype);
                row.addView(narration1);

                TextView credit = new TextView(Statementlist.this);
                TextView debit = new TextView(Statementlist.this);

                if(value<0)
                {
                    credit.setText(spacing+value);
                    row.addView(credit);
                    debit.setText(spacing+"-");
                    row.addView(debit);
                    tempbalance=tempbalance+value;

                    tdebit=tdebit+value;
                }
                else
                {
                    credit.setText(spacing+"-");
                    row.addView(credit);
                    debit.setText(spacing+value);
                    row.addView(debit);
                    tempbalance=tempbalance+value;
                    tcredit=tcredit+value;
                }

                TextView Balance = new TextView(Statementlist.this);

                if(tempbalance >= 0){
                    Balance.setText(spacing+tempbalance+" जमा");
                }
                if(tempbalance < 0){
                    Balance.setText(spacing+tempbalance+" नावे");
                }

                row.addView(Balance);
                 if("SB".equalsIgnoreCase(receiptname)) {
                     ImageButton delete = new ImageButton(Statementlist.this);
                     delete.setImageResource(com.vsi.smart.dairy1.R.drawable.ic_event_note_black_24dp);
                     delete.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View view) {

                             Intent intent = new Intent(Statementlist.this, Statementdetails.class);

                             intent.putExtra("rno",rno);
                             intent.putExtra("receiptname",receiptname);
                             intent.putExtra("itemGroupcode",itemgroupcode);
                             intent.putExtra("voucherdate",voucherdate);
                             intent.putExtra("billno",billno);

                             startActivity(intent);
                         }
                     });
                     row.addView(delete);
                 }


                table1.addView(row);
            }
            TextView tcredit1=(TextView) findViewById(com.vsi.smart.dairy1.R.id.totalcreditamount);tcredit1.setText(""+Math.abs(tcredit));
            TextView tdebit1=(TextView) findViewById(com.vsi.smart.dairy1.R.id.totaldebitamount);tdebit1.setText(""+Math.abs(tdebit));
            if(tempbalance >= 0) {
                TextView tbal = (TextView) findViewById(com.vsi.smart.dairy1.R.id.totalbalance);
                tbal.setText("" + Math.abs(tempbalance)+" जमा");
            }else{

                TextView tbal = (TextView) findViewById(com.vsi.smart.dairy1.R.id.totalbalance);
                tbal.setText("" + Math.abs(tempbalance)+" नावे");
            }

        } catch (Exception e) {}
    }
    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Statementlist.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}

