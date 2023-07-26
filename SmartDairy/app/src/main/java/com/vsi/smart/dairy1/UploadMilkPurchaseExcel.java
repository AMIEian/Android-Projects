package com.vsi.smart.dairy1;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.inputmethodservice.Keyboard;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vsi.smart.dairy1.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class UploadMilkPurchaseExcel extends AppCompatActivity {

    Calendar c = Calendar.getInstance();
    public final int mYear = c.get(Calendar.YEAR);
    public  final int mMonth = c.get(Calendar.MONTH);
    public final  int mDay = c.get(Calendar.DAY_OF_MONTH);
    public  int pday;
    public int pmonth;
    public int pyear;
    Button btnsubmit,btnSelectPhoto;
    String detail,title,status,cdate,photo;
    SimpleDateFormat df;

    TextView txtdate;
    ImageView ivImage;
    String strtxtdate;String formatteddatetime;
    private ArrayList<Person> arrayPerson = new ArrayList<>();
    private  class Person
    {
        public String inkm;
        public String outkm;
        public String outphoto;
        public String inphoto;
    }
    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadmilkpurchaseexcel);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);
        View view = getSupportActionBar().getCustomView();
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        TextView tittle = (TextView) mCustomView.findViewById(R.id.titletxt);
        tittle.setText("Upload Milk Purchase Excel");
        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        ivImage=(ImageView)findViewById(R.id.ivImage);


        txtdate=(TextView)findViewById(R.id.txtdate);

        Calendar c= Calendar.getInstance();
        SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
        String formatteddate=df.format(c.getTime());
        txtdate.setText(formatteddate);

        SimpleDateFormat dftime=new SimpleDateFormat("hh:mm a");
        formatteddatetime=dftime.format(c.getTime());



        txtdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DatePickerDialog datePickerDialog = new DatePickerDialog(UploadMilkPurchaseExcel.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth)
                            {
                                txtdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                pyear=year;
                                pmonth=monthOfYear;
                                pday=dayOfMonth;
                                SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
                                Date date1 = new Date(year-1900, (monthOfYear), dayOfMonth);
                                String currdate="";
                                currdate=df.format(date1.getTime());
                                txtdate.setText(currdate);

//                                Toast.makeText(Birthday.this,pyear+ "-"+pmonth+"-"+pday, Toast.LENGTH_SHORT).show();





                            }
                        },mYear,mMonth,mDay);

                datePickerDialog.show();

            }
        });



        btnSelectPhoto=(Button)findViewById(R.id.btnSelectPhoto);
        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        btnsubmit=(Button)findViewById(R.id.btnsubmit);
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                strtxtdate=txtdate.getText().toString();
                photo=encoded;
//                if(null == encoded || "".equals(encoded)){
//                    Toast.makeText(AddcheckIN.this, "Please Add Start Km Meter Photo", Toast.LENGTH_LONG).show();
//                    return;
//                }

            }
        });
        new Longsavedate2().execute();
    }

    private void selectImage() {
        final CharSequence[] items = { "SmartDairy Excel","Prompt Excel","IMS CSV",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadMilkPurchaseExcel.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(UploadMilkPurchaseExcel.this);

                if (items[item].equals("SmartDairy Excel")) {
                    userChoosenTask ="SmartDairy Excel";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Prompt Excel")) {
                    userChoosenTask ="Prompt Excel";
                    if(result)
                        galleryIntent();

                }else if (items[item].equals("IMS CSV")) {
                    userChoosenTask ="IMS CSV";
                    if(result)
                        galleryIntent();

                }
                else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        if(userChoosenTask.equalsIgnoreCase("IMS CSV")) {
            intent.setType("text/*");
        }else {
            intent.setType("application/vnd.ms-excel");
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode ==
                Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            }
        }
    }

    String TAG ="main";
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;
    String encoded = "";
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

