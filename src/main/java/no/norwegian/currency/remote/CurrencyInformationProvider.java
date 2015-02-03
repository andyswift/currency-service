package no.norwegian.currency.remote;

import no.norwegian.currency.model.CurrencyInformation;

import java.util.Optional;

/**
 * Provides currency information.
 */
public class CurrencyInformationProvider implements Listener<CurrencyInformation> {

    private Optional<CurrencyInformation> currencyInformation = Optional.empty();

    @Override
    public void notify(CurrencyInformation currencyInformation) {
        this.currencyInformation = Optional.ofNullable(currencyInformation);
    }

    public Optional<CurrencyInformation> getCurrencyInformation() {
        return currencyInformation;
    }

}
