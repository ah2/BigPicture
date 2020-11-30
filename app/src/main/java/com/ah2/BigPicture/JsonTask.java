package com.ah2.BigPicture;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

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
        if (result == null)
            result = Utils.getstringfromfile(context, "search.json");

        List<PictureCardData> cards = Utils.getPictureDataFromjsonstring(result);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 600);  // width, height
        int margin = Utils.dpToPixel(8, gal.getResources().getDisplayMetrics().density);
        layoutParams.setMargins(margin, margin, margin, margin);

        TableLayout cardholder = gal.findViewById(R.id.gImages);

        TableRow row = new TableRow(gal.getContext());

        int WEIDTH =2;
        for (int i = 0; i < cards.size(); i++) {
            row.addView(Utils.getCardViewFromPicdata(cards.get(i), inf));
            if (i % WEIDTH == 0){
                cardholder.addView(row);
                row = new TableRow(gal.getContext());
            }
        }
    }
}
