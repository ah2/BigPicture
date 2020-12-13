package com.ah2.BigPicture;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import androidx.viewpager.widget.ViewPager;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Tab3 extends Fragment {

    WeakReference<Context> context;
    ViewPager pager;
    View gal;
    String[] tags;
    RelativeLayout pickerFrame;
    DatePicker picker;
    ImageButton dateButton;
    String date;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        gal = inflater.inflate(R.layout.tab_3, null);
        //final EditText search_bar = gal.findViewById(R.id.search_bar);
        ImageButton search_button = gal.findViewById(R.id.search_button2);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = dateFormat.format(new Date());

        tags = new String[0];
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this.getContext(), android.R.layout.select_dialog_item, tags);


        final AutoCompleteTextView search_bar = (AutoCompleteTextView) gal.findViewById(R.id.search_bar2);
        new LoadTagsTask(search_bar, inflater.getContext()).execute("https://bigpicture2.herokuapp.com/api/v1/topics");
        //search_bar.showDropDown();
        search_bar.setThreshold(1);
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

        Button return_Button = (Button) pickerFrame.findViewById(R.id.returnButton);

        return_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollparent.removeView(pickerFrame);
                date = getCurrentDate();
                Toast.makeText(gal.getContext(), "set Date: " + date, Toast.LENGTH_LONG).show();
            }
        });

        final Activity activity = this.getActivity();
        dateButton = (ImageButton) gal.findViewById(R.id.date_button);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gal.findViewById(R.id.datePicker) == null) {
                    scrollparent.addView(pickerFrame);
                    pickerFrame.setVisibility(View.VISIBLE);
                    hideKeyboard(activity);
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

        hideKeyboard(this.getActivity());
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

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void switchToMap(){
        ((TabFragment) getParentFragment()).goToTab(1);
    }
}
