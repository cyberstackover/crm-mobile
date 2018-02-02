package com.sinergiinformatika.sisicrm.conn;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by wendi on 31-Dec-14.
 */
public class JsonError implements Serializable {

    @JsonProperty
    private Integer code;

    @JsonProperty
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
