package com.sinergiinformatika.sisicrm.data.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sinergiinformatika.sisicrm.utils.Calculator;
import com.sinergiinformatika.sisicrm.utils.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wendi on 26-Dec-14.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Store implements Serializable, Parcelable, Cloneable {

    public static final Creator<Store> CREATOR = new Creator<Store>() {
        @Override
        public Store createFromParcel(Parcel source) {
            return new Store(source);
        }

        @Override
        public Store[] newArray(int size) {
            return new Store[size];
        }
    };
    private static final String LOG_TAG = Store.class.getSimpleName();
    @JsonIgnore
    private Integer id = 0;
    @JsonProperty("store_id")
    private String storeId;
    @JsonProperty("store_name")
    private String name;
    @JsonProperty("store_capacity")
    private Integer capacity = 0;
    @JsonProperty("store_category_code")
    private Integer categoryCode = -1;
    @JsonProperty("store_category")
    private String categoryLabel;
    @JsonIgnore
    private String distributorId;
    @JsonProperty("nfc_id")
    private String nfcId;
    @JsonProperty("longitude")
    private Double longitude = 0.0;
    @JsonProperty("latitude")
    private Double latitude = 0.0;
    @JsonProperty("store_address_province")
    private String province;
    @JsonProperty("store_address_city")
    private String city;
    @JsonProperty("store_address_subdistrict")
    private String subdistrict;
    @JsonProperty("store_address_street")
    private String street;
    @JsonProperty("store_zip_code")
    private String zipcode;
    @JsonProperty("store_phone_mobile")
    private String phoneMobile;
    @JsonProperty("store_phone")
    private String phone;
    @JsonProperty("store_owner_full_name")
    private String ownerName;
    @JsonProperty("store_owner_birthdate")
    private String ownerBirthDate;
    @JsonProperty("store_owner_religion_code")
    private Integer ownerReligionCode = -1;
    @JsonProperty("store_owner_religion")
    private String ownerReligionLabel;
    @JsonProperty("last_check_in")
    private String lastCheckIn;
    @JsonProperty("store_information")
    private String information;
    @JsonProperty("store_created")
    private String created;
    @JsonProperty("store_photo")
    private String photo;
    @JsonProperty("store_status")
    private String status;
    @JsonIgnore
    private String syncDate;
    @JsonProperty("deleted")
    private int deleted;

    @JsonIgnore
    private String modifiedDate;

    @JsonIgnore
    private boolean isHeader = false;//untuk keperluan diplay di list view
    @JsonIgnore
    private String headerName = "";//untuk keperluan diplay di list view

    @JsonProperty("store_address_province_id")
    private String provinceId;
    @JsonProperty("store_address_city_id")
    private String cityId;
    @JsonProperty("store_address_subdistrict_id")
    private String subdistrictId;

    private String syncStatus;

    public Store() {

    }

    public Store(Parcel parcel) {
        setValues(parcel);
    }

    public Store(JSONObject json) {
        setValues(json);
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(Integer categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryLabel() {
        return categoryLabel;
    }

    public void setCategoryLabel(String categoryLabel) {
        this.categoryLabel = categoryLabel;
    }

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSubdistrict() {
        return subdistrict;
    }

    public void setSubdistrict(String subdistrict) {
        this.subdistrict = subdistrict;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getPhoneMobile() {
        return phoneMobile;
    }

    public void setPhoneMobile(String phoneMobile) {
        this.phoneMobile = phoneMobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNfcId() {
        return nfcId;
    }

    public void setNfcId(String nfcId) {
        this.nfcId = nfcId;
    }

    public Double getLongitude() {
        if (longitude == null) {
            longitude = 0.0;
        }

        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        if (latitude == null) {
            latitude = 0.0;
        }

        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerBirthDate() {
        return ownerBirthDate;
    }

    public void setOwnerBirthDate(String ownerBirthDate) {
        this.ownerBirthDate = ownerBirthDate;
    }

    public Integer getOwnerReligionCode() {
        return ownerReligionCode;
    }

    public void setOwnerReligionCode(Integer ownerReligionCode) {
        this.ownerReligionCode = ownerReligionCode;
    }

    public String getOwnerReligionLabel() {
        return ownerReligionLabel;
    }

    public void setOwnerReligionLabel(String ownerReligionLabel) {
        this.ownerReligionLabel = ownerReligionLabel;
    }

    public String getLastCheckIn() {
        return lastCheckIn;
    }

    public void setLastCheckIn(String lastCheckIn) {
        this.lastCheckIn = lastCheckIn;
    }

    public String getPhoneDefault() {
        if (getPhone() != null && getPhone().length() > 0) {
            return getPhone();
        }

        if (getPhoneMobile() != null && getPhoneMobile().length() > 0) {
            return getPhoneMobile();
        }

        return "";
    }

    public String getOwnerNameAndPhone() {
        StringBuilder result = new StringBuilder("");

        if (getOwnerName() != null && getOwnerName().length() > 0) {
            result.append(getOwnerName());
        }

        if (result.length() > 0) {
            result.append(" - ");
        }

        if (getPhoneDefault() != null && getPhoneDefault().length() > 0) {
            result.append(getPhoneDefault());
        }

        return result.toString();
    }

    public Date getLastCheckInDate() {
        if (getLastCheckIn() == null || getLastCheckIn().trim().length() == 0) {
            return null;
        }
        return DateUtil.parse(getLastCheckIn(), false);
    }

    public String getLastCheckInYYYYMMDD() {
        if (getLastCheckInDate() == null) {
            return null;
        }
        return DateUtil.format(getLastCheckInDate(), "yyyy-MM-dd");
    }

    public String getLastCheckInDDMMMMYYYY() {
        if (getLastCheckInDate() == null) {
            return null;
        }
        return DateUtil.format(getLastCheckInDate(), "dd MMMM yyyy");
    }

    public String getDateAndMonth() {
        if (getLastCheckInDate() == null) {
            return "";
        }

        return DateUtil.format(getLastCheckInDate(), "dd MMM");
    }

    public Double getStoreDistance(double currentLatitude, double currentLongitude) {

        if (getLatitude() == 0 && getLongitude() == 0) {
            return null;
        }

        return Calculator.distance(currentLatitude, currentLongitude, getLatitude(), getLongitude());
    }

    public Long getStoreDistanceInLong(double currentLatitude, double currentLongitude) {
        Double distance = getStoreDistance(currentLatitude, currentLongitude);

        if (distance == null || distance.isNaN() || distance.isInfinite()) {
            return null;
        }

        return (long) distance.intValue();
    }

    public String getStoreDistanceFmt(double currentLatitude, double currentLongitude) {
        Long distance = getStoreDistanceInLong(currentLatitude, currentLongitude);

        if (distance == null) {
            return "";
        }

        String unit = "m";
        if (distance > 1000) {
            distance = distance / 1000;
            unit = "km";
        }

        return String.format("%s%s", distance, unit);

    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getOwnerBirthDateLongFmt() {
        if (TextUtils.isEmpty(getOwnerBirthDate()) || getOwnerBirthDate().equals("0000-00-00")) {
            return "";
        }

        return DateUtil.format(DateUtil.parse(getOwnerBirthDate()), "dd MMMM yyyy");
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSyncDate() {
        return syncDate;
    }

    public void setSyncDate(String syncDate) {
        this.syncDate = syncDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getSubdistrictId() {
        return subdistrictId;
    }

    public void setSubdistrictId(String subdistrictId) {
        this.subdistrictId = subdistrictId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public void setValues(JSONObject json) {

        try {
            setName(json.getString("store_name"));
            setCategoryCode(json.getInt("store_category_code"));
            setCategoryLabel(json.getString("store_category"));
            setStoreId(json.getString("store_id"));
            setNfcId(json.getString("nfc_id"));
            setProvince(json.getString("store_address_province"));
            setCity(json.getString("store_address_city"));
            setSubdistrict(json.getString("store_address_subdistrict"));
            setNfcId(json.getString("store_address_street"));
            setZipcode(json.getString("store_zip_code"));
            setPhoneMobile(json.getString("store_phone_mobile"));
            setPhone(json.getString("store_phone"));
            setInformation(json.getString("store_information"));
            setStatus(json.getString("store_status"));
            double lng = json.getDouble("longitude");
            double lat = json.getDouble("latitude");

            if (lng != 0) {
                setLongitude(lng);
            }

            if (lat != 0) {
                setLatitude(lat);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(getId());
        dest.writeString(getStoreId());
        dest.writeString(getName());
        dest.writeInt(getCapacity());
        dest.writeInt(getCategoryCode());
        dest.writeString(getCategoryLabel());
        dest.writeString(getDistributorId());
        dest.writeString(getNfcId());
        dest.writeDouble(getLongitude());
        dest.writeDouble(getLatitude());
        dest.writeString(getProvinceId());
        dest.writeString(getProvince());
        dest.writeString(getCityId());
        dest.writeString(getCity());
        dest.writeString(getSubdistrictId());
        dest.writeString(getSubdistrict());
        dest.writeString(getStreet());
        dest.writeString(getZipcode());
        dest.writeString(getPhoneMobile());
        dest.writeString(getPhone());
        dest.writeString(getOwnerName());
        dest.writeString(getOwnerBirthDate());
        dest.writeInt(getOwnerReligionCode());
        dest.writeString(getOwnerReligionLabel());
        dest.writeString(getLastCheckIn());
        dest.writeString(getInformation());
        dest.writeString(getStatus());
        dest.writeString(getSyncDate());
        dest.writeString(getModifiedDate());
        dest.writeString(getPhoto());
        dest.writeString(getSyncStatus());
        dest.writeString(getCreated());
    }

    @JsonIgnore
    public void setValues(Parcel parcel) {
        setId(parcel.readInt());
        setStoreId(parcel.readString());
        setName(parcel.readString());
        setCapacity(parcel.readInt());
        setCategoryCode(parcel.readInt());
        setCategoryLabel(parcel.readString());
        setDistributorId(parcel.readString());
        setNfcId(parcel.readString());
        setLongitude(parcel.readDouble());
        setLatitude(parcel.readDouble());
        setProvinceId(parcel.readString());
        setProvince(parcel.readString());
        setCityId(parcel.readString());
        setCity(parcel.readString());
        setSubdistrictId(parcel.readString());
        setSubdistrict(parcel.readString());
        setStreet(parcel.readString());
        setZipcode(parcel.readString());
        setPhoneMobile(parcel.readString());
        setPhone(parcel.readString());
        setOwnerName(parcel.readString());
        setOwnerBirthDate(parcel.readString());
        setOwnerReligionCode(parcel.readInt());
        setOwnerReligionLabel(parcel.readString());
        setLastCheckIn(parcel.readString());
        setInformation(parcel.readString());
        setStatus(parcel.readString());
        setSyncDate(parcel.readString());
        setModifiedDate(parcel.readString());
        setPhoto(parcel.readString());
        setSyncStatus(parcel.readString());
        setCreated(parcel.readString());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

