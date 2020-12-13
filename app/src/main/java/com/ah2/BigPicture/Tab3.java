package com.ah2.BigPicture;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Tab3 extends Fragment {

    WeakReference<Context> context;
    View gal;
    String[] tags;
    RelativeLayout pickerFrame;
    DatePicker picker;
    ImageButton dateButton;
    String date;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = new WeakReference<>(inflater.getContext());

        gal = inflater.inflate(R.layout.tab_3, null);
        //final EditText search_bar = gal.findViewById(R.id.search_bar);
        ImageButton search_button = gal.findViewById(R.id.search_button2);
        new LoadTagsTask(gal, inflater);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // setting time-zone to match the server
        dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));

        tags = new String[]{"Belgium", "France", "France_", "Italy", "Germany", "Spain"};
        date = dateFormat.format(new Date());

        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this.getContext(), android.R.layout.simple_dropdown_item_1line, tags);

        final AutoCompleteTextView search_bar = (AutoCompleteTextView) gal.findViewById(R.id.search_bar2);
        search_bar.setAdapter(adapter);

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(search_bar, inflater, gal);
            }
        });

        final FrameLayout scrollparent = gal.findViewById(R.id.scrollparent);

        pickerFrame = (RelativeLayout) inflater.inflate(R.layout.date_picker, null);
        picker = pickerFrame.findViewById(R.id.datePicker);
        //pickerFrame.setVisibility(View.GONE);

        Button return_Button = (Button) pickerFrame.findViewById(R.id.returnButton);

        return_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollparent.removeView(pickerFrame);
                date = getCurrentDate();
                Toast.makeText(gal.getContext(), "set Date: " + date, Toast.LENGTH_LONG).show();
            }
        });

        dateButton = (ImageButton) gal.findViewById(R.id.date_button);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gal.findViewById(R.id.datePicker) == null) {
                    scrollparent.addView(pickerFrame);
                    pickerFrame.setVisibility(View.VISIBLE);
                } else if (pickerFrame.getVisibility() == View.VISIBLE) {
                    scrollparent.removeView(pickerFrame);
                    date = getCurrentDate();
                    Toast.makeText(gal.getContext(), "set Date: " + date, Toast.LENGTH_LONG).show();
                } else
                    pickerFrame.setVisibility(View.VISIBLE);
            }
        });

        return gal;
    }

    private void search(AutoCompleteTextView search_bar, LayoutInflater inflater, View gal) {

        new LoadTagsTask(gal, inflater).execute("https://bigpicture2.herokuapp.com/api/v1/topics");

        if (search_bar.getText().toString().isEmpty()) {
            //Fetching data in Json from backend using only Date field
            new LoadJsonTask(gal, inflater, false).execute(String.format("https://bigpicture2.herokuapp.com/api/v1/search?date=%s", date));
        } else {
            //Fetching data in Json from backend using Date and tags fields
            new LoadJsonTask(gal, inflater, false).execute(String.format("https://bigpicture2.herokuapp.com/api/v1/search?date=%s&tag=%s", date, search_bar.getText()));
            //Toast.makeText(gal.getContext(), "Searching for: " + search_bar.getText(), Toast.LENGTH_LONG).show();
        }
    }

    public void addTags(String str) {
        TextView txt = new TextView(context.get());
        txt.setText(str);
        //Toast.makeText(gal.getContext(), "tags: " +str, Toast.LENGTH_LONG).show();

        ((LinearLayout) gal.findViewById(R.id.search_view)).addView(txt);
    }

    public String getCurrentDate() {

        String date = picker.getYear() + "-" +
                (picker.getMonth() + 1) + "-" +//month is 0 based
                picker.getDayOfMonth();
        return date;
    }


}
