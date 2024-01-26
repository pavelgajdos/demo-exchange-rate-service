package com.pavelgajdos.demo.exchangerateservice.rates;

import com.pavelgajdos.demo.exchangerateservice.common.dto.outbound.DailyTimeSeries;
import com.pavelgajdos.demo.exchangerateservice.common.enums.Period;
import com.pavelgajdos.demo.exchangerateservice.common.dto.outbound.ExchangeRates;
import com.pavelgajdos.demo.exchangerateservice.rates.provider.FallbackingExchangeRateProvider;
import com.pavelgajdos.demo.exchangerateservice.rates.provider.exceptions.ExchangeRateProviderException;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExchangeRateService {

    private final FallbackingExchangeRateProvider exchangeRateProvider;

    public ExchangeRateService(FallbackingExchangeRateProvider exchangeRateProvider) {
        this.exchangeRateProvider = exchangeRateProvider;
    }

    @NotNull
    public ExchangeRates getRates(@NotNull LocalDate date, @NotNull String baseCurrency, @NotNull Set<String> currencies) throws CouldNotProvideExchangeRatesException {
        try {
            return exchangeRateProvider.getRatesForDay(date, baseCurrency, currencies);
        } catch (ExchangeRateProviderException e) {
            throw new CouldNotProvideExchangeRatesException(e.getMessage());
        }
    }

    @NotNull
    public DailyTimeSeries getDailyTimeSeries(@NotNull Period period, @NotNull String baseCurrency, @NotNull String currency) throws CouldNotProvideExchangeRatesException {
        var toDate = LocalDate.now();
        var fromDate = toDate.minus(period.toTemporalAmount());
        try {
            var result = exchangeRateProvider.getDailyTimeSeries(fromDate, toDate, baseCurrency, currency);
            var rates = result.stream().collect(Collectors.toMap(ExchangeRates::date, exchangeRates -> exchangeRates.rates().get(currency)));

            return new DailyTimeSeries(
                    fromDate,
                    toDate,
                    baseCurrency,
                    currency,
                    rates
            );
        } catch (ExchangeRateProviderException e) {
            throw new CouldNotProvideExchangeRatesException(e.getMessage());
        }
    }
}
