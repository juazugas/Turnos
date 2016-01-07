package com.defimak47.turnos.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzuriaga on 7/1/16.
 */
public class ShiftInfo extends BaseInfo {

    /**
     * Stuff list.
     * JsonPath: $.feed.entry[...] {
     *     id.$t, updated.$t
     *     gsx$login.$t, gsx$alias.$t, gsx$name.$t, gsx$position.$t
     * }
     */
    protected List<ShiftRecord> shift;

    /**
     * Stuff info default constructor.
     */
    public ShiftInfo () {
        super();
        /* no-op constructor. */
    }

    public List<ShiftRecord> getShift() {
        if(null==shift) {
            shift = new ArrayList<>();
        }
        return shift;
    }

    public void setShift(List<ShiftRecord> shift) {
        this.shift = shift;
    }

    public void addShift(ShiftRecord record) {
        getShift().add(record);
    }
}
