package com.av.investment_tracker.price.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PriceConfiguration {

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }

}
