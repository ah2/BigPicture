package com.ah2.BigPicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class JsonTask extends AsyncTask<String, String, String> {
    View gal;
    LayoutInflater inf;
    Context context;

    JsonTask(View gal, LayoutInflater inf, Context context) {
        this.gal = gal;
        this.inf = inf;
        this.context = context;
    }

    protected void onPreExecute() {
        super.onPreExecute();

    }

    protected String doInBackground(String... params) {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
                Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

            }
            return buffer.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        TextView txt = gal.findViewById(R.id.waittext);
        List<PictureCardData> cards = Utils.getPictureDataFromjsonstring(result);
        if (result == null) {
            txt.setText(R.string.no_results);
            return;
        }

        TableLayout cardholder = gal.findViewById(R.id.gImages);
        cardholder.setShrinkAllColumns(true);
        TableRow row = new TableRow(gal.getContext());
        row.setAlpha(0);



        if (cards == null)
            if (cards.size() > 0)
                txt.setText("results: " + cards.size());
            else
                txt.setText(R.string.no_results);

        final GoogleMap.OnCameraIdleListener maplisten = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                //get latlng at the center by calling
                //LatLng midLatLng = mMap.getCameraPosition().target;
            }
        };

        GoogleMap map = ((MainActivity) context).map;
        final CameraUpdateAnimator animator = new CameraUpdateAnimator(map, maplisten);

        int WEIDTH = 2;
        for (int i = 0; i < cards.size() && i < 50; i++) {

            final PictureCardData card = cards.get(i);
            View v = Utils.getCardViewFromPicdata(card, inf);
            loadMarkerIcon(card, map);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mMap.clear();
                    animator.add(CameraUpdateFactory.newLatLngZoom(card.location, 17), true, 1000);
                    animator.execute();
                }
            });

            row.addView(v);
            if (i % WEIDTH == 0) {
                cardholder.addView(row);
                row = new TableRow(gal.getContext());
            }
        }
    }

    private void loadMarkerIcon(PictureCardData card, GoogleMap map) {
        MarkerOptions markerOptions = new MarkerOptions().position(card.location).title(card.name).snippet(card.title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.image_not_found));
        final Marker marker = map.addMarker(markerOptions);

        Glide.with(context)
                .asBitmap()
                .load(card.url)
                .apply(RequestOptions.circleCropTransform())
                .into(new SimpleTarget<Bitmap>(100,100) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> glideAnimation) {
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
                        marker.setIcon(icon);
                    }
                });
    }
}

