package com.vsi.smart.dairy1;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class Statementdetails extends AppCompatActivity {
    public TableLayout table1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_statementdetails);

        table1=(TableLayout)findViewById(com.vsi.smart.dairy1.R.id.table1);
        TextView Billno1=(TextView)findViewById(com.vsi.smart.dairy1.R.id.Billno);
        TextView BillDate1=(TextView)findViewById(com.vsi.smart.dairy1.R.id.Billdate);
        TextView Group1=(TextView)findViewById(com.vsi.smart.dairy1.R.id.Group);
        TextView Name1=(TextView)findViewById(com.vsi.smart.dairy1.R.id.Name);
        //TextView Billtype1=(TextView)findViewById(R.id.Billtype);

        TextView Transport1=(TextView)findViewById(com.vsi.smart.dairy1.R.id.Transport);
        TextView Freight1=(TextView)findViewById(com.vsi.smart.dairy1.R.id.Freight);
        TextView OthDiscount1=(TextView)findViewById(com.vsi.smart.dairy1.R.id.OthDiscount);
        TextView BillAmount1=(TextView)findViewById(com.vsi.smart.dairy1.R.id.BillAmount);
        TextView PaidAmount1=(TextView)findViewById(com.vsi.smart.dairy1.R.id.PaidAmount);
        TextView TotalCGST1=(TextView)findViewById(com.vsi.smart.dairy1.R.id.TotalCGST);
        TextView TotalSGST1=(TextView)findViewById(com.vsi.smart.dairy1.R.id.TotalSGST);
        TextView TotalGST1=(TextView)findViewById(com.vsi.smart.dairy1.R.id.TotalGST);
        TextView Round1=(TextView)findViewById(com.vsi.smart.dairy1.R.id.Round);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Statement Details");

        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Statementdetails.this,Statementlist.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        Intent intent=getIntent();
        String rno=intent.getStringExtra("rno");
        String receiptname=intent.getStringExtra("receiptname");
        String itemGroupcode=intent.getStringExtra("itemGroupcode");
        String voucherdate=intent.getStringExtra("voucherdate");
        String billno=intent.getStringExtra("billno");
        CallSoap cs = new CallSoap();
        String  spacingStr = "\n\t\t\t\t";

        double tcgstamt=0;
        double tsgstamt=0;
        double ttgstamt=0;
        DecimalFormat f = new DecimalFormat("##.00");
        String JSON_RESULT = cs.GetSaleEntry(ApplicationRuntimeStorage.COMPANYID, ApplicationRuntimeStorage.USERID, billno, rno, itemGroupcode,voucherdate);
        try {
            JSONArray jsonarray = new JSONArray(JSON_RESULT);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);

                String  BillNo = jsonobject.getString("BillNo");
                Billno1.setText(BillNo);
                String  BillDate = jsonobject.getString("BillDate");
                BillDate1.setText(BillDate);
                String Narration=jsonobject.getString("Narration");
                tittle.setText(Narration); //billtype
                String pname=jsonobject.getString("pname");
                Name1.setText(pname);
                String itemGroupName=jsonobject.getString("itemGroupName");
                Group1.setText(itemGroupName);



                String  ItemName = jsonobject.getString("ItemName");
                String  Qty = jsonobject.getString("Qty");
                String  Rate = jsonobject.getString("Rate");double Rate21 = Double.parseDouble(Rate);
                String  cgst = jsonobject.getString("cgst");
                String  sgst = jsonobject.getString("sgst");
                String  tgst = jsonobject.getString("tgst");
                String  Amount12 = jsonobject.getString("ItmAmt"); double Amount121 = Double.parseDouble(Amount12);
                String discountamt=jsonobject.getString("discountamt");double discountamt1 = Double.parseDouble(discountamt);
                String discount=jsonobject.getString("discount");

                String headrow1=jsonobject.getString("headrow");

                String  cgstamt = jsonobject.getString("cgstamt");
                double cgstamt11 = Double.parseDouble(cgstamt); tcgstamt = tcgstamt+cgstamt11;


                String  sgstamt = jsonobject.getString("sgstamt");
                double sgstamt11 = Double.parseDouble(sgstamt); tsgstamt = tsgstamt+sgstamt11;


                String  tgstamt = jsonobject.getString("tgstamt");
                double tgstamt11 = Double.parseDouble(tgstamt); ttgstamt = ttgstamt+tgstamt11;


                    if("1".equalsIgnoreCase(headrow1)) {

                        String transport = jsonobject.getString("transport");
                        Transport1.setText(transport);

                        String freight = jsonobject.getString("freight");
                        Freight1.setText(freight);

                        String OTH_Discount = jsonobject.getString("OTH_Discount");
                        OthDiscount1.setText(OTH_Discount);

                        String netTotal = jsonobject.getString("netTotal");
                        BillAmount1.setText(netTotal);

                        String JamaAmt1 = jsonobject.getString("JamaAmt");
                        PaidAmount1.setText(JamaAmt1);

                        String roundoffvcno = jsonobject.getString("roundoff");
                        Round1.setText(roundoffvcno);

                    }

                TableRow row= new TableRow(Statementdetails.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                TextView ItemName1 = new TextView(Statementdetails.this);
                ItemName1.setText(spacingStr+ItemName);
                row.addView(ItemName1);

                TextView Qty1 = new TextView(Statementdetails.this);
                Qty1.setText(spacingStr+Qty);
                row.addView(Qty1);

                TextView Rate1 = new TextView(Statementdetails.this);
                Rate1.setText(spacingStr+f.format(Rate21));
                row.addView(Rate1);

                TextView cgst1 = new TextView(Statementdetails.this);
                cgst1.setText("\n\t\t"+cgst);
                row.addView(cgst1);

                TextView cgstamt112 = new TextView(Statementdetails.this);
                cgstamt112.setText(spacingStr+f.format(cgstamt11));
                row.addView(cgstamt112);


                TextView sgst1 = new TextView(Statementdetails.this);
                sgst1.setText(spacingStr+sgst);
                row.addView(sgst1);

                TextView sgstamt13 = new TextView(Statementdetails.this);
                sgstamt13.setText(spacingStr+f.format(sgstamt11));
                row.addView(sgstamt13);

                TextView discount1 = new TextView(Statementdetails.this);
                discount1.setText(spacingStr+discount);
                row.addView(discount1);

                TextView discountamt11 = new TextView(Statementdetails.this);
                discountamt11.setText(spacingStr+f.format(discountamt1));
                row.addView(discountamt11);

                TextView Amount11 = new TextView(Statementdetails.this);
                Amount11.setText(spacingStr+f.format(Amount121));
                row.addView(Amount11);

                table1.addView(row);
            }

            TotalCGST1.setText(""+tcgstamt);
            TotalSGST1.setText(""+tsgstamt);
            TotalGST1.setText(""+ttgstamt);

        } catch (Exception e) {}
    }
    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Statementdetails.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
