package com.abdallah;

import javax.swing.*;

/**
 * Created by DayDay on 5/12/2015.
 */
public class Validator {
    public static boolean isPresent(JTextField textField, String name) {
        if (textField.getText().isEmpty()) {
            View.messageBox(name + " is a required field.", "Entry Error");
            textField.grabFocus();
            return false;
        } else {
            return true;
        }
    }

    public static boolean isNumeric(JTextField textField, String name) {
        try {
            Double.parseDouble(textField.getText());
        } catch (NumberFormatException nfe) {
            View.messageBox(name + " can only contain a numeric value.", "Entry Error");
            textField.grabFocus();
            return  false;
        }
        return true;
    }

    public static boolean isInteger(JTextField textField, String name) {
        try {
            Long.parseLong(textField.getText());
        } catch (NumberFormatException nfe) {
            View.messageBox(name + " can only contain a numeric integer value.", "Entry Error");
            textField.grabFocus();
            return  false;
        }
        return true;
    }
}
