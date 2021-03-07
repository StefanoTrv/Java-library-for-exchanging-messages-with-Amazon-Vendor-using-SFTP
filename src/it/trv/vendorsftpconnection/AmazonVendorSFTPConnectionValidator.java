/*
Class use to validate a new SFTP connection with the Amazon Vendor server, by passing the tests for the connection required by Amazon.
 */
package it.trv.vendorsftpconnection;

import javax.swing.*;
import java.io.IOException;

public class AmazonVendorSFTPConnectionValidator {
    public static void main(String[] args) {
        String settingsFilePath = "settings_and_keys/AmazonVendorConnectionSettings.txt";
        SFTPBasedMessageExchangerForAmazonVendor connectionToVendor = null;
        try {
            //Asks the passphrase to the user
            String passphrase = JOptionPane.showInputDialog("Insert the password for the connection with the Amazon Vendor server:");

            connectionToVendor = new SFTPBasedMessageExchangerForAmazonVendor(settingsFilePath, passphrase);

            System.out.println("Connection created with not errors");

            System.out.println(connectionToVendor.receive());

            connectionToVendor.send("This is a test.","ConnectivityTest");

            System.out.println("Validation completed with no errors.");
        } catch (ConnectionException e){
            System.out.println("There was an error during the creation of the connection.");
            e.printStackTrace();
        } catch (IOException e){
            System.out.println("There was an error while reading the settings file.");
            e.printStackTrace();
        } catch (MessageReceptionException e){
            System.out.println("There was an error during the receiving of the message.");
            e.printStackTrace();
        } catch (MessageForwardingException e){
            System.out.println("There was an error during the sending of the message.");
            e.printStackTrace();
        }finally{
            if(connectionToVendor!=null)connectionToVendor.close();
        }
    }
}
