package com.ah2.BigPicture;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

public class Tab1 extends Fragment {
  @RequiresApi(api = Build.VERSION_CODES.O)

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View gal = inflater.inflate(R.layout.tab_1,null);
        new JsonTask(gal, inflater).execute("https://bigpicture2.herokuapp.com/api/v1/latest");

        return gal;
    }
}
