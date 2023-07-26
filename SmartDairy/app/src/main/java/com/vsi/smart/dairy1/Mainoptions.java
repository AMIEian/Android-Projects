package com.vsi.smart.dairy1;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class Mainoptions extends AppCompatActivity
{
    ImageView imgmilk,imginventory,imgaccount,imgmaster;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_mainoptions);
        final List<String> list = Arrays.asList(ApplicationRuntimeStorage.AccessRights.split(","));

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Menu");
        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Mainoptions.this,Login.class);
                startActivity(intent);
            }
        });
        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        imgmilk=(ImageView)findViewById(com.vsi.smart.dairy1.R.id.imgmilk);
        imginventory=(ImageView)findViewById(com.vsi.smart.dairy1.R.id.imginventory);
        imgaccount=(ImageView)findViewById(com.vsi.smart.dairy1.R.id.imgaccount);
        imgmaster=(ImageView)findViewById(com.vsi.smart.dairy1.R.id.imgmaster);


            imgmilk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ApplicationRuntimeStorage.AccessRights.contains("MILK")) {
                        Intent intent = new Intent(Mainoptions.this, options.class);
                        intent.putExtra("option", "1");
                        startActivity(intent);

                    }else{
                        Toast.makeText(Mainoptions.this, "Access Denied !", Toast.LENGTH_LONG).show();
                    }
            }});



            imginventory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ApplicationRuntimeStorage.AccessRights.contains("INVENTORY")) {
                        Intent intent = new Intent(Mainoptions.this, options.class);
                        intent.putExtra("option", "2");
                        startActivity(intent);
                    }else{
                        Toast.makeText(Mainoptions.this, "Access Denied !", Toast.LENGTH_LONG).show();
                    }
                }
            });



            imgaccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ApplicationRuntimeStorage.AccessRights.contains("ACCOUNT")) {
                        Intent intent = new Intent(Mainoptions.this, options.class);
                        intent.putExtra("option", "3");
                        startActivity(intent);
                    }else{
                        Toast.makeText(Mainoptions.this, "Access Denied !", Toast.LENGTH_LONG).show();
                    }

                }
            });


            imgmaster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ApplicationRuntimeStorage.AccessRights.contains("MASTER")) {
                        Intent intent = new Intent(Mainoptions.this, options.class);
                        intent.putExtra("option", "4");
                        startActivity(intent);
                    }else{
                        Toast.makeText(Mainoptions.this, "Access Denied !", Toast.LENGTH_LONG).show();
                    }
                }
            });


    }
}
