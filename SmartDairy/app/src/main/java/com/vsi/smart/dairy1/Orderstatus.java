package com.vsi.smart.dairy1;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Orderstatus extends AppCompatActivity {

    TableLayout table1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_orderstatus);

        table1=(TableLayout)findViewById(com.vsi.smart.dairy1.R.id.table1);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Order Status");

        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Orderstatus.this,AddDailySale.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        CallSoap cs=new CallSoap();
        String JSON_RESULT= cs.GetLastOrderList(ApplicationRuntimeStorage.USERID,ApplicationRuntimeStorage.COMPANYID); // Web Call to populate JSON ItemList
        try{
            JSONArray jsonarray = new JSONArray(JSON_RESULT);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String OrderDate = jsonobject.getString("OrderDateStr");
                final String  orderId= jsonobject.getString("orderId");
                String  orderStatus= jsonobject.getString("orderStatus");

                TableRow row= new TableRow(Orderstatus.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                SimpleDateFormat formatter1=new SimpleDateFormat("MM/dd/yyyy");
                Date date1=formatter1.parse(OrderDate);

                SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
                String formatteddate=df.format(date1);

//                TextView orderid = new TextView(Orderstatus.this);
//                orderid.setText("\t\t\t\t"+orderId);
//                row.addView(orderid);

                TextView orderStatus1 = new TextView(Orderstatus.this);
                if("".equalsIgnoreCase(orderStatus.trim())) {
                    orderStatus1.setText("\n\t PENDING");
                    ImageButton pending = new ImageButton(Orderstatus.this);
                    pending.setImageResource(com.vsi.smart.dairy1.R.drawable.ic_error_black_24dp);
                    String gray="#fffafafa";
                    int gray1= Color.parseColor(gray);
                    pending.setBackgroundColor(gray1);
                    row.addView(pending);

                    ImageButton edit=new ImageButton(Orderstatus.this);
                    edit.setImageResource(com.vsi.smart.dairy1.R.drawable.ic_edit_black_24dp);
                    String gray2="#fffafafa";
                    int gray3= Color.parseColor(gray2);
                    edit.setBackgroundColor(gray3);
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            Intent intent=new Intent(Orderstatus.this,DelerOrderupdate.class);
                            intent.putExtra("orderid",orderId);
                            startActivity(intent);
                        }
                    });
                    row.addView(edit);

                }else {
                    orderStatus1.setText("\t\t\t" + orderStatus);
                    ImageView dispatched = new ImageView(Orderstatus.this);
                    dispatched.setImageResource(com.vsi.smart.dairy1.R.drawable.ic_time_to_leave_black_24dp);
                    row.addView(dispatched);
                }
                row.addView(orderStatus1);

                TextView orderDate = new TextView(Orderstatus.this);
                orderDate.setText("\n\t\t\t\t"+formatteddate);
                row.addView(orderDate);

                TextView DeleverDate = new TextView(Orderstatus.this);
                DeleverDate.setText("\n\t\t\t\t"+formatteddate);
                row.addView(DeleverDate);

                table1.addView(row);
            }

        }catch (Exception e) {e.printStackTrace();}
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, list);
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner2.setAdapter(dataAdapter);
    }
    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Orderstatus.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
