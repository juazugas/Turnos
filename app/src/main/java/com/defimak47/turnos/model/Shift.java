package com.defimak47.turnos.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by jzuriaga on 02/01/15.
 */
public class Shift {

    private int index;

    private int year;

    private int week;

    @SerializedName("start_date")
    private Date startDate;

    private int sprint;

    private int pairing;

    @SerializedName("P1_IMASDMX1")
    private String imasdMx1;

    @SerializedName("P1_IMASDMX2")
    private String imasdMx2;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getSprint() {
        return sprint;
    }

    public void setSprint(int sprint) {
        this.sprint = sprint;
    }

    public int getPairing() {
        return pairing;
    }

    public void setPairing(int pairing) {
        this.pairing = pairing;
    }

    public String getImasdMx1() {
        return imasdMx1;
    }

    public void setImasdMx1(String imasdMx1) {
        this.imasdMx1 = imasdMx1;
    }

    public String getImasdMx2() {
        return imasdMx2;
    }

    public void setImasdMx2(String imasdMx2) {
        this.imasdMx2 = imasdMx2;
    }
}
