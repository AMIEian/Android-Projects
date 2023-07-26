package com.vsi.smart.dairy1;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Milkreportlist extends AppCompatActivity {

    Button Milkcollection,customerwise,sansthacollectionregister,sansthaPaymentRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_milkreportlist);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("दुध संकलन रिपोर्ट");

        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Milkreportlist.this,Login.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        Milkcollection=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnMilkCollection);
        customerwise=(Button) findViewById(com.vsi.smart.dairy1.R.id.btnCustomerWise);
        sansthacollectionregister=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnSansthaCollectionRegister);
        sansthaPaymentRegister=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnSansthaPaymentRegister);

        customerwise.setVisibility(View.INVISIBLE);
        sansthacollectionregister.setVisibility(View.INVISIBLE);
        sansthaPaymentRegister.setVisibility(View.INVISIBLE);
        if(ApplicationRuntimeStorage.AccessRights.length() > 0) {
            String[] rulesArr =ApplicationRuntimeStorage.AccessRights.split(",");
            for (String rule:rulesArr) {
                if("RULE_DASHBOARD_SUMMARY".equalsIgnoreCase(rule))
                {
                    customerwise.setVisibility(View.VISIBLE);
                    sansthacollectionregister.setVisibility(View.VISIBLE);
                    sansthaPaymentRegister.setVisibility(View.VISIBLE);
                }

            }
        }



        Milkcollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Milkreportlist.this, SansthaMilkData.class);
                startActivity(i);

            }
        });

        customerwise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Milkreportlist.this,Customermilkcollection.class);
                startActivity(intent);
            }
        });

        sansthacollectionregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Milkreportlist.this,Sansthamilkcollection.class);
                startActivity(intent);
            }
        });

        sansthaPaymentRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Milkreportlist.this,PaymentList.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Milkreportlist.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
