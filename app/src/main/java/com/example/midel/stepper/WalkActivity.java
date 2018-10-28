package com.example.midel.stepper;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;


public class WalkActivity extends AppCompatActivity implements SensorEventListener {

    private SimpleWalk simpleWalk;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
        running = false;
        simpleWalk = (SimpleWalk) getIntent().getSerializableExtra("simpleWalk");
        simpleWalk.getName();
        setTitle(simpleWalk.getName());
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
        stepCounter=0;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        time.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedTime = chronometer.getBase() - SystemClock.elapsedRealtime();
                elapsedTime = (elapsedTime*-1)/1000;
                if(elapsedTime%5==0) {
                }
            }
        });

        cancelbtn.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
        });
        pausebtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!running){
                            time.start();
                            vibrator.vibrate(500);
                            sensorManager.registerListener(WalkActivity.this, stepperSensor, SensorManager.SENSOR_DELAY_NORMAL);
                            running=!running;
                        }else{
                            time.stop();
                            vibrator.vibrate(500);
                            sensorManager.unregisterListener(WalkActivity.this, stepperSensor);
                            running=!running;
                        }
                    }
                });
        finishbtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if((sensorEvent.sensor.equals(stepperSensor))&&running) {
            stepCounter++;
            steps.setText("Steps:"+stepCounter);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // In this app we do nothing if sensor's accuracy changes
    }
}
