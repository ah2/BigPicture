package com.ah2.BigPicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class LoadJsonTask extends AsyncTask<String, String, String> {
    WeakReference<View> galRef;
    LayoutInflater inf;
    boolean loadFlickr;
    String TagStr;

    LoadJsonTask(View gal, LayoutInflater inf, boolean loadFlickr) {
        this.galRef = new WeakReference<>(gal);
        this.inf = inf;
        this.loadFlickr = loadFlickr;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        galRef.get().findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
        Toast.makeText(((View)galRef.get()).getContext(), "started tags", Toast.LENGTH_LONG).show();

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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Toast.makeText(((View)galRef.get()).getContext(), "tags done", Toast.LENGTH_LONG).show();
        final View gal = galRef.get();
        TextView txt = gal.findViewById(R.id.waittext);

        //List<PictureCardData> cards = Utils.getPictureDataFromjsonObj(context, "search.json");
        if (result == null) {
            txt.setText(R.string.no_results);
            //return;
            result = Utils.getstringfromfile(gal.getContext(), "search.json");
        }
        List<PictureCardData> cards;
        if(!loadFlickr)
            cards = Utils.getPictureDataFromjsonstring(result);
        else
            cards = Utils.getPictureDataFromjsonstringFlikr(result);


        Toast.makeText(gal.getContext(), "loaded: " + cards.size(), Toast.LENGTH_LONG).show();

        LatLng sharjah = new LatLng(25.28D, 55.47D);
        if (!loadFlickr)
            Collections.sort(cards, new Sortbyloc(sharjah));

        TableLayout cardholder = gal.findViewById(R.id.gImages);
        cardholder.removeAllViews();
        cardholder.setShrinkAllColumns(true);
        TableRow row = new TableRow(gal.getContext());
        row.setAlpha(0);

        if (cards != null)
            if (cards.size() > 0) {
                //txt.setText("results: " + cards.size());
                //txt.setVisibility(View.VISIBLE);
            } else {
                txt.setText(R.string.no_results);
                txt.setVisibility(View.VISIBLE);
            }

        final GoogleMap.OnCameraIdleListener maplisten = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                //get latlng at the center by calling
                //LatLng midLatLng = mMap.getCameraPosition().target;
            }
        };

        GoogleMap map = ((MainActivity) gal.getContext()).getMap();
        while (map == null) {
            try {
                Toast.makeText(gal.getContext(),"map resource was null", Toast.LENGTH_LONG).show();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            map = ((MainActivity) gal.getContext()).getMap();
        }

        final CameraUpdateAnimator animator = new CameraUpdateAnimator(map, maplisten);

        int WEIDTH = 3;
        for (int i = 0; i < cards.size(); i++) {
            final PictureCardData card = cards.get(i);

            //txt.setText(txt.getText()+ "\n" + card.toString());
            View v = Utils.getCardViewFromPicdata(card, inf, false);

            loadMarkerIconAndImage(card, map, (ImageView) v.findViewById(R.id.mCardImage));

            final FrameLayout scrollparent = gal.findViewById(R.id.scrollparent);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View i = Utils.getCardViewFromPicdata(card, inf,  true);
                    i.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //scrollparent.removeAllViews();
                            i.setVisibility(View.GONE);
                        }
                    });
                    scrollparent.addView(i);

                    //ViewPager viewPager = (ViewPager) ((MainActivity) gal.getContext()).getParent().findViewById(R.id.tabs);
                    //TabFragment.goToTab(1);
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
        gal.findViewById(R.id.loading_progress).setVisibility(View.GONE);
        //gal.findViewById(R.id.waittext).setVisibility(View.GONE);
        //((ScrollView)gal.findViewById(R.id.scrollView)).fullScroll(ScrollView.FOCUS_UP);
        Toast.makeText(gal.getContext(), "found: " + cards.size()+ " results", Toast.LENGTH_LONG).show();
    }

    private void loadMarkerIconAndImage(final PictureCardData card, GoogleMap map, final ImageView imageView) {
        MarkerOptions markerOptions = new MarkerOptions().position(card.location)
                .title(card.name).snippet(card.title)
                .icon(Utils.bitmapDescriptorFromVector(imageView.getContext(), R.drawable.ic_info));
        final Marker marker = map.addMarker(markerOptions);
        marker.setTag(card);

        Glide.with(galRef.get().getContext())
                .asBitmap()
                .load(card.url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, Transition<? super Bitmap> glideAnimation) {

                        imageView.setImageBitmap(bitmap);

                        bitmap = Utils.getCircularBitmap(bitmap);
                        bitmap = Utils.getResizedBitmap(bitmap, 100);

                        //Bitmap b = Bitmap.createBitmap( imageView.getLayoutParams().width, imageView.getLayoutParams().height, Bitmap.Config.ARGB_8888);
                        //Canvas c = new Canvas(b);
                        //imageView.layout(imageView.getLeft(), imageView.getTop(), imageView.getRight(), imageView.getBottom());
                        //imageView.draw(c);

                        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
                        marker.setIcon(icon);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }
}

