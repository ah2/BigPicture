package com.ah2.BigPicture;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;

public class TabFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    EditText search_bar;
    public static int int_items = 3;
    private int[] tabIcons = {
            R.drawable.ic_home,
            R.drawable.ic_beam,
            R.drawable.ic_toys,
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        //this inflates out tab layout file.
        View x =  inflater.inflate(R.layout.tab_fragment_layout,null);
        // set up stuff.
        tabLayout = x.findViewById(R.id.tabs);
        viewPager = x.findViewById(R.id.viewpager);
        search_bar = x.findViewById(R.id.search_bar);

        ImageButton search_button = x.findViewById(R.id.search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!search_bar.getText().toString().isEmpty()) {
                    //Toast.makeText(MainActivity.this, "you have entered email id " +                                     email.getText().toString() + "Password " + password.getText().toString(), Toast.LENGTH_LONG).show();
                } else {
                    //email.setError("Please Enter Email id");
                }
            }
        });

        // create a new adapter for our pageViewer. This adapters returns child fragments as per the positon of the page Viewer.
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        // this is a workaround
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                //provide the viewPager to TabLayout.
                tabLayout.setupWithViewPager(viewPager);
                //setupTabIcons();
            }
        });
        //to preload the adjacent tabs. This makes transition smooth.
        viewPager.setOffscreenPageLimit(2);

       return x;
   }
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }
    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        //return the fragment with respect to page position.
        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                case 0 : return new Tab1();
                case 1 : return new Tab2();
                case 2 : return new Tab3();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        //This method returns the title of the tab according to the position.
        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "latest";
                case 1 :
                    return "Map";
                case 2:
                    return "Gallery";
            }
            return null;
        }
    }
}