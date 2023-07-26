package sysnotion.tagid.tagsmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import sysnotion.tagid.tagsmart.adapter.MultiSelectionAdapter;
import sysnotion.tagid.tagsmart.db.DBManager;
import sysnotion.tagid.tagsmart.nLevel.NLevelAdapter;
import sysnotion.tagid.tagsmart.nLevel.NLevelItem;
import sysnotion.tagid.tagsmart.nLevel.NLevelView;
import sysnotion.tagid.tagsmart.nLevel.SomeObject;
import sysnotion.tagid.tagsmart.utils.Constants;
import sysnotion.tagid.tagsmart.utils.FileManager;
import sysnotion.tagid.tagsmart.utils.PermissionHelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StockTakeOptionACategoryActivity extends AppCompatActivity {
    SharedPreferences pref;
    String currentCategory= new String();
    int currentLevel=0;
    TextView StoreIdTV;
    ListView listView;
    ArrayList<String>treeList = new ArrayList<String>();
    Button nextBtn;
    Button radioSelectAllButton, radioUnselectAllButton;
    Button expandButton, collapseButton ;
    String treeJSON="";

    List<NLevelItem> list;
    NLevelAdapter adapter;

    int[] colors ;
    int counter=0;
    int parentId=-1;
    JSONArray arr;
    private ProgressDialog dialog;
    int maxLevel=0;
    ArrayList<String> selectedBarcodeList = new ArrayList<String>();
    String location_id="";
    String sublocation_id="";
    JSONObject locationObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_take_option_a_category);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        try
        {
            locationObject = new JSONObject(getIntent().getStringExtra("location_JSON"));

            location_id=locationObject.getString("location_id");
            sublocation_id=locationObject.getString("sub_location_id");

            // Log.v("InventoryA","Location JSON "+locationObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        colors= getResources().getIntArray(R.array.colors_hex_code);

        dialog = new ProgressDialog(StockTakeOptionACategoryActivity.this);

        new LoadTreeAsyncTask(StockTakeOptionACategoryActivity.this).execute(Constants.DOMAIN_URL+"product_tree_by_customer_id.php");

        StoreIdTV =(TextView) findViewById(R.id.StoreIdTV);
        StoreIdTV.setText("Store ID: "+pref.getString(Constants.STORE_NUMBER,"0"));


        radioSelectAllButton =(Button) findViewById(R.id.radioSelectAllButton);
        radioUnselectAllButton =(Button) findViewById(R.id.radioUnselectAllButton);

        radioSelectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SelectUnselectAllAsyncTask(StockTakeOptionACategoryActivity.this, true).execute();
            }
        });
        radioUnselectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SelectUnselectAllAsyncTask(StockTakeOptionACategoryActivity.this, false).execute();
            }
        });

        expandButton =(Button) findViewById(R.id.expandButton);
        collapseButton =(Button) findViewById(R.id.collapseButton);

        collapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new  ExpandCollapseAllAsyncTask(StockTakeOptionACategoryActivity.this,false).execute();

            }
        });

        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new  ExpandCollapseAllAsyncTask(StockTakeOptionACategoryActivity.this,true).execute();
            }
        });



        nextBtn =(Button) findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedBarcodeList.size()>0) {


                    ArrayList<String> mArrayProducts = selectedBarcodeList;

                    if(mArrayProducts.size()> 0)
                    {
                       // Log.d(StockTakeOptionACategoryActivity.class.getSimpleName(), "Selected Items: " + mArrayProducts.toString());

                        //saving to shared preferences
                        Constants.saveArrayList(mArrayProducts,Constants.CATEGORY_ARRAY_JSON, StockTakeOptionACategoryActivity.this);
                        Intent intent = new Intent(StockTakeOptionACategoryActivity.this , SerialInventoryStockTakeAActivity.class);
                        //Testing
                        //Intent intent = new Intent(StockTakeOptionACategoryActivity.this , RNDActivity.class);

                        intent.putExtra("location_JSON",locationObject.toString());
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Please select categories " , Toast.LENGTH_LONG).show();
                    }



                }
            }
        });
    }

    private void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }



    private void NLevelExpandableListView(){

        listView = (ListView) findViewById(R.id.listView1);
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

       // Log.d("StockTakeOptionA","MaxLevel "+maxLevel);

    }

    private void nestedLoop(String levelList, NLevelItem nLevelItem, final LayoutInflater inflater, int level){

        try{
            maxLevel = level;
            JSONArray jsonArrayStringList = new JSONArray(levelList);
            int length = jsonArrayStringList.length();
            for (int i=0; i<length; i++){

                JSONObject itemObject = jsonArrayStringList.getJSONObject(i);

                if(itemObject.has("children"))
                {
                    int childrenSize = itemObject.getJSONArray("children").length();
                    NLevelItem Parent = itemView(counter, i, itemObject.getString("name"), nLevelItem, inflater, level, !(childrenSize > 0));
                    if (level == 0) {
                        parentId = -1;
                        //System.out.println("parent ID11 "+parentId);
                    } else {
                        parentId = nLevelItem.getId();
                        // System.out.println("parent ID22 "+parentId);
                    }

                    Parent.setParentId(parentId);
                    Parent.setId(counter);
                    Parent.setLevel(level);
                    Parent.setTotalQty(0);
                    Parent.setScanQty(0);
                    // Parent.toggle();
                    list.add(Parent);
                    parentId = counter;
                    counter++;
                    if (childrenSize > 0) {
                        nestedLoop(itemObject.getJSONArray("children").toString(), Parent, inflater, level + 1);
                    }
                }
            }



        }catch (Exception e){

        }

    }

    private NLevelItem itemView(final int position,final int itemRow, final String Title, final NLevelItem nLevelItem, final LayoutInflater inflater, final int level, final boolean isLast){

        NLevelItem superChild = new NLevelItem(new SomeObject(Title), nLevelItem, new NLevelView() {
            @Override
            public View getView(final  NLevelItem item) {
                View view = inflater.inflate(R.layout.nlevel_list_item, null);
                TextView tv = (TextView) view.findViewById(R.id.textView);
                String name = (String) ((SomeObject) item.getWrappedObject()).getName();
                tv.setText(name);

                tv.setBackgroundColor(colors[level]);
                LinearLayout listItemContainer = (LinearLayout)view.findViewById(R.id.listItemContainer);
                listItemContainer.setBackgroundColor(colors[level]);

                CheckBox chkbox = (CheckBox) view.findViewById(R.id.chkEnable);
                chkbox.setChecked(item.isIschecked());
                chkbox.setTag(position);


                chkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int currentPos = (int) v.getTag();
                        boolean isChecked = false;
                        if (item.isIschecked()==false){
                            isChecked=true;
                        }
                        item.setIschecked(isChecked);

                        System.out.println("parent ID "+item.getParentId());
                        System.out.println(" ID "+item.getId()+" level  "+item.getLevel());
                        System.out.println(" name "+(String) ((SomeObject) item.getWrappedObject()).getName()+" is checked  "+item.isIschecked());

                        int pid = item.getId();
                        int currentLevel = item.getLevel();
                        String name = (String) ((SomeObject) item.getWrappedObject()).getName();

                        new  UpdateCheckboxStatusAsyncTask(StockTakeOptionACategoryActivity.this,pid,currentLevel, name,isChecked).execute();


                    }
                });


                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) tv.getLayoutParams();
                mlp.setMargins(level*50, 5, 5, 5);

                if(isLast){
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(StockTakeOptionACategoryActivity.this, "Clicked on: "+Title, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                return view;
            }
        });

        return superChild;
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


    class LoadTreeAsyncTask extends AsyncTask<String, String, String> {



        private Context context;

        public LoadTreeAsyncTask(Context con) {

            context = con;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog.setMessage("Loading data...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... string) {
            String response = null;
            try {
                URL url = new URL(string[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(90000);
                conn.setConnectTimeout(90000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("X-Environment", "android");

                String store_id = pref.getString(Constants.STORE_ID,"0");

                String customer_id = pref.getString(Constants.CUSTOMER_ID,"0");
                String urlParameters = "customer_id=" + customer_id+"&location="+location_id+"&sublocation="+sublocation_id+"&store_id="+store_id;
                System.out.println("urlParameters " + urlParameters);

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
            //UI Generating loop

            NLevelExpandableListView();


            /* Call this after NLevelExpandableListView() to get lastlevl ***/
            SharedPreferences.Editor edit = pref.edit();
            edit.putString(Constants.MAX_LEVEL, String.valueOf(maxLevel));

            edit.commit();
            dismissProgressDialog();

        }
    }



    class UpdateCheckboxStatusAsyncTask extends AsyncTask<String, String, String> {



        private Context context;
        int pid,currentLevel;
        boolean status;
        String name;

        public UpdateCheckboxStatusAsyncTask(Context con, int p, int l,String n, boolean s) {

            context = con;
            pid = p;
            currentLevel = l;
            status = s;
            name = n;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog.setMessage("Updating...");

            dialog.show();
        }

        @Override
        protected String doInBackground(String... string) {
            String response = null;
            try {
                int id=0;
                for(int i=0 ; i < list.size(); i++)
                {
                    id = list.get(i).getId();

                    if(id == pid )
                    {

                        // View component =list.get(i).getView();
                        NLevelItem childItem = list.get(i);
                        //CheckBox chkbox2 = (CheckBox) component.findViewById(R.id.chkEnable);
                        childItem.setIschecked(status);
                        // chkbox2.setChecked(item.isIschecked());
                        //System.out.println("childItem.getLevel() "+childItem.getLevel());
                        if(childItem.getLevel() == (maxLevel -1) && status)
                        {
                            selectedBarcodeList.add(((SomeObject)list.get(i).getWrappedObject()).getName());
                        }
                        if(childItem.getLevel() == (maxLevel -1) && !status)
                        {
                            selectedBarcodeList.remove(((SomeObject)list.get(i).getWrappedObject()).getName());
                        }

                        if(i< (list.size()-1) )
                        {
                            int le = list.get(i+1).getLevel();
                            if(currentLevel< le) // check level inorder to apply only to the subcategories
                            {
                                pid= list.get(i+1).getId();;
                            }
                            else
                            {
                                break;
                            }

                        }

                    }
                    // System.out.print("parent ID "+list.get(i).getParentId()+" ID " + id + " level  " + ll);
                    // System.out.println(" name " + name + " is checked  " + list.get(i).isIschecked());
                }
               /* Log.d("####StockTakeOptionACategoryActivity", "Start print selected barcode ");
                for(int k =0; k<selectedBarcodeList.size();k++)
                {
                    Log.d("StockTakeOptionACategoryActivity", "selected barcode "+selectedBarcodeList.get(k));
                }
                Log.d("####StockTakeOptionACategoryActivity", "End print selected barcode ");*/



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
            dismissProgressDialog();
            adapter.notifyDataSetChanged();

        }
    }


    class SelectUnselectAllAsyncTask extends AsyncTask<String, String, String> {



        private Context context;
        boolean status;

        public SelectUnselectAllAsyncTask(Context con, boolean s) {

            context = con;

            status = s;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog.setMessage("Updating...");

            dialog.show();
        }

        @Override
        protected String doInBackground(String... string) {
            String response = null;
            try {
                int id=0;
                for(int i=0 ; i < list.size(); i++)
                {
                    NLevelItem childItem = list.get(i);
                    childItem.setIschecked(status);
                    if(childItem.getLevel() == (maxLevel -1) && status)
                    {
                        selectedBarcodeList.add(((SomeObject)list.get(i).getWrappedObject()).getName());
                    }
                    if(childItem.getLevel() == (maxLevel -1) && !status)
                    {
                        selectedBarcodeList.remove(((SomeObject)list.get(i).getWrappedObject()).getName());
                    }
                }

                 /*Log.d("####StockTakeOptionACategoryActivity", "Start print selected barcode ");
                for(int k =0; k<selectedBarcodeList.size();k++)
                {
                    Log.d("StockTakeOptionACategoryActivity", "selected barcode "+selectedBarcodeList.get(k));
                }
                Log.d("####StockTakeOptionACategoryActivity", "End print selected barcode ");*/

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
            dismissProgressDialog();
            adapter.notifyDataSetChanged();

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
            dialog.setMessage("Updating UI...");

            dialog.show();
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