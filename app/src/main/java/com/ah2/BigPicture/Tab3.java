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
        String Jstr = Utils.getstringfromfile(inflater.getContext(), "search.json");

        List<PictureCardData> cards = Utils.getPictureDataFromjsonstring(Jstr);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 600);  // width, height
        int margin = Utils.dpToPixel(8, getResources().getDisplayMetrics().density);
        layoutParams.setMargins(margin, margin, margin, margin);

        TableLayout cardholder = gal.findViewById(R.id.gImages);

        for(int i = 0; i < cards.size(); i++) {
            cardholder.addView(getCardViewFromPicdatav2(cards.get(i),inflater));
        }



        return gal;
    }

    protected View getCardViewFromPicdatav2(final PictureCardData card, LayoutInflater inflater){

        View mCard =  inflater.inflate(R.layout.matterial_picrure_card, null);
        TextView name = (TextView) mCard.findViewById(R.id.mCardname);
        TextView title = (TextView) mCard.findViewById(R.id.mCardtitle);
        ImageView image =  mCard.findViewById(R.id.mCardImage);

        name.setText(card.getName());
        title.setText(card.getTitle());
        Glide.with(inflater.getContext()).load(card.url.replace("_c.jpg","_t.jpg")).into(image);
        return mCard;
    }

}
