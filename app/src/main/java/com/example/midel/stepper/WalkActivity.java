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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
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
    float elapsedTime;
    boolean ready;

    LatLng previousLocation;
    long previousSteps;
    float previousTime;
    double previousAltitude;
    float previousDistance;
    SlotWalk slot;
    float totalDistance;
    float currentSpeed;

    //private com.example.midel.stepper.XMLManager XMLManager;
    private final String WALKSXMLFILE = "simpleWalks.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        activitiesList = (ArrayList<SimpleWalk>)i.getSerializableExtra("simpleWalkList");

        running = false;
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
        /*time.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                elapsedTime = chronometer.getBase() - SystemClock.elapsedRealtime();
                elapsedTime = (elapsedTime * -1) / 1000;
            }
        });*/
        mLocation = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (running) {
                    simpleWalk.addSlot(prepareData(locationResult.getLastLocation()));
                } else {
                    previousAltitude = locationResult.getLastLocation().getAltitude();
                    previousLocation = new LatLng(locationResult.getLastLocation().getLatitude(),
                            locationResult.getLastLocation().getLongitude());
                    slot = new SlotWalk(previousAltitude, previousDistance, previousLocation.longitude,
                            previousLocation.latitude, previousSteps, previousTime);
                }
                ready = true;//gps ready
            }
        };
        checkLocationPermission();

        cancelbtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vibrator.vibrate(500);
                        pauseWalk();
                        confirmCancel();
                    }
                });
        pausebtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!running) {
                            if (ready) {
                                vibrator.vibrate(500);
                                startWalk();
                            } else {
                                Toast toast;
                                toast = Toast.makeText(WalkActivity.this, "Waiting for GPS signal", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        } else {
                            vibrator.vibrate(500);
                            pauseWalk();
                        }
                    }
                });
        finishbtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vibrator.vibrate(500);
                        pauseWalk();
                        confirmFinish();
                    }
                });

    }

    private SlotWalk prepareData(Location location){
        double altit = location.getAltitude();

        elapsedTime = time.getBase() - SystemClock.elapsedRealtime();
        elapsedTime = (elapsedTime * -1) / 1000;

        float slotTime = elapsedTime-previousTime;

        LatLng currentLocation =new LatLng(location.getLatitude(),
                location.getLongitude());

        float[] result={0};
        Location.distanceBetween(previousLocation.latitude,previousLocation.longitude ,
                currentLocation.latitude,currentLocation.longitude, result);
        float slotDistance = result[0];

        totalDistance += slotDistance;

        long slotSteps = stepCounter-previousSteps;

        previousSteps = stepCounter;
        previousLocation = currentLocation;
        previousTime = elapsedTime;
        currentSpeed = (slotDistance / (elapsedTime))*((float)3.6);
        String aux = String.format("Distance: %.2fm",totalDistance);
        distance.setText(aux);
        aux = String.format("Speed: %.2fkm/h",currentSpeed);
        speed.setText(aux);
        aux = String.format("Altitude: %.2fm",altit);
        altitude.setText(aux);
        return new SlotWalk(altit,slotDistance,currentLocation.longitude,currentLocation.latitude, slotSteps, slotTime);
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
                            try{XMLManager.XMLWalk.saveXMLToFile(XMLManager.XMLWalk.write_XML(activitiesList),getFilesDir(), WALKSXMLFILE);}catch(Exception e){}
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
            FileInputStream is = XMLManager.XMLWalk.read_File(getFilesDir(), WALKSXMLFILE);
            XMLManager.XMLWalk.parse_XML(is,activitiesList);
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
        View mView = layoutInflaterAndroid.inflate(R.layout.pop_up_name, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        alert.setTitle("Walk name");
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

    private void confirmCancel(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.pop_up_confirm, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        alert.setTitle("Cancel");
        alert.setView(mView);
        ((TextView) mView.findViewById(R.id.message)).setText("Do you wan to cancel?");
        alert
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        cancelWalk();
                    }
                })

                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                //dialogBox.cancel();
                            startWalk();
                            }
                        });

        AlertDialog alertDialogAndroid = alert.create();
        alertDialogAndroid.show();
    }
    private void confirmFinish(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.pop_up_confirm, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        alert.setTitle("Finish");
        alert.setView(mView);
        ((TextView) mView.findViewById(R.id.message)).setText("Do you wan to finish?");
        alert
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        finishWalk();
                        Intent i = new Intent(WalkActivity.this,StatisticsActivity.class);
                        i.putExtra("simpleWalk", simpleWalk);
                        startActivity(i);
                        finish();
                    }
                })

                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                //dialogBox.cancel();
                            startWalk();
                            }
                        });

        AlertDialog alertDialogAndroid = alert.create();
        alertDialogAndroid.show();
    }

    private void cancelWalk(){
        time.stop();
        mLocation.removeLocationUpdates(mLocationCallback);
        sensorManager.unregisterListener(WalkActivity.this, stepperSensor);
        running = false;
        finish();
    }

    private void pauseWalk(){
        mLocation.removeLocationUpdates(mLocationCallback);
        time.stop();
        sensorManager.unregisterListener(WalkActivity.this, stepperSensor);
        pausebtn.setImageResource(R.drawable.play);
        running = false;
    }

    private void startWalk(){
        time.start();
        simpleWalk.startWalk(slot);
        checkLocationPermission();
        sensorManager.registerListener(WalkActivity.this, stepperSensor, SensorManager.SENSOR_DELAY_NORMAL);
        running = true;
        pausebtn.setImageResource(R.drawable.pause);
    }

    private void finishWalk(){
        mLocation.removeLocationUpdates(mLocationCallback);
        getLastPosition();
        running = false;
    }


}
