package com.abdallah;

import java.sql.Date;
import java.util.ArrayList;

public class Controller {

    private static double bargainPrice = 1.00;              //price of all records in the bargain basement
    private static final double CONSIGNORS_CUT = 0.4;       //consignor gets 40% of sale price

    private static final int DAYS_BEFORE_BARAGIN = 30;      //how many days before a record must be moved to basement
    private static final int DAYS_BEFORE_DONATE = 365;      //how many days before a record must be donated to charity

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

    public ArrayList<Record> requestRecordsForBargain() {
        return db.getRecordsForBargain(DAYS_BEFORE_BARAGIN);
    }

    public ArrayList<Record> requestRecordsForDonation() {
        return db.getRecordsForDonation(DAYS_BEFORE_DONATE);
    }

    /**
     * Method for selling a record
     *
     * Tasks:
     * - get today's date as 'dateSold'
     * - add record to Sales table
     * - delete record from 'AvailableRecords' view
     * - update consignor with new amount owed
     *
     */
    public String sellRecord(int recordID) {
        //get record data
        Record record = db.getRecordData(recordID);
        if (record == null) return "Record not found";                  //return error message if record not fetched

        //get sale price
        double price = record.getPrice();

        //get consignor data
        int consignorID = record.getConsignorID();

        Consignor consignor = db.getConsignorData(consignorID);
        if (consignor == null) return "Consignor not found";            //return error message if consignor not fetched

        double amountOwed = consignor.getAmountOwed();

        //date sold = today
        Date dateSold = new Date(new java.util.Date().getTime());

        //add to sales table
        String addToSalesMsg = requestAddToSales(recordID, dateSold);
        if (addToSalesMsg != null) return addToSalesMsg;                //return error message if not null

        //delete from available records view
        String deleteRecordMsg = requestDeleteRecord(recordID);
        if (deleteRecordMsg != null) return deleteRecordMsg;            //return error message if not null


        //update amount owed to consignor
        double newAmountOwed = amountOwed + (price * CONSIGNORS_CUT);

        consignor.setAmountOwed(newAmountOwed);

        String editConsignorMsg = requestEditConsignor(consignor);
        if (editConsignorMsg != null) return editConsignorMsg;          //return error message if not null

        //TODO: email consignor

        return null;                                                    //all is good

        //TODO (future versions): find a way to undo all changes if anything goes wrong
        // cant have a record move to the sales table without deleting it
        // from the records/bargain basement table,
        // or without updating the amount owed to the consignor, etc.
    }

    /**
     * Method for paying a consignor
     * Tasks:
     * - subtract amount paid from total amount owed to consignor
     * - add amount paid to total amount paid out to consignor
     * - update database with new consignor data
     *
     */
    public String payConsignor(int consignorID, double amountPaid) {
        //get consignor data
        Consignor consignor = db.getConsignorData(consignorID);
        if (consignor == null) return "Consignor not found";            //return error message if consignor not fetched

        //update total amount owed and amount paid out to consignor
        double amountOwed = consignor.getAmountOwed() - amountPaid;
        double totalPaid = consignor.getTotalPaid() + amountPaid;

        consignor.setAmountOwed(amountOwed);
        consignor.setTotalPaid(totalPaid);

        String editConsignorMsg = requestEditConsignor(consignor);
        if (editConsignorMsg != null) return editConsignorMsg;          //return error message if not null

        return null;                                                    //all is good
    }

    public String getOldRecords() {
        ArrayList<Record> recordsForBargain = requestRecordsForBargain();
        if (recordsForBargain == null) return "Error occurred while checking for old records";

        ArrayList<Record> recordsForDonation = requestRecordsForDonation();
        if (recordsForDonation == null) return "Error occurred while checking for old records";

        //get consignors to email
        ArrayList<Consignor> consignors = new ArrayList<Consignor>();

        for (Record record : recordsForBargain) {
            int consignorID = record.getConsignorID();
            Consignor consignor = db.getConsignorData(consignorID);

            if (consignor == null) return "Error fetching consignor data";

            if (!consignors.contains(consignor)) consignors.add(consignor);
        }

        for (Record record : recordsForDonation) {
            int consignorID = record.getConsignorID();
            Consignor consignor = db.getConsignorData(consignorID);

            if (consignor == null) return "Error fetching consignor data";

            if (!consignors.contains(consignor)) consignors.add(consignor);
        }

        boolean errors = false;

        //email consignors
        for (Consignor consignor : consignors) {

            int consignorID = consignor.getConsignorID();
            String name = consignor.getConsignorName();
            String address = consignor.getEmail();

            String subject = name + "! Some of your records are getting old!";

            //get records to be moved to bargain basement
            ArrayList<Record> bargainRecords = new ArrayList<Record>();

            for (Record record : recordsForBargain) {
                if (record.getConsignorID() == consignorID) {
                    bargainRecords.add(record);
                }
            }

            //get records to be donated
            ArrayList<Record> donationRecords = new ArrayList<Record>();

            for (Record record : recordsForDonation) {
                if (record.getConsignorID() == consignorID) {
                    donationRecords.add(record);
                }
            }

            if (bargainRecords.isEmpty() && donationRecords.isEmpty()) continue;    //don't send email

            //compose email body
            String message =
                    "Dear " + name + ",\n" +
                            "Some of your records have been in our store for too long.";

            //list records to be moved to bargain basement
            if (!bargainRecords.isEmpty()) {
                message += "\nThe following records have been in stock for over " + DAYS_BEFORE_BARAGIN + " days:\n";
                for (Record record : bargainRecords) {
                    message += "\n" + record.toString();
                }
                message +=
                        "\n\nAs per store policy, these records will be moved to the bargain basement" +
                                " and priced at $" + bargainPrice + ". You can still come and pick up you record" +
                                " if you would not like for it to be sold anymore.\n";
            }

            //list records to be donated
            if (!donationRecords.isEmpty()) {
                message += "\nThe following records have been in stock for over " + DAYS_BEFORE_DONATE + " days:\n";
                for (Record record : donationRecords) {
                    message += "\n" + record.toString();
                }
                message +=
                        "\n\nAs per store policy, these records will be put up for donation to a third party" +
                                " and neither you nor the store will profit from it. If you would like, we will hold" +
                                " your records for a few more days so that you can come and pick them up.\n";
            }

            message += "\nFor questions or concerns, please contact the store manager at " + Emailer.managerEmail;

            //send email
            String errorMessage = Emailer.sendEmail(address, subject, message);
            if (errorMessage != null) errors = true;
        }

        if (errors) {
            return "Error sending to some recipients";
        } else {
            return null;
        }
    }
}

/**
 * taken from laptop inventory project (project author: Clara James)
 * url: https://github.com/minneapolis-edu/laptop
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
