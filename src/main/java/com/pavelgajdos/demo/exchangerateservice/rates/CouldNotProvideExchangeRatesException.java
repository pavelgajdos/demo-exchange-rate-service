package com.pavelgajdos.demo.exchangerateservice.rates;

public class CouldNotProvideExchangeRatesException extends Exception {
    public CouldNotProvideExchangeRatesException(String message) {
        super(message);
    }
}
