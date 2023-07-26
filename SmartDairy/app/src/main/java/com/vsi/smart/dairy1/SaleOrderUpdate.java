package com.vsi.smart.dairy1;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SaleOrderUpdate extends AppCompatActivity {
    Button add1;
    Calendar c = Calendar.getInstance();
    public final int mYear = c.get(Calendar.YEAR);
    public  final int mMonth = c.get(Calendar.MONTH);
    public final  int mDay = c.get(Calendar.DAY_OF_MONTH);
    public  int pday;
    public int pmonth;
    public int pyear;
    TextView txtunit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_sale_order_update);

        add1 = (Button) findViewById(com.vsi.smart.dairy1.R.id.btnadd);
        final TableLayout table1 = (TableLayout) findViewById(com.vsi.smart.dairy1.R.id.table2);
        txtunit=(TextView)findViewById(com.vsi.smart.dairy1.R.id.txtunit);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Counter Update");

        ImageButton imgbutton = (ImageButton) mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SaleOrderUpdate.this, Menu_Retail_Sale.class);
                startActivity(intent);
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
                Spinner itm = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountercode);
                String itemnm = itm.getSelectedItem().toString();

                Spinner pack = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountername);
                String packnm = pack.getSelectedItem().toString();

                final EditText edit = (EditText) findViewById(com.vsi.smart.dairy1.R.id.id_qty);
                String sqty = (String) edit.getText().toString();

                TextView txtunit=(TextView) findViewById(com.vsi.smart.dairy1.R.id.txtunit);
                String unit1 =(String) txtunit.getText().toString();

                TableRow row = new TableRow(SaleOrderUpdate.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                TextView prod = new TextView(SaleOrderUpdate.this);
                prod.setText(itemnm);
                row.addView(prod);

                TextView packing = new TextView(SaleOrderUpdate.this);
                packing.setText("\t\t"+packnm);
                row.addView(packing);

                TextView qty = new TextView(SaleOrderUpdate.this);
                qty.setText("\t\t"+sqty);
                row.addView(qty);

                TextView unit2=new TextView(SaleOrderUpdate.this);
                unit2.setText(unit1+"\t\t\t");
                row.addView(unit2);

                ImageButton delete=new ImageButton(SaleOrderUpdate.this);
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
        });
        ImageButton calendarbtn = (ImageButton) findViewById(com.vsi.smart.dairy1.R.id.imgcal);
        final TextView date = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtcurrdate);
        calendarbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(SaleOrderUpdate.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                pyear = year;
                                pmonth = monthOfYear;
                                pday = dayOfMonth;


                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        //  TextView gps_lati = (TextView) mCustomView.findViewById(R.id.txt_gps_lati);
        //gps_lati.setText(""+String.valueOf(ApplicationRuntimeStorage.GPS_Latitude));

        // TextView gps_long = (TextView) mCustomView.findViewById(R.id.txt_gps_long);
        //  gps_long.setText(""+String.valueOf(ApplicationRuntimeStorage.GPS_Longitude));

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

                Spinner spinner6 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountername1);
                List<String> list = new ArrayList<>();
                if("".equalsIgnoreCase(itemNameSelected)){
                    Toast.makeText(SaleOrderUpdate.this, "Please Select Item Name", Toast.LENGTH_SHORT).show();
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
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SaleOrderUpdate.this,android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner6.setAdapter(dataAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                Toast.makeText(SaleOrderUpdate.this, "Please Select Item Name", Toast.LENGTH_SHORT).show();
            }

        });


        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                final String itemNameSelected = parentView.getItemAtPosition(position).toString();

                Spinner spinner3 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountername);
                List<String> list = new ArrayList<>();
                if("".equalsIgnoreCase(itemNameSelected)){
                    Toast.makeText(SaleOrderUpdate.this, "Please Select Item Name", Toast.LENGTH_SHORT).show();
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
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SaleOrderUpdate.this,android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner3.setAdapter(dataAdapter);
                spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                    {
                        String packingname = adapterView.getItemAtPosition(i).toString();
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
                Toast.makeText(SaleOrderUpdate.this, "Please Select Item Name", Toast.LENGTH_SHORT).show();
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
            Intent intent=new Intent(SaleOrderUpdate.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}

