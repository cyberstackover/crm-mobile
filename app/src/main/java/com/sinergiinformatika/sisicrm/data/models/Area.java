package com.sinergiinformatika.sisicrm.data.models;

import java.io.Serializable;

/**
 * Created by wendi on 20-Feb-15.
 */
public class Area implements Serializable {

    private String areaId;
    private String areaName;

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
