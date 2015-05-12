package com.abdallah;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Taken from 2901 Midterm project (book club app), code from Daryl Schmidt
 * url: https://github.com/MCTC-Projects/BookClub
 */
public class Emailer {

    public static String managerEmail;

    private static final String SENDER_FILE = "sender.txt";
    private static final String MANAGER_FILE = "manager.txt";

    //indexes of username and password (0-first line in text file, 1-second line in text file)
    private static final int USER_INDEX = 0;
    private static final int PASS_INDEX = 1;

    public static String sendEmail(String recipient, String subject, String emailBody) {

        String[] senderCredentials = getSenderCredentials();
        // if no errors from reading sender credentials from text file,
        // should return an array with two Strings (username and password)
        // otherwise returns an array with one String (being the error message)
        // SEE: getSenderCredentials() method

        if (senderCredentials.length == 1) return senderCredentials[0];

        //get manager email form text file
        String updateManagerEmailMsg = updateManagerEmail();
        if (updateManagerEmailMsg != null) return updateManagerEmailMsg;

        //get username and password
        final String USER = senderCredentials[USER_INDEX];
        final String PASS = senderCredentials[PASS_INDEX];

        // set the host smtp server
        String host = "smtp.gmail.com";
        String port = "587";    //smtp starttls port number for gmail

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        // Get the Session object with authentication
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USER, PASS);
                    }
                });

        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(USER));

            // Add recipient
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(recipient));


            message.addRecipient(Message.RecipientType.CC,      // Manager CCed on all emails sent to consignors
                    new InternetAddress(managerEmail));         // change if necessary

            // Set Subject: header field
            message.setSubject(subject);

            // Set email body text
            message.setText(emailBody);

            // Send message
            Transport.send(message);

            return null;    //all is good

        } catch (MessagingException e) {
            System.out.println(e.getMessage());
            return "Error compiling or sending email";
        }
    }

    private static String[] getSenderCredentials() {
        //get credentials for email sending from text file

        String filename = SENDER_FILE;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            String[] lines = new String[2];

            int i = 0;
            String line = reader.readLine();
            while (line != null) {
                lines[i] = line;
                i++;
                line = reader.readLine();
            }

            if (lines.length != 2) {
                //file is missing data or has incorrect format
                //correct format:
                //line1: username (example: username@gmail.com) --MUST BE GMAIL
                //line2: password (example: myPassword1234)
                return new String[] {"'" + filename + "' has insufficient data or incorrect format"};
            }

            return lines;

        } catch (FileNotFoundException fnf) {
            return new String[] {"File '" + filename + "' not found"};
        } catch (IOException ioe) {
            return new String[] {"Error reading from '" + filename + "'"};
        }
    }

    private static String updateManagerEmail() {
        //get store manager's email address from text file

        String filename = MANAGER_FILE;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            managerEmail = reader.readLine();

            return null;    //all is good
        } catch (FileNotFoundException fnf) {
            return "File '" + filename + "' not found";
        } catch (IOException ioe) {
            return "Error reading from '" + filename + "'";
        }
    }
}
