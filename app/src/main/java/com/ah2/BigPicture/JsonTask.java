package com.ah2.BigPicture;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

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

    JsonTask( View gal, LayoutInflater inf, Context context)
    {
        this.gal = gal;
        this.inf =inf;
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

        List<PictureCardData> cards = Utils.getPictureDataFromjsonstring(result);
        TableLayout cardholder = gal.findViewById(R.id.gImages);
        cardholder.setShrinkAllColumns(true);
        TableRow row = new TableRow(gal.getContext());
        row.setAlpha(1);

        if (result == null) {
        }

        TextView txt = gal.findViewById(R.id.waittext);
        if (cards.size() > 0 )
           txt.setText("results: " + cards.size());
        else
            txt.setText("no results");


        int WEIDTH =2;
        for (int i = 0; i < cards.size()&&i< 50; i++) {
            row.addView(Utils.getCardViewFromPicdata(cards.get(i), inf));
            if (i % WEIDTH == 0){
                cardholder.addView(row);
                row = new TableRow(gal.getContext());
            }
        }
    }
}
