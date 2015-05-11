package com.abdallah;

import java.sql.Date;
import java.util.ArrayList;

public class Controller {

    private static double bargainPrice = 1.00;    //price of all records in the bargain basement

    public static double getBargainPrice() {
        return bargainPrice;
    }

    public static void setBargainPrice(double price) {
        bargainPrice = price;
        db.updateBargainPrice();
    }

    static DatabaseModel db;

    public static void main(String[] args) {

        AddShutdownHook closeDBConnection = new AddShutdownHook();
        closeDBConnection.attachShutdownHook();

        Controller controller = new Controller();

        db = new DatabaseModel(controller);

        boolean setupSuccessful = db.setupDatabase();
        if (!setupSuccessful) {
            //TODO: display error messages on GUI
            System.out.println("Error setting up database, see error message.");

            System.out.println("Cleaning up connections...");
            db.cleanup();

            System.out.println("Quitting program...");
            System.exit(-1);    //exit with errors
        }

        new View(controller).launchUI();
    }

    public String requestAddRecord(Record record) {

        boolean success = db.addRecord(record);
        if (success) {
            return null;    //no errors
        } else {
            return "Unable to add record to database";
        }
    }

    public String requestAddConsignor(Consignor consignor) {

        boolean success = db.addConsignor(consignor);
        if (success) {
            return null;
        } else {
            return "Unable to add consignor to database";
        }
    }

    public String requestAddToSales(int recordID, Date dateSold) {

        boolean success = db.addToSales(recordID, dateSold);
        if (success) {
            return null;
        } else {
            return "Unable to add record to sales table";
        }
    }

    public String requestAddToBargainBasement(int recordID) {

        boolean success = db.addToBargainBasement(recordID);
        if (success) {
            return null;
        } else {
            return "Unable to add record to bargain basement table";
        }
    }

    public String requestDeleteRecord(int recordID) {

        boolean success = db.deleteRecord(recordID);
        if (success) {
            return null;
        } else {
            return "Unable to delete record from database";
        }
    }

    public String requestDeleteConsignor(int consignorID) {

        boolean success = db.deleteConsignor(consignorID);
        if (success) {
            return null;
        } else {
            return "Unable to delete consignor from database." +
                    " Make sure consignor does not have records in store before deleting";
        }
    }

    public String requestEditRecord(Record newData) {

        boolean success = db.editRecord(newData);
        if (success) {
            return null;
        } else {
            return "Unable to edit record in database";
        }
    }

    public String requestEditConsignor(Consignor newData) {

        boolean success = db.editConsignor(newData);
        if (success) {
            return null;
        } else {
            return "Unable to edit consignor in database";
        }
    }

    public ArrayList<Record> requestRecordsFromRecordsTable() {
        return db.getAllFromRecordsTable();
    }

    public ArrayList<Record> requestBargainBasementRecords() {
        return db.getAllFromBargainBasementTable();
    }

    public ArrayList<SoldRecord> requestAllSales() {
        return db.getAllSales();
    }

    public ArrayList<Consignor> requestAllConsignors() {
        return db.getAllConsignors();
    }

    public ArrayList<Record> requestRecordsByConsignor(int consignorID) {
        return db.getRecordsByConsignor(consignorID);
    }
}

/**
 * taken from laptop inventory project (Author: Clara James)
 */
class AddShutdownHook {

    public void attachShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Shutdown hook: program closed, attempting to shut database connection");
                //Unfortunately this doesn't seem to be called when a program is restarted in eclipse.
                //Avoid restarting your programs. If you do, and you get an existing connection error you can either
                // 1. restart eclipse - Menu > Restart
                // 2. Delete your database folder. In this project it's a folder called laptopinventoryDB (or similar) in the root directory of your project.
                Controller.db.cleanup();
            }
        });
    }
}
