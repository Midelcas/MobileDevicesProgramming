package com.example.midel.stepper;

import com.google.android.gms.maps.model.LatLng;

public class SlotWalk {
    private float mAltitude;
    private float mDistance;
    private LatLng mLocation;
    private long mSteps;
    private float mTime;

    public SlotWalk(float aAltitude, float aDistance, LatLng aLocation, long aSteps, float aTime){
        mAltitude=aAltitude;
        mDistance=aDistance;
        mLocation=aLocation;
        mSteps=aSteps;
        mTime=aTime;
    }

    public float getAltitude(){ return mAltitude;}
    public float getDistance(){ return mDistance;}
    public LatLng getLocation(){ return mLocation;}
    public long getSteps(){ return mSteps;}
    public float getTime(){ return mTime;}

}
