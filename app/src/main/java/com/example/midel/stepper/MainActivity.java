package com.example.midel.stepper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener{
    private ListView listView;
    private SimpleWalkAdapter adapter;
    private ArrayList<SimpleWalk> activitiesList;
    Toast toast;
    int pos;
    Intent i;
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
                goToWalkActivity();
            }
        });

        activitiesList = new ArrayList<SimpleWalk>();


        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        listView.setChoiceMode( ListView.CHOICE_MODE_SINGLE );

        adapter = new SimpleWalkAdapter(this, activitiesList);
        listView.setAdapter(adapter);
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
            FileInputStream is = XMLManager.XMLWalk.read_File(getFilesDir(), getString(R.string.WALKSXMLFILE));
            XMLManager.XMLWalk.parse_XML(is,activitiesList);
        }catch(IOException e){
            toast = Toast.makeText(this,getString(R.string.error_reading_xml),Toast.LENGTH_SHORT);
            toast.show();
        }catch(ParseException e){
            toast = Toast.makeText(this,getString(R.string.error_reading_xml),Toast.LENGTH_SHORT);
            toast.show();
        }catch(Exception e){
            toast = Toast.makeText(this,getString(R.string.error_reading_xml),Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        goToStatisticsActivity(activitiesList.get(position));
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
    }

    public void confirmRemove(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.pop_up_confirm, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        alert.setTitle(getString(R.string.remove));
        alert.setView(mView);
        ((TextView) mView.findViewById(R.id.message)).setText(getString(R.string.want_remove)+ activitiesList.get(pos).getName()+"?");
        alert
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        activitiesList.remove(pos);
                        adapter.notifyDataSetChanged();
                        try{XMLManager.XMLWalk.saveXMLToFile(XMLManager.XMLWalk.write_XML(activitiesList),getFilesDir(), getString(R.string.WALKSXMLFILE));}catch(Exception e){}
                    }
                })

                .setNegativeButton(getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {

                            }
                        });

        AlertDialog alertDialogAndroid = alert.create();
        alertDialogAndroid.show();
    }

    public class SimpleWalkAdapter extends ArrayAdapter<SimpleWalk> {
        private ArrayList<SimpleWalk> items;
        private Context mContext;

        SimpleWalkAdapter(Context context, ArrayList<SimpleWalk> activities ) {
            super( context, 0, activities );
            items = activities;
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent ) {
            //LayoutInflater inflater = getLayoutInflater();
            //View row = inflater.inflate(R.layout.country_list_item, parent, false);
            View newView = convertView;

            // This approach can be improved for performance
            if ( newView == null ) {
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                newView = inflater.inflate(R.layout.simple_walk_list, parent, false);
                ImageButton btn = (ImageButton)newView.findViewById(R.id.btn);
                btn.setTag(position);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pos = (Integer) view.getTag();
                        confirmRemove();
                    }
                });
            }
            //-----

            TextView name = (TextView) newView.findViewById(R.id.name);
            TextView date = (TextView) newView.findViewById(R.id.date);

            SimpleWalk simpleWalk = items.get(position);
            SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.dateformat), Locale.ENGLISH);
            String walkDate = formatter.format(simpleWalk.getDate());

            name.setText(simpleWalk.getName());
            date.setText(walkDate);
            return newView;
        }

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    private void goToWalkActivity(){
        i = new Intent(MainActivity.this,WalkActivity.class);
        i.putExtra(getString(R.string.simpleWalk), activitiesList );
        startActivity(i);
        finish();
    }

    private void goToStatisticsActivity(SimpleWalk simple){
        i = new Intent(MainActivity.this,StatisticsActivity.class);
        i.putExtra(getString(R.string.simpleWalk), simple);
        startActivity(i);
        finish();
    }
}
