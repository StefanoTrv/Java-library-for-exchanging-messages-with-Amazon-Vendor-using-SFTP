package message_exchanging_utilities;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Vector;

/*
MessageExchanger utilizzato per inviare e ricevere messaggi dai server di Amazon Vendor attraverso il protocollo SFTP.
 */
public class SFTPBasedMessageExchangerForAmazonVendor extends MessageExchanger{
    private Session sessionDown;
    private ChannelSftp sftpChannelDown;
    private Session sessionUp;
    private ChannelSftp sftpChannelUp;
    /*
    Stabilisce una connessione in upload e download con i server di Amazon basandosi sulle impostazioni contenute nel file in settingsFilePath.
    Throws: ConnectionException se si verifica un errore durante l'instaurazione della connessione.
            IOException se si verifica un errore durante la lettura del file con le impostazioni
     */
    public SFTPBasedMessageExchangerForAmazonVendor(String settingsFilePath, String passphrase) throws ConnectionException, IOException {
        ConnectionSettings settings=new ConnectionSettings(settingsFilePath);
        JSch jsch = new JSch();

        //configurazione delle connessioni
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");

        try{
            //Instauro la connessione in download
            jsch.addIdentity(settings.privateKeyDown,passphrase+"D");
            sessionDown = jsch.getSession( settings.usernameDown, "sftp-eu.amazonsedi.com", 2222 );
            sessionDown.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
            sessionDown.setConfig(config);
            sessionDown.connect();
            Channel channelDown = sessionDown.openChannel( "sftp" );
            channelDown.connect();
            sftpChannelDown = (ChannelSftp) channelDown;

            //Instauro la connessione in upload
            jsch.addIdentity(settings.privateKeyUp,passphrase+"U");
            sessionUp = jsch.getSession( settings.usernameUp, "sftp-eu.amazonsedi.com", 2222 );
            sessionUp.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
            sessionUp.setConfig(config);
            sessionUp.connect();
            Channel channelUp = sessionUp.openChannel( "sftp" );
            channelUp.connect();
            sftpChannelUp = (ChannelSftp) channelUp;
        }catch (Exception e){
            throw new ConnectionException(e.getMessage());
        }

    }

    @Override
    public void send(String msg) throws MessageForwardingException {
        send(msg,"newMessage"+Math.random());
    }

    /*
        Versione di send() in cui si può specificare il nome che avrà il file sul server remoto, utile solo in casi di test.
     */
    public void send(String msg, String destinationFileName) throws MessageForwardingException {
        try{
            //creo file locale temporaneo contenente il messagio
            String localFileName = writeTempFile(msg);

            //invio il file
            sftpChannelUp.put(localFileName,"upload/"+destinationFileName);

            //elimino il file locale temporaneo
            File localFile = new File(localFileName);
            localFile.delete();
        }catch (Exception e){
            throw new MessageForwardingException(e.getMessage());
        }
    }

    @Override
    public String receive() throws MessageReceptionException, NoSuchElementException {
        ChannelSftp.LsEntry remoteFile = null;
        Vector<ChannelSftp.LsEntry> fileList = null;
        String msg = null;
        try{
            //trovo l'elenco dei file sul server remoto
            fileList = sftpChannelDown.ls("download");

            //trovo il file più vecchio, corrispondente al messaggio più vecchio
            for (ChannelSftp.LsEntry entry : fileList)
            {
                if (!entry.getFilename().equals(".") && !entry.getFilename().equals(".."))
                {
                    if(remoteFile==null||entry.getAttrs().getMTime()<remoteFile.getAttrs().getMTime()){
                        remoteFile=entry;
                    }
                }
            }
        }catch (Exception e){
            throw new MessageReceptionException(e.getMessage());
        }

        if(remoteFile==null){
            throw new NoSuchElementException("Non ci sono messaggi da ricevere.");
        }else{
            try{
                //scarico su un file temporaneo il file trovato
                String localFileName = "tempDownloadFileAmazonVendor"+Math.random();
                sftpChannelDown.get("download/"+remoteFile.getFilename(), localFileName);

                //leggo il file
                msg = readFile(localFileName);

                //elimino da remoto e da locale il file
                sftpChannelDown.rm("download/"+remoteFile.getFilename());
                File localFile = new File(localFileName);
                localFile.delete();

                //restituisco il messaggio
                return msg;
            } catch (Exception e){
                throw new MessageReceptionException(e.getMessage());
            }
        }
    }

    @Override
    public void close(){
        sftpChannelDown.exit();
        sessionDown.disconnect();
        sftpChannelUp.exit();
        sessionUp.disconnect();
    }

    private static class ConnectionSettings{
        private String privateKeyDown;
        private String privateKeyUp;
        private String usernameDown;
        private String usernameUp;

        /*
        Legge il percorso delle chiavi private e il nome utente dal file delle impostazioni, quindi le inserisce negli attributi dell'oggetto.
        Parametri: il percorso del file delle impostazioni
        Throws: FileNotFoundException quando non trova il file
         */
        public ConnectionSettings(String settingsFilePath) throws IOException {
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(settingsFilePath)));
                this.usernameDown=reader.readLine();
                this.privateKeyDown=reader.readLine();
                this.usernameUp=reader.readLine();
                this.privateKeyUp=reader.readLine();
                reader.close();
            }catch (IOException e){
                throw new IOException("Errore durante la lettura del file delle impostazioni per Amazon Vendor.",e);
            }
        }

        public String getPrivateKeyDown() {
            return privateKeyDown;
        }

        public String getPrivateKeyUp() {
            return privateKeyUp;
        }

        public String getUsernameDown() {
            return usernameDown;
        }

        public String getUsernameUp() {
            return usernameUp;
        }
    }

    private String readFile(String filePath) throws IOException{
        StringBuilder content = new StringBuilder();
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            while (reader.ready()) {
                content.append(reader.readLine()).append("\n");
            }
            reader.close();
            return content.toString().substring(0,content.length()-1);//rimuovo l'ultimo a capo
        }catch (IOException e){
            throw new IOException("Errore durante la lettura del file delle impostazioni per Amazon Vendor.",e);
        }
    }

    private String writeTempFile(String msg) throws IOException{
        String localFileName = "tempUploadFileAmazonVendor"+Math.random();
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(localFileName), "UTF8");
        writer.write(msg);
        writer.close();
        return localFileName;
    }
}
