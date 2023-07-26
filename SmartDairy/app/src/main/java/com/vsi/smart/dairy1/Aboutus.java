package com.vsi.smart.dairy1;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.vsi.smart.dairy1.R;

public class Aboutus extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setDisplayShowCustomEnabled(true);
//        getSupportActionBar().setCustomView(R.layout.custom_actionbar);
//        View view =getSupportActionBar().getCustomView();
//        LayoutInflater mInflater=LayoutInflater.from(this);
//        View mCustomView =mInflater.inflate(R.layout.custom_actionbar,null);
//        TextView tittle=(TextView)mCustomView.findViewById(R.id.titletxt);
//        tittle.setText("About Us");

//        ImageButton imgbutton =(ImageButton)findViewById(R.id.imgbutton);
//        imgbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                Intent intent=new Intent(Aboutus.this,options.class);
//                startActivity(intent);
//            }
//        });

       // getSupportActionBar().setCustomView(mCustomView);
        //getSupportActionBar().setDisplayShowCustomEnabled(true);
    }
}
