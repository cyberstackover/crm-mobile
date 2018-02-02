package com.sinergiinformatika.sisicrm.data.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mark on 1/8/2015.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class Agenda implements Parcelable {
    public static final Creator<Agenda> CREATOR = new Creator<Agenda>() {
        @Override
        public Agenda createFromParcel(Parcel parcel) {
            return new Agenda(parcel);
        }

        @Override
        public Agenda[] newArray(int i) {
            return new Agenda[i];
        }
    };

    private long id;
    private String date;
    private long storeDbId;
    private String storeId;
    private String storeName;
    private double storeLong;
    private double storeLat;
    private String checkInDateTime;
    private double checkInLong;
    private double checkInLat;
    private String checkOutDateTime;
    private double checkOutLong;
    private double checkOutLat;

    private long surveyDbId;
    private int isSurvey = 0;
    private int isPhoto = 0;
    private int isComplain = 0;


    public Agenda() {

    }

    public Agenda(Parcel in) {
        id = in.readLong();
        date = in.readString();
        storeDbId = in.readLong();
        storeId = in.readString();
        storeName = in.readString();
        storeLong = in.readDouble();
        storeLat = in.readDouble();
        checkInDateTime = in.readString();
        checkInLong = in.readDouble();
        checkInLat = in.readDouble();
        checkOutDateTime = in.readString();
        checkOutLong = in.readDouble();
        checkOutLat = in.readDouble();
    }

    public Agenda(long id, String date, long storeDbId, String storeId, String storeName,
                  double storeLong, double storeLat,
                  String checkInDateTime, double checkInLong, double checkInLat,
                  String checkOutDateTime, double checkOutLong, double checkOutLat) {
        this.id = id;
        this.date = date;
        this.storeDbId = storeDbId;
        this.storeId = storeId;
        this.storeName = storeName;
        this.storeLong = storeLong;
        this.storeLat = storeLat;
        this.checkInDateTime = checkInDateTime;
        this.checkInLong = checkInLong;
        this.checkInLat = checkInLat;
        this.checkOutDateTime = checkOutDateTime;
        this.checkOutLong = checkOutLong;
        this.checkOutLat = checkOutLat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(date);
        parcel.writeLong(storeDbId);
        parcel.writeString(storeId);
        parcel.writeString(storeName);
        parcel.writeDouble(storeLong);
        parcel.writeDouble(storeLat);
        parcel.writeString(checkInDateTime);
        parcel.writeDouble(checkInLong);
        parcel.writeDouble(checkInLat);
        parcel.writeString(checkOutDateTime);
        parcel.writeDouble(checkOutLong);
        parcel.writeDouble(checkOutLat);
    }

    public double getStoreLong() {
        return storeLong;
    }

    public double getStoreLat() {
        return storeLat;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getStoreDbId() {
        return storeDbId;
    }

    public void setStoreDbId(long storeDbId) {
        this.storeDbId = storeDbId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setStoreLong(double storeLong) {
        this.storeLong = storeLong;
    }

    public void setStoreLat(double storeLat) {
        this.storeLat = storeLat;
    }

    public String getCheckInDateTime() {
        return checkInDateTime;
    }

    public void setCheckInDateTime(String checkInDateTime) {
        this.checkInDateTime = checkInDateTime;
    }

    public double getCheckInLong() {
        return checkInLong;
    }

    public void setCheckInLong(double checkInLong) {
        this.checkInLong = checkInLong;
    }

    public double getCheckInLat() {
        return checkInLat;
    }

    public void setCheckInLat(double checkInLat) {
        this.checkInLat = checkInLat;
    }

    public String getCheckOutDateTime() {
        return checkOutDateTime;
    }

    public void setCheckOutDateTime(String checkOutDateTime) {
        this.checkOutDateTime = checkOutDateTime;
    }

    public double getCheckOutLong() {
        return checkOutLong;
    }

    public void setCheckOutLong(double checkOutLong) {
        this.checkOutLong = checkOutLong;
    }

    public double getCheckOutLat() {
        return checkOutLat;
    }

    public void setCheckOutLat(double checkOutLat) {
        this.checkOutLat = checkOutLat;
    }

    public int getIsComplain() {
        return isComplain;
    }

    public void setIsComplain(int isComplain) {
        this.isComplain = isComplain;
    }

    public int getIsPhoto() {
        return isPhoto;
    }

    public void setIsPhoto(int isPhoto) {
        this.isPhoto = isPhoto;
    }

    public int getIsSurvey() {
        return isSurvey;
    }

    public void setIsSurvey(int isSurvey) {
        this.isSurvey = isSurvey;
    }

    public long getSurveyDbId() {
        return surveyDbId;
    }

    public void setSurveyDbId(long surveyDbId) {
        this.surveyDbId = surveyDbId;
    }
}
