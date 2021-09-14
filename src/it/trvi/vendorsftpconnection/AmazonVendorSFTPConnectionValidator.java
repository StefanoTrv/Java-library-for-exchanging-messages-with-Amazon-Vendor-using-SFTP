/*
Class use to validate a new SFTP connection with the Amazon Vendor server, by passing the tests for the connection required by Amazon.
 */
package it.trvi.vendorsftpconnection;

import javax.swing.*;
import java.io.IOException;

public class AmazonVendorSFTPConnectionValidator {

    /*
    Validates a new SFTP connection with the Amazon Vendor server.
    It can receive zero, one or three arguments:
    - The first argument is the path of the settings file.
    - The second and the third arguments are, respectively, the address and port of Amazon's SFTP server.
    If it receives only two arguments, it ignores the second one and behaves as if it only received one.
    A popup will appear asking for the passphrases.
     */
    public static void main(String[] args){
        String settingsFilePath = "settings_and_keys/AmazonVendorConnectionSettings.txt";
        if(args.length>0){
            settingsFilePath=args[0];
        }
        if(args.length>=3){
            validateConnection(settingsFilePath, args[1], Integer.parseInt(args[2]));
        } else {
            validateConnection(settingsFilePath);
        }
    }

    /*
    It validates a new SFTP connection with the Amazon Vendor server, by passing the tests for the connection required by Amazon.
    It uses the settings contained in the file in settingsFilePath and the default address and port for Europe.
    A popup will appear asking for the passphrases.
     */
    public static void validateConnection(String settingsFilePath) {
        validateConnection(settingsFilePath,"sftp-eu.amazonsedi.com", 2222);
    }

    /*
    It validates a new SFTP connection with the Amazon Vendor server, by passing the tests for the connection required by Amazon.
    It uses the settings contained in the file in settingsFilePath and the address and port specified in, respectively, host and port.
    A popup will appear asking for the passphrases.
     */
    public static void validateConnection(String settingsFilePath, String host, int port) {
        SFTPBasedMessageExchangerForAmazonVendor connectionToVendor = null;
        try {
            //Asks the passphrases to the user
            String passphraseDown = JOptionPane.showInputDialog("Insert the password for the download connection with the Amazon Vendor server:");

            String passphraseUp = JOptionPane.showInputDialog("Insert the password for the upload connection with the Amazon Vendor server:");

            connectionToVendor = new SFTPBasedMessageExchangerForAmazonVendor(settingsFilePath, passphraseDown, passphraseUp, host, port);

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
