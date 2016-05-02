package com.abdallah;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class View {
    private JTabbedPane tabbedPane1;
    public JPanel panel1;
    private JPanel recordsTab;
    private JPanel consignorsTab;
    private JScrollPane recordsScrollPane;
    private JScrollPane bargainBasementScrollPane;
    private JPanel centerPanel;
    private JButton moveToBasementButton;
    private JList recordsList;
    private JList bargainBasementList;
    private JButton addRecordButton;
    private JButton editRecordButton;
    private JButton deleteRecordButton;
    private JList consignorsList;
    private JButton addConsignorButton;
    private JButton editConsignorButton;
    private JButton deleteConsignorButton;
    private JButton payConsignorButton;
    private JButton sellRecordButton;
    private JPanel salesTab;
    private JList salesList;
    private JButton bargainPriceButton;
    private JPanel bargainBasementTab;
    private JButton editBargainRecord;
    private JButton deleteBaragainRecord;
    private JButton sellBargainRecord;

    Controller myController;
    JFrame mainFrame = null;

    private View myView;

    public View(Controller controller) {
        myView = this;
        myController = controller;

        controller.getOldRecords();

        //prepare JLists and other UI components
        updateUI();

        /** Records Tab **/

        //Add record
        addRecordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Consignor> allConsignors = getAllConsignors();

                if (allConsignors.isEmpty()) {
                    View.messageBox("Could not load a list of consignors, adding a record requires a consignor.", "Error");
                } else {
                    AddRecordDialog dialog = new AddRecordDialog(allConsignors, myView);
                    dialog.setTitle("Add Record");
                    dialog.setVisible(true);
                }
            }
        });

        //Edit record
        editRecordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!recordsList.isSelectionEmpty()) {
                    Record record = (Record)recordsList.getSelectedValue();

                    EditRecordDialog dialog = new EditRecordDialog(record, myView);
                    dialog.setTitle("Edit Record");
                    dialog.setVisible(true);
                }
            }
        });

        //Delete record
        deleteRecordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!recordsList.isSelectionEmpty()) {
                    Record record = (Record) recordsList.getSelectedValue();
                    int recordID = record.getRecordID();

                    deleteRecord(DatabaseModel.RECORDS_TABLE, recordID);

                    updateUI();
                }
            }
        });

        //Sell record from records table
        sellRecordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!recordsList.isSelectionEmpty()) {
                    Record record = (Record) recordsList.getSelectedValue();
                    int recordID = record.getRecordID();

                    sellRecord(DatabaseModel.RECORDS_TABLE, recordID);

                    updateUI();
                }
            }
        });

        //Move record to bargain basement
        moveToBasementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!recordsList.isSelectionEmpty()) {
                    Record record = (Record) recordsList.getSelectedValue();
                    int recordID = record.getRecordID();

                    moveToBargainBasement(recordID);

                    updateUI();
                }
            }
        });

        /** Bargain Basement Tab **/

        //Change bargain price
        bargainPriceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SetBargainPriceDialog dialog = new SetBargainPriceDialog(new View(myController));
                dialog.setTitle("Set Bargain Price");
                dialog.setVisible(true);
            }
        });

        //Edit bargain basement record
        editBargainRecord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!bargainBasementList.isSelectionEmpty()) {
                    Record record = (Record)bargainBasementList.getSelectedValue();

                    EditBargainRecordDialog dialog = new EditBargainRecordDialog(record, myView);
                    dialog.setTitle("Edit Record");
                    dialog.setVisible(true);
                }
            }
        });

        //Delete bargain basement record
        deleteBaragainRecord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!bargainBasementList.isSelectionEmpty()) {
                    Record record = (Record) bargainBasementList.getSelectedValue();
                    int recordID = record.getRecordID();

                    deleteRecord(DatabaseModel.BARGAIN_BSMT_TABLE, recordID);

                    updateUI();
                }
            }
        });

        //Sell bargain basement record
        sellBargainRecord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!bargainBasementList.isSelectionEmpty()) {
                    Record record = (Record) bargainBasementList.getSelectedValue();
                    int recordID = record.getRecordID();

                    sellRecord(DatabaseModel.BARGAIN_BSMT_TABLE, recordID);

                    updateUI();
                }
            }
        });

        /** Consignors Tab **/

        //Add consignor
        addConsignorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddConsignorDialog dialog = new AddConsignorDialog(myView);
                dialog.setTitle("Add Consignor");
                dialog.setVisible(true);
            }
        });

        //Edit consignor
        editConsignorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!consignorsList.isSelectionEmpty()) {
                    Consignor consignor = (Consignor) consignorsList.getSelectedValue();

                    EditConsignorDialog dialog = new EditConsignorDialog(consignor, myView);
                    dialog.setTitle("Edit Consignor");
                    dialog.setVisible(true);
                }
            }
        });

        //Delete consignor
        deleteConsignorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!consignorsList.isSelectionEmpty()) {
                    Consignor consignor = (Consignor) consignorsList.getSelectedValue();
                    int consignorID = consignor.getConsignorID();

                    deleteConsignor(consignorID);

                    updateUI();
                }
            }
        });

        //Pay consignor
        payConsignorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!consignorsList.isSelectionEmpty()) {
                    Consignor consignor = (Consignor) consignorsList.getSelectedValue();

                    if (consignor.getAmountOwed() == 0f) {
                        messageBox("You do not owe this consignor any money!", "No payment required");
                    } else {
                        PayConsignorDialog dialog = new PayConsignorDialog(consignor, myView);
                        dialog.setTitle("Pay Consignor");
                        dialog.setVisible(true);
                    }
                }
            }
        });
    }

    public void launchUI() {
        //open GUI form
        if (mainFrame != null) {
            //dispose old frame if exists
            mainFrame.dispose();
        }

        mainFrame = new JFrame("mainForm");
        mainFrame.setContentPane(new View(myController).panel1);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();

        mainFrame.setTitle("Record Store (development version)");    //Form title

        mainFrame.setVisible(true);
    }

    public void updateUI() {
        setBargainPriceButtonText();
        updateJLists();
    }

    private void setBargainPriceButtonText() {
        String p = String.format("$%.2f", Controller.getBargainPrice());
        bargainPriceButton.setText(p);
        // BUG: for some reason, the button text doesn't update immediately,
        // will update the next time UpdateUI() is called, though
    }

    private void updateJLists() {
        //updates all JLists in main form
        populateRecordsJList();
        populateBargainBasementJList();
        populateConsignorsJList();
        populateSalesJList();
    }

    private void populateRecordsJList() {
        ArrayList<Record> records = getRecordsFromRecordsTable();

        recordsList.setModel(getRecordListModel(records));
    }

    private void populateBargainBasementJList() {
        ArrayList<Record> records = getRecordsFromBargainBasementTable();

        bargainBasementList.setModel(getRecordListModel(records));
    }

    private void populateConsignorsJList() {
        ArrayList<Consignor> consignors = getAllConsignors();

        consignorsList.setModel(getConsignorListModel(consignors));
    }

    private void populateSalesJList() {
        ArrayList<SoldRecord> records = getAllSales();

        salesList.setModel(getSalesListModel(records));
    }

    //Methods for communicating with controller
    public void addRecord(Record record) {
        String error = myController.requestAddRecord(record);

        if (error != null) {
            //error occurred
            messageBox("Could not add record to database.\n" + error, "Error");
        } else {
            messageBox("Record added successfully!", "Success!");
        }
    }

    public void addConsignor(Consignor consignor) {
        String error = myController.requestAddConsignor(consignor);

        if (error != null) {
            //error occurred
            messageBox("Could not add consignor to database.\n" + error, "Error");
        } else {
            messageBox("Consignor added successfully!", "Success!");
        }
    }

    private void moveToBargainBasement(int recordID) {
        String error = myController.moveToBargainBasement(recordID);

        if (error != null) {
            //error occurred
            messageBox("Could not move record to bargain basement.\n" + error, "Error");
        } else {
            messageBox("Record successfully moved to bargain basement!", "Success!");
        }
    }

    public void sellRecord(String fromTable, int recordID) {
        String error = myController.sellRecord(fromTable, recordID);

        if (error != null) {
            //error occurred
            messageBox("Could not sell this record.\n" + error, "Error");
        } else {
            messageBox("Record sold successfully!", "Success!");
        }
    }

    private void deleteRecord(String fromTable, int recordID) {
        String error = myController.requestDeleteRecord(fromTable, recordID);

        if (error != null) {
            //error occurred
            messageBox("Could not delete record from database.\n" + error, "Error");
        } else {
            messageBox("Record deleted successfully!", "Success!");
        }
    }

    private void deleteConsignor(int consignorID) {
        String error = myController.requestDeleteConsignor(consignorID);

        if (error != null) {
            //error occurred
            messageBox("Could not delete consignor from database.\n" + error, "Error");
        } else {
            messageBox("Consignor deleted successfully!", "Success!");
        }
    }

    public void editRecord(String fromTable, Record newData) {
        String error = myController.requestEditRecord(fromTable, newData);

        if (error != null) {
            //error occurred
            messageBox("Could not edit record in database.\n" + error, "Error");
        } else {
            messageBox("Record updated successfully!", "Success!");
        }
    }

    public void editConsignor(Consignor newData) {
        String error = myController.requestEditConsignor(newData);

        if (error != null) {
            //error occurred
            messageBox("Could not edit consignor in database.\n" + error, "Error");
        } else {
            messageBox("Consignor updated successfully!", "Success!");
        }
    }

    public void payConsignor(int consignorID, double amountPaid) {
        String error = myController.payConsignor(consignorID, amountPaid);

        if (error != null) {
            //error occurred
            messageBox("Could not edit consignor payment data in database.\n" + error, "Error");
        } else {
            messageBox("Consignor payment data updated successfully!", "Success!");
        }
    }

    private ArrayList<Record> getRecordsFromRecordsTable() {
        ArrayList<Record> records = myController.requestRecordsFromRecordsTable();

        if (records == null) {
            //error occurred
            messageBox("Error fetching record data from records table", "Error");
        }

        return records;
    }

    private ArrayList<Record> getRecordsFromBargainBasementTable() {
        ArrayList<Record> records = myController.requestBargainBasementRecords();

        if (records == null) {
            //error occurred
            messageBox("Error fetching record data from bargain basement table", "Error");
        }

        return records;
    }

    private ArrayList<Consignor> getAllConsignors() {
        ArrayList<Consignor> consignors = myController.requestAllConsignors();

        if (consignors == null) {
            //error occurred
            messageBox("Error fetching consignor data from consignors table", "Error");
        }

        return consignors;
    }

    private ArrayList<SoldRecord> getAllSales() {
        ArrayList<SoldRecord> records = myController.requestAllSales();

        if (records == null) {
            //error occurred
            messageBox("Error fetching record data from sales table", "Error");
        }

        return records;
    }

    private ArrayList<Record> getRecordsForConsignor(int consignorID) {
        ArrayList<Record> records = myController.requestRecordsByConsignor(consignorID);

        if (records == null) {
            //error occurred
            messageBox("Error fetching record data from database", "Error");
        }

        return records;
    }

    public DefaultListModel<Record> getRecordListModel(ArrayList<Record> records) {
        //populates a default list model object with record objects for use with JLists

        DefaultListModel<Record> recordListModel = new DefaultListModel<Record>();

        for (Record record : records) {
            recordListModel.addElement(record);
        }

        return recordListModel;
    }

    public DefaultListModel<SoldRecord> getSalesListModel(ArrayList<SoldRecord> records) {
        //populates a default list model object with sold record objects for use with JLists

        DefaultListModel<SoldRecord> recordListModel = new DefaultListModel<SoldRecord>();

        for (SoldRecord record : records) {
            recordListModel.addElement(record);
        }

        return recordListModel;
    }

    public DefaultListModel<Consignor> getConsignorListModel(ArrayList<Consignor> consignors) {
        //populates a default list model object with consignor objects for use with JLists

        DefaultListModel<Consignor> consignorListModel = new DefaultListModel<Consignor>();

        for (Consignor consignor : consignors) {
            consignorListModel.addElement(consignor);
        }

        return consignorListModel;
    }

    public static void messageBox(String message, String title)
    {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
