package com.example.midel.stepper;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.list_fragment, container,false);
        SimpleWalk simpleWalk = (SimpleWalk) getArguments().getSerializable(getString(R.string.simpleWalk));
        ArrayList<StatisticItem> statsList;
        ListView lv = (ListView)rootView.findViewById(R.id.listView2);
        lv.setChoiceMode(lv.CHOICE_MODE_SINGLE);

        statsList= new ArrayList<StatisticItem>();
        statsList.add(new StatisticItem(simpleWalk.getMinimumAtitude(), getString(R.string.minimum_altitude),StatisticItem.METER));
        statsList.add(new StatisticItem(simpleWalk.getMaximumAtitude(), getString(R.string.maximum_altitude),StatisticItem.METER));
        statsList.add(new StatisticItem(simpleWalk.getMinSpeed(), getString(R.string.minimum_speed),StatisticItem.KMH));
        statsList.add(new StatisticItem(simpleWalk.getMaxSpeed(), getString(R.string.maximum_speed),StatisticItem.KMH));
        statsList.add(new StatisticItem(simpleWalk.getMeanSpeed(), getString(R.string.mean_speed),StatisticItem.KMH));
        statsList.add(new StatisticItem(simpleWalk.getTotalDistance(), getString(R.string.total_distance),StatisticItem.METER));
        statsList.add(new StatisticItem(simpleWalk.getTotalSteps(), getString(R.string.total_steps),StatisticItem.NUMBER));
        statsList.add(new StatisticItem(simpleWalk.getTotalTime(), getString(R.string.total_time),StatisticItem.TIME));

        ActivitiesArrayAdapter activitiesArrayAdapter = new ActivitiesArrayAdapter(getActivity(), statsList);
        lv.setAdapter(activitiesArrayAdapter);

        return rootView;
    }



    public class StatisticItem {
        private Object mValue;
        private String mName;
        private int mType;
        public static final int METER=0;
        public static final int KMH=1;
        public static final int NUMBER=2;
        public static final int TIME=3;


        public StatisticItem(Object aValue, String aName, int aType){
            mValue=aValue;
            mName=aName;
            mType=aType;
        }
        public Object getValue(){
            return mValue;
        }
        public String getName(){
            return mName;
        }
        public int getType(){
            return mType;
        }
    }

    class ActivitiesArrayAdapter extends  ArrayAdapter <StatisticItem>{
        private ArrayList<StatisticItem> items;
        private Context mContext;

        ActivitiesArrayAdapter(Context context, ArrayList<StatisticItem> activities){
            super(context, 0, activities);
            items=activities;
            mContext=context;
        }

        @Override
        public View getView (int position, View convertView, ViewGroup parent){
            View newView = convertView;
            if(newView==null){
                LayoutInflater inflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                newView=inflater.inflate(R.layout.statistics_item_list, parent, false);
            }
            TextView name =(TextView)newView.findViewById(R.id.tvactivity_name);
            TextView result=(TextView)newView.findViewById(R.id.tvactivity_result);

            if(position%2==1){
                newView.setBackgroundColor(Color.parseColor("#a00088cc"));
                name.setTextColor(Color.WHITE);
                result.setTextColor(Color.WHITE);
            }

            StatisticItem statisticItem=items.get(position);
            name.setText(statisticItem.getName());

            switch(statisticItem.getType()) {
                case StatisticItem.METER:
                    result.setText(String.format(String.format(getString(R.string.distance_format), (Double)statisticItem.getValue())));
                    break;
                case StatisticItem.KMH:
                    result.setText(String.format(String.format(getString(R.string.speed_format), (Double)statisticItem.getValue())));
                    break;
                case StatisticItem.NUMBER:
                    result.setText(String.format(String.format(getString(R.string.count_format), (long)statisticItem.getValue())));
                    break;
                case StatisticItem.TIME:
                    float secs= (float)statisticItem.getValue();
                    int sec= (int)secs%60;
                    int minutes = (int)secs/60;
                    int hours = (int)minutes/60;
                    minutes = minutes - hours*60;
                    result.setText(String.format(getString(R.string.time_format),hours, minutes,sec));
                    break;

            }
            return newView;
        }
    }
}