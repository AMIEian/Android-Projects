package nitin.sangale.eagleclock;

import java.nio.charset.Charset;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import java.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity {
	Button connectClock, setTime;

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
	
	TimePicker alarmTime;
	CheckBox repeat;
	TextView test;
	
	boolean twenty_four_hrs_format = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//---------------------------BLUETOOTH INIT CODE-------------------------------//
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter == null) {
		   finishDialogNoBluetooth();
		   return;
		}
				
		mSerialService = new BluetoothSerialService(this, mHandlerBT);
		//---------------------------END OF BLUETOOTH INIT CODE-------------------------------//
		
		alarmTime = (TimePicker)findViewById(R.id.Alarm);
		repeat = (CheckBox)findViewById(R.id.AlarmRepeat);
		test = (TextView)findViewById(R.id.textView1);

		connectClock = (Button)findViewById(R.id.button2);
		setTime = (Button)findViewById(R.id.button1);

		setTime.setEnabled(false);
		setTime.setVisibility(View.INVISIBLE);
	}
	
	//------------------------------BLUETOOTH FUNCTIONS--------------------------------------//
	@Override
    public void onPause() {
        super.onPause();        
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
                	connectClock.setEnabled(false);
                	connectClock.setVisibility(View.INVISIBLE);
                	setTime.setEnabled(true);
                	setTime.setVisibility(View.VISIBLE);
                	break;
            	case MESSAGE_TOAST:
                	Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
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
	
	public void SetClock(View view)
	{
		/*String timeString = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        timeString = new StringBuilder(dateFormat.format(cal.getTime()).replaceAll("/", "").replaceAll(":", "").replaceAll(" ", "")).reverse().toString();
           
        writeDataToSerial(timeString);*/
		
		String timeString = "";
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		month = month + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hrs = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		int sec = calendar.get(Calendar.SECOND);
		timeString = "U" + "Y" + Integer.toString(year) + "M" + Integer.toString(month) + "D" + Integer.toString(day) + "H" + Integer.toString(hrs) + "N" + Integer.toString(min) + "S" + Integer.toString(sec) + "#";
		writeDataToSerial(timeString);
		Toast.makeText(this,"Clock Time Updated...!",Toast.LENGTH_SHORT).show();
	}
	
	public void ToggleFormat(View view)
	{
		if(twenty_four_hrs_format)
			{
				writeDataToSerial("T");
				twenty_four_hrs_format = false;
			}
		else
			{
				writeDataToSerial("F");
				twenty_four_hrs_format = true;
			}
		Toast.makeText(this,"Time Format Changed...!",Toast.LENGTH_SHORT).show();
	}
	
	public void SetAlarm(View view)
	{
		String alarm_Hrs = new StringBuilder(String.format("%02d", alarmTime.getCurrentHour())).toString();
		String alarm_Mins = new StringBuilder(String.format("%02d", alarmTime.getCurrentMinute())).toString();
		
		String alarmString = "A" + "H" + alarm_Hrs + "M" + alarm_Mins + "R";
		
		if(repeat.isChecked())
			alarmString = alarmString + "1";
		else
			alarmString = alarmString + "0";
		
		alarmString = alarmString + "#";
		
		//test.setText(alarmString);
		writeDataToSerial(alarmString);
		
		Toast.makeText(this,"Alarm Set...!",Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		/*
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}
}
