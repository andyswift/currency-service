package no.norwegian.currency.remote;

import no.norwegian.currency.api.RestApiServer;
import no.norwegian.currency.config.CurrencyServiceConfig;
import no.norwegian.currency.model.CurrencyInformation;
import no.norwegian.currency.remote.CurrencyInformationPoller;
import no.norwegian.currency.remote.CurrencyInformationProvider;
import no.norwegian.currency.remote.CurrencyRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static no.norwegian.currency.config.CurrencyServiceConfig.testDailyConfig;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CurrencyRequestTest {

    public static final String NOT_FOUND = "http://localhost:9999/doesNotExist";

    private CurrencyInformationPoller poller;
    private RestApiServer restApiServer;
    private CurrencyServiceConfig config;

    @Mock
    private CurrencyInformationProvider historicalCurrencyInformation;

    @Before
    public void setUp() throws Exception {
        poller = mock(CurrencyInformationPoller.class);
        config = testDailyConfig();
    }

    @After
    public void tearDown() throws Exception {
        if (restApiServer != null) {
            restApiServer.stop();
        }
    }

    @Test()
    public void shouldNotThrowExceptionWhenDNBNotAvailable() throws Exception {
        config.pollingLocationUri = NOT_FOUND;
        CurrencyRequest request = new CurrencyRequest(poller, config);
        request.run();
    }

    @Test
    public void shouldNotReturnCurrencyInformationWhenNotAvailable() throws Exception {
        config.pollingLocationUri = NOT_FOUND;
        CurrencyRequest request = new CurrencyRequest(poller, config);
        Optional<CurrencyInformation> currencyInformation = request.execute();
        assertThat(currencyInformation.isPresent(), is(false));

    }

    @Test
    public void shouldReturnCurrencyInformationWhenAvailable() throws Exception {
        //currency information available
        CurrencyInformationProvider dailyCurrencyInformation = mock(CurrencyInformationProvider.class);
        when(dailyCurrencyInformation.getCurrencyInformation()).thenReturn(Optional.of(new CurrencyInformation("hello")));
        restApiServer = new RestApiServer(dailyCurrencyInformation, historicalCurrencyInformation);
        restApiServer.start();

        CurrencyRequest request = new CurrencyRequest(poller, config);

        Optional<CurrencyInformation> currencyInformation = request.execute();
        assertThat(currencyInformation.isPresent(), is(true));
    }
}