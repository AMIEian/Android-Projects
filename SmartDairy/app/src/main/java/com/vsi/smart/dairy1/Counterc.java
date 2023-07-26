package com.vsi.smart.dairy1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Counterc extends AppCompatActivity {

    Button btn_search;
    TableLayout table1,table2,table3,table4;
    android.icu.util.Calendar c = android.icu.util.Calendar.getInstance();
    public final int mYear = c.get(android.icu.util.Calendar.YEAR);
    public  final int mMonth = c.get(android.icu.util.Calendar.MONTH);
    public final  int mDay = c.get(android.icu.util.Calendar.DAY_OF_MONTH);
    public  int pday;
    public int pmonth;
    public int pyear;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_counterc);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Counterwise Search");

        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Counterc.this,Searchr.class);
                startActivity(intent);            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        btn_search=(Button)findViewById(com.vsi.smart.dairy1.R.id.id_Searchbtn);
        table1=(TableLayout)findViewById(com.vsi.smart.dairy1.R.id.table1); table2=(TableLayout)findViewById(com.vsi.smart.dairy1.R.id.table2);
        table3=(TableLayout)findViewById(com.vsi.smart.dairy1.R.id.table3);table4=(TableLayout)findViewById(com.vsi.smart.dairy1.R.id.table4);

        ImageButton calendarbtn = (ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate);

        final TextView date = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtdate);
        calendarbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Counterc.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                pyear = year;
                                pmonth = monthOfYear;
                                pday = dayOfMonth;
                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();

            }
        });

        ImageButton calendarbtn2 = (ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate2);

        final TextView date2 = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtdate2);
        calendarbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Counterc.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                date2.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                pyear = year;
                                pmonth = monthOfYear;
                                pday = dayOfMonth;
//                                getdate=date.getText().toString();
//                                Toast.makeText(Birthday.this,pyear+ "-"+pmonth+"-"+pday, Toast.LENGTH_SHORT).show();

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();

            }
        });

        btn_search.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
               table1.setVisibility(View.VISIBLE);
                table2.setVisibility(View.VISIBLE);
                table3.setVisibility(View.VISIBLE);
                table4.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Counterc.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
