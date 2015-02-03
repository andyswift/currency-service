package no.norwegian.currency.remote;

/**
 * Implemented by objects which are interested in CurrencyInformation
 */
public interface Listener<T> {
    public void notify(T currencyInformation);
}
