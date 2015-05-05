package com.abdallah;

import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * Created by DayDay on 4/30/2015.
 */
public class InventoryModel {

    //JDBC driver name and protocol, used to create a database connection
    private static String protocol = "jdbc:derby:";
    private static String dbName = "recordInventoryDB";

    //Database credentials
    private static final String USER = "admin";
    private static final String PASS = "admin";

    InventoryController myController;

    Statement statement = null;
    Connection conn = null;
    ResultSet rs = null;

    ArrayList<Statement> allStatements = new ArrayList<Statement>();

    //Prepared statements
    PreparedStatement psAddRecord = null;
    PreparedStatement psAddConsignor = null;
    PreparedStatement psAddSale = null;

    PreparedStatement psFindRecord = null;
    PreparedStatement psFindConsignor = null;

    PreparedStatement psDeleteRecord = null;
    PreparedStatement psDeleteConsignor = null;

    PreparedStatement psUpdateRecord = null;
    PreparedStatement psUpdateConsignor = null;

    public InventoryModel(InventoryController controller) {
        this.myController = controller;
    }

    public boolean setupDatabase() {
        // default is to setup without deleting
        // tables if they already exist,
        // Use second method for testing purposes only
        return setupDatabase(false);
    }

    public boolean setupDatabase(boolean deleteAndRecreate) {
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

        //At this point, everything works
        return true;
    }

    private void createConnection() throws SQLException {
        conn = DriverManager.getConnection(protocol + dbName + ";create=true", USER, PASS);
        statement = conn.createStatement();
        allStatements.add(statement);
    }

    private void createRecordsTable (boolean deleteAndRecreate) throws SQLException {
        String createRecordsTableSQL = "CREATE TABLE Records (recordID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY, title varchar(50), artist varchar(50), consignorID int, dateConsigned date, price double)";
        String deleteTableSQL = "DROP TABLE Records";

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
        String createConsignorsTableSQL = "CREATE TABLE Consignors (consignorID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY, consignorName varchar(50), phone varchar(20), email varchar(50), amountOwed double, totalPaid double)";
        String deleteTableSQL = "DROP TABLE Consignors";

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
        String createSalesTableSQL = "CREATE TABLE Sales (recordID int, title varchar(50), artist varchar(50), consignorID int, dateConsigned date, price double, dateSold date)";
        String deleteTableSQL = "DROP TABLE Sales";

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

    public boolean addRecord (Record record) {

        //Create SQL query to add this record info to DB

        String addRecordSQLps = "INSERT INTO Records (title, artist, consignorID, dateConsigned, price) VALUES (?, ?, ?, ?, ?)";

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

        String addConsignorSQLps = "INSERT INTO Consignors (consignorName, phone, email, amountOwed, totalPaid) VALUES (?, ?, ?, ?, ?)";

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

    public boolean moveToSales(int recordID, Date dateSold) {
        //make sure record exists
        if (!recordExists(recordID)) return false;

        //SQL query for moving a record from records table to sales table
        String addSaleSQLps = "INSERT INTO Sales (recordID, title, artist, consignorID, dateConsigned, price, dateSold) VALUES ((SELECT * FROM Records WHERE recordID=?), ?)";

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

    public boolean deleteRecord(int recordID) {
        //make sure record exists
        if (!recordExists(recordID)) return false;

        // SQL query for deleting record from DB
        String deleteRecordSQLps = "DELETE FROM Records WHERE recordID=?";

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
        if (!consignorExists(consignorID)) return false;

        // if we're here, consignor was found in DB

        // SQL query for deleting consignor from DB
        String deleteConsignorSQLps = "DELETE FROM Consignors WHERE consignorID=?";

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
        if (!recordExists(recordID)) return false;

        // SQL query for updating record
        String updateRecordSQLps = "UPDATE Records SET title=?,artist=?,consignorID=?,dateConsigned=?,price=? WHERE recordID=?";

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
        if (!consignorExists(consignorID)) return false;

        // SQL query for updating consignor
        String updateConsignorSQLps = "UPDATE Consignor SET consignorName=?,phone=?,email=?,amountOwed=?,totalPaid=? WHERE consignorID=?";

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

    //TODO: method for moving a record to the bargain basement

    //TODO: method for paying a consignor

    private boolean recordExists(int recordID) {
        // SQL query for finding record in DB
        String findRecordSQLps = "SELECT * FROM Records WHERE recordID=?";

        try {
            psFindRecord = conn.prepareStatement(findRecordSQLps);
            allStatements.add(psFindRecord);
            psFindRecord.setInt(1, recordID);

            rs = psFindRecord.executeQuery();
            if (!rs.next()) {
                //TODO: display error message
                System.out.println("Could not find record with ID #" + recordID);
                return false;
            }
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error preparing statement or executing prepared statement to find record.");
            System.err.println(se.getErrorCode() + " " + se.getMessage());

            return false;
        }

        //record exists
        return true;
    }

    private boolean consignorExists(int consignorID) {
        // SQL query for finding consignor in DB
        String findConsignorSQLps = "SELECT * FROM Consignors WHERE consignorID=?";

        try {
            psFindConsignor = conn.prepareStatement(findConsignorSQLps);
            allStatements.add(psFindConsignor);
            psFindConsignor.setInt(1, consignorID);

            rs = psFindConsignor.executeQuery();
            if (!rs.next()) {
                //TODO: display error message
                System.out.println("Could not find consignor with ID #" + consignorID);
                return false;
            }
        } catch (SQLException se) {
            //TODO: display error message
            System.err.println("Error preparing statement or executing prepared statement to find consignor.");
            System.err.println(se.getErrorCode() + " " + se.getMessage());

            return false;
        }

        //consignor exists
        return true;
    }
}
