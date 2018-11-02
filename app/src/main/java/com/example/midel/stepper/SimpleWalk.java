package com.example.midel.stepper;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SimpleWalk implements Serializable {
    private Date mDate;
    private String mName;
    private long mTotalSteps;
    private float mTotalTime;
    private float mTotalDistance;
    private double mInitialAltitude;
    private double mMaxAltitude;
    private double mMinAltitude;
    private double mEndingAltitude;
    private double mInitialLongitude;
    private double mInitialLatitude;
    private double mEndingLongitude;
    private double mEndingLatitude;
    private ArrayList<SlotWalk> mRouteList;



    public SimpleWalk(String aName, Date aDate){
        mName = aName;
        mRouteList = new ArrayList<SlotWalk>();
        mInitialAltitude = 0;
        mInitialLongitude = 0;
        mInitialLatitude = 0;
        mEndingLongitude = 0;
        mEndingLatitude = 0;
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
    public double getTotalDistance(){ return mTotalDistance;}
    public double getEndingLongitude() { return mEndingLongitude;}
    public double getEndingLatitude() { return mEndingLatitude;}
    public double getInitialLongitude() { return mInitialLongitude;}
    public double getInitialLatitude() { return mInitialLatitude;}
    public ArrayList<SlotWalk> getRouteList() { return mRouteList;}

    public void startWalk(SlotWalk aSlot){
        mInitialAltitude = aSlot.getAltitude();
        mInitialLatitude = aSlot.getLatitude();
        mInitialLongitude = aSlot.getLongitude();
        mMinAltitude = aSlot.getAltitude();
        mMaxAltitude = aSlot.getAltitude();
        addSlot(aSlot);
    }
    public void endWalk(SlotWalk aSlot){
        mEndingLatitude = aSlot.getLatitude();
        mEndingLongitude = aSlot.getLongitude();
        mEndingAltitude = aSlot.getAltitude();
        addSlot(aSlot);
    }
    public void incrementTime(float aTime){ mTotalTime+=aTime; }
    public void incrementSteps(long aSteps){ mTotalSteps += aSteps;}
    public void incrementDistance(float aDistance){ mTotalDistance+= aDistance;}
    private void setMaxAltitude(double aAltitude){
        if(aAltitude>mMaxAltitude)
            mMaxAltitude = aAltitude;
    }
    private void setMinAltitude(double aAltitude){
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
