package com.example.midel.stepper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ChartFragment extends Fragment {
    private List<Entry> entries = new ArrayList<Entry>();
    private LineChart lChart;
    private LineDataSet dataSet;
    private LineData lineData;
    private List<Entry> entries2 = new ArrayList<Entry>();
    private LineChart lChart2;
    private LineDataSet dataSet2;
    private LineData lineData2;

    private List<Entry> entries3 = new ArrayList<Entry>();
    private LineChart lChart3;
    private LineDataSet dataSet3;
    private LineData lineData3;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SimpleWalk simpleWalk = (SimpleWalk) getArguments().getSerializable(getString(R.string.simpleWalk));
        View rootView = inflater.inflate(R.layout.chart_fragment, container, false);
        lChart=(LineChart)rootView.findViewById(R.id.chart);
        lChart2=(LineChart)rootView.findViewById(R.id.chart2);
        lChart3=(LineChart)rootView.findViewById(R.id.chart3);

        configCharts(simpleWalk);

        ArrayList<SlotWalk> slotWalkList= simpleWalk.getRouteList();


        float incT=0;
        for(int xIndex=1;xIndex<slotWalkList.size();xIndex++){
            incT+=(slotWalkList.get(xIndex).getTime());
            dataSet.addEntry(new Entry(incT,slotWalkList.get(xIndex).getSteps()));
            dataSet.setDrawValues(false);
        }

        incT=0;
        for(int xIndex=0;xIndex<slotWalkList.size();xIndex++){
            incT+=(slotWalkList.get(xIndex).getTime());
            dataSet2.addEntry(new Entry(incT,(float)slotWalkList.get(xIndex).getAltitude()));
            dataSet2.setDrawValues(false);
        }

        incT=0;
        for(int xIndex=0;xIndex<slotWalkList.size();xIndex++){
            incT+=(slotWalkList.get(xIndex).getTime());
            dataSet3.addEntry(new Entry(incT,(float) (((slotWalkList.get(xIndex).getDistance())/(slotWalkList.get(xIndex).getTime()))*3.6)));
            dataSet3.setDrawValues(false);
        }


        lineData.notifyDataChanged();
        lChart.notifyDataSetChanged();
        lChart.invalidate(); // refresh


        lineData2.notifyDataChanged();
        lChart2.notifyDataSetChanged();
        lChart2.invalidate(); // refresh

        lineData3.notifyDataChanged();
        lChart3.notifyDataSetChanged();
        lChart3.invalidate(); // refresh


        return rootView;
    }

    private void configCharts(SimpleWalk simpleWalk){
        entries.add(new Entry(0,0));
        dataSet=new LineDataSet(entries, getString(R.string.steps) + getString(R.string.total_steps) + simpleWalk.getTotalSteps());
        lChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        XAxis xAxis1 = lChart.getXAxis();
        xAxis1.setValueFormatter(new MyXAxisValueFormatter());
        lChart.getDescription().setEnabled(false);
        lineData=new LineData(dataSet);
        lChart.setData(lineData);

        dataSet2=new LineDataSet (entries2,getString(R.string.altitude));
        lChart2.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        XAxis xAxis2 = lChart2.getXAxis();
        xAxis2.setValueFormatter(new MyXAxisValueFormatter());
        lChart2.getDescription().setEnabled(false);
        lineData2=new LineData (dataSet2);
        lChart2.setData(lineData2);


        entries3.add(new Entry(0,0));
        dataSet3=new LineDataSet (entries3,getString(R.string.speed));
        lChart3.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        XAxis xAxis3 = lChart3.getXAxis();
        xAxis3.setValueFormatter(new MyXAxisValueFormatter());
        lChart3.getDescription().setEnabled(false);
        lineData3=new LineData (dataSet3);
        lChart3.setData(lineData3);
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private DecimalFormat mFormat;

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return getTimeString(value);
        }

        private String getTimeString(float secs){
            int sec= (int)secs%60;
            int minutes = (int)secs/60;
            return String.format(getString(R.string.time_format), minutes,sec);
        }
    }
}