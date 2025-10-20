package com.backend.petplace.global.component;

import com.backend.petplace.domain.place.dto.KcisaDto;
import com.backend.petplace.global.config.ImportProperties;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class KcisaClient {

  private final ImportProperties props;

  private WebClient webClient() {
    int bytes = props.getMaxInMemoryMb() * 1024 * 1024;

    return WebClient.builder()
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.ACCEPT_ENCODING, "gzip")
        .codecs(c -> c.defaultCodecs().maxInMemorySize(bytes))
        .build();
  }

  public List<KcisaDto.Item> fetchPage(int pageNo) {
    URI uri = URI.create(String.format(
        "%s?serviceKey=%s&numOfRows=%d&pageNo=%d",
        props.getBaseUrl(), props.getServiceKey(), props.getPageSize(), pageNo
    ));

    // builder를 매번 새로 만들 필요는 없지만, props 변경 적용 위해 메서드로 분리
    WebClient client = webClient();

    KcisaDto root = client.get()
        .uri(uri)
        .retrieve()
        .bodyToMono(KcisaDto.class)
        .block();

    if (root == null || root.response() == null || root.response().body() == null) return List.of();
    var items = root.response().body().items();
    return (items == null || items.item() == null) ? List.of() : items.item();
  }
}
