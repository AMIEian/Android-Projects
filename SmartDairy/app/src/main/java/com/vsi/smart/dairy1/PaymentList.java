package com.vsi.smart.dairy1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PaymentList extends AppCompatActivity {
    public Calendar c = Calendar.getInstance();
    public final int mYear = c.get(Calendar.YEAR);
    public  final int mMonth = c.get(Calendar.MONTH);
    public final  int mDay = c.get(Calendar.DAY_OF_MONTH);
    public int i;
    public  int pday;
    public int pmonth;
    public int pyear;
    public TableLayout table1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_payment_list);

        final TextView date=(TextView)findViewById(com.vsi.smart.dairy1.R.id.date);
        final TextView date2=(TextView)findViewById(com.vsi.smart.dairy1.R.id.date2);
        ImageButton datebtn=(ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate);
        ImageButton datebtn2=(ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate2);

        table1=(TableLayout)findViewById(com.vsi.smart.dairy1.R.id.table1);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("पेमेंट रजिस्टर");

        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(PaymentList.this,Login.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DatePickerDialog datePickerDialog = new DatePickerDialog(PaymentList.this,
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

//
////                                Toast.makeText(Birthday.this,pyear+ "-"+pmonth+"-"+pday, Toast.LENGTH_SHORT).show();
////                                getWebData(date.getText().toString());
//
                            }
                        },mYear,mMonth,mDay);

                datePickerDialog.show();
            }
        });
        datebtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DatePickerDialog datePickerDialog = new DatePickerDialog(PaymentList.this,
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

////                                Toast.makeText(Birthday.this,pyear+ "-"+pmonth+"-"+pday, Toast.LENGTH_SHORT).show();
                                getWebData(date.getText().toString(),date2.getText().toString());
//
                            }
                        },mYear,mMonth,mDay);

                datePickerDialog.show();
            }
        });


    }


    private void getWebData(String datestr,String datestr2) {
        try {
            table1=(TableLayout)findViewById(com.vsi.smart.dairy1.R.id.table1);
            int cnter = table1.getChildCount();
            for (int i = 0; i < cnter; i++) {
                View child = table1.getChildAt(i);
                if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
            }
            CallSoap cs = new CallSoap();
            DecimalFormat f = new DecimalFormat("##.00");
            String JSON_RESULT = cs.GetMilkPaymentList(ApplicationRuntimeStorage.COMPANYID,  datestr,datestr2);
            try {
                JSONArray jsonarray = new JSONArray(JSON_RESULT);
                double tltr=0;
                double tpamt=0;
                double tdamt=0;
                double tnamt=0;
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);

                    String  rno = jsonobject.getString("userid");
                    String  cname = jsonobject.getString("cname");

                    String  liters = jsonobject.getString("liters");
                    double good_ltr = Double.parseDouble(liters); tltr = tltr+good_ltr;

                    String  pamt= jsonobject.getString("pamt");
                    double good_pamt = Double.parseDouble(pamt); tpamt = tpamt+good_pamt;

                    String  damt= jsonobject.getString("damt");
                    double good_damt = Double.parseDouble(damt); tdamt = tdamt+good_damt;

                   // String  namt= jsonobject.getString("namt");
                   // double good_namt = Double.parseDouble(namt); tnamt = tnamt+good_namt;



                    TableRow row= new TableRow(PaymentList.this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);

                    TextView receiptDate1 = new TextView(PaymentList.this);
                    receiptDate1.setText("\n\t\t"+rno);
                    row.addView(receiptDate1);

                    TextView cname1 = new TextView(PaymentList.this);
                    cname1.setText("\n\t\t"+cname);
                    row.addView(cname1);

                    TextView liters1 = new TextView(PaymentList.this);
                    liters1.setText("\n\t\t"+f.format(good_ltr));
                    row.addView(liters1);

                    TextView fat1 = new TextView(PaymentList.this);
                    fat1.setText("\n\t\t"+f.format(good_pamt));
                    row.addView(fat1);

                    TextView snf1 = new TextView(PaymentList.this);
                    snf1.setText("\n\t\t"+f.format(good_damt));
                    row.addView(snf1);


                    TextView rate1 = new TextView(PaymentList.this);
                    rate1.setText("\n\t\t"+f.format(good_pamt-good_damt));
                    row.addView(rate1);





                    table1.addView(row);
                }



                TextView tliter1=(TextView) findViewById(com.vsi.smart.dairy1.R.id.tliter);tliter1.setText(""+f.format( tltr));
                TextView tlow1=(TextView) findViewById(com.vsi.smart.dairy1.R.id.tlow);tlow1.setText(""+f.format(tpamt));
                TextView tnash1=(TextView) findViewById(com.vsi.smart.dairy1.R.id.tnash);tnash1.setText(""+f.format(tdamt));

                TextView tjapt1 = (TextView) findViewById(com.vsi.smart.dairy1.R.id.tjapt);  tjapt1.setText(""+f.format(tpamt-tdamt) );



            } catch (Exception e) {
            }
        } catch (Exception rr) {
        }
    }
    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(PaymentList.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
