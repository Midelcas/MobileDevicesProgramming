package com.example.midel.stepper;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class SlotWalk implements Serializable {
    private double mAltitude;
    private float mDistance;
    private LatLng mLocation;
    private long mSteps;
    private float mTime;

    public SlotWalk(double aAltitude, float aDistance, LatLng aLocation, long aSteps, float aTime){
        mAltitude=aAltitude;
        mDistance=aDistance;
        mLocation=aLocation;
        mSteps=aSteps;
        mTime=aTime;
    }

    public double getAltitude(){ return mAltitude;}
    public float getDistance(){ return mDistance;}
    public LatLng getLocation(){ return mLocation;}
    public long getSteps(){ return mSteps;}
    public float getTime(){ return mTime;}

}
