package com.vsi.smart.dairy1;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Counterdata extends AppCompatActivity {

   Button btnsave,btnupdate;
    EditText etx1,etx2,etx3;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_counterdata);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Daily Sale Data");

        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Counterdata.this,List_Sale_Details.class);
                startActivity(intent);            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        btnsave=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnsave);
        btnupdate=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnupdate);

        etx1=(EditText)findViewById(com.vsi.smart.dairy1.R.id.etxpr1);
        etx2=(EditText)findViewById(com.vsi.smart.dairy1.R.id.etxpr2);
        etx3=(EditText)findViewById(com.vsi.smart.dairy1.R.id.etxpr3);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(Counterdata.this, "Your New Data is Saved", Toast.LENGTH_SHORT).show();
            }
        });

        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                etx1.setText("");
                etx2.setText("");
                etx3.setText("");
            }
        });
    }

    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Counterdata.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
