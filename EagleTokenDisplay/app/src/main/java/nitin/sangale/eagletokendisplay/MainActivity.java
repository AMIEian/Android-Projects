package nitin.sangale.eagletokendisplay;

import java.nio.charset.Charset;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	int token_number = 0;
	Button one, two, three, four, five, six, seven, eight, nine, zero, next, prev, sendToken, connect;
	TextView tokenText;
	Boolean tokenSent = false;

	WakeLock wakeLock;
	
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

		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLGame");
		
		//---------------------------BLUETOOTH INIT CODE-------------------------------//
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter == null) {
		   finishDialogNoBluetooth();
		   return;
		}
				
		mSerialService = new BluetoothSerialService(this, mHandlerBT);
		//---------------------------END OF BLUETOOTH INIT CODE-------------------------------//
		
		zero = (Button)findViewById(R.id.B0);
		zero.setOnClickListener(this);
		
		one = (Button)findViewById(R.id.B1);
		one.setOnClickListener(this);
		
		two = (Button)findViewById(R.id.B2);
		two.setOnClickListener(this);
		
		three = (Button)findViewById(R.id.B3);
		three.setOnClickListener(this);
		
		four = (Button)findViewById(R.id.B4);
		four.setOnClickListener(this);
		
		five = (Button)findViewById(R.id.B5);
		five.setOnClickListener(this);
		
		six = (Button)findViewById(R.id.B6);
		six.setOnClickListener(this);
		
		seven = (Button)findViewById(R.id.B7);
		seven.setOnClickListener(this);
		
		eight = (Button)findViewById(R.id.B8);
		eight.setOnClickListener(this);
		
		nine = (Button)findViewById(R.id.B9);
		nine.setOnClickListener(this);
		
		next = (Button)findViewById(R.id.BNext);
		next.setOnClickListener(this);
		
		prev = (Button)findViewById(R.id.BPrev);
		prev.setOnClickListener(this);
		
		sendToken = (Button)findViewById(R.id.BSendToken);
		sendToken.setOnClickListener(this);
		
		connect = (Button)findViewById(R.id.BConnect);
		connect.setOnClickListener(this);
		
		tokenText = (TextView)findViewById(R.id.TokenNo);
		tokenText.setText(String.format("%03d", token_number));

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
                	connect.setEnabled(false);
                    connect.setVisibility(View.GONE);
					showKeys();
                	break;
            	case MESSAGE_TOAST:
                	Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
					hideKeys();
                	connect.setVisibility(View.VISIBLE);
                    connect.setEnabled(true);
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) 
			{
				case R.id.B1:
					if(tokenSent == true)
					{
						token_number = 0;
						tokenSent = false;
					}
					if(token_number == 0 || token_number > 99)
						token_number = 1;
					else
						token_number = token_number * 10 + 1;
					tokenText.setText(String.format("%03d", token_number));
					break;
				case R.id.B2:
					if(tokenSent == true)
					{
						token_number = 0;
						tokenSent = false;
					}
					if(token_number == 0 || token_number > 99)
						token_number = 2;
					else
						token_number = token_number * 10 + 2;
					tokenText.setText(String.format("%03d", token_number));
					break;
				case R.id.B3:
					if(tokenSent == true)
					{
						token_number = 0;
						tokenSent = false;
					}
					if(token_number == 0 || token_number > 99)
						token_number = 3;
					else
						token_number = token_number * 10 + 3;
					tokenText.setText(String.format("%03d", token_number));
					break;
				case R.id.B4:
					if(tokenSent == true)
					{
						token_number = 0;
						tokenSent = false;
					}
					if(token_number == 0 || token_number > 99)
						token_number = 4;
					else
						token_number = token_number * 10 + 4;
					tokenText.setText(String.format("%03d", token_number));
					break;
				case R.id.B5:
					if(tokenSent == true)
					{
						token_number = 0;
						tokenSent = false;
					}
					if(token_number == 0 || token_number > 99)
						token_number = 5;
					else
						token_number = token_number * 10 + 5;
					tokenText.setText(String.format("%03d", token_number));
					break;
				case R.id.B6:
					if(tokenSent == true)
					{
						token_number = 0;
						tokenSent = false;
					}
					if(token_number == 0 || token_number > 99)
						token_number = 6;
					else
						token_number = token_number * 10 + 6;
					tokenText.setText(String.format("%03d", token_number));
					break;	
				case R.id.B7:
					if(tokenSent == true)
					{
						token_number = 0;
						tokenSent = false;
					}
					if(token_number == 0 || token_number > 99)
						token_number = 7;
					else
						token_number = token_number * 10 + 7;
					tokenText.setText(String.format("%03d", token_number));
					break;	
				case R.id.B8:
					if(tokenSent == true)
					{
						token_number = 0;
						tokenSent = false;
					}
					if(token_number == 0 || token_number > 99)
						token_number = 8;
					else
						token_number = token_number * 10 + 8;
					tokenText.setText(String.format("%03d", token_number));
					break;	
				case R.id.B9:
					if(tokenSent == true)
					{
						token_number = 0;
						tokenSent = false;
					}
					if(token_number == 0 || token_number > 99)
						token_number = 9;
					else
						token_number = token_number * 10 + 9;
					tokenText.setText(String.format("%03d", token_number));
					break;	
				case R.id.B0:
					if(tokenSent == true)
					{
						token_number = 0;
						tokenSent = false;
					}
					if(token_number < 99)
						token_number = token_number * 10;
					else
						token_number = 0;
					tokenText.setText(String.format("%03d", token_number));
					break;	
				case R.id.BNext:
					if(token_number >= 0 && token_number < 999)
						token_number = token_number + 1;
					else
						token_number = 0;
					tokenText.setText(String.format("%03d", token_number));
					break;	
				case R.id.BPrev:
					if(token_number > 0)
						token_number = token_number - 1;
					else
						token_number = 0;
					tokenText.setText(String.format("%03d", token_number));
					break;		
				case R.id.BSendToken:
					writeDataToSerial(tokenText.getText().toString()+"0"+"#");
					tokenSent = true;
					break;
				case R.id.BConnect:
					ConnectToGadget(v);
					break;	
				default:
					// TODO Auto-generated method stub
					break;
		}
	}

	private void hideKeys()
	{
		one.setEnabled(false);
		one.setVisibility(View.INVISIBLE);

		two.setEnabled(false);
		two.setVisibility(View.INVISIBLE);

		three.setEnabled(false);
		three.setVisibility(View.INVISIBLE);

		four.setEnabled(false);
		four.setVisibility(View.INVISIBLE);

		five.setEnabled(false);
		five.setVisibility(View.INVISIBLE);

		six.setEnabled(false);
		six.setVisibility(View.INVISIBLE);

		seven.setEnabled(false);
		seven.setVisibility(View.INVISIBLE);

		eight.setEnabled(false);
		eight.setVisibility(View.INVISIBLE);

		nine.setEnabled(false);
		nine.setVisibility(View.INVISIBLE);

		zero.setEnabled(false);
		zero.setVisibility(View.INVISIBLE);

		next.setEnabled(false);
		next.setVisibility(View.INVISIBLE);

		prev.setEnabled(false);
		prev.setVisibility(View.INVISIBLE);

		sendToken.setEnabled(false);
		sendToken.setVisibility(View.INVISIBLE);

		tokenText.setEnabled(false);
		tokenText.setVisibility(View.INVISIBLE);
	}

	private void showKeys()
	{
		one.setEnabled(true);
		one.setVisibility(View.VISIBLE);

		two.setEnabled(true);
		two.setVisibility(View.VISIBLE);

		three.setEnabled(true);
		three.setVisibility(View.VISIBLE);

		four.setEnabled(true);
		four.setVisibility(View.VISIBLE);

		five.setEnabled(true);
		five.setVisibility(View.VISIBLE);

		six.setEnabled(true);
		six.setVisibility(View.VISIBLE);

		seven.setEnabled(true);
		seven.setVisibility(View.VISIBLE);

		eight.setEnabled(true);
		eight.setVisibility(View.VISIBLE);

		nine.setEnabled(true);
		nine.setVisibility(View.VISIBLE);

		zero.setEnabled(true);
		zero.setVisibility(View.VISIBLE);

		next.setEnabled(true);
		next.setVisibility(View.VISIBLE);

		prev.setEnabled(true);
		prev.setVisibility(View.VISIBLE);

		sendToken.setEnabled(true);
		sendToken.setVisibility(View.VISIBLE);

		tokenText.setEnabled(true);
		tokenText.setVisibility(View.VISIBLE);
	}
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	*/
}
