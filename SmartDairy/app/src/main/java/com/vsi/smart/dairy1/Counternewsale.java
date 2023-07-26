package com.vsi.smart.dairy1;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Counternewsale extends AppCompatActivity {

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Button btnSelect;
    private ImageView ivImage;
    private String userChoosenTask;

    Button btn_save;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_counternewsale);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();
        LayoutInflater mInflater=LayoutInflater.from(this);
        View mCustomView =mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar,null);
        TextView tittle=(TextView)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("New Sale Point");
        ImageButton imgbutton =(ImageButton)mCustomView.findViewById(com.vsi.smart.dairy1.R.id.imgbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Counternewsale.this,Menu_Retail_Sale.class);
                startActivity(intent);            }
        });
        addItemsOnSpinner5();
        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        final TableLayout table1 = (TableLayout) findViewById(com.vsi.smart.dairy1.R.id.table2);

        ivImage=(ImageView) findViewById(com.vsi.smart.dairy1.R.id.ivImage);
        btnSelect = (Button) findViewById(com.vsi.smart.dairy1.R.id.btnSelectPhoto);
        btnSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        final ImageView ivImage = (ImageView) findViewById(com.vsi.smart.dairy1.R.id.ivImage);


        btn_save=(Button)findViewById(com.vsi.smart.dairy1.R.id.btnsave);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Spinner stp = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountercode1);
                String stp_nm = stp.getSelectedItem().toString();

                JSONArray jsonArray = new JSONArray();
                double tqty = 0;


                EditText etx_Name = (EditText) findViewById(com.vsi.smart.dairy1.R.id.etxname);
                EditText etx_add = (EditText) findViewById(com.vsi.smart.dairy1.R.id.etxadd);
                EditText etx_cont = (EditText) findViewById(com.vsi.smart.dairy1.R.id.etxcontact);
                EditText etx_email = (EditText) findViewById(com.vsi.smart.dairy1.R.id.etxemail);

                String Name = etx_Name.getText().toString();
                String address = etx_add.getText().toString();
                String contact = etx_cont.getText().toString();
                String email = etx_email.getText().toString();


                JSONObject student2 = new JSONObject();
                try {

                    student2.put("cname", Name);
                    student2.put("address", address);
                    student2.put("mobile", contact);
                    student2.put("email", email);
                    student2.put("companyid", ApplicationRuntimeStorage.COMPANYID);
                    student2.put("saletype", stp_nm);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                jsonArray.put(student2);

                try{

                    CallSoap cs=new CallSoap();
                    String JSON_RESULT= cs.SavePOSPlace(jsonArray.toString(),ApplicationRuntimeStorage.COMPANYID,ApplicationRuntimeStorage.USERID); // Web Call to populate JSON ItemList
                    Toast.makeText(Counternewsale.this, JSON_RESULT, Toast.LENGTH_SHORT).show();
                    //  Toast.makeText(Counternewsale.this, "Counter Created Successfully", Toast.LENGTH_SHORT).show();
                }catch(Exception er)
                {
                    Toast.makeText(Counternewsale.this, "ERROR :"+er.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private Spinner spinner5;
    // add items into spinner dynamically
    public void addItemsOnSpinner5()
    {
        Spinner spinner5 = (Spinner) findViewById(com.vsi.smart.dairy1.R.id.spnCountercode1);
       /* List<String> list = new ArrayList<>();
        CallSoap cs=new CallSoap();
        String JSON_RESULT= cs.GetSaleTypeList(ApplicationRuntimeStorage.USERID,ApplicationRuntimeStorage.COMPANYID); // Web Call to populate JSON ItemList
        try{
            JSONArray jsonarray = new JSONArray(JSON_RESULT);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String itemCode = jsonobject.getString("Key");
                String itemName = jsonobject.getString("Value");
                list.add(itemName);
            }
        }catch (Exception e) {e.printStackTrace();}*/
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, ApplicationRuntimeStorage.LIST_SALE_TYPE);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner5.setAdapter(dataAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(Counternewsale.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(Counternewsale.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode ==
                Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ivImage.setImageBitmap(bm);
    }

    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Counternewsale.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
