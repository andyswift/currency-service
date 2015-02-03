package no.norwegian.currency.remote;

import no.norwegian.currency.api.RestApiServer;
import no.norwegian.currency.config.CurrencyServiceConfig;
import no.norwegian.currency.remote.CurrencyInformationPoller;
import no.norwegian.currency.remote.CurrencyInformationProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class CurrencyInformationPollerTest {

    private RestApiServer restApiServer = new RestApiServer(new CurrencyInformationProvider(), new CurrencyInformationProvider());

    private CurrencyInformationPoller currencyInformationPoller;

    @Before
    public void setUp() throws Exception {
        //Starts up the RestServer
        restApiServer.start();
    }

    @After
    public void tearDown() throws Exception {
        restApiServer.stop();
    }

    @Test
    public void shouldBeNotifiedWhenCurrencyInformationIsPresent() throws Exception {
        CurrencyInformationProvider listener = new CurrencyInformationProvider();

        //test config points to the restApiServers fake endpoint
        currencyInformationPoller = new CurrencyInformationPoller(CurrencyServiceConfig.testDailyConfig());
        currencyInformationPoller.subscribe(listener);
        currencyInformationPoller.start();

        int count = 0;
        while (!listener.getCurrencyInformation().isPresent() && count < 2) {
            count++;
            Thread.sleep(100L);
        }

        assertThat("Expected currency information",listener.getCurrencyInformation().isPresent());
    }
}