package com.sinergiinformatika.sisicrm.data.models;

import com.sinergiinformatika.sisicrm.Constants;

/**
 * Created by wendi on 26-Dec-14.
 */
public class StoreCategory extends LabelValue {

    public static final int CODE_PLATINUM = Constants.STORE_CATEGORY_CODE_PLATINUM;
    public static final int CODE_GOLD = Constants.STORE_CATEGORY_CODE_GOLD;
    public static final int CODE_SILVER = Constants.STORE_CATEGORY_CODE_SILVER;

    public static final String LABEL_PLATINUM = Constants.STORE_CATEGORY_LABEL_PLATINUM;
    public static final String LABEL_GOLD = Constants.STORE_CATEGORY_LABEL_GOLD;
    public static final String LABEL_SILVER = Constants.STORE_CATEGORY_LABEL_SILVER;

    public static final StoreCategory PLATINUM;
    public static final StoreCategory GOLD;
    public static final StoreCategory SILVER;

    static {
        PLATINUM = new StoreCategory(String.valueOf(CODE_PLATINUM), LABEL_PLATINUM);
        GOLD = new StoreCategory(String.valueOf(CODE_GOLD), LABEL_GOLD);
        SILVER = new StoreCategory(String.valueOf(CODE_SILVER), LABEL_SILVER);
    }

    public StoreCategory() {
    }

    public StoreCategory(int code) {
        this(String.valueOf(code), getLabel(code));
    }

    public StoreCategory(String value, String label) {
        super(value, label);
    }

    public StoreCategory(String value, String label, boolean placeHolder) {
        super(value, label, placeHolder);
    }

    public static String getLabel(int code){
        if(CODE_PLATINUM == code){
            return LABEL_PLATINUM;
        }else if(CODE_GOLD == code){
            return LABEL_GOLD;
        }else if(CODE_SILVER == code){
            return LABEL_SILVER;
        }
        return "";
    }




}
