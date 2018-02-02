package com.sinergiinformatika.sisicrm.data.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by wendi on 17-Feb-15.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class City implements Serializable {

    @JsonIgnore
    private Integer id;

    @JsonProperty("id")
    private String cityId;

    @JsonProperty("name")
    private String cityName;

    @JsonProperty("province_id")
    private String provinceId;

    @JsonProperty("date_modified")
    private String syncDate;

    public String getSyncDate() {
        return syncDate;
    }

    public void setSyncDate(String syncDate) {
        this.syncDate = syncDate;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
