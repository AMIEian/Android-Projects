package com.vsi.smart.dairy1;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Summary2 extends AppCompatActivity {
    ListViewAdapter listViewAdapter;
    private ListView listView;
    private ArrayList<Branch> arrayPersonbr = new ArrayList<>();
    Spinner spnbranch;
    public Calendar c = Calendar.getInstance();
    public final int mYear = c.get(Calendar.YEAR);
    public final int mMonth = c.get(Calendar.MONTH);
    public final int mDay = c.get(Calendar.DAY_OF_MONTH);
    public int i;
    public int pday;
    public int pmonth;
    public int pyear;
    TextView txttodate, txtfromdate;
    private ArrayList<Person> arrayPerson = new ArrayList<>();
    public class Branch
    {
        public String  name;
        public String userid;
        @Override
        public  String toString()
        {
            return name;
        }

    }
    private class Person {
        public String branch;
        public String type;
        public String liter;
        public String fat;
        public String snf;
        public String rate;
        public String amount;
        public String ReceiptDate;
    }

    ImageButton btnsearch;
    String todate, fromdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_summary2);

        txtfromdate = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtfromdate);
        txttodate = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txttodate);

        btnsearch = (ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btnsearch);
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListdata();
            }
        });

        listView = (ListView) findViewById(com.vsi.smart.dairy1.R.id.listview);

        spnbranch = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnbranch);

        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

//        calendar.add(Calendar.MONTH, 1);
//        calendar.set(Calendar.DAY_OF_MONTH, 1);
//        calendar.add(Calendar.DATE, -1);

        Date lastDayOfMonth = calendar.getTime();
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strfirst = sdf.format(lastDayOfMonth);
        txtfromdate.setText(strfirst);

        Date today1 = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today1);
       // cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date firstDayOfTheMonth = cal.getTime();
        DateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        String formatteddate1 = sdf1.format(firstDayOfTheMonth);
        txttodate.setText(strfirst);
        txtfromdate.setText(strfirst);
        final TextView date = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txttodate);
        txttodate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Summary2.this,
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
                                txttodate.setText(currdate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        final TextView date2 = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtfromdate);
        txtfromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Summary2.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                date2.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                pyear = year;
                                pmonth = monthOfYear;
                                pday = dayOfMonth;

                                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                Date date1 = new Date(year - 1900, (monthOfYear), dayOfMonth);
                                String currdate = "";
                                currdate = df.format(date1.getTime());
                                txtfromdate.setText(currdate);
                            //    showListdata();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        listViewAdapter = new ListViewAdapter();
        listView.setAdapter(listViewAdapter);

    }

    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Summary2.this,Login.class);
            startActivity(intent);
        }else{showListdata();}
        super.onResume();
    }

