package it.trvi.vendorsftpconnection;

public class MessageForwardingException  extends Exception{
    public MessageForwardingException(String errorMessage) {
        super("An error occurred while sending the message, the details follow:\n"+errorMessage);
    }
}
