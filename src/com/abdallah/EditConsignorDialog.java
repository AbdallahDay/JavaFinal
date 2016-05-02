package com.abdallah;

import javax.swing.*;
import java.awt.event.*;

public class EditConsignorDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField nameTextField;
    private JTextField phoneTextField;
    private JTextField emailTextField;

    private View myView;
    private Consignor myConsignor;

    public EditConsignorDialog(Consignor consignor, View view) {

        myConsignor = consignor;
        myView = view;

        nameTextField.setText(myConsignor.getConsignorName());
        phoneTextField.setText(myConsignor.getPhone());
        emailTextField.setText(myConsignor.getEmail());

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

            myConsignor.setConsignorName(name);
            myConsignor.setPhone(phone);
            myConsignor.setEmail(email);

            myView.editConsignor(myConsignor);

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
                Validator.isPresent(nameTextField, "Name") &&
                Validator.isPresent(phoneTextField, "Phone") &&
                Validator.isInteger(phoneTextField, "Phone") &&
                Validator.isCorrectLength(phoneTextField, 10, "Phone") &&
                Validator.isPresent(emailTextField, "Email") &&
                Validator.isValidEmailAddress(emailTextField);
    }
}
