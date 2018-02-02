package com.sinergiinformatika.sisicrm.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Mark on 3/23/2015.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class ItemNote implements Parcelable, Serializable {
    public static final Creator<ItemNote> CREATOR = new Creator<ItemNote>() {
        @Override
        public ItemNote createFromParcel(Parcel parcel) {
            return new ItemNote(parcel);
        }

        @Override
        public ItemNote[] newArray(int i) {
            return new ItemNote[i];
        }
    };

    @JsonIgnore
    private int id;
    @JsonProperty("product_id")
    private String productId;
    @JsonProperty("note_type")
    private String noteType;
    @JsonProperty("note")
    private String note;

    public ItemNote() {

    }

    public ItemNote(Parcel parcel) {
        id = parcel.readInt();
        productId = parcel.readString();
        noteType = parcel.readString();
        note = parcel.readString();
    }

    public ItemNote(String productId, String noteType, String note) {
        this.productId = productId;
        this.noteType = noteType;
        this.note = note;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(productId);
        parcel.writeString(noteType);
        parcel.writeString(note);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
