package com.sinergiinformatika.sisicrm.data.models;

/**
 * Created by wendi on 26-Dec-14.
 */
public class LabelValue {

    private String value;
    private String label;
    private boolean placeHolder;

    public LabelValue() {

    }

    public LabelValue(String value) {
        this(value, value);
    }

    public LabelValue(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public LabelValue(String value, String label, boolean placeHolder) {
        this.value = value;
        this.label = label;
        this.placeHolder = placeHolder;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(boolean placeHolder) {
        this.placeHolder = placeHolder;
    }
}
