package com.pavelgajdos.demo.exchangerateservice.rates.provider.local.database;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"baseCurrency", "date", "currency"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @CreatedDate
    private Instant savedAt;

    @NotNull
    private LocalDate date;

    @NotNull
    private String baseCurrency;

    @NotNull
    private String currency;

    @NotNull
    @Column(precision = 18, scale = 8)
    // 8 = fixer returns scale 6, this is just a precaution for future changes or for another API
    private BigDecimal rate;

    public ExchangeRate(LocalDate date, String baseCurrency, String currency, BigDecimal rate) {
        this.date = date;
        this.baseCurrency = baseCurrency;
        this.currency = currency;
        this.rate = rate;
    }
}
