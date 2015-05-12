package com.abdallah;

import javax.swing.*;

public class View {
    private JTabbedPane tabbedPane1;
    public JPanel panel1;
    private JPanel recordsTab;
    private JPanel consignorsTab;
    private JScrollPane recordsScrollPane;
    private JScrollPane bargainBasementScrollPane;
    private JPanel centerPanel;
    private JPanel bottomPanel;
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

    Controller myController;
    JFrame mainFrame = null;

    public View(Controller controller) {
        myController = controller;
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

    //TODO: call db cleanup before closing

    //TODO: method for paying a consignor

    //TODO: method for selling a record
}
