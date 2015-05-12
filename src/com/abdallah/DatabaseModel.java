package com.abdallah;

import java.sql.*;
import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by DayDay on 4/30/2015.
 */
public class DatabaseModel {

    //table, view, and column names

    //tables and views
    public static final String RECORDS_TABLE = "Records";
    public static final String CONSIGNORS_TABLE = "Consignors";
    public static final String SALES_TABLE = "Sales";
    public static final String BARGAIN_BSMT_TABLE = "BargainBasement";
    public static final String AVAILABLE_RECORDS_VIEW = "AvailableRecords";

    //columns
    public static final String RECORD_ID = "recordID";
    public static final String TITLE = "title";
    public static final String ARTIST = "artist";
    public static final String DATE_CONSIGNED = "dateConsigned";
    public static final String PRICE = "price";
    public static final String DATE_SOLD = "dateSold";
    public static final String CONSIGNOR_ID = "consignorID";
    public static final String CONSIGNOR_NAME = "consignorName";
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String AMOUNT_OWED = "amountOwed";
    public static final String TOTAL_PAID = "totalPaid";

    //JDBC driver name and protocol, used to create a database connection
    private static String protocol = "jdbc:derby:";
    private static String dbName = "recordInventoryDB";

    //Database credentials
    private static final String USER = "admin";
    private static final String PASS = "admin";

    Controller myController;

    Statement statement = null;
    Connection conn = null;
    ResultSet rs = null;

    ArrayList<Statement> allStatements = new ArrayList<Statement>();

    //Prepared statements
    PreparedStatement psAddRecord = null;
    PreparedStatement psAddConsignor = null;
    PreparedStatement psAddSale = null;

    PreparedStatement psFindRecords = null;
    PreparedStatement psFindConsignors = null;
    PreparedStatement psFindBargainRecords = null;

    PreparedStatement psDeleteRecord = null;
    PreparedStatement psDeleteConsignor = null;

    PreparedStatement psUpdateRecord = null;
    PreparedStatement psUpdateConsignor = null;

    PreparedStatement psAddToBargainBasement = null;

    public DatabaseModel(Controller controller) {
        this.myController = controller;
    }

    public boolean setupDatabase() {
        // default is to setup without deleting
        // tables if they already exist,
        // Use second method for testing purposes only
        return setupDatabase(false);
    }

    private boolean setupDatabase(boolean deleteAndRecreate) {
        // call this method with 'true' only for testing
        // calling with 'true' will delete all tables (all data will be lost)

        try {
            createConnection();
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error creating connection to database.");
            System.err.println(se.getMessage());

            return false;
        }

        //create records table
        try {
            createRecordsTable(deleteAndRecreate);
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Unable to create records table.");
            System.err.println(se.getMessage() + " " + se.getErrorCode());

            return false;
        }

        //create consignors table
        try {
            createConsignorsTable(deleteAndRecreate);
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Unable to create consignors table.");
            System.err.println(se.getMessage() + " " + se.getErrorCode());

            return false;
        }

        //create sales table
        try {
            createSalesTable(deleteAndRecreate);
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Unable to create sales table.");
            System.err.println(se.getMessage() + " " + se.getErrorCode());

            return false;
        }

        //create bargain basement table
        try {
            createBargainBasementTable(deleteAndRecreate);
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Unable to create bargain basement table.");
            System.err.println(se.getMessage() + " " + se.getErrorCode());

            return false;
        }

        //create available records view
        try {
            createAvailableRecordsView(deleteAndRecreate);
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Unable to create available records view.");
            System.err.println(se.getMessage() + " " + se.getErrorCode());

            return false;
        }

        //At this point, everything works
        return true;
    }

    private void createConnection() throws SQLException {
        conn = DriverManager.getConnection(protocol + dbName + ";create=true", USER, PASS);
        statement = conn.createStatement();
        allStatements.add(statement);
    }

