package com.abdallah;

import javax.swing.*;
import java.awt.event.*;
import java.sql.Date;
import java.util.ArrayList;

public class AddRecordDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField titleTextField;
    private JTextField artistTextField;
    private JTextField priceTextField;
    private JComboBox consignorComboBox;

    private View myView;

    public AddRecordDialog(ArrayList<Consignor> allConsignors, View view) {

        myView = view;

        if (allConsignors == null || allConsignors.isEmpty()) {
            View.messageBox("Could not load a list of consignors, adding a record requires a consignor.", "Error");
            dispose();
        }

        DefaultComboBoxModel<Consignor> consignorListModel = new DefaultComboBoxModel<Consignor>();

        for (Consignor consignor : allConsignors) {
            consignorListModel.addElement(consignor);
        }

        consignorComboBox.setModel(consignorListModel);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        if (isValidInput()) {
            String title = titleTextField.getText();
            String artist = artistTextField.getText();
            double price = Double.parseDouble(priceTextField.getText());

            Consignor consignor = (Consignor)consignorComboBox.getSelectedItem();
            int consignorID = consignor.getConsignorID();

            Date today = new Date(new java.util.Date().getTime());

            Record record = new Record(title, artist, consignorID, today, price);

            myView.addRecord(record);

            dispose();
        }
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    boolean isValidInput() {
        return
                Validator.isPresent(titleTextField, "Title") &&
                Validator.isPresent(artistTextField, "Artist") &&
                Validator.isPresent(priceTextField, "Price") &&
                Validator.isNumeric(priceTextField, "Price");
    }
}
