package com.ah2.BigPicture;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View gal = inflater.inflate(R.layout.tab_3, null);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //setting time-zone to match the server
        dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
        String date = dateFormat.format(new Date());

        //Fetching data in Json from backend and adding image cards
        new LoadJsonTask(gal, inflater).execute(String.format("https://bigpicture2.herokuapp.com/api/v1/search?date=%s", date));

        return gal;
    }
}
