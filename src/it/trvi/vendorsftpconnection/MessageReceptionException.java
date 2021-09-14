package it.trvi.vendorsftpconnection;

public class MessageReceptionException  extends Exception{
    public MessageReceptionException(String errorMessage) {
        super("An error occurred while receiving the message, the details follow:\n"+errorMessage);
    }
}
