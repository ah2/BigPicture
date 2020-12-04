package com.ah2.BigPicture;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Tab3 extends Fragment  implements LoadTagsTask.AsyncResponse{

    private List<String> tags;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View gal = inflater.inflate(R.layout.tab_3, null);

        final EditText search_bar = gal.findViewById(R.id.search_bar);
        ImageButton search_button = gal.findViewById(R.id.search_button);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // setting time-zone to match the server
        dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
        final String date = dateFormat.format(new Date());

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!search_bar.getText().toString().isEmpty()) {
                    //Fetching data in Json from backend using only Date field and adding image cards
                    new LoadJsonTask(gal, inflater).execute(String.format("https://bigpicture2.herokuapp.com/api/v1/search?date=%s", date));
                } else {
                    //Fetching data in Json from backend using Date and tags fields and adding image cards
                    new LoadJsonTask(gal, inflater).execute(String.format("https://bigpicture2.herokuapp.com/api/v1/search?date=%s&tag=e%s", date,search_bar.getText()));
                    Toast.makeText(gal.getContext(),"Searching for: "+ search_bar.getText(),Toast.LENGTH_LONG);
                }
            }
        });

        return gal;
    }

    @Override
    public void processFinish(String output) {
        try {
            JSONArray tagsJson = new JSONObject(output).getJSONArray("tag");
            Toast.makeText(getContext(), "loaded: "+ tagsJson.length(), Toast.LENGTH_LONG).show();

            Log.i("added Tags: ",tagsJson.toString());
            for (int i = 0; i < tagsJson.length(); i++)
                tags.add(tagsJson.get(i).toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
