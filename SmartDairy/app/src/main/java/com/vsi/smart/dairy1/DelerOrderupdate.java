package com.vsi.smart.dairy1;


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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DelerOrderupdate extends AppCompatActivity {

    Calendar c = Calendar.getInstance();
    public final int mYear = c.get(Calendar.YEAR);
    public  final int mMonth = c.get(Calendar.MONTH);
    public final  int mDay = c.get(Calendar.DAY_OF_MONTH);
    public  int pday;
    public int pmonth;
    public int pyear;
    TableLayout table1;
    TextView txtunit;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_delerorderupdate);

        Button update = (Button) findViewById(com.vsi.smart.dairy1.R.id.btnupdate);
        Button add1 = (Button) findViewById(com.vsi.smart.dairy1.R.id.btnadd);
        txtunit=(TextView) findViewById(com.vsi.smart.dairy1.R.id.txtunit);
        table1 = (TableLayout) findViewById(com.vsi.smart.dairy1.R.id.table2);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view = getSupportActionBar().getCustomView();
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar, null);
        TextView tittle = (TextView) mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Update Order");

        ImageButton imgbutton = (ImageButton) mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DelerOrderupdate.this, options.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        Bundle bundle = getIntent().getExtras();
        String orderid1 = bundle.getString("orderid");
        Long orderid = Long.parseLong(orderid1);

        CallSoap cs = new CallSoap();
        String JSON_RESULT = cs.GetOrderDetails(ApplicationRuntimeStorage.COMPANYID, ApplicationRuntimeStorage.USERID, orderid); // Web Call to populate JSON ItemList
        try {
            JSONArray jsonarray = new JSONArray(JSON_RESULT);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String ItemName = jsonobject.getString("ItemName");
                String ItemPackingName = jsonobject.getString("ItemPackingName");
                String Quantity = jsonobject.getString("Quantity");
                String UnitName = jsonobject.getString("UnitName");

                TableRow row = new TableRow(DelerOrderupdate.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                TextView ItemName1 = new TextView(DelerOrderupdate.this);
                ItemName1.setText("\n\t\t\t\t" + ItemName);
                row.addView(ItemName1);

                TextView ItemPackingName1 = new TextView(DelerOrderupdate.this);
                ItemPackingName1.setText("\n\t\t\t\t" + ItemPackingName);
                row.addView(ItemPackingName1);

                TextView Quantity1 = new TextView(DelerOrderupdate.this);
                Quantity1.setText("\n\t\t\t\t" + Quantity);
                row.addView(Quantity1);

                TextView UnitName1 = new TextView(DelerOrderupdate.this);
                UnitName1.setText("\n\t\t\t\t" + UnitName);
                row.addView(UnitName1);
                table1.addView(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        addItemsOnSpinner2();
        addListenerOnSpinnerItemSelection();
        addListenerOnButton();
        add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Spinner itm = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountercode);
                String itemnm = itm.getSelectedItem().toString();

                Spinner pack = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountername);
                String packnm = pack.getSelectedItem().toString();

                final EditText edit = (EditText) findViewById(com.vsi.smart.dairy1.R.id.id_qty);
                String sqty = (String) edit.getText().toString();

                String unit1 = (String) txtunit.getText().toString();
                //table1.setVisibility(View.VISIBLE);

                TableRow row = new TableRow(DelerOrderupdate.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                TextView prod = new TextView(DelerOrderupdate.this);
                prod.setText(itemnm);
                row.addView(prod);

                TextView packing = new TextView(DelerOrderupdate.this);
                packing.setText("\t\t" + packnm);
                row.addView(packing);

                TextView qty = new TextView(DelerOrderupdate.this);
                qty.setText("\t\t" + sqty);
                row.addView(qty);

                TextView unit2 = new TextView(DelerOrderupdate.this);
                unit2.setText(unit1 + "\t\t\t");
                row.addView(unit2);

                ImageButton delete = new ImageButton(DelerOrderupdate.this);
                delete.setImageResource(com.vsi.smart.dairy1.R.drawable.ic_delete_black_24dp);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View row = (View) view.getParent();
                        ViewGroup container = ((ViewGroup) row.getParent());
                        container.removeView(row);
                        container.invalidate();
                    }
                });
                row.addView(delete);

                table1.addView(row);
            }
        });
    }
        Spinner spinner1;
        Spinner spinner2;
        Button btnSubmit;
        // add items into spinner dynamically
    public void addItemsOnSpinner2() {

        spinner2 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountercode);
        List<String> list = new ArrayList<>();

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
            //String str="Employee Name:"+empname+"\n"+"Employee Salary:"+empsalary;
            // textView1.setText(str);

        }catch (Exception e) {e.printStackTrace();}
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);
    }

    public void addListenerOnSpinnerItemSelection() {

        spinner1 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountercode);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                final String itemNameSelected = parentView.getItemAtPosition(position).toString();

                Spinner spinner3 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountername);
                List<String> list = new ArrayList<>();
                if("".equalsIgnoreCase(itemNameSelected)){
                    Toast.makeText(DelerOrderupdate.this, "Please Select Item Name", Toast.LENGTH_SHORT).show();
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
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(DelerOrderupdate.this,android.R.layout.simple_spinner_item, list);
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
                Toast.makeText(DelerOrderupdate.this, "Please Select Item Name", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // get the selected dropdown list value
    public void addListenerOnButton() {

        spinner1 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountername);
        spinner2 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountercode);
        btnSubmit = (Button) findViewById(com.vsi.smart.dairy1.R.id.btnadd);

    }

    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(DelerOrderupdate.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}

