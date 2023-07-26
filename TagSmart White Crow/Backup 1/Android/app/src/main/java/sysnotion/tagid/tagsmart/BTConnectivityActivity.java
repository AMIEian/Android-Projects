package sysnotion.tagid.tagsmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import co.kr.bluebird.sled.BTReader;
import co.kr.bluebird.sled.SDConsts;
import sysnotion.tagid.tagsmart.adapter.TagListAdapter;
import sysnotion.tagid.tagsmart.utils.ListItem;
import sysnotion.tagid.tagsmart.utils.PermissionHelper;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Set;

public class BTConnectivityActivity extends AppCompatActivity {
    private static final String TAG = BTConnectivityActivity.class.getSimpleName();

    private static final boolean D = false;

    private TextView mActionTextView;

    private TextView mMessageTextView;

    private TextView mConnectedDeviceTextView;

    private Button mDisconnectBt;

    private Button mEnableBt;

    private Button mDisableBt;

    private Button mGetBtStateBt;

    private Button mScanBt;

    private Button mStopScanBt;

    private Button mRemoveAllPairedBt ;

    private BTReader mReader;

    private Context mContext;

    private TagListAdapter mAdapter;

    private ListView mDeviceList;

    private ProgressBar mProgressBar;
    private final ConnectivityHandler mConnectivityHandler = new ConnectivityHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_connectivity);
        mContext = this.getBaseContext();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mActionTextView = (TextView)findViewById(R.id.action_textview);

        mMessageTextView = (TextView)findViewById(R.id.message_textview);

        mConnectedDeviceTextView = (TextView)findViewById(R.id.connected_device_textview);

        mDisconnectBt = (Button)findViewById(R.id.bt_disconnect);
        mDisconnectBt.setOnClickListener(buttonListener);

        mScanBt = (Button)findViewById(R.id.bt_scan);
        mScanBt.setOnClickListener(buttonListener);

        mStopScanBt = (Button)findViewById(R.id.bt_stop_scan);
        mStopScanBt.setOnClickListener(buttonListener);

        mEnableBt = (Button)findViewById(R.id.bt_enable);
        mEnableBt.setOnClickListener(buttonListener);

        mDisableBt = (Button)findViewById(R.id.bt_disable);
        mDisableBt.setOnClickListener(buttonListener);

        mGetBtStateBt = (Button)findViewById(R.id.bt_state);
        mGetBtStateBt.setOnClickListener(buttonListener);

        mProgressBar = (ProgressBar)findViewById(R.id.scan_progress);

        mRemoveAllPairedBt = (Button)findViewById(R.id.bt_remove_pair);
        mRemoveAllPairedBt.setOnClickListener(buttonListener);

        mDeviceList = (ListView)findViewById(R.id.device_list);
        mDeviceList.setOnItemClickListener(listListener);
        mAdapter = new TagListAdapter(mContext);

        mDeviceList.setAdapter(mAdapter);


    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        mReader = BTReader.getReader(mContext, mConnectivityHandler);
        boolean openResult = false;
        if (mReader != null){
            if (mReader.BT_IsEnabled())
            {
                addPairedDevices();
            }

            if (mReader.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED)
            {
                updateConnectedInfo(mReader.BT_GetConnectedDeviceName() + "\n" + mReader.BT_GetConnectedDeviceAddr());
            }
            else
            {
                updateConnectedInfo("");
            }

        }
        else
        {
            Log.e(TAG, "Reader open failed");
        }


        super.onStart();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        if (D) Log.d(TAG, "onStop");
        super.onStop();
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

    private OnClickListener buttonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int ret = -100;
            String retString = null;
            Set<BluetoothDevice> pairedDevices = null;
            switch (v.getId()) {
                case R.id.bt_disconnect:
                    if (mReader.BT_Disconnect() == SDConsts.BTResult.SUCCESS)
                        retString = "Disconnect";
                    else
                        retString = "Disconnect failed";
                    break;
                case R.id.bt_enable:
                    if (mReader.BT_Enable())
                        retString = "Bluetooth Enable";
                    else
                        retString = "Bluetooth Enable failed";
                    break;
                case R.id.bt_disable:
                    mAdapter.removeAllItem();
                    if (mReader.BT_Disable())
                        retString = "Bluetooth Disable";
                    else
                        retString = "Bluetooth Disable failed";
                    break;
                case R.id.bt_state:
                    retString = "Bluetooth State = ";
                    if (mReader.BT_IsEnabled())
                        retString += "Enabled";
                    else
                        retString += "Disabled";
                    break;
                case R.id.bt_scan:
                    addPairedDevices();
                    boolean b = PermissionHelper.checkPermission(mContext, PermissionHelper.mLocationPerms[0]);
                    if (b) {
                        if (mReader.BT_StartScan()) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            retString = "Bluetooth Scan";
                        } else
                            retString = "Bluetooth Scan failed";
                    }
                    else
                        PermissionHelper.requestPermission(BTConnectivityActivity.this, PermissionHelper.mLocationPerms);
                    break;
                case R.id.bt_stop_scan:
                    if (mReader.BT_StopScan()) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        retString = "Bluetooth Stop Scan";
                    }
                    else
                        retString = "Bluetooth Stop Scan failed";
                    break;
                case R.id.bt_remove_pair:
                    if (mReader.BT_StopScan())
                        mProgressBar.setVisibility(View.INVISIBLE);
                    pairedDevices = mReader.BT_GetPairedDevices();
                    if (pairedDevices != null && pairedDevices.size() > 0) {
                        for (BluetoothDevice d : pairedDevices)
                            mReader.BT_UnpairDevice(d.getAddress());
                        retString = "Bluetooth Remove All Paired";
                    }
                    mAdapter.removeAllItem();
                    break;
            }
            if (ret != -100) {
                retString += ret;
            }
            mActionTextView.setText(" " + retString);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (D) Log.d(TAG, "onRequestPermissionsResult");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (requestCode) {
                case PermissionHelper.REQ_PERMISSION_CODE:
                    if (permissions != null) {
                        boolean hasResult = false;
                        for (String p : permissions) {
                            if (p.equals(PermissionHelper.mLocationPerms[0])) {
                                hasResult = true;
                                break;
                            }
                        }
                        if (hasResult) {
                            if (grantResults != null && grantResults.length != 0 &&
                                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                if (mReader.BT_StartScan())
                                    mProgressBar.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                    break;
            }
        }
    }

    private OnItemClickListener listListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // TODO Auto-generated method stub
            ListItem li = (ListItem) mAdapter.getItem(arg2);
            int result = -100;
            if (mReader.BT_GetConnectState() != SDConsts.BTConnectState.CONNECTED) {
                result = mReader.BT_Connect(li.mDt);
                mActionTextView.setText(" " + "Connect result = " + result);
            }
            else {
                result = mReader.BT_Disconnect();
                mActionTextView.setText(" " + "Disconnect result = " + result);
            }
            if (D) Log.d(TAG, "Click Result = " + result);
        }
    };

    private static class ConnectivityHandler extends Handler {
        private final WeakReference<BTConnectivityActivity> mExecutor;
        public ConnectivityHandler(BTConnectivityActivity f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            BTConnectivityActivity executor = mExecutor.get();
            if (executor != null) {
                executor.handleMessage(msg);
            }
        }
    }

    public void handleMessage(Message m) {
        if (D) Log.d(TAG, "mConnectivityHandler");
        if (true) Log.d(TAG, "command = " + m.arg1 + " result = " + m.arg2 + " obj = data");
        String receivedData = "";
        switch (m.what) {
            case SDConsts.Msg.SDMsg:
                if (m.arg1 == SDConsts.SDCmdMsg.SLED_HOTSWAP_STATE_CHANGED) {
                    if (m.arg2 == SDConsts.SDHotswapState.HOTSWAP_STATE)
                        Toast.makeText(mContext, "HOTSWAP STATE CHANGED = HOTSWAP_STATE", Toast.LENGTH_SHORT).show();
                    else if (m.arg2 == SDConsts.SDHotswapState.NORMAL_STATE)
                        Toast.makeText(mContext, "HOTSWAP STATE CHANGED = NORMAL_STATE", Toast.LENGTH_SHORT).show();

                }
                break;
            case SDConsts.Msg.BTMsg:
                if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_DEVICE_FOUND) {
                    receivedData = "SLED_BT_DEVICE_FOUND";
                    if (m.obj != null  && m.obj instanceof Bundle) {
                        Bundle b = (Bundle)m.obj;
                        String name = b.getString(SDConsts.BT_BUNDLE_NAME_KEY);
                        String addr = b.getString(SDConsts.BT_BUNDLE_ADDR_KEY);
                        int bondState = b.getInt(SDConsts.BT_BUNDLE_BOND_STATE_KEY);
                        if (D) Log.d(TAG, "SLED_BT_DEVICE_FOUND " + name + " " + addr + " " + bondState);
                        mAdapter.addItem(-1, name, addr, false, false);
                    }
                }
                else if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_BOND_STATE_CHAGNED) {
                    receivedData = "SLED_BT_BOND_STATE_CHAGNED";
                    if (m.obj != null  && m.obj instanceof Bundle) {
                        Bundle b = (Bundle)m.obj;
                        String name = b.getString(SDConsts.BT_BUNDLE_NAME_KEY);
                        String addr = b.getString(SDConsts.BT_BUNDLE_ADDR_KEY);
                        int bondState = b.getInt(SDConsts.BT_BUNDLE_BOND_STATE_KEY);
                        int newBondState = b.getInt(SDConsts.BT_BUNDLE_BOND_NEW_STATE_KEY);
                        int prevBondState = b.getInt(SDConsts.BT_BUNDLE_BOND_PREV_STATE_KEY);
                        if (D) Log.d(TAG, "SLED_BT_BOND_STATE_CHAGNED " + name + " " + addr + " " + bondState + " " + newBondState + " " + prevBondState);
                    }
                }
                else if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_PAIRING_REQUEST) {
                    receivedData = "SLED_BT_PAIRING_REQUEST";

                }
                else if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_DISCOVERY_STARTED) {
                    receivedData = "SLED_BT_DISCOVERY_STARTED";
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                else if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_DISCOVERY_FINISHED) {
                    receivedData = "SLED_BT_DISCOVERY_FINISHED";
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
                else if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_STATE_CHANGED) {
                    receivedData = "SLED_BT_STATE_CHANGED";
                    if (m.obj != null  && m.obj instanceof Bundle) {
                        Bundle b = (Bundle)m.obj;
                        int newBondState = b.getInt(SDConsts.BT_BUNDLE_BOND_NEW_STATE_KEY);
                        int prevBondState = b.getInt(SDConsts.BT_BUNDLE_BOND_PREV_STATE_KEY);
                        if (D) Log.d(TAG, "SLED_BT_STATE_CHANGED " + newBondState + " " + prevBondState);
                    }
                    if (mReader.BT_IsEnabled())
                        addPairedDevices();
                    if (D) Log.d(TAG, "BT State changed = " + m.arg2);
                }
                else if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_STATE_CHANGED) {
                    receivedData = "SLED_BT_CONNECTION_STATE_CHANGED";
                    if (D) Log.d(TAG, "SLED_BT_CONNECTION_STATE_CHANGED = " + m.arg2);

                }
                else if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_ESTABLISHED) {
                    receivedData = "SLED_BT_CONNECTION_ESTABLISHED";
                    updateConnectedInfo(mReader.BT_GetConnectedDeviceName() + "\n" + mReader.BT_GetConnectedDeviceAddr());
                    addPairedDevices();
                }
                else if  (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_DISCONNECTED) {
                    receivedData = "SLED_BT_DISCONNECTED";
                    updateConnectedInfo("");
                }
                else if  (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_LOST) {
                    receivedData = "SLED_BT_CONNECTION_LOST";
                    updateConnectedInfo("");
                }
                else if  (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_ACL_CONNECTED) {
                    receivedData = "SLED_BT_ACL_CONNECTED";
                    if (m.obj != null  && m.obj instanceof Bundle) {
                        Bundle b = (Bundle)m.obj;
                        String name = b.getString(SDConsts.BT_BUNDLE_NAME_KEY);
                        String addr = b.getString(SDConsts.BT_BUNDLE_ADDR_KEY);
                        int bondState = b.getInt(SDConsts.BT_BUNDLE_BOND_STATE_KEY);
                        if (D) Log.d(TAG, "SLED_BT_ACL_CONNECTED " + name + " " + addr + " " + bondState);
                    }
                }
                else if  (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_ACL_DISCONNECT_REQUESTED) {
                    receivedData = "SLED_BT_ACL_DISCONNECT_REQUESTED";
                    updateConnectedInfo("");
                }
                else if  (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_ACL_DISCONNECTED) {
                    receivedData = "SLED_BT_ACL_DISCONNECTED";
                    if (m.obj != null  && m.obj instanceof Bundle) {
                        Bundle b = (Bundle)m.obj;
                        String name = b.getString(SDConsts.BT_BUNDLE_NAME_KEY);
                        String addr = b.getString(SDConsts.BT_BUNDLE_ADDR_KEY);
                        int bondState = b.getInt(SDConsts.BT_BUNDLE_BOND_STATE_KEY);
                        if (D) Log.d(TAG, "SLED_BT_ACL_DISCONNECTED " + name + " " + addr + " " + bondState);
                    }
                    updateConnectedInfo("");
                }
                else if  (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_ADAPTER_CONNECTION_STATE_CHANGED) {
                    receivedData = "SLED_BT_ADAPTER_CONNECTION_STATE_CHANGED";
                    if (m.obj != null  && m.obj instanceof Bundle) {
                        Bundle b = (Bundle)m.obj;
                        String name = b.getString(SDConsts.BT_BUNDLE_NAME_KEY);
                        String addr = b.getString(SDConsts.BT_BUNDLE_ADDR_KEY);
                        int bondState = b.getInt(SDConsts.BT_BUNDLE_BOND_STATE_KEY);
                        int newConState = b.getInt(SDConsts.BT_BUNDLE_CON_NEW_STATE_KEY);
                        int prevConState = b.getInt(SDConsts.BT_BUNDLE_CON_PREV_STATE_KEY);
                        if (D) Log.d(TAG, "SLED_BT_CONNECTION_STATE_CHANGED " + name + " " + addr + " " + bondState + " " + newConState + " " +  prevConState);
                    }
                }
                break;
        }
        mMessageTextView.setText(receivedData);
    }

    private void addPairedDevices() {
        if (mAdapter != null && mReader != null) {
            mAdapter.removeAllItem();
            Set<BluetoothDevice> pairedDevices = mReader.BT_GetPairedDevices();
            if (pairedDevices != null && pairedDevices.size() > 0) {
                for (BluetoothDevice d : pairedDevices)
                    mAdapter.addItem(-1, d.getName() + "\n" + "[paired device]", d.getAddress(), false, false);
            }
        }
    }

    private void updateConnectedInfo(String info) {
        if (mReader != null && mConnectedDeviceTextView != null) {
            mConnectedDeviceTextView.setText(info);
            mConnectedDeviceTextView.setTextColor(Color.BLUE);
            if (info != null && info != "")
                mDisconnectBt.setVisibility(View.VISIBLE);
            else
                mDisconnectBt.setVisibility(View.INVISIBLE);
        }
    }

}