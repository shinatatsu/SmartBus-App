package com.example.root.finalbletest;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

public class MapsActivity extends Activity implements OnMapReadyCallback {

    public static final int LOCATION_update_min_distance = 10;
    public static final int LOCATION_update_min_time = 5000;
    private TextView txv;
    private GoogleMap mMap;
    private MapFragment MapFragment;
    private LocationManager mgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        final Context contex = this.getApplicationContext();
        final RequestQueue Queue = Volley.newRequestQueue(contex);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        txv = (TextView) findViewById(R.id.textView_serviceName);

        setMap();
        mgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        initMap();

        new Thread() {
            @Override
            public void run() {
                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateWithNewLocation();
                    }
                });
            }
        }.start();

        new Thread(new Runnable() {
            public void run() {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://61.60.30.138/opendata/b03ebfae-5996-4960-9c3a-2eb6548bf956?format=json", null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("TAG", response.toString());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("TAG", error.getMessage(), error);
                            }
                        });
                Queue.add(jsonObjectRequest);
            }
        }).start();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mgr.removeUpdates(locationListener);
        }catch (SecurityException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateWithNewLocation();
    }
    //----------------for map----------------//
    private void initMap() {
        int googlePlayStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        try {
            if (googlePlayStatus != ConnectionResult.SUCCESS) {
                GooglePlayServicesUtil.getErrorDialog(googlePlayStatus, this, -1).show();
                finish();
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

    private void setMap(){
        if(mMap == null){
            MapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            MapFragment.getMapAsync(this);
        }
    }

    @Override
    public void startManagingCursor(Cursor c) {
        if (c == null) {
            throw new IllegalStateException("cannot manage cursor: cursor == null");
        }
        super.startManagingCursor(c);
    }

    private void showMarkerMe(Location location){
        if(mMap != null){
            mMap.clear();
            LatLng gps = new LatLng(location.getLatitude(),location.getLongitude());
            //-----------Parse Json----------//
//            JSONObject jsonObject;
//            try{
//                jsonObject = new JSONObject(Jsontext);
//                JSONArray dataArray = new JSONArray(jsonObject);
//                String[] stopName = new String[dataArray.length()];
//                String[] stopLat = new String[dataArray.length()];
//                String[] stopLon =  new String[dataArray.length()];
//
//                for(int i=0;i<dataArray.length();i++){
//                    stopName[i] = dataArray.getJSONObject(i).getString("站牌名稱");
//                    stopLat[i] = dataArray.getJSONObject(i).getString("緯度");
//                    stopLon[i] = dataArray.getJSONObject(i).getString("經度");
//                    float stoplat = Float.parseFloat(stopLat[i]);
//                    float stoplon = Float.parseFloat(stopLon[i]);
//                    //1km = 0.01
//                    if((Math.abs(stoplat-location.getLatitude())<0.01)
//                            ||(Math.abs(stoplon-location.getLongitude())<0.01)){
//                        LatLng busstop = new LatLng(stoplat,stoplon);
//                        mMap.addMarker(new MarkerOptions()
//                                .position(busstop)
//                                .title(stopName[i])
//                                .visible(true));
//                    }
//                }
//
//            }catch (JSONException e){
//                e.printStackTrace();
//            }
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

//        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1);

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
