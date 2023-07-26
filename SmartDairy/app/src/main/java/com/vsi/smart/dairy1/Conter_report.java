package com.vsi.smart.dairy1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.vsi.smart.dairy1.R;

public class Conter_report extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter_report);

        Intent intent=getIntent();
        int avg=intent.getIntExtra("sum",0);


    }
}
