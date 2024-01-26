package com.pavelgajdos.demo.exchangerateservice.api;

import com.pavelgajdos.demo.exchangerateservice.common.dto.outbound.DailyTimeSeries;
import com.pavelgajdos.demo.exchangerateservice.common.dto.outbound.ExchangeRates;
import com.pavelgajdos.demo.exchangerateservice.common.enums.Period;
import com.pavelgajdos.demo.exchangerateservice.rates.CouldNotProvideExchangeRatesException;
import com.pavelgajdos.demo.exchangerateservice.rates.ExchangeRateService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/rates")
@CrossOrigin(origins = "http://localhost:5173")
public class RatesController {


    @Value("${exchangeRates.baseCurrency}")
    private String baseCurrency;

    @Value("#{'${exchangeRates.supportedCurrencies}'.split(',')}")
    private Set<String> supportedCurrencies;

    private final ExchangeRateService exchangeRateService;

    public RatesController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    /**
     * Just a simple demo. This endpoint may be extended with parameters such as:
     * - periodEndDate - A date which denotes the end of period - mutually exclusive with startDate and endDate
     * - startDate
     * - endDate
     * - frequency - Instead of getting only daily rates, we could be able to provide more granular data. It all depends on an actual data source
     */
    @GetMapping("/timeseries/{currency}")
    public DailyTimeSeries getTimeSeries(
            @Valid @NotNull @PathVariable("currency") String currency,
            @RequestParam(value = "baseCurrency", required = false) String baseCurrency,
            @RequestParam(value = "period", required = false) Period period
    ) {
        if (!supportedCurrencies.contains(currency)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The currency '" + currency + "' is not supported.");
        }

        if (baseCurrency != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Base currency is not supported at the moment."); // only EUR is working in the free plan
        }
        baseCurrency = this.baseCurrency;

        if (period == null) {
            period = Period.WEEK;
        } else if (period != Period.WEEK) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The period parameter " + period.name() + " is valid but it is disabled at the moment.");
        }

        //

        try {
            return exchangeRateService.getDailyTimeSeries(period, baseCurrency, currency);
        } catch (CouldNotProvideExchangeRatesException e) {
            log.error("Could not provide exchange rates (" + period.name() + " " + baseCurrency + " " + currency + "): " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The service is not able to provide exchange rates: " + e.getMessage());
        }
    }

    @GetMapping("/{date}")
    public ExchangeRates getRatesForDay(
            @PathVariable("date") LocalDate date,
            @RequestParam(value = "baseCurrency", required = false) String baseCurrency
    ) {
        if (baseCurrency != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Base currency is not supported at the moment."); // only EUR is working in the free plan
        }
        baseCurrency = this.baseCurrency;

        //

        try {
            return exchangeRateService.getRates(date, baseCurrency, supportedCurrencies);
        } catch (CouldNotProvideExchangeRatesException e) {
            log.error("Could not provide exchange rates for the given date " + date.toString() + " and base currency: " + baseCurrency + ": " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The service is not able to provide exchange rates: " + e.getMessage());
        }
    }
}
