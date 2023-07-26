package com.vsi.smart.dairy1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Tabulardashboard extends AppCompatActivity {

    int primarycolor;
    int buttonState = 0;
    Calendar c = Calendar.getInstance();
    public final int mYear = c.get(Calendar.YEAR);
    public  final int mMonth = c.get(Calendar.MONTH);
    public final  int mDay = c.get(Calendar.DAY_OF_MONTH);
    public int i;
    public  int pday;
    public int pmonth;
    public int pyear;
    int flagper,flagcomp,btncompare,result;
    Button btnall,btnmorning,btnevening,btnpersonal,btncompany;
    TextView litercow,fatcow,snfcow,ratecow,literbuff,fatbuff,snfbuff,ratebuff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_tabulardashboard);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Assessment");

        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Tabulardashboard.this,Login.class);
                startActivity(intent);            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        litercow=(TextView)findViewById(com.vsi.smart.dairy1.R.id.cowliter);
        fatcow=(TextView)findViewById(com.vsi.smart.dairy1.R.id.cowfat);
        snfcow=(TextView)findViewById(com.vsi.smart.dairy1.R.id.cowsnf);
        ratecow=(TextView)findViewById(com.vsi.smart.dairy1.R.id.cowrate);

        literbuff=(TextView)findViewById(com.vsi.smart.dairy1.R.id.buffliter);
        fatbuff=(TextView)findViewById(com.vsi.smart.dairy1.R.id.bufffat);
        snfbuff=(TextView)findViewById(com.vsi.smart.dairy1.R.id.buffsnf);
        ratebuff=(TextView)findViewById(com.vsi.smart.dairy1.R.id.buffrate);


        final TextView date=(TextView)findViewById(com.vsi.smart.dairy1.R.id.date);
        ImageButton datebtn=(ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate);
        btnpersonal=(Button) findViewById(com.vsi.smart.dairy1.R.id.btnpersonal);
        btncompany=(Button) findViewById(com.vsi.smart.dairy1.R.id.btncompany);
        btnall=(Button) findViewById(com.vsi.smart.dairy1.R.id.btnall);
        btnmorning=(Button) findViewById(com.vsi.smart.dairy1.R.id.btnmorning);
        btnevening=(Button) findViewById(com.vsi.smart.dairy1.R.id.btnevening);
        primarycolor= Color.parseColor("#ff14b590");

        if(ApplicationRuntimeStorage.AccessRights.length() > 0) {
            String[] rulesArr =ApplicationRuntimeStorage.AccessRights.split(",");
            for (String rule:rulesArr) {
                if("RULE_DASHBOARD_SUMMARY".equalsIgnoreCase(rule))
                {
                    flagcomp=1;
                    result=0;
                }

                if("RULE_DASHBOARD".equalsIgnoreCase(rule))
                {
                    flagper=1;
                    result=1;

                }

                if(flagcomp==1 && flagper==1)
                {
                    btncompany.setVisibility(View.VISIBLE);
                    btnpersonal.setVisibility(View.VISIBLE);

                }

            }
        }
        btnall.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                buttonState = 0;
                refreshData();
                btnall.setBackgroundColor(Color.GREEN);
                btnmorning.setBackgroundColor(primarycolor);
                btnevening.setBackgroundColor(primarycolor);
            }
        });

        btncompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                btnpersonal.setBackgroundColor(primarycolor);
                btncompany.setBackgroundColor(Color.GREEN);
                btncompare=0;
                result=0;
                getWebData(date.getText().toString());
//                            0 for company data
            }
        });
        btnpersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnpersonal.setBackgroundColor(Color.GREEN);
                btncompany.setBackgroundColor(primarycolor);
                getWebData(date.getText().toString());
                btncompare=1;
                result=1;
//                            1 for personal data
            }
        });

        btnmorning.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                buttonState = 1;
                refreshData();
                btnall.setBackgroundColor(primarycolor);
                btnmorning.setBackgroundColor(Color.GREEN);
                btnevening.setBackgroundColor(primarycolor);
            }
        });

        btnevening.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                buttonState = 2;
                refreshData();
                btnall.setBackgroundColor(primarycolor);
                btnmorning.setBackgroundColor(primarycolor);
                btnevening.setBackgroundColor(Color.GREEN);
            }
        });

        SimpleDateFormat df1=new SimpleDateFormat("dd/MM/yyyy");
        String currdate="";
        Calendar c= Calendar.getInstance();
        currdate=df1.format(c.getTime());
        date.setText(currdate);

        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Tabulardashboard.this,
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
                                Date date1 = new Date(year-1900, (monthOfYear), dayOfMonth);
                                String currdate="";
                                currdate=df.format(date1.getTime());
                                date.setText(currdate);

