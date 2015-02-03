package no.norwegian.currency.remote;

import no.norwegian.currency.model.CurrencyInformation;
import no.norwegian.currency.config.CurrencyServiceConfig;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Makes requests for currency information at a configurable rate.
 */
public class CurrencyInformationPoller implements Poller<CurrencyInformation> {

    private final Set<Listener> listeners;
    private final CurrencyServiceConfig currencyServiceConfig;
    private final ScheduledExecutorService executorService;
    private final int pollingRateSeconds;
    private ScheduledFuture<?> requestHandle;

    public CurrencyInformationPoller(CurrencyServiceConfig currencyServiceConfig) {
        this.listeners = new HashSet<>();
        this.currencyServiceConfig = currencyServiceConfig;
        this.executorService = currencyServiceConfig.executorService;
        this.pollingRateSeconds = currencyServiceConfig.pollingRateSeconds;
    }

    @Override
    public void start() {
        if (requestHandle != null) {
            throw new IllegalStateException("Poller already started");
        }

        CurrencyRequest request = new CurrencyRequest(this, currencyServiceConfig);

        requestHandle = executorService.scheduleAtFixedRate(request, 0L, pollingRateSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        if (requestHandle != null) {
            requestHandle.cancel(true);
        }
        requestHandle = null;
    }

    @Override
    public void subscribe(Listener<CurrencyInformation> listener) {
        listeners.add(listener);
    }

    public void success(CurrencyInformation currencyInformation) {
        listeners.parallelStream()
            .forEach((listener) -> listener.notify(currencyInformation));
    }

    public void fail() {

    }
}
