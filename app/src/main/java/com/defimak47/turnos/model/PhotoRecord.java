package com.defimak47.turnos.model;

import java.util.Date;

/**
 * Created by jzuriaga on 7/5/17.
 */

public class PhotoRecord extends Photo {

    /**
     * Stuff row identifier.
     */
    protected String id;
    /**
     * Last update date.
     */
    protected Date lastupdated;

    public PhotoRecord() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(Date lastupdated) {
        this.lastupdated = lastupdated;
    }
}
