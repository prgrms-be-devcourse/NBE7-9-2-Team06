package com.backend.petplace.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

  private final ImportProperties props;

  @Bean(name = "kcisaWebClient")
  public WebClient kcisaWebClient() {
    int bytes = props.maxInMemoryMb() * 1024 * 1024;

    ExchangeStrategies strategies = ExchangeStrategies.builder()
        .codecs(c -> c.defaultCodecs().maxInMemorySize(bytes))
        .build();

    return WebClient.builder()
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.ACCEPT_ENCODING, "gzip")
        .exchangeStrategies(strategies)
        .build();
  }
}
