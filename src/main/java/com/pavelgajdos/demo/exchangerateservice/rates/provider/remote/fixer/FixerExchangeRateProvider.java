package com.pavelgajdos.demo.exchangerateservice.rates.provider.remote.fixer;

import com.pavelgajdos.demo.exchangerateservice.common.dto.outbound.ExchangeRates;
import com.pavelgajdos.demo.exchangerateservice.rates.provider.ExchangeRateProvider;
import com.pavelgajdos.demo.exchangerateservice.rates.provider.exceptions.ExchangeRateProviderException;
import com.pavelgajdos.demo.exchangerateservice.rates.provider.remote.fixer.dto.FixerHistoricalRatesResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class FixerExchangeRateProvider implements ExchangeRateProvider {
    private final FixerAPI fixerAPIService;

    public FixerExchangeRateProvider(FixerAPI fixerAPIService) {
        this.fixerAPIService = fixerAPIService;
    }

    @Value("${exchangeRates.apis.fixerio.apiKey}")
    private String apiKey;

    @Override
    public ExchangeRates getRatesForDay(@NotNull LocalDate date, @NotNull String baseCurrency, Collection<String> currencies) throws ExchangeRateProviderException {
        // ignoring currencies parameter. we can load all rates, it is still just a single HTTP request
        var response = fixerAPIService.getHistoricalRates(apiKey, date, baseCurrency, null);

        if (!response.success()) {
            throw new ExchangeRateProviderException("The Fixer.io API returned an error: " + response.error().toString()); // always 500, all errors are fault of our service, except for cases when the API is down, but this is also 500
        }

        return new ExchangeRates(
                response.date(),
                response.base(),
                response.rates()
        );
    }

    /**
     * TODO Optimize not to load rates for days which were previously downloaded. I decided not to improve it for the sake of this demo
     * How? Upgrade fixer subscription plan or change the upper caching layer to use {@code ExchangeRateProvider::getRatesForDay} method.
     */
    @Override
    public List<ExchangeRates> getDailyTimeSeries(@NotNull LocalDate dateFrom, @NotNull LocalDate dateTo, @NotNull String baseCurrency, @NotNull String currency) throws ExchangeRateProviderException {
        // fixer.io free plan does not support time series endpoint..
        var timeSeries = new ArrayList<ExchangeRates>();
        var date = dateFrom;
        while (date.isBefore(dateTo) || date.isEqual(dateTo)) {
            var rates = getRatesForDay(date, baseCurrency, null);
            timeSeries.add(rates);
            date = date.plusDays(1);
        }
        return timeSeries;
    }

    public interface FixerAPI {
        @GetExchange("/{date}")
        FixerHistoricalRatesResponse getHistoricalRates(
                @RequestParam(name = "access_key") String accessKey,
                @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                @RequestParam(name = "base") String baseCurrency,
                @RequestParam(name = "symbols", required = false) List<String> symbols
        );
    }
}
