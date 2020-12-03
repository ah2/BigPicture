package com.ah2.BigPicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
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
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class JsonTask extends AsyncTask<String, String, String> {
    WeakReference <View> galRef;
    LayoutInflater inf;

    JsonTask(View gal, LayoutInflater inf) {
        this.galRef = new WeakReference<>(gal);
        this.inf = inf;
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
            String line;

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
        View gal = galRef.get();

        TextView txt = gal.findViewById(R.id.waittext);
        List<PictureCardData> cards = Utils.getPictureDataFromjsonstring(result);
        //List<PictureCardData> cards = Utils.getPictureDataFromjsonObj(context, "search.json");
        if (result == null) {
            txt.setText(R.string.no_results);
            return;
        }

        TableLayout cardholder = gal.findViewById(R.id.gImages);
        cardholder.setShrinkAllColumns(true);
        TableRow row = new TableRow(gal.getContext());
        row.setAlpha(0);



        if (cards != null)
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

        GoogleMap map = ((MainActivity) gal.getContext()).map;
        final CameraUpdateAnimator animator = new CameraUpdateAnimator(map, maplisten);

        int WEIDTH = 3;
        for (int i = 0; i < cards.size() && i < 50; i++) {

            final PictureCardData card = cards.get(i);
            View v = Utils.getCardViewFromPicdata(card, inf);

            loadMarkerIconAndImage(card, map, (ImageView)v.findViewById(R.id.mCardImage));

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

    private void loadMarkerIconAndImage(PictureCardData card, GoogleMap map, final ImageView imageView) {
        MarkerOptions markerOptions = new MarkerOptions().position(card.location)
                .title(card.name).snippet(card.title)
                .icon(Utils.bitmapDescriptorFromVector(imageView.getContext(), R.drawable.ic_info));

        final Marker marker = map.addMarker(markerOptions);

        Glide.with(galRef.get().getContext())
                .asBitmap()
                .load(card.url)
                //.apply(RequestOptions.circleCropTransform())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, Transition<? super Bitmap> glideAnimation) {

                        imageView.setImageBitmap(bitmap);

                        bitmap = Utils.getCircularBitmap(bitmap);
                        bitmap = Utils.getResizedBitmap(bitmap, 100);
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
                        marker.setIcon(icon);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }
}

