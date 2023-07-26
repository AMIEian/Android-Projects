package com.vsi.smart.dairy1;



import android.app.DatePickerDialog;

import android.app.TimePickerDialog;
import android.content.Intent;




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
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;



public class AddDailySale extends AppCompatActivity {

    java.util.Calendar c = Calendar.getInstance();
    public final int mYear = c.get(Calendar.YEAR);
    public  final int mMonth = c.get(Calendar.MONTH);
    public final  int mDay = c.get(Calendar.DAY_OF_MONTH);
    TimePickerDialog timePickerDialog;
    TimePicker timePicker;
    public  int pday;
    public int pmonth;
    public int pyear;
    public int phour;
    public int pminute;
    public String getTime;
    EditText etxArea,etxCity,etxComment;
    TableLayout table1;
    Button add1,btnsubmit;
    TextView currdate,txtunit,txttime;

String edit_orderid="";
    String isedit="YES";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_adddailysale);

        try {
            Bundle bundle = getIntent().getExtras();
            edit_orderid = bundle.getString("orderid");
            getIntent().putExtra("orderid", "");

        }catch (Exception er){
            edit_orderid = "";

        }
        try {
            Bundle bundle = getIntent().getExtras();
           ;
            isedit = bundle.getString("isUpdate");
            getIntent().putExtra("isUpdate", "");

        }catch (Exception er){
            edit_orderid = "";

        }


        ApplicationRuntimeStorage.orderid = "";
        txtunit=(TextView)findViewById(com.vsi.smart.dairy1.R.id.txtunit);
        currdate=(TextView) findViewById(com.vsi.smart.dairy1.R.id.txtcurrdate);
        txttime=(TextView)findViewById(com.vsi.smart.dairy1.R.id.txttime);

        etxArea=(EditText)findViewById(com.vsi.smart.dairy1.R.id.etxArea);
        etxCity=(EditText)findViewById(com.vsi.smart.dairy1.R.id.etxCity);
        etxComment=(EditText)findViewById(com.vsi.smart.dairy1.R.id.etxComment);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view = getSupportActionBar().getCustomView();
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar, null);
        TextView tittle = (TextView) mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        if("YES".equalsIgnoreCase(isedit)) {
            if (edit_orderid.length() > 0) {
                tittle.setText("Update Daily Sale");
            } else {

                tittle.setText("Add Daily Sale");
            }
        }else{
            tittle.setText("Daily Sale Details");
        }

        ImageButton imgbutton = (ImageButton) mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edit_orderid.length() > 0  ) {

                    Intent intent = new Intent(AddDailySale.this, List_Sale_Details.class);
                    startActivity(intent);
                }else{

                    Intent intent = new Intent(AddDailySale.this, Menu_Retail_Sale.class);
                    startActivity(intent);
                }

            }
        });


        java.util.Calendar c= java.util.Calendar.getInstance();
        SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
        String formatteddate=df.format(c.getTime());
        currdate.setText(formatteddate);

        SimpleDateFormat dftime=new SimpleDateFormat("HH:mm");
        String formatteddatetime=dftime.format(c.getTime());
        txttime.setText(formatteddatetime);

        table1 = (TableLayout) findViewById(com.vsi.smart.dairy1.R.id.table2);
        Button order=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnorder);
        if("NO".equalsIgnoreCase(isedit)) {

            order.setVisibility(View.INVISIBLE);
        }
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if(etxCity.length()==0)
                {
                    Toast.makeText(AddDailySale.this, "Please Enter City", Toast.LENGTH_SHORT).show();
                }
                else if(etxArea.length()==0)
                {
                    Toast.makeText(AddDailySale.this, "Please Enter Area", Toast.LENGTH_SHORT).show();
                }
                else if(etxComment.length()==0)
                {
                    Toast.makeText(AddDailySale.this, "Please Enter Comment", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Spinner stp = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountercode1);
                    String stp_nm = stp.getSelectedItem().toString();

                    Spinner cnter = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountername1);
                    String cnterNM = cnter.getSelectedItem().toString();

                    String City = ((EditText) findViewById(com.vsi.smart.dairy1.R.id.etxCity)).getText().toString();
                    String Area = ((EditText) findViewById(com.vsi.smart.dairy1.R.id.etxArea)).getText().toString();
                    String Comment = ((EditText) findViewById(com.vsi.smart.dairy1.R.id.etxComment)).getText().toString();
                    String result = ((EditText) findViewById(com.vsi.smart.dairy1.R.id.etxResult)).getText().toString();

                    TextView txt_currDt = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtdate);
                    String currDt = currdate.getText().toString();
                    String timejson =txttime.getText().toString();
                    JSONArray jsonArray = new JSONArray();
                    double tqty = 0;
                    if(table1.getChildCount()==0){


                        JSONObject student22 = new JSONObject();
                        try {

                            student22.put("CompanyId", ApplicationRuntimeStorage.COMPANYID);
                            student22.put("UserId", ApplicationRuntimeStorage.USERID);

                            student22.put("ItemName", "NA");
                            student22.put("ItemPackingName", "NA");
                            student22.put("Quantity", "0");
                            student22.put("UnitName", "NOS");
                            student22.put("saletype", stp_nm);
                            student22.put("posName", cnterNM);
                            student22.put("ordertime", timejson);
                            student22.put("City", City);
                            student22.put("Area", Area);
                            student22.put("Comment", Comment);
                            student22.put("result", result);
                            student22.put("GPS_Longitude", ApplicationRuntimeStorage.GPS_Longitude);
                            student22.put("GPS_Latitude", ApplicationRuntimeStorage.GPS_Latitude);
                            student22.put("GPS_CityName", ApplicationRuntimeStorage.GPS_CityName);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        jsonArray.put(student22);

                    }else {
                        for (int i = 0; i < table1.getChildCount(); i++) {
                            TableRow mRow = (TableRow) table1.getChildAt(i);
                            TextView txt_itmNm = (TextView) mRow.getChildAt(0);
                            TextView txt_packing = (TextView) mRow.getChildAt(1);
                            TextView txt_qty = (TextView) mRow.getChildAt(2);
                            String itmNm = txt_itmNm.getText().toString();
                            String packing = txt_packing.getText().toString();
                            String qty = txt_qty.getText().toString();
                            String unit = txtunit.getText().toString();
                            tqty = Double.parseDouble(qty);

                            JSONObject student2 = new JSONObject();
                            try {

                                student2.put("CompanyId", ApplicationRuntimeStorage.COMPANYID);
                                student2.put("UserId", ApplicationRuntimeStorage.USERID);

                                student2.put("ItemName", itmNm);
                                student2.put("ItemPackingName", packing);
                                student2.put("Quantity", tqty);
                                student2.put("UnitName", unit);
                                student2.put("saletype", stp_nm);
                                student2.put("posName", cnterNM);
                                student2.put("ordertime", timejson);
                                student2.put("City", City);
                                student2.put("Area", Area);
                                student2.put("Comment", Comment);
                                student2.put("result", result);
                                student2.put("GPS_Longitude", ApplicationRuntimeStorage.GPS_Longitude);
                                student2.put("GPS_Latitude", ApplicationRuntimeStorage.GPS_Latitude);
                                student2.put("GPS_CityName", ApplicationRuntimeStorage.GPS_CityName);

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            jsonArray.put(student2);
                        }
                    }
                    try{
                        // SimpleDateFormat formatter1=new SimpleDateFormat("dd/MM/yyyy");
                        // Date date1=formatter1.parse(currDt);

                        CallSoap cs=new CallSoap();
                        if(edit_orderid.length() > 0) {
                            String JSON_RESULT = cs.UpdateUserDailySaleOrder(jsonArray.toString(), ApplicationRuntimeStorage.COMPANYID, ApplicationRuntimeStorage.USERID, currDt,edit_orderid); // Web Call to populate JSON ItemList
                            Toast.makeText(AddDailySale.this, JSON_RESULT, Toast.LENGTH_SHORT).show();
                        }else{
                            String JSON_RESULT = cs.SaveUserDailySaleOrder(jsonArray.toString(), ApplicationRuntimeStorage.COMPANYID, ApplicationRuntimeStorage.USERID, currDt); // Web Call to populate JSON ItemList
                            Toast.makeText(AddDailySale.this, JSON_RESULT, Toast.LENGTH_SHORT).show();

                        }
                    }catch(Exception er)
                    {
                        Toast.makeText(AddDailySale.this, "ERROR :"+er.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

        addListenerOnSpinnerItemSelection();
        addItemsOnSpinner2();
        addItemsOnSpinner5();
        addListenerOnButton();

        add1 = (Button) findViewById(com.vsi.smart.dairy1.R.id.btnadd);
        add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText edit = (EditText) findViewById(com.vsi.smart.dairy1.R.id.id_qty);
                if(edit.length()==0)
                {
                    Toast.makeText(AddDailySale.this, "Please Enter Quantity", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Spinner itm = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountercode);
                    String itemnm = itm.getSelectedItem().toString();

                    Spinner pack = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountername);
                    String packnm = pack.getSelectedItem().toString();


                    String sqty = (String) edit.getText().toString();

                    TextView txtunit=(TextView) findViewById(com.vsi.smart.dairy1.R.id.txtunit);
                    String unit1 =(String) txtunit.getText().toString();

                    TableRow row = new TableRow(AddDailySale.this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);

                    TextView prod = new TextView(AddDailySale.this);
                    prod.setText(itemnm);
                    row.addView(prod);

                    TextView packing = new TextView(AddDailySale.this);
                    packing.setText("\t\t"+packnm);
                    row.addView(packing);

                    TextView qty = new TextView(AddDailySale.this);
                    qty.setText("\t\t"+sqty);
                    row.addView(qty);

                    TextView unit2=new TextView(AddDailySale.this);
                    unit2.setText(unit1+"\t\t\t");
                    row.addView(unit2);

                    ImageButton delete=new ImageButton(AddDailySale.this);
                    delete.setImageResource(com.vsi.smart.dairy1.R.drawable.ic_delete_black_24dp);
                    delete.setBackgroundColor(0xfffafafa);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            View row=(View) view.getParent();
                            ViewGroup container=((ViewGroup)row.getParent());
                            container.removeView(row);
                            container.invalidate();
                        }
                    });
                    row.addView(delete);
                    table1.addView(row);
                }


            }
        });

        final TextView date = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtcurrdate);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddDailySale.this,
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
                                String currdate="";
                                currdate=df.format(date1.getTime());
                                date.setText(currdate);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        txttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                openTimePickerDialog(false);
            }
            TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, final int hourOfDay, final int minute) {

                    boolean isPM = (hourOfDay >= 12);
                    txttime.setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
                    getTime=txttime.getText().toString();
                    phour=hourOfDay;
                    pminute=minute;

                }
            };

            private void openTimePickerDialog(boolean b)
            {

                Calendar calendar = Calendar.getInstance();
                timePickerDialog = new TimePickerDialog(AddDailySale.this, onTimeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),b);
                timePickerDialog.setTitle("Set Date");

                timePickerDialog.show();

            }
        });

        if(edit_orderid.length() > 0 ){

            CallSoap cs=new CallSoap();
            long oid = 0;
            oid = Long.parseLong(edit_orderid.trim());
            String JSON_RESULT= cs.GetOrderDetails(ApplicationRuntimeStorage.COMPANYID,ApplicationRuntimeStorage.USERID,oid); // Web Call to populate JSON ItemList

            Spinner stp = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountercode1);
           // String stp_nm = stp.getSelectedItem().toString();

            Spinner cnter = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountername1);
          //  String cnterNM = cnter.getSelectedItem().toString();
            try {
                JSONArray jsonarray = new JSONArray(JSON_RESULT);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String ItemName = jsonobject.getString("ItemName");

                    String ItemPackingName = jsonobject.getString("ItemPackingName");
                    String Quantity = jsonobject.getString("Quantity");
                    String UnitName = jsonobject.getString("UnitName");

                    if(i==0) {
                        String saletype = jsonobject.getString("saletype");
                        String posName = jsonobject.getString("posName");
                        selectSpinnerItemByValue(stp, saletype);
                        selectSpinnerItemByValue(cnter, posName);
                        String result = jsonobject.getString("result");
                        String City = jsonobject.getString("City");
                        String Area = jsonobject.getString("Area");
                        String Comment = jsonobject.getString("Comment");
                        String ordertime = jsonobject.getString("ordertime");
                        String OrderDateStr = jsonobject.getString("OrderDateStr");
                        txttime.setText(ordertime);
                        ((TextView) findViewById(com.vsi.smart.dairy1.R.id.etxCity)).setText(City);
                        ((TextView) findViewById(com.vsi.smart.dairy1.R.id.txtcurrdate)).setText(OrderDateStr);

                        ((TextView) findViewById(com.vsi.smart.dairy1.R.id.etxArea)).setText(Area);
                        ((TextView) findViewById(com.vsi.smart.dairy1.R.id.etxComment)).setText(Comment);
                        ((TextView) findViewById(com.vsi.smart.dairy1.R.id.etxResult)).setText(result   );
                    }





                    if("NA".equalsIgnoreCase(ItemName)){
                        continue;

                    }

                    TableRow row = new TableRow(AddDailySale.this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);

                    TextView ItemName1 = new TextView(AddDailySale.this);
                    ItemName1.setText("\n\t\t\t" + ItemName);
                    row.addView(ItemName1);

                    TextView ItemPackingName1 = new TextView(AddDailySale.this);
                    ItemPackingName1.setText("\n\t\t\t" + ItemPackingName);
                    row.addView(ItemPackingName1);

                    TextView Quantity1 = new TextView(AddDailySale.this);
                    Quantity1.setText("\n\t\t\t" + Quantity);
                    row.addView(Quantity1);

                    TextView UnitName1 = new TextView(AddDailySale.this);
                    UnitName1.setText("\n\t\t\t" + UnitName);
                    row.addView(UnitName1);
                    table1.addView(row);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


//        TextView gps_lati = (TextView) findViewById(R.id.txt_gps_lati);
//        gps_lati.setText("GPS LOCATION : ");
//
//       TextView gps_long = (TextView) findViewById(R.id.txt_gps_long);
//      gps_long.setText(""+ApplicationRuntimeStorage.GPS_CityName);

    }
    public static void selectSpinnerItemByValue(Spinner spnr, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spnr.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if(adapter.getItem(position).toString() .equalsIgnoreCase( value)) {
                spnr.setSelection(position);
                return;
            }
        }
    }

    private Spinner spinner4;

    private Spinner spinner6;
    private Button btnSubmit;
    private Spinner spinner5;
    // add items into spinner dynamically
    public void addItemsOnSpinner5(){
        Spinner spinner5 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountercode1);
       /* List<String> list = new ArrayList<>();
        CallSoap cs=new CallSoap();
        String JSON_RESULT= cs.GetSaleTypeList(ApplicationRuntimeStorage.USERID,ApplicationRuntimeStorage.COMPANYID); // Web Call to populate JSON ItemList
        try{
            JSONArray jsonarray = new JSONArray(JSON_RESULT);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String itemCode = jsonobject.getString("Key");
                String itemName = jsonobject.getString("Value");
                list.add(itemName);
            }
        }catch (Exception e) {e.printStackTrace();}*/
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, ApplicationRuntimeStorage.LIST_SALE_TYPE);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner5.setAdapter(dataAdapter);
    }
    public void addItemsOnSpinner2() {
        Spinner spinner2 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountercode);
        /*List<String> list = new ArrayList<>();
        CallSoap cs=new CallSoap();
        String JSON_RESULT= cs.GetItemList(ApplicationRuntimeStorage.USERID,ApplicationRuntimeStorage.COMPANYID); // Web Call to populate JSON ItemList
        try{
            JSONArray jsonarray = new JSONArray(JSON_RESULT);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String itemCode = jsonobject.getString("ItemCode");
                String itemName = jsonobject.getString("ItemName");
                list.add(itemName);
            }
        }catch (Exception e) {e.printStackTrace();}*/
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, ApplicationRuntimeStorage.LIST_ITEMS);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);
    }

    public void addListenerOnSpinnerItemSelection() {
        Spinner spinner1 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountercode);
        spinner4 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountername1);
        spinner5 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountercode1);

        spinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String itemNameSelected = parentView.getItemAtPosition(position).toString();
                if("PLEASE SELECT".equalsIgnoreCase(itemNameSelected)) return;
                Spinner spinner6 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountername1);
                List<String> list = new ArrayList<>();
                if("".equalsIgnoreCase(itemNameSelected)){
                    Toast.makeText(AddDailySale.this, "Please Select Item Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                CallSoap cs=new CallSoap();
                String JSON_RESULT= cs.GetPointOfSaleList(itemNameSelected,ApplicationRuntimeStorage.COMPANYID); // Web Call to populate JSON ItemList
                try{
                    JSONArray jsonarray = new JSONArray(JSON_RESULT);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String itemCode = jsonobject.getString("Key");
                        String itemName = jsonobject.getString("Value");
                        list.add(itemName);
                    }

                }catch (Exception e) {e.printStackTrace();}
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddDailySale.this,android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner6.setAdapter(dataAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                Toast.makeText(AddDailySale.this, "Please Select Item Name", Toast.LENGTH_SHORT).show();
            }

        });


        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                final String itemNameSelected = parentView.getItemAtPosition(position).toString();

                if("PLEASE SELECT".equalsIgnoreCase(itemNameSelected)) return;


                Spinner spinner3 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountername);
                List<String> list = new ArrayList<>();
                if("".equalsIgnoreCase(itemNameSelected)){
                    Toast.makeText(AddDailySale.this, "Please Select Item Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                CallSoap cs=new CallSoap();
                String JSON_RESULT= cs.GetItemPackingList(itemNameSelected,ApplicationRuntimeStorage.COMPANYID); // Web Call to populate JSON ItemList
                try{
                    JSONArray jsonarray = new JSONArray(JSON_RESULT);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String itemCode = jsonobject.getString("ItemCode");
                        String itemName = jsonobject.getString("ItemName");
                        list.add(itemName);
                    }

                }catch (Exception e) {e.printStackTrace();}
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddDailySale.this,android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner3.setAdapter(dataAdapter);
                spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                    {
                        String packingname = adapterView.getItemAtPosition(i).toString();
                        if("PLEASE SELECT".equalsIgnoreCase(packingname)) return;

                        CallSoap cs1=new CallSoap();
                        String JSON_RESULT= cs1.GetItemPackingUnit(itemNameSelected,packingname,ApplicationRuntimeStorage.COMPANYID); // Web Call to populate JSON ItemList
                        txtunit.setText(JSON_RESULT);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                Toast.makeText(AddDailySale.this, "Please Select Item Name", Toast.LENGTH_SHORT).show();
            }
        });

    }
    // get the selected dropdown list value
    public void addListenerOnButton() {

        Spinner spinner1 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountername);
        Spinner spinner2 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountercode);
        Spinner spinner4 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountername1);
        Spinner spinner5 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountercode1);
        btnSubmit = (Button) findViewById(com.vsi.smart.dairy1.R.id.btnadd);

    }

    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(AddDailySale.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}

