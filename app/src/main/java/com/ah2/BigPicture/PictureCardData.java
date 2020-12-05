package com.ah2.BigPicture;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class PictureCardData implements Parcelable {
    Date date;
    int id;
    LatLng location;
    String name;
    String title;
    List<String>  tags;
    String url;

public PictureCardData()
{
    new PictureCardData("", -1, new LatLng(0,0), "", "", "");
}

public PictureCardData(JSONObject jObj) throws JSONException {
    this.date = Utils.parseDate(jObj.optString("date"));
    this.id = jObj.optInt("id");
    this.location = new LatLng(jObj.optDouble("lat"),jObj.optDouble("lon"));
    this.name = jObj.optString("name");
    this.title = jObj.optString("title");
    this.url = jObj.optString("url");
    String tags = jObj.optString("tags");
    this.tags = Arrays.asList(tags.split(" "));

}

public PictureCardData(String date, int id, LatLng location, String name, String title, String url) {
    this.date = Utils.parseDate(date);
    this.id = id;
    this.location = location;
    this.name = name;
    this.title = title;
    this.url = url;
    }

    protected PictureCardData(Parcel in) {
        date = Utils.parseDate(in.readString());
        id = in.readInt();
        location = in.readParcelable(LatLng.class.getClassLoader());
        name = in.readString();
        title = in.readString();
        url = in.readString();
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

    public Date getDate() {
    return date;
}

    public void setDate(Date date){
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

    public void setTags(List<String> tags){ this.tags = tags; }

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

    public List<String> getTags(){ return tags; }

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
        dest.writeString(Utils.parseDate(date));
        dest.writeInt(id);
        dest.writeParcelable(location, flags);
        dest.writeString(name);
        dest.writeString(title);
        dest.writeString(url);
    }

    static View createCardViewFromPicdata(PictureCardData card, LayoutInflater inflater){

        View mCard =  inflater.inflate(R.layout.picrure_card, null);
        TextView name = (TextView) mCard.findViewById(R.id.mCardname);
        TextView title = (TextView) mCard.findViewById(R.id.mCardtitle);
        ImageView image =  mCard.findViewById(R.id.mCardImage);

        name.setText(card.getName());
        title.setText(card.getTitle());
        Glide.with(mCard.getContext()).load(card.url.replace("_c.jpg","_t.jpg")).into(image);

        return mCard;
    }
}

//sorts by location distance
class Sortbyloc implements Comparator<PictureCardData>
{   LatLng center;

    Sortbyloc(LatLng center){
        this.center = center;
    }

    public int compare(PictureCardData a, PictureCardData b)
    {
        float tmp = Utils.getDistance(a.getLocation(), center) - Utils.getDistance(b.getLocation(), center);
        return (int)tmp;
    }
}
