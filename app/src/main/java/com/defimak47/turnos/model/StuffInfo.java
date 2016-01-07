package com.defimak47.turnos.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzuriaga on 03/10/15.
 */
public class StuffInfo extends BaseInfo {

    /**
     * Stuff list.
     * JsonPath: $.feed.entry[...] {
     *     id.$t, updated.$t
     *     gsx$login.$t, gsx$alias.$t, gsx$name.$t, gsx$position.$t
     * }
     */
    protected List<StuffRecord> stuff;

    /**
     * Stuff info default constructor.
     */
    public StuffInfo () {
        super();
        /* no-op constructor. */
    }

    public List<StuffRecord> getStuff() {
        if (null==stuff) {
            stuff = new ArrayList<>();
        }
        return stuff;
    }

    public void setStuff(List<StuffRecord> stuff) {
        this.stuff = stuff;
    }

    public void addStuff(StuffRecord record) {
        getStuff().add(record);
    }
}
