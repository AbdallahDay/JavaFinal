package com.abdallah;

/**
 * Created by DayDay on 5/1/2015.
 */
public class Consignor {
    private int consignorID;
    private String consignorName;
    private String phone;
    private String email;
    private double amountOwed;
    private double totalPaid;

    private static final int NO_ID = -1;     //Value to represent unknown ID

    public Consignor(String consignorName, String phone, String email, double amountOwed, double amountPaid) {
        this.consignorName = consignorName;
        this.phone = phone;
        this.email = email;
        this.amountOwed = amountOwed;
        this.totalPaid = amountPaid;

        this.consignorID = NO_ID;
    }

    public Consignor(int consignorID, String consignorName, String phone, String email, double amountOwed, double amountPaid) {
        this(consignorName, phone, email, amountOwed, amountPaid);
        this.consignorID = consignorID;
    }

    public int getConsignorID() {
        return this.consignorID;
    }

    public String getConsignorName() {
        return this.consignorName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public double getAmountOwed() {
        return amountOwed;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    @Override
    public String toString() {
        String idString = (this.consignorID == NO_ID) ? "<No ID assigned>" : Integer.toString(this.consignorID);
        return "ID: " + idString
                + ", Name: " + this.consignorName
                + ", Phone: " + this.phone
                + ", Email: " + this.email
                + ", Amount Owed: " + this.amountOwed
                + ", Amount Paid: " + this.totalPaid;
    }
}
