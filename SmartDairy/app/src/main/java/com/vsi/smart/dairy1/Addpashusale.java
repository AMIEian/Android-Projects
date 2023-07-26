package com.vsi.smart.dairy1;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Addpashusale extends AppCompatActivity
{
    Calendar c = Calendar.getInstance();
    public final int mYear = c.get(Calendar.YEAR);
    public  final int mMonth = c.get(Calendar.MONTH);
    public final  int mDay = c.get(Calendar.DAY_OF_MONTH);
    TimePickerDialog timePickerDialog;
    TimePicker timePicker;
    EditText txtrate,txtqty;
    RadioGroup grpsms;
    public  int pday;
    public int pmonth;
    long flagsms =1;
    public int pyear;
    public int phour;
    public int pminute;
    public String getTime,stringdate;
    EditText etxqty;
    String orderid;
    TableLayout table1;
    Button add1,btnsubmit;
    TextView txtdate,txtamt;
    ArrayList<String> codelist = new ArrayList<>();
    ArrayList<String> codegrplist = new ArrayList<>();
    ArrayList<String> acccodelist=new ArrayList<>();
    EditText id_etxqty,id_etxrate,id_txtamt;
    Spinner spnitem,spncustomer,spnitemgrp;
    ArrayAdapter<String> dataAdapter1,dataAdapter,dataAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_addpashusale);

        ActivityCompat.requestPermissions(Addpashusale.this,new String[]{Manifest.permission.SEND_SMS}, 1);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view = getSupportActionBar().getCustomView();
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar, null);
        TextView tittle = (TextView) mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Add Pashu Sale Entry");

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        spnitemgrp=(Spinner)findViewById(com.vsi.smart.dairy1.R.id.spnCountercode2);
        spnitem=(Spinner)findViewById(com.vsi.smart.dairy1.R.id.spnCountercode1);
        final EditText txtamt=(EditText) findViewById(com.vsi.smart.dairy1.R.id.txtamt);
        txtrate=(EditText)findViewById(com.vsi.smart.dairy1.R.id.etxrate);
        txtqty=(EditText)findViewById(com.vsi.smart.dairy1.R.id.etxqty);
        add1=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnadd);
        table1 = (TableLayout) findViewById(com.vsi.smart.dairy1.R.id.table2);
        btnsubmit=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnorder);
        spncustomer=(Spinner)findViewById(com.vsi.smart.dairy1.R.id.spncustomer);
        id_txtamt =  (EditText) findViewById(com.vsi.smart.dairy1.R.id.txtamt);
        id_txtamt.setEnabled(false);
        id_etxrate =  (EditText) findViewById(com.vsi.smart.dairy1.R.id.etxrate);
        id_etxqty =  (EditText) findViewById(com.vsi.smart.dairy1.R.id.etxqty);

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getApplicationContext(), com.vsi.smart.dairy1.R.layout.spinner_item,
                ApplicationRuntimeStorage.codelistps);
        dataAdapter1.setDropDownViewResource(com.vsi.smart.dairy1.R.layout.spinner_item);
        spncustomer.setAdapter(dataAdapter1);

        id_etxrate.addTextChangedListener(new TextWatcher() {
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

        id_etxqty.addTextChangedListener(new TextWatcher() {
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

        dataAdapter2 = new ArrayAdapter<String>(Addpashusale.this, com.vsi.smart.dairy1.R.layout.spinner_item, codelist);
        dataAdapter2.setDropDownViewResource(com.vsi.smart.dairy1.R.layout.spinner_item);
        spnitem.setAdapter(dataAdapter2);

        callcustomerservice();

        dataAdapter = new ArrayAdapter<String>(getApplicationContext(), com.vsi.smart.dairy1.R.layout.spinner_item,codegrplist);
        dataAdapter.setDropDownViewResource(com.vsi.smart.dairy1.R.layout.spinner_item);
        spnitemgrp.setAdapter(dataAdapter);
        spnitemgrp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                codelist.clear();
                dataAdapter2.notifyDataSetChanged();

                String itemGroupSelected=spnitemgrp.getSelectedItem().toString();
                if("PLEASE SELECT".equalsIgnoreCase(itemGroupSelected)) return;

                long itemgrpCode=0;
                try{
                    itemgrpCode = Long.parseLong(itemGroupSelected.split("_")[0]);
                }catch (Exception r){
                    r.printStackTrace();
                }

                codelist.add("PLEASE SELECT");
                for (ItemMaster im: ApplicationRuntimeStorage.LIST_ITEMS_1) {
                        if(im.itemGroupCode==itemgrpCode){
                            if(0== itemgrpCode){
                                codegrplist.add("PLEASE SELECT");
                            }else {
                                codelist.add(im.itemCode + "_" + im.itemName);
                            }
                        }
                }
                dataAdapter2.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                Toast.makeText(Addpashusale.this, "Please Select Item Name", Toast.LENGTH_SHORT).show();
            }

        });
        final TextView date = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtdate);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formatteddate = df.format(c.getTime());
        date.setText(formatteddate);
        stringdate=date.getText().toString();

        ImageButton calendarbtn = (ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate);
        calendarbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Addpashusale.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                pyear = year;
                                pmonth = monthOfYear;
                                pday = dayOfMonth;

                                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                Date date1 = new Date(year - 1900, (monthOfYear), dayOfMonth);
                                String currdate = "";
                                currdate = df.format(date1.getTime());
                                date.setText(currdate);
                                stringdate=date.getText().toString();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });


        add1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(txtrate.length()==0 && txtrate.equals(""))
                {
                    Toast.makeText(Addpashusale.this, "Please Enter Rate Proprly", Toast.LENGTH_SHORT).show();
                }
                else if(txtqty.length()==0 && txtqty.equals(""))
                {
                    Toast.makeText(Addpashusale.this, "Please Enter Quantity Properly", Toast.LENGTH_SHORT).show();
                }
                else if (stringdate.length() == 0) {
                    Toast.makeText(Addpashusale.this, "Please Enter Date", Toast.LENGTH_LONG).show();
                }
                else if (txtamt.length() == 0 && txtamt.equals("")) {
                    Toast.makeText(Addpashusale.this, "Please Enter Amount", Toast.LENGTH_LONG).show();
                }
                else
                {
                    spnitem=(Spinner)findViewById(com.vsi.smart.dairy1.R.id.spnCountercode1);
                    String customer=spncustomer.getSelectedItem().toString();
                    String itemnm = spnitem.getSelectedItem().toString();
                    String itemcodestr = itemnm.split("_")[0];
                    String itemname = itemnm.split("_")[1];
                    String qty = txtqty.getText().toString();
                    String amount =txtamt.getText().toString();
                    String rate =txtrate.getText().toString();

                    TableRow row = new TableRow(Addpashusale.this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);

                    TextView itemcode = new TextView(Addpashusale.this);
                    itemcode.setText(itemcodestr+"\t\t");
                    row.addView(itemcode);

                    TextView prod = new TextView(Addpashusale.this);
                    prod.setText(itemname);
                    row.addView(prod);

                    TextView qty1 = new TextView(Addpashusale.this);
                    qty1.setText("\t\t\t\t\t\t\t\t\t"+qty);
                    row.addView(qty1);

                    TextView rate1 = new TextView(Addpashusale.this);
                    rate1.setText("\t\t\t\t\t"+rate);
                    row.addView(rate1);

                    TextView amount1=new TextView(Addpashusale.this);
                    amount1.setText("\t\t\t\t"+amount);
                    row.addView(amount1);

                    ImageButton delete=new ImageButton(Addpashusale.this);
                    delete.setImageResource(com.vsi.smart.dairy1.R.drawable.ic_delete_black_24dp);
                    delete.setBackgroundColor(0xfffafafa);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            View row=(View) view.getParent();
                            ViewGroup container=((ViewGroup)row.getParent());
                            container.removeView(row);
                            container.invalidate();
                        }
                    });
                    row.addView(delete);
                    table1.addView(row);
                }
            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String customer=spncustomer.getSelectedItem().toString();
                String acccode = customer.split("_")[0];
                String cname = customer.split("_")[1];
                String itemnm = spnitem.getSelectedItem().toString();
                String itemcodestr = itemnm.split("_")[0];
                String itemname = itemnm.split("_")[1];

                String amnt = ((EditText) findViewById(com.vsi.smart.dairy1.R.id.txtamt)).getText().toString().trim();
                String rate = ((EditText) findViewById(com.vsi.smart.dairy1.R.id.etxrate)).getText().toString().trim();
                String qty = ((EditText) findViewById(com.vsi.smart.dairy1.R.id.etxqty)).getText().toString().trim();

                TextView txt_currDt = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtdate);
                String currDt = txt_currDt.getText().toString();
                JSONArray jsonArray = new JSONArray();
                double tqty = 0;
                if(table1.getChildCount()==0)
                {
                    JSONObject student2 = new JSONObject();
                    try {

                        student2.put("CompanyId", ApplicationRuntimeStorage.COMPANYID);
                        student2.put("UserId", ApplicationRuntimeStorage.USERID);

                        student2.put("acccode", acccode);
                        student2.put("cname", cname);
                        student2.put("orderid", "0");
//                        student2.put("OrderDate",currDt );
                        student2.put("OrderDateStr", currDt);
                        student2.put("ItemCode", itemcodestr);
                        student2.put("ItemName", itemname);
                        student2.put("Quantity",qty );
                        student2.put("Rate", rate);
                        student2.put("Amount", amnt);


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    jsonArray.put(student2);

                }else {
                    for (int i = 0; i < table1.getChildCount(); i++) {
                        TableRow mRow = (TableRow) table1.getChildAt(i);
                        TextView itemcode1 = (TextView) mRow.getChildAt(0);
                        TextView prod = (TextView) mRow.getChildAt(1);
                        TextView qty1 = (TextView) mRow.getChildAt(2);
                        TextView rate1 = (TextView) mRow.getChildAt(3);
                        TextView amount1 = (TextView) mRow.getChildAt(4);
                        String itemcode11 = itemcode1.getText().toString().trim();
                        String prod1 = prod.getText().toString().trim();
                        String qty11 = qty1.getText().toString().trim();
                        String rate11 = rate1.getText().toString().trim();
                        String amount11 = amount1.getText().toString().trim();

                        JSONObject student2 = new JSONObject();
                        try {

                            student2.put("CompanyId", ApplicationRuntimeStorage.COMPANYID);
                            student2.put("UserId", ApplicationRuntimeStorage.USERID);

                            student2.put("acccode", acccode);
                            student2.put("cname", cname);
                            student2.put("orderid", "0");
//                            student2.put("OrderDate",currDt );
                            student2.put("OrderDateStr", currDt);
                            student2.put("ItemCode", itemcode11);
                            student2.put("ItemName", prod1);
                            student2.put("Quantity",qty11 );
                            student2.put("Rate", rate11);
                            student2.put("Amount", amount11);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        jsonArray.put(student2);
                    }
                }
                try{
                    CallSoap cs=new CallSoap();
                    String JSON_RESULT = cs.SaveUserOrdersmart(jsonArray.toString(), ApplicationRuntimeStorage.COMPANYID, ApplicationRuntimeStorage.USERID, currDt,acccode); // Web Call to populate JSON ItemList
                    Toast.makeText(Addpashusale.this, JSON_RESULT, Toast.LENGTH_SHORT).show();
                    if (flagsms == 0) {
                        SmsManager sms = SmsManager.getDefault();
                        long acccode_curr = 0;
                        try {
                            acccode_curr = Integer.parseInt(acccode.trim());
                            } catch (Exception ew) { }

                           // String glname11 = customer.substring(1 + customer.indexOf("_"), customer.length());
                            for (MilkCustomer mc : ApplicationRuntimeStorage.LIST_CUSTOMERS) {
                                long code11 = 0;
                                try {
                                    code11 = Long.parseLong(mc.code.trim());
                                } catch (Exception e) { }

                                if (code11 == acccode_curr)
                                {
                                    String vtypestr = "नावे टाकली आहे.";
                                    String smsText = " प्रिय ग्राहक - " + mc.cname.trim() + " - आपली रक्कम रु " + amnt + ", " + customer.trim() + " ला " + vtypestr.trim() + ", धन्यवाद .";
                                    String mobile11 = mc.mobile;
                                    if (mobile11.length() == 10) {
                                       // sms.sendTextMessage(mobile11, null, smsText, null, null);
                                        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("content://com.android.contacts/data/" + mobile11));
                                        i.setType("text/plain");
                                        i.setPackage("com.whatsapp");           // so that only Whatsapp reacts and not the chooser
                                        i.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                                        i.putExtra(Intent.EXTRA_TEXT, smsText);
                                        startActivity(i);
                                    }
                                    else{
                                        Toast.makeText(Addpashusale.this, "No Watsapp", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                }catch(Exception er)
                {
                    Toast.makeText(Addpashusale.this, "ERROR :"+er.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }


        });

    }
    private void calculateAmt(){
        double rate =0  ;try{rate= Double.parseDouble(id_etxrate.getText().toString());}catch (Exception e){}
        double qty =0  ;try{qty= Double.parseDouble(id_etxqty.getText().toString());}catch (Exception e){}

        id_txtamt.setText(""+(qty*rate));

    }
    private void callcustomerservice()
    {
        new LongOperation_1().execute("");
      //  new LongOperation_2().execute("");
    }

    private class LongOperation_1 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                ApplicationRuntimeStorage.LIST_ITEMS_1.clear();
                CallSoap cs = new CallSoap();
                String result = cs.GetItemListpashu(ApplicationRuntimeStorage.COMPANYID, ApplicationRuntimeStorage.USERID);
                JSONArray jsonarray = new JSONArray(result);
                for (int i = 0; i < jsonarray.length(); i++)
                {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String ItemName = jsonobject.getString("ItemName");
                    String ItemCode = jsonobject.getString("ItemCode");
                    String ItemGroupName = jsonobject.getString("ItemGroupName");
                    String ItemGroupCode = jsonobject.getString("ItemGroupCode");
                    //codelist.add(" "+ItemCode+"_"+ItemName);

                    if (!codegrplist.contains(ItemGroupCode + "_" + ItemGroupName)) {
                        if ("0".equalsIgnoreCase(ItemGroupCode)) {
                            codegrplist.add("PLEASE SELECT");
                        } else if (null != ItemGroupName) {
                            codegrplist.add(ItemGroupCode + "_" + ItemGroupName);
                        } else {
                            codegrplist.add("PLEASE SELECT");
                        }
                    }

                    long icode = 0;
                    try {
                        icode = Long.parseLong(ItemCode);
                    } catch (Exception e) {
                    }

                    long igrpcode = 0;
                    try {
                        igrpcode = Long.parseLong(ItemGroupCode);
                    } catch (Exception e) { }

                    ItemMaster im = new ItemMaster(icode, ItemName, igrpcode, ItemGroupName);
                    ApplicationRuntimeStorage.LIST_ITEMS_1.add(im);
                }
            }catch (Exception r){
                r.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                dataAdapter.notifyDataSetChanged();
              //  dataAdapter1.notifyDataSetChanged();
            }catch (Exception e) {e.printStackTrace();}
            //hide the dialog
            asyncDialog.dismiss();

            super.onPostExecute(result);
        }
        ProgressDialog asyncDialog = new ProgressDialog(Addpashusale.this);
        @Override
        protected void onPreExecute() {
            //set message of the dialog
            asyncDialog.setMessage("Loading...");
            //show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

//    private class LongOperation_2 extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            String result = null;
//            try
//            {
//                ApplicationRuntimeStorage.LIST_CUSTOMERS.clear();
//                CallSoap cs1=new CallSoap();
//                result=cs1.CustomerList(ApplicationRuntimeStorage.COMPANYID,ApplicationRuntimeStorage.USERID,"0,5");
//            }catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            try{
//                JSONArray jsonarray = new JSONArray(result);
//                for (int i = 0; i < jsonarray.length(); i++)
//                {
//                    MilkCustomer mc = new MilkCustomer();
//                    JSONObject jsonobject = jsonarray.getJSONObject(i);
//                    String code = jsonobject.getString("code");mc.code=code;
//                    String cname = jsonobject.getString("cname");mc.cname=cname;
//                    String mobile = jsonobject.getString("mobile");mc.mobile=mobile;
//                    acccodelist.add(" "+code+"_"+cname);
//                    ApplicationRuntimeStorage.LIST_CUSTOMERS.add(mc);
//                }
//                //dataAdapter.notifyDataSetChanged();
//
//               // dataAdapter1.notifyDataSetChanged();
//            }catch (Exception e) {e.printStackTrace();}
//            //hide the dialog
//            asyncDialog.dismiss();
//
//            super.onPostExecute(result);
//        }
//        ProgressDialog asyncDialog = new ProgressDialog(Addpashusale.this);
//        @Override
//        protected void onPreExecute()
//        {
//            //set message of the dialog
//            asyncDialog.setMessage("Loading...");
//            //show dialog
//            asyncDialog.show();
//            super.onPreExecute();
//        }
//        @Override
//        protected void onProgressUpdate(Void... values) {}
//    }

    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Addpashusale.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
