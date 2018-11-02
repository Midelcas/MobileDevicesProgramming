package com.example.midel.stepper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback{
    SimpleWalk simpleWalk;
    MapView mMap;
    private GoogleMap googleMap;
    RadioGroup radGrp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        simpleWalk = (SimpleWalk) getArguments().getSerializable("simpleWalk");
        View rootView = inflater.inflate(R.layout.map_fragment, container, false);
        radGrp = (RadioGroup)rootView.findViewById(R.id.grupoRadioMapType);


        mMap = (MapView) rootView.findViewById(R.id.mapView);
        mMap.onCreate(savedInstanceState);

        mMap.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMap.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap gooMap) {
        googleMap = gooMap;

        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        LatLng start = new LatLng(
                simpleWalk.getRouteList().get(0).getLongitude(),
                simpleWalk.getRouteList().get(0).getLatitude());
        LatLng finish = new LatLng(
                simpleWalk.getRouteList().get((simpleWalk.getRouteList().size()-1)).getLongitude(),
                simpleWalk.getRouteList().get((simpleWalk.getRouteList().size()-1)).getLatitude());
        Marker mk = googleMap.addMarker(new MarkerOptions().position(start).title("Start"));
        mk.showInfoWindow(); // Shows the name of the camera in the marker
        Marker mk1 = googleMap.addMarker(new MarkerOptions().position(finish).title("Finish"));
        mk1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.finish_run));
        mk1.showInfoWindow();

        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(start));
        //googleMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );
        List<Marker> markersList = new ArrayList<Marker>();
        markersList.add(mk);
        markersList.add(mk1);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker m : markersList) {
            builder.include(m.getPosition());
        }

//Bounds padding here
        int padding = 100;

        //Create bounds here
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        PolylineOptions pLoptions = new PolylineOptions().color(0xFF0000FF);
        for(int i=0; i< simpleWalk.getRouteList().size(); i++){
            pLoptions.add(new LatLng(simpleWalk.getRouteList().get(i).getLongitude(),
                    simpleWalk.getRouteList().get(i).getLatitude()));
        }
        Polyline polyline = googleMap.addPolyline(pLoptions);
        googleMap.animateCamera(cu);
        radGrp.setOnCheckedChangeListener(new radioGroupCheckedChanged() );
    }

    class radioGroupCheckedChanged implements RadioGroup.OnCheckedChangeListener {
        public void onCheckedChanged(RadioGroup arg0, int id) {
            switch (id)
            {
                case R.id.typeMap:
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    break;
                case R.id.typeSatellite:
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    break;
                case R.id.typeHybrid:
                    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    break;
            }
        }
    }
}