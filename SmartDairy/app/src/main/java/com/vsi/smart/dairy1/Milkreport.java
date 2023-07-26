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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Milkreport extends AppCompatActivity
{
    int primarycolor;
    Button btnmorning,btnevening;
    int me = 0;
    Calendar c = Calendar.getInstance();
    public final int mYear = c.get(Calendar.YEAR);
    public  final int mMonth = c.get(Calendar.MONTH);
    public final  int mDay = c.get(Calendar.DAY_OF_MONTH);
    public int i;
    public  int pday;
    public int pmonth;
    public int pyear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_milkreport);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Milk Report");

        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Milkreport.this,options.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        final TextView date=(TextView)findViewById(com.vsi.smart.dairy1.R.id.date);
        final TextView date2=(TextView)findViewById(com.vsi.smart.dairy1.R.id.date2);
        ImageButton datebtn=(ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate);
        ImageButton datebtn2=(ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate2);
        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Milkreport.this,
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
//                                getWebData(date.getText().toString());

                            }
                        },mYear,mMonth,mDay);

                datePickerDialog.show();
            }
        });

                datebtn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(Milkreport.this,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        date2.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                        pyear = year;
                                        pmonth = monthOfYear;
                                        pday = dayOfMonth;
                                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                        Date date1 = new Date(year - 1900, (monthOfYear), dayOfMonth);
                                        String currdate = "";
                                        currdate = df.format(date1.getTime());
                                        date2.setText(currdate);

//                                Toast.makeText(Birthday.this,pyear+ "-"+pmonth+"-"+pday, Toast.LENGTH_SHORT).show();
//                                        getWebData(date.getText().toString());


                                    }
                                }, mYear, mMonth, mDay);

                        datePickerDialog.show();
                    }
                });

                        btnmorning = (Button) findViewById(com.vsi.smart.dairy1.R.id.btnmorning);
                        btnevening = (Button) findViewById(com.vsi.smart.dairy1.R.id.btnevening);
                        primarycolor = Color.parseColor("#ff14b590");
                        btnmorning.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                me = 0;
//                                refreshData();
                                btnmorning.setBackgroundColor(Color.GREEN);
                                btnevening.setBackgroundColor(primarycolor);
                            }
                        });

                        btnevening.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                me = 1;
//                                refreshData();
                                btnmorning.setBackgroundColor(primarycolor);
                                btnevening.setBackgroundColor(Color.GREEN);
                            }
                        });
                    }
    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Milkreport.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
                }