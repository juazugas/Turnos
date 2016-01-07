package com.defimak47.turnos.model;

import java.util.Date;

/**
 * Created by jzuriaga on 7/1/16.
 */
public class ShiftRecord extends Shift {
    /**
     * Stuff row identifier.
     */
    protected String id;
    /**
     * Last update date.
     */
    protected Date lastupdated;

    /**
     * Default constructor
     */
    public ShiftRecord () {
        /* no-op constructor. */
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
