package com.ah2.BigPicture;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.telephony.CellSignalStrength;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
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
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Utils {
    @SuppressLint("SimpleDateFormat")
    static
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static String getstringfromfile(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String readStringFromUrl(String url) {
        try {
            InputStream is = new URL(url).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            is.close();
            return jsonText;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static View getCardViewFromPicdata(final PictureCardData card, LayoutInflater inflater, boolean loadFullImage) {
        View mCard;

        if (loadFullImage)
            mCard = inflater.inflate(R.layout.picrure_card_full_page, null);

        else
            mCard = inflater.inflate(R.layout.picrure_card, null);

        TextView name = mCard.findViewById(R.id.mCardname);
        TextView title = mCard.findViewById(R.id.mCardtitle);
        ImageView image = mCard.findViewById(R.id.mCardImage);

        name.setText(card.getName());
        title.setText(card.getTitle());
        if (card.getName() == null || card.getName().equals(""))
            name.setVisibility(View.GONE);

        if (loadFullImage) {
            TextView tags = mCard.findViewById(R.id.mCardtags);

            String tagstr = "tags: ";
            for (String tmp : card.getTags())
                tagstr += tmp + ", ";
            tags.setText(tagstr);

            TextView date = mCard.findViewById(R.id.mDate);
            date.setText("date: " + card.getDate());

            Glide.with(inflater.getContext())
                    .asBitmap()
                    .load(card.url)
                    .into(image);
        }
        return mCard;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static List<PictureCardData> getPictureDataFromjsonObj(Context context, String fileName) {
        String jsonString = getstringfromfile(context, fileName);

        if (jsonString == null)
            return null;

        return getPictureDataFromjsonstring(jsonString);

    }

    static List<PictureCardData> getPictureDataFromjsonstring(String JSONasString) {

        if (JSONasString == null)
            return null;
        try {
            JSONObject jsonObj = new JSONObject(JSONasString);
            JSONArray ja_data = jsonObj.getJSONArray("photos");
            //int length = ja_data.length();
            //Log.i("entries retreived:", "".format("A String %2d", length));

            List<PictureCardData> results = new ArrayList<PictureCardData>();
            for (int i = 0; i < ja_data.length(); i++) {
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

    static List<PictureCardData> getPictureDataFromjsonstringFlikr(String JSONasString) {

        if (JSONasString == null)
            return Collections.emptyList();
        try {
            JSONObject jsonObj = new JSONObject(JSONasString).getJSONObject("photos");
            JSONArray ja_data = jsonObj.getJSONArray("photo");
            //int length = ja_data.length();
            //Log.i("entries retreived:", "".format("A String %2d", length));

            List<PictureCardData> results = new ArrayList<>();
            for (int i = 0; i < ja_data.length(); i++) {
                JSONObject jObj = ja_data.getJSONObject(i);
                PictureCardData pObj = new PictureCardData(jObj, true);
                //if (pObj.getId() > 0)
                results.add(pObj);
                //Log.i("added from json:", pObj.toString());
            }
            return results;
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return Collections.emptyList();
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

        double r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2.0;
        } else {
            r = bitmap.getWidth() / 2.0;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawCircle((float) r, (float) r, (float) r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
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

    public static float getDistance(LatLng my_latlong, LatLng frnd_latlong) {
        Location l1 = new Location("One");
        l1.setLatitude(my_latlong.latitude);
        l1.setLongitude(my_latlong.longitude);

        Location l2 = new Location("Two");
        l2.setLatitude(frnd_latlong.latitude);
        l2.setLongitude(frnd_latlong.longitude);

        return l1.distanceTo(l2);
    }


    public static boolean hasPermissions(Context context, String... allPermissionNeeded) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && context != null && allPermissionNeeded != null)
            for (String permission : allPermissionNeeded)
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
        return true;
    }

    public static Date parseDate(String dateStr) {
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date(0);
    }

    public static String parseDate(Date date) {
        return dateFormat.format(date);
    }

    public static void loadMarkerIconAndImage(final PictureCardData card, GoogleMap map, final ImageView imageView) {
        MarkerOptions markerOptions = new MarkerOptions().position(card.location)
                .title(card.name).snippet(card.title)
                .icon(Utils.bitmapDescriptorFromVector(imageView.getContext(), R.drawable.ic_info));
        final Marker marker = map.addMarker(markerOptions);
        marker.setTag(card);

        Glide.with(imageView.getContext())
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
