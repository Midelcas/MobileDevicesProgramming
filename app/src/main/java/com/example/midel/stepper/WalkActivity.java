package com.example.midel.stepper;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;


public class WalkActivity extends AppCompatActivity implements SensorEventListener {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private SimpleWalk simpleWalk;
    private ArrayList<SimpleWalk> activitiesList;
    FloatingActionButton cancelbtn;
    FloatingActionButton pausebtn;
    FloatingActionButton finishbtn;
    PausableChronometer time;
    TextView altitude;
    TextView speed;
    TextView steps;
    TextView distance;
    boolean running;
    private SensorManager sensorManager;
    Sensor stepperSensor;
    long stepCounter;
    Vibrator vibrator;
    FusedLocationProviderClient mLocation;
    LocationCallback mLocationCallback;
    long elapsedTime;
    boolean ready;

    LatLng previousLocation;
    long previousSteps;
    float previousTime;
    double previousAltitude;
    float previousDistance;
    SlotWalk slot;

    private XMLManagerWalk XMLManager;
    private final String WALKSXMLFILE = "simpleWalks.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
        running = false;
        XMLManager = new XMLManagerWalk();
        activitiesList = new ArrayList<SimpleWalk>();
        checkWalks();
        getName();



        cancelbtn = (FloatingActionButton) findViewById(R.id.cancelbtn);
        pausebtn = (FloatingActionButton) findViewById(R.id.pausebtn);
        finishbtn = (FloatingActionButton) findViewById(R.id.finishbtn);
        time = (PausableChronometer) findViewById(R.id.time);
        altitude = (TextView) findViewById(R.id.altitude);
        speed = (TextView) findViewById(R.id.speed);
        steps = (TextView) findViewById(R.id.steps);
        distance = (TextView) findViewById(R.id.distance);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepperSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        stepCounter = 0;
        elapsedTime = 0;
        ready = false;

        previousLocation = null;
        previousSteps = 0;
        previousTime = 0;
        previousAltitude = 0;


        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        time.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                elapsedTime = chronometer.getBase() - SystemClock.elapsedRealtime();
                elapsedTime = (elapsedTime * -1) / 1000;
                if (elapsedTime % 5 == 0) {
                }
            }
        });
        mLocation = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (running) {
                    simpleWalk.addSlot(prepareData(locationResult.getLastLocation()));
                } else {
                    previousAltitude = locationResult.getLastLocation().getAltitude();
                    previousLocation = new LatLng(locationResult.getLastLocation().getLongitude(),
                            locationResult.getLastLocation().getLatitude());
                    slot = new SlotWalk(previousAltitude, previousDistance, previousLocation,
                            previousSteps, previousTime);
                }
                ready = true;//gps ready
            }
        };
        checkLocationPermission();

        cancelbtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        time.stop();
                        mLocation.removeLocationUpdates(mLocationCallback);
                        vibrator.vibrate(500);
                        sensorManager.unregisterListener(WalkActivity.this, stepperSensor);
                        running = false;
                        /*VOLVER A LA PANTALLA ANTERIOR*/
                    }
                });
        pausebtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!running) {
                            if (ready) {
                                time.start();
                                vibrator.vibrate(500);
                                simpleWalk.startWalk(slot);
                                checkLocationPermission();
                                sensorManager.registerListener(WalkActivity.this, stepperSensor, SensorManager.SENSOR_DELAY_NORMAL);
                                running = !running;
                            } else {
                                Toast toast;
                                toast = Toast.makeText(WalkActivity.this, "Waiting for GPS signal", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        } else {
                            mLocation.removeLocationUpdates(mLocationCallback);
                            vibrator.vibrate(500);
                            time.stop();
                            sensorManager.unregisterListener(WalkActivity.this, stepperSensor);
                            running = !running;
                        }
                    }
                });
        finishbtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mLocation.removeLocationUpdates(mLocationCallback);
                        vibrator.vibrate(500);
                        getLastPosition();
                        running = !running;
                    }
                });

    }

    private SlotWalk prepareData(Location location){
        double altit = location.getAltitude();
        altitude.setText(altit+"m");
        float slotTime = elapsedTime-previousTime;

        LatLng currentLocation =new LatLng(location.getLongitude(),
                location.getLatitude());

        float[] result={0};
        Location.distanceBetween(previousLocation.longitude,previousLocation.latitude ,
                currentLocation.longitude,currentLocation.latitude, result);
        float slotDistance = result[0];

        long slotSteps = stepCounter-previousSteps;

        previousSteps = slotSteps;
        previousLocation = currentLocation;
        previousTime = slotTime;
        return new SlotWalk(altit,slotDistance,currentLocation, slotSteps, slotTime);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if((sensorEvent.sensor.equals(stepperSensor))&&running) {
            stepCounter++;
            steps.setText("Steps:"+stepCounter);
        }

    }

    private void getLastPosition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //If Location permissions are not granted for the app, ask user for it! Request response will be received in the onRequestPermissionsResult.
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            } else {
                mLocation.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            time.stop();
                            sensorManager.unregisterListener(WalkActivity.this, stepperSensor);
                            simpleWalk.endWalk(prepareData(location));
                            try{XMLManager.saveXMLToFile(XMLManager.write_XML(activitiesList),getFilesDir(), WALKSXMLFILE);}catch(Exception e){}
                        }
                    }
                });

            }
        }
    }

    private void checkLocationPermission() {
        //If Android version is M (6.0 API 23) or newer, check if it has Location permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //If Location permissions are not granted for the app, ask user for it! Request response will be received in the onRequestPermissionsResult.
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
            else {
                mLocation.requestLocationUpdates(getLocationRequest(), mLocationCallback, null);
            }
        }
    }

    private LocationRequest getLocationRequest(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        //Check if permission request response is from Location
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //User granted permissions. Setup the scan settings
                } else {
                    //User denied Location permissions. Here you could warn the user that without
                    //Location permissions the app is not able to scan for BLE devices
                    //In this case we just close the app
                    finish();
                }
                return;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // In this app we do nothing if sensor's accuracy changes
    }

    public void checkWalks(){
        try {
            FileInputStream is = XMLManager.read_File(getFilesDir(), WALKSXMLFILE);
            XMLManager.parse_XML(is,activitiesList);
        }catch(IOException e){
            Toast toast;
            toast = Toast.makeText(this,"Error while reading xml",Toast.LENGTH_SHORT);
            toast.show();
        }catch(ParseException e){
            Toast toast;
            toast = Toast.makeText(this,"Error while parsing xml",Toast.LENGTH_SHORT);
            toast.show();
        }catch(Exception e){
            Toast toast;
            toast = Toast.makeText(this,"Error while parsing xml",Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void getName(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.pop_up, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(mView);
        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        alert
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        String name = userInputDialogEditText.getText().toString();
                        if(name.length()==0){
                            name = "New Walk";
                        }
                        simpleWalk = new SimpleWalk(name, null);
                        setTitle(simpleWalk.getName());
                        activitiesList.add(simpleWalk);
                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                                simpleWalk = new SimpleWalk("New Walk", null);
                                setTitle(simpleWalk.getName());
                                activitiesList.add(simpleWalk);
                            }
                        });

        AlertDialog alertDialogAndroid = alert.create();
        alertDialogAndroid.show();
    }


}
