package no.norwegian.currency.api;

import no.norwegian.common.monitor.Flag;
import no.norwegian.common.monitor.SimpleStatus;
import no.norwegian.common.monitor.StaticStatus;
import no.norwegian.common.monitor.Status;
import no.norwegian.common.monitor.metrics.Measurement;
import no.norwegian.common.monitor.metrics.MetricRepository;
import no.norwegian.common.monitor.metrics.MetricRepositoryFactory;
import no.norwegian.common.monitor.status.JvmStatusIndicator;
import no.norwegian.common.monitor.status.StatusIndicator;
import no.norwegian.common.monitor.status.StatusIndicatorService;
import no.norwegian.currency.model.CurrencyInformation;
import no.norwegian.currency.remote.CurrencyInformationProvider;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static spark.Spark.get;
import static spark.Spark.head;

/**
 * Provides a REST API to access currency exchange rates.
 * The CurrencyInformation is provided by the CurrencyInformationProvider object
 */
public class RestApiServer {

    private final CurrencyInformationProvider dailyCurrencyInformation;
    private final CurrencyInformationProvider historicalCurrencyInformation;
    private MetricRepository averageMetricRepository;
    private StatusIndicatorService statusIndicatorService;

    public RestApiServer(CurrencyInformationProvider dailyCurrencyInformation, CurrencyInformationProvider historicalCurrencyInformation) {
        this.dailyCurrencyInformation = dailyCurrencyInformation;
        this.historicalCurrencyInformation = historicalCurrencyInformation;
    }

    public RestApiServer start() {

        setUpMonitoring();

        setUpRoutes();

        waitUntilStarted();

        return this;
    }

    private void setUpMonitoring() {
        averageMetricRepository = MetricRepositoryFactory.getAverageMetricRepository();

        Set<StatusIndicator> indicators = new LinkedHashSet<>();
        // Add default indicators
        indicators.add(new StatusIndicator() {
            @Override
            public Status status() {
                return new StaticStatus("1.0-SNAPSHOT");
            }
        });
        indicators.add(new JvmStatusIndicator());
        // Monitor the polling of currency information
        indicators.add(new CurrencyInformationIndicator("Daily currency rates", dailyCurrencyInformation));
        indicators.add(new CurrencyInformationIndicator("Historical currency rates", historicalCurrencyInformation));

        statusIndicatorService = new StatusIndicatorService(indicators);
    }

    private void waitUntilStarted() {
        int retry = 0;
        while (!ping() && retry < 3) {
            try {
                Thread.sleep(100L);
                retry++;
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void setUpRoutes() {
        //support ping
        head("/", (req, res) -> "Pong");

        //test data
        get("/fake/daily", (req, res) -> "daily");
        get("/fake/historical", (req, res) -> "historical");

        get("/currency/daily", (req, res) -> {
            Optional<CurrencyInformation> currencyInformation = dailyCurrencyInformation.getCurrencyInformation();
            if(currencyInformation.isPresent()) {
                return currencyInformation.get().toString();
            }
            res.status(404);
            return "Not Found!";
        });

        get("/currency/historical", (req, res) -> {
            Optional<CurrencyInformation> currencyInformation = historicalCurrencyInformation.getCurrencyInformation();
            if(currencyInformation.isPresent()) {
                return currencyInformation.get().toString();
            }
            res.status(404);
            return "Not Found!";
        });


        get("/monitor", (req, res) -> printMonitorPage());

        get("/status", (req, res) -> printStatusPage());

        get("/metrics", (req, res) -> printMetrics());

    }

    public RestApiServer stop() {
        spark.Spark.stop();
        return this;
    }

    /**
     * Pings a HTTP URL. This effectively sends a HEAD request and returns <code>true</code> if the response code is in
     * the 200-399 range.
     */
    public boolean ping() {

        // The url to ping
        String url = "http://localhost:4567";
        // timeout in millis
        int timeout = 100;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException exception) {
            return false;
        }
    }

    private String printMonitorPage() {
        StringBuilder page = new StringBuilder();
        Collection<Status> indicatedStatuses = statusIndicatorService.getStatus();
        for (Status status : indicatedStatuses) {
            page.append(status.toHtmlString());
            page.append("<br/>");
        }
        return page.toString();
    }


    private String printStatusPage() {
        StringBuilder page = new StringBuilder();
        Collection<Status> indicatedStatuses = statusIndicatorService.getStatus();
        for (Status status : indicatedStatuses) {
            page.append(status.toHtmlString());
            page.append("<br/>");
        }

        Collection<Measurement> averageMetrics = averageMetricRepository.retrieveAllMeasurements();
        if (!averageMetrics.isEmpty()) {
            page.append("<b>Metrics:</b><br/>");
            page.append(printMetrics());
        }
        return page.toString();
    }


    private String printMetrics() {
        StringBuilder page = new StringBuilder();
        Collection<Measurement> metrics = averageMetricRepository.retrieveAllMeasurements();
        for (Measurement metric : metrics) {
            page.append(metric.toString());
            page.append("<br/>");
        }
        return page.toString();
    }

    private class CurrencyInformationIndicator implements StatusIndicator {

        private final String name;
        private final CurrencyInformationProvider currencyInformationProvider;

        public CurrencyInformationIndicator(String name, CurrencyInformationProvider currencyInformationProvider) {
            this.name = name;
            this.currencyInformationProvider = currencyInformationProvider;
        }

        @Override
        public Status status() {
            Flag flag = currencyInformationProvider.getCurrencyInformation().isPresent() ? Flag.OK : Flag.BUSY;
            return new SimpleStatus(flag, name);
        }
    }
}
