package com.pavelgajdos.demo.exchangerateservice.rates.provider.local.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.List;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    List<ExchangeRate> findByBaseCurrencyAndDateAndCurrencyIsIn(String baseCurrency, LocalDate date, Collection<String> currencies);

    List<ExchangeRate> findByBaseCurrencyAndDateIsBetweenAndCurrency(String baseCurrency, LocalDate dateFrom, LocalDate dateTo, String currency);

    boolean existsByBaseCurrencyAndDate(String baseCurrency, LocalDate date);

    boolean existsByBaseCurrencyAndDateAndCurrency(String baseCurrency, LocalDate date, String currency);
    boolean existsByBaseCurrencyAndDateAndCurrencyIsIn(String baseCurrency, LocalDate date, Collection<String> currencies);

    @Query("SELECT COUNT(DISTINCT e.date) FROM ExchangeRate e WHERE e.baseCurrency = ?1 AND e.date BETWEEN ?3 AND ?4 AND e.currency = ?2")
    long countDistinctDatesBetweenForCurrency(LocalDate dateFrom, LocalDate dateTo, String baseCurrency, String currency);

    default boolean allDataExistInDateRange(String baseCurrency, String currency, LocalDate dateFrom, LocalDate dateTo) {
        var numberOfDistinctDatesInDatabase = countDistinctDatesBetweenForCurrency(dateFrom, dateTo, baseCurrency, currency);
        var expectedNumberOfDays = Math.abs(Period.between(dateFrom, dateTo.plusDays(1)).getDays());
        return expectedNumberOfDays == numberOfDistinctDatesInDatabase;
    }

}
