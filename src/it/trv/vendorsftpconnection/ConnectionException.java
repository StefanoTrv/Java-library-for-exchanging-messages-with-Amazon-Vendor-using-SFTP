package it.trv.vendorsftpconnection;

public class ConnectionException extends Exception{
    public ConnectionException(String errorMessage) {
        super("Error during the creation of the connection, the details follow:\n"+errorMessage);
    }
}
