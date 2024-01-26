package com.pavelgajdos.demo.exchangerateservice.rates.provider;

import com.pavelgajdos.demo.exchangerateservice.common.dto.outbound.ExchangeRates;
import com.pavelgajdos.demo.exchangerateservice.rates.provider.exceptions.ExchangeRateProviderException;
import com.pavelgajdos.demo.exchangerateservice.rates.provider.exceptions.NoExchangeRateProviderException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.function.ThrowingFunction;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class FallbackingExchangeRateProvider implements ExchangeRateProvider {
    private final List<? extends ExchangeRateProvider> clients;

    public FallbackingExchangeRateProvider(List<? extends ExchangeRateProvider> clients) {
        this.clients = clients;
    }

    @NotNull
    private ExchangeRateProvider getFirst() throws NoExchangeRateProviderException {
        if (clients.isEmpty()) {
            throw new NoExchangeRateProviderException();
        }
        return clients.get(0);
    }

    @NotNull
    private ExchangeRateProvider getNext(ExchangeRateProvider current) throws NoExchangeRateProviderException {
        if (current == null) {
            return getFirst();
        }

        log.info("Looking for a fallback API service for " + current.getClass().getName());

        var index = clients.indexOf(current);

        if (index < 0) { // this should not happen
            log.error("Current API provider not found, error! This should not happen, what is going on?????!!!");
            throw new NoExchangeRateProviderException();
        }

        if ((index + 1) >= clients.size()) {
            log.error("There is no other exchange rate provider");
            throw new NoExchangeRateProviderException();
        }
        return clients.get(index + 1);
    }

    @Override
    public ExchangeRates getRatesForDay(@NotNull LocalDate date, @NotNull String baseCurrency, Collection<String> currencies) throws ExchangeRateProviderException {
        return callMethodWithFallback(exchangeRateProvider -> exchangeRateProvider.getRatesForDay(date, baseCurrency, currencies));
    }

    @Override
    public List<ExchangeRates> getDailyTimeSeries(@NotNull LocalDate dateFrom, @NotNull LocalDate dateTo, @NotNull String baseCurrency, @NotNull String currency) throws ExchangeRateProviderException {
        return callMethodWithFallback(exchangeRateProvider -> exchangeRateProvider.getDailyTimeSeries(dateFrom, dateTo, baseCurrency, currency));
    }

    private <R> R callMethodWithFallback(ThrowingFunction<ExchangeRateProvider, R> method) throws ExchangeRateProviderException {
        ExchangeRateProvider provider = null;

        var errorMessages = new HashMap<String, String>();

        while (true) { // be careful not to break any of the logic below to avoid an infinite loop!
            try {
                provider = getNext(provider);
            } catch (NoExchangeRateProviderException e) {
                log.error("All API repositories failed to load data. Details: " + errorMessages);
                throw new ExchangeRateProviderException("No API repository returned a valid response. Here is the list of error messages: " + errorMessages);
            }

            try {
                return method.apply(provider);
            } catch (RuntimeException e) {
                e.printStackTrace(); // TODO sentry logging?

                var providerName = provider.getClass().getName();

                log.error("Error loading data from exchange rate provider [" + providerName + "]: " + e.getMessage());
                errorMessages.put(providerName, e.getMessage());
            }
        }
    }
}
