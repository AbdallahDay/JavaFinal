package com.abdallah;

import java.sql.Date;

/**
 * Created by DayDay on 5/11/2015.
 */
public class SoldRecord extends Record {
    //represents a record in the sales table of the database

    private Date dateSold;

    public SoldRecord(int recordID, String title, String artist, int consignorID, Date dateConsigned, double salePrice, Date dateSold) {
        super(recordID, title, artist, consignorID, dateConsigned, salePrice);
        this.dateSold = dateSold;
    }

    public Date getDateSold() {
        return this.dateSold;
    }

    @Override
    public String toString() {
        return super.toString() + ", Date Sold: " + this.dateSold;
    }
}
