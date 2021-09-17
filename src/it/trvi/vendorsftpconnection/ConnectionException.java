package it.trvi.vendorsftpconnection;

/**
 * A simple exception that is thrown when a connection cannot be established.
 */
public class ConnectionException extends Exception{
    public ConnectionException(String errorMessage) {
        super("Error during the creation of the connection, the details follow:\n"+errorMessage);
    }
}
