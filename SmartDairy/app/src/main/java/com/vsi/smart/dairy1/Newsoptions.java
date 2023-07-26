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

public class Newsoptions extends AppCompatActivity {

    Button Dairynews,ksknews,jobnews,healthnews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_newsoptions);

        Dairynews=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnDairyNews);
        ksknews=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnKSKNews);
        jobnews=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnjob);
        healthnews=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnHealthTips);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("महत्वाच्या बातम्या");

        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Newsoptions.this,options.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        Dairynews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(Newsoptions.this,Chapterlist.class);

                intent.putExtra("Subcode","0");
                startActivity(intent);;

            }
        });
        ksknews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(Newsoptions.this,Chapterlist.class);

                intent.putExtra("Subcode","1");
                startActivity(intent);;
            }
        });
        jobnews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(Newsoptions.this,Chapterlist.class);

                intent.putExtra("Subcode","2");
                startActivity(intent);;
            }
        });
        healthnews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(Newsoptions.this,Chapterlist.class);

                intent.putExtra("Subcode","3");
                startActivity(intent);;
            }
        });
    }
    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Newsoptions.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
