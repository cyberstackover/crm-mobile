package com.sinergiinformatika.sisicrm.conn;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sinergiinformatika.sisicrm.Constants;

/**
 * Created by wendi on 30-Dec-14.
 */
public class JsonResponse<T> {

    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private T data;

    @JsonProperty
    private JsonError error;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess(){
        return Constants.JSON_STATUS_OK.equals(getStatus());
    }

    public JsonError getError() {
        return error;
    }


    public String getErrorMessage() {
        return (getError() != null) ? getError().getMessage() : "";
    }

    public void setError(JsonError error) {
        this.error = error;
    }

    public Integer getErrorCode(){
        return (getError() != null) ? getError().getCode() : Integer.valueOf(-1);
    }

}
