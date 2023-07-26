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

public class Customermilkcollection extends AppCompatActivity {

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
        setContentView(com.vsi.smart.dairy1.R.layout.activity_customermilkcollection);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("कस्टमर प्रमाणे संकलन");

        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Customermilkcollection.this,Login.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        final TextView date=(TextView)findViewById(com.vsi.smart.dairy1.R.id.date);
        ImageButton datebtn=(ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate);

        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Customermilkcollection.this,
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
                                getWebData(date.getText().toString());
//
                            }
                        },mYear,mMonth,mDay);

                datePickerDialog.show();
            }
        });
    }

    private void getWebData(String datestr) {
        try {
            table1=(TableLayout)findViewById(com.vsi.smart.dairy1.R.id.table1);
            int cnter = table1.getChildCount();
            for (int i = 0; i < cnter; i++) {
                View child = table1.getChildAt(i);
                if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
            }
            CallSoap cs = new CallSoap();
            String JSON_RESULT = cs.GetMilkCollectionData_CustomerWise(ApplicationRuntimeStorage.COMPANYID, ApplicationRuntimeStorage.USERID, datestr);
            try {
                JSONArray jsonarray = new JSONArray(JSON_RESULT);
                double tltr=0;
                double tamt=0;
                double tnash=0;
                double tjapt=0;
                DecimalFormat f = new DecimalFormat("##.00");
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);

                    String  rno = jsonobject.getString("rno");
                    String  cname = jsonobject.getString("cname");
                    String  me = jsonobject.getString("me");
                    String  cb = jsonobject.getString("cb");
                    String  liters = jsonobject.getString("liters");
                    double good_ltr = Double.parseDouble(liters); tltr = tltr+good_ltr;
                    String  fat= jsonobject.getString("fat");
                    String  snf= jsonobject.getString("snf");
                    String  rate= jsonobject.getString("rate");
                    String  amt= jsonobject.getString("amt");
                    double low_ltr =  Double.parseDouble(amt);tamt = tamt+low_ltr;
//                    String nashcanltr=jsonobject.getString("nashcanltr");
//                    double nash_ltr =  Double.parseDouble(nashcanltr);tnash = tnash+nash_ltr;
//                    String japtcanltr=jsonobject.getString("japtcanltr");
//                    double japt_ltr = Double.parseDouble(japtcanltr);tjapt = tjapt+japt_ltr;
                    TableRow row= new TableRow(Customermilkcollection.this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);

                    TextView receiptDate1 = new TextView(Customermilkcollection.this);
                    receiptDate1.setText("\n\t\t"+rno);
                    row.addView(receiptDate1);

                    TextView cname1 = new TextView(Customermilkcollection.this);
                    cname1.setText("\n\t\t"+cname);
                    row.addView(cname1);

                    TextView cb1 = new TextView(Customermilkcollection.this);
                    if(cb.equalsIgnoreCase("0")) {
                        cb1.setText("\n\t\tगाय");
                    }else{
                        cb1.setText("\n\t\tम्हैस");
                    }
                    row.addView(cb1);

                    TextView liters1 = new TextView(Customermilkcollection.this);
                    liters1.setText("\n\t\t"+liters);
                    row.addView(liters1);

                    TextView fat1 = new TextView(Customermilkcollection.this);
                    fat1.setText("\n\t\t"+fat);
                    row.addView(fat1);

                    TextView snf1 = new TextView(Customermilkcollection.this);
                    snf1.setText("\n\t\t"+snf);
                    row.addView(snf1);


                    TextView rate1 = new TextView(Customermilkcollection.this);
                    rate1.setText("\n\t\t"+rate);
                    row.addView(rate1);

                    TextView amt1 = new TextView(Customermilkcollection.this);
                    amt1.setText("\n\t\t"+amt);
                    row.addView(amt1);

                    TextView me1 = new TextView(Customermilkcollection.this);
                    if(me.equalsIgnoreCase("0")) {
                        me1.setText("\n\tसका." );
                    }else{
                        me1.setText("\n\tसायं." );
                    }
                    row.addView(me1);







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
            Intent intent=new Intent(Customermilkcollection.this,Login.class);
            startActivity(intent);
        }else{ }
        super.onResume();
    }
}
