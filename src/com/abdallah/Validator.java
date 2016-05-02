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

    public static boolean isCorrectLength(JTextField textField, int length, String name) {
        String s = textField.getText();

        if (s.length() != length) {
            View.messageBox(name + " must be " + length + " characters long.", "Entry Error");
            return false;
        }
        return true;
    }

    public static boolean numberIsWithinRange(JTextField textField, String name, double min, double max) {
        Double number = Double.parseDouble(textField.getText());
        if (number < min || number > max) {
            View.messageBox(name + " must be between " + min + " and " + max, "Entry Error");
            textField.grabFocus();
            return false;
        } else {
            return true;
        }
    }

    public static boolean isValidEmailAddress(JTextField textField) {
        String error = "Invalid email address";

        String s = textField.getText();
        String[] parts = s.split("@");

        if (parts.length != 2) {
            View.messageBox(error, "Error");
            textField.grabFocus();
            return false;
        }

        for (String part: parts) {
            if (part.isEmpty()) {
                View.messageBox(error, "Error");
                textField.grabFocus();
                return false;
            }
        }

        String domainPart = parts[1];

        String[] domainParts = domainPart.split("\\.");

        if (domainParts.length != 2) {
            View.messageBox(error, "Error");
            textField.grabFocus();
            return false;
        }

        for (String part: domainParts) {
            if (part.isEmpty()) {
                View.messageBox(error, "Error");
                textField.grabFocus();
                return false;
            }
        }

        return true;
    }
}
