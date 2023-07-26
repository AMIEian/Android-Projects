package com.vsi.smart.dairy1;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import android.widget.Spinner;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Deler extends AppCompatActivity {
    TextView currdate,txtunit;
    TableLayout table1;
    Button add1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_deler);

        Button order=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnorder);
        add1=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnadd);
        table1=(TableLayout)findViewById(com.vsi.smart.dairy1.R.id.table2);

        txtunit=(TextView) findViewById(com.vsi.smart.dairy1.R.id.txtunit);

//Date Selection
        currdate=(TextView) findViewById(com.vsi.smart.dairy1.R.id.txtcurrdate);
        Calendar c=Calendar.getInstance();
        SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
        String formatteddate=df.format(c.getTime());
        currdate.setText(formatteddate);

        //click on Order placed button
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                TextView txt_currDt = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtcurrdate);
                String currDt = txt_currDt.getText().toString();
                JSONArray jsonArray = new JSONArray();
                double tqty = 0;
                for (int i = 0; i < table1.getChildCount(); i++) {
                    TableRow mRow = (TableRow) table1.getChildAt(i);
                    TextView txt_itmNm = (TextView) mRow.getChildAt(0);
                    TextView txt_packing = (TextView) mRow.getChildAt(1);
                    TextView txt_qty = (TextView) mRow.getChildAt(2);
                    String itmNm = txt_itmNm.getText().toString();
                    String packing = txt_packing.getText().toString();
                    String qty = txt_qty.getText().toString();
                    String unit=txtunit.getText().toString();
                    tqty = Double.parseDouble(qty);

                    JSONObject student2 = new JSONObject();
                    try {
                        SimpleDateFormat formatter1=new SimpleDateFormat("dd/MM/yyyy");
                        Date date1=formatter1.parse(currDt);

                        student2.put("CompanyId", ApplicationRuntimeStorage.COMPANYID);
                        student2.put("UserId",ApplicationRuntimeStorage.USERID);
                       // student2.put("OrderDate", currDt);
                        student2.put("ItemName", itmNm);
                        student2.put("ItemPackingName", packing);
                        student2.put("Quantity", tqty);
                        student2.put("Unit",unit );

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    jsonArray.put(student2);
                }
                try{
                    SimpleDateFormat formatter1=new SimpleDateFormat("dd/MM/yyyy");
                    Date date1=formatter1.parse(currDt);
                    // System.out.println("Sum of the provision are " + total);
                    //tot.setText("" + total);
                    CallSoap cs=new CallSoap();
                    String JSON_RESULT= cs.SaveUserOrder(jsonArray.toString(),ApplicationRuntimeStorage.COMPANYID,ApplicationRuntimeStorage.USERID,currDt); // Web Call to populate JSON ItemList
                    Toast.makeText(Deler.this, JSON_RESULT, Toast.LENGTH_SHORT).show();
                }catch(Exception er){

                    Toast.makeText(Deler.this, "ERROR :"+er.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        addItemsOnSpinner2();
        addListenerOnSpinnerItemSelection();
        addListenerOnButton();
        add1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Spinner itm = (Spinner)findViewById(com.vsi.smart.dairy1.R.id.spnCountercode);
                String itemnm = itm.getSelectedItem().toString();

                Spinner pack = (Spinner)findViewById(com.vsi.smart.dairy1.R.id.spnCountername);
                String packnm = pack.getSelectedItem().toString();

                final EditText edit =  (EditText)findViewById(com.vsi.smart.dairy1.R.id.id_qty);
                String sqty =(String) edit.getText().toString();

                String unit1 =(String) txtunit.getText().toString();
                //table1.setVisibility(View.VISIBLE);

                TableRow row= new TableRow(Deler.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                TextView prod = new TextView(Deler.this);
                prod.setText(itemnm);
                row.addView(prod);

                TextView packing = new TextView(Deler.this);
                packing.setText("\t\t"+packnm);
                row.addView(packing);

                TextView qty = new TextView(Deler.this);
                qty.setText("\t\t"+sqty);
                row.addView(qty);

                TextView unit2=new TextView(Deler.this);
                unit2.setText(unit1+"\t\t\t");
                row.addView(unit2);

                ImageButton delete=new ImageButton(Deler.this);
                delete.setImageResource(com.vsi.smart.dairy1.R.drawable.ic_delete_black_24dp);
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

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("New Booking");

        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Deler.this,options.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
    }
    private Spinner spinner1;
    private Spinner spinner2;
    private Button btnSubmit;
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
                    Toast.makeText(Deler.this, "Please Select Item Name", Toast.LENGTH_SHORT).show();
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
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Deler.this,android.R.layout.simple_spinner_item, list);
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
                Toast.makeText(Deler.this, "Please Select Item Name", Toast.LENGTH_SHORT).show();
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
            Intent intent=new Intent(Deler.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
