package com.vsi.smart.dairy1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Updatepassword extends AppCompatActivity {

    EditText username, oldpassword, newpassword;
    String username1, oldpassword1, newpassword1;
    Button updatebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_updatepassword);

        username = (EditText) findViewById(com.vsi.smart.dairy1.R.id.etxid_User);
        oldpassword = (EditText) findViewById(com.vsi.smart.dairy1.R.id.etxid_oldPassword);
        newpassword = (EditText) findViewById(com.vsi.smart.dairy1.R.id.etxid_newPassword);
        updatebtn=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnid_updatebtn);
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.length() == 0) {
                    Toast.makeText(Updatepassword.this, "Please Enter Username", Toast.LENGTH_SHORT).show();
                } else if (oldpassword.length() == 0) {
                    Toast.makeText(Updatepassword.this, "Please Enter Old Password", Toast.LENGTH_SHORT).show();
                } else if (newpassword.length() == 0) {
                    Toast.makeText(Updatepassword.this, "Please Enter Comment", Toast.LENGTH_SHORT).show();
                } else {
                    try {

                        username1=username.getText().toString();
                        oldpassword1=oldpassword.getText().toString();
                        newpassword1=newpassword.getText().toString();
                        CallSoap cs = new CallSoap();
                        String JSON_RESULT = cs.UpdateUserPassword(username1, oldpassword1, newpassword1); // Web Call to populate JSON ItemList
                        Toast.makeText(Updatepassword.this, JSON_RESULT, Toast.LENGTH_SHORT).show();

                    } catch (Exception er) {
                        Toast.makeText(Updatepassword.this, "ERROR :" + er.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });

    }
}
