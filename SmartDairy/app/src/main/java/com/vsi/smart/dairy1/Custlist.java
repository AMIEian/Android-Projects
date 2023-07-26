package com.vsi.smart.dairy1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class Custlist extends AppCompatActivity
{
    ListViewAdapter listViewAdapter;
    ListView listView;FloatingActionButton fabadd;
    private ArrayList<Person> arrayPerson = new ArrayList<>();
    private ArrayList<Person> arrayPerson1 = new ArrayList<>();
    private ArrayList<Person> arrayPerson2 = new ArrayList<>();
    private ArrayList<Person> arrayPerson_search = new ArrayList<>();
    private ArrayList<Person> arrayPerson_search1 = new ArrayList<>();
    private ArrayList<Person> arrayPerson_search2 = new ArrayList<>();
    RadioGroup idgrpctype;EditText inputSearch;int ctype=1;
    String radioflag="0";Button btnsync;
    public class Person
    {
        public String code;public String mobile;
        public String cname;public String ucode;
    }
    ListView Listview;AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_custlist);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(com.vsi.smart.dairy1.R.layout.custom_actionbar);
        View view = getSupportActionBar().getCustomView();
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(com.vsi.smart.dairy1.R.layout.custom_actionbar, null);
        TextView tittle = (TextView) mCustomView.findViewById(com.vsi.smart.dairy1.R.id.titletxt);
        tittle.setText("Customer List");
        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        listView=(ListView)findViewById(com.vsi.smart.dairy1.R.id.Listview);

        btnsync=(Button) findViewById(com.vsi.smart.dairy1.R.id.btnsync);
        btnsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new Longoperationlistvisit().execute();
                new Longoperationlistvisitsale().execute();
                new Longoperationlistvisitpurchase().execute();
            }
        });

        inputSearch = (EditText) findViewById(com.vsi.smart.dairy1.R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                String text = inputSearch.getText().toString().toLowerCase(Locale.getDefault());
                listViewAdapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        listViewAdapter = new ListViewAdapter();
        listView.setAdapter(listViewAdapter);
      //  callwebservice();
    }
    private void callwebservice()
    {
        new Longoperationlistvisit().execute();
    }

    private class Longoperationlistvisit extends AsyncTask<String, Void, String>
    {
        @Override
        protected java.lang.String doInBackground(java.lang.String... params)
        {
            java.lang.String JSON_RESULT= "";
            CallSoap cs=new CallSoap();
            JSON_RESULT= cs.CustomerList(ApplicationRuntimeStorage.COMPANYID,
                    ApplicationRuntimeStorage.USERID,"0,2,5,1"); // Web Call to populate JSON ItemList
            return  JSON_RESULT;
        }
        @Override
        protected void onPostExecute(java.lang.String result) {
            try {
                asyncDialog.dismiss();
            }catch(Exception e){e.printStackTrace();}
            try{
                listViewAdapter.notifyDataSetChanged();
                arrayPerson.clear();
                JSONArray jsonarray = new JSONArray(result);
                for (int i = 0; i < jsonarray.length(); i++) {
                    Person p = new Person();
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String code = jsonobject.getString("code");p.code=code;
                    String cname = jsonobject.getString("cname");p.cname=cname;
                    String mobile = jsonobject.getString("mobile");p.mobile=mobile;
                    String ucode = jsonobject.getString("ucode");p.ucode=ucode;
                    arrayPerson.add(p);
                }
                arrayPerson_search.clear();
                arrayPerson_search.addAll(arrayPerson);
            }catch (Exception e) {
                e.printStackTrace();
            }
            //hide the dialog
            listViewAdapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }

        ProgressDialog asyncDialog = new ProgressDialog(Custlist.this);
        @Override
        protected void onPreExecute()
        {
            //set message of the dialog
            asyncDialog.setMessage("Loading...");
            //show dialog
            asyncDialog.show();
            super.onPreExecute();
        }
        @Override
        protected void onProgressUpdate(java.lang.Void... values) {}

        protected void onStop(java.lang.Void... values) {}
    }//get Marketing

    @Override
    protected void onResume()
    {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(Custlist.this,Login.class);
            startActivity(intent);
        }else{
//            new Longoperationlistvisit().execute();
//            new Longoperationlistvisitsale().execute();
//            new Longoperationlistvisitpurchase().execute();
        }
        super.onResume();
    }
    private class ListViewAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return arrayPerson_search.size();
        }
        @Override
        public Object getItem(int position)
        {
            return arrayPerson_search.get(position);
        }
        @Override
        public long getItemId(int position)
        {
            return position;
        }
        @Override
        public View getView(int position, View view, ViewGroup viewGroup)
        {
            Person p = arrayPerson_search.get(position);
            LayoutInflater layoutInflater = getLayoutInflater();
            View rootView = layoutInflater.inflate(com.vsi.smart.dairy1.R.layout.listview_single_item_custui, null);
            try {
                // Init TextView of listview_single_item_ui.xml
                TextView textView = (TextView) rootView.findViewById(com.vsi.smart.dairy1.R.id.single_textviewname);
                textView.setText(p.cname);

                TextView textViewmobile = (TextView) rootView.findViewById(com.vsi.smart.dairy1.R.id.single_textviewcity);
                textViewmobile.setText(p.mobile);

                TextView textViewrating = (TextView) rootView.findViewById(com.vsi.smart.dairy1.R.id.single_textviewmobile);
                textViewrating.setText(p.ucode);

            } catch (Exception sf) { }
            return rootView;
        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            arrayPerson_search.clear();
            if (charText.length() == 0) {
                arrayPerson_search.addAll(arrayPerson);
            }
            else
            {
                for (Person wp : arrayPerson)
                {
                    String str = wp.code+""+wp.cname+""+wp.mobile+""+wp.ucode;
                    if (str.toLowerCase(Locale.getDefault()).contains(charText))
                    {
                        arrayPerson_search.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    private class Longoperationlistvisitsale extends AsyncTask<String, Void, String>
    {
        @Override
        protected java.lang.String doInBackground(java.lang.String... params)
        {
            java.lang.String JSON_RESULT= "";
            CallSoap cs=new CallSoap();
            JSON_RESULT= cs.CustomerList(ApplicationRuntimeStorage.COMPANYID,
                    ApplicationRuntimeStorage.USERID,"0,5"); // Web Call to populate JSON ItemList
            return  JSON_RESULT;
        }
        @Override
        protected void onPostExecute(java.lang.String result) {
            try {
                asyncDialog.dismiss();
            }catch(Exception e){e.printStackTrace();}
            try{
                listViewAdapter.notifyDataSetChanged();
                arrayPerson1.clear();
                JSONArray jsonarray = new JSONArray(result);
                for (int i = 0; i < jsonarray.length(); i++) {
                    Person p = new Person();
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String code = jsonobject.getString("code");p.code=code;
                    String cname = jsonobject.getString("cname");p.cname=cname;
                    String mobile = jsonobject.getString("mobile");p.mobile=mobile;
                    String ucode = jsonobject.getString("ucode");p.ucode=ucode;
                    ApplicationRuntimeStorage.codelistch.add(" "+code+"_"+cname);
                    arrayPerson1.add(p);
                }
                arrayPerson_search1.clear();
                arrayPerson_search1.addAll(arrayPerson1);
                ApplicationRuntimeStorage.arrayPersonapp.addAll(arrayPerson_search1);
            }catch (Exception e) {
                e.printStackTrace();
            }
            //hide the dialog
            listViewAdapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }

        ProgressDialog asyncDialog = new ProgressDialog(Custlist.this);
        @Override
        protected void onPreExecute()
        {
            //set message of the dialog
            asyncDialog.setMessage("Loading...");
            //show dialog
            asyncDialog.show();
            super.onPreExecute();
        }
        @Override
        protected void onProgressUpdate(java.lang.Void... values) {}

        protected void onStop(java.lang.Void... values) {}
    }//get Marketing


    private class Longoperationlistvisitpurchase extends AsyncTask<String, Void, String>
    {
        @Override
        protected java.lang.String doInBackground(java.lang.String... params) {

            java.lang.String JSON_RESULT= "";
            CallSoap cs=new CallSoap();
            JSON_RESULT= cs.CustomerList(ApplicationRuntimeStorage.COMPANYID,
                    ApplicationRuntimeStorage.USERID,"0,5"); // Web Call to populate JSON ItemList
            return  JSON_RESULT;
        }
        @Override
        protected void onPostExecute(java.lang.String result) {
            try
            {
                ApplicationRuntimeStorage.codelistps.clear();
                asyncDialog.dismiss();
            }
            catch(Exception e){e.printStackTrace();}
            try{
                listViewAdapter.notifyDataSetChanged();
                arrayPerson2.clear();
                JSONArray jsonarray = new JSONArray(result);
                for (int i = 0; i < jsonarray.length(); i++) {
                    Person p = new Person();
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String code = jsonobject.getString("code");p.code=code;
                    String cname = jsonobject.getString("cname");p.cname=cname;
                    String mobile = jsonobject.getString("mobile");p.mobile=mobile;
                    String ucode = jsonobject.getString("ucode");p.ucode=ucode;
                    ApplicationRuntimeStorage.codelistps.add(" "+code+"_"+cname);
                    arrayPerson2.add(p);
                }
                arrayPerson_search2.clear();
                arrayPerson_search2.addAll(arrayPerson2);
            }catch (Exception e) {
                e.printStackTrace();
            }
            //hide the dialog
            listViewAdapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }

        ProgressDialog asyncDialog = new ProgressDialog(Custlist.this);
        @Override
        protected void onPreExecute()
        {
            //set message of the dialog
            asyncDialog.setMessage("Loading...");
            //show dialog
            asyncDialog.show();
            super.onPreExecute();
        }
        @Override
        protected void onProgressUpdate(java.lang.Void... values) {}

        protected void onStop(java.lang.Void... values) {}
    }//get Marketing
}

