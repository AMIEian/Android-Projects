package sysnotion.tagid.tagsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import sysnotion.tagid.tagsmart.adapter.InventoryAdapter;
import sysnotion.tagid.tagsmart.adapter.MissingEPCAdapter;
import sysnotion.tagid.tagsmart.db.DBManager;
import sysnotion.tagid.tagsmart.model.BarcodeEPCSearch;
import sysnotion.tagid.tagsmart.model.Inventory;
import sysnotion.tagid.tagsmart.nLevel.NLevelAdapter;
import sysnotion.tagid.tagsmart.nLevel.NLevelItem;
import sysnotion.tagid.tagsmart.nLevel.NLevelView;
import sysnotion.tagid.tagsmart.nLevel.SomeObject;
import sysnotion.tagid.tagsmart.utils.Constants;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class InwardStockSummaryActivity extends AppCompatActivity {
    SharedPreferences pref;
    private DBManager dbManager;
    JSONObject locationObject;
    TextView StoreIdShopTV, progressTV, precentTV, timeTV, totalScanTV;
    private ProgressDialog pDialog;
    ListView listView;
    String treeJSON="";
    Button expandButton, collapseButton;
    int[] colors ;
    int lastLevel = 0;
    List<NLevelItem> list;
    NLevelAdapter adapter;
    int counter=0;
    int parentId=-1;
    int maxLevel=0;

    //for storing category values
    java.util.HashMap<String,Integer> hashMapBarcode=new HashMap<String,Integer>();
    java.util.HashMap<String,Integer> hashMapBarcodePosition=new HashMap<String,Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inward_stock_summary);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        dbManager = new DBManager(InwardStockSummaryActivity.this);

        pDialog = new ProgressDialog(InwardStockSummaryActivity.this);

        StoreIdShopTV =(TextView) findViewById(R.id.StoreIdShopTV);
        StoreIdShopTV.setText("Store ID: "+pref.getString(Constants.STORE_NUMBER,"0"));
        totalScanTV =(TextView) findViewById(R.id.totalScanTV);
        totalScanTV.setText(getIntent().getStringExtra("scanned"));
        progressTV =(TextView) findViewById(R.id.progressTV);
        progressTV.setText(getIntent().getStringExtra("progress"));

        precentTV =(TextView) findViewById(R.id.precentTV);
        precentTV.setText(getIntent().getStringExtra("percentage"));

        timeTV =(TextView) findViewById(R.id.timeTV);
        timeTV.setText(getIntent().getStringExtra("time"));
        lastLevel = Integer.parseInt(pref.getString(Constants.MAX_LEVEL,"0"));

        expandButton =(Button) findViewById(R.id.expandButton);
        collapseButton =(Button) findViewById(R.id.collapseButton);

        collapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new  ExpandCollapseAllAsyncTask(InwardStockSummaryActivity.this,false).execute();
            }
        });

        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new ExpandCollapseAllAsyncTask(InwardStockSummaryActivity.this,true).execute();
            }
        });

       colors= getResources().getIntArray(R.array.colors_hex_code);
        if(Constants.isNetworkAvailable(InwardStockSummaryActivity.this)) {
            new InwardStockSummaryActivity.StockTakeSummaryAsyncTask(InwardStockSummaryActivity.this).execute(Constants.BASE_URL+"InwardingSummary.php");
        }
        else
        {
            Constants.internetAlert(InwardStockSummaryActivity.this);
        }
    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        dbManager.close();
        super.onDestroy();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    private void NLevelExpandableListView(){



        listView = (ListView) findViewById(R.id.summaryListView);
        list = new ArrayList<NLevelItem>();
        final LayoutInflater inflater = LayoutInflater.from(this);
        nestedLoop(treeJSON, null, inflater, 0);

        adapter = new NLevelAdapter(list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                ((NLevelAdapter)listView.getAdapter()).toggle(arg2);
                ((NLevelAdapter)listView.getAdapter()).getFilter().filter();
            }
        });



    }

    private void nestedLoop(String levelList, NLevelItem nLevelItem, final LayoutInflater inflater, int level){

        try{
            maxLevel = level;
            JSONArray jsonArrayStringList = new JSONArray(levelList);
            int length = jsonArrayStringList.length();

            //System.out.println("jsonArrayStringList  "+jsonArrayStringList);
            //System.out.println("levelList  "+levelList);
            for (int i=0; i<length; i++){

                JSONObject itemObject = jsonArrayStringList.getJSONObject(i);

                // System.out.println("category0000  "+itemObject.getString("name"));
                if(itemObject.has("children"))
                {

                    int childrenSize = itemObject.getJSONArray("children").length();
                    //System.out.println("category111  "+itemObject.getString("name")+" no.of child "+childrenSize);

                    NLevelItem Parent = itemView(counter,i, itemObject.getString("name"), nLevelItem, inflater, level, !(childrenSize>0));
                    if(level==0)
                    {
                        parentId = -1;
                        //System.out.println("parent ID11 "+parentId);
                    }
                    else
                    {
                        parentId = nLevelItem.getId();
                        // System.out.println("parent ID22 "+parentId);
                    }

                    Parent.setParentId(parentId);
                    Parent.setId(counter);
                    Parent.setLevel(level);
                    Parent.setTotalQty(0);
                    Parent.setScanQty(0);
                    //  Parent.toggle();
                    list.add(Parent);
                    if(level == (lastLevel -1 ))//i.e. maxlevel  -1
                    {

                        //System.out.println("## no child found ############ "+((SomeObject)Parent.getWrappedObject()).getName());
                        if( ((SomeObject) Parent.getWrappedObject()).getName().trim().length() > 0)
                        {
                            hashMapBarcodePosition.put((String) ((SomeObject) Parent.getWrappedObject()).getName(),counter);
                            hashMapBarcode.put((String) ((SomeObject) Parent.getWrappedObject()).getName(),0);
                        }

                    }


                    parentId = counter;
                    counter++;

                    if(childrenSize>0){
                        nestedLoop(itemObject.getJSONArray("children").toString(), Parent, inflater, level+1);
                    }
                }
                else
                {
                    // System.out.println("category111  "+itemObject.getString("name"));
                    NLevelItem Parent = list.get(list.size() -1);
                    String qtyScanQty = itemObject.getString("name");
                    String param [] = qtyScanQty.split(";");

                    if(param.length == 2)
                    {
                        Parent.setTotalQty(Integer.parseInt(param[0]));
                        Parent.setScanQty(Integer.parseInt(param[1]));
                    }
                    else
                    {
                        Parent.setTotalQty(0);
                        Parent.setScanQty(0);
                    }

                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private NLevelItem itemView(final int position,final int itemRow, final String Title, final NLevelItem nLevelItem, final LayoutInflater inflater, final int level, final boolean isLast){

        NLevelItem superChild = new NLevelItem(new SomeObject(Title), nLevelItem, new NLevelView() {
            @Override
            public View getView(final  NLevelItem item) {
                View view = inflater.inflate(R.layout.nlevel_list_item_without_chkbx, null);
                TextView tv = (TextView) view.findViewById(R.id.textView);
                String name = (String) ((SomeObject) item.getWrappedObject()).getName();

                tv.setText(name);

                TextView tv2 = (TextView) view.findViewById(R.id.qtytextView);
                tv2.setText(item.getScanQty()+" / "+item.getTotalQty());

                LinearLayout listItemContainer = (LinearLayout)view.findViewById(R.id.listItemContainer);
                listItemContainer.setBackgroundColor(colors[level]);

                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) tv.getLayoutParams();
                mlp.setMargins(level*50, 5, 5, 5);

                if(isLast){
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(InwardStockSummaryActivity.this, "Clicked on: "+Title, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                return view;
            }
        });

        return superChild;
    }


    private void updateQuantityOfCategorySubcategory()
    {
        try
        {

            for (Integer pos : hashMapBarcodePosition.values()) {

                int position = pos;

                //System.out.println("barcode " + obj.getString("barcode") + " Position " + obj.getString("position") );
                if(list.size()>= position)
                {

                    NLevelItem Parent = list.get(position);
                    int qty = Parent.getTotalQty();
                    int scanQty = Parent.getScanQty();
                    int ll = Parent.getLevel();
                    int parentId = Parent.getParentId();
                    //System.out.println("barcode " + obj.getString("barcode") +" totalQty " + totalQty + " qty " + qty );
                    while(ll != -1 & parentId != -1)
                    {
                        NLevelItem superParent = list.get(parentId);

                        int qty2 = qty+superParent.getTotalQty();
                        int scanQty2 = scanQty+ superParent.getScanQty();
                        superParent.setTotalQty(qty2);
                        superParent.setScanQty(scanQty2);
                        ll = superParent.getLevel();
                        parentId = superParent.getParentId();
                    }

                }
            }

            //update UI of treelistview
            adapter.notifyDataSetChanged();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    class StockTakeSummaryAsyncTask extends AsyncTask<String, String, String> {

        public StockTakeSummaryAsyncTask(Activity activity) {


        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Generating Moving Summary...");

            pDialog.show();

        }


        @Override
        protected String doInBackground(String... string) {
            String response = null;
            try {
                String customer_id = pref.getString(Constants.CUSTOMER_ID,"0");

                URL url = new URL(string[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(900000);
                conn.setConnectTimeout(900000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("X-Environment", "android");

                Date date = new Date();
                String modifiedDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

                String time = sdf.format(cal.getTime());
                String user_id = pref.getString(Constants.USER_ID,"0");
                String store_id = pref.getString(Constants.STORE_ID,"0");
                String sessionId = Constants.getSessionId(InwardStockSummaryActivity.this);

                String stock_table_name = pref.getString(Constants.CUSTOMER_STOCK_DATA_TABLE_NAME,"");
                String product_table_name = pref.getString(Constants.CUSTOMER_PRODUCT_DATA_TABLE_NAME,"");
                String dispatch_order = pref.getString(Constants.DISPATCH_ORDER,"0");

                String urlParameters = "user_id="+user_id+"&session_id="+sessionId+"&stock_table_name=" + stock_table_name+"&product_table_name=" + product_table_name+"&customer_id="+customer_id+"&store_id="+store_id+"&dispatch_order="+dispatch_order ;


                Log.d("InwardStockSummaryActi", "urlParameters "+ urlParameters);

                OutputStream wr = conn.getOutputStream();
                wr.write(urlParameters.getBytes("UTF-8"));
                wr.flush();
                wr.close();
                // Add any data you wish to post here

                conn.connect();
                int responseCode = conn.getResponseCode();
                System.out.println("responseCode " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the input stream into a String
                    InputStream inputStream = conn.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        return null;
                    }

                    treeJSON = buffer.toString();

                    //System.out.println("query "+treeJSON);
                    return  treeJSON;
                } else {
                    return null;

                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }


        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dismissProgressDialog();
            System.out.println("InwardStockSummaryActivity Response "+result);

            if (result != null) {
                try
                {
                    NLevelExpandableListView();

                    // Date d1 = new Date();

                    updateQuantityOfCategorySubcategory();



                } catch (Exception e) {

                    // e.printStackTrace();
                    Toast.makeText(InwardStockSummaryActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                }


            } else {

                Toast.makeText(InwardStockSummaryActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
            }
        }
    }

    class ExpandCollapseAllAsyncTask extends AsyncTask<String, String, String> {



        private Context context;
        boolean status;

        public ExpandCollapseAllAsyncTask(Context con, boolean s) {

            context = con;

            status = s;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Updating UI...");

            pDialog.show();
        }

        @Override
        protected String doInBackground(String... string) {
            String response = null;
            try {
                if(list.size() ==0)
                {
                    return null;
                }
                //Log.d("StockTakeOptionA","((NLevelAdapter)listView.getAdapter()).getCount() "+list.size());
                for(int c= 1 ; c< list.size(); c++)
                {
                    list.get(c).set_isExpanded(status);

                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            listView.performItemClick(listView.getChildAt(0), 0, listView.getItemIdAtPosition(0));
            dismissProgressDialog();

        }
    }


}