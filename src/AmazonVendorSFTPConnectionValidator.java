/*
Classe adibilita a convalidare una nuova connessione SFTP ai server di Amazon Vendor, superando i test di Amazon per la connessione.
 */
package miscellaneous_utilities;

import message_exchanging_utilities.*;

import javax.swing.*;
import java.io.IOException;

public class AmazonVendorSFTPConnectionValidator {
    public static void main(String[] args) {
        String settingsFilePath = "settings_and_keys/AmazonVendorConnectionSettings.txt";
        SFTPBasedMessageExchangerForAmazonVendor connectionToVendor = null;
        try {
            //Richiedo all'utente la passphrase
            String passphrase = JOptionPane.showInputDialog("Inserisci la password per la connessione con i server di Amazon Vendor:");

            connectionToVendor = new SFTPBasedMessageExchangerForAmazonVendor(settingsFilePath, passphrase);

            System.out.println("Connessione creata senza errori");

            System.out.println(connectionToVendor.receive());

            connectionToVendor.send("Questo è un test.","ConnectivityTest");

            System.out.println("Completato senza errori");
        } catch (ConnectionException e){
            System.out.println("C'è stato un errore durante la crezione della connessione");
            e.printStackTrace();
        } catch (IOException e){
            System.out.println("C'è stato un errore durante la lettura del file delle impostazioni");
            e.printStackTrace();
        } catch (MessageReceptionException e){
            System.out.println("C'è stato un errore durante la ricezione del messaggio");
            e.printStackTrace();
        } catch (MessageForwardingException e){
            System.out.println("C'è stato un errore durante l'invio del messaggio");
            e.printStackTrace();
        }finally{
            if(connectionToVendor!=null)connectionToVendor.close();
        }
    }
}
