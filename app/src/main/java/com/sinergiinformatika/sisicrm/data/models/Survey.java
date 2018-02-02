package com.sinergiinformatika.sisicrm.data.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sinergiinformatika.sisicrm.utils.DateUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Mark on 12/27/2014.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class Survey implements Parcelable, Serializable, Cloneable {

    public static final Creator<Survey> CREATOR = new Creator<Survey>() {
        @Override
        public Survey createFromParcel(Parcel parcel) {
            return new Survey(parcel);
        }

        @Override
        public Survey[] newArray(int i) {
            return new Survey[i];
        }
    };


    @JsonIgnore
    private Integer id;

    @JsonProperty("survey_id")
    private String surveyId;

    @JsonProperty("products")
    private List<ItemPrice> prices;

    @JsonProperty("photos")
    private List<ItemImage> images;

    @JsonProperty("complains")
    private List<ItemComplain> complains;

    @JsonProperty("store_name")
    private String storeName;

    @JsonProperty("survey_check_in")
    private String checkIn;

    @JsonProperty("is_survey")
    private Boolean isSurvey = Boolean.FALSE;

    @JsonProperty("is_photo")
    private Boolean isPhoto = Boolean.FALSE;

    @JsonProperty("is_complain")
    private Boolean isComplain = Boolean.FALSE;

    @JsonProperty("is_competitor")
    private Boolean isCompetitor = Boolean.FALSE;

    @JsonProperty("store_id")
    private String storeId;

    @JsonIgnore
    private boolean checkInNfc;

    @JsonIgnore
    private double checkInLongitude;

    @JsonIgnore
    private double checkInLatitude;

    @JsonIgnore
    private String checkInTime;

    @JsonIgnore
    private double checkOutLongitude;

    @JsonIgnore
    private double checkOutLatitude;

    @JsonIgnore
    private String checkOut;

    @JsonIgnore
    private String planDate;

    @JsonProperty("survey_server_date")
    private String surveyDate;

    @JsonIgnore
    private String surveyClienDate;


    @JsonIgnore
    private boolean isHeader = false;

    @JsonProperty("comment")
    private String complainNote;

    @JsonIgnore
    private String syncDate;

    @JsonIgnore
    private String modifiedDate;

    @JsonIgnore
    private int agendaDbId;

    @JsonProperty("competitor_note")
    private String competitorNotes;

    private List<ItemCompetitor> competitorPrograms;

    private List<ItemNote> notes;

    @JsonIgnore
    private String syncStatus;

    @JsonIgnore
    private String statusData;

    @JsonIgnore
    private String statusImage;

    public Survey() {

    }

    public Survey(Parcel in) {
        prices = new ArrayList<>();
        images = new ArrayList<>();
        complains = new ArrayList<>();
        competitorPrograms = new ArrayList<>();
        notes = new ArrayList<>();

        surveyId = in.readString();
        in.readTypedList(prices, ItemPrice.CREATOR);
        in.readTypedList(images, ItemImage.CREATOR);
        in.readTypedList(complains, ItemComplain.CREATOR);
        in.readTypedList(competitorPrograms, ItemCompetitor.CREATOR);
        agendaDbId = in.readInt();
        storeName = in.readString();
        complainNote = in.readString();
        id = in.readInt();
        competitorNotes = in.readString();
        storeId = in.readString();
        checkIn = in.readString();
        checkInLatitude = in.readDouble();
        checkInLongitude = in.readDouble();
        checkOut = in.readString();
        checkOutLatitude = in.readDouble();
        checkOutLongitude = in.readDouble();
        surveyDate = in.readString();
        planDate = in.readString();
        in.readTypedList(notes, ItemNote.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(surveyId);
        parcel.writeTypedList(prices);
        parcel.writeTypedList(images);
        parcel.writeTypedList(complains);
        parcel.writeTypedList(competitorPrograms);
        parcel.writeInt(agendaDbId);
        parcel.writeString(storeName);
        parcel.writeString(complainNote);
        parcel.writeInt(id == null ? 0 : id);
        parcel.writeString(competitorNotes);
        parcel.writeString(storeId);
        parcel.writeString(checkIn);
        parcel.writeDouble(checkInLatitude);
        parcel.writeDouble(checkInLongitude);
        parcel.writeString(checkOut);
        parcel.writeDouble(checkOutLatitude);
        parcel.writeDouble(checkOutLongitude);
        parcel.writeString(surveyDate);
        parcel.writeString(planDate);
        parcel.writeTypedList(notes);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public List<ItemPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<ItemPrice> prices) {
        this.prices = prices;
    }

    public List<ItemComplain> getComplains() {
        return complains;
    }

    public void setComplains(List<ItemComplain> complains) {
        this.complains = complains;
    }

    public List<ItemImage> getImages() {
        return images;
    }

    public void setImages(List<ItemImage> images) {
        this.images = images;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setIsCompetitor(Boolean isCompetitor) {
        this.isCompetitor = isCompetitor;
    }

    public Boolean getIsComplain() {
        return isComplain;
    }

    public void setIsComplain(Boolean isComplain) {
        this.isComplain = isComplain;
    }

    public Boolean getIsPhoto() {
        return isPhoto;
    }

    public void setIsPhoto(Boolean isPhoto) {
        this.isPhoto = isPhoto;
    }

    public Boolean getIsSurvey() {
        return isSurvey;
    }

    public void setIsSurvey(Boolean isSurvey) {
        this.isSurvey = isSurvey;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }


    public String getComplainNote() {
        return complainNote;
    }

    public void setComplainNote(String complainNote) {
        this.complainNote = complainNote;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

    public Date getCheckInDate() {
        return getCheckInDate(false);
    }

    public Date getCheckInDate(boolean withTime) {

        if (getCheckIn() == null || getCheckIn().trim().length() == 0) {
            return null;
        }

        return DateUtil.parse(getCheckIn(), !withTime);
    }

    public String getCheckInDateDDMMMMYYYY() {
        return getCheckInDateDDMMMMYYYY(false);
    }

    public String getCheckInDateDDMMMMYYYY(boolean withTime) {

        Date d = getCheckInDate(withTime);
        if (d == null) {
            return "";
        }

        String format = "dd MMMM yyyy";
        if (withTime) {
            format += " HH:mm:ss";
        }

        return DateUtil.format(d, format);
    }

    public String getCheckInDateYYYYMMDD() {

        if (getCheckInDate() == null) {
            return "";
        }

        return DateUtil.format(getCheckInDate(), "yyyy-MM-dd");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


    public String getSurveyClienDate() {
        return surveyClienDate;
    }

    public void setSurveyClienDate(String surveyClienDate) {
        this.surveyClienDate = surveyClienDate;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public double getCheckInLongitude() {
        return checkInLongitude;
    }

    public void setCheckInLongitude(double checkInLongitude) {
        this.checkInLongitude = checkInLongitude;
    }

    public double getCheckInLatitude() {
        return checkInLatitude;
    }

    public void setCheckInlatitude(double checkInLatitude) {
        this.checkInLatitude = checkInLatitude;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public double getCheckOutLongitude() {
        return checkOutLongitude;
    }

    public void setCheckOutLongitude(double checkOutLongitude) {
        this.checkOutLongitude = checkOutLongitude;
    }

    public double getCheckOutLatitude() {
        return checkOutLatitude;
    }

    public void setCheckOutLatitude(double checkOutLatitude) {
        this.checkOutLatitude = checkOutLatitude;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }

    public String getPlanDate() {
        return planDate;
    }

    public void setPlanDate(String planDate) {
        this.planDate = planDate;
    }

    public String getSurveyDate() {
        return surveyDate;
    }

    public void setSurveyDate(String surveyDate) {
        this.surveyDate = surveyDate;
    }

    public boolean isCheckInNfc() {
        return checkInNfc;
    }

    public void setCheckInNfc(boolean checkInNfc) {
        this.checkInNfc = checkInNfc;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getSyncDate() {
        return syncDate;
    }

    public void setSyncDate(String syncDate) {
        this.syncDate = syncDate;
    }

    public int getAgendaDbId() {
        return agendaDbId;
    }

    public void setAgendaDbId(int agendaDbId) {
        this.agendaDbId = agendaDbId;
    }

    public String getCompetitorNotes() {
        return competitorNotes;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getStatusData() {
        return statusData;
    }

    public void setStatusData(String statusData) {
        this.statusData = statusData;
    }

    public String getStatusImage() {
        return statusImage;
    }

    public void setStatusImage(String statusImage) {
        this.statusImage = statusImage;
    }

    public String formatCheckIn(String format) {
        return DateUtil.format(getCheckInDate(true), format);
    }

    public Date getSurveyDateInDate() {
        return getSurveyDateInDate(false);
    }

    public Date getSurveyDateInDate(boolean withTime) {

        if (getSurveyDate() == null || getSurveyDate().trim().length() == 0) {
            return null;
        }

        return DateUtil.parse(getSurveyDate(), !withTime);
    }

    public String formatSurveyDate(String format) {
        return DateUtil.format(getSurveyDateInDate(true), format);
    }

    public String getSurveyDateYYYYMMDD() {

        if (getSurveyDateInDate() == null) {
            return "";
        }

        return DateUtil.format(getSurveyDateInDate(), "yyyy-MM-dd");
    }

    public String getSurveyDateDDMMMMYYYY() {
        return getSurveyDateDDMMMMYYYY(false);
    }

    public String getSurveyDateDDMMMMYYYY(boolean withTime) {

        Date d = getSurveyDateInDate(withTime);
        if (d == null) {
            return "";
        }

        String format = "dd MMMM yyyy";
        if (withTime) {
            format += " HH:mm:ss";
        }

        return DateUtil.format(d, format);
    }

    public boolean isCompetitorNotes() {
        return !TextUtils.isEmpty(competitorNotes) && !competitorNotes.equals("[]");
    }

    public void setCompetitorNotes(String competitorNotes) {
        this.competitorNotes = competitorNotes;
    }

    public boolean isCompetitors() {
        return (competitorPrograms != null && !competitorPrograms.isEmpty()) || isCompetitor;
    }

    public List<ItemCompetitor> getCompetitorPrograms() {
        return competitorPrograms;
    }

    public void setCompetitorPrograms(List<ItemCompetitor> competitorPrograms) {
        this.competitorPrograms = competitorPrograms;
    }

    public List<ItemNote> getNotes() {
        return notes;
    }

    public void setNotes(List<ItemNote> notes) {
        this.notes = notes;
    }

    public void addNotes(List<ItemNote> notes) {
        if (this.notes == null) {
            setNotes(notes);
        } else {
            this.notes.addAll(notes);
        }
    }
}
