package it.trvi.vendorsftpconnection;

/**
 * A simple exception that is thrown when a message cannot be sent.
 */
public class MessageForwardingException  extends Exception{
    public MessageForwardingException(String errorMessage) {
        super("An error occurred while sending the message, the details follow:\n"+errorMessage);
    }
}
