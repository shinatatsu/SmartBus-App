package com.example.root.finalbletest;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.UUID;

public class BatteryServiceFragment extends ServiceFragment implements SensorEventListener {

	private static final UUID BATTERY_SERVICE_UUID = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
	private static final UUID BATTERY_LEVEL_UUID = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");
	/**/
	private SensorManager aSensorManager;
	private Sensor aSensor;
	private float gravity[] = new float[3];
	/**/
	private ServiceFragmentDelegate mDelegate;
	private BluetoothGattService mBatteryService;
	private BluetoothGattCharacteristic mBatteryLevelCharacteristic;

	public BatteryServiceFragment() {
		mBatteryLevelCharacteristic = new BluetoothGattCharacteristic(BATTERY_LEVEL_UUID,
				BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
				BluetoothGattCharacteristic.PERMISSION_READ);

		mBatteryLevelCharacteristic.addDescriptor(Peripheral.getClientCharacteristicConfigurationDescriptor());

		mBatteryService = new BluetoothGattService(BATTERY_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);
		mBatteryService.addCharacteristic(mBatteryLevelCharacteristic);
	}

	// Lifecycle callbacks
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.paintview, container, false);
		Context contex = getActivity().getApplicationContext();
		PaintView pv = new PaintView(contex,null);
		final TextView dataview = (TextView) view.findViewById(R.id.data);
		aSensorManager = (SensorManager) contex.getSystemService(Context.SENSOR_SERVICE);
		aSensor = aSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		aSensorManager.registerListener(this, aSensor, aSensorManager.SENSOR_DELAY_NORMAL);
		RequestQueue Queue = Volley.newRequestQueue(contex);

//
//		new Thread(new Runnable(){
//			public void run(){
				JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://120.114.183.20/signup/link.php",null,
						new Response.Listener<JSONObject>(){
							@Override
							public void onResponse(JSONObject response){
								Log.d("TAG",response.toString());
								dataview.setText(response.toString());
							}},
						new Response.ErrorListener(){
							@Override
							public void onErrorResponse(VolleyError error){
								Log.e("TAG",error.getMessage(),error);
							}
						});
		Queue.add(jsonObjectRequest);
//			}
//		}).start();

		return pv;
		
		//mBatteryLevelEditText = (EditText) view.findViewById(R.id.textView_batteryLevel);
		//mBatteryLevelEditText.setOnEditorActionListener(mOnEditorActionListener);
		//Button notifyButton = (Button) view.findViewById(R.id.button_batteryLevelNotify);
		//notifyButton.setOnClickListener(mNotifyButtonListener);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mDelegate = (ServiceFragmentDelegate) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement ServiceFragmentDelegate");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mDelegate = null;
	}

	public BluetoothGattService getBluetoothGattService() {
		return mBatteryService;
	}

	@Override
	public ParcelUuid getServiceUUID() {
		return new ParcelUuid(BATTERY_SERVICE_UUID);
	}

	private void setGravityLevel( ) {
		String g = "";
		g = String.valueOf(gravity[0]) + ":" + String.valueOf(gravity[1]) + ":" + String.valueOf(gravity[2]);
		mBatteryLevelCharacteristic.setValue(g);
	}

	@Override
	// TODO Auto-generated method stub
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		gravity[0] = event.values[0];
		gravity[1] = event.values[1];
		gravity[2] = event.values[2];
		setGravityLevel();
	}

	@Override
	// 取消註冊SensorEventListener 
	public void onPause() {								
		aSensorManager.unregisterListener(this);
		Toast.makeText(getActivity().getApplication(), "Unregister accelerometerListener", Toast.LENGTH_LONG).show();
		super.onPause();
	}
	
	//private EditText mBatteryLevelEditText;
		/*private final OnEditorActionListener mOnEditorActionListener = new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					String newBatteryLevelString = textView.getText().toString();
					// Need to check if the string is empty since isDigitsOnly
					// returns
					// true for empty strings.
					if (!newBatteryLevelString.isEmpty() && android.text.TextUtils.isDigitsOnly(newBatteryLevelString)) {
						int newBatteryLevel = Integer.parseInt(newBatteryLevelString);
						if (newBatteryLevel <= BATTERY_LEVEL_MAX) {
						} else {
							Toast.makeText(getActivity(), R.string.batteryLevelTooHigh, Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(getActivity(), R.string.batteryLevelIncorrect, Toast.LENGTH_SHORT).show();
					}
				}
				return false;
			}
		};*/

		/*private final OnClickListener mNotifyButtonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDelegate.sendNotificationToDevices(mBatteryLevelCharacteristic);
			}
		};*/

}