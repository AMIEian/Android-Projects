package nitin.sangale.remote;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements OnTouchListener{

	ImageButton remote_switch[] = new ImageButton[12]; 
	ToggleButton latch_switch[] = new ToggleButton[4];
	Button con;
	byte[] data = new byte[1];
	byte txCode = 0x00;
	byte matrixKey = 0x00;
	byte toggleSwitches = 0x00;
	boolean latch [] = new boolean[4] ;
	
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
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLGame:");
		
		//---------------------------BLUETOOTH INIT CODE-------------------------------//
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter == null) {
		   finishDialogNoBluetooth();
		   return;
		}
				
		mSerialService = new BluetoothSerialService(this, mHandlerBT);
		//---------------------------END OF BLUETOOTH INIT CODE-------------------------------//

		con = (Button)findViewById(R.id.Button1);
		
		remote_switch[0] = (ImageButton)findViewById(R.id.sw1);
		remote_switch[1] = (ImageButton)findViewById(R.id.sw2);
		remote_switch[2] = (ImageButton)findViewById(R.id.sw3);
		remote_switch[3] = (ImageButton)findViewById(R.id.sw4);
		remote_switch[4] = (ImageButton)findViewById(R.id.sw5);
		remote_switch[5] = (ImageButton)findViewById(R.id.sw6);
		remote_switch[6] = (ImageButton)findViewById(R.id.sw7);
		remote_switch[7] = (ImageButton)findViewById(R.id.sw8);
		remote_switch[8] = (ImageButton)findViewById(R.id.sw9);
		remote_switch[9] = (ImageButton)findViewById(R.id.sw10);
		remote_switch[10] = (ImageButton)findViewById(R.id.sw11);
		remote_switch[11] = (ImageButton)findViewById(R.id.sw12);
		
		remote_switch[0].setOnTouchListener(this);
		remote_switch[1].setOnTouchListener(this);
		remote_switch[2].setOnTouchListener(this);
		remote_switch[3].setOnTouchListener(this);
		remote_switch[4].setOnTouchListener(this);
		remote_switch[5].setOnTouchListener(this);
		remote_switch[6].setOnTouchListener(this);
		remote_switch[7].setOnTouchListener(this);
		remote_switch[8].setOnTouchListener(this);
		remote_switch[9].setOnTouchListener(this);
		remote_switch[10].setOnTouchListener(this);
		remote_switch[11].setOnTouchListener(this);
		
		latch_switch[0] = (ToggleButton)findViewById(R.id.sw13);
		latch_switch[1] = (ToggleButton)findViewById(R.id.sw14);
		latch_switch[2] = (ToggleButton)findViewById(R.id.sw15);
		latch_switch[3] = (ToggleButton)findViewById(R.id.sw16);	
		
		hideKeys();
		
		Thread thread = new Thread() {
		    @Override
		    public void run() {
		        try {
		            while(true) {
		                sleep(1000);
		                data[0] = txCode;
						mSerialService.write(data); 
		            }
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
		    }
		};

		thread.start();
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
                	con.setEnabled(false);
                	con.setVisibility(View.GONE);
                	showKeys();
                	break;
            	case MESSAGE_TOAST:
                	Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                	hideKeys();
                	con.setVisibility(View.VISIBLE);
                	con.setEnabled(true);
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
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		v.onTouchEvent(event);
		if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				int redButtonId = getResources().getIdentifier("nitin.sangale.remote:drawable/red", null, null);
				ImageButton btn = (ImageButton)findViewById(v.getId());
				btn.setImageResource(redButtonId);
				matrixKey = 0x00;
				toggleSwitches = 0x00;
				
				switch(v.getId())
				{
					case R.id.sw1:
						matrixKey = (byte) (matrixKey | 0x02);
						break;
					case R.id.sw2:
						matrixKey = (byte) (matrixKey | 0x03);
						break;
					case R.id.sw3:
						matrixKey = (byte) (matrixKey | 0x04);
						break;
					case R.id.sw4:
						matrixKey = (byte) (matrixKey | 0x05);
						break;
					case R.id.sw5:
						matrixKey = (byte) (matrixKey | 0x06);
						break;
					case R.id.sw6:
						matrixKey = (byte) (matrixKey | 0x07);
						break;
					case R.id.sw7:
						matrixKey = (byte) (matrixKey | 0x08);
						break;
					case R.id.sw8:
						matrixKey = (byte) (matrixKey | 0x09);
						break;
					case R.id.sw9:
						matrixKey = (byte) (matrixKey | 0x0A);
						break;
					case R.id.sw10:
						matrixKey = (byte) (matrixKey | 0x0B);
						break;
					case R.id.sw11:
						matrixKey = (byte) (matrixKey | 0x0C);
						break;
					case R.id.sw12:
						matrixKey = (byte) (matrixKey | 0x0D);
						break;
				}
				
				
				if(latch[0])
					toggleSwitches = (byte) (toggleSwitches | 0x01);
				  else
				    toggleSwitches = (byte) (toggleSwitches & 0x0E);
				if(latch[1])
					toggleSwitches = (byte) (toggleSwitches | 0x02);
				  else
				    toggleSwitches = (byte) (toggleSwitches & 0x0D);
				if(latch[2])
					toggleSwitches = (byte) (toggleSwitches | 0x04);
				  else
				    toggleSwitches = (byte) (toggleSwitches & 0x0B);
				if(latch[3])
					toggleSwitches = (byte) (toggleSwitches | 0x08);
				  else
				    toggleSwitches = (byte) (toggleSwitches & 0x07);
				
				txCode  = (byte) ((matrixKey << 4) | toggleSwitches);
				data[0] = txCode;
				mSerialService.write(data); 
				/*
				int hexToInt = new Integer(txCode).intValue();
				String s1 = String.format("%8s", Integer.toBinaryString(hexToInt & 0xFF)).replace(' ', '0');
				con.setText(s1);
				*/
			}
		if(event.getAction() == MotionEvent.ACTION_UP)
			{
				int redButtonId = getResources().getIdentifier("nitin.sangale.remote:drawable/yellow", null, null);
				ImageButton btn = (ImageButton)findViewById(v.getId());
				btn.setImageResource(redButtonId);
				
				matrixKey = 0x00;
				toggleSwitches = 0x00;
				
				if(latch[0])
					toggleSwitches = (byte) (toggleSwitches | 0x01);
				  else
				    toggleSwitches = (byte) (toggleSwitches & 0x0E);
				if(latch[1])
					toggleSwitches = (byte) (toggleSwitches | 0x02);
				  else
				    toggleSwitches = (byte) (toggleSwitches & 0x0D);
				if(latch[2])
					toggleSwitches = (byte) (toggleSwitches | 0x04);
				  else
				    toggleSwitches = (byte) (toggleSwitches & 0x0B);
				if(latch[3])
					toggleSwitches = (byte) (toggleSwitches | 0x08);
				  else
				    toggleSwitches = (byte) (toggleSwitches & 0x07);
				
				txCode  = (byte) ((matrixKey << 4) | toggleSwitches);
				data[0] = txCode;
				mSerialService.write(data); 
				/*
				int hexToInt = new Integer(txCode).intValue();
				String s1 = String.format("%8s", Integer.toBinaryString(hexToInt & 0xFF)).replace(' ', '0');
				con.setText(s1);
				*/
			}
		return false;
	}
	
	public void onToggleClicked(View v) {
		
		int pinkButtonId = getResources().getIdentifier("nitin.sangale.remote:drawable/pink", null, null);
		int greenButtonId = getResources().getIdentifier("nitin.sangale.remote:drawable/green", null, null);
		int redButtonId = getResources().getIdentifier("nitin.sangale.remote:drawable/red", null, null);
		int orangeButtonId = getResources().getIdentifier("nitin.sangale.remote:drawable/orange", null, null);
		ToggleButton btn = (ToggleButton)findViewById(v.getId());
		
		matrixKey = 0x00;
		toggleSwitches = 0x00;
		
	    switch(v.getId())
	    {
	    	case R.id.sw13:
	    		latch[0] = ((ToggleButton)v).isChecked();
	    		if(latch[0])
	    			btn.setBackgroundResource(greenButtonId);
	    		else
	    			btn.setBackgroundResource(pinkButtonId);
	    		break;
	    	case R.id.sw14:
	    		latch[1] = ((ToggleButton)v).isChecked();
	    		if(latch[1])
	    			btn.setBackgroundResource(greenButtonId);
	    		else
	    			btn.setBackgroundResource(pinkButtonId);
	    		break;
	    	case R.id.sw15:
	    		latch[2] = ((ToggleButton)v).isChecked();
	    		if(latch[2])
	    			btn.setBackgroundResource(greenButtonId);
	    		else
	    			btn.setBackgroundResource(pinkButtonId);
	    		break;
	    	case R.id.sw16:
	    		latch[3] = ((ToggleButton)v).isChecked();
	    		if(latch[3])
	    			btn.setBackgroundResource(redButtonId);
	    		else
	    			btn.setBackgroundResource(orangeButtonId);
	    		break;	
	    }
	    
		if(latch[0])
			toggleSwitches = (byte) (toggleSwitches | 0x01);
		else
		    toggleSwitches = (byte) (toggleSwitches & 0x0E);
		if(latch[1])
			toggleSwitches = (byte) (toggleSwitches | 0x02);
		  else
		    toggleSwitches = (byte) (toggleSwitches & 0x0D);
		if(latch[2])
			toggleSwitches = (byte) (toggleSwitches | 0x04);
		  else
		    toggleSwitches = (byte) (toggleSwitches & 0x0B);
		if(latch[3])
			toggleSwitches = (byte) (toggleSwitches | 0x08);
		  else
		    toggleSwitches = (byte) (toggleSwitches & 0x07);
		
		txCode  = (byte) ((matrixKey << 4) | toggleSwitches);
		data[0] = txCode;
		mSerialService.write(data); 
		/*
		int hexToInt = new Integer(txCode).intValue();
		String s1 = String.format("%8s", Integer.toBinaryString(hexToInt & 0xFF)).replace(' ', '0');
		con.setText(s1);
		*/
	}

	private void hideKeys()
	{
		for(int i = 0; i <= 11; i++)
		{
			remote_switch[i].setEnabled(false);
			remote_switch[i].setVisibility(View.INVISIBLE);
		}
		for(int i = 0; i <= 3; i++)
		{
			latch_switch[i].setEnabled(false);
			latch_switch[i].setVisibility(View.INVISIBLE);
		}
	}
	
	private void showKeys()
	{
		for(int i = 0; i <= 11; i++)
		{
			remote_switch[i].setVisibility(View.VISIBLE);
			remote_switch[i].setEnabled(true);
		}
		for(int i = 0; i <= 3; i++)
		{
			latch_switch[i].setVisibility(View.VISIBLE);
			latch_switch[i].setEnabled(true);			
		}
	}
}
