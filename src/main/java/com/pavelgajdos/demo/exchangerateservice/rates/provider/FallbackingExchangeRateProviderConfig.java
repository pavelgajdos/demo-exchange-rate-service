package com.pavelgajdos.demo.exchangerateservice.rates.provider;

import com.pavelgajdos.demo.exchangerateservice.rates.provider.local.database.DatabaseCachingExchangeRateProvider;
import com.pavelgajdos.demo.exchangerateservice.rates.provider.local.database.ExchangeRateRepository;
import com.pavelgajdos.demo.exchangerateservice.rates.provider.local.example.AnotherCachingExchangeRateProvider;
import com.pavelgajdos.demo.exchangerateservice.rates.provider.remote.example.AnotherAPIClient;
import com.pavelgajdos.demo.exchangerateservice.rates.provider.remote.fixer.FixerExchangeRateProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FallbackingExchangeRateProviderConfig {

    private final FixerExchangeRateProvider fixerExchangeRateProvider;
    private final AnotherAPIClient anotherAPIClient;
    private final ExchangeRateRepository exchangeRateRepository;

    public FallbackingExchangeRateProviderConfig(FixerExchangeRateProvider fixerExchangeRateProvider, AnotherAPIClient anotherAPIClient, ExchangeRateRepository exchangeRateRepository) {
        this.fixerExchangeRateProvider = fixerExchangeRateProvider;
        this.anotherAPIClient = anotherAPIClient;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Bean
    public FallbackingExchangeRateProvider exchangeRateProviders() {
        return new FallbackingExchangeRateProvider(
                List.of(
                        new AnotherCachingExchangeRateProvider(
                                new DatabaseCachingExchangeRateProvider(
                                        fixerExchangeRateProvider,
                                        exchangeRateRepository
                                )
                        ),
                        anotherAPIClient // an example of a fallback exchange rate provider that we do not wish to cache, we want to cache the primary provider only..
                )
        );
    }
}
