package com.vsi.smart.dairy1;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class CallSoap
{
    public final String SOAP_ACTION = "http://tempuri.org/AuthenticateUser";

    public  final String OPERATION_NAME = "AuthenticateUser";

    public  final String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";

    public  final String SOAP_ADDRESS = "http://www.vsitrade.com/RAJHANS/myservice.asmx";
    public CallSoap() { }

    public String SaveUserOrder(String jsonOrderString, long companyId, String userid, String orderDate)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"SaveUserOrder");

        PropertyInfo pi=new PropertyInfo();
        pi.setName("jsonOrderString");
        pi.setValue(jsonOrderString);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("companyId");
        pi.setValue(companyId);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("orderDate");
        pi.setValue(orderDate);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/SaveUserOrder", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }
    public String UpdateUserDailySaleOrder(String jsonOrderString, long companyId, String userid, String orderDate,String orderid)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"UpdateUserDailySaleOrder");

        PropertyInfo pi=new PropertyInfo();
        pi.setName("jsonorderstring");
        pi.setValue(jsonOrderString);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyId);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("orderdate");
        pi.setValue(orderDate);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("orderid");
        pi.setValue(orderid);
        pi.setType(String.class);
        request.addProperty(pi);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/UpdateUserDailySaleOrder", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }
    public String SaveUserDailySaleOrder(String jsonOrderString, long companyId, String userid, String orderDate)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"SaveUserDailySaleOrder");

        PropertyInfo pi=new PropertyInfo();
        pi.setName("jsonOrderString");
        pi.setValue(jsonOrderString);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("companyId");
        pi.setValue(companyId);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("orderDate");
        pi.setValue(orderDate);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/SaveUserDailySaleOrder", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String GetMilkReport(long companyid, String userid, String startdate, String enddate, String flag)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","GetMilkReport");

        PropertyInfo pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("startdate");
        pi.setValue(startdate);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("enddate");
        pi.setValue(enddate);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("flag");
        pi.setValue(flag);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetMilkReport", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String GetBranchList(long companyid, String userid)
    {
        SoapObject request = new SoapObject("http://vsitrade.com/","GetBranchList");

        PropertyInfo pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://rajhansapp.com/SDAIRY/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://vsitrade.com/GetBranchList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }
    public String UpdateUserPassword(String username,String oldpassword,String newpassword)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"UpdateUserPassword");
        PropertyInfo pi=new PropertyInfo();
        pi.setName("username");
        pi.setValue(username);
        pi.setType(String.class);

        request.addProperty(pi);
        pi=new PropertyInfo();
        pi.setName("oldpassword");
        pi.setValue(oldpassword);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("newpassword");
        pi.setValue(newpassword);
        pi.setType(String.class);
        request.addProperty(pi);



        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/UpdateUserPassword", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }
    public String AuthenticateUser(String username,String password,String deviceid)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","AuthenticateUser");
        PropertyInfo pi=new PropertyInfo();
        pi.setName("Username");
        pi.setValue(username);
        pi.setType(Integer.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("Password");
        pi.setValue(password);
        pi.setType(Integer.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("deviceid");
        pi.setValue(deviceid);
        pi.setType(Integer.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/AuthenticateUser", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }
    public String GetItemList(String userid,long companyid)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","GetItemList");
        PropertyInfo pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetItemList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String GetItemPackingList(String itemName,long companyid)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetItemPackingList");
        PropertyInfo pi=new PropertyInfo();
        pi.setName("ItemName");
        pi.setValue(itemName);
        pi.setType(String.class);
        request.addProperty(pi);
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetItemPackingList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String GetSaleTypeList(String userid,long companyid)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetSaleTypeList");
        PropertyInfo pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetSaleTypeList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String GetPointOfSaleList(String itemName,long companyid)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetPointOfSaleList");
        PropertyInfo pi=new PropertyInfo();
        pi.setName("saleType");
        pi.setValue(itemName);
        pi.setType(String.class);
        request.addProperty(pi);
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetPointOfSaleList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }
    public String GetLastOrderList(String userid , long companyId)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetLastOrderList");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyId);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetLastOrderList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String GetItemPackingUnit(String itemName,String packingname,long companyid)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetItemPackingUnit");
        PropertyInfo pi=new PropertyInfo();
        pi.setName("itemname");
        pi.setValue(itemName);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("packingname");
        pi.setValue(packingname);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetItemPackingUnit", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String GetDailySaleList(long companyid,String userid,String orderdate)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetDailySaleList");
        PropertyInfo pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("orderdate");
        pi.setValue(orderdate);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetDailySaleList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String SavePOSPlace(String jsonPOSstring, long companyid, String userid)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"SavePOSPlace");

        PropertyInfo pi=new PropertyInfo();
        pi.setName("jsonPOSstring");
        pi.setValue(jsonPOSstring);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/SavePOSPlace", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String GetOrderDetails(long companyid ,String userid ,long orderid)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","GetOrderDetails");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("orderid");
        pi.setValue(orderid);
        pi.setType(Long.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetOrderDetails", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }


    public String GetUserAccess(long companyid ,String username,String password)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetUserAccess");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("Username");
        pi.setValue(username);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("Password");
        pi.setValue(password);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetUserAccess", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String GetReportingList(String userid,long companyid)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetReportingList");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetReportingList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }
    public String GetNotificationList(long companyId)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetNotificationList");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyId);
        pi.setType(Long.class);
        request.addProperty(pi);



        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetNotificationList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }
    public String GetNotificationDetails(long companyId,long ImageId)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetNotificationDetails");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyId);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("ImageId");
        pi.setValue(ImageId);
        pi.setType(Long.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetNotificationDetails", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }


    public String GetMilkCollectionCustomerData(long companyid,String fromdate,String CID)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetMilkCollectionCustomerData");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("fromdate");
        pi.setValue(fromdate);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("CID");
        pi.setValue(CID);
        pi.setType(Integer.class);
        request.addProperty(pi);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetMilkCollectionCustomerData", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }



    public String GetMilkData(long companyId,String fromdate,int category,String userid)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetMilkCollectionData");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyId);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("fromdate");
        pi.setValue(fromdate);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("category");
        pi.setValue(category);
        pi.setType(Integer.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetMilkCollectionData", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String GetMilkCollectionData_Sanstha(long companyid,long me, String userid,  String fromdate, String todate,long stype)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetMilkCollectionData_Sanstha");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("me");
        pi.setValue(me);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("fromdate");
        pi.setValue(fromdate);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("todate");
        pi.setValue(todate);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("stype");
        pi.setValue(stype);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetMilkCollectionData_Sanstha", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }
    public String GetNewsList(long companyId)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetNewsList");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyId);
        pi.setType(Long.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetNewsList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }
    public String GetNewsDetails(long companyId,long ImageId)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetNewsDetails");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyId);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("ImageId");
        pi.setValue(ImageId);
        pi.setType(Long.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetNewsDetails", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }
    public String GetMilkCollectionData_CustomerWise(long companyid,String userid,String fromdate)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetMilkCollectionData_CustomerWise");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("fromdate");
        pi.setValue(fromdate);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetMilkCollectionData_CustomerWise", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }
    public String GetMilkCollectionData_SansthaRegister(long companyid,String userid,String fromdate,String todate)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetMilkCollectionData_SansthaRegister");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("fromdate");
        pi.setValue(fromdate);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("todate");
        pi.setValue(todate);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetMilkCollectionData_SansthaRegister", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String GetMilkPayment(long companyid,String userid,String fromdate,String todate)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetMilkPayment");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("fromdate");
        pi.setValue(fromdate);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("todate");
        pi.setValue(todate);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetMilkPayment", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }


    public String GetMilkPaymentList(long companyid,String fromdate,String todate)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetMilkPaymentList");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);


        pi=new PropertyInfo();
        pi.setName("fromdate");
        pi.setValue(fromdate);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("todate");
        pi.setValue(todate);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetMilkPaymentList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }




    public String GethelthNewsList(long companyId)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GethelthNewsList");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyId);
        pi.setType(Long.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GethelthNewsList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }
    public String GethelthNewsDetails(long companyId,long ImageId)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GethelthNewsDetails");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyId);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("ImageId");
        pi.setValue(ImageId);
        pi.setType(Long.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GethelthNewsDetails", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }



    public String GetjobNewsList(long companyId)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetjobNewsList");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyId);
        pi.setType(Long.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetjobNewsList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }
    public String GetjobNewsDetails(long companyId,long ImageId)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetjobNewsDetails");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyId);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("ImageId");
        pi.setValue(ImageId);
        pi.setType(Long.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetjobNewsDetails", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }


    public String GetkskNewsList(long companyId)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetkskNewsList");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyId);
        pi.setType(Long.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetkskNewsList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }
    public String GetkskNewsDetails(long companyId,long ImageId)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetkskNewsDetails");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyId);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("ImageId");
        pi.setValue(ImageId);
        pi.setType(Long.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetkskNewsDetails", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String GetStatement(long companyid, String ledgername, String userid,  String fromdate, String todate)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetStatement");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("ledgername");
        pi.setValue(ledgername);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("fromdate");
        pi.setValue(fromdate);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("todate");
        pi.setValue(todate);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetStatement", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String GetSaleEntry(long companyid,String userid, String billno,String rno , String itemGroupcode, String voucherdate)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetSaleEntry");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("billno");
        pi.setValue(billno);
        pi.setType(Long.class);
        request.addProperty(pi);



        pi=new PropertyInfo();
        pi.setName("rno");
        pi.setValue(rno);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("itemgroupcode");
        pi.setValue(itemGroupcode);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("billdate");
        pi.setValue(voucherdate);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetSaleEntry", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String GetKapatList(String userid,long companyid)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetKapatList");
        PropertyInfo pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetKapatList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }
    public String GetSubjectTopicList(long companyid,long typeid,String searchstring,long pageno)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","GetSubjectTopicList");
        PropertyInfo pi=new PropertyInfo();

        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("ctype");
        pi.setValue(typeid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("searchstring");
        pi.setValue(searchstring);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("pageno");
        pi.setValue(pageno);
        pi.setType(Long.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport = new HttpTransportSE("http://www.vsitrade.com/MPSC/MpscService.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetSubjectTopicList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String GetSubjectTopicDetails(long companyid,long ImageId,long typeid)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","GetSubjectTopicDetails");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);


        pi=new PropertyInfo();
        pi.setName("ImageId");
        pi.setValue(ImageId);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("typeid");
        pi.setValue(typeid);
        pi.setType(Long.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://www.vsitrade.com/MPSC/MpscService.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetSubjectTopicDetails", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }


    public String AddMilkEntry(long companyid,String userid, long code,double lit,double fat,double snf ,
                               double clr,double rate,double amount,String fromdate ,long me, long cb,double paidamt)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","AddMilkEntry");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("code");
        pi.setValue(""+code);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("liter");
        pi.setValue(""+lit);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("fat");
        pi.setValue(""+fat);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("snf");
        pi.setValue(""+snf);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("clr");
        pi.setValue(""+clr);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("rate");
        pi.setValue(""+rate);
        pi.setType(String.class);

        request.addProperty(pi);
        pi=new PropertyInfo();
        pi.setName("amount");
        pi.setValue(""+amount);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("fromdate");
        pi.setValue(fromdate);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("me");
        pi.setValue(""+me);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("cb");
        pi.setValue(""+cb);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("paidamt");
        pi.setValue(""+paidamt);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/AddMilkEntry", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String AddMilkEntry_sale(long companyid,String userid, long code,double lit,double fat,double snf ,double clr,double rate,double amount,String fromdate ,long me, long cb,double paidamt)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","AddMilkEntry_sale");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("code");
        pi.setValue(""+code);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("liter");
        pi.setValue(""+lit);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("fat");
        pi.setValue(""+fat);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("snf");
        pi.setValue(""+snf);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("clr");
        pi.setValue(""+clr);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("rate");
        pi.setValue(""+rate);
        pi.setType(String.class);

        request.addProperty(pi);
        pi=new PropertyInfo();
        pi.setName("amount");
        pi.setValue(""+amount);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("fromdate");
        pi.setValue(fromdate);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("me");
        pi.setValue(""+me);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("cb");
        pi.setValue(""+cb);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("paidamt");
        pi.setValue(""+paidamt);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/AddMilkEntry_sale", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String MilkCollectionList_Sale(long companyid,String userid,String fromdate,String me)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","MilkCollectionList_Sale");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("fromdate");
        pi.setValue(fromdate);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("me");
        pi.setValue(me);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/MilkCollectionList_Sale", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String MilkCollectionList(long companyid,String userid,String fromdate,String me)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","MilkCollectionList");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("fromdate");
        pi.setValue(fromdate);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("me");
        pi.setValue(me);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/MilkCollectionList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String CustomerList(long companyid,String userid,String mtype)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","CustomerList");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("mtype");
        pi.setValue(mtype);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/CustomerList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String TransactionList(long companyid,String userid,String fromdate)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","TransactionList");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("fromdate");
        pi.setValue(fromdate);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/TransactionList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String SaveTransaction(String companyid, String userid, String acccode, String vno, String vdate, String cddr, String amt, String narration, String glcode, String glcodestr)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","SaveTransaction");
        PropertyInfo pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("acccode");
        pi.setValue(acccode);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("vno");
        pi.setValue(vno);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("vdate");
        pi.setValue(vdate);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("cddr");
        pi.setValue(cddr);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("amt");
        pi.setValue(amt);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("narration");
        pi.setValue(narration);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("glcode");
        pi.setValue(glcode);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("glcodestr");
        pi.setValue(glcodestr);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/SaveTransaction", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }



    public String GetCustomerPreviousFatSNF(long companyid,String srno, long centerid, long Days)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","GetCustomerPreviousFatSNF");
        PropertyInfo pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("srno");
        pi.setValue(srno);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("centerid");
        pi.setValue(centerid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("Days");
        pi.setValue(Days);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/RateChartServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetCustomerPreviousFatSNF", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }



    public String GetMilkRateForQuality(String data,String JsonString)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","GetMilkRateForQuality");
        PropertyInfo pi=new PropertyInfo();
        pi.setName("data");
        pi.setValue(data);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("JsonString");
        pi.setValue(JsonString);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/MilkReport.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetMilkRateForQuality", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String GetMilkRateForQuality_New(String JsonString)
    {
        SoapObject request = new SoapObject("http://www.vsitrade.com/","GetMilkRateForQuality_New");
        PropertyInfo pi=new PropertyInfo();
        pi.setName("JsonString");
        pi.setValue(JsonString);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-25249.el-alt.com/sdairy/Milkreport.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://vsitrade.com/GetMilkRateForQuality_New", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

//    public String GetMilkRateForQuality_New(long companyid, String userid, String acccode, String fat, String snf,
//                                        String clr, String bysnf, String cb)
//    {
//        SoapObject request = new SoapObject("http://www.vsitrade.com/","GetMilkRateForQuality_New");
//
//        PropertyInfo pi=new PropertyInfo();
//        pi.setName("companyid");
//        pi.setValue(companyid);
//        pi.setType(long.class);
//        request.addProperty(pi);
//
//        pi=new PropertyInfo();
//        pi.setName("userid");
//        pi.setValue(userid);
//        pi.setType(String.class);
//        request.addProperty(pi);
//
//        pi=new PropertyInfo();
//        pi.setName("acccode");
//        pi.setValue(acccode);
//        pi.setType(String.class);
//        request.addProperty(pi);
//
//        pi=new PropertyInfo();
//        pi.setName("fat");
//        pi.setValue(fat);
//        pi.setType(String.class);
//        request.addProperty(pi);
//
//        pi=new PropertyInfo();
//        pi.setName("snf");
//        pi.setValue(snf);
//        pi.setType(String.class);
//        request.addProperty(pi);
//
//        pi=new PropertyInfo();
//        pi.setName("clr");
//        pi.setValue(clr);
//        pi.setType(String.class);
//        request.addProperty(pi);
//
//        pi=new PropertyInfo();
//        pi.setName("bysnf");
//        pi.setValue(bysnf);
//        pi.setType(String.class);
//        request.addProperty(pi);
//
//        pi=new PropertyInfo();
//        pi.setName("cb");
//        pi.setValue(cb);
//        pi.setType(String.class);
//        request.addProperty(pi);
//
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
//                SoapEnvelope.VER11);
//        envelope.dotNet = true;
//
//
//        envelope.setOutputSoapObject(request);
//
//        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-25249.el-alt.com/sdairy/SD_MilkCollectionServices.asmx?op=GetMilkRateForQuality_New");
//        Object response=null;
//        try
//        {
//            httpTransport.call("http://vsitrade.com/GetMilkRateForQuality_New", envelope);
//            response = envelope.getResponse();
//        }
//        catch (Exception exception)
//        {
//            response=exception.toString();
//        }
//        return response.toString();
//    }

    public String DeleteSaleOrder(long companyid,String userid,String orderid)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","DeleteSaleOrder");
        PropertyInfo pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("orderid");
        pi.setValue(orderid);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/DeleteSaleOrder", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String DeleteTransaction(long companyid, String userid, String acccode, String vno, String vdate, String glcode)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","DeleteTransaction");
        PropertyInfo pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("acccode");
        pi.setValue(acccode);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("vno");
        pi.setValue(vno);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("vdate");
        pi.setValue(vdate);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("glcode");
        pi.setValue(glcode);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/DeleteTransaction", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String SaveUserPurchaseOrder(String jsonOrderString, long companyId, String userid, String orderDate,String acccode)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","SaveUserPurchaseOrder");

        PropertyInfo pi=new PropertyInfo();
        pi.setName("jsonOrderString");
        pi.setValue(jsonOrderString);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("companyId");
        pi.setValue(companyId);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("orderDate");
        pi.setValue(orderDate);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("acccode");
        pi.setValue(acccode);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/SaveUserPurchaseOrder", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String GetDailyPurchaseList(long companyid,String userid,String orderdate)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","GetDailyPurchaseList");
        PropertyInfo pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("orderdate");
        pi.setValue(orderdate);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetDailyPurchaseList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }
    public String SaveUserOrdersmart(String jsonOrderString, long companyId, String userid, String orderDate,String acccode)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","SaveUserOrder");

        PropertyInfo pi=new PropertyInfo();
        pi.setName("jsonOrderString");
        pi.setValue(jsonOrderString);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("companyId");
        pi.setValue(companyId);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("orderDate");
        pi.setValue(orderDate);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("acccode");
        pi.setValue(acccode);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/SaveUserOrder", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String GetItemListpashu(long companyid, String userid)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","GetItemList");
        PropertyInfo pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetItemList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String GetDailySaleListsmart(long companyid,String userid,String orderdate)
    {
        SoapObject request = new SoapObject("http://tempuri.org/","GetDailySaleList");
        PropertyInfo pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("orderdate");
        pi.setValue(orderdate);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://4234-31282.el-alt.com/WSCall/Milk/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetDailySaleList", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }

    public String GetOrderDetailssmart(long companyid , String userid , String orderid)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,"GetOrderDetails");
        PropertyInfo pi=new PropertyInfo();
        pi=new PropertyInfo();
        pi.setName("companyid");
        pi.setValue(companyid);
        pi.setType(Long.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("userid");
        pi.setValue(userid);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("orderid");
        pi.setValue(orderid);
        pi.setType(Long.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE("http://www.vsitrade.com/sdairy/SD_MilkCollectionServices.asmx");
        Object response=null;
        try
        {
            httpTransport.call("http://tempuri.org/GetOrderDetails", envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception)
        {
            response=exception.toString();
        }
        return response.toString();
    }
}