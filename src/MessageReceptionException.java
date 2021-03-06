package message_exchanging_utilities;

public class MessageReceptionException  extends Exception{
    public MessageReceptionException(String errorMessage) {
        super("Errore durante la ricezione del messaggio, di seguito i dettagli:\n"+errorMessage);
    }
}
