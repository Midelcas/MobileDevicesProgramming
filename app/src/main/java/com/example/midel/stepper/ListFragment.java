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

    ListView lv;
    SimpleWalk simpleWalk;
    double[] array_values=null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SimpleWalk simpleWalk = (SimpleWalk) getArguments().getSerializable(getString(R.string.simpleWalk));
        ArrayList<SlotWalk> slotWalkList= simpleWalk.getRouteList();

        View rootView=inflater.inflate(R.layout.list_fragment, container,false);

        super.onCreate(savedInstanceState);
        lv = (ListView)rootView.findViewById(R.id.listView2);
        lv.setChoiceMode(lv.CHOICE_MODE_SINGLE);
        array_values = valuesCalculator(simpleWalk);
        ActivityData activityData = new ActivityData();
        //array_values = valuesCalculator(simpleWalk);
        ActivitiesArrayAdapter activitiesArrayAdapter = new ActivitiesArrayAdapter(getActivity(), activityData.getActivityDataList());
        //array_values = valuesCalculator(simpleWalk);
        lv.setAdapter(activitiesArrayAdapter);

        return rootView;

    }

    private double [] valuesCalculator (SimpleWalk simpleWalk){
        double maxSpeed=0;
        double minSpeed=100;
        double meanSpeed=0;
        double [] valores={0,0,0,0,0,0,0,0};

        double currentSpeed=0;
        for(int i=0; i<simpleWalk.getRouteList().size();i++){
            if(simpleWalk.getRouteList().get(i).getTime()!=0){
                currentSpeed=(simpleWalk.getRouteList().get(i).getDistance())/(simpleWalk.getRouteList().get(i).getTime());
            }
            if(currentSpeed>maxSpeed){
                maxSpeed=currentSpeed;
            }
            if(currentSpeed<minSpeed){
                minSpeed=currentSpeed;
            }
        }
        meanSpeed=3.6*simpleWalk.getTotalDistance()/simpleWalk.getTotalTime();
        maxSpeed*=3.6;
        minSpeed*=3.6;

        valores [0] = simpleWalk.getMinimumAtitude();
        valores [1] = simpleWalk.getMaximumAtitude();
        valores [2] = minSpeed;
        valores [3] = maxSpeed;
        valores [4] = meanSpeed;
        valores [5] = simpleWalk.getTotalDistance();
        valores [6] = simpleWalk.getTotalSteps();
        valores [7] = simpleWalk.getTotalTime();

        return valores;
    }


    public class Activity {
        private Double result;
        private String name;

        public Activity (Double res, String n){
            result=res;
            name=n;
        }
        public Double getResult(){
            return result;
        }
        public String getName(){
            return name;
        }
        public String toString(){
            return name;
        }
    }
    public class ActivityData{

        private String [] activities_names={
                "Minimum Altitude (m)",
                "Maximum Altitude (m)",
                "Minimum Speed (km/h)",
                "Maximum Speed (km/h)",
                "Mean Speed    (km/h)",
                "Total Distance   (m)",
                "Total Steps      (#)",
                "Total Time   (mm:ss)"
        };
        private double [] activities_results = array_values;

        private ArrayList<Activity> mList= new ArrayList<Activity>();

        public ActivityData(){

            for(int i=0;i<activities_names.length;i++){
                Activity activity=new Activity(activities_results[i],activities_names[i]);
                mList.add(activity);
            }
        }

        public ArrayList<Activity> getActivityDataList() {
            return mList;
        }
    }
    class ActivitiesArrayAdapter extends  ArrayAdapter <Activity>{
        private ArrayList<Activity> items;
        private Context mContext;

        ActivitiesArrayAdapter(Context context, ArrayList<Activity> activities){
            super(context, 0, activities);
            items=activities;
            mContext=context;
        }

        @Override
        public View getView (int position2, View convertView, ViewGroup parent){
            View newView = convertView;
            if(newView==null){
                LayoutInflater inflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                newView=inflater.inflate(R.layout.activity_list_item, parent, false);
            }
            TextView name =(TextView)newView.findViewById(R.id.tvactivity_name);
            TextView result=(TextView)newView.findViewById(R.id.tvactivity_result);

            Activity activity=items.get(position2);
            name.setText(activity.getName());
            if(position2%2==1){
                newView.setBackgroundColor(Color.parseColor("#a00088cc"));
                name.setTextColor(Color.WHITE);
                result.setTextColor(Color.WHITE);
            }
            if(position2==7)
            {
                double secs= activity.getResult();
                int sec= (int)secs%60;
                int minutes = (int)secs/60;
                result.setText(String.format(getString(R.string.time_format), minutes,sec));
            }else {
                result.setText(String.format("%.2f", activity.getResult()));
            }

            return newView;
        }
    }
}