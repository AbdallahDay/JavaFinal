package com.abdallah;

import javax.swing.*;
import java.awt.event.*;

public class EditBargainRecordDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField titleTextField;
    private JTextField artistTextField;

    private View myView;
    private Record myRecord;

    public EditBargainRecordDialog(Record record, View view) {

        myView = view;
        myRecord = record;

        titleTextField.setText(record.getTitle());
        artistTextField.setText(record.getArtist());

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

            myRecord.setTitle(title);
            myRecord.setArtist(artist);

            myView.editRecord(DatabaseModel.BARGAIN_BSMT_TABLE, myRecord);

            myView.updateUI();

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
                Validator.isPresent(artistTextField, "Artist");
    }
}