//    private class longspndata extends AsyncTask<String, Void, String> {
//
//        String user,pass;
//        @Override
//        protected String doInBackground(String... params) {
//            CallSoap cs=new CallSoap();
//            String JSON_RESULT= cs.GetBranchList(ApplicationRuntimeStorage.COMPANYID,
//                    ApplicationRuntimeStorage.USERID); // Web Call to populate JSON ItemList
//            return  JSON_RESULT;
//        }
//        @Override
//        protected void onPostExecute(String JSON_RESULT) {
//            asyncDialog.dismiss();
//            try
//            {
//                try {
//                    JSONArray jsonarray = new JSONArray(JSON_RESULT);
//                    for (int i = 0; i < jsonarray.length(); i++) {
//                        Branch p=new Branch();
//                        JSONObject jsonobject = jsonarray.getJSONObject(i);
//                        String pname = jsonobject.getString("name");
//                        p.name=pname;
//                        String userid = jsonobject.getString("userid");
//                        p.userid=userid;
//                        arrayPersonbr.add(p);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if(arrayPersonbr.size()>0){
//                    ArrayAdapter<Branch> adapter = new ArrayAdapter<Branch>(Summary2.this, R.layout.spinner_item, arrayPersonbr);
//                    adapter.setDropDownViewResource(R.layout.spinner_item);
//                    spnbranch.setAdapter(adapter);}
//                else
//                    {
//                    Branch p=new Branch();
//                    p.name="Please Select";p.userid="0";
//                    arrayPersonbr.add(p);
//                    ArrayAdapter<Branch> adapter = new ArrayAdapter<Branch>(Summary2.this, R.layout.spinner_item, arrayPersonbr);
//                    adapter.setDropDownViewResource(R.layout.spinner_item);
//                    spnbranch.setAdapter(adapter);
//                }
//            }catch (Exception e) {e.printStackTrace();}
//            //hide the dialog
//        }
//        ProgressDialog asyncDialog = new ProgressDialog(Summary2.this);
//        @Override
//        protected void onPreExecute() {
//            //set message of the dialog
//            asyncDialog.setMessage("Loading...");
//            //show dialog
//            asyncDialog.show();
//            super.onPreExecute();
//        }
//        @Override
//        protected void onProgressUpdate(Void... values) {}
//    }//get Delalers
//

    private void showListdata() {
        todate = txttodate.getText().toString();
        fromdate = txtfromdate.getText().toString();
        new LongOperation_Marketing().execute(ApplicationRuntimeStorage.USERID, todate, fromdate);
    }

    private void listViewUpdate()
    {
//         CallSoap cs = new CallSoap();
        String JSON_RESULT = "";
//        cs.GetMilkReport(ApplicationRuntimeStorage.COMPANYID, ApplicationRuntimeStorage.USERID, txttodate.getText().toString(),
//                txtfromdate.getText().toString(),"ALL"); // Web Call to populate JSON ItemList
        JSON_RESULT = ApplicationRuntimeStorage.MARKETING_TEAM_LIST;
        long fgps, fgps1 = 0, fnogps, fnogps1 = 0, fact, fact1 = 0;
        arrayPerson.clear();
        listViewAdapter.notifyDataSetChanged();
        try {
            arrayPerson.clear();
            double fat1=0,liter1=0,snf1=0,rate1,amt1=0;
            JSONArray jsonarray = new JSONArray(JSON_RESULT);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                Person p = new Person();
                final String branch = jsonobject.getString("branch");
                p.branch = branch;
                final String type = jsonobject.getString("type");
                p.type = type;

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
                double ltr = 0;
                try {
                    ltr = Double.parseDouble(liter);
                    liter1 = liter1+ltr;
                } catch (Exception e) { }
                try {
                    fat1 = fat1+ (Double.parseDouble(fat)*ltr);
                } catch (Exception e) { }
                try {
                    snf1 = snf1 + (Double.parseDouble(snf)*ltr);
                } catch (Exception e) { }
                try {
                    amt1 =amt1+ Double.parseDouble(amount);
                } catch (Exception e) {
                }
                arrayPerson.add(p);
            }
            TextView idliter = (TextView) findViewById(com.vsi.smart.dairy1.R.id.id_liter);
            TextView idfat = (TextView) findViewById(com.vsi.smart.dairy1.R.id.id_fat);
            TextView idsnf = (TextView) findViewById(com.vsi.smart.dairy1.R.id.id_snf);
            TextView idrate = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtrate);
            TextView idamt = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtamnt);
            DecimalFormat f = new DecimalFormat("##.00");
            try
            {
                idliter.setText("" + f.format(liter1));
                if("NaN".equalsIgnoreCase(""+f.format((amt1)))){
                    idamt.setText("Amt : 0");
                }else {
                    idamt.setText("Amt : " + f.format(amt1));
                }

                if("NaN".equalsIgnoreCase(""+f.format((amt1 / liter1)))){
                    idrate.setText("Rate : 0" );
                }else {
                    idrate.setText("Rate : " + f.format((amt1 / liter1)));
                }

                if("NaN".equalsIgnoreCase(""+f.format((fat1 / liter1)))) {
                    idfat.setText("0" );
                }else{
                    idfat.setText("" + f.format((fat1 / liter1)));
                }

                if("NaN".equalsIgnoreCase(""+f.format((snf1 / liter1)))) {

                }else {
                    idsnf.setText("" + f.format((snf1 / liter1)));
                }
            }catch (Exception er){
                idliter.setText("" + f.format(liter1));
                idamt.setText("" +  f.format(amt1));
                idrate.setText("0.0");
                idfat.setText("0.0" );
                idsnf.setText("0.0");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        listViewAdapter.notifyDataSetChanged();
    }

    private class ListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return arrayPerson.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayPerson.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            Person p = arrayPerson.get(position);
            LayoutInflater layoutInflater = getLayoutInflater();
            View rootView = layoutInflater.inflate(com.vsi.smart.dairy1.R.layout.listview_single_item_uisummary2, null);
            try {
                // Init TextView of listview_single_item_ui.xml
                TextView textView = (TextView) rootView.findViewById(com.vsi.smart.dairy1.R.id.single_textviewbranch);
                textView.setText(p.branch);

                TextView textViewcity = (TextView) rootView.findViewById(com.vsi.smart.dairy1.R.id.single_textviewtype);
                textViewcity.setText(p.type);

                TextView textViewmobile = (TextView) rootView.findViewById(com.vsi.smart.dairy1.R.id.single_textviewltr);
                textViewmobile.setText(p.liter);

                TextView textViewmobile1 = (TextView) rootView.findViewById(com.vsi.smart.dairy1.R.id.single_textviewfat);
                textViewmobile1.setText(p.fat);

                TextView textViewmobile2 = (TextView) rootView.findViewById(com.vsi.smart.dairy1.R.id.single_textviewsnf);
                textViewmobile2.setText(p.snf);

                TextView single_textviewrate = (TextView) rootView.findViewById(com.vsi.smart.dairy1.R.id.single_textviewrate);
                single_textviewrate.setText("Rate : "+p.rate);

                TextView single_textviewamount = (TextView) rootView.findViewById(com.vsi.smart.dairy1.R.id.single_textviewamount);
                single_textviewamount.setText("Amt : "+p.amount);

            } catch (Exception sf) { }

            return rootView;
        }
    }

    private class LongOperation_Marketing extends AsyncTask<String, String, String> {
        String user, pass;

        @Override
        protected String doInBackground(String... params)
        {
            CallSoap cs = new CallSoap();
            String JSON_RESULT = cs.GetMilkReport(ApplicationRuntimeStorage.COMPANYID, ApplicationRuntimeStorage.USERID, txttodate.getText().toString(),
                    txtfromdate.getText().toString(),"ALL"); // Web Call to populate JSON ItemList
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

        ProgressDialog asyncDialog = new ProgressDialog(Summary2.this);

        @Override
        protected void onPreExecute() {
            //set message of the dialog
            asyncDialog.setMessage("Loading...");
            //show dialog
            asyncDialog.show();
            super.onPreExecute();
        }
    }
}
