package com.vsi.smart.dairy1;

import android.content.Intent;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.DatePicker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class List_Sale_Details extends AppCompatActivity {
    Calendar c = Calendar.getInstance();
    public final int mYear = c.get(Calendar.YEAR);
    public  final int mMonth = c.get(Calendar.MONTH);
    public final  int mDay = c.get(Calendar.DAY_OF_MONTH);
    public  int pday;
    public int pmonth;
    public int pyear;
    TableLayout table1;
    TextView currdate;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_list_sale_details);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Order Details");

        currdate=(TextView)findViewById(com.vsi.smart.dairy1.R.id.txtcurrdate);
        java.util.Calendar c= java.util.Calendar.getInstance();
        SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
        String formatteddate=df.format(c.getTime());
        currdate.setText(formatteddate);


        table1=(TableLayout)findViewById(com.vsi.smart.dairy1.R.id.table1);

        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(List_Sale_Details.this,Menu_Retail_Sale.class);
                startActivity(intent);
            }
        });
        ImageButton calendarbtn = (ImageButton) findViewById(com.vsi.smart.dairy1.R.id.btndate);
        final TextView date = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtdate);
        calendarbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(List_Sale_Details.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                currdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                pyear = year;
                                pmonth = monthOfYear;
                                pday = dayOfMonth;

                                SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
                                Date date1 = new Date(year-1900, (monthOfYear), dayOfMonth);
                                String currdate11="";
                                currdate11=df.format(date1.getTime());
                                currdate.setText(currdate11);

                                table1 = (TableLayout) findViewById(com.vsi.smart.dairy1.R.id.table1);
                                int cnter = table1.getChildCount();
                                for (int i = 0; i < cnter; i++) {
                                    View child = table1.getChildAt(i);
                                    if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
                                }


                                CallSoap cs=new CallSoap();
                                String JSON_RESULT= cs.GetDailySaleList(ApplicationRuntimeStorage.COMPANYID,ApplicationRuntimeStorage.USERID,currdate.getText().toString()); // Web Call to populate JSON ItemList
                                try{
                                    JSONArray jsonarray = new JSONArray(JSON_RESULT);
                                    for (int i = 0; i < jsonarray.length(); i++) {
                                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                                        String  orderstatus = jsonobject.getString("orderStatus");
                                        final String  orderId = jsonobject.getString("orderId");
                                        String  City= jsonobject.getString("City");
                                        String  Area= jsonobject.getString("Area");
                                        String  Time= jsonobject.getString("ordertime");
                                        String  Comment= jsonobject.getString("Comment");
                                        String srno=jsonobject.getString("srno");

                                        TableRow row= new TableRow(List_Sale_Details.this);
                                        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                                        row.setLayoutParams(lp);

                                        TextView orderid1 = new TextView(List_Sale_Details.this);
                                        orderid1.setText("\n\t\t\t"+orderId);
                                        row.addView(orderid1);

                                        TextView orderstatus1 = new TextView(List_Sale_Details.this);
                                        if("".equalsIgnoreCase(orderstatus.trim())) {

                                            ImageButton edit=new ImageButton(List_Sale_Details.this);
                                            edit.setImageResource(com.vsi.smart.dairy1.R.drawable.ic_edit_black_24dp);
                                            String gray2="#fffafafa";
                                            int gray3= Color.parseColor(gray2);
                                            edit.setBackgroundColor(gray3);
                                            edit.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view)
                                                {
                                                    Intent intent=new Intent(List_Sale_Details.this,AddDailySale.class);
                                                    intent.putExtra("orderid",orderId);
                                                    startActivity(intent);
                                                }
                                            });
                                            row.addView(edit);
                                        }
                                        row.addView(orderstatus1);

                                        TextView Time1 = new TextView(List_Sale_Details.this);
                                        Time1.setText("\n\t\t\t\t\t"+Time);
                                        row.addView(Time1);

                                        TextView City1 = new TextView(List_Sale_Details.this);
                                        City1.setText("\n\t\t\t\t\t"+City);
                                        row.addView(City1);

                                        TextView Area1 = new TextView(List_Sale_Details.this);
                                        Area1.setText("\n\t\t\t\t\t"+Area);
                                        row.addView(Area1);

//                                        TextView Comment1 = new TextView(List_Sale_Details.this);
//                                        Comment1.setText("\n\t\t\t\t\t"+Comment);
//                                        row.addView(Comment1);

                                        table1.addView(row);
                                    }

                                }catch (Exception e) {e.printStackTrace();}
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        Calendar c1= Calendar.getInstance();
        SimpleDateFormat df1=new SimpleDateFormat("dd/MM/yyyy");
        String Orderdate=df1.format(c1.getTime());

        CallSoap cs=new CallSoap();
        String JSON_RESULT= cs.GetDailySaleList(ApplicationRuntimeStorage.COMPANYID,ApplicationRuntimeStorage.USERID,Orderdate); // Web Call to populate JSON ItemList
        try{
            JSONArray jsonarray = new JSONArray(JSON_RESULT);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String  orderstatus = jsonobject.getString("orderStatus");
                final String  orderId = jsonobject.getString("orderId");
                String  City= jsonobject.getString("City");
                String  Area= jsonobject.getString("Area");
                String  Time= jsonobject.getString("ordertime");
                String  Comment= jsonobject.getString("Comment");
                String srno=jsonobject.getString("srno");

                TableRow row= new TableRow(List_Sale_Details.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                TextView orderid1 = new TextView(List_Sale_Details.this);
                orderid1.setText("\n\t\t\t"+orderId);
                row.addView(orderid1);

                TextView orderstatus1 = new TextView(List_Sale_Details.this);
                if("".equalsIgnoreCase(orderstatus.trim())) {

                    ImageButton edit=new ImageButton(List_Sale_Details.this);
                    edit.setImageResource(com.vsi.smart.dairy1.R.drawable.ic_edit_black_24dp);
                    String gray2="#fffafafa";
                    int gray3= Color.parseColor(gray2);
                    edit.setBackgroundColor(gray3);
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            Intent intent=new Intent(List_Sale_Details.this,AddDailySale.class);
                            intent.putExtra("orderid",orderId);
                            startActivity(intent);
                        }
                    });
                    row.addView(edit);
                }
                row.addView(orderstatus1);

//                TextView srno1=new TextView(List_Sale_Details.this);
//                srno1.setText("\t\t"+srno);
//                row.addView(srno1);

                TextView Time1 = new TextView(List_Sale_Details.this);
                Time1.setText("\n\t\t\t\t\t"+Time);
                row.addView(Time1);

                TextView City1 = new TextView(List_Sale_Details.this);
                City1.setText("\n\t\t\t\t\t"+City);
                row.addView(City1);

                TextView Area1 = new TextView(List_Sale_Details.this);
                Area1.setText("\n\t\t\t\t\t"+Area);
                row.addView(Area1);

//                TextView Comment1 = new TextView(List_Sale_Details.this);
//                Comment1.setText("\n\t\t\t\t\t"+Comment);
//                row.addView(Comment1);

                table1.addView(row);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(List_Sale_Details.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}