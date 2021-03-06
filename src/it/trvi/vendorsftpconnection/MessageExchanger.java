package it.trvi.vendorsftpconnection;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

/**
 * Abstract class that defines a simple tool to send and receive String messages.
 */
public abstract class MessageExchanger implements AutoCloseable{
    /**
     * Sends a message.
     *
     * @param msg contains the message to be sent
     * @throws MessageForwardingException if an error prevents the sending of the message
     */
    public abstract void send(String msg) throws MessageForwardingException;

    /**
     * If there's a message waiting to be received, it immediately returns it. Otherwise, it throws a NoSuchElemetException.
     *
     * @return the received message
     * @throws MessageReceptionException if an error prevents the sending of the message
     * @throws NoSuchElementException if there's no message waiting to be received
     */
    public abstract String receive() throws MessageReceptionException, NoSuchElementException;

    /**
     * If there's a message waiting to be received, it immediately returns it. Otherwise, it waits until such a message exists, then it returns it.
     * It tries to receive a message every waitTime milliseconds.
     *
     * @param waitTime is the time, in milliseconds, that it must wait between each reception attempt
     * @return the received message
     * @throws MessageReceptionException if an error prevents the receiving of the message
     */
    public String waitForMessage(int waitTime) throws MessageReceptionException{
        while(true){
            try{//it tries to receive a message
                return this.receive();
            } catch (NoSuchElementException e){//if there's no message...
                try{
                    Thread.sleep(waitTime);//...it waits waitTime milliseconds and then tries again
                }catch(InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * If there's a message waiting to be received, it immediately returns it. Otherwise, it waits at most totalWaitTime milliseconds for a message to arrive.
     * If the message arrives within totalWaitTime milliseconds, then it returns it as soon as it receives it, otherwise it throws a TimeoutException.
     * It tries to receive a message every waitTime milliseconds.
     *
     * @param totalWaitTime is the maximum amount of milliseconds that must be waited. If it's not a multiple of waitTime, it's approximated to the following multiple of waitTime
     * @param waitTime is the time, in milliseconds, that it must wait between each reception attempt
     * @return the received message
     * @throws MessageReceptionException if an error prevents the receiving of the message
     * @throws TimeoutException if it doesn't receive a message withing millis milliseconds (approximated as explained above)
     */
    public String waitForMessageForMilliseconds(int totalWaitTime, int waitTime) throws MessageReceptionException, TimeoutException {
        int i;
        for(i=0;i<totalWaitTime;i+=waitTime){
            try{//it tries to receive a message
                return this.receive();
            } catch (NoSuchElementException e){//if there's no message...
                try{
                    Thread.sleep(waitTime);//...it waits waitTime milliseconds and then tries again
                }catch(InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new TimeoutException("No message has been received in the last "+i+" milliseconds.");
    }

    /**
     * If the underlying communication is connection-oriented, it closes the connection. Otherwise, it does nothing.
     * If the connection is already closed, it does nothing.
     */
    public abstract void close();
}
