package com.pavelgajdos.demo.exchangerateservice.rates.provider.remote.example;

import com.pavelgajdos.demo.exchangerateservice.common.dto.outbound.ExchangeRates;
import com.pavelgajdos.demo.exchangerateservice.rates.provider.ExchangeRateProvider;
import com.pavelgajdos.demo.exchangerateservice.rates.provider.exceptions.ExchangeRateProviderException;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

// just an example of another client which could be used when the primary exchange rate provider is unavailable
@Service
public class AnotherAPIClient implements ExchangeRateProvider {

    @Override
    public ExchangeRates getRatesForDay(LocalDate date, String baseCurrency, Collection<String> currencies) throws ExchangeRateProviderException {
        return null;
    }

    @Override
    public List<ExchangeRates> getDailyTimeSeries(LocalDate dateFrom, LocalDate dateTo, String baseCurrency, @NotNull String currency) throws ExchangeRateProviderException {
        return null;
    }
}
