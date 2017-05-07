package com.defimak47.turnos.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzuriaga on 7/5/17.
 */
public class PhotoInfo extends BaseInfo{

    /**
     * Photo list.
     * JsonPath: $.feed.entry[...] {
     *     id.$t, updated.$t
     *     gsx$login.$t, gsx$url.$t
     * }
     */
    protected List<PhotoRecord> photo;

    /**
     * Stuff info default constructor.
     */
    public PhotoInfo () {
        super();
        /* no-op constructor. */
    }

    public List<PhotoRecord> getPhoto() {
        if(null==photo) {
            photo = new ArrayList<>();
        }
        return photo;
    }

    public void setPhoto(List<PhotoRecord> photo) {
        this.photo = photo;
    }

    public void addPhoto(PhotoRecord record) {
        getPhoto().add(record);
    }
}
