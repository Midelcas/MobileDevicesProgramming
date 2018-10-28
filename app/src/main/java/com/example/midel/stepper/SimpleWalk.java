package com.example.midel.stepper;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SimpleWalk {
    private Date mDate;
    private String mName;
    private long mTotalSteps;
    private float mTotalTime;
    private float mTotalDistance;
    private float mInitialAltitude;
    private float mMaxAltitude;
    private float mMinAltitude;
    private float mEndingAltitude;
    private LatLng mInitialLocation;
    private LatLng mEndingLocation;
    private ArrayList<SlotWalk> mRouteList;



    public SimpleWalk(String aName, Date aDate){
        mName = aName;
        mRouteList = new ArrayList<SlotWalk>();
        mInitialAltitude = 0;
        mInitialLocation = null;
        mEndingLocation = null;
        mTotalTime = 0;
        mTotalSteps = 0;
        mTotalDistance = 0;

        mMaxAltitude = 0;
        mMinAltitude = 0;
        mEndingAltitude = 0;
        if(aDate == null) {
            mDate = Calendar.getInstance().getTime();
        }else{
            mDate = aDate;
        }
    }


    public String getName(){ return mName;}
    public Date getDate(){ return mDate;}
    public float getTotalTime(){ return mTotalTime;}
    public long getTotalSteps(){ return mTotalSteps;}
    public float getTotalDistance(){ return mTotalDistance;}
    public LatLng getEndingLocation() { return mEndingLocation;}
    public LatLng getInitialLocation() { return mInitialLocation;}
    public ArrayList<SlotWalk> getRouteList() { return mRouteList;}

    public void startWalk(SlotWalk aSlot){
        mInitialAltitude = aSlot.getAltitude();
        mInitialLocation = aSlot.getLocation();
    }
    public void endWalk(SlotWalk aSlot){
        mEndingLocation = aSlot.getLocation();
        mEndingAltitude = aSlot.getAltitude();
        addSlot(aSlot);
    }
    public void incrementTime(float aTime){ mTotalTime+=aTime; }
    public void incrementSteps(long aSteps){ mTotalSteps += aSteps;}
    public void incrementDistance(float aDistance){ mTotalDistance+= aDistance;}
    private void setMaxAltitude(float aAltitude){
        if(aAltitude>mMaxAltitude)
            mMaxAltitude = aAltitude;
    }
    private void setMinAltitude(float aAltitude){
        if(aAltitude<mMinAltitude)
            mMinAltitude = aAltitude;
    }
    public void addSlot(SlotWalk aSlot){
        mRouteList.add(aSlot);

        setMaxAltitude(aSlot.getAltitude());
        setMinAltitude(aSlot.getAltitude());
        incrementDistance(aSlot.getDistance());
        incrementSteps(aSlot.getSteps());
        incrementTime(aSlot.getTime());
    }

    public String toString(){
        return mDate.toString() + "\n" +mName;
    }
}