    /**
     * taken from laptop inventory project (project author: Clara James)
     * url: https://github.com/minneapolis-edu/laptop
     */
    public void cleanup() {

        try {
            if (rs != null) {
                rs.close();  //Close result set
                System.out.println("ResultSet closed");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }

        //Close all of the statements. Stored a reference to each statement in allStatements so we can loop over all of them and close them all.
        for (Statement s : allStatements) {

            if (s != null) {
                try {
                    s.close();
                    System.out.println("Statement closed");
                } catch (SQLException se) {
                    System.out.println("Error closing statement");
                    se.printStackTrace();
                }
            }
        }

        try {
            if (conn != null) {
                conn.close();  //Close connection to database
                System.out.println("Database connection closed");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private void createRecordsTable (boolean deleteAndRecreate) throws SQLException {
        String createRecordsTableSQL =
                "CREATE TABLE " + RECORDS_TABLE + " (" +
                        RECORD_ID + " int PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                        TITLE + " varchar(50), " +
                        ARTIST + " varchar(50), " +
                        CONSIGNOR_ID + " int, " +
                        DATE_CONSIGNED + " date, " +
                        PRICE + " double)";

        String deleteTableSQL = "DROP TABLE " + RECORDS_TABLE;

        try {
            statement.executeUpdate(createRecordsTableSQL);
            System.out.println("Created records table");
        } catch (SQLException se) {
            //Either table already exists or some other error occurred

            if (se.getSQLState().startsWith("X0")) {
                //Error code for table already existing starts with "X0" (ex, zero)
                if (deleteAndRecreate) {
                    //delete and recreate table
                    System.out.println("Records table already exists, delete and recreate");

                    statement.executeUpdate(deleteTableSQL);
                    statement.executeUpdate(createRecordsTableSQL);
                    //Exception might still be thrown (catch and handle whenever method is called)
                }
                //else - do nothing
            } else {
                //Throw exception to be handled wherever method is called
                throw se;
            }
        }
    }

    private void createConsignorsTable(boolean deleteAndRecreate) throws SQLException {
        String createConsignorsTableSQL =
                "CREATE TABLE " + CONSIGNORS_TABLE + " (" +
                        CONSIGNOR_ID + " int PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                        CONSIGNOR_NAME + " varchar(50), " +
                        PHONE + " varchar(20), " +
                        EMAIL + " varchar(50), " +
                        AMOUNT_OWED + " double, " +
                        TOTAL_PAID + " double)";

        String deleteTableSQL = "DROP TABLE " + CONSIGNORS_TABLE;

        try {
            statement.executeUpdate(createConsignorsTableSQL);
            System.out.println("Created consignors table");
        } catch (SQLException se) {
            //Either table already exists or some other error occurred

            if (se.getSQLState().startsWith("X0")) {
                //Error code for table already existing starts with "X0" (ex, zero)
                if (deleteAndRecreate) {
                    //delete and recreate table
                    System.out.println("Consignors table already exists, delete and recreate");

                    statement.executeUpdate(deleteTableSQL);
                    statement.executeUpdate(createConsignorsTableSQL);
                    //Exception might still be thrown (catch and handle whenever method is called)
                }
                //else - do nothing
            } else {
                //Throw exception to be handled wherever method is called
                throw se;
            }
        }
    }

    private void createSalesTable (boolean deleteAndRecreate) throws SQLException {
        String createSalesTableSQL =
                "CREATE TABLE " + SALES_TABLE + " (" +
                        RECORD_ID + " int, " +
                        TITLE + " varchar(50), " +
                        ARTIST + " varchar(50), " +
                        CONSIGNOR_ID + " int, " +
                        DATE_CONSIGNED + " date, " +
                        PRICE + " double, " +
                        DATE_SOLD + " date)";

        String deleteTableSQL = "DROP TABLE " + SALES_TABLE;

        try {
            statement.executeUpdate(createSalesTableSQL);
            System.out.println("Created sales table");
        } catch (SQLException se) {
            //Either table already exists or some other error occurred

            if (se.getSQLState().startsWith("X0")) {
                //Error code for table already existing starts with "X0" (ex, zero)
                if (deleteAndRecreate) {
                    //delete and recreate table
                    System.out.println("Sales table already exists, delete and recreate");

                    statement.executeUpdate(deleteTableSQL);
                    statement.executeUpdate(createSalesTableSQL);
                    //Exception might still be thrown (catch and handle whenever method is called)
                }
                //else - do nothing
            } else {
                //Throw exception to be handled wherever method is called
                throw se;
            }
        }
    }

    private void createBargainBasementTable (boolean deleteAndRecreate) throws SQLException {
        String createSalesTableSQL =
                "CREATE TABLE " + BARGAIN_BSMT_TABLE + " (" +
                        RECORD_ID + " int, " +
                        TITLE + " varchar(50), " +
                        ARTIST + " varchar(50), " +
                        CONSIGNOR_ID + " int, " +
                        DATE_CONSIGNED + " date, " +
                        PRICE + " double)";

        String deleteTableSQL = "DROP TABLE " + BARGAIN_BSMT_TABLE;

        try {
            statement.executeUpdate(createSalesTableSQL);
            System.out.println("Created bargain basement table");
        } catch (SQLException se) {
            //Either table already exists or some other error occurred

            if (se.getSQLState().startsWith("X0")) {
                //Error code for table already existing starts with "X0" (ex, zero)
                if (deleteAndRecreate) {
                    //delete and recreate table
                    System.out.println("Bargain basement table already exists, delete and recreate");

                    statement.executeUpdate(deleteTableSQL);
                    statement.executeUpdate(createSalesTableSQL);
                    //Exception might still be thrown (catch and handle whenever method is called)
                }
                //else - do nothing
            } else {
                //Throw exception to be handled wherever method is called
                throw se;
            }
        }
    }

    private void createAvailableRecordsView(boolean deleteAndRecreate) throws SQLException {
        //create a view of all available records (including bargain basement records)
        //to make it easier to query all available records

        String createSalesTableSQL =
                "CREATE VIEW " + AVAILABLE_RECORDS_VIEW +
                        " AS " +
                        "SELECT * FROM " + RECORDS_TABLE +
                        " UNION  ALL " +
                        "SELECT * FROM " + BARGAIN_BSMT_TABLE;

        String deleteTableSQL = "DROP VIEW " + AVAILABLE_RECORDS_VIEW;

        try {
            statement.executeUpdate(createSalesTableSQL);
            System.out.println("Created available records view table");
        } catch (SQLException se) {
            //Either table already exists or some other error occurred

            if (se.getSQLState().startsWith("X0")) {
                //Error code for table already existing starts with "X0" (ex, zero)
                if (deleteAndRecreate) {
                    //delete and recreate table
                    System.out.println("Available records view already exists, delete and recreate");

                    statement.executeUpdate(deleteTableSQL);
                    statement.executeUpdate(createSalesTableSQL);
                    //Exception might still be thrown (catch and handle whenever method is called)
                }
                //else - do nothing
            } else {
                //Throw exception to be handled wherever method is called
                throw se;
            }
        }
    }

    public boolean addRecord (Record record) {

        //Create SQL query to add this record info to DB

        String addRecordSQLps =
                "INSERT INTO " + RECORDS_TABLE + " (" +
                        TITLE + ", " +
                        ARTIST + ", " +
                        CONSIGNOR_ID + ", " +
                        DATE_CONSIGNED + ", " +
                        PRICE +
                        ") VALUES (?, ?, ?, ?, ?)";

        try {
            // prepare statement
            psAddRecord = conn.prepareStatement(addRecordSQLps);
            allStatements.add(psAddRecord);

            // get record data
            psAddRecord.setString(1, record.getTitle());
            psAddRecord.setString(2, record.getArtist());
            psAddRecord.setInt(3, record.getConsignorID());
            psAddRecord.setDate(4, record.getDateConsigned());
            psAddRecord.setDouble(5, record.getPrice());

            psAddRecord.execute();
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error preparing statement or executing prepared statement to add record");
            System.out.println(se.getErrorCode() + " " + se.getMessage());

            return false;
        }

        return true;
    }

    public boolean addConsignor (Consignor consignor) {

        //Create SQL query to add this consignor info to DB

        String addConsignorSQLps =
                "INSERT INTO " + CONSIGNORS_TABLE + " (" +
                        CONSIGNOR_NAME + ", " +
                        PHONE + ", " +
                        EMAIL + ", " +
                        AMOUNT_OWED + ", " +
                        TOTAL_PAID +
                        ") VALUES (?, ?, ?, ?, ?)";

        try {
            // prepare statement
            psAddConsignor = conn.prepareStatement(addConsignorSQLps);
            allStatements.add(psAddRecord);

            // get consignor data
            psAddConsignor.setString(1, consignor.getConsignorName());
            psAddConsignor.setString(2, consignor.getPhone());
            psAddConsignor.setString(3, consignor.getEmail());
            psAddConsignor.setDouble(4, consignor.getAmountOwed());
            psAddConsignor.setDouble(5, consignor.getTotalPaid());

            psAddConsignor.execute();
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error preparing statement or executing prepared statement to add consignor");
            System.out.println(se.getErrorCode() + " " + se.getMessage());

            return false;
        }

        return true;
    }

    public boolean addToSales(int recordID, Date dateSold) {
        //make sure record exists
        if (getRecordData(recordID) == null) return false;

        //SQL query for moving a record from records table to sales table
        String addSaleSQLps =
                "INSERT INTO " + SALES_TABLE + " (" +
                        RECORD_ID + ", " +
                        TITLE + ", " +
                        ARTIST + ", " +
                        CONSIGNOR_ID + ", " +
                        DATE_CONSIGNED + ", " +
                        PRICE + ", " +
                        DATE_SOLD +
                        ") VALUES ((SELECT * FROM " + AVAILABLE_RECORDS_VIEW +
                        " WHERE " + RECORD_ID + "=?), ?)";

        try {
            //prepare statement
            psAddSale = conn.prepareStatement(addSaleSQLps);
            allStatements.add(psAddSale);

            //insert data
            psAddSale.setInt(1, recordID);
            psAddSale.setDate(2, dateSold);

            psAddSale.execute();
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error preparing statement or executing prepared statement to move record to sales table");
            System.out.println(se.getErrorCode() + " " + se.getMessage());

            return false;
        }

        return true;
    }

    public boolean addToBargainBasement(int recordID) {
        //make sure record exists
        if (getRecordData(recordID) == null) return false;

        //SQL query for moving a record from records table to bargain basement table
        String addToBargainBasementSQLps =
                "INSERT INTO " + BARGAIN_BSMT_TABLE + " (" +
                        RECORD_ID + ", " +
                        TITLE + ", " +
                        ARTIST + ", " +
                        CONSIGNOR_ID + ", " +
                        DATE_CONSIGNED + ", " +
                        PRICE +
                        ") VALUES ((SELECT " +
                        RECORD_ID + ", " +
                        TITLE + ", " +
                        ARTIST + ", " +
                        CONSIGNOR_ID + ", " +
                        DATE_CONSIGNED +
                        " FROM " + RECORDS_TABLE +
                        " WHERE " + RECORD_ID + "=?), ?)";

        try {
            //prepare statement
            psAddToBargainBasement = conn.prepareStatement(addToBargainBasementSQLps);
            allStatements.add(psAddToBargainBasement);

            //insert data
            psAddToBargainBasement.setInt(1, recordID);
            psAddToBargainBasement.setDouble(2, Controller.getBargainPrice());   //set price as bargain price (currently $1)

            psAddToBargainBasement.execute();
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error preparing statement or executing prepared statement to move record to bargain basement table");
            System.out.println(se.getErrorCode() + " " + se.getMessage());

            return false;
        }

        return true;
    }

    public boolean deleteRecord(int recordID) {
        //make sure record exists
        if (getRecordData(recordID) == null) return false;

        // SQL query for deleting record from DB
        String deleteRecordSQLps =
                "DELETE FROM " + AVAILABLE_RECORDS_VIEW +
                        " WHERE " + RECORD_ID + "=?";

        try {
            psDeleteRecord = conn.prepareStatement(deleteRecordSQLps);
            allStatements.add(psDeleteRecord);
            psDeleteRecord.setInt(1, recordID);

            psDeleteRecord.execute();
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error preparing statement or executing prepared statement to delete record.");
            System.err.println(se.getErrorCode() + " " + se.getMessage());

            return false;
        }

        //record was successfully deleted from database
        return true;
    }

    public boolean deleteConsignor(int consignorID) {
        //make sure consignor exists
        if (getConsignorData(consignorID) == null) return false;

        //do not allow delete if consignor still has records in store
        if (consignorHasRecords(consignorID)) return false;

        // SQL query for deleting consignor from DB
        String deleteConsignorSQLps =
                "DELETE FROM " + CONSIGNORS_TABLE +
                        " WHERE " + CONSIGNOR_ID + "=?";

        try {
            psDeleteConsignor = conn.prepareStatement(deleteConsignorSQLps);
            allStatements.add(psDeleteConsignor);
            psDeleteConsignor.setInt(1, consignorID);

            psDeleteConsignor.execute();
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error preparing statement or executing prepared statement to delete consignor.");
            System.err.println(se.getErrorCode() + " " + se.getMessage());

            return false;
        }

        //consignor was successfully deleted from database
        return true;
    }

    public boolean editRecord(Record newData) {
        //takes Record object with new record data
        int recordID = newData.getRecordID();

        //make sure record exists
        if (getRecordData(recordID) == null) return false;

        // SQL query for updating record
        String updateRecordSQLps =
                "UPDATE " + AVAILABLE_RECORDS_VIEW + " SET " +
                        TITLE + "=?," +
                        ARTIST + "=?," +
                        CONSIGNOR_ID + "=?," +
                        DATE_CONSIGNED + "=?," +
                        PRICE + "=?" +
                        " WHERE " + RECORD_ID + "=?";

        try {
            psUpdateRecord = conn.prepareStatement(updateRecordSQLps);
            allStatements.add(psUpdateRecord);

            psUpdateRecord.setString(1, newData.getTitle());
            psUpdateRecord.setString(2, newData.getArtist());
            psUpdateRecord.setInt(3, newData.getConsignorID());
            psUpdateRecord.setDate(4, newData.getDateConsigned());
            psUpdateRecord.setDouble(5, newData.getPrice());
            psUpdateRecord.setInt(6, recordID);

            psUpdateRecord.execute();
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error preparing statement or executing prepared statement to update record data.");
            System.err.println(se.getErrorCode() + " " + se.getMessage());

            return false;
        }

        //record data successfully updated
        return true;
    }

    public boolean editConsignor(Consignor newData) {
        //takes Consignor object with new consignor data
        int consignorID = newData.getConsignorID();

        //make sure consignor exists
        if (getConsignorData(consignorID) == null) return false;

        // SQL query for updating consignor
        String updateConsignorSQLps =
                "UPDATE " + CONSIGNORS_TABLE + " SET " +
                        CONSIGNOR_NAME + "=?," +
                        PHONE + "=?," +
                        EMAIL + "=?," +
                        AMOUNT_OWED + "=?," +
                        TOTAL_PAID + "=?" +
                        " WHERE " + CONSIGNOR_ID + "=?";

        try {
            psUpdateConsignor = conn.prepareStatement(updateConsignorSQLps);
            allStatements.add(psUpdateConsignor);

            psUpdateConsignor.setString(1, newData.getConsignorName()); //name
            psUpdateConsignor.setString(2, newData.getPhone()); //phone
            psUpdateConsignor.setString(3, newData.getEmail()); //email
            psUpdateConsignor.setDouble(4, newData.getAmountOwed()); //amount owed
            psUpdateConsignor.setDouble(5, newData.getTotalPaid()); //total paid
            psUpdateConsignor.setInt(6, consignorID); //conID

            psUpdateConsignor.execute();
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error preparing statement or executing prepared statement to update consignor data.");
            System.err.println(se.getErrorCode() + " " + se.getMessage());

            return false;
        }

        //consignor data successfully updated
        return true;
    }

    public boolean updateBargainPrice() {
        //SQL query for updating all rows in bargain basement table
        String updateRecordsSQL =
                "UPDATE " + BARGAIN_BSMT_TABLE + " SET " +
                        PRICE + "=" + Controller.getBargainPrice();

        try {
            statement.executeUpdate(updateRecordsSQL);
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error updating bargain basement records.");
            System.err.println(se.getErrorCode() + " " + se.getMessage());

            return false;
        }

        //update successful
        return true;
    }

    public ArrayList<Record> getAllFromRecordsTable() {

        ArrayList<Record> records = new ArrayList<Record>();

        String getRecords = "SELECT * FROM " + RECORDS_TABLE;
        try {
            rs = statement.executeQuery(getRecords);
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error fetching records from records table");
            System.out.println(se.getErrorCode() + " " + se.getMessage());

            return null;
        }

        try {
            while (rs.next()) {
                int recordID = rs.getInt(RECORD_ID);
                String title = rs.getString(TITLE);
                String artist = rs.getString(ARTIST);
                int consignorID = rs.getInt(CONSIGNOR_ID);
                Date dateConsigned = rs.getDate(DATE_CONSIGNED);
                double price = rs.getDouble(PRICE);

                Record record = new Record(
                        recordID,
                        title,
                        artist,
                        consignorID,
                        dateConsigned,
                        price);

                records.add(record);
            }
        } catch (SQLException se) {
            System.err.println("Error reading from result set after fetching record data");
            System.out.println(se.getErrorCode() + " " + se.getMessage());

            return null;
        }

        return records;
    }

    public ArrayList<Record> getAllFromBargainBasementTable() {

        ArrayList<Record> records = new ArrayList<Record>();

        String getRecords = "SELECT * FROM " + BARGAIN_BSMT_TABLE;
        try {
            rs = statement.executeQuery(getRecords);
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error fetching records from bargain basement table");
            System.out.println(se.getErrorCode() + " " + se.getMessage());

            return null;
        }

        try {
            while (rs.next()) {
                int recordID = rs.getInt(RECORD_ID);
                String title = rs.getString(TITLE);
                String artist = rs.getString(ARTIST);
                int consignorID = rs.getInt(CONSIGNOR_ID);
                Date dateConsigned = rs.getDate(DATE_CONSIGNED);
                double price = rs.getDouble(PRICE);

                Record record = new Record(
                        recordID,
                        title,
                        artist,
                        consignorID,
                        dateConsigned,
                        price);

                records.add(record);
            }
        } catch (SQLException se) {
            System.err.println("Error reading from result set after fetching record data");
            System.out.println(se.getErrorCode() + " " + se.getMessage());

            return null;
        }

        return records;
    }

    public ArrayList<SoldRecord> getAllSales() {

        ArrayList<SoldRecord> allSales = new ArrayList<SoldRecord>();

        String getRecords = "SELECT * FROM " + SALES_TABLE;
        try {
            rs = statement.executeQuery(getRecords);
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error fetching records from sales table");
            System.out.println(se.getErrorCode() + " " + se.getMessage());

            return null;
        }

        try {
            while (rs.next()) {
                int recordID = rs.getInt(RECORD_ID);
                String title = rs.getString(TITLE);
                String artist = rs.getString(ARTIST);
                int consignorID = rs.getInt(CONSIGNOR_ID);
                Date dateConsigned = rs.getDate(DATE_CONSIGNED);
                double price = rs.getDouble(PRICE);
                Date dateSold = rs.getDate(DATE_SOLD);

                SoldRecord record = new SoldRecord(
                        recordID,
                        title,
                        artist,
                        consignorID,
                        dateConsigned,
                        price,
                        dateSold);

                allSales.add(record);
            }
        } catch (SQLException se) {
            System.err.println("Error reading from result set after fetching record data");
            System.out.println(se.getErrorCode() + " " + se.getMessage());

            return null;
        }

        return allSales;
    }

    public ArrayList<Consignor> getAllConsignors() {

        ArrayList<Consignor> allConsignors = new ArrayList<Consignor>();

        String getAllConsignors = "SELECT * FROM " + CONSIGNORS_TABLE;
        try {
            rs = statement.executeQuery(getAllConsignors);
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error fetching consignors from consignors table");
            System.out.println(se.getErrorCode() + " " + se.getMessage());

            return null;
        }

        try {
            while (rs.next()) {
                int consignorID = rs.getInt(CONSIGNOR_ID);
                String consignorName = rs.getString(CONSIGNOR_NAME);
                String phone = rs.getString(PHONE);
                String email = rs.getString(EMAIL);
                double amountOwed = rs.getDouble(AMOUNT_OWED);
                double totalPaid = rs.getDouble(TOTAL_PAID);

                Consignor consignor = new Consignor(
                        consignorID,
                        consignorName,
                        phone,
                        email,
                        amountOwed,
                        totalPaid);

                allConsignors.add(consignor);
            }
        } catch (SQLException se) {
            System.err.println("Error reading from result set after fetching consignor data");
            System.out.println(se.getErrorCode() + " " + se.getMessage());

            return null;
        }

        return allConsignors;
    }

    public ArrayList<Record> getRecordsByConsignor(int consignorID) {
        //make sure consignor exists
        if (getConsignorData(consignorID) == null) return null;

        ArrayList<Record> records = new ArrayList<Record>();

        //get records for this consignor from available records view
        String getRecords =
                "SELECT * FROM " + AVAILABLE_RECORDS_VIEW +
                        " WHERE " + CONSIGNOR_ID + "=" + consignorID;
        try {
            rs = statement.executeQuery(getRecords);
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error fetching records from available records view");
            System.out.println(se.getErrorCode() + " " + se.getMessage());

            return null;
        }

        try {
            while (rs.next()) {
                int recordID = rs.getInt(RECORD_ID);
                String title = rs.getString(TITLE);
                String artist = rs.getString(ARTIST);
                int conID = rs.getInt(CONSIGNOR_ID);
                Date dateConsigned = rs.getDate(DATE_CONSIGNED);
                double price = rs.getDouble(PRICE);

                Record record = new Record(
                        recordID,
                        title,
                        artist,
                        conID,
                        dateConsigned,
                        price);

                records.add(record);
            }
        } catch (SQLException se) {
            System.err.println("Error reading from result set after fetching record data");
            System.out.println(se.getErrorCode() + " " + se.getMessage());

            return null;
        }

        return records;
    }

    /**
     *
     * @param numberOfDays: number of days a record can stay in store before it needs to go into bargain basement
     *
     */
    public ArrayList<Record> getRecordsForBargain(int numberOfDays) {

        ArrayList<Record> records = new ArrayList<Record>();

        //get record data from records table where the age of the record exceeds a given number of days
        String getRecords =
                "SELECT " +
                        RECORD_ID + ", " +
                        TITLE + ", " +
                        ARTIST + ", " +
                        CONSIGNOR_ID + ", " +
                        DATE_CONSIGNED + ", " +
                        PRICE +
                        ", CAST(DATEDIFF(day, dateConsigned, GETDATE()) AS INT) AS daysOld" +
                        " FROM " + RECORDS_TABLE +
                        " WHERE daysOld >= " + numberOfDays;
        //TODO: getting exception: 30000 Column 'DAY' is either not in any table in the FROM list
        // or appears within a join specification and is outside the scope of the join specification
        // or appears in a HAVING clause and is not in the GROUP BY list.
        // If this is a CREATE or ALTER TABLE  statement then 'DAY' is not a column in the target table.
        try {
            rs = statement.executeQuery(getRecords);
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error fetching records from records table");
            System.out.println(se.getErrorCode() + " " + se.getMessage());

            return null;
        }

        try {
            while (rs.next()) {
                int recordID = rs.getInt(RECORD_ID);
                String title = rs.getString(TITLE);
                String artist = rs.getString(ARTIST);
                int conID = rs.getInt(CONSIGNOR_ID);
                Date dateConsigned = rs.getDate(DATE_CONSIGNED);
                double price = rs.getDouble(PRICE);

                Record record = new Record(
                        recordID,
                        title,
                        artist,
                        conID,
                        dateConsigned,
                        price);

                records.add(record);
            }
        } catch (SQLException se) {
            System.err.println("Error reading from result set after fetching record data");
            System.out.println(se.getErrorCode() + " " + se.getMessage());

            return null;
        }

        return records;
    }

    /**
     *
     * @param numberOfDays: number of days before record needs to donated to charity
     *
     */
    public ArrayList<Record> getRecordsForDonation(int numberOfDays) {

        ArrayList<Record> records = new ArrayList<Record>();

        //get record data from available records view where the age of the record exceeds a given number of days
        String getRecords =
                "SELECT " +
                        RECORD_ID + ", " +
                        TITLE + ", " +
                        ARTIST + ", " +
                        CONSIGNOR_ID + ", " +
                        DATE_CONSIGNED + ", " +
                        PRICE +
                        ", CAST(DATEDIFF(day, dateConsigned, GETDATE()) AS INT) AS daysOld" +
                        " FROM " + AVAILABLE_RECORDS_VIEW +
                        " WHERE daysOld >= " + numberOfDays;
        try {
            rs = statement.executeQuery(getRecords);
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error fetching records from available records view");
            System.out.println(se.getErrorCode() + " " + se.getMessage());

            return null;
        }

        try {
            while (rs.next()) {
                int recordID = rs.getInt(RECORD_ID);
                String title = rs.getString(TITLE);
                String artist = rs.getString(ARTIST);
                int conID = rs.getInt(CONSIGNOR_ID);
                Date dateConsigned = rs.getDate(DATE_CONSIGNED);
                double price = rs.getDouble(PRICE);

                Record record = new Record(
                        recordID,
                        title,
                        artist,
                        conID,
                        dateConsigned,
                        price);

                records.add(record);
            }
        } catch (SQLException se) {
            System.err.println("Error reading from result set after fetching record data");
            System.out.println(se.getErrorCode() + " " + se.getMessage());

            return null;
        }

        return records;
    }

    public Record getRecordData(int recordID) {
        // SQL query for finding record in DB
        String findRecordSQLps =
                "SELECT * FROM " + AVAILABLE_RECORDS_VIEW +
                        " WHERE " + RECORD_ID + "=?";

        try {
            //get data
            psFindRecords = conn.prepareStatement(findRecordSQLps);
            allStatements.add(psFindRecords);
            psFindRecords.setInt(1, recordID);

            rs = psFindRecords.executeQuery();

            int id = rs.getInt(RECORD_ID);
            String title = rs.getString(TITLE);
            String artist = rs.getString(ARTIST);
            int consignorID = rs.getInt(CONSIGNOR_ID);
            Date dateConsigned = rs.getDate(DATE_CONSIGNED);
            double price = rs.getDouble(PRICE);

            return new Record(id, title, artist, consignorID, dateConsigned, price);

        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error preparing statement or executing prepared statement to fetch record.");
            System.err.println(se.getErrorCode() + " " + se.getMessage());

            //null means error occurred
            return null;
        }
    }

    public Consignor getConsignorData(int consignorID) {
        // SQL query for finding consignor in DB
        String findConsignorSQLps =
                "SELECT * FROM " + CONSIGNORS_TABLE +
                        " WHERE " + CONSIGNOR_ID + "=?";

        try {
            //get data
            psFindConsignors = conn.prepareStatement(findConsignorSQLps);
            allStatements.add(psFindConsignors);
            psFindConsignors.setInt(1, consignorID);

            rs = psFindConsignors.executeQuery();

            int id = rs.getInt(CONSIGNOR_ID);
            String name = rs.getString(CONSIGNOR_NAME);
            String phone = rs.getString(PHONE);
            String email = rs.getString(EMAIL);
            double amountOwed = rs.getDouble(AMOUNT_OWED);
            double totalPaid = rs.getDouble(TOTAL_PAID);

            return new Consignor(id, name, phone, email, amountOwed, totalPaid);

        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error preparing statement or executing prepared statement to find consignor.");
            System.err.println(se.getErrorCode() + " " + se.getMessage());

            //null means error occurred
            return null;
        }
    }

    private boolean consignorHasRecords(int consignorID) {
        //SQL query for finding records by consignorID
        String findRecordsSQLps =
                "SELECT * FROM " + AVAILABLE_RECORDS_VIEW +
                        " WHERE " + CONSIGNOR_ID + "=?";

        String findBargainRecordsSQLps =
                "SELECT * FROM " + BARGAIN_BSMT_TABLE +
                        " WHERE " + CONSIGNOR_ID + "=?";

        try {
            psFindRecords = conn.prepareStatement(findRecordsSQLps);
            allStatements.add(psFindRecords);
            psFindRecords.setInt(1, consignorID);

            rs = psFindRecords.executeQuery();
            if (rs.next()) {
                return true;
            }

            psFindBargainRecords = conn.prepareStatement(findBargainRecordsSQLps);
            allStatements.add(psFindBargainRecords);
            psFindBargainRecords.setInt(1, consignorID);

            rs = psFindBargainRecords.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException se) {
            System.err.println("Error preparing statement or executing prepared statement to find consignor.");
            System.err.println(se.getErrorCode() + " " + se.getMessage());

            return true;    //assume true, to be safe
        }

        return false;
    }
}
