package com.pavelgajdos.demo.exchangerateservice.common.enums;

import java.time.temporal.TemporalAmount;

public enum Period {
    WEEK(java.time.Period.ofWeeks(1)),
    MONTH(java.time.Period.ofMonths(1)),
    YEAR(java.time.Period.ofYears(1));

    private final java.time.Period period;

    Period(java.time.Period period) {
        this.period = period;
    }

    public TemporalAmount toTemporalAmount() {
        return period;
    }
}
