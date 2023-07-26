package com.vsi.smart.dairy1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Addchalanlist extends AppCompatActivity {
    Calendar c = Calendar.getInstance();
    public final int mYear = c.get(Calendar.YEAR);
    public  final int mMonth = c.get(Calendar.MONTH);
    public final  int mDay = c.get(Calendar.DAY_OF_MONTH);
    public  int pday;
    public int pmonth;
    public int pyear;
    String fromdate;
    TextView date;
    CustomAdapter adapter;
    FloatingActionButton fabadd;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_addchalanlist);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view = getSupportActionBar().getCustomView();
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar, null);
        TextView tittle = (TextView) mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Transaction");

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        listView = (ListView) findViewById(com.vsi.smart.dairy1.R.id.listviewstatement);
        adapter = new CustomAdapter(getBaseContext());
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {
                    try {
                        final Chalan ch = chalanList.get(pos);
                        final int position = pos;
                        AlertDialog.Builder builder = new AlertDialog.Builder(Addchalanlist.this);
                        builder.setMessage("Do you want to Delete?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(ApplicationRuntimeStorage.AccessRights.contains("RULE_DELETE_CHALAN")) {
                                    chalanList.remove(position);
                                    adapter.notifyDataSetChanged();

                                    new Longoperationdelete().execute(ch.acccode, ch.voucherno, fromdate, ch.glcode);
                                }else{

                                    Toast.makeText(Addchalanlist.this, "Access Denied", Toast.LENGTH_LONG).show();

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
                    final Chalan ch = chalanList.get(position);
                    Intent intent = new Intent(Addchalanlist.this, Addupdatechalan.class);
                    intent.putExtra("glcode", ch.glcode);
                    intent.putExtra("custacccode", ch.acccode);
                    intent.putExtra("narration", ch.narration);
                    intent.putExtra("amt", ch.amt);
                    intent.putExtra("cname", ch.cname);
                    intent.putExtra("glcname", ch.narration1);
                    intent.putExtra("fromdate", fromdate);
                    intent.putExtra("vno", ch.voucherno);
                    startActivity(intent);
                }catch (Exception ew){
                    ew.printStackTrace();
                }

            }
        });



        fabadd = (FloatingActionButton) findViewById(com.vsi.smart.dairy1.R.id.fabadd);
        fabadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Addchalanlist.this, Addchalan.class);
                startActivity(intent);
            }
        });

        ImageButton calendarbtn = (ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate);
        date = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtdate);
        calendarbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Addchalanlist.this,
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
                                fromdate=date.getText().toString();
                                calltranslist();
                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();

            }
        });
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formatteddate = df.format(c.getTime());
        date.setText(formatteddate);

        fromdate = date.getText().toString();
        calltranslist();
    }
    @Override
    public void onResume() {
        super.onResume();
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Addchalanlist.this,Login.class);
            startActivity(intent);
        }else{ calltranslist();}

    }
    private void calltranslist()
    {
        new LongOperation_1().execute(date.getText().toString());
    }

    private class CustomAdapter extends BaseAdapter {

       public CustomAdapter(Context baseContext) {
       }

       @Override
        public int getCount() {

            return chalanList.size();
        }

        @Override
        public Object getItem(int position) {

            return chalanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {



            LayoutInflater inflater=getLayoutInflater();
            View rowView=inflater.inflate(com.vsi.smart.dairy1.R.layout.listoftransactionlistview, null,true);
            try {
               final Chalan ch = chalanList.get(position);

                TextView glcodename11 = (TextView) rowView.findViewById(com.vsi.smart.dairy1.R.id.txtglcodename11);
                glcodename11.setText(ch.narration1);

                TextView txglcode111 = (TextView) rowView.findViewById(com.vsi.smart.dairy1.R.id.txglcode11);
                txglcode111.setText(ch.glcode);

                TextView date = (TextView) rowView.findViewById(com.vsi.smart.dairy1.R.id.txtdate);
                date.setText(ch.acccode);
                TextView narration = (TextView) rowView.findViewById(com.vsi.smart.dairy1.R.id.txtnarration);
                narration.setText(ch.narration);
                TextView balance = (TextView) rowView.findViewById(com.vsi.smart.dairy1.R.id.txtcredit);
                balance.setText(ch.amt);
                TextView name = (TextView) rowView.findViewById(com.vsi.smart.dairy1.R.id.txtname);
                name.setText(ch.cname);

                name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {

                    }
                });
               /// }


                TextView type = (TextView) rowView.findViewById(com.vsi.smart.dairy1.R.id.txttype);
                Double typevalue = Double.parseDouble(ch.amt.trim());
                if (typevalue < 0) {
                    type.setText("Debit");
                } else {
                    type.setText("Credit");
                }

                return rowView;
            }catch (Exception ew){
                   ew.printStackTrace();
            }
            return rowView;
        }
    }

    List<Chalan>  chalanList =  new ArrayList<>();
    private class Chalan {

        public String acccode;
        public String narration;
        public String narration1;
        public String amt;
        public String cname;
        public String voucherno;
        public String glcode;
        public String vtype;
    }

    private class LongOperation_1 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            CallSoap cs = new CallSoap();
            String RESULT = "";

            RESULT = cs.TransactionList(ApplicationRuntimeStorage.COMPANYID, ApplicationRuntimeStorage.USERID, params[0]);


            return RESULT;

        }

        @Override
        protected void onPostExecute(String result) {
            try{

                chalanList.clear();


                double cramt=0,dramt=0;

                try {
                    JSONArray jsonarray = new JSONArray(result);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        Chalan ch = new Chalan();
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String acccode = jsonobject.getString("acccode");

                        ch.acccode = acccode;
                        String narration = jsonobject.getString("narration");

                        ch.narration = narration;
                        String narration1 = jsonobject.getString("narration1");

                        ch.narration1 = narration1;
                        String amt = jsonobject.getString("amt");

                        ch.amt = amt;
                        try {
                            double am = Double.parseDouble(amt);
                            if(am < 0) {
                                dramt = dramt+am;
                            }else{
                                cramt = cramt+am;
                            }
                        } catch (Exception e) {
                        }
                        String cname=jsonobject.getString("cname");

                        ch.cname = cname;
                        String voucherno=jsonobject.getString("voucherno");
                        ch.voucherno = voucherno;
                        String glcode=jsonobject.getString("glcode");
                        ch.glcode=glcode;

                        chalanList.add(ch);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                adapter.notifyDataSetChanged();

                TextView cr = (TextView) findViewById(com.vsi.smart.dairy1.R.id.id_cr);
                TextView dr = (TextView) findViewById(com.vsi.smart.dairy1.R.id.id_dr);
                TextView bal = (TextView) findViewById(com.vsi.smart.dairy1.R.id.id_bal);

                DecimalFormat f = new DecimalFormat("##.00");
                try {
                    cr.setText("" + f.format(cramt));
                    dr.setText("" +  f.format(dramt));
                    bal.setText("" +  f.format((dramt+cramt)));

                }catch (Exception er){

                    cr.setText("0.0");
                    dr.setText("0.0" );
                    bal.setText("0.0");

                }


            }catch (Exception e) {e.printStackTrace();}
            //hide the dialog
            asyncDialog.dismiss();

            super.onPostExecute(result);
        }
        ProgressDialog asyncDialog = new ProgressDialog(Addchalanlist.this);
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

    private class Longoperationdelete extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... params)
        {
            CallSoap cs1=new CallSoap();
            String result=cs1.DeleteTransaction(ApplicationRuntimeStorage.COMPANYID,ApplicationRuntimeStorage.USERID,params[0],params[1],params[2],params[3]);
            return result;
        }

        ProgressDialog asyncDialog = new ProgressDialog(Addchalanlist.this);
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
}
