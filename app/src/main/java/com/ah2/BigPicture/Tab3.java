package com.ah2.BigPicture;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static androidx.core.content.ContextCompat.getSystemService;

public class Tab3 extends Fragment {

    WeakReference<Context> context;
    View gal;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = new WeakReference<>(inflater.getContext());

        gal = inflater.inflate(R.layout.tab_3, null);
        final EditText search_bar = gal.findViewById(R.id.search_bar);
        ImageButton search_button = gal.findViewById(R.id.search_button);

        new LoadTagsTask(gal, inflater);
        //async.delegate = delegate;
        //async.execute();

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(search_bar, inflater, gal);
            }
        });

        return gal;
    }

    private void search(EditText search_bar, LayoutInflater inflater, View gal) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // setting time-zone to match the server
        dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
        final String date = dateFormat.format(new Date());
        //Toast.makeText(gal.getContext(), "Date: "+date, Toast.LENGTH_LONG).show();
        new LoadTagsTask(gal, inflater).execute("https://bigpicture2.herokuapp.com/api/v1/topics");

        if (search_bar.getText().toString().isEmpty()) {
            //Fetching data in Json from backend using only Date field
            new LoadJsonTask(gal, inflater, false).execute("https://bigpicture2.herokuapp.com/api/v1/search?date=2020-12-02");
        } else {
            //Fetching data in Json from backend using Date and tags fields
            new LoadJsonTask(gal, inflater, false).execute(String.format("https://bigpicture2.herokuapp.com/api/v1/search?date=%s&tag=%s",date, search_bar.getText()));
            //Toast.makeText(gal.getContext(), "Searching for: " + search_bar.getText(), Toast.LENGTH_LONG).show();
        }
    }

    public void addTags(String str){
        TextView txt = new TextView(context.get());
        txt.setText(str);
        //Toast.makeText(gal.getContext(), "tags: " +str, Toast.LENGTH_LONG).show();

        ((LinearLayout)gal.findViewById(R.id.search_view)).addView(txt);
    }
}
