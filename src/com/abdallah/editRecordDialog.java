package com.abdallah;

import javax.swing.*;
import java.awt.event.*;

public class editRecordDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField titleTextField;
    private JTextField artistTextField;
    private JTextField priceTextField;

    View myView;
    Record myRecord;

    public editRecordDialog(Record record, View view) {

        myView = view;
        myRecord = record;

        titleTextField.setText(record.getTitle());
        artistTextField.setText(record.getArtist());
        priceTextField.setText(String.format("%.2f", record.getPrice()));

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

            myRecord.setTitle(title);
            myRecord.setArtist(artist);
            myRecord.setPrice(price);

            myView.editRecord(myRecord);

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
