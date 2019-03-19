package com.example.root.finalbletest;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.UUID;

public class HeartRateServiceFragment extends ServiceFragment implements OnMapReadyCallback {
  private static final String TAG = HeartRateServiceFragment.class.getCanonicalName();
  /**
   * See <a href="https://developer.bluetooth.org/gatt/services/Pages/ServiceViewer.aspx?u=org.bluetooth.service.heart_rate.xml">
   * Heart Rate Service</a>
   */
  private static final UUID HEART_RATE_SERVICE_UUID = UUID
      .fromString("0000180D-0000-1000-8000-00805f9b34fb");

  /**
   * See <a href="https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml">
   * Heart Rate Measurement</a>
   */
  private static final UUID HEART_RATE_MEASUREMENT_UUID = UUID
      .fromString("00002A00-0000-1000-8000-00805f9b34fb");
  private static final int EXPENDED_ENERGY_FORMAT = BluetoothGattCharacteristic.FORMAT_UINT16;

  /**
   * See <a href="https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.body_sensor_location.xml">
   * Body Sensor Location</a>
   */
  private static final UUID BODY_SENSOR_LOCATION_UUID = UUID
      .fromString("00002A38-0000-1000-8000-00805f9b34fb");

  /**
   * See <a href="https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_control_point.xml">
   * Heart Rate Control Point</a>
   */
  private static final UUID HEART_RATE_CONTROL_POINT_UUID = UUID
      .fromString("00002A39-0000-1000-8000-00805f9b34fb");
  private BluetoothGattService mHeartRateService;
  private BluetoothGattCharacteristic mHeartRateMeasurementCharacteristic;
  private BluetoothGattCharacteristic mBodySensorLocationCharacteristic;
  private BluetoothGattCharacteristic mHeartRateControlPoint;

  private ServiceFragment.ServiceFragmentDelegate mDelegate;
  private GoogleMap mMap;
  private SupportMapFragment mapFragment;
  private LocationManager mgr;
  public static final int LOCATION_update_min_distance = 10;
  public static final int LOCATION_update_min_time = 5000;
  private FloatingActionButton fab;
  private TextView txv;
  private Context context;

//
//  private final OnEditorActionListener mOnEditorActionListenerEnergyExpended = new OnEditorActionListener() {
//    @Override
//    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
//      if (actionId == EditorInfo.IME_ACTION_DONE) {
//        String newEnergyExpendedString = textView.getText().toString();
//        if (isValidCharacteristicValue(newEnergyExpendedString,
//            EXPENDED_ENERGY_FORMAT)) {
//          int newEnergyExpended = Integer.parseInt(newEnergyExpendedString);
//          mHeartRateMeasurementCharacteristic.setValue(newEnergyExpended,
//              EXPENDED_ENERGY_FORMAT,
//              /* offset */ 2);
//        } else {
//          Toast.makeText(getActivity(), R.string.energyExpendedInvalid,
//              Toast.LENGTH_SHORT).show();
//        }
//      }
//      return false;
//    }
//  };


