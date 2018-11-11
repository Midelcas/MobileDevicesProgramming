package com.example.midel.stepper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.Image;
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
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;


public class WalkActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener{
    private static final int RUNNING = 0;
    private static final int STOPPED = 1;
    private static final int RUN = 0;
    private static final int STOP = 1;
    private static final int FINISH = 2;
    private static final int CANCEL = 3;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private SimpleWalk simpleWalk;
    private ArrayList<SimpleWalk> activitiesList;
    FloatingActionButton cancelbtn;
    FloatingActionButton pausebtn;
    FloatingActionButton playbtn;
    FloatingActionButton finishbtn;
    PausableChronometer time;
    TextView altitude;
    TextView speed;
    TextView steps;
    TextView distance;
    ImageView stepperimage;
    //boolean running;
    private SensorManager sensorManager;
    Sensor stepperSensor;
    long stepCounter;
    Vibrator vibrator;
    FusedLocationProviderClient mLocation;
    LocationCallback mLocationCallback;
    float elapsedTime;
    boolean ready;
    boolean walkStarted;
    Toast toast;

    LatLng previousLocation;
    long previousSteps;
    float previousTime;
    double previousAltitude;
    float previousDistance;
    SlotWalk slot;
    float totalDistance;
    float currentSpeed;
    int currentStatus;
    int previousStatus;
    Intent i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        i = getIntent();
        activitiesList = (ArrayList<SimpleWalk>)i.getSerializableExtra(getString(R.string.simpleWalkList));
        currentStatus=STOPPED;
        previousStatus=STOPPED;
        //running = false;
        getName();

