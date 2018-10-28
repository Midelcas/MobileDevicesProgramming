package com.example.midel.stepper;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener{
    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<SimpleWalk> activitiesList;
    private XMLManagerWalk XMLManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /*LEER FICHERO DE ACTIVIDADES*/

        XMLManager = new XMLManagerWalk();
        activitiesList = new ArrayList<SimpleWalk>();



        activitiesList.add(new SimpleWalk("primera"));
        activitiesList.add(new SimpleWalk("segunda"));
        activitiesList.add(new SimpleWalk("tercera"));



        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, activitiesList);
        listView.setAdapter(adapter);
        listView.setChoiceMode( ListView.CHOICE_MODE_SINGLE ); // This configuration can be done in XML

        listView.setEnabled(false);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

}
