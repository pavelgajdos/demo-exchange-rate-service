package com.pavelgajdos.demo.exchangerateservice.rates.provider.remote.fixer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FixerHistoricalRatesResponse(
        boolean success,
        LocalDate date,
        String base,
        Map<String, BigDecimal> rates,
        FixerErrorResponse error
) {
}
