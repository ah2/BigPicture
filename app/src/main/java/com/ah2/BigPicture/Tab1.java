package com.ah2.BigPicture;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class Tab1 extends Fragment {

    public static ViewPager pager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View gal = inflater.inflate(R.layout.tab_1, null);
        new LoadJsonTask(gal, inflater, false).execute("https://bigpicture2.herokuapp.com/api/v1/latest");

        return gal;
    }
}
