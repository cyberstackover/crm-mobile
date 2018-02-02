package com.sinergiinformatika.sisicrm.data.models;

import java.util.Comparator;
import java.util.List;

/**
 * Created by wendi on 26-Dec-14.
 */
public class ItemHeader<T> {

    private String headerName;
    private List<T> items;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

}
