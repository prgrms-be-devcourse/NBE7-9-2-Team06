package com.backend.petplace.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "import")
public class ImportProperties {

  private boolean enabled;
  private String baseUrl;
  private String serviceKey;
  private int pageSize = 10_000;
  private long sleepMs = 200L;
}
