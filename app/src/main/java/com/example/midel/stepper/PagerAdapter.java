package com.example.midel.stepper;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    Bundle data;
    public PagerAdapter(FragmentManager fm, int NumOfTabs, SimpleWalk simpleWalk) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        data = new Bundle();

        data.putSerializable("simpleWalk", simpleWalk);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ListFragment tab1 = new ListFragment();
                tab1.setArguments(data);
                return tab1;
            case 1:
                ChartFragment tab2 = new ChartFragment();
                tab2.setArguments(data);
                return tab2;
            case 2:
                MapFragment tab3 = new MapFragment();
                tab3.setArguments(data);
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}