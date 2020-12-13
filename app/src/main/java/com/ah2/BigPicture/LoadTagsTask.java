package com.ah2.BigPicture;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

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

public class LoadTagsTask extends AsyncTask<String, String, String> {
    WeakReference<AutoCompleteTextView> search_bar_Ref;
    WeakReference<Context> contextRef;

    LoadTagsTask(AutoCompleteTextView search_bar, Context context) {
        this.search_bar_Ref = new WeakReference<>(search_bar);
        this.contextRef = new WeakReference<>(context);
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
                Log.d("Response: ", "> " + line);

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

        String[] tags;
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONArray ja_data = jsonObj.getJSONArray("tag");
            tags = new String[ja_data.length()];
            for (int i = 0; i < ja_data.length(); i++) {
                tags[i] = ja_data.optString(i);
                //Toast.makeText(contextRef.get(), "tag: " + ja_data.optString(i), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            tags = new String[0];
        }

        AutoCompleteTextView search_bar = search_bar_Ref.get();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(contextRef.get(), android.R.layout.select_dialog_item, tags);
        search_bar.setThreshold(1);
        search_bar.setAdapter(adapter);

        final String[] finalTags = tags;
        search_bar.setValidator(new AutoCompleteTextView.Validator() {
            @Override
            public boolean isValid(CharSequence text) {
                //some logic here returns true or false based on if the text is validated
                for (int i = 0; i < finalTags.length; i++)
                    if (text == finalTags[i])
                        return true;
                return false;
            }

            @Override
            public CharSequence fixText(CharSequence invalidText) {
                //If .isValid() returns false then the code comes here
                //do whatever way you want to fix in the users input and  return it
                return "";
            }
        });


        Toast.makeText(contextRef.get(), "loaded: " + result.length() + " Tags", Toast.LENGTH_LONG).show();
    }

}

