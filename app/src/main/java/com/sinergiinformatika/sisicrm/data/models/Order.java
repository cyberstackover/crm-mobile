package com.sinergiinformatika.sisicrm.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sinergiinformatika.sisicrm.utils.DateUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wendi on 05-Jan-15.
 */
public class Order implements Parcelable, Serializable, Cloneable {

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel parcel) {
            return new Order(parcel);
        }

        @Override
        public Order[] newArray(int i) {
            return new Order[i];
        }
    };
    private long id;
    @JsonProperty("order_id")
    private String orderId;
    @JsonIgnore
    private String storeId;
    @JsonProperty("store_name")
    private String storeName;
    @JsonProperty("order_date")
    private String orderDate;
    private boolean isHeader = false;
    @JsonProperty("orders")
    private List<ItemPrice> prices = new ArrayList<>(0);
    @JsonIgnore
    private String distributorId;
    @JsonProperty("distributor_name")
    private String distributorName;
    private String products;
    private String deliveryDate;
    private String syncStatus;
    private Integer storeDbId;

    public Order() {

    }

    public Order(Parcel parcel) {
        setOrderId(parcel.readString());
        setStoreName(parcel.readString());
        setOrderDate(parcel.readString());
        setId(parcel.readLong());
        parcel.readTypedList(prices, ItemPrice.CREATOR);
        setDistributorId(parcel.readString());
        setDistributorName(parcel.readString());
        setStoreDbId(parcel.readInt());
        setDeliveryDate(parcel.readString());
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(getOrderId());
        parcel.writeString(getStoreName());
        parcel.writeString(getOrderDate());
        parcel.writeLong(getId());
        parcel.writeTypedList(getPrices());
        parcel.writeString(getDistributorId());
        parcel.writeString(getDistributorName());
        parcel.writeInt(getStoreDbId() == null ? 0 : getStoreDbId());
        parcel.writeString(getDeliveryDate());
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public Date getOrderDateInDate() {
        return getOrderDateInDate(false);
    }

    public Date getOrderDateInDate(boolean withTime) {

        if (getOrderDate() == null || getOrderDate().trim().length() == 0) {
            return null;
        }

        return DateUtil.parse(getOrderDate(), !withTime);
    }

    public String getOrderDateInDDMMMMYYYY() {
        return getOrderDateInDDMMMMYYYY(false);
    }

    public String formatOrderDate(String format) {
        return DateUtil.format(getOrderDateInDate(true), format);
    }

    public String getOrderDateInDDMMMMYYYY(boolean withTime) {

        Date d = getOrderDateInDate(withTime);
        if (d == null) {
            return "";
        }

        String format = "dd MMMM yyyy";
        if (withTime) {
            format += " HH:mm:ss";
        }

        return DateUtil.format(d, format);
    }

    public String getOrderDateInYYYYMMDD() {

        if (getOrderDateInDate() == null) {
            return "";
        }

        return DateUtil.format(getOrderDateInDate(), "yyyy-MM-dd");
    }

    public Date getDeliveryDateInDate() {
        return getDeliveryDateInDate(false);
    }

    public Date getDeliveryDateInDate(boolean withTime) {

        if (getDeliveryDate() == null || getDeliveryDate().trim().length() == 0) {
            return null;
        }

        return DateUtil.parse(getDeliveryDate(), !withTime);
    }

    public String getDeliveryDateInEEEEDDMMYYYY() {
        if (getDeliveryDateInDate() == null)
            return null;

        return DateUtil.format(getDeliveryDateInDate(), "EEEE, dd MMMM yyyy");
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public List<ItemPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<ItemPrice> prices) {
        this.prices = prices;
    }

    public String getDistributorName() {
        return distributorName;
    }

    public void setDistributorName(String distributorName) {
        this.distributorName = distributorName;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public Integer getStoreDbId() {
        return storeDbId;
    }

    public void setStoreDbId(Integer storeDbId) {
        this.storeDbId = storeDbId;
    }
}
