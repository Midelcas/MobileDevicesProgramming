package com.example.midel.stepper;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class SlotWalk implements Serializable {
    private double mAltitude;
    private float mDistance;
    private double mLongitude;
    private double mLatitude;
    private long mSteps;
    private float mTime;

    public SlotWalk(double aAltitude, float aDistance, double aLongitude, double aLatitude, long aSteps, float aTime){
        mAltitude=aAltitude;
        mDistance=aDistance;
        mLongitude = aLongitude;
        mLatitude = aLatitude;
        mSteps=aSteps;
        mTime=aTime;
    }

    public double getAltitude(){ return mAltitude;}
    public float getDistance(){ return mDistance;}
    public double getLongitude(){ return mLongitude;}
    public double getLatitude(){ return mLatitude;}
    public long getSteps(){ return mSteps;}
    public float getTime(){ return mTime;}

}
