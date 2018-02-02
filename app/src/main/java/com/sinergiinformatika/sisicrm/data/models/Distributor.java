package com.sinergiinformatika.sisicrm.data.models;

/**
 * Created by Mark on 1/7/2015.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class Distributor {
    private long dbId;
    private String id;
    private String name;
    private String syncDate;

    public Distributor() {
    }

    public Distributor(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDbId(long dbId) {
        this.dbId = dbId;
    }

    public String getSyncDate() {
        return syncDate;
    }

    public void setSyncDate(String syncDate) {
        this.syncDate = syncDate;
    }
}
