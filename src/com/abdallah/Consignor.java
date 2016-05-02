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

    public Consignor(String consignorName, String phone, String email, double amountOwed, double totalPaid) {
        this.consignorName = consignorName;
        this.phone = phone;
        this.email = email;
        this.amountOwed = amountOwed;
        this.totalPaid = totalPaid;

        this.consignorID = NO_ID;
    }

    public Consignor(int consignorID, String consignorName, String phone, String email, double amountOwed, double totalPaid) {
        this(consignorName, phone, email, amountOwed, totalPaid);
        this.consignorID = consignorID;
    }

    public int getConsignorID() {
        return this.consignorID;
    }

    public String getConsignorName() {
        return this.consignorName;
    }

    public void setConsignorName(String name) {
        this.consignorName = name;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public double getAmountOwed() {
        return amountOwed;
    }

    public void setAmountOwed(double amountOwed) {
        this.amountOwed = amountOwed;
    }


    public double getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(double totalPaid) {
        this.totalPaid = totalPaid;
    }


    @Override
    public String toString() {
        String idString = (this.consignorID == NO_ID) ? "<No ID assigned>" : Integer.toString(this.consignorID);
        return "ID: " + idString
                + ", Name: " + this.consignorName
                + ", Phone: " + this.phone
                + ", Email: " + this.email
                + ", Amount Owed: " + String.format("$%.2f", this.amountOwed)
                + ", Total Paid: " + String.format("$%.2f", this.totalPaid);
    }
}
