package com.example.midel.stepper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.utils.FileUtils;

public class ChartFragment extends Fragment {
    private List<Entry> entries = new ArrayList<Entry>();
    private LineChart lChart;
    private LineDataSet dataSet;
    private LineData lineData;
    private int xIndex = 0;

    private List<Entry> entries2 = new ArrayList<Entry>();
    private LineChart lChart2;
    private LineDataSet dataSet2;
    private LineData lineData2;
    private int xIndex2 = 0;

    private List<Entry> entries3 = new ArrayList<Entry>();
    private LineChart lChart3;
    private LineDataSet dataSet3;
    private LineData lineData3;
    SimpleWalk simpleWalk;
    ArrayList<SlotWalk> registrosSlotWalk;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        simpleWalk = (SimpleWalk) getArguments().getSerializable("simpleWalk");
        View rootView = inflater.inflate(R.layout.chart_fragment, container, false);
        lChart=(LineChart)rootView.findViewById(R.id.chart);
        lChart2=(LineChart)rootView.findViewById(R.id.chart2);
        lChart3=(LineChart)rootView.findViewById(R.id.chart3);

        registrosSlotWalk= new ArrayList<SlotWalk>();

        long totalSteps=simpleWalk.getTotalSteps();
        float totalTime=0;
        double totalDistance=simpleWalk.getTotalDistance();


        registrosSlotWalk= simpleWalk.getRouteList();

        int size=registrosSlotWalk.size();

        float registroPasos [] = new float[size];
        float registroTiempo [] = new float[size];
        float registroVelocidad [] = new float[size];

        for(int j=1;j<registrosSlotWalk.size();j++){
            registroPasos[j]= registrosSlotWalk.get(j).getSteps();
            registroTiempo[j] = (registrosSlotWalk.get(j).getTime())/60;
            if((registrosSlotWalk.get(j).getSteps()==0)|(registrosSlotWalk.get(j).getTime()==0)){
                registroPasos[j]=0;
            }
            else {
                registroPasos[j] = registroPasos[j] / (registrosSlotWalk.get(j).getTime());
                registroPasos[j] = registroPasos[j];

            }
        }

        entries.add(new Entry((long)(xIndex++),0));
        dataSet=new LineDataSet(entries, "pasos/minuto");
        lChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        entries2.add(new Entry((long)(xIndex2++),0));
        dataSet2=new LineDataSet (entries2,"altura/segundo");
        lChart2.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        entries3.add(new Entry((long)(xIndex++),0));
        dataSet3=new LineDataSet (entries3,"km/h");
        lChart3.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        lineData=new LineData(dataSet);
        lChart.setData(lineData);

        lineData2=new LineData (dataSet2);
        lChart2.setData(lineData2);

        lineData3=new LineData (dataSet3);
        lChart3.setData(lineData3);

        float incrementoT;
        float incrementoT1=registroTiempo[0];

        totalTime=0;
        for (int j=0;j<registroPasos.length;j++){
            totalTime=totalTime+registroTiempo[j];
        }

        for(xIndex=1; xIndex<(registroPasos.length); xIndex++){
            incrementoT=((registroTiempo[xIndex]+incrementoT1));
            dataSet.addEntry(new Entry (incrementoT, registroPasos[xIndex]));
            dataSet.setDrawValues(false);
            incrementoT1=incrementoT;
        }

        for(xIndex=0; xIndex<(registrosSlotWalk.size()); xIndex++){
            registroVelocidad[xIndex]= (float) (((registrosSlotWalk.get(xIndex).getDistance())/(registrosSlotWalk.get(xIndex).getTime()))*3.6);
            if(registrosSlotWalk.get(xIndex).getTime()==0){
                registroVelocidad[xIndex]=0;
                //dataSet3.addEntry(new Entry(xIndex, (int) registroVelocidad[xIndex]));
            }
            else {
                dataSet3.addEntry(new Entry(xIndex, (int) registroVelocidad[xIndex]));
                dataSet3.setDrawValues(false);
            }
        }


        int tamaño=registrosSlotWalk.size();
        for(xIndex2=1; xIndex2<tamaño; xIndex2++){
            dataSet2.addEntry(new Entry ( xIndex2, (int)registrosSlotWalk.get(xIndex2).getAltitude()));
            dataSet2.setDrawValues(false);

        }

        lineData.notifyDataChanged();
        float maxPasos = lineData.getYMax();
        float minPasos = lineData.getYMin();
        lChart.notifyDataSetChanged();
        lChart.invalidate(); // refresh


        lineData2.notifyDataChanged();
        lChart2.notifyDataSetChanged();
        lChart2.invalidate(); // refresh

        lineData3.notifyDataChanged();
        lChart3.notifyDataSetChanged();
        lChart3.invalidate(); // refresh

        TextView tvmaxPasos;
        tvmaxPasos=rootView.findViewById(R.id.tvmaxpasos);
        String smaxpasos=Float.toString(lineData.getYMax());
        tvmaxPasos.setText(smaxpasos);

        TextView tvminPasos;
        tvminPasos=rootView.findViewById(R.id.tvminpasos);
        String sminpasos=Float.toString(lineData.getYMin());
        tvminPasos.setText(sminpasos);

        TextView tvmaxh;
        tvmaxh=rootView.findViewById(R.id.tvmaxh);
        String smaxH=Float.toString(lineData2.getYMax());
        tvmaxh.setText(smaxH);

        TextView tvminh;
        tvminPasos=rootView.findViewById(R.id.tvminh);
        String sminH=Float.toString(lineData2.getYMin());
        tvminPasos.setText(sminH);

/*
        TextView tvtotalSteps, tvtotalTime, tvtotalDistance;
        tvtotalSteps=(TextView)findViewById(R.id.tvtotalSteps);
        String stotalSteps=Long.toString(totalSteps);
        tvName.setText(stotalSteps);

        tvtotalTime=(TextView)findViewById(R.id.tvtotalTime);
        String stotalTime=Float.toString(totalTime);
        tvtotalTime.setText(stotalTime);

        tvtotalDistance=(TextView)findViewById(R.id.tvtotalDistance);
        String stotalDistance=Double.toString(totalDistance);
        tvtotalDistance.setText(stotalDistance);
  */
        return rootView;
    }
}