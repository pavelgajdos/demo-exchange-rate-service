package com.pavelgajdos.demo.exchangerateservice.rates.provider;


import com.pavelgajdos.demo.exchangerateservice.common.dto.outbound.ExchangeRates;
import com.pavelgajdos.demo.exchangerateservice.rates.provider.exceptions.ExchangeRateProviderException;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface ExchangeRateProvider {

    ExchangeRates getRatesForDay(@NotNull LocalDate date, @NotNull String baseCurrency, Collection<String> currencies) throws ExchangeRateProviderException;

    List<ExchangeRates> getDailyTimeSeries(@NotNull LocalDate dateFrom, @NotNull LocalDate dateTo, @NotNull String baseCurrency, @NotNull String currency) throws ExchangeRateProviderException;

    // TODO define more methods..
}
