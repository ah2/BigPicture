package com.ah2.BigPicture;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class Tab2 extends Fragment implements OnMapReadyCallback {

    LayoutInflater inflater;
    MainActivity main;
    MapView mapView;
    FrameLayout mapParent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.inflater = inflater;
        main = (MainActivity) getActivity();
        View v = inflater.inflate(R.layout.tab_2, container, false);

        mapParent = v.findViewById(R.id.mapParent);

        // Gets the MapView from the XML layout and creates it
        mapView = v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        if (ActivityCompat.checkSelfPermission(super.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(super.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.setInfoWindowAdapter(new MapInfoWindowAdapter());
        main.setMap(googleMap);

        // Updates the location and zoom of the MapView
        /*CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
        map.animateCamera(cameraUpdate);*/
        LatLng sharjah = new LatLng(25.28D, 55.47D);
        googleMap.addMarker((new MarkerOptions()).position(sharjah).title("Marker in Sharjah"));
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(sharjah));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sharjah, 12.0f));
        //map.moveCamera(CameraUpdateFactory.newLatLng(sharjah));

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        public MapInfoWindowAdapter() {

        }

        @Override
        public View getInfoWindow(Marker marker) {
            // Getting view from the layout file
            PictureCardData card = (PictureCardData) marker.getTag();
            //Toast.makeText(main.getContext(), "marker tag: " + card.getId(), Toast.LENGTH_LONG).show();
            //marker.setIcon(Utils.bitmapDescriptorFromVector(context, R.drawable.ic_lightbulb));
            return Utils.getCardViewFromPicdata((PictureCardData) marker.getTag(), inflater, true);
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

    }
}
