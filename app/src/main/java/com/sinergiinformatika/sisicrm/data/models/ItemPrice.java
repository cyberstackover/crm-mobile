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
public class ItemPrice implements Parcelable, Serializable, Cloneable {
    public static final Creator<ItemPrice> CREATOR = new Creator<ItemPrice>() {
        @Override
        public ItemPrice createFromParcel(Parcel parcel) {
            return new ItemPrice(parcel);
        }

        @Override
        public ItemPrice[] newArray(int i) {
            return new ItemPrice[i];
        }
    };

    @JsonIgnore
    private Integer id;

    @JsonProperty("price_id")
    private String priceId;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_package")
    private String productPackage;

    @JsonProperty("price")
    private double price;

    @JsonProperty("price_purchase")
    private double pricePurchase;

    @JsonProperty("volume")
    private double volume;

    @JsonProperty("stock")
    private double stock;

    @JsonProperty("distributor_id")
    private String distributorId;

    @JsonProperty("distributor_name")
    private String distributorName;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("is_competitor")
    private int isCompetitor;

    @JsonIgnore
    private String modifiedDate;

    @JsonProperty("product_weight")
    private String productWeight;

    @JsonIgnore
    private String volumenUnit;

    @JsonIgnore
    private String stockUnit;

    @JsonIgnore
    private boolean isHeader = false;//untuk keperluan display di list view

    @JsonProperty("term_of_payment")
    private int termOfPayment;

    public ItemPrice() {

    }

    public ItemPrice(Parcel in) {
        priceId = in.readString();
        productId = in.readString();
        price = in.readDouble();
        pricePurchase = in.readDouble();
        volume = in.readDouble();
        stock = in.readDouble();
        productName = in.readString();
        productPackage = in.readString();
        distributorId = in.readString();
        distributorName = in.readString();
        quantity = in.readInt();
        isCompetitor = in.readInt();
        termOfPayment = in.readInt();
    }

    public ItemPrice(String priceId, String productId, int price, int volume, int stock) {
        this.priceId = priceId;
        this.productId = productId;
        this.price = price;
        this.volume = volume;
        this.stock = stock;
    }

    public ItemPrice(String priceId, String productId, int price, int volume, int stock,
                     String productName, String productPackage) {
        this(priceId, productId, price, volume, stock);
        this.productName = productName;
        this.productPackage = productPackage;
    }

    public ItemPrice(String priceId, String productId, int price, int volume, int stock,
                     String productName, String productPackage, String distributorId,
                     String distributorName) {
        this(priceId, productId, price, volume, stock, productName, productPackage);
        this.distributorId = distributorId;
        this.distributorName = distributorName;
    }

    public ItemPrice(String priceId, String productId, int price, int volume, int stock,
                     String productName, String productPackage, String distributorId,
                     String distributorName, int quantity) {
        this(priceId, productId, price, volume, stock, productName, productPackage, distributorId,
                distributorName);
        this.quantity = quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(priceId);
        parcel.writeString(productId);
        parcel.writeDouble(price);
        parcel.writeDouble(pricePurchase);
        parcel.writeDouble(volume);
        parcel.writeDouble(stock);
        parcel.writeString(productName);
        parcel.writeString(productPackage);
        parcel.writeString(distributorId);
        parcel.writeString(distributorName);
        parcel.writeInt(quantity);
        parcel.writeInt(isCompetitor);
        parcel.writeInt(termOfPayment);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPriceId() {
        return priceId;
    }

    public void setPriceId(String priceId) {
        this.priceId = priceId;
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

    public String getProductPackage() {
        return productPackage;
    }

    public void setProductPackage(String productPackage) {
        this.productPackage = productPackage;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

    public String getDistributorName() {
        return distributorName;
    }

    public void setDistributorName(String distributorName) {
        this.distributorName = distributorName;
    }

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public int getIsCompetitor() {
        return isCompetitor;
    }

    public void setIsCompetitor(int isCompetitor) {
        this.isCompetitor = isCompetitor;
    }

    public String getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(String productWeight) {
        this.productWeight = productWeight;
    }

    public String getStockUnit() {
        return stockUnit;
    }

    public void setStockUnit(String stockUnit) {
        this.stockUnit = stockUnit;
    }

    public String getVolumenUnit() {
        return volumenUnit;
    }

    public void setVolumenUnit(String volumenUnit) {
        this.volumenUnit = volumenUnit;
    }

    public String getPriceFmt() {
        return format(String.valueOf(getPrice()));
    }

    public String getPurchasePriceFmt() {
        return format(String.valueOf(getPricePurchase()));
    }

    public String getVolumeFmt() {
        return format(String.valueOf(getVolume()));
    }

    public String getStockFmt() {
        return format(String.valueOf(getStock()));
    }

    public double getPricePurchase() {
        return pricePurchase;
    }

    public void setPricePurchase(double pricePurchase) {
        this.pricePurchase = pricePurchase;
    }

    private String format(String val) {

        if (val == null || val.trim().length() == 0) {
            return "";
        }

        if (val.endsWith(".00")) {
            return val.replace(".00", "");
        }

        if (val.endsWith(".0")) {
            return val.replace(".0", "");
        }

        return val;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getTermOfPayment() {
        return termOfPayment;
    }

    public void setTermOfPayment(int termOfPayment) {
        this.termOfPayment = termOfPayment;
    }
}
