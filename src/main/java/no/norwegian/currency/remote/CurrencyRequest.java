package no.norwegian.currency.remote;

import no.norwegian.currency.model.CurrencyInformation;
import no.norwegian.currency.config.CurrencyServiceConfig;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * Makes a http request to retrieve Currency information
 */
public class CurrencyRequest implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(CurrencyRequest.class);
    private final CurrencyInformationPoller parent;
    private final CloseableHttpClient httpClient;
    private final String uri;
    private final int maxRetries;

    /**
     * @param parent The object to notify of success or failure when fetching currency info
     * @param config The containing the polling location and the http client
     */
    public CurrencyRequest(CurrencyInformationPoller parent, CurrencyServiceConfig config) {
        this.parent = parent;
        this.httpClient = config.httpClient;
        this.uri = config.pollingLocationUri;
        this.maxRetries = config.requestRetryLimit;
    }

    @Override
    public void run() {
        Optional<CurrencyInformation> currencyInformation = getWithRetries(maxRetries);

        if (currencyInformation.isPresent()) {
            parent.success(currencyInformation.get());
            return;
        }

        parent.fail();
    }

    //This is handled by http client so not actually necessary
    private Optional<CurrencyInformation> getWithRetries(int numberOfRetries) {
        Optional<CurrencyInformation> currencyInformation = Optional.empty();
        int attemptNumber = 0;
        while (attemptNumber < numberOfRetries && !currencyInformation.isPresent()) {
            currencyInformation = execute();
            if (!currencyInformation.isPresent()) {
                attemptNumber = waitAndIncrementAttemptNumber(attemptNumber);
            }
        }
        return currencyInformation;
    }

    public Optional<CurrencyInformation> execute() {
        HttpGet httpGet = new HttpGet(uri);
        log.trace("Making http request: " + httpGet);

        try(CloseableHttpResponse response = httpClient.execute(httpGet)) {

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                log.info("Request ok: " + httpGet);
                return getCurrencyInformation(response);
            }
            else {
                log.warn("Request Not ok: " + httpGet);
                return Optional.empty();
            }

        } catch (IOException e) {
            log.warn("Request failed: " + httpGet,e);
            return Optional.empty();
        }
    }

    private Optional<CurrencyInformation> getCurrencyInformation(CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();

        String content = EntityUtils.toString(entity);
        EntityUtils.consume(entity);

        return Optional.of(new CurrencyInformation(content));
    }

    private int waitAndIncrementAttemptNumber(final int attemptNumber) {
        try {
            Thread.sleep(100L);
            return attemptNumber + 1;
        } catch (InterruptedException e) {
            //interrupted so don't try again
            return maxRetries;
        }
    }
}
