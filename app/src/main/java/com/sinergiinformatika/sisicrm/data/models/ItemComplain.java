package com.sinergiinformatika.sisicrm.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class ItemComplain implements Parcelable, Serializable {
    public static final Creator<ItemComplain> CREATOR = new Creator<ItemComplain>() {
        @Override
        public ItemComplain createFromParcel(Parcel parcel) {
            return new ItemComplain(parcel);
        }

        @Override
        public ItemComplain[] newArray(int i) {
            return new ItemComplain[i];
        }
    };

    @JsonIgnore
    private Integer id;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("complain_id")
    private String complainId;

    @JsonProperty("complain_text")
    private String complain;

    @JsonProperty("checked")
    private int checked; //

    @JsonProperty("value")
    private boolean value = false; //

    @JsonIgnore
    private String modifiedDate;


    public ItemComplain() {

    }

    public ItemComplain(Parcel in) {
        complainId = in.readString();
        complain = in.readString();
        productId = in.readString();
        productName = in.readString();
        checked = in.readInt();
    }

    public ItemComplain(String complainId, String complain) {
        this.complainId = complainId;
        this.complain = complain;
    }

    public ItemComplain(String productId, String productName, String complainId, String complain,
                        int checked) {
        this.productId = productId;
        this.productName = productName;
        this.complainId = complainId;
        this.complain = complain;
        this.checked = checked;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(complainId);
        parcel.writeString(complain);
        parcel.writeString(productId);
        parcel.writeString(productName);
        parcel.writeInt(checked);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int isChecked() {
        return checked;
    }

    public String getComplain() {
        return complain;
    }

    public void setComplain(String complain) {
        this.complain = complain;
    }

    public String getComplainId() {
        return complainId;
    }

    public void setComplainId(String complainId) {
        this.complainId = complainId;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }
}
