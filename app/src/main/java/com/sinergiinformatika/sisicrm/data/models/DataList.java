package com.sinergiinformatika.sisicrm.data.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by wendi on 30-Dec-14.
 */
public class DataList<T> {

    @JsonProperty("result_count")
    private Integer count;

    @JsonProperty("last_update")
    private String lastUpdate;

    @JsonProperty("total_count")
    private Integer totalCount;

    @JsonProperty("next_offset")
    private Integer nextOffset;


    @JsonProperty("list")
    private List<T> list;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(Integer nextOffset) {
        this.nextOffset = nextOffset;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public boolean isEmpty(){
        return (getList() == null || getList().size() == 0);
    }
}
