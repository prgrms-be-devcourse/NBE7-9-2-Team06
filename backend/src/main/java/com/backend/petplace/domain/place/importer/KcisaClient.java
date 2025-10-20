package com.backend.petplace.domain.place.importer;

import com.backend.petplace.domain.place.dto.KcisaDto;
import com.backend.petplace.global.config.ImportProperties;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class KcisaClient {

  private final ImportProperties props;

  @Qualifier("kcisaWebClient")
  private final WebClient client;

  public List<KcisaDto.Item> fetchPage(int pageNo) {
    URI uri = URI.create(String.format(
        "%s?serviceKey=%s&numOfRows=%d&pageNo=%d",
        props.baseUrl(), props.serviceKey(), props.pageSize(), pageNo
    ));

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
