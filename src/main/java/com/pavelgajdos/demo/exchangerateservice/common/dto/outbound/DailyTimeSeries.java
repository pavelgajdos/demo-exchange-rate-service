package com.pavelgajdos.demo.exchangerateservice.common.dto.outbound;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record DailyTimeSeries(
        @NotNull LocalDate from,
        @NotNull LocalDate to,
        @NotNull String baseCurrency,
        @NotNull String currency,
        @NotNull Map<LocalDate, BigDecimal> rates // key=day, value=rate
) {
}

