package com.vsi.smart.dairy1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import Decoder.BASE64Decoder;

public class Notificationdetail extends AppCompatActivity {

    ImageView imgnot;
    long imageid=0;
    Drawable drawable;
    Bitmap bitmap;
    String ImagePath;
    Uri URI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_notificationdetail);
        imgnot=(ImageView)findViewById(com.vsi.smart.dairy1.R.id.imgnot);

        try {
            Bundle bundle = getIntent().getExtras();
            imageid =  Long.parseLong(bundle.getString("ImageId"));
            getIntent().putExtra("ImageId", "");

        }catch (Exception er){
            imageid = 0;

        }
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Notification Details");

        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Notificationdetail.this,Notificationlist.class);
                startActivity(intent);            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        CallSoap cs=new CallSoap();
        String JSON_RESULT= cs.GetNotificationDetails(ApplicationRuntimeStorage.COMPANYID,imageid); // Web Call to populate JSON ItemList
        try{
            JSONArray jsonarray = new JSONArray(JSON_RESULT);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String  title = jsonobject.getString("title");
                String  sdetail= jsonobject.getString("sdetail");
                String  fdetail= jsonobject.getString("fdetail");
                String  ImageData= jsonobject.getString("ImageData");

                byte[] imageByte;

                BASE64Decoder decoder = new BASE64Decoder();
                imageByte = decoder.decodeBuffer(ImageData);
                Bitmap decodebyte= BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);

                ImageView imgnot1=(ImageView) findViewById(com.vsi.smart.dairy1.R.id.imgnot);
                imgnot1.setImageBitmap(decodebyte);

                TextView nottitle1 = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtnottittle);
                nottitle1.setText(title);

                TextView notsdetail1 = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtnotsdetail);
                notsdetail1.setText(sdetail);

                TextView notfdetail1 = (TextView) findViewById(com.vsi.smart.dairy1.R.id.txtnotfdetail);
                notfdetail1.setText(fdetail);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        imgnot.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                drawable = getResources().getDrawable(com.vsi.smart.dairy1.R.drawable.logo11);
                bitmap = ((BitmapDrawable)imgnot.getDrawable()).getBitmap();
                ImagePath = MediaStore.Images.Media.insertImage(
                        getContentResolver(),                        bitmap,                        "demo_image",                        "demo_image"                );




               // URI = Uri.parse(ImagePath);
                Toast.makeText(Notificationdetail.this, "Image Saved Successfully", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Notificationdetail.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
