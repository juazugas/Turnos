package com.defimak47.turnos.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by jzuriaga on 02/01/15.
 */
public class Shift {

    protected int index;

    protected int year;

    protected int week;

    @SerializedName("start_date")
    protected Date startDate;

    protected int sprint;

    protected int pairing;

    @SerializedName("P1_IMASDEU")
    protected String imasdEu;

    @SerializedName("P1_IMASDMX1")
    protected String imasdMx1;

    @SerializedName("P1_IMASDMX2")
    protected String imasdMx2;

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

    public String getImasdEu() {
        return imasdEu;
    }

    public void setImasdEu(String imasdEu) {
        this.imasdEu = imasdEu;
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
