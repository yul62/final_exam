package com.example.igx.problem1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener/* implements Something1, Something2 */ {

    private double mLat,mLng;
    private LocationManager manager;
    private SensorManager sensorManager;
    private Sensor  ziroSensor, linearSensor, gravitySensor, accelSensor;
    private GPSlistenr gpslistener = new GPSlistenr();
    private String state1,state2,state3,state4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkpermission();

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        ziroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        linearSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        Button btn_getLocation = (Button) findViewById(R.id.btn_getLocation);
        Button btn_getSensors = (Button) findViewById(R.id.btn_getSensors);
        Button btn_sendMessage = (Button) findViewById(R.id.btn_sendMessage);

        final TextView text_selectedData = (TextView) findViewById(R.id.text_selectedData);
        final TextView text_selectedType = (TextView) findViewById(R.id.text_selectedType);
        final EditText edit_phoneNumber = (EditText) findViewById(R.id.edit_phoneNumber);

        btn_getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                text_selectedType.setText("LOCATION");

                startLocation();
                text_selectedData.setText(getLoca(mLat,mLng));
            }
        });

        btn_getSensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_selectedType.setText("SENSOR");
                text_selectedData.setText(state1+state2+state3+state4);
            }
        });

        btn_sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);intent.setData(Uri.parse("smsto:"+edit_phoneNumber.getText()));  // This ensures only SMS apps respond
                intent.putExtra("sms_body", text_selectedData.getText());
                startActivity(intent);

            }
        });
    }


    public void checkpermission(){
        String[] permissions = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
        };
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for(int i=0;i< permissions.length;i++){
            permissionCheck = ContextCompat.checkSelfPermission(this,permissions[i]);
            if(permissionCheck==PackageManager.PERMISSION_DENIED)
                break;
        }

        if(permissionCheck==PackageManager.PERMISSION_GRANTED) {
            //권한있음
        }else{//권한 없음
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,permissions[0])){
                //ds
            }else
                ActivityCompat.requestPermissions(this,permissions,1);
        }
    }

    public String getLoca(double lat, double lng){
        String str = "Latitude : "+ lat +" Longitude : "+lng;
        return str;
    }
    public void startLocation()
    {
        long minTime =1000;
        float minDistance = 0;
        try{
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,minTime,minDistance,gpslistener);
            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, minTime,minDistance,gpslistener);

        }catch (SecurityException e){
            Toast.makeText(getBaseContext(),"ddddd",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,accelSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,ziroSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,gravitySensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,linearSensor,SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_GRAVITY:
                state1  ="Gravity x = "+event.values[0]+", y = "+event.values[1]+", z = "+event.values[2]+"\n\n";
                break;
            case Sensor.TYPE_GYROSCOPE:
                state2  ="Zyro x = "+event.values[0]+", y = "+event.values[1]+", z = "+event.values[2]+"\n\n";
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                state3  ="Linear Accel x = "+event.values[0]+", y = "+event.values[1]+", z = "+event.values[2]+"\n\n";
                break;
            case Sensor.TYPE_ACCELEROMETER:
                state4  ="Accel x = "+event.values[0]+", y = "+event.values[1]+", z = "+event.values[2]+"\n\n";
                break;

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    class GPSlistenr implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            mLat = location.getLatitude();
            mLng = location.getLongitude();

            try{
                manager.removeUpdates(gpslistener);
            }catch (SecurityException e){
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