  public HeartRateServiceFragment() {
    mHeartRateMeasurementCharacteristic =
        new BluetoothGattCharacteristic(HEART_RATE_MEASUREMENT_UUID,
            BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            /* No permissions */ 0);

    mHeartRateMeasurementCharacteristic.addDescriptor(
        Peripheral.getClientCharacteristicConfigurationDescriptor());

    mBodySensorLocationCharacteristic =
        new BluetoothGattCharacteristic(BODY_SENSOR_LOCATION_UUID,
            BluetoothGattCharacteristic.PROPERTY_READ,
            BluetoothGattCharacteristic.PERMISSION_READ);

    mHeartRateControlPoint =
        new BluetoothGattCharacteristic(HEART_RATE_CONTROL_POINT_UUID,
            BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_WRITE);

    mHeartRateService = new BluetoothGattService(HEART_RATE_SERVICE_UUID,
        BluetoothGattService.SERVICE_TYPE_PRIMARY);
    mHeartRateService.addCharacteristic(mHeartRateMeasurementCharacteristic);
    mHeartRateService.addCharacteristic(mBodySensorLocationCharacteristic);
    mHeartRateService.addCharacteristic(mHeartRateControlPoint);

  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_heart_rate, container, false);
    txv = (TextView)view.findViewById(R.id.textView_serviceName);

//    fab = (FloatingActionButton)view.findViewById(R.id.fab);
//    fab.setOnClickListener(new View.OnClickListener(){
//      @Override
//      public void onClick(View view){
//        updateWithNewLocation();
//      }
//    });
    context = getActivity().getApplicationContext();
    setUpMap();
    mgr = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
    initMap();
    updateWithNewLocation();
    return view;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mDelegate = (ServiceFragment.ServiceFragmentDelegate) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
          + " must implement ServiceFragmentDelegate");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mDelegate = null;
  }

  private void initMap() {
    int googlePlayStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

    try {
      if (googlePlayStatus != ConnectionResult.SUCCESS) {
        GooglePlayServicesUtil.getErrorDialog(googlePlayStatus, (Activity) context, -1).show();
//        finish();
      } else {
        if (mMap != null) {
          mMap.setMyLocationEnabled(true);
          mMap.getUiSettings().setMyLocationButtonEnabled(true);
          mMap.getUiSettings().setAllGesturesEnabled(true);
        }
      }
    }catch (SecurityException e){
      e.printStackTrace();
    }
  }

  private void setUpMap(){
    if(mMap == null){
//      mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.fragment);
      mapFragment.getMapAsync(this);

    }
  }

  public void onMapReady(GoogleMap map) {
    mMap = map;
    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
  }
  @Override
  public BluetoothGattService getBluetoothGattService() {
    return mHeartRateService;
  }

  @Override
  public ParcelUuid getServiceUUID() {
    return new ParcelUuid(HEART_RATE_SERVICE_UUID);
  }

//  private void setHeartRateMeasurementValue(int heartRateMeasurementValue, int expendedEnergy) {
//
//    Log.d(TAG, Arrays.toString(mHeartRateMeasurementCharacteristic.getValue()));
//    /* Set the org.bluetooth.characteristic.heart_rate_measurement
//     * characteristic to a byte array of size 4 so
//     * we can use setValue(value, format, offset);
//     *
//     * Flags (8bit) + Heart Rate Measurement Value (uint8) + Energy Expended (uint16) = 4 bytes
//     *
//     * Flags = 1 << 3:
//     *   Heart Rate Format (0) -> UINT8
//     *   Sensor Contact Status (00) -> Not Supported
//     *   Energy Expended (1) -> Field Present
//     *   RR-Interval (0) -> Field not pressent
//     *   Unused (000)
//     */
//    mHeartRateMeasurementCharacteristic.setValue(new byte[]{0b00001000, 0, 0, 0});
//    // Characteristic Value: [flags, 0, 0, 0]
//    mHeartRateMeasurementCharacteristic.setValue(heartRateMeasurementValue,
//        HEART_RATE_MEASUREMENT_VALUE_FORMAT,
//        /* offset */ 1);
//    // Characteristic Value: [flags, heart rate value, 0, 0]
//    mHeartRateMeasurementCharacteristic.setValue(expendedEnergy,
//        EXPENDED_ENERGY_FORMAT,
//        /* offset */ 2);
//    // Characteristic Value: [flags, heart rate value, energy expended (LSB), energy expended (MSB)]
//  }


//  private boolean isValidCharacteristicValue(String s, int format) {
//    try {
//      int value = Integer.parseInt(s);
//      if (format == BluetoothGattCharacteristic.FORMAT_UINT8) {
//        return (value >= MIN_UINT) && (value <= MAX_UINT8);
//      } else if (format == BluetoothGattCharacteristic.FORMAT_UINT16) {
//        return (value >= MIN_UINT) && (value <= MAX_UINT16);
//      } else {
//        throw new IllegalArgumentException(format + " is not a valid argument");
//      }
//    } catch (NumberFormatException e) {
//      return false;
//    }
//  }
//
//  @Override
//  public int writeCharacteristic(BluetoothGattCharacteristic characteristic, int offset, byte[] value) {
//    if (offset != 0) {
//      return BluetoothGatt.GATT_INVALID_OFFSET;
//    }
//    // Heart Rate control point is a 8bit characteristic
//    if (value.length != 1) {
//      return BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH;
//    }
//    if ((value[0] & 1) == 1) {
//      getActivity().runOnUiThread(new Runnable() {
//        @Override
//        public void run() {
//          mHeartRateMeasurementCharacteristic.setValue(0,
//              EXPENDED_ENERGY_FORMAT, /* offset */ 2);
//        }
//      });
//    }
//    return BluetoothGatt.GATT_SUCCESS;
//  }

