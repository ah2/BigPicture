package com.ah2.BigPicture;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

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

    LoadJsonTask(View gal, LayoutInflater inf, boolean loadFlickr) {
        this.galRef = new WeakReference<>(gal);
        this.inf = inf;
        this.loadFlickr = loadFlickr;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        galRef.get().findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
        //Toast.makeText(((View) galRef.get()).getContext(), "started tags", Toast.LENGTH_LONG).show();

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
        //Toast.makeText(((View)galRef.get()).getContext(), "tags done", Toast.LENGTH_LONG).show();
        final View gal = galRef.get();
        TextView txt = gal.findViewById(R.id.waittext);

        //List<PictureCardData> cards = Utils.getPictureDataFromjsonObj(context, "search.json");
        if (result == null) {
            txt.setText(R.string.no_results);
            //return;
            result = Utils.getstringfromfile(gal.getContext(), "search.json");
        }
        List<PictureCardData> cards;
        if (!loadFlickr)
            cards = Utils.getPictureDataFromjsonstring(result);
        else
            cards = Utils.getPictureDataFromjsonstringFlikr(result);

        if (cards.size() > 0)
            Toast.makeText(gal.getContext(), "Found: " + cards.size() + "results", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(gal.getContext(), "No results", Toast.LENGTH_LONG).show();

        LatLng sharjah = new LatLng(25.28D, 55.47D);

        if (!loadFlickr)
            Collections.sort(cards, new Sortbyloc(sharjah));

        GoogleMap map = ((MainActivity) gal.getContext()).getMap();
        final GoogleMap.OnCameraIdleListener maplisten = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                //get latlng at the center by calling
                //LatLng midLatLng = mMap.getCameraPosition().target;
            }
        };

        final FrameLayout scrollparent = gal.findViewById(R.id.scrollparent);
        scrollparent.removeAllViews();

        int perPage = gal.getResources().getInteger(R.integer.perPage);
        PictureCardData[] cardsArr = new PictureCardData[cards.size()];

        for (int i = 0; i < cardsArr.length; i++)
            cardsArr[i] = cards.get(i);

        RecyclerView recyclerView = (RecyclerView) inf.inflate(R.layout.recyler_layout, null);
        MyListAdapter<PictureCardData> adapter = new MyListAdapter<>(cardsArr, inf, map, scrollparent);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new GridLayoutManager(gal.getContext(), 3));
        recyclerView.setAdapter(adapter);

        scrollparent.addView(recyclerView);

        gal.findViewById(R.id.loading_progress).setVisibility(View.GONE);
        //Toast.makeText(gal.getContext(), "found: " + cards.size() + " results", Toast.LENGTH_LONG).show();
    }

}

