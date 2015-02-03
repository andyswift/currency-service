package no.norwegian.currency.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Configuration for the currency service
 */
public class CurrencyServiceConfig {

    public CloseableHttpClient httpClient;
    public ScheduledExecutorService executorService;

    public String pollingLocationUri;

    //how often to check for updates (default 1 hour)
    public int pollingRateSeconds = 60*60*1;
    //the number of times to retry a request before giving up
    public int requestRetryLimit = 3;

    private CurrencyServiceConfig(CloseableHttpClient httpClient, ScheduledExecutorService executorService) {
        this.httpClient = httpClient;
        this.executorService = executorService;
    }

    public static CurrencyServiceConfig testDailyConfig() {
        CurrencyServiceConfig currencyServiceConfig = defaultConfig();
        currencyServiceConfig.pollingRateSeconds = 2;
        currencyServiceConfig.pollingLocationUri = "http://localhost:4567/fake/daily";
        return currencyServiceConfig;
    }

    public static CurrencyServiceConfig testHistoricalConfig() {
        CurrencyServiceConfig currencyServiceConfig = defaultConfig();
        currencyServiceConfig.pollingRateSeconds = 2;
        currencyServiceConfig.pollingLocationUri = "http://localhost:4567/fake/daily";
        return currencyServiceConfig;
    }

    public static CurrencyServiceConfig prodHistoricalConfig() {
        CurrencyServiceConfig currencyServiceConfig = defaultConfig();
        currencyServiceConfig.pollingLocationUri = "https://www.dnb.no/portalfront/datafiles/miscellaneous/csv/historiske_kurser.csv";
        return currencyServiceConfig;
    }

    public static CurrencyServiceConfig prodDailyConfig() {
        CurrencyServiceConfig currencyServiceConfig = defaultConfig();
        currencyServiceConfig.pollingLocationUri = "https://www.dnb.no/portalfront/datafiles/miscellaneous/csv/kursliste_ws.xml";;
        return currencyServiceConfig;
    }

    private static CurrencyServiceConfig defaultConfig() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        return new CurrencyServiceConfig(httpClient, executorService);
    }
}
