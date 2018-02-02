package com.sinergiinformatika.sisicrm.data.models;

/**
 * Created by wendi on 26-Dec-14.
 */
public class Religion extends LabelValue {

    public static final int ISLAM = 1;
    public static final int KRISTEN = 2;
    public static final int KATOLIK = 3;
    public static final int HINDU = 4;
    public static final int BUDHA = 5;
    public static final int KHONGHUCU = 6;

    public Religion() {

    }

    public Religion(int code, String label) {
        super(String.valueOf(code), label);
    }

    public Religion(String value, String label) {
        super(value, label);
    }

    public static String getLabel(int code) {

        String label = "";
        switch (code) {
            case ISLAM:
                label = "Islam";
                break;
            case KRISTEN:
                label = "Kristen";
                break;
            case KATOLIK:
                label = "Katolik";
                break;
            case BUDHA:
                label = "Budha";
                break;
            case HINDU:
                label = "Hindu";
                break;
            case KHONGHUCU:
                label = "Khonghucu";
                break;
            default:
                label = "";
                break;
        }

        return label;
    }

}
