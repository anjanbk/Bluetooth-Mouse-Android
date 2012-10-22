package com.example.labrat;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity implements SensorEventListener{
	private BluetoothAdapter mBluetoothAdapter;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private BluetoothCommandService mCommandService;
	private float vx, vy, velocityMagnitude, ux, uy, t, t2, t1;
	private float a1, a2;
	private State state;
	private String mConnectedDeviceName = null;
	
	public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
	
	private String[] logStates = {"Idle", "Acceleration", "Constant Velocity"};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ux = uy = vx = vy = 0;
        t = 0;
        a1 = 0;
        state = State.IDLE;
        
        // Initialize Phone Sensors 
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        
        // Initialize Bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }
    
    
    @Override
    protected void onStart() {
    	super.onStart();
    	
    	if (!mBluetoothAdapter.isEnabled()) {
    		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    		startActivity(enableIntent);
    	} else {
    		if (mCommandService != null) {
    			setupCommand();
    		}
    	}
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	if (mCommandService != null) {
    		if (mCommandService.getState() == BluetoothCommandService.STATE_NONE) {
    			mCommandService.start();
    		}
    	}
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	if (mCommandService != null)
    		mCommandService.stop();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

	public void onSensorChanged(SensorEvent event) {
		//Log.d("Current State", logStates[state.ordinal()]);
		a2 = event.values[0] + event.values[1];
		
		//if (Math.abs(a2) - Math.abs(a1) > 0.3)
			//Log.d("Change in acceleration", "" + (a2-a1));
		
		a1 = a2;
		
		vx = ux + event.values[0];
		vy = uy + event.values[1];
		
		ux = vx;
		uy = vy;
		
		float temp = KinematicsGovernor.vectorMagnitude(new float[]{vx, vy});
		if (temp > 0.3)
			Log.d("Velocity", "" + vx);
		
		//velocityMagnitude = KinematicsGovernor.vectorMagnitude(new float[]{vx, vy});
		//Log.d("Acceleration Vector", "" + (event.values[0] + event.values[1]));
		
		switch (state) {
		case IDLE:
			ux = 0;
			uy = 0;

			if (velocityMagnitude > 1)
				state = State.MOVING;
			break;
		case MOVING:
			ux = vx;
			uy = vy;
			
			if (velocityMagnitude < 1)
				state = State.IDLE;
			break;
		default:
			break;
		}
		/*
		if (Math.abs(event.values[0]) > 1) {
			vx = ux + event.values[0] * t;
			Log.d("X-motion: ", "" + event.values[0]);
		}
		if (Math.abs(event.values[1]) > 1)
			Log.d("Y-motion: ", "" + event.values[1]);
			*/
	}
	
	private void setupCommand() {
		mCommandService = new BluetoothCommandService(this, mHandler);
	}

	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                case BluetoothCommandService.STATE_CONNECTED:
                    Log.d("MainActivity", "Connected to" + mConnectedDeviceName);
                    break;
                case BluetoothCommandService.STATE_CONNECTING:
                    Log.d("MainActivity", "Connecting...");
                    break;
                case BluetoothCommandService.STATE_LISTEN:
                case BluetoothCommandService.STATE_NONE:
                    Log.d("MainActivity", "Not connected.");
                    break;
                }
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString("Device_Name");
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    
}
