package com.pavelgajdos.demo.exchangerateservice.rates.provider.local.database;

import com.pavelgajdos.demo.exchangerateservice.common.dto.outbound.ExchangeRates;
import com.pavelgajdos.demo.exchangerateservice.rates.provider.AbstractCachingExchangeRateProvider;
import com.pavelgajdos.demo.exchangerateservice.rates.provider.ExchangeRateProvider;
import com.pavelgajdos.demo.exchangerateservice.rates.provider.exceptions.ExchangeRateProviderException;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseCachingExchangeRateProvider extends AbstractCachingExchangeRateProvider {

    private final ExchangeRateRepository exchangeRateRepository;

    public DatabaseCachingExchangeRateProvider(ExchangeRateProvider source, ExchangeRateRepository exchangeRateRepository) {
        super(source);
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Override
    public ExchangeRates getRatesForDay(@NotNull LocalDate date, @NotNull String baseCurrency, Collection<String> currencies) throws ExchangeRateProviderException {
        if (!hasRatesForDay(date, baseCurrency, currencies)) {
            var ratesFromSource = source.getRatesForDay(date, baseCurrency, currencies);
            saveData(ratesFromSource);
        }
        return loadDataForDay(date, baseCurrency, currencies);
    }

    @Override
    public List<ExchangeRates> getDailyTimeSeries(@NotNull LocalDate dateFrom, @NotNull LocalDate dateTo, @NotNull String baseCurrency, @NotNull String currency) throws ExchangeRateProviderException {
        if (!hasRatesForDateRange(dateFrom, dateTo, baseCurrency, currency)) {
            var ratesFromSource = source.getDailyTimeSeries(dateFrom, dateTo, baseCurrency, currency);
            ratesFromSource.forEach(this::saveData);
        }
        return loadDataForDailyTimeSeries(dateFrom, dateTo, baseCurrency, currency);
    }


    public boolean hasRatesForDay(LocalDate date, String baseCurrency, @NotNull Collection<String> currencies) {
        return currencies.stream().allMatch(currency -> exchangeRateRepository.existsByBaseCurrencyAndDateAndCurrency(baseCurrency, date, currency));
    }

    public boolean hasRatesForDateRange(LocalDate dateFrom, LocalDate dateTo, String baseCurrency, @NotNull String currency) {
        return exchangeRateRepository.allDataExistInDateRange(baseCurrency, currency, dateFrom, dateTo);
    }

    public ExchangeRates loadDataForDay(LocalDate date, String baseCurrency, Collection<String> currencies) {
        var listOfExchangeRateEntities = exchangeRateRepository.findByBaseCurrencyAndDateAndCurrencyIsIn(baseCurrency, date, currencies);
        var values = transformListOfRatesToMap(listOfExchangeRateEntities);

        return new ExchangeRates(
                date,
                baseCurrency,
                values
        );
    }

    public List<ExchangeRates> loadDataForDailyTimeSeries(LocalDate dateFrom, LocalDate dateTo, String baseCurrency, @NotNull String currency) {
        var result = exchangeRateRepository.findByBaseCurrencyAndDateIsBetweenAndCurrency(baseCurrency, dateFrom, dateTo, currency);

        var groupedData = result.stream()
                .collect(Collectors.groupingBy(ExchangeRate::getDate));

        var list = new ArrayList<ExchangeRates>();

        groupedData.forEach((date, exchangeRates) -> {
            var values = transformListOfRatesToMap(exchangeRates);
            var rates = new ExchangeRates(
                    date,
                    baseCurrency,
                    values
            );
            list.add(rates);
        });

        return list;
    }

    private static Map<String, BigDecimal> transformListOfRatesToMap(List<ExchangeRate> ratesAsList) {
        return ratesAsList
                .stream()
                .collect(Collectors.toMap(ExchangeRate::getCurrency, ExchangeRate::getRate));
    }

    public void saveData(ExchangeRates exchangeRates) {

        var entities = new ArrayList<ExchangeRate>();

        exchangeRates.rates().forEach((currency, rate) -> {
            if (exchangeRateRepository.existsByBaseCurrencyAndDateAndCurrency(exchangeRates.baseCurrency(), exchangeRates.date(), currency)) {
                return;
            }

            synchronized (this) {
                if (!exchangeRateRepository.existsByBaseCurrencyAndDateAndCurrency(exchangeRates.baseCurrency(), exchangeRates.date(), currency)) {
                    var entity = new ExchangeRate(
                            exchangeRates.date(),
                            exchangeRates.baseCurrency(),
                            currency,
                            rate
                    );
                    entities.add(entity);
                }
            }
        });

        exchangeRateRepository.saveAllAndFlush(entities);
    }
}
