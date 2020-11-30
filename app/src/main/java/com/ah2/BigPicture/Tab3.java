package com.ah2.BigPicture;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.List;
import com.bumptech.glide.Glide;

public class Tab3 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View gal = inflater.inflate(R.layout.tab_3,null);
        //PictureCardData.add_card_view_from_json((TableLayout) gal.findViewById(R.id.gImages), inflater);
        return gal;
    }

}
