package com.vsi.smart.dairy1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Addpashupurchaselist extends AppCompatActivity {

    Calendar c = Calendar.getInstance();
    public final int mYear = c.get(Calendar.YEAR);
    public  final int mMonth = c.get(Calendar.MONTH);
    public final  int mDay = c.get(Calendar.DAY_OF_MONTH);
    public  int pday;
    public int pmonth;
    public int pyear;
    String stringdate;
    String orderid;
    String custnamecode;
    CustomAdapter customAdapter;
    ArrayList<Person> codelist=new ArrayList<>();

    private  class Person{

        public String custcode;
        public String name;
        public String amount;
        public String orderid;

    }
    FloatingActionButton fabadd;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_addpashupurchaselist);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view = getSupportActionBar().getCustomView();
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar, null);
        TextView tittle = (TextView) mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Inventory Purchase List");

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        fabadd = (FloatingActionButton) findViewById(com.vsi.smart.dairy1.R.id.fabadd);
        fabadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Addpashupurchaselist.this, Addpashupurchase.class);
                startActivity(intent);
            }
        });
        final TextView date = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtdate);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formatteddate = df.format(c.getTime());
        date.setText(formatteddate);
        stringdate = date.getText().toString();

        ImageButton calendarbtn = (ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate);
        calendarbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Addpashupurchaselist.this,
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
                                calllist(stringdate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        listView = (ListView) findViewById(com.vsi.smart.dairy1.R.id.listviewstatement);
        customAdapter=new CustomAdapter(getBaseContext(),codelist);
        listView.setAdapter(customAdapter);


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                try {
                    final Addpashupurchaselist.Person p = codelist.get(pos);
                    final int position = pos;
                    AlertDialog.Builder builder = new AlertDialog.Builder(Addpashupurchaselist.this);
                    builder.setMessage("Do you want to Delete?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(ApplicationRuntimeStorage.AccessRights.contains("RULE_PURCHASE_DELETE_PASHUKHADYA")) {
                                codelist.remove(position);
                                customAdapter.notifyDataSetChanged();

                                new Longoperation().execute(p.orderid);
                            }else{

                                Toast.makeText(Addpashupurchaselist.this, "Access Denied", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    // Create and show the dialog
                    builder.show();
                }catch (Exception e){

                    e.printStackTrace();
                }
                // Signal OK to avoid further processing of the long click
                return true;

            }
        });




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                try {
                    final Addpashupurchaselist.Person p = codelist.get(position);
                    Intent intent=new Intent(Addpashupurchaselist.this,Addupdatepashusale.class);
                    intent.putExtra("orderid1",p.orderid);
                    intent.putExtra("stringdate",stringdate);
                    intent.putExtra("custcode",p.custcode);
                    intent.putExtra("custname",p.name);
                    startActivity(intent);
                }catch (Exception ew){
                    ew.printStackTrace();
                }

            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Addpashupurchaselist.this,Login.class);
            startActivity(intent);
        }else{
        final TextView date = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtdate);
        stringdate=date.getText().toString();
        calllist(stringdate);}
    }

    private void calllist(String stringdate)
    {
        codelist.clear();
        customAdapter.notifyDataSetChanged();
        CallSoap cs = new CallSoap();
        String RESULT = cs.GetDailyPurchaseList(ApplicationRuntimeStorage.COMPANYID, ApplicationRuntimeStorage.USERID, stringdate);
        try {
            JSONArray jsonarray = new JSONArray(RESULT);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                Person p = new Person();
                String acccode = jsonobject.getString("acccode");
                p.custcode = acccode;
                String cname = jsonobject.getString("cname");
                p.name = cname;
                custnamecode=(acccode+"_"+cname);
                String Amount = jsonobject.getString("Amount");
                p.amount = Amount;
                orderid = jsonobject.getString("orderId");
                p.orderid=orderid;
                codelist.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class CustomAdapter extends BaseAdapter {

        public CustomAdapter(Context baseContext, ArrayList<Person> codelist) {
        }

        @Override
        public int getCount() {
            return codelist.size();

        }

        @Override
        public Object getItem(int position) {

            return codelist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            final Person p = codelist.get(position);

            LayoutInflater inflater=getLayoutInflater();
            View rowView=inflater.inflate(com.vsi.smart.dairy1.R.layout.pashulistoflistview, null,true);

            TextView code = (TextView) rowView.findViewById(com.vsi.smart.dairy1.R.id.txtcode);
            code.setText(p.orderid);
            TextView txtname = (TextView) rowView.findViewById(com.vsi.smart.dairy1.R.id.txtname);
            txtname.setText(p.custcode+"_"+p.name);



            TextView txtamount = (TextView) rowView.findViewById(com.vsi.smart.dairy1.R.id.txtamount);
            txtamount.setText(p.amount);

            return rowView;
        }
    }

    private class Longoperation extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            CallSoap cs1=new CallSoap();
            String result=cs1.DeleteSaleOrder(ApplicationRuntimeStorage.COMPANYID,ApplicationRuntimeStorage.USERID,params[0]);
            return result;
        }

        ProgressDialog asyncDialog = new ProgressDialog(Addpashupurchaselist.this);
        @Override
        protected void onPreExecute() {
            //set message of the dialog
            asyncDialog.setMessage("Loading...");
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
}
