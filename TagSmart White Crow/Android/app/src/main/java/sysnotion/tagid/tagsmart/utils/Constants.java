package sysnotion.tagid.tagsmart.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;

import sysnotion.tagid.tagsmart.R;


public class Constants {
    //New API Changes
    public static String DOMAIN_URL_2 = "https://tagidapi.smart-iam.com/";
    public static String LOGIN_URL = DOMAIN_URL_2 + "login";
    public  static String LOGIN_URL_JSON = DOMAIN_URL_2 + "login-json";
    public static String TAG_INFO_URL = "tag/tag_info";

    public static String USER_ID_NO = "0";
    public static String EMAIL = "xyz@tagid.co.in";
    public static String LAST_LOGIN = "NA";
    public static String STORE_ID_NO = "0";
    public static String UPDATED_TIME = null;
    public static String PASSWORD = "passord";
    public static String USERNAME = "user";
    public static String USER_ROLE = "0";

    public static String PREFS_NAME ="tagsmart_shared_preference";
    //public static String DOMAIN_URL ="http://13.234.61.73/";

    public static String DOMAIN_URL ="http://13.234.61.73/ethnicity/";//"http://13.234.61.73/white_crow/";

    public static String BASE_URL =DOMAIN_URL+"api/";
    public static String USER_ID="user_id";
    public static String CUSTOMER_ID="customer_id";
    public static String STORE_ID="store_id";
    public static String CUSTOMER_NAME="customer_name";
    public static String CUSTOMER_STOCK_DATA_TABLE_NAME="customer_stock_data_table_name";
    public static String CUSTOMER_PRODUCT_DATA_TABLE_NAME="customer_product_data_table_name";
    public static String STORE_NUMBER="store_number";
    public static String ADDRESS="address";
    public static String TABLE_METADATA_JSON = "table_metadata_json";
    public static String DIRECTORY_PATH= Environment.getExternalStorageDirectory()+"/tagsmart/";
    public static String DB_NAME="TAGSMART.DB";
    public static String HOSTNAME="hostname";
    public static String PORT_NUMER="port_number";
    public static int CATEGORY_LEVEL=0;
    public static String CATEGORY_ARRAY_JSON="category_array_json";
    public static String CATEGORY_BASE_QUERY="category_base_query";
    public static final String mLogDir = "/TAGID";

    public static final String MOVEMENT_SESSION="-1";
    public static final String REPLENISHMENT_SESSION="-2";
    public static final String PICKUP_SESSION="-3";
    public static String iCODES="icodes";

    public static final String mLogFileName = "/tree";

    public static final String mExtentionName = ".json";

    public static final boolean RECV_D = false;

    public static final boolean MAIN_D = false;

    public static final boolean CON_D = false;

    public static final boolean SD_D = false;

    public static final boolean CFG_D = false;

    public static final boolean ACC_D = false;

    public static final boolean SEL_D = false;

    public static final boolean INV_D = false;

    public static final boolean BAR_D = false;

    public static final boolean BAT_D = false;

    public static final boolean INFO_D = false;

    public static boolean mSoundPlay = true;
    public static float mSoundVolume = 0;
    public static String MAX_LEVEL="max_level";
    public static String MISSING_EPC="missing_epc";
    public static String SUMMARY_JSON="summary_json";
    public static String SESSION_ID="session_id";
    public static String IS_NEW_SESSION="is_new_session";
    public static int UI_PAGINATION=50;
    public static String DISPATCH_ORDER="dispatch_order";

