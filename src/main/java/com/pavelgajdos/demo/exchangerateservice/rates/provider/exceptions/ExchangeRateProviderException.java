package com.pavelgajdos.demo.exchangerateservice.rates.provider.exceptions;

import java.io.IOException;

public class ExchangeRateProviderException extends IOException {
    public ExchangeRateProviderException(String message) {
        super(message);
    }
}
