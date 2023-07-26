package com.vsi.smart.dairy1;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Addmilkentrylist extends AppCompatActivity {
    Calendar c = Calendar.getInstance();
    public final int mYear = c.get(Calendar.YEAR);
    public  final int mMonth = c.get(Calendar.MONTH);
    public final  int mDay = c.get(Calendar.DAY_OF_MONTH);
    public  int pday;
    public int pmonth;
    public int pyear;
    String fromdate;
    CustomAdapter customAdapter;
    ArrayList<MilkEntry> codelist=new ArrayList<>();
    TextView date;
    FloatingActionButton fabadd;
    ListView listView;
    RadioButton rdmorning,rdevening;
    RadioGroup grptime;
    String pageType = "A0";
    long flagtime=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_addmilkentrylist);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view = getSupportActionBar().getCustomView();
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar, null);
        TextView tittle = (TextView) mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Milk Purchase Report");
        try {
            Bundle bundle = getIntent().getExtras();
            pageType = bundle.getString("pageType");
           // getIntent().putExtra("pageType", "0");

        }catch (Exception er){
            pageType = "A0";
        }
            if("A1".equalsIgnoreCase(pageType)) {
                tittle.setText("Milk Sale Report");
            }

        ImageButton imgbutton = (ImageButton) mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Intent intent = new Intent(Addmilkentrylist.this, options.class);
                //startActivity(intent);
            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        grptime=(RadioGroup)findViewById(com.vsi.smart.dairy1.R.id.grptime);
        listView = (ListView) findViewById(com.vsi.smart.dairy1.R.id.listviewstatement);

        fabadd = (FloatingActionButton) findViewById(com.vsi.smart.dairy1.R.id.fabadd);
        fabadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Addmilkentrylist.this, Addmilkentry.class);
                intent.putExtra("pageType",pageType);
                intent.putExtra("Date",date.getText().toString());
                intent.putExtra("ME",flagtime);
                startActivity(intent);
            }
        });

        ImageButton calendarbtn = (ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate);
        date = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtdate);

        calendarbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Addmilkentrylist.this,
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
                                calllist(currdate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        java.util.Calendar c = java.util.Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formatteddate = df.format(c.getTime());
        date.setText(formatteddate);
        calllist(formatteddate);

        grptime.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
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
                calllist(date.getText().toString());
            }
        });
    }

    class MilkEntry
    {
        public String code;
        public String lit;
        public String fat;
        public String snf;
        public String rate;
        public String amt;
        public String cname;
        public String rno;
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Addmilkentrylist.this,Login.class);
            startActivity(intent);
        }else{ calllist(date.getText().toString());}
    }

    private class LongOperation_1 extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            CallSoap cs = new CallSoap();
            String RESULT = "";
            if("A0".equalsIgnoreCase(pageType))
            {
                RESULT =  cs.MilkCollectionList(ApplicationRuntimeStorage.COMPANYID, ApplicationRuntimeStorage.USERID, params[0],""+flagtime);
            }else {
                RESULT =  cs.MilkCollectionList_Sale(ApplicationRuntimeStorage.COMPANYID, ApplicationRuntimeStorage.USERID, params[0],""+flagtime);
            }
            return RESULT;
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                codelist.clear();
                double fat1=0,liter1=0,snf1=0,rate1,amt1=0;
                try {
                    JSONArray jsonarray = new JSONArray(result);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        MilkEntry mentry = new MilkEntry();
                        String acccode = jsonobject.getString("acccode");
                        mentry.code = acccode;
                        String liters = jsonobject.getString("liters");
                        mentry.lit = liters;
                        double ltr = 0;
                        try {
                            ltr = Double.parseDouble(liters);
                            liter1 = liter1+ltr;
                        } catch (Exception e) { }

                        String fat = jsonobject.getString("fat");
                        mentry.fat = fat;
                        try {
                            fat1 = fat1+ (Double.parseDouble(fat)*ltr);
                        } catch (Exception e) { }

                        String snf = jsonobject.getString("snf");
                        mentry.snf = snf;
                        try {
                            snf1 = snf1 + (Double.parseDouble(snf)*ltr);
                        } catch (Exception e) { }

                        String rate = jsonobject.getString("rate");
                        mentry.rate = rate;

                        String amt = jsonobject.getString("amt");
                        mentry.amt = amt;
                        try {
                            amt1 =amt1+ Double.parseDouble(amt);
                        } catch (Exception e) { }

                        String rno = jsonobject.getString("rno");
                        mentry.rno = rno;

                        String cname = jsonobject.getString("cname");
                        mentry.cname = cname;
                        codelist.add(i,mentry   );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                TextView idliter = (TextView) findViewById(com.vsi.smart.dairy1.R.id.id_liter);
                TextView idfat = (TextView) findViewById(com.vsi.smart.dairy1.R.id.id_fat);
                TextView idsnf = (TextView) findViewById(com.vsi.smart.dairy1.R.id.id_snf);
                TextView idrate = (TextView) findViewById(com.vsi.smart.dairy1.R.id.id_rate);
                TextView idamt = (TextView) findViewById(com.vsi.smart.dairy1.R.id.id_amt);
                DecimalFormat f = new DecimalFormat("##.00");
                try
                {
                    idliter.setText("" + f.format(liter1));
                    idamt.setText("" +  f.format(amt1));
                    idrate.setText("" +  f.format((amt1 / liter1)));
                    idfat.setText("" +  f.format((fat1 / liter1)));
                    idsnf.setText("" +  f.format((snf1 / liter1)));
                }catch (Exception er) {
                    idliter.setText("" + f.format(liter1));
                    idamt.setText("" +  f.format(amt1));
                    idrate.setText("0.0");
                    idfat.setText("0.0" );
                    idsnf.setText("0.0");
                }

                customAdapter=new CustomAdapter(getBaseContext());
                listView.setAdapter(customAdapter);
                customAdapter.notifyDataSetChanged();

            }catch (Exception e) {e.printStackTrace();}
            //hide the dialog
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }
        ProgressDialog asyncDialog = new ProgressDialog(Addmilkentrylist.this);
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

    private void calllist(String fromdate1)
    {
        new LongOperation_1().execute(date.getText().toString());
    }

    private class CustomAdapter extends BaseAdapter {
        Context Addmilkentrylist;
        public CustomAdapter(Context Addmilkentrylist) {
            this.Addmilkentrylist = Addmilkentrylist;
        }

        @Override
        public int getCount() {
            return codelist.size();
        }

        @Override
        public Object getItem(int position)
        {
            return codelist.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {

//            LayoutInflater inflater=getLayoutInflater();
//            View rowView=inflater.inflate(R.layout.listoflistview, null,true);

            View rowView;
            LayoutInflater inflater=(LayoutInflater)Addmilkentrylist.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView=new View(Addmilkentrylist);
            rowView=inflater.inflate(com.vsi.smart.dairy1.R.layout.listoflistview,null);

            MilkEntry mentry = codelist.get(position);

            TextView code = (TextView) rowView.findViewById(com.vsi.smart.dairy1.R.id.txtcode);
            code.setText(mentry.code);

            TextView cname = (TextView) rowView.findViewById(com.vsi.smart.dairy1.R.id.txtcname);
            cname.setText(mentry.cname);

            TextView txtlit = (TextView) rowView.findViewById(com.vsi.smart.dairy1.R.id.txtlit);
            txtlit.setText(mentry.lit);

            TextView txtfat = (TextView) rowView.findViewById(com.vsi.smart.dairy1.R.id.txtfat);
            txtfat.setText(mentry.fat);

            TextView txtsnf = (TextView) rowView.findViewById(com.vsi.smart.dairy1.R.id.txtsnf);
            txtsnf.setText(mentry.snf);

            TextView txtamount = (TextView) rowView.findViewById(com.vsi.smart.dairy1.R.id.txtamount);
            txtamount.setText(mentry.amt);

            TextView txtrate = (TextView) rowView.findViewById(com.vsi.smart.dairy1.R.id.txtrate);
            txtrate.setText(mentry.rate);

            return rowView;
        }
    }

    abstract class ValueFilter extends Filter {
        @SuppressLint("DefaultLocale")
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
//                FilterResults results = new FilterResults();
//                ProductList applist = null;
//                if (constraint != null && constraint.length() > 0) {
//
//                    ArrayList<ProductList> filterList = new ArrayList<ProductList>();
//                    for (int i = 0; i < mStringFilterList.size(); i++) {
//                        if ((mStringFilterList.get(i).getTitle().toUpperCase())
//                                .contains(constraint.toString().toUpperCase())) {
//
//                            applist = new ProductList(mStringFilterList.get(i)
//                                    .getTitle(), mStringFilterList.get(i)
//                                    .getSubTitle());
//
//                            filterList.add(applist);
//                        }
//                    }
//                    results.count = filterList.size();
//                    results.values = filterList;
//                } else {
//                    results.count = mStringFilterList.size();
//                    results.values = mStringFilterList;
//                }
//                return results;

//            }
//
//
//        }
            return null;
        }
    }
}
