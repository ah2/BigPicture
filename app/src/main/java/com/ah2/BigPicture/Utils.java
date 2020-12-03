package com.ah2.BigPicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

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

    static View getCardViewFromPicdata(final PictureCardData card, LayoutInflater inflater) {

        View mCard = inflater.inflate(R.layout.matterial_picrure_card, null);
        TextView name = (TextView) mCard.findViewById(R.id.mCardname);
        TextView title = (TextView) mCard.findViewById(R.id.mCardtitle);
        ImageView image = mCard.findViewById(R.id.mCardImage);

        name.setText(card.getName());
        title.setText(card.getTitle());
        //Glide.with(inflater.getContext())
        //        .load(card.url.replace("_c.jpg", "_t.jpg"))
        //        .into(image);
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
                if (pObj.getId() > 0)
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

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_info);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