    public static boolean checkIfImageExisit(String name)
    {
        // File extStore = Environment.getExternalStorageDirectory();
        File myFile = new File(Constants.DIRECTORY_PATH + name);

        if(myFile.exists()){

            return true;
        }
        return false;
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    public static String StoreImageToSdcard(Bitmap image, String imageName)
    {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (long)stat.getBlockSizeLong() *(long)stat.getBlockCountLong();
        long megAvailable = bytesAvailable ;

        if(image == null)
        {

            return null;
        }

        //Toast.makeText(this.getActivity(), "#####   storeImage storing ezed profile 1111", Toast.LENGTH_SHORT).show();
        if(megAvailable > (image.getRowBytes() * image.getHeight())  )
        {
            try
            {
                //Toast.makeText(this.getActivity(), "#####   storeImage storing ezed profile 2222", Toast.LENGTH_SHORT).show();
                File APP_FILE_PATH = new File(Constants.DIRECTORY_PATH);
                if (!APP_FILE_PATH.exists())
                {
                    APP_FILE_PATH.mkdirs();
                }

                final FileOutputStream fos = new FileOutputStream( Constants.DIRECTORY_PATH+ imageName);


                try {

                    image.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                    System.out.println("### stored images");
                } catch (FileNotFoundException e) {
                    Log.d("Constants store image", "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d("Constants store image", "Error accessing file: " + e.getMessage());
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }

            return Constants.DIRECTORY_PATH+ imageName;
        }
        else {
            return null;
        }

    }

    public static String convertEPCToBarcode( String EPCNumber)
    {

        //System.out.println("EPCNumber   "+EPCNumber) ;
        String biString = new BigInteger(EPCNumber,16).toString(2);


        // System.out.println("h "+biString.substring(0,12).trim()) ;


        //System.out.println("w "+biString ) ;


        String actual_epc = biString.substring(12,biString.length()).trim();

        //System.out.println("E "+biString.substring(0,12).trim()+actual_epc) ;

        //System.out.println("M "+actual_epc) ;


        //System.out.println("barcode "+actual_epc.substring(0,44).trim()) ;

        String Bar = actual_epc.substring(0,44).trim();

        String companyprefix = Bar.substring(0,24).trim();
        //System.out.println("C "+companyprefix) ;
        //System.out.println("companyprefix   "+new BigInteger(companyprefix,2).toString(10)) ;

        String deciCompanyPrefix = new BigInteger(companyprefix,2).toString(10);

        String itemref = Bar.substring(24,44).trim();

        //System.out.println("I "+itemref) ;

        String itemref2 = itemref.substring(0,20).trim();
        //System.out.println("I2 "+itemref2) ;

        String deciItemRef = new BigInteger(itemref2,2).toString(10);

        String indicator="";
        if(deciItemRef.length() <5)
        {
            if(deciItemRef.length()==4)
            {
                deciItemRef ="0"+deciItemRef;
            }
            else
            if(deciItemRef.length()==3)
            {
                deciItemRef ="00"+deciItemRef;
            }else if(deciItemRef.length()==2)
            {
                deciItemRef ="000"+deciItemRef;
            }
            else if(deciItemRef.length()==1)
            {
                deciItemRef ="0000"+deciItemRef;
            }
        }
        else if(deciItemRef.length() >5)
        {
            String str_val = String.valueOf(deciItemRef);
            indicator= str_val.charAt(0)+"";
            deciItemRef= str_val.substring(1,str_val.length());


        }

        // System.out.println("deciItemRef   "+deciItemRef) ;


        // String serial_number = actual_epc.substring(44,actual_epc.length()).trim();

        String barcode = indicator+deciCompanyPrefix + deciItemRef;
        //System.out.println("barcode    "+barcode) ;



        String barcodeString = barcode+getCheckSumDigit(barcode);
        //System.out.println("checksumDigit added barcode    "+barcodeString) ;

        if(barcodeString.length() == 14)
        {
            //System.out.println("Barcode 14 digit  ") ;
            barcodeString = FBB_14_digit_barcode_correction(barcodeString);
        }
       // System.out.println("Barcode   "+barcodeString) ;
        return barcodeString;


    }

    private static String FBB_14_digit_barcode_correction(String barcode)
    {
        //System.out.println("barcode.substring ( 1, 8)   "+barcode.substring ( 1, 8)) ;
        //System.out.println("barcode.substring ( 8, 12)   "+barcode.substring ( 8, 12)) ;

        String correction_barcode = barcode.substring ( 1, 8)+barcode.charAt(0)+barcode.substring ( 8, 12)+barcode.charAt(12);

        return correction_barcode;
    }

    public static String getCheckSumDigit(String str)
    {
        int evenSum=0;
        int oddSum = 0 ;

        //System.out.println("Barcode  length getCheckSumDigit  "+str.length()) ;
        if(str.length() == 12)
        {
            for(int y =0 ; y< str.length(); y++)
            {
                if(y%2 == 0)
                {
                    evenSum = evenSum+ Integer.parseInt(str.charAt(y)+"");
                }
                else
                {
                    oddSum = oddSum+ Integer.parseInt(str.charAt(y)+"");
                }
            }

            oddSum = 3* oddSum;

            int sumValue = evenSum+oddSum;

            if(sumValue % 10 == 0)
            {
                return "0";
            }
            else
            {
                int subValue =  10 - (sumValue % 10);

                return subValue+"";
            }
        }
        else if(str.length() == 13)
        {
            for(int y =0 ; y< str.length(); y++)
            {
                if(y%2 == 0)
                {
                    evenSum = evenSum+ Integer.parseInt(str.charAt(y)+"");
                }
                else
                {
                    oddSum = oddSum+ Integer.parseInt(str.charAt(y)+"");
                }
            }

            evenSum = 3* evenSum;

            int sumValue = evenSum+oddSum;

            if(sumValue % 10 == 0)
            {
                return "0";
            }
            else
            {
                int subValue =  10 - (sumValue % 10);

                return subValue+"";
            }
        }
        return null;


    }

    public static void saveArrayList(ArrayList<String> list, String key, Activity act){


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();

    }

    public static void saveMissingEPCResponse(String reponse, Activity act){
       // Log.d("saveArrayList", "missing epc json "+ reponse);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.MISSING_EPC, reponse);

        editor.apply();

    }

    public static String getMissingEPCJSON(Activity act){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        String json = prefs.getString(Constants.MISSING_EPC, null);
        return json;
    }


    public static void saveSummaryResponse(String reponse, Activity act){
        // Log.d("saveArrayList", "missing epc json "+ reponse);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.SUMMARY_JSON, reponse);

        editor.apply();

    }

    public static String getSummaryJSON(Activity act){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        String json = prefs.getString(Constants.SUMMARY_JSON, null);
        return json;
    }

    public static void saveSessionID(String session, Activity act){
        // Log.d("saveArrayList", "missing epc json "+ reponse);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.SESSION_ID, session);

        editor.apply();

    }

    public static String getSessionId(Activity act){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        String json = prefs.getString(Constants.SESSION_ID, "0");
        return json;
    }
    public static ArrayList<String> getArrayList(String key, Activity act){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void saveSessionFlag(String session, Activity act){
        // Log.d("saveArrayList", "missing epc json "+ reponse);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.IS_NEW_SESSION, session);

        editor.apply();

    }
    public static String isSessionNew(Activity act){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        String json = prefs.getString(Constants.IS_NEW_SESSION, "0");
        return json;
    }




    public static void internetAlert(Context context)
    {
        new AlertDialog.Builder(context)
                .setTitle("Alert")
                .setMessage("No internet connection")
                .setIcon(R.drawable.no_internet)
                .setPositiveButton(android.R.string.ok, null).show();
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean outcome = false;

        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo[] networkInfos = cm.getAllNetworkInfo();

            for (NetworkInfo tempNetworkInfo : networkInfos) {


                /**
                 * Can also check if the user is in roaming
                 */
                if (tempNetworkInfo.isConnected()) {
                    outcome = true;
                    break;
                }
            }
        }

        return outcome;
    }

    public static ArrayList<String> createTree(ArrayList<String> arRows, String sSeperator) throws Exception
    {
        ArrayList<String> arReturnNodes = new ArrayList<String>();
        Collections.sort(arRows);
        String sLastPath = "";
        int iFolderLength = 0, maxLevel = 0;
        for(int iRow=0;iRow<arRows.size();iRow++)
        {
            String sRow = arRows.get(iRow);
            String[] sFolders = sRow.split(sSeperator);
            iFolderLength = sFolders.length;
            maxLevel = iFolderLength-1;
            String sTab = "\n";
            String[] sLastFolders = sLastPath.split(sSeperator);
            //System.out.println("sRow "+sRow);


            for(int i=0;i<iFolderLength;i++)
            {
                //System.out.println("iCounter "+i+" sLastFolders "+sLastFolders.length);
                if(i>0)
                    sTab = sTab+"	";
                if(!sLastPath.equals(sRow))
                {

                    if(sLastFolders!=null && sLastFolders.length>i)
                    {
                        if(!sLastFolders[i].equals(sFolders[i]))
                        {
                            if(i==0) {
                                if(iRow ==0) // 1st parent
                                {
                                    arReturnNodes.add(sTab+"[{\"name\":\""+sFolders[i].replaceAll("^\"|\"$", "")+"\"");
                                    sLastFolders = null;
                                }else {

                                    String endBrackets ="";
                                    int temp =(maxLevel -1);
                                    for(int e=0; e< temp; e++)
                                    {

                                        endBrackets= endBrackets+"}]";
                                    }
                                    endBrackets = endBrackets+"}";

                                    arReturnNodes.add(sTab+endBrackets+"   ,{\"name\":\""+sFolders[i].replaceAll("^\"|\"$", "")+"\"");
                                    sLastFolders = null;
                                }
                            }
                            else
                            {
                                String endBrackets ="";
                                int temp =(maxLevel-1 -i);
                                for(int e=0; e< temp; e++)
                                {

                                    endBrackets= endBrackets+"}]";
                                }
                                endBrackets = endBrackets+"}";
                                arReturnNodes.add(sTab+endBrackets+",{\"name\":\""+sFolders[i].replaceAll("^\"|\"$", "")+"\"");
                                sLastFolders = null;
                            }

                        }
                    }
                    else
                    {
                        //last child of node

                        if(i== maxLevel )
                        {
                            if(iRow == (arRows.size() -1)) // lastest child
                            {
                                String endBrackets ="";
                                int temp =(maxLevel);
                                for(int e=0; e< temp; e++)
                                {

                                    endBrackets= endBrackets+"}]";
                                }
                                arReturnNodes.add(sTab+",\"children\": [{\"name\":\""+sFolders[i].replaceAll("^\"|\"$", "")+"\"}]"+endBrackets);
                            }
                            else
                            {
                                String endBrackets ="";
                                for(int e=i; e<= iFolderLength; e++)
                                {

                                    endBrackets= endBrackets+"}]";
                                }

                                arReturnNodes.add(sTab+",\"children\": [{\"name\":\""+sFolders[i].replaceAll("^\"|\"$", "")+"\"}]");
                            }
                        }
                        else
                        {
                            arReturnNodes.add(sTab+",\"children\": [{\"name\":\""+sFolders[i].replaceAll("^\"|\"$", "")+"\"");
                        }
                    }
                }



            }

            sLastPath = sRow;
        }
        return arReturnNodes;
    }


}
