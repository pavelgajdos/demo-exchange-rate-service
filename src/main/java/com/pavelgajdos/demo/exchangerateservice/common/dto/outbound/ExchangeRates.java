package com.pavelgajdos.demo.exchangerateservice.common.dto.outbound;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record ExchangeRates(
    @NotNull LocalDate date,
    @NotNull String baseCurrency,
    @NotNull Map<String, BigDecimal> rates // key=currency, value=rate
) {
}
