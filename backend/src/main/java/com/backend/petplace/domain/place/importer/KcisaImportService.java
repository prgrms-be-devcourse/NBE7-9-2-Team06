package com.backend.petplace.domain.place.importer;

import com.backend.petplace.domain.place.dto.KcisaDto;
import com.backend.petplace.domain.place.dto.KcisaDto.Item;
import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.place.importer.model.ImportParsed;
import com.backend.petplace.domain.place.repository.PlaceRepository;
import com.backend.petplace.global.config.ImportProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KcisaImportService {

  private final ImportProperties props;
  private final KcisaClient client;
  private final KcisaParser parser;
  private final PlaceRepository placeRepository;

  /**
   * 전체 페이지를 돌며 적재 (멱등)
   */
  @Transactional
  public int importAll() {
    int total = 0;
    for (int page = 1; ; page++) {
      List<Item> items = client.fetchPage(page);
      if (items.isEmpty())
        break;

      for (KcisaDto.Item it : items) {
        var p = parser.parse(it);
        upsert(p);
        total++;
      }

      // 페이징 종료 판단: 응답 아이템 수가 page-size 미만이면 마지막
      if (items.size() < props.pageSize())
        break;

      // 다음 호출 전 sleep
      try {
        Thread.sleep(props.sleepMs());
      } catch (InterruptedException ignored) {
      }
    }
    return total;
  }

  /** uniqueKey 기준 업서트 */
  private void upsert(ImportParsed f) {
    var opt = placeRepository.findByUniqueKey(f.uniqueKey());
    if (opt.isPresent()) {
      Place e = opt.get();
      e.apply(
          f.name(), f.category1(), f.category2(),
          f.openingHours(), f.closedDays(), f.parking(), f.petAllowed(), f.petRestriction(),
          f.tel(), f.url(), f.postalCode(), f.address(), f.latitude(), f.longitude(), f.rawDescription()
      );
    } else {
      Place e = Place.builder()
          .uniqueKey(f.uniqueKey())
          .name(f.name())
          .category1(f.category1())
          .category2(f.category2())
          .openingHours(f.openingHours())
          .closedDays(f.closedDays())
          .parking(f.parking())
          .petAllowed(f.petAllowed())
          .petRestriction(f.petRestriction())
          .tel(f.tel())
          .url(f.url())
          .postalCode(f.postalCode())
          .address(f.address())
          .latitude(f.latitude())
          .longitude(f.longitude())
          .rawDescription(f.rawDescription())
          .build();
      placeRepository.save(e);
    }
  }
}
