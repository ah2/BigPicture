package com.ah2.BigPicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    static String getstringfromfile(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            return new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static View getCardViewFromPicdata(final PictureCardData card, LayoutInflater inflater){

        View mCard =  inflater.inflate(R.layout.matterial_picrure_card, null);
        TextView name = (TextView) mCard.findViewById(R.id.mCardname);
        TextView title = (TextView) mCard.findViewById(R.id.mCardtitle);
        ImageView image =  mCard.findViewById(R.id.mCardImage);

        name.setText(card.getName());
        title.setText(card.getTitle());
        Glide.with(inflater.getContext()).load(card.url.replace("_c.jpg","_t.jpg")).into(image);
        return mCard;
    }

    static List<PictureCardData> getPictureDataFromjsonObj(Context context, String fileName) {
        String jsonString = getstringfromfile(context, fileName);

        if (jsonString == null)
            return null;

        return getPictureDataFromjsonstring(jsonString);

    }

    static List<PictureCardData> getPictureDataFromjsonstring(String JSONasString) {

        try {
            JSONObject jsonObj = new JSONObject(JSONasString);

            JSONArray ja_data = jsonObj.getJSONArray("photos");
            //int length = ja_data.length();
            //Log.i("entries retreived:", "".format("A String %2d", length));

            List<PictureCardData> results = new ArrayList<PictureCardData>();
            for (int i = 0; i < ja_data.length() && i < 50; i++) {
                JSONObject jObj = ja_data.getJSONObject(i);
                PictureCardData pObj = new PictureCardData(jObj);
                results.add(pObj);
                //Log.i("added from json:", pObj.toString());
            }
            return results;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int dpToPixel(int dp, float scale) {
        // Get the screen's density scale
        //float scale = getResources().getDisplayMetrics().density;
        // Add 0.5f to round the figure up to the nearest whole number
        return (int) (dp * scale + 0.5f);
    }

    public static Bitmap bitmapFromUrl(final String url) throws IOException {
        final Bitmap[] x = new Bitmap[1];
        new Thread(new Runnable() {
            public void run() {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    x[0] = BitmapFactory.decodeStream(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return x[0];
    }


}
