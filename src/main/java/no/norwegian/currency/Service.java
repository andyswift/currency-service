package no.norwegian.currency;

import no.norwegian.currency.api.RestApiServer;
import no.norwegian.currency.config.CurrencyServiceConfig;
import no.norwegian.currency.remote.CurrencyInformationPoller;
import no.norwegian.currency.remote.CurrencyInformationProvider;

import static no.norwegian.currency.config.CurrencyServiceConfig.prodDailyConfig;
import static no.norwegian.currency.config.CurrencyServiceConfig.prodHistoricalConfig;

/**
 * Polls DNB for daily and historical currency information and makes it
 * available through a Rest API.
 *
 * curl localhost:4567/currency/historical
 * curl localhost:4567/currency/daily
 *
 * The CurrencyInformationPollers notify the RestApiServer of currency information using a listener which provides
 * this information to the server.
 */
public class Service {

    public static void main( String[] args ) {

        CurrencyInformationProvider dailyInformationProvider = startPollingOfDailyRate();
        CurrencyInformationProvider historicalInformationProvider = startPollingOfHistoricalData();

        //TODO: Once we have the daily rates and the historical rates we should stop polling, until the next day
        RestApiServer restApiServer = new RestApiServer(dailyInformationProvider,historicalInformationProvider);
        restApiServer.start();
    }

    private static CurrencyInformationProvider startPollingOfHistoricalData() {
        CurrencyServiceConfig historicalCurrencyConfig = prodHistoricalConfig();
        CurrencyInformationPoller historicalCurrencyInformation = new CurrencyInformationPoller(historicalCurrencyConfig);
        CurrencyInformationProvider historicalInformationProvider = new CurrencyInformationProvider();
        historicalCurrencyInformation.subscribe(historicalInformationProvider);
        historicalCurrencyInformation.start();
        return historicalInformationProvider;
    }

    private static CurrencyInformationProvider startPollingOfDailyRate() {
        CurrencyServiceConfig dailyCurrencyConfig = prodDailyConfig();
        CurrencyInformationPoller dailyCurrencyInformation = new CurrencyInformationPoller(dailyCurrencyConfig);
        CurrencyInformationProvider dailyInformationProvider = new CurrencyInformationProvider();
        dailyCurrencyInformation.subscribe(dailyInformationProvider);
        dailyCurrencyInformation.start();
        return dailyInformationProvider;
    }
}
