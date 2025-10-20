package com.backend.petplace.global.initdata;

import com.backend.petplace.domain.place.importer.KcisaImportService;
import com.backend.petplace.global.config.ImportProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "import", name = "enabled", havingValue = "true")
public class KcisaImportRunner implements ApplicationRunner {

  private final KcisaImportService service;
  private final ImportProperties props;

  @Override
  public void run(ApplicationArguments args) {
    int count = service.importAll();
    System.out.printf("[KCISA IMPORT] imported=%d (pageSize=%d)%n", count, props.pageSize());
  }
}
