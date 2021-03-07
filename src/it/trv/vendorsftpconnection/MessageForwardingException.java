package it.trv.vendorsftpconnection;

public class MessageForwardingException  extends Exception{
    public MessageForwardingException(String errorMessage) {
        super("Errore durante l'invio del messaggio, di seguito i dettagli:\n"+errorMessage);
    }
}
