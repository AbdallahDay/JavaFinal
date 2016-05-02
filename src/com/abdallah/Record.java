package com.abdallah;

import java.sql.Date;

/**
 * Created by DayDay on 5/1/2015.
 */
public class Record {

    private int recordID;
    private String title;
    private String artist;
    private int consignorID;
    private Date dateConsigned;
    private double price;

    private static final int NO_ID = -1;    //Value to represent unknown ID

    public Record(String title, String artist, int consignorID, Date dateConsigned, double price) {
        this.title = title;
        this.artist = artist;
        this.consignorID = consignorID;
        this.dateConsigned = dateConsigned;
        this.price = price;

        this.recordID = NO_ID;
    }

    public Record(int recordID, String title, String artist, int consignorID, Date dateConsigned, double price) {
        this(title, artist, consignorID, dateConsigned, price);
        this.recordID = recordID;
    }

    public int getRecordID() {
        return this.recordID;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }


    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    public int getConsignorID() {
        return this.consignorID;
    }


    public Date getDateConsigned() {
        return this.dateConsigned;
    }


    public String toString() {
        String idString = (this.recordID == NO_ID) ? "<No ID assigned>" : Integer.toString(this.recordID);
        return "ID: " + idString
                + "Title: " + this.title
                + ", Artist: " + this.artist
                + ", Price: " + String.format("$%.2f", this.price)
                + ", Consignor ID: " + this.consignorID
                + ", Date Consigned: " + this.dateConsigned;
    }
}
