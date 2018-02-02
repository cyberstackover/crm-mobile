package com.sinergiinformatika.sisicrm.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sinergiinformatika.sisicrm.Constants;

import java.io.Serializable;

/**
 * Created by Mark on 3/23/2015.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class ItemCompetitor implements Parcelable, Serializable {
    public static final Creator<ItemCompetitor> CREATOR = new Creator<ItemCompetitor>() {
        @Override
        public ItemCompetitor createFromParcel(Parcel parcel) {
            return new ItemCompetitor(parcel);
        }

        @Override
        public ItemCompetitor[] newArray(int i) {
            return new ItemCompetitor[i];
        }
    };

    @JsonIgnore
    private Integer id;
    @JsonProperty("product_id")
    private String productId;
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("program_id")
    private String programId;
    @JsonProperty("program_name")
    private String programName;
    @JsonProperty("unit_name")
    private String unitName;
    @JsonProperty("unit_value")
    private String unitValue;
    @JsonProperty("checked")
    private int checked = Constants.FLAG_FALSE;
    @JsonIgnore
    private String modifiedDate;


    public ItemCompetitor() {

    }

    public ItemCompetitor(Parcel in) {
        programId = in.readString();
        programName = in.readString();
        productId = in.readString();
        productName = in.readString();
        checked = in.readInt();
        unitName = in.readString();
        unitValue = in.readString();
    }

    public ItemCompetitor(String programId, String programName) {
        this.programId = programId;
        this.programName = programName;
    }

    public ItemCompetitor(String productId, String productName, String programId, String programName,
                          int checked) {
        this.productId = productId;
        this.productName = productName;
        this.programId = programId;
        this.programName = programName;
        this.checked = checked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(programId);
        parcel.writeString(programName);
        parcel.writeString(productId);
        parcel.writeString(productName);
        parcel.writeInt(checked);
        parcel.writeString(unitName);
        parcel.writeString(unitValue);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
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

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(String unitValue) {
        this.unitValue = unitValue;
    }
}
