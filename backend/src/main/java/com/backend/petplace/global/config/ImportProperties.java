package com.backend.petplace.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "import")
public record ImportProperties(
    boolean enabled,
    String baseUrl,
    String serviceKey,
    int pageSize,
    long sleepMs,
    int maxInMemoryMb
) {}
