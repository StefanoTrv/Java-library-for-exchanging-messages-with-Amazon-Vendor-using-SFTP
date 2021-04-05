package it.trv.vendorsftpconnection;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Vector;

/*
A Message Exchanger used to send and receive messages to and from the Amazon Vendor server through the SFTP protocol.
 */
public class SFTPBasedMessageExchangerForAmazonVendor extends MessageExchanger{
    private Session sessionDown;
    private ChannelSftp sftpChannelDown;
    private Session sessionUp;
    private ChannelSftp sftpChannelUp;

    /*
    It establishes an upload and download connection with the Amazon server using the settings contained in the file in settingsFilePath and the default host and port for Europe.
    Parameters: settingsFilePath is the path to the settings file
                passphraseDown is the passphrase for the private keys for the download connection
                passphraseUp is the passphrase for the private keys for the upload connection
    Throws: ConnectionException if an error occurs while establishing the connection
            IOException if an error occurs while reading the settings file
     */
    public SFTPBasedMessageExchangerForAmazonVendor(String settingsFilePath, String passphraseDown, String passphraseUp) throws ConnectionException, IOException {
        this(settingsFilePath, passphraseDown, passphraseUp, "sftp-eu.amazonsedi.com", 2222);
    }

    /*
    It establishes an upload and download connection with the Amazon server using the settings contained in the file in settingsFilePath and the specified address and port of the SFTP server.
    Parameters: settingsFilePath is the path to the settings file
                passphraseDown is the passphrase for the private keys for the download connection
                passphraseUp is the passphrase for the private keys for the upload connection
                host is the address of the SFTP server
                port is the port of the SFTP server
    Throws: ConnectionException if an error occurs while establishing the connection
            IOException if an error occurs while reading the settings file
     */
    public SFTPBasedMessageExchangerForAmazonVendor(String settingsFilePath, String passphraseDown, String passphraseUp, String host, int port) throws ConnectionException, IOException {
        ConnectionSettings settings=new ConnectionSettings(settingsFilePath);
        JSch jsch = new JSch();

        //configuration of the connections
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");

        try{
            //Establishes a download connection
            jsch.addIdentity(settings.getPrivateKeyDown(),passphraseDown);
            sessionDown = jsch.getSession( settings.getUsernameDown(), host, port );
            sessionDown.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
            sessionDown.setConfig(config);
            sessionDown.connect();
            Channel channelDown = sessionDown.openChannel( "sftp" );
            channelDown.connect();
            sftpChannelDown = (ChannelSftp) channelDown;

            //Establishes an upload connection
            jsch.addIdentity(settings.getPrivateKeyUp(),passphraseUp);
            sessionUp = jsch.getSession( settings.getUsernameUp(), host, port );
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
    Version of send() in which the name of the file uploaded to the remote server can be chosen, useful only for testing.
     */
    public void send(String msg, String destinationFileName) throws MessageForwardingException {
        try{
            //creates a temporary file containing the message
            String localFileName = writeTempFile(msg);

            //sends the file
            sftpChannelUp.put(localFileName,"upload/"+destinationFileName);

            //deletes the local temporary file
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
            //finds the list of the files on the remote server
            fileList = sftpChannelDown.ls("download");

            //finds the older file, which corresponds to the oldest message
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
            throw new NoSuchElementException("There are no messages to be received.");
        }else{
            try{
                //downloads on a temporary file the file that was found
                String localFileName = "tempDownloadFileAmazonVendor"+Math.random();
                sftpChannelDown.get("download/"+remoteFile.getFilename(), localFileName);

                //reads the file
                msg = readFile(localFileName);

                //deletes both the remote and local copies of the file
                sftpChannelDown.rm("download/"+remoteFile.getFilename());
                File localFile = new File(localFileName);
                localFile.delete();

                //return the message
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

    /*
    Private class containing the settings read from the settings file.
     */
    private static class ConnectionSettings{
        private String privateKeyDown;
        private String privateKeyUp;
        private String usernameDown;
        private String usernameUp;

        /*
        Reads the path of the private keys and the user name from the settings file, then it puts them in the attributes of the object.
        Parameters: the path of the settings file
        Throws: IOException if an error occurs while reading the settings file
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
                throw new IOException("An error occurred while reading the file containing the settings for Amazon Vendor.",e);
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
            return content.toString().substring(0,content.length()-1);//removes the last new line
        }catch (IOException e){
            throw new IOException("An error occurred while reading the file at \'"+filePath+"\'.",e);
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
