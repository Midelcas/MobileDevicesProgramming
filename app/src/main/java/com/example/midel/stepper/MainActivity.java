package com.example.midel.stepper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener{
    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<SimpleWalk> activitiesList;
    private XMLManagerWalk XMLManager;
    private final String WALKSXMLFILE = "simpleWalks.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startAnimation();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,WalkActivity.class);
                // Put as extras the coordinates string and the camera name corresponding to the currently
                // selected camera:
                //i.putExtra("simpleWalkList", activitiesList );
                startActivity(i);

                /*PASAR A PANTALLA DE PASEO*/
            }
        });

        /*LEER FICHERO DE ACTIVIDADES*/

        XMLManager = new XMLManagerWalk();
        activitiesList = new ArrayList<SimpleWalk>();


        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        listView.setChoiceMode( ListView.CHOICE_MODE_SINGLE );



        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, activitiesList);
        listView.setAdapter(adapter);
         // This configuration can be done in XML

        //listView.setEnabled(false);
    }

    @Override
    protected void onResume(){
        super.onResume();
        activitiesList.clear();
        checkWalks();
        adapter.notifyDataSetChanged();
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

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int i =0;
        String name;
        Intent intent=new Intent(MainActivity.this, StatisticsActivity.class);
        intent.putExtra("position",position);
        startActivity(intent);
        /*CAMBIAR A PANTALLA DE ESTADISTICAS*/
    }

    public void startAnimation() {
        View vi = findViewById(R.id.stepperimage);
        Animation anim = new ScaleAnimation(
                0f, 1f, // Start and end values for the X axis scaling
                0f, 1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(1000);
        vi.startAnimation(anim);

        //scaleView(v, 0f, .6f);
    }

}
