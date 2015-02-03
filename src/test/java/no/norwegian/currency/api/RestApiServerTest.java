package no.norwegian.currency.api;

import junit.framework.TestCase;
import no.norwegian.currency.remote.CurrencyInformationProvider;

import static org.hamcrest.MatcherAssert.assertThat;

public class RestApiServerTest extends TestCase {

    RestApiServer restApiServer = new RestApiServer(new CurrencyInformationProvider(), new CurrencyInformationProvider());

    @Override
    public void tearDown() throws Exception {
        restApiServer.stop();
    }

    public void testStart() throws Exception {
        restApiServer.start();
        assertThat("Did not get ping response", restApiServer.ping() );
    }

}