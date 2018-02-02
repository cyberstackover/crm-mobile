package com.sinergiinformatika.sisicrm.data.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by wendi on 17-Feb-15.
 */
public class Province implements Serializable {

    @JsonIgnore
    private Integer id;

    @JsonProperty("id")
    private String provinceId;

    @JsonProperty("name")
    private String provinceName;

    @JsonProperty("date_modified")
    private String syncDate;

    public String getSyncDate() {
        return syncDate;
    }

    public void setSyncDate(String syncDate) {
        this.syncDate = syncDate;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
