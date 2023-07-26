package com.vsi.smart.dairy1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

public class Sansthamilkcollection extends AppCompatActivity {

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
        setContentView(com.vsi.smart.dairy1.R.layout.activity_sansthamilkcollection);

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
        tittle.setText("संस्था संकलन रजिस्टर");

        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Sansthamilkcollection.this,Login.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Sansthamilkcollection.this,
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(Sansthamilkcollection.this,
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
            String JSON_RESULT = cs.GetMilkCollectionData_SansthaRegister(ApplicationRuntimeStorage.COMPANYID, ApplicationRuntimeStorage.USERID, datestr,datestr2);
            try {
                JSONArray jsonarray = new JSONArray(JSON_RESULT);
                double tltr=0;
                double tamt=0;
                double tnash=0;
                double tjapt=0;
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);

                    String  rno = jsonobject.getString("receiptDate");
                    String  me = jsonobject.getString("me");
                    String  cb = jsonobject.getString("cb");
                    String  liters = jsonobject.getString("liters");
                    double good_ltr = Double.parseDouble(liters); tltr = tltr+good_ltr;
                    String  fat= jsonobject.getString("fat");
                    double fat2 = Double.parseDouble(fat);
                    String  snf= jsonobject.getString("snf");
                    double snf2 = Double.parseDouble(snf);
                    String  rate= jsonobject.getString("rate");
                    double rate2 = Double.parseDouble(rate);
                    String  amt= jsonobject.getString("amt");
                    double amt2 = Double.parseDouble(amt);
                    double low_ltr =  Double.parseDouble(amt);tamt = tamt+low_ltr;
//                    String nashcanltr=jsonobject.getString("nashcanltr");
//                    double nash_ltr =  Double.parseDouble(nashcanltr);tnash = tnash+nash_ltr;
//                    String japtcanltr=jsonobject.getString("japtcanltr");
//                    double japt_ltr = Double.parseDouble(japtcanltr);tjapt = tjapt+japt_ltr;
                    TableRow row= new TableRow(Sansthamilkcollection.this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);

                    TextView receiptDate1 = new TextView(Sansthamilkcollection.this);
                    receiptDate1.setText("\n\t\t"+rno);
                    row.addView(receiptDate1);

                    TextView me1 = new TextView(Sansthamilkcollection.this);
                    if(me.equalsIgnoreCase("0")) {
                        me1.setText("\n\tसका." );
                    }else{
                        me1.setText("\n\tसायं." );
                    }
                    row.addView(me1);


                    TextView cb1 = new TextView(Sansthamilkcollection.this);
                    if(cb.equalsIgnoreCase("0")) {
                        cb1.setText("\n\t\tगाय");
                    }else{
                        cb1.setText("\n\t\tम्हैस");
                    }
                    row.addView(cb1);

                    TextView liters1 = new TextView(Sansthamilkcollection.this);
                    liters1.setText("\n\t\t"+f.format(good_ltr));
                    row.addView(liters1);

                    TextView fat1 = new TextView(Sansthamilkcollection.this);
                    fat1.setText("\n\t\t"+f.format(fat2));
                    row.addView(fat1);

                    TextView snf1 = new TextView(Sansthamilkcollection.this);
                    snf1.setText("\n\t\t"+f.format(snf2));
                    row.addView(snf1);


                    TextView rate1 = new TextView(Sansthamilkcollection.this);
                    rate1.setText("\n\t\t"+f.format(rate2));
                    row.addView(rate1);

                    TextView amt1 = new TextView(Sansthamilkcollection.this);
                    amt1.setText("\n\t\t"+f.format(amt2));
                    row.addView(amt1);



                    table1.addView(row);
                }



                TextView tliter1=(TextView) findViewById(com.vsi.smart.dairy1.R.id.tliter);tliter1.setText(""+f.format( tltr));
                TextView tlow1=(TextView) findViewById(com.vsi.smart.dairy1.R.id.tlow);tlow1.setText(""+f.format(tamt/tltr));
                TextView tnash1=(TextView) findViewById(com.vsi.smart.dairy1.R.id.tnash);tnash1.setText(""+f.format(tamt));

                TextView tjapt1 = (TextView) findViewById(com.vsi.smart.dairy1.R.id.tjapt);
                tjapt1.setText("" );



            } catch (Exception e) {
            }
        } catch (Exception rr) {
        }
    }
    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Sansthamilkcollection.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
