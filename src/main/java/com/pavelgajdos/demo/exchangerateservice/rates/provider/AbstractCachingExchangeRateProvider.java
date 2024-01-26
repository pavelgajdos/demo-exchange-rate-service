package com.pavelgajdos.demo.exchangerateservice.rates.provider;

import org.springframework.lang.NonNull;

public abstract class AbstractCachingExchangeRateProvider implements ExchangeRateProvider {

    protected final ExchangeRateProvider source;

    public AbstractCachingExchangeRateProvider(@NonNull ExchangeRateProvider source) {
        this.source = source;
    }
}