@Override
public void onPause()
{
// TODO Auto-generated method stub
//Toast.makeText(this, "Unregister accelerometerListener", Toast.LENGTH_LONG).show();
super.onPause();
  try{
    mgr.removeUpdates(locationListener);
  }catch(SecurityException e){
    e.printStackTrace();
  }
}
  @Override
  public void onResume() {
    super.onResume();
    updateWithNewLocation();
  }

  private void showMarkerMe(Location location){
    if(mMap != null){
      mMap.clear();
      LatLng gps = new LatLng(location.getLatitude(),location.getLongitude());
      //-----------Parse Json----------//
      JSONObject jsonObject;
//      try{
//        jsonObject = new JSONObject(Jsontext);
//        JSONArray dataArray = new JSONArray(jsonObject);
//        String[] stopName = new String[dataArray.length()];
//        String[] stopLat = new String[dataArray.length()];
//        String[] stopLon =  new String[dataArray.length()];
//
//        for(int i=0;i<dataArray.length();i++){
//          stopName[i] = dataArray.getJSONObject(i).getString("站牌名稱");
//          stopLat[i] = dataArray.getJSONObject(i).getString("緯度");
//          stopLon[i] = dataArray.getJSONObject(i).getString("經度");
//          float stoplat = Float.parseFloat(stopLat[i]);
//          float stoplon = Float.parseFloat(stopLon[i]);
//          //1km = 0.01
//          if((Math.abs(stoplat-location.getLatitude())<0.01)
//                  ||(Math.abs(stoplon-location.getLongitude())<0.01)){
//            LatLng busstop = new LatLng(stoplat,stoplon);
//            mMap.addMarker(new MarkerOptions()
//                    .position(busstop)
//                    .title(stopName[i])
//                    .visible(true));
//          }
//        }
//
//      }catch (JSONException e){
//        e.printStackTrace();
//      }
      //-----------random bus----------//
      MarkerOptions markbus3 = new MarkerOptions();

      markbus3.position(new LatLng(22.9800863,120.2187232));
      markbus3.title("No. 203");
      markbus3.snippet("目前車內狀況...");

      mMap.addMarker(markbus3);

      mMap.addMarker(new MarkerOptions()
              .position(gps)
              .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
              .title("You"));

      mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps,16));
    }

  }

  private void updateWithNewLocation() {
    boolean isGPSEnabled = mgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
    boolean isNetworkEnabled = mgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    Location location = null;
    try {
      if (isNetworkEnabled) {
        mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                LOCATION_update_min_time, LOCATION_update_min_distance, locationListener);
        location = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
      }
      if(isGPSEnabled){
        mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                LOCATION_update_min_time, LOCATION_update_min_distance, locationListener);
        location = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      }
      if(location != null){
        txv.setText(String.format("(%.4f, %.4f)",location.getLatitude(),location.getLongitude()));
        //getJson();
        showMarkerMe(location);
      }
    }catch (SecurityException e){
      e.printStackTrace();
    }
  }

  private LocationListener locationListener = new LocationListener() {
    @Override
    public void onLocationChanged(Location location) {
      try {
        if (location != null) {
          txv.setText(String.format("%f , %f", location.getLatitude(), location.getLongitude()));
          showMarkerMe(location);
          mgr.removeUpdates(locationListener);
        } else
          txv.setText("Location is null");
      }catch (SecurityException e)
      {
        e.printStackTrace();
      }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
//
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
  };

}