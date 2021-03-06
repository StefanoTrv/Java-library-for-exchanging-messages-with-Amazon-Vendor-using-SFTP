package message_exchanging_utilities;

public class ConnectionException extends Exception{
    public ConnectionException(String errorMessage) {
        super("Errore durante la creazione della connessione, di seguito i dettagli:\n"+errorMessage);
    }
}
