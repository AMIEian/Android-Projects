package nitin.sangale.videocond2hremote;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.view.View.OnTouchListener;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity implements OnTouchListener, OnClickListener {

    Button connectBtn, powerBtn, oneBtn, twoBtn, threeBtn, fourBtn, fiveBtn, sixBtn, sevenBtn, eightBtn, nineBtn, zeroBtn, volUpBtn, volDownBtn, infoBtn, muteBtn, chPlusBtn, chMinusBtn, upBtn, downBtn, leftBtn, rightBtn, exitBtn, okBtn, backBtn;
    boolean Clicked = false;
    PowerManager.WakeLock wakeLock;
    //---------------------------------------BLUETOOTH REQUIREMENTS ----------------------------------//
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Message types sent from the BluetoothReadService Handler
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    private BluetoothAdapter mBluetoothAdapter = null;
    private static BluetoothSerialService mSerialService = null;
    private boolean mEnablingBT;

    // Name of the connected device
    private String mConnectedDeviceName = null;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    //String to read input data
    public static String RxData = null;
    //---------------------------------------END OF BLUETOOTH REQUIREMENTS ----------------------------------//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectBtn = (Button)findViewById(R.id.BConnect);
        powerBtn = (Button)findViewById(R.id.BPower);
        oneBtn = (Button)findViewById(R.id.B1);
        twoBtn = (Button)findViewById(R.id.B2);
        threeBtn = (Button)findViewById(R.id.B3);
        fourBtn = (Button)findViewById(R.id.B4);
        fiveBtn = (Button)findViewById(R.id.B5);
        sixBtn = (Button)findViewById(R.id.B6);
        sevenBtn = (Button)findViewById(R.id.B7);
        eightBtn = (Button)findViewById(R.id.B8);
        nineBtn = (Button)findViewById(R.id.B9);
        zeroBtn = (Button)findViewById(R.id.B0);
        volUpBtn = (Button)findViewById(R.id.BVolUp);
        volDownBtn = (Button)findViewById(R.id.BVolDn);
        infoBtn = (Button)findViewById(R.id.BInfo);
        muteBtn = (Button)findViewById(R.id.BMute);
        chPlusBtn = (Button)findViewById(R.id.BChUp);
        chMinusBtn = (Button)findViewById(R.id.BChDn);
        upBtn = (Button)findViewById(R.id.BUp);
        downBtn = (Button)findViewById(R.id.BDn);
        leftBtn = (Button)findViewById(R.id.BLeft);
        rightBtn = (Button)findViewById(R.id.BRight);
        exitBtn = (Button)findViewById(R.id.BExit);
        okBtn = (Button)findViewById(R.id.BOk);
        backBtn = (Button)findViewById(R.id.BBack);

        connectBtn.setOnClickListener(this);
        powerBtn.setOnTouchListener(this);
        oneBtn.setOnTouchListener(this);
        twoBtn.setOnTouchListener(this);
        threeBtn.setOnTouchListener(this);
        fourBtn.setOnTouchListener(this);
        fiveBtn.setOnTouchListener(this);
        sixBtn.setOnTouchListener(this);
        sevenBtn.setOnTouchListener(this);
        eightBtn.setOnTouchListener(this);
        nineBtn.setOnTouchListener(this);
        zeroBtn.setOnTouchListener(this);
        volUpBtn.setOnTouchListener(this);
        volDownBtn.setOnTouchListener(this);
        infoBtn.setOnTouchListener(this);
        muteBtn.setOnTouchListener(this);
        chPlusBtn.setOnTouchListener(this);
        chMinusBtn.setOnTouchListener(this);
        upBtn.setOnTouchListener(this);
        downBtn.setOnTouchListener(this);
        leftBtn.setOnTouchListener(this);
        rightBtn.setOnTouchListener(this);
        exitBtn.setOnTouchListener(this);
        okBtn.setOnTouchListener(this);
        backBtn.setOnTouchListener(this);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLGame:");

        //---------------------------BLUETOOTH INIT CODE-------------------------------//
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            finishDialogNoBluetooth();
            return;
        }

        mSerialService = new BluetoothSerialService(this, mHandlerBT);
        //---------------------------END OF BLUETOOTH INIT CODE-------------------------------//

        hideKeys();
    }
    //------------------------------BLUETOOTH FUNCTIONS--------------------------------------//
    @Override
    public void onPause() {
        super.onPause();
        wakeLock.release();
    }

    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (mSerialService != null)
            mSerialService.stop();
    }

    public void onStart() {
        super.onStart();
        mEnablingBT = false;
    }

    public void onResume() {
        super.onResume();
        wakeLock.acquire();
        if (!mEnablingBT) { // If we are turning on the BT we cannot check if it's enable
            if ( (mBluetoothAdapter != null)  && (!mBluetoothAdapter.isEnabled()) ) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.alert_dialog_turn_on_bt)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.alert_dialog_warning_title)
                        .setCancelable( false )
                        .setPositiveButton(R.string.alert_dialog_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mEnablingBT = true;
                                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                            }
                        })
                        .setNegativeButton(R.string.alert_dialog_no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finishDialogNoBluetooth();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

            if (mSerialService != null) {
                // Only if the state is STATE_NONE, do we know that we haven't started already
                if (mSerialService.getState() == BluetoothSerialService.STATE_NONE) {
                    // Start the Bluetooth chat services
                    mSerialService.start();
                }
            }
        }
        //new repeatSend().start();
    }

    public int getConnectionState() {
        return mSerialService.getState();
    }

    public void finishDialogNoBluetooth() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_dialog_no_bt)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.app_name)
                .setCancelable( false )
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_CONNECT_DEVICE:

                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mSerialService.connect(device);
                }
                break;

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode != Activity.RESULT_OK) {

                    finishDialogNoBluetooth();
                }
        }
    }

    // The Handler that gets information back from the BluetoothService
    private final Handler mHandlerBT = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_connected_to) + " "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    connectBtn.setEnabled(false);
                    connectBtn.setVisibility(View.INVISIBLE);
                    showKeys();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    hideKeys();
                    connectBtn.setVisibility(View.VISIBLE);
                    connectBtn.setEnabled(true);
                    break;
                default:
                    break;
            }
        }
    };

    private void writeDataToSerial(String strWrite) {
        byte[] buffer = strWrite.getBytes(Charset.forName("UTF-8"));
        mSerialService.write(buffer);
    }

    private String readDataFromSerial() {
        String temp;
        temp = RxData;
        RxData = null;
        return temp;
    }

    public void ConnectToGadget(View view) {
        // TODO Auto-generated method stub
        if (getConnectionState() == BluetoothSerialService.STATE_NONE) {
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
            MainActivity.this.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        }
        else
        if (getConnectionState() == BluetoothSerialService.STATE_CONNECTED) {
            mSerialService.stop();
            mSerialService.start();
        }
    }
    //------------------------------ END OF BLUETOOTH FUNCTIONS--------------------------------------//

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        view.onTouchEvent(motionEvent);
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            if(!Clicked){
                switch(view.getId()){
                    case R.id.BPower:
                        writeDataToSerial("E");
                        Clicked = true;
                        break;
                    case R.id.B1:
                        writeDataToSerial("1");
                        Clicked = true;
                        break;
                    case R.id.B2:
                        writeDataToSerial("2");
                        Clicked = true;
                        break;
                    case R.id.B3:
                        writeDataToSerial("3");
                        Clicked = true;
                        break;
                    case R.id.B4:
                        writeDataToSerial("4");
                        Clicked = true;
                        break;
                    case R.id.B5:
                        writeDataToSerial("5");
                        Clicked = true;
                        break;
                    case R.id.B6:
                        writeDataToSerial("6");
                        Clicked = true;
                        break;
                    case R.id.B7:
                        writeDataToSerial("7");
                        Clicked = true;
                        break;
                    case R.id.B8:
                        writeDataToSerial("8");
                        Clicked = true;
                        break;
                    case R.id.B9:
                        writeDataToSerial("9");
                        Clicked = true;
                        break;
                    case R.id.B0:
                        writeDataToSerial("0");
                        Clicked = true;
                        break;
                    case R.id.BVolUp:
                        writeDataToSerial("A");
                        Clicked = true;
                        break;
                    case R.id.BVolDn:
                        writeDataToSerial("B");
                        Clicked = true;
                        break;
                    case R.id.BInfo:
                        writeDataToSerial("I");
                        Clicked = true;
                        break;
                    case R.id.BMute:
                        writeDataToSerial("J");
                        Clicked = true;
                        break;
                    case R.id.BChUp:
                        writeDataToSerial("C");
                        Clicked = true;
                        break;
                    case R.id.BChDn:
                        writeDataToSerial("D");
                        Clicked = true;
                        break;
                    case R.id.BUp:
                        writeDataToSerial("K");
                        Clicked = true;
                        break;
                    case R.id.BDn:
                        writeDataToSerial("L");
                        Clicked = true;
                        break;
                    case R.id.BLeft:
                        writeDataToSerial("M");
                        Clicked = true;
                        break;
                    case R.id.BRight:
                        writeDataToSerial("N");
                        Clicked = true;
                        break;
                    case R.id.BExit:
                        writeDataToSerial("G");
                        Clicked = true;
                        break;
                    case R.id.BOk:
                        writeDataToSerial("H");
                        Clicked = true;
                        break;
                    case R.id.BBack:
                        writeDataToSerial("F");
                        Clicked = true;
                        break;
                }
            }
            return true;
        }
        if(motionEvent.getAction() == MotionEvent.ACTION_UP)
        {
            Clicked = false;
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        ConnectToGadget(view);
    }

    class repeatSend extends Thread{
        Handler handler = new Handler(Looper.getMainLooper());
        public void run(){
            while (true)
            {
                try
                {
                    Thread.sleep(100);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
                if(Clicked)
                {
                    handler.post(new Runnable(){
                                     public void run() {
                                         writeDataToSerial("O");
                                     }
                                 }
                    );
                }
            }
        }
    }

    void hideKeys()
    {
        powerBtn.setEnabled(false);
        powerBtn.setVisibility(View.INVISIBLE);

        oneBtn.setEnabled(false);
        oneBtn.setVisibility(View.INVISIBLE);

        twoBtn.setEnabled(false);
        twoBtn.setVisibility(View.INVISIBLE);

        threeBtn.setEnabled(false);
        threeBtn.setVisibility(View.INVISIBLE);

        fourBtn.setEnabled(false);
        fourBtn.setVisibility(View.INVISIBLE);

        fiveBtn.setEnabled(false);
        fiveBtn.setVisibility(View.INVISIBLE);

        sixBtn.setEnabled(false);
        sixBtn.setVisibility(View.INVISIBLE);

        sevenBtn.setEnabled(false);
        sevenBtn.setVisibility(View.INVISIBLE);

        eightBtn.setEnabled(false);
        eightBtn.setVisibility(View.INVISIBLE);

        nineBtn.setEnabled(false);
        nineBtn.setVisibility(View.INVISIBLE);

        zeroBtn.setEnabled(false);
        zeroBtn.setVisibility(View.INVISIBLE);

        volUpBtn.setEnabled(false);
        volUpBtn.setVisibility(View.INVISIBLE);

        volDownBtn.setEnabled(false);
        volDownBtn.setVisibility(View.INVISIBLE);

        infoBtn.setEnabled(false);
        infoBtn.setVisibility(View.INVISIBLE);

        muteBtn.setEnabled(false);
        muteBtn.setVisibility(View.INVISIBLE);

        chPlusBtn.setEnabled(false);
        chPlusBtn.setVisibility(View.INVISIBLE);

        chMinusBtn.setEnabled(false);
        chMinusBtn.setVisibility(View.INVISIBLE);

        upBtn.setEnabled(false);
        upBtn.setVisibility(View.INVISIBLE);

        downBtn.setEnabled(false);
        downBtn.setVisibility(View.INVISIBLE);

        leftBtn.setEnabled(false);
        leftBtn.setVisibility(View.INVISIBLE);

        rightBtn.setEnabled(false);
        rightBtn.setVisibility(View.INVISIBLE);

        exitBtn.setEnabled(false);
        exitBtn.setVisibility(View.INVISIBLE);

        okBtn.setEnabled(false);
        okBtn.setVisibility(View.INVISIBLE);

        backBtn.setEnabled(false);
        backBtn.setVisibility(View.INVISIBLE);
    }

    void showKeys()
    {
        powerBtn.setEnabled(true);
        powerBtn.setVisibility(View.VISIBLE);

        oneBtn.setEnabled(true);
        oneBtn.setVisibility(View.VISIBLE);

        twoBtn.setEnabled(true);
        twoBtn.setVisibility(View.VISIBLE);

        threeBtn.setEnabled(true);
        threeBtn.setVisibility(View.VISIBLE);

        fourBtn.setEnabled(true);
        fourBtn.setVisibility(View.VISIBLE);

        fiveBtn.setEnabled(true);
        fiveBtn.setVisibility(View.VISIBLE);

        sixBtn.setEnabled(true);
        sixBtn.setVisibility(View.VISIBLE);

        sevenBtn.setEnabled(true);
        sevenBtn.setVisibility(View.VISIBLE);

        eightBtn.setEnabled(true);
        eightBtn.setVisibility(View.VISIBLE);

        nineBtn.setEnabled(true);
        nineBtn.setVisibility(View.VISIBLE);

        zeroBtn.setEnabled(true);
        zeroBtn.setVisibility(View.VISIBLE);

        volUpBtn.setEnabled(true);
        volUpBtn.setVisibility(View.VISIBLE);

        volDownBtn.setEnabled(true);
        volDownBtn.setVisibility(View.VISIBLE);

        infoBtn.setEnabled(true);
        infoBtn.setVisibility(View.VISIBLE);

        muteBtn.setEnabled(true);
        muteBtn.setVisibility(View.VISIBLE);

        chPlusBtn.setEnabled(true);
        chPlusBtn.setVisibility(View.VISIBLE);

        chMinusBtn.setEnabled(true);
        chMinusBtn.setVisibility(View.VISIBLE);

        upBtn.setEnabled(true);
        upBtn.setVisibility(View.VISIBLE);

        downBtn.setEnabled(true);
        downBtn.setVisibility(View.VISIBLE);

        leftBtn.setEnabled(true);
        leftBtn.setVisibility(View.VISIBLE);

        rightBtn.setEnabled(true);
        rightBtn.setVisibility(View.VISIBLE);

        exitBtn.setEnabled(true);
        exitBtn.setVisibility(View.VISIBLE);

        okBtn.setEnabled(true);
        okBtn.setVisibility(View.VISIBLE);

        backBtn.setEnabled(true);
        backBtn.setVisibility(View.VISIBLE);
    }
}
