package com.vsi.smart.dairy1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import java.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Searchr extends AppCompatActivity {
    Calendar c = Calendar.getInstance();
    public final int mYear = c.get(Calendar.YEAR);
    public  final int mMonth = c.get(Calendar.MONTH);
    public final  int mDay = c.get(Calendar.DAY_OF_MONTH);
    Button btn_Counter,btn_Product;
    TextView currdate;
    public  int pday;
    public int pmonth;
    public int pyear;
    TableLayout table1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_reports);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Reports");

        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Searchr.this,options.class);
                startActivity(intent);            }
        });

        currdate=(TextView) findViewById(com.vsi.smart.dairy1.R.id.txtcurrdate);
        java.util.Calendar c= java.util.Calendar.getInstance();
        SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
        String formatteddate=df.format(c.getTime());
        currdate.setText(formatteddate);

        final TextView date = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtcurrdate);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Searchr.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                pyear = year;
                                pmonth = monthOfYear;
                                pday = dayOfMonth;

                                SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
                                Date date1 = new Date(year-1900, (monthOfYear), dayOfMonth);
                                String currdate11="";
                                currdate11=df.format(date1.getTime());
                                date.setText(currdate11);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });



        add_HOD_LIST();
        addListenerOn_HOD_Selection();;
        addListenerOn_RMO_Selection();
        addListenerOn_MO_Selection();

        table1 = (TableLayout) findViewById(com.vsi.smart.dairy1.R.id.table2);
        Button order=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnorder);
        order.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {

                                         String hod = ((Spinner) findViewById(com.vsi.smart.dairy1.R.id.spn_hod_list)).getSelectedItem().toString();
                                         String rmo = ((Spinner) findViewById(com.vsi.smart.dairy1.R.id.spn_rmo_list)).getSelectedItem().toString();
                                         String mo = ((Spinner) findViewById(com.vsi.smart.dairy1.R.id.spn_mo_list)).getSelectedItem().toString();
                                         String sr = ((Spinner) findViewById(com.vsi.smart.dairy1.R.id.spn_sr_list)).getSelectedItem().toString();
                                         String selectedUser = "";
                                         TextView txt_currDt = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtcurrdate);
                                         String currDt = currdate.getText().toString();

                                         if("PLEASE SELECT".equalsIgnoreCase(sr)){
                                             if("PLEASE SELECT".equalsIgnoreCase(mo)){
                                                 if("PLEASE SELECT".equalsIgnoreCase(rmo)){
                                                     if("PLEASE SELECT".equalsIgnoreCase(hod)){
                                                         Toast.makeText(Searchr.this, "Please Select Reporting Person", Toast.LENGTH_SHORT).show();
                                                            return;
                                                     }else{

                                                         selectedUser = hod.split(" ")[0].toString();
                                                     }

                                                 }else{

                                                     selectedUser = rmo.split(" ")[0].toString();
                                                 }

                                             }else{

                                                 selectedUser = mo.split(" ")[0].toString();
                                             }

                                         }else{

                                             selectedUser = sr.split(" ")[0].toString();
                                         }


                                         table1 = (TableLayout) findViewById(com.vsi.smart.dairy1.R.id.table1);
                                         int cnter = table1.getChildCount();
                                         for (int i = 0; i < cnter; i++) {
                                             View child = table1.getChildAt(i);
                                             if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
                                         }


                                         CallSoap cs=new CallSoap();
                                         String JSON_RESULT= cs.GetDailySaleList(ApplicationRuntimeStorage.COMPANYID,selectedUser,currDt); // Web Call to populate JSON ItemList
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

                                                 TableRow row= new TableRow(Searchr.this);
                                                 TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                                                 row.setLayoutParams(lp);

                                                 TextView orderid1 = new TextView(Searchr.this);
                                                 orderid1.setText("\n\t\t\t"+orderId);
                                                 row.addView(orderid1);

                                                 TextView orderstatus1 = new TextView(Searchr.this);
                                                 if("".equalsIgnoreCase(orderstatus.trim())) {

                                                     ImageButton edit=new ImageButton(Searchr.this);
                                                     edit.setImageResource(com.vsi.smart.dairy1.R.drawable.ic_edit_black_24dp);
                                                     String gray2="#fffafafa";
                                                     int gray3= Color.parseColor(gray2);
                                                     edit.setBackgroundColor(gray3);
                                                     edit.setOnClickListener(new View.OnClickListener() {
                                                         @Override
                                                         public void onClick(View view)
                                                         {
                                                             Intent intent=new Intent(Searchr.this,AddDailySale.class);
                                                             intent.putExtra("orderid",orderId);
                                                             intent.putExtra("isUpdate","NO");
                                                             startActivity(intent);
                                                         }
                                                     });
                                                     row.addView(edit);
                                                 }
                                                 row.addView(orderstatus1);

                                                 TextView Time1 = new TextView(Searchr.this);
                                                 Time1.setText("\n\t\t\t\t\t"+Time);
                                                 row.addView(Time1);

                                                 TextView City1 = new TextView(Searchr.this);
                                                 City1.setText("\n\t\t\t\t\t"+City);
                                                 row.addView(City1);

                                                 TextView Area1 = new TextView(Searchr.this);
                                                 Area1.setText("\n\t\t\t\t\t"+Area);
                                                 row.addView(Area1);

