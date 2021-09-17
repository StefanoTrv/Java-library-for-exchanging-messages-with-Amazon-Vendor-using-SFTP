package it.trvi.vendorsftpconnection;

/**
 * A simple exception that is thrown when a message cannot be received because of some error.
 */
public class MessageReceptionException  extends Exception{
    public MessageReceptionException(String errorMessage) {
        super("An error occurred while receiving the message, the details follow:\n"+errorMessage);
    }
}
