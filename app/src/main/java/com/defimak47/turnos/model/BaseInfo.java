package com.defimak47.turnos.model;

import java.util.Date;

/**
 * Created by jzuriaga on 7/1/16.
 */
public class BaseInfo {

    /**
     * Last modification date.
     * JsonPath: $.feed.updated.$t
     */
    protected Date lastupdated;
    /**
     * Link to spreadsheet Json source.
     * JsonPath: $.feed.author.link[0].href
     */
    protected String linkToSource;
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
     * Stuff info default constructor.
     */
    public BaseInfo () {
        /* no-op constructor. */
    }


    public Date getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(Date lastupdated) {
        this.lastupdated = lastupdated;
    }

    public String getLinkToSource() {
        return linkToSource;
    }

    public void setLinkToSource(String linkToSource) {
        this.linkToSource = linkToSource;
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



}
