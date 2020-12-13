package com.ah2.BigPicture;

import android.graphics.Camera;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import java.lang.ref.WeakReference;

public class MyListAdapter<MyListData> extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private final MyListData[] listdata;
    private final GoogleMap map;
    private final WeakReference<LayoutInflater> inflater_refrence;
    private final GoogleMap.OnCameraIdleListener maplisten;
    private final CameraUpdateAnimator animator;
    private final FrameLayout scrollparent;

    // RecyclerView recyclerView;
    public MyListAdapter(MyListData[] listdata, LayoutInflater inflater, GoogleMap map, FrameLayout scrollparent) {
        this.listdata = listdata;
        this.inflater_refrence = new WeakReference<>(inflater);
        this.map = map;
        this.scrollparent = scrollparent;

        this.maplisten = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                //get latlng at the center by calling
                //LatLng midLatLng = map.getCameraPosition().target;
            }
        };

        this.animator = new CameraUpdateAnimator(map, maplisten);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.picrure_card, parent, false);

        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyListData myListData = listdata[position];

        holder.name.setText(((PictureCardData) myListData).getName());
        holder.title.setText(((PictureCardData) myListData).getTitle());

        Utils.loadMarkerIconAndImage((PictureCardData) myListData, map, holder.img);

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View i = Utils.getCardViewFromPicdata((PictureCardData) myListData, inflater_refrence.get(), true);

                i.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //i.setVisibility(View.GONE);
                        scrollparent.removeView(i);

                    }
                });
                scrollparent.addView(i);


                //ViewPager viewPager = (ViewPager) ((MainActivity) gal.getContext()).getParent().findViewById(R.id.tabs);
                //TabFragment.goToTab(1);
                animator.add(CameraUpdateFactory.newLatLngZoom(((PictureCardData) myListData).location, 17), true, 1000);
                animator.execute();
            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView card;
        ImageView img;
        TextView name;
        TextView title;

        public ViewHolder(View card) {
            super(card);
            this.card = (CardView) card;
            this.img = card.findViewById(R.id.mCardImage);
            this.name = card.findViewById(R.id.mCardname);
            this.title = card.findViewById(R.id.mCardtitle);
        }
    }
}
