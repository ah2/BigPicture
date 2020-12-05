package com.ah2.BigPicture;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final LayoutInflater inflater;

    public MapInfoWindowAdapter(Context context){
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // Getting view from the layout file
        PictureCardData card = (PictureCardData) marker.getTag();
        Toast.makeText(inflater.getContext(), "marker tag: " + card.getId(), Toast.LENGTH_LONG).show();
        //marker.setIcon(Utils.bitmapDescriptorFromVector(context, R.drawable.ic_lightbulb));
        return Utils.getCardViewFromPicdata((PictureCardData) marker.getTag(), inflater, false);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
