package com.pavelgajdos.demo.exchangerateservice.rates.provider.remote.fixer;

import com.pavelgajdos.demo.exchangerateservice.rates.provider.exceptions.ExchangeRateProviderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties
@Slf4j
public class FixerConfig {

    @Value("${exchangeRates.apis.fixerio.baseUrl}")
    private String baseUrl;

    @Bean
    public FixerExchangeRateProvider.FixerAPI fixerAPIClient() {
        log.info("Creating FixerAPI with base url: " + baseUrl);

        var restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    throw new ExchangeRateProviderException("Fixer API responded with an error status code: " + response.getStatusCode());
                })
                .build();

        var adapter = RestClientAdapter.create(restClient);
        var factory = HttpServiceProxyFactory
                .builderFor(adapter)
                .build();

        return factory.createClient(FixerExchangeRateProvider.FixerAPI.class);
    }
}
