package no.norwegian.currency.model;

import au.com.bytecode.opencsv.CSVReader;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests the parsing of the historical rates file
 * currently not used
 */
public class ParseHistoricalRatesTest {

    @Test
    public void shouldParseFileOK() throws Exception {
        InputStream resourceAsStream = ParseHistoricalRatesTest.class.getResourceAsStream("/examples/historicalRates.csv");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(resourceAsStream))) {
            CSVReader reader = new CSVReader(in);
            List<String[]> lines = reader.readAll();
            parseCSV(lines);
        }
    }


    private Map<String, Valuta> parseCSV(List<String[]> lines) {
        Map<String, Valuta> tmpCurrencySetHistoric = new HashMap<>();

        if (lines == null || lines.size() <= 1) {
            return tmpCurrencySetHistoric;
        }

        for (int i = 1; i < lines.size(); i++) {
            Valuta valuta = new Valuta();
            valuta.oppdatert = lines.get(i)[0];
            valuta.valutakursList = new ArrayList<>();

            valuta.valutakursList.add(createValutakurs("USD", 1, new BigDecimal(lines.get(i)[1])));
            valuta.valutakursList.add(createValutakurs("EUR", 1, new BigDecimal(lines.get(i)[2])));
            valuta.valutakursList.add(createValutakurs("SEK", 100, new BigDecimal(lines.get(i)[3])));
            valuta.valutakursList.add(createValutakurs("DKK", 100, new BigDecimal(lines.get(i)[4])));
            valuta.valutakursList.add(createValutakurs("GBP", 1, new BigDecimal(lines.get(i)[5])));
            valuta.valutakursList.add(createValutakurs("CHF", 100, new BigDecimal(lines.get(i)[6])));
            valuta.valutakursList.add(createValutakurs("JPY", 100, new BigDecimal(lines.get(i)[7])));
            valuta.valutakursList.add(createValutakurs("CAD", 1, new BigDecimal(lines.get(i)[8])));
            //valuta.valutakursList.add(createValutakurs("ISK", 1, new BigDecimal(lines.get(i)[1])));
            valuta.valutakursList.add(createValutakurs("AUD", 1, new BigDecimal(lines.get(i)[10])));
            tmpCurrencySetHistoric.put(valuta.oppdatert, valuta);
        }

        return tmpCurrencySetHistoric;
    }

    private Valutakurs createValutakurs(String currencyCode, int unitSize, BigDecimal n) {
        Valutakurs valutakurs = new Valutakurs(null, null, currencyCode, unitSize, null);
        valutakurs.overforsel = new Overforsel(n, n, n, n, n);
        valutakurs.seddel = new Seddel(n, n);
        return valutakurs;
    }

}
