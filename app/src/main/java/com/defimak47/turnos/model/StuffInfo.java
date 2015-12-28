package com.defimak47.turnos.model;

import java.util.Date;
import java.util.List;

/**
 * Created by jzuriaga on 03/10/15.
 */
public class StuffInfo {

    /**
     * Last modification date.
     * JsonPath: $.feed.updated.$t
     */
    protected Date lastupdated;
    /**
     * Link to spreadsheet Json source.
     * JsonPath: $.feed.author.link[0].href
     */
    protected String linkToSourde;
    /**
     * Author name.
     * JsonPath: $.feed.author.name.$t
     */
    protected String authorName;
    /**
     * Author email.
     * JsonPath: $.feed.author.email.$t
     */
    protected String authorEmail;
    /**
     * Number of results.
     * $.feed.openSearch$totalResults.$t
     */
    protected Integer totalResults;
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
        /* no-op constructor. */
    }

    public Date getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(Date lastupdated) {
        this.lastupdated = lastupdated;
    }

    public String getLinkToSourde() {
        return linkToSourde;
    }

    public void setLinkToSourde(String linkToSourde) {
        this.linkToSourde = linkToSourde;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public List<StuffRecord> getStuff() {
        return stuff;
    }

    public void setStuff(List<StuffRecord> stuff) {
        this.stuff = stuff;
    }
}
