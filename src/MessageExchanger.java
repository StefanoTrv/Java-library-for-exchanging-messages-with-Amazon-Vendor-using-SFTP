package message_exchanging_utilities;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

/*
Classe astratta che definisce un semplice strumento per inviare e ricevere messaggi sotto forma di stringa.
 */

public abstract class MessageExchanger implements AutoCloseable{
    /*
    Invia un messaggio
    Parametri: msg contiene il messaggio da inviare
    Throws: message_exchanging_utilities.MessageForwardingException quando un errore impedisce l'invio del messaggio
     */
    public abstract void send(String msg) throws MessageForwardingException;

    /*
    Se c'è un messaggio in attesa di essere ricevuto, lo ritorna subito. Altrimenti lancia NoSuchElemetException.
    Return: il messaggio ricevuto
    Throws: message_exchanging_utilities.MessageReceptionException se un errore impedisce la ricezione del messaggio
            NoSuchElementException se non c'è alcun messaggio in attesa di essere ricevuto
     */
    public abstract String receive() throws MessageReceptionException, NoSuchElementException;

    /*
    Se c'è un messaggio in attesa di essere ricevuto, lo ritorna subito. Altrimenti attende fino a quando non esiste tale messaggio, e quindi lo ritorna.
    Cerca di ricevere il messaggio ogni waitTime millisecondi.
    Parametri:  waitTime è il tempo che deve attendere tra ogni tentativo di ricezione.
    Return: il messaggio ricevuto
    Throws: message_exchanging_utilities.MessageReceptionException se un errore impedisce la ricezione del messaggio
     */
    public String waitForMessage(int waitTime) throws MessageReceptionException{
        while(true){
            try{//tenta di ricevere un messaggio
                return this.receive();
            } catch (NoSuchElementException e){//se non c'è alcun messaggio...
                try{
                    Thread.sleep(waitTime);//...attende waitTime millisecondi e poi riprova
                }catch(InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /*
    Se c'è un messaggio in attesa di essere ricevuto, lo ritorna subito. Altrimenti attende al massimo millis millisecondi l'arrivo del messaggio.
    Se il messaggio arriva entro millis millisecondi, allora lo ritorna appena lo riceve, altrimenti lancia una TimeoutException.
    Il metodo tenta di ricevere il messaggio ogni waitTime millisecondi.
    Parametri: millis sono i millisecondi da aspettare. Se non sono un multiplo di waitTime, vengono approssimati al successivo multiplo di waitTime.
                waitTime è il tempo che deve attendere tra un tentativo e l'altro.
    Return: il messaggio ricevuto
    Throws: message_exchanging_utilities.MessageReceptionException se un errore impedisce la ricezione del messaggio
            TimeoutException se non riceve un messaggio entro millis (approssimato come descritto sopra)
     */
    public String waitForMessageForMilliseconds(int totalWaitTime, int waitTime) throws MessageReceptionException, TimeoutException {
        int i;
        for(i=0;i<totalWaitTime;i+=waitTime){
            try{//tenta di ricevere un messaggio
                return this.receive();
            } catch (NoSuchElementException e){//se non c'è alcun messaggio...
                try{
                    Thread.sleep(waitTime);//...attende un terzo di secondo e poi riprova
                }catch(InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new TimeoutException("Non è stato rivevuto nessun messaggio negli ultimi "+i+" millisecondi.");
    }

    /*
    Se la connessione sottostante è basata sulla connessione, chiude la connessione. Altrimenti non fa nulla.
    Se la connesione è gia chiusa, non fa nulla.
     */
    public abstract void close();
}
