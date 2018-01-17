package com.incognisyssolutions.sarcasmdaily;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CHARUL on 12-01-2018.
 */

public class Image implements Parcelable{
    int id;
    String name,thumbnailurl,imageurl;

    public Image(int id, String name, String thumbnailurl, String imageurl) {
        this.id = id;
        this.name = name;
        this.thumbnailurl = thumbnailurl;
        this.imageurl = imageurl;
    }

    public Image() {
    }

    protected Image(Parcel in) {
        id = in.readInt();
        name = in.readString();
        thumbnailurl = in.readString();
        imageurl = in.readString();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnailurl() {
        return thumbnailurl;
    }

    public void setThumbnailurl(String thumbnailurl) {
        this.thumbnailurl = thumbnailurl;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(thumbnailurl);
        dest.writeString(imageurl);
    }
}