        stepperimage = (ImageView) findViewById(R.id.stepperimage);
        cancelbtn = (FloatingActionButton) findViewById(R.id.cancelbtn);
        pausebtn = (FloatingActionButton) findViewById(R.id.pausebtn);
        playbtn = (FloatingActionButton) findViewById(R.id.playbtn);
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
        walkStarted=false;
        previousLocation = null;
        previousSteps = 0;
        previousTime = 0;
        previousAltitude = 0;


        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mLocation = LocationServices.getFusedLocationProviderClient(this);
        time.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long seconds = time.getBase() - SystemClock.elapsedRealtime();
                seconds = (seconds * -1) / 1000;
                if(seconds%2==0){
                    stepperimage.setScaleX(-1);
                }else{
                    stepperimage.setScaleX(1);
                }


            }
        });
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                switch (currentStatus){
                    case STOPPED:
                        previousAltitude = locationResult.getLastLocation().getAltitude();
                        previousLocation = new LatLng(locationResult.getLastLocation().getLatitude(),
                                locationResult.getLastLocation().getLongitude());
                        slot = new SlotWalk(previousAltitude, previousDistance, previousLocation.longitude,
                                previousLocation.latitude, previousSteps, previousTime);
                        break;
                    case RUNNING:
                        simpleWalk.addSlot(prepareData(locationResult.getLastLocation()));
                        break;
                }
                ready = true;//gps ready
            }
        };
        checkLocationPermission();

        cancelbtn.setOnClickListener(this);
        pausebtn.setOnClickListener(this);
        playbtn.setOnClickListener(this);
        finishbtn.setOnClickListener(this);
    }

    @SuppressLint("RestrictedApi")
    private void statusChange(int action){
        switch(currentStatus){
            case STOPPED:
                switch(action){
                    case RUN:
                        if(ready){
                            startWalk();
                            previousStatus=currentStatus;
                            currentStatus = RUNNING;
                            playbtn.setVisibility(View.GONE);
                            pausebtn.setVisibility(View.VISIBLE);
                            vibrator.vibrate(500);
                            walkStarted=true;
                        }else{
                            toast = Toast.makeText(WalkActivity.this, getString(R.string.gps_wait), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        break;
                    case CANCEL:
                        if(walkStarted) {
                            pauseWalk();
                            previousStatus = currentStatus;
                            currentStatus = STOPPED;
                        }
                        confirmCancel();
                        vibrator.vibrate(500);
                        break;
                    case FINISH:
                        if(walkStarted) {
                            pauseWalk();
                            previousStatus = currentStatus;
                            currentStatus = STOPPED;
                            confirmFinish();
                            vibrator.vibrate(500);
                        }else{
                            toast = Toast.makeText(WalkActivity.this, getString(R.string.no_walk), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        break;
                }
                break;
            case RUNNING:
                switch(action){
                    case STOP:
                        pauseWalk();
                        previousStatus=currentStatus;
                        currentStatus=STOPPED;
                        pausebtn.setVisibility(View.GONE);
                        playbtn.setVisibility(View.VISIBLE);
                        vibrator.vibrate(500);
                        break;
                    case CANCEL:
                        previousStatus=currentStatus;
                        confirmCancel();
                        vibrator.vibrate(500);
                        break;
                    case FINISH:
                        if(walkStarted){
                            pauseWalk();
                            previousStatus=currentStatus;
                            currentStatus=STOPPED;
                            confirmFinish();
                            vibrator.vibrate(500);
                        }else{
                            toast = Toast.makeText(WalkActivity.this, getString(R.string.no_walk), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        break;
                }
                break;
        }
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
        String aux = String.format(getString(R.string.distanceformat),totalDistance);
        distance.setText(aux);
        aux = String.format(getString(R.string.speedformat),currentSpeed);
        speed.setText(aux);
        aux = String.format(getString(R.string.altitudeformat),altit);
        altitude.setText(aux);
        return new SlotWalk(altit,slotDistance,currentLocation.longitude,currentLocation.latitude, slotSteps, slotTime);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if((sensorEvent.sensor.equals(stepperSensor))&&(currentStatus==RUNNING)) {
            stepCounter++;
            steps.setText(getString(R.string.step)+stepCounter);
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
                            try{XMLManager.XMLWalk.saveXMLToFile(XMLManager.XMLWalk.write_XML(activitiesList),getFilesDir(), getString(R.string.WALKSXMLFILE));}catch(Exception e){}
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
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationPermission();
                } else {
                    goToMainActivity();
                }
                return;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // In this app we do nothing if sensor's accuracy changes
    }

    private void getName(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.pop_up_name, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        alert.setTitle(getString(R.string.walk_name));
        alert.setView(mView);
        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        alert
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        String name = userInputDialogEditText.getText().toString();
                        if(name.length()==0){
                            name = getString(R.string.new_walk);
                        }
                        simpleWalk = new SimpleWalk(name, null);
                        setTitle(simpleWalk.getName());
                        activitiesList.add(simpleWalk);
                    }
                })

                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                cancelWalk();
                                goToMainActivity();
                            }
                        });

        AlertDialog alertDialogAndroid = alert.create();
        alertDialogAndroid.show();
    }

    private void confirmCancel(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.pop_up_confirm, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        alert.setTitle(getString(R.string.cancel));
        alert.setView(mView);
        ((TextView) mView.findViewById(R.id.message)).setText(getString(R.string.want_cancel));
        alert
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        cancelWalk();
                    }
                })

                .setNegativeButton(getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                switch (previousStatus){
                                    case STOPPED:
                                        statusChange(STOP);
                                        break;
                                    case RUNNING:
                                        statusChange(RUN);
                                        break;
                                }
                            }
                        });

        AlertDialog alertDialogAndroid = alert.create();
        alertDialogAndroid.show();
    }
    private void confirmFinish(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.pop_up_confirm, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        alert.setTitle(getString(R.string.finish));
        alert.setView(mView);
        ((TextView) mView.findViewById(R.id.message)).setText(getString(R.string.want_finish));
        alert
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        finishWalk();
                        goToStatisticsActivity();
                    }
                })

                .setNegativeButton(getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                //dialogBox.cancel();
                                switch (previousStatus){
                                    case STOPPED:
                                        statusChange(STOP);
                                        break;
                                    case RUNNING:
                                        statusChange(RUN);
                                        break;
                                }
                            }
                        });

        AlertDialog alertDialogAndroid = alert.create();
        alertDialogAndroid.show();
    }

    private void cancelWalk(){
        time.stop();
        mLocation.removeLocationUpdates(mLocationCallback);
        sensorManager.unregisterListener(WalkActivity.this, stepperSensor);
        goToMainActivity();
    }

    private void pauseWalk(){
        mLocation.removeLocationUpdates(mLocationCallback);
        time.stop();
        sensorManager.unregisterListener(WalkActivity.this, stepperSensor);
    }

    private void startWalk(){
        time.start();
        simpleWalk.startWalk(slot);
        checkLocationPermission();
        sensorManager.registerListener(WalkActivity.this, stepperSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void finishWalk(){
        mLocation.removeLocationUpdates(mLocationCallback);
        getLastPosition();
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.pausebtn:
                statusChange(STOP);
                break;
            case R.id.playbtn:
                statusChange(RUN);
                break;
            case R.id.finishbtn:
                statusChange(FINISH);
                break;
            case R.id.cancelbtn:
                statusChange(CANCEL);
                break;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onBackPressed(){
        goToMainActivity();
    }

    private void goToMainActivity(){
        i = new Intent(WalkActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }

    private void goToStatisticsActivity(){
        i = new Intent(WalkActivity.this,StatisticsActivity.class);
        i.putExtra(getString(R.string.simpleWalk), simpleWalk);
        startActivity(i);
        finish();
    }
}
