package no.norwegian.currency.remote;

/**
 * Implemented by objects which request information repeatedly
 */
public interface Poller<T> {

    /**
     * Starts polling
     */
    void start();

    /**
     * Stops polling
     */
    void stop();

    /**
     * Subscribe to the results of the polling
     */
    void subscribe(Listener<T> listener);
}
