package com.pavelgajdos.demo.exchangerateservice.rates.provider.local.example;

import com.pavelgajdos.demo.exchangerateservice.common.dto.outbound.ExchangeRates;
import com.pavelgajdos.demo.exchangerateservice.rates.provider.AbstractCachingExchangeRateProvider;
import com.pavelgajdos.demo.exchangerateservice.rates.provider.ExchangeRateProvider;
import com.pavelgajdos.demo.exchangerateservice.rates.provider.exceptions.ExchangeRateProviderException;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

// This is just an example of what could be a redis or in-memory cache.
// It gives us a better control over what is cached or not.
// For simple use cases, Spring's cache abstraction would be satisfactory.
public class AnotherCachingExchangeRateProvider extends AbstractCachingExchangeRateProvider {

    public AnotherCachingExchangeRateProvider(ExchangeRateProvider source) {
        super(source);
    }

    @Override
    public ExchangeRates getRatesForDay(@NotNull LocalDate date, @NotNull String baseCurrency, Collection<String> currencies) throws ExchangeRateProviderException {
        return source.getRatesForDay(date, baseCurrency, currencies);
    }

    @Override
    public List<ExchangeRates> getDailyTimeSeries(@NotNull LocalDate dateFrom, @NotNull LocalDate dateTo, @NotNull String baseCurrency, @NotNull String currency) throws ExchangeRateProviderException {
        return source.getDailyTimeSeries(dateFrom, dateTo, baseCurrency, currency);
    }
}
