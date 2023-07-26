package com.vsi.smart.dairy1;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class checkcode extends AppCompatActivity
{
    Button hello;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vsi.smart.dairy1.R.layout.activity_checkcode);
        hello=(Button)findViewById(com.vsi.smart.dairy1.R.id.hello);
        hello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LongOperation().execute();
            }
        });
    }

    private class LongOperation extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            String data="[{\"clr\":\"0.0\",\"companyid\":10,\"cb\":\"0\",\"acccode\":\"12\",\"fat\":\"2.5\",\"userid\":\"p0001\",\"bysnf\":\"0\",\"snf\":\"7.5\"}]";
            try{
                CallSoap cs = new CallSoap();
                String result = cs.GetMilkRateForQuality("1",data);
                return result;
            } catch (Exception ert) {
                return "0";
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
//            try{
//                List<MilkCustomer> list = ApplicationRuntimeStorage.LIST_CUSTOMERS;
//                list.clear();
//                JSONArray jsonarray = new JSONArray(result);
//                for (int i = 0; i < jsonarray.length(); i++) {
//                    MilkCustomer cust=  new MilkCustomer();
//                    JSONObject jsonobject = jsonarray.getJSONObject(i);
//                    String code = jsonobject.getString("code");
//                    String cname = jsonobject.getString("cname");
//                    String mobile = jsonobject.getString("mobile");
//                    cust.cname = cname;
//                    cust.code=code;
//                    cust.mobile=mobile;
//                    list.add(cust);
//                }
//            }catch (Exception e) {e.printStackTrace();}
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    @Override
    protected void onResume() {
        if(ApplicationRuntimeStorage.USERID.equals("0"))
        {
            Intent intent=new Intent(checkcode.this,Login.class);
            startActivity(intent);
        }else{}
        super.onResume();
    }
}
