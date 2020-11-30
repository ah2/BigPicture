package com.ah2.BigPicture;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.List;

public class PictureCardData implements Parcelable {
    String date;
    int id;
    LatLng location;
    LatLng mid;
    String name;
    String title;
    String url;
    Bitmap image;


public PictureCardData()
{
    new PictureCardData("", -1, new LatLng(0,0), "", "", "", Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888));
}

public PictureCardData(JSONObject jObj) throws JSONException {
    this.date = jObj.optString("date");
    this.id = jObj.optInt("id");
    this.location = new LatLng(jObj.optDouble("lat"),jObj.optDouble("lon"));
    this.name = jObj.optString("name");
    this.title = jObj.optString("title");
    this.url = jObj.optString("url");
    this.image = null;
}

public PictureCardData(String date, int id, LatLng location, String name, String title, String url, Bitmap image) {
    this.date = date;
    this.id = id;
    this.location = location;
    this.name = name;
    this.title = title;
    this.url = url;
    this.image = image;
    }

    protected PictureCardData(Parcel in) {
        date = in.readString();
        id = in.readInt();
        location = in.readParcelable(LatLng.class.getClassLoader());
        name = in.readString();
        title = in.readString();
        url = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<PictureCardData> CREATOR = new Creator<PictureCardData>() {
        @Override
        public PictureCardData createFromParcel(Parcel in) {
            return new PictureCardData(in);
        }

        @Override
        public PictureCardData[] newArray(int size) {
            return new PictureCardData[size];
        }
    };

    public String getDate() {
    return date;
}

    public void setDate(String date){
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImage(Bitmap image) { this.image = image; }

    public int getId() {
        return id;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public Bitmap getImage() { return image; }

    @Override
    public String toString() {
        return "com.example.bigpicture.resultsjson{" +
                "date=" + date +
                ", id=" + id +
                ", location=" + location +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeInt(id);
        dest.writeParcelable(location, flags);
        dest.writeString(name);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeValue(image);
    }

    static void add_card_view_from_json(TableLayout images_view, LayoutInflater inflater) {

        List<PictureCardData> PicDataList = null;
        PicDataList = Utils.getPictureDataFromjsonObj(images_view.getContext(), "search.json");
        //Log.i("data", PicDataList);

        assert PicDataList != null;
        for(int i = 0; i < PicDataList.size(); i++) {
            images_view.addView(createCardViewFromPicdata(PicDataList.get(i), inflater));
        }
    }

    static View createCardViewFromPicdata(PictureCardData card, LayoutInflater inflater){

        View mCard =  inflater.inflate(R.layout.matterial_picrure_card, null);
        TextView name = (TextView) mCard.findViewById(R.id.mCardname);
        TextView title = (TextView) mCard.findViewById(R.id.mCardtitle);
        ImageView image =  mCard.findViewById(R.id.mCardImage);

        name.setText(card.getName());
        title.setText(card.getTitle());
        Glide.with(mCard.getContext()).load(card.url.replace("_c.jpg","_t.jpg")).into(image);

        return mCard;
    }
}
