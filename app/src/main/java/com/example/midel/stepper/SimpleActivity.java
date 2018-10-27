package com.example.midel.stepper;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SimpleActivity {
    private int mTotalTime;
    private ArrayList<LatLng> mRouteList;
    private String mName;
    private int mSteps;

    public SimpleActivity(String aName){
        mName = aName;
        mTotalTime = 0;
        mRouteList = new ArrayList<LatLng>();
        mSteps = 0;
    }

    public String getName(){ return mName;}
    public int getTotalTime(){ return mTotalTime;}
    public int getSteps(){ return mSteps;}
    public ArrayList<LatLng> getmRouteList() { return mRouteList;}

    public void setTotalTime(int aTotalTime){ mTotalTime=aTotalTime; }
    public void setSteps(int aSteps){ mSteps = aSteps;}
    public void setRouteList(ArrayList<LatLng> aRouteList){ mRouteList = aRouteList;}

    public String toString(){
        return mName;
    }
}
