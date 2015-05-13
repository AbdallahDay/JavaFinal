package com.abdallah;

import javax.swing.*;
import java.awt.event.*;

public class AddConsignorDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField nameTextField;
    private JTextField phoneTextField;
    private JTextField emailTextField;

    View myView;

    public AddConsignorDialog(View view) {

        myView = view;

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
            String name = nameTextField.getText();
            String phone = phoneTextField.getText();
            String email = emailTextField.getText();
            double amoountPaid = 0f;
            double amountOwed = 0f;

            Consignor consignor = new Consignor(name, phone, email, amountOwed, amoountPaid);

            myView.addConsignor(consignor);

            dispose();
        }
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    boolean isValidInput() {
        return
                Validator.isPresent(nameTextField, "Name") &&
                Validator.isPresent(phoneTextField, "Phone") &&
                Validator.isPresent(emailTextField, "Email") &&
                Validator.isInteger(phoneTextField, "Phone");
    }
}
