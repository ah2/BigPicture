package com.ah2.BigPicture;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Tab3 extends Fragment {

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
                    new LoadJsonTask(gal, inflater).execute(String.format("https://bigpicture2.herokuapp.com/api/v1/search?date=%s", date));

                }
            }
        });

        return gal;
    }
}