//                                        TextView Comment1 = new TextView(List_Sale_Details.this);
//                                        Comment1.setText("\n\t\t\t\t\t"+Comment);
//                                        row.addView(Comment1);

                                                 table1.addView(row);
                                             }

                                         }catch (Exception e) {e.printStackTrace();}


                                     }
                                 });


        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);




//        btn_Product=(Button)findViewById(R.id.btn_Product);
//        btn_Counter=(Button)findViewById(R.id.btn_Counter);
//
//        btn_Product.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                Intent intent=new Intent(Searchr.this,Productc.class);
//                startActivity(intent);
//            }
//        });
//
//        btn_Counter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                Intent intent=new Intent(Searchr.this,Counterc.class);
//                startActivity(intent);
//            }
//        });

    }

    public void add_HOD_LIST() {
        Spinner spinner2 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spn_hod_list);
        List<String> list = new ArrayList<>();
        CallSoap cs=new CallSoap();
        String JSON_RESULT= cs.GetReportingList(ApplicationRuntimeStorage.USERID,ApplicationRuntimeStorage.COMPANYID); // Web Call to populate JSON ItemList
        try{
            JSONArray jsonarray = new JSONArray(JSON_RESULT);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String itemCode = jsonobject.getString("Key");
                String itemName = jsonobject.getString("Value");
                list.add(itemName);
            }
        }catch (Exception e) {e.printStackTrace();}
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);
    }
    Spinner spinner_hod;
    Spinner spinner_rmo;
    Spinner spinner_mo;
    Spinner spinner_sr;

    public void addListenerOn_HOD_Selection() {
       spinner_hod = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spn_hod_list);
       spinner_rmo = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spn_rmo_list);

        spinner_hod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String hodSelected = parentView.getItemAtPosition(position).toString();
                if("PLEASE SELECT".equalsIgnoreCase(hodSelected)) return;

                spinner_rmo.setVisibility(View.VISIBLE);
                spinner_mo.setVisibility(View.INVISIBLE);
                spinner_sr.setVisibility(View.INVISIBLE);

                List<String> list = new ArrayList<>();
                if("".equalsIgnoreCase(hodSelected)){
                    Toast.makeText(Searchr.this, "Please Select Reporting Person", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] ss = hodSelected.split(" ");


                CallSoap cs=new CallSoap();
                String JSON_RESULT= cs.GetReportingList(ss[0],ApplicationRuntimeStorage.COMPANYID); // Web Call to populate JSON ItemList
                try{
                    JSONArray jsonarray = new JSONArray(JSON_RESULT);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String itemCode = jsonobject.getString("Key");
                        String itemName = jsonobject.getString("Value");
                        list.add(itemName);
                    }

                }catch (Exception e) {e.printStackTrace();}
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Searchr.this,android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_rmo.setAdapter(dataAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                Toast.makeText(Searchr.this, "Please Select Reporting Person", Toast.LENGTH_SHORT).show();
            }

        });

    }




    public void addListenerOn_RMO_Selection() {
        spinner_rmo = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spn_rmo_list);
        spinner_mo = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spn_mo_list);

        spinner_rmo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String hodSelected = parentView.getItemAtPosition(position).toString();
                if("PLEASE SELECT".equalsIgnoreCase(hodSelected)) return;
                spinner_mo.setVisibility(View.VISIBLE);
                spinner_sr.setVisibility(View.INVISIBLE);

                List<String> list = new ArrayList<>();
                if("".equalsIgnoreCase(hodSelected)){
                    Toast.makeText(Searchr.this, "Please Select Reporting Person", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] ss = hodSelected.split(" ");


                CallSoap cs=new CallSoap();
                String JSON_RESULT= cs.GetReportingList(ss[0],ApplicationRuntimeStorage.COMPANYID); // Web Call to populate JSON ItemList
                try{
                    JSONArray jsonarray = new JSONArray(JSON_RESULT);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String itemCode = jsonobject.getString("Key");
                        String itemName = jsonobject.getString("Value");
                        list.add(itemName);
                    }

                }catch (Exception e) {e.printStackTrace();}
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Searchr.this,android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_mo.setAdapter(dataAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                Toast.makeText(Searchr.this, "Please Select Reporting Person", Toast.LENGTH_SHORT).show();
            }

        });

    }

    public void addListenerOn_MO_Selection() {
        spinner_mo = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spn_mo_list);
        spinner_sr = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spn_sr_list);

        spinner_mo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String hodSelected = parentView.getItemAtPosition(position).toString();
                if("PLEASE SELECT".equalsIgnoreCase(hodSelected)) return;
                spinner_sr.setVisibility(View.VISIBLE);
                List<String> list = new ArrayList<>();
                if("".equalsIgnoreCase(hodSelected)){
                    Toast.makeText(Searchr.this, "Please Select Reporting Person", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] ss = hodSelected.split(" ");


                CallSoap cs=new CallSoap();
                String JSON_RESULT= cs.GetReportingList(ss[0],ApplicationRuntimeStorage.COMPANYID); // Web Call to populate JSON ItemList
                try{
                    JSONArray jsonarray = new JSONArray(JSON_RESULT);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String itemCode = jsonobject.getString("Key");
                        String itemName = jsonobject.getString("Value");
                        list.add(itemName);
                    }

                }catch (Exception e) {e.printStackTrace();}
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Searchr.this,android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_sr.setAdapter(dataAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                Toast.makeText(Searchr.this, "Please Select Reporting Person", Toast.LENGTH_SHORT).show();
            }

        });

    }
    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Searchr.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }

}
