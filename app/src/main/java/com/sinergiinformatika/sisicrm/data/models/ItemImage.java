package com.sinergiinformatika.sisicrm.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Mark on 12/27/2014.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class ItemImage implements Parcelable, Serializable {
    public static final Creator<ItemImage> CREATOR = new Creator<ItemImage>() {
        @Override
        public ItemImage createFromParcel(Parcel parcel) {
            return new ItemImage(parcel);
        }

        @Override
        public ItemImage[] newArray(int i) {
            return new ItemImage[i];
        }
    };


    @JsonProperty("photo_id")
    private String imageId;

    @JsonProperty("source")
    private String imageUri;

    public ItemImage() {

    }

    public ItemImage(Parcel in) {
        imageId = in.readString();
        imageUri = in.readString();
    }

    public ItemImage(String imageId, String imageUri) {
        this.imageId = imageId;
        this.imageUri = imageUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(imageId);
        parcel.writeString(imageUri);
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
