package com.example.midel.stepper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.LineData;



public class StatisticsActivity extends AppCompatActivity {

    private ArrayList<SimpleWalk> activitiesList;
    private ArrayList<SlotWalk> registrosSlotWalk;
    private XMLManagerWalk XMLManager;
    private final String WALKSXMLFILE = "simpleWalks.xml";
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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        lChart=(LineChart)findViewById(R.id.chart);
        lChart2=(LineChart)findViewById(R.id.chart2);

        Intent i=getIntent();

        int position= i.getIntExtra("position", 0);
        XMLManager = new XMLManagerWalk();
        activitiesList = new ArrayList<SimpleWalk>();
        registrosSlotWalk= new ArrayList<SlotWalk>();
        SimpleWalk kk=null;


        checkWalks();
        kk=activitiesList.get(position);

        String name=kk.getName();
        long totalSteps=kk.getTotalSteps();
        float totalTime=0;
        double totalDistance=kk.getTotalDistance();


        registrosSlotWalk= kk.getRouteList();

        int size=registrosSlotWalk.size();

        float registroPasos [] = new float[size];
        float registroTiempo [] = new float[size];

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

        lineData=new LineData(dataSet);
        lChart.setData(lineData);

        lineData2=new LineData (dataSet2);
        lChart2.setData(lineData2);

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

/**/
        int tamaño=registrosSlotWalk.size();
        for(xIndex2=1; xIndex2<tamaño; xIndex2++){
            dataSet2.addEntry(new Entry ( xIndex2, (int)registrosSlotWalk.get(xIndex2).getAltitude()));
            dataSet2.setDrawValues(false);

        }
/**/
        lineData.notifyDataChanged();
        lineData.getYMax();
        lineData.getYMin();
        lChart.notifyDataSetChanged();
        lChart.invalidate(); // refresh


        lineData2.notifyDataChanged();
        lChart2.notifyDataSetChanged();
        lChart2.invalidate(); // refresh



        TextView tvName=(TextView)findViewById(R.id.tvname);
        tvName.setText(name);


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
    }



    public void checkWalks(){
        try {
            FileInputStream is = XMLManager.read_File(getFilesDir(), WALKSXMLFILE);
            XMLManager.parse_XML(is,activitiesList);
        }catch(IOException e){
            Toast toast;
            toast = Toast.makeText(this,"Error while reading xml",Toast.LENGTH_SHORT);
            toast.show();
        }catch(ParseException e){
            Toast toast;
            toast = Toast.makeText(this,"Error while parsing xml",Toast.LENGTH_SHORT);
            toast.show();
        }catch(Exception e){
            Toast toast;
            toast = Toast.makeText(this,"Error while parsing xml",Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
