package com.abdallah;

import javax.swing.*;
import java.awt.event.*;

public class PayConsignorDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField amountTextField;
    private JLabel amountOwedLabel;

    private View myView;
    private Consignor myConsignor;

    public PayConsignorDialog(Consignor consignor, View view) {

        myView = view;
        myConsignor = consignor;

        double amountOwed = myConsignor.getAmountOwed();

        amountOwedLabel.setText("You owe this consignor " + String.format("$%.2f", amountOwed));

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

            double amount = Double.parseDouble(amountTextField.getText());
            int consignorID = myConsignor.getConsignorID();

            myView.payConsignor(consignorID, amount);

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
                Validator.isPresent(amountTextField, "Amount") &&
                Validator.isNumeric(amountTextField, "Amount") &&
                Validator.numberIsWithinRange(amountTextField, "Amount", 0, myConsignor.getAmountOwed());
    }
}