//                                Toast.makeText(Birthday.this,pyear+ "-"+pmonth+"-"+pday, Toast.LENGTH_SHORT).show();

                                getWebData(date.getText().toString());
                            }
                        },mYear,mMonth,mDay);
                datePickerDialog.show();

            }
        });
        btnall.setBackgroundColor(Color.GREEN); //default selected
        getWebData(date.getText().toString());
        refreshData();

        Button menu1=(Button) findViewById(com.vsi.smart.dairy1.R.id.btn_menu1);
        //click on Order placed button
        menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(Tabulardashboard.this,options.class);
                startActivity(intent);
            }
        });

    }

    private void refreshData()
    {
        setData3();
        setData4();
    }
    private void getWebData(String datestr){
        try{
            CallSoap cs = new CallSoap();
            String JSON_RESULT = cs.GetMilkData(ApplicationRuntimeStorage.COMPANYID,datestr,result,ApplicationRuntimeStorage.USERID );
            try{

                JSONObject jsonobject = new JSONObject(JSON_RESULT);

                MilkData.sangh_all = (float)jsonobject.getDouble("sangh_all");
                MilkData.adp_all = (float)jsonobject.getDouble("adp_all");
                MilkData.parisar_all = (float)jsonobject.getDouble("parisar_all");
                MilkData.cow_good_all = (float)jsonobject.getDouble("cow_good_all");
                MilkData.cow_low_all = (float)jsonobject.getDouble("cow_low_all");
                MilkData.cow_nash_all = (float)jsonobject.getDouble("cow_nash_all");
                MilkData.cow_japt_all = (float)jsonobject.getDouble("cow_japt_all");
                MilkData.buff_good_all = (float)jsonobject.getDouble("buff_good_all");
                MilkData.buff_low_all = (float)jsonobject.getDouble("buff_low_all");
                MilkData.buff_nash_all = (float)jsonobject.getDouble("buff_nash_all");
                MilkData.buff_japt_all = (float)jsonobject.getDouble("buff_japt_all");

                MilkData.sangh_mor = (float)jsonobject.getDouble("sangh_mor");
                MilkData.adp_mor = (float)jsonobject.getDouble("adp_mor");
                MilkData.parisar_mor = (float)jsonobject.getDouble("parisar_mor");
                MilkData.cow_good_mor = (float)jsonobject.getDouble("cow_good_mor");
                MilkData.cow_low_mor = (float)jsonobject.getDouble("cow_low_mor");
                MilkData.cow_nash_mor = (float)jsonobject.getDouble("cow_nash_mor");
                MilkData.cow_japt_mor = (float)jsonobject.getDouble("cow_japt_mor");
                MilkData.buff_good_mor = (float)jsonobject.getDouble("buff_good_mor");
                MilkData.buff_low_mor = (float)jsonobject.getDouble("buff_low_mor");
                MilkData.buff_nash_mor = (float)jsonobject.getDouble("buff_nash_mor");
                MilkData.buff_japt_mor = (float)jsonobject.getDouble("buff_japt_mor");

                MilkData.sangh_eve = (float)jsonobject.getDouble("sangh_eve");
                MilkData.adp_eve = (float)jsonobject.getDouble("adp_eve");
                MilkData.parisar_eve = (float)jsonobject.getDouble("parisar_eve");
                MilkData.cow_good_eve = (float)jsonobject.getDouble("cow_good_eve");
                MilkData.cow_low_eve = (float)jsonobject.getDouble("cow_low_eve");
                MilkData.cow_nash_eve = (float)jsonobject.getDouble("cow_nash_eve");
                MilkData.cow_japt_eve = (float)jsonobject.getDouble("cow_japt_eve");
                MilkData.buff_good_eve = (float)jsonobject.getDouble("buff_good_eve");
                MilkData.buff_low_eve = (float)jsonobject.getDouble("buff_low_eve");
                MilkData.buff_nash_eve = (float)jsonobject.getDouble("buff_nash_eve");
                MilkData.buff_japt_eve = (float)jsonobject.getDouble("buff_japt_eve");

            }catch (Exception e)
            {
                //Toast.makeText(this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }catch (Exception rr){//Toast.makeText(this,""+rr.getMessage(), Toast.LENGTH_SHORT).show();
        }
        refreshData();
    }
    private void setData4() {

        if(buttonState==0){
            literbuff.setText(""+MilkData.buff_good_mor);
            fatbuff.setText(""+MilkData.buff_low_mor);
            snfbuff.setText(""+MilkData.buff_nash_mor);
            ratebuff.setText(""+MilkData.buff_japt_mor);
        }else  if(buttonState==1){
            literbuff.setText(""+MilkData.buff_good_mor);
            fatbuff.setText(""+MilkData.buff_low_mor);
            snfbuff.setText(""+MilkData.buff_nash_mor);
            ratebuff.setText(""+MilkData.buff_japt_mor);
        }else  if(buttonState==2){
            literbuff.setText(""+MilkData.buff_good_mor);
            fatbuff.setText(""+MilkData.buff_low_mor);
            snfbuff.setText(""+MilkData.buff_nash_mor);
            ratebuff.setText(""+MilkData.buff_japt_mor);
        }else
        {
            literbuff.setText("0");
            fatbuff.setText("0");
            snfbuff.setText("0");
            ratebuff.setText("0");
        }
    }
    private void setData3() {

        if(buttonState==0){
            litercow.setText(""+MilkData.cow_good_all);
            fatcow.setText(""+MilkData.cow_low_all);
            snfcow.setText(""+MilkData.cow_nash_all);
            ratecow.setText(""+MilkData.cow_japt_all);
        }else  if(buttonState==1){
            litercow.setText(""+MilkData.cow_good_all);
            fatcow.setText(""+MilkData.cow_low_all);
            snfcow.setText(""+MilkData.cow_nash_all);
            ratecow.setText(""+MilkData.cow_japt_all);
        }else  if(buttonState==2){
            litercow.setText(""+MilkData.cow_good_all);
            fatcow.setText(""+MilkData.cow_low_all);
            snfcow.setText(""+MilkData.cow_nash_all);
            ratecow.setText(""+MilkData.cow_japt_all);
        }else
            {
            litercow.setText("0");
            fatcow.setText("0");
            snfcow.setText("0");
            ratecow.setText("0");
        }
    }
    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Tabulardashboard.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