//
//
//        //_____________________________________________
//        try {
//            InputStream myInput;
//            // initialize asset manager
//            AssetManager assetManager = getAssets();
//            //  open excel sheet
//            myInput = assetManager.open( data.getData().getPath());
//            // Create a POI File System object
//            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
//            // Create a workbook using the File System
//            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
//            // Get the first sheet from workbook
//            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
//            // We now need something to iterate through the cells.
//            Iterator<Keyboard.Row> rowIter = mySheet.rowIterator();
//            int rowno =0;
//            textView.append("\n");
//            while (rowIter.hasNext()) {
//                Log.e(TAG, " row no "+ rowno );
//                HSSFRow myRow = (HSSFRow) rowIter.next();
//                if(rowno !=0) {
//                    Iterator<Cell> cellIter = myRow.cellIterator();
//                    int colno =0;
//                    String sno="", date="", det="";
//                    while (cellIter.hasNext()) {
//                        HSSFCell myCell = (HSSFCell) cellIter.next();
//                        if (colno==0){
//                            sno = myCell.toString();
//                        }else if (colno==1){
//                            date = myCell.toString();
//                        }else if (colno==2){
//                            det = myCell.toString();
//                        }
//                        colno++;
//                        Log.e(TAG, " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
//                    }
//                    //textView.append( sno + " -- "+ date+ "  -- "+ det+"\n");
//                }
//                rowno++;
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "error "+ e.toString());
//        }
//        //____________________________________________
//






    }

    private class Longsavedate extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            CallSoap cs = new CallSoap();
            String result ="";
//                    cs.SaveCheckInOutKM(ApplicationRuntimeStorage.COMPANYID,ApplicationRuntimeStorage.USERID,
//                    stretxkm,""+ApplicationRuntimeStorage.GPS_Latitude,
//                    ""+ApplicationRuntimeStorage.GPS_Longitude,ApplicationRuntimeStorage.GPS_CityName,encoded );
            return result;
        }
        @Override
        protected void onPostExecute(String JSON_RESULT) {
            asyncDialog.dismiss();
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(UploadMilkPurchaseExcel.this);
                builder.setMessage(JSON_RESULT);
                builder.setCancelable(false);
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
//                        Intent intent=new Intent(Addnotification.this,Notificationlist.class);
//                        startActivity(intent);
                        onBackPressed();
                    }
                });
                builder.show();
            }catch (Exception e){}
            //hide the dialog
        }
        ProgressDialog asyncDialog = new ProgressDialog(UploadMilkPurchaseExcel.this);
        @Override
        protected void onPreExecute() {
            //set message of the dialog
            asyncDialog.setMessage("Loading...");
            //show dialog
            asyncDialog.show();
            super.onPreExecute();
        }
        @Override
        protected void onProgressUpdate(Void... values) {}
    }
    private class Longsavedate2 extends AsyncTask<String, Void, String> {

        Calendar c= Calendar.getInstance();
        SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
        String formatteddate=df.format(c.getTime());

        @Override
        protected String doInBackground(String... params) {
            CallSoap cs = new CallSoap();
            String result = "";//cs.GetCheckInOutList(ApplicationRuntimeStorage.COMPANYID,formatteddate,ApplicationRuntimeStorage.USERID);
            return result;
        }
        @Override
        protected void onPostExecute(String JSON_RESULT) {
            asyncDialog.dismiss();
            //hide the dialog
            String inkm = null,inphoto=null;
            try{
                double f1amount=0;
                arrayPerson.clear();
                JSONArray jsonarray = new JSONArray(JSON_RESULT);
                for (int i = 0; i < jsonarray.length(); i++) {
                    Person p = new Person();
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    inkm = jsonobject.getString("inkm");
                    p.inkm=inkm;
                    inphoto = jsonobject.getString("inphoto");
                    p.inphoto=inphoto;
                    String outkm = jsonobject.getString("outkm");
                    p.outkm=outkm;
                    String outphoto = jsonobject.getString("outphoto");
                    p.outphoto=outphoto;
                    arrayPerson.add(p);
                }


                try{
                    byte [] encodeByte=Base64.decode(inphoto, Base64.DEFAULT);
                    Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                    ivImage.setImageBitmap(bitmap);
                }catch(Exception e){
                    e.getMessage();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        ProgressDialog asyncDialog = new ProgressDialog(UploadMilkPurchaseExcel.this);
        @Override
        protected void onPreExecute() {
            //set message of the dialog
            asyncDialog.setMessage("Loading...");
            //show dialog
            asyncDialog.show();
            super.onPreExecute();
        }
        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
