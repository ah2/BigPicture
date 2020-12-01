package com.ah2.BigPicture;

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

public class Tab3 extends Fragment {
    View gal;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable   @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        gal = inflater.inflate(R.layout.tab_3,null);
        String date= new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        new JsonTask(gal, inflater, gal.getContext()).execute(String.format("https://bigpicture2.herokuapp.com/api/v1/search?date=%s", date));

        return gal;
    }
}
