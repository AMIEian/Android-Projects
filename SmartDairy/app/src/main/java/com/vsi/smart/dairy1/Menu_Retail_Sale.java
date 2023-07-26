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

public class Menu_Retail_Sale extends AppCompatActivity {

    Button Counterup, Counternew,Counternewsale;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_counter_options);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Sale");

        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Menu_Retail_Sale.this,options.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        Counternew=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnnewCounter);
        Counterup=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnupcounter);
        Counternewsale=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnnewsalecounter);


        Counternew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(Menu_Retail_Sale.this, AddDailySale.class);
                startActivity(i);
            }
        });

        Counterup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(Menu_Retail_Sale.this, List_Sale_Details.class);
                startActivity(i);
            }
        });

        Counternewsale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(Menu_Retail_Sale.this, com.vsi.smart.dairy1.Counternewsale.class);
                startActivity(i);
            }
        });

    }
    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Menu_Retail_Sale.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
