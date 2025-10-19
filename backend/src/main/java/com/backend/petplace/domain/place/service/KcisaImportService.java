package com.backend.petplace.domain.place.service;

import com.backend.petplace.domain.place.dto.KcisaDto;
import com.backend.petplace.domain.place.dto.KcisaDto.Item;
import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.place.repository.PlaceRepository;
import com.backend.petplace.global.component.KcisaClient;
import com.backend.petplace.global.component.KcisaParser;
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
      if (items.size() < props.getPageSize())
        break;

      // 다음 호출 전 sleep
      try {
        Thread.sleep(props.getSleepMs());
      } catch (InterruptedException ignored) {
      }
    }
    return total;
  }

  /** uniqueKey 기준 업서트 */
  private void upsert(KcisaParser.Parsed f) {
    var opt = placeRepository.findByUniqueKey(f.getUniqueKey());
    if (opt.isPresent()) {
      Place e = opt.get();
      e.apply(
          f.getName(), f.getCategory1(), f.getCategory2(),
          f.getOpeningHours(), f.getClosedDays(), f.getParking(), f.getPetAllowed(), f.getPetRestriction(),
          f.getTel(), f.getUrl(), f.getPostalCode(), f.getAddress(), f.getLatitude(), f.getLongitude(), f.getRawDescription()
      );
    } else {
      Place e = Place.builder()
          .uniqueKey(f.getUniqueKey())
          .name(f.getName())
          .category1(f.getCategory1())
          .category2(f.getCategory2())
          .openingHours(f.getOpeningHours())
          .closedDays(f.getClosedDays())
          .parking(f.getParking())
          .petAllowed(f.getPetAllowed())
          .petRestriction(f.getPetRestriction())
          .tel(f.getTel())
          .url(f.getUrl())
          .postalCode(f.getPostalCode())
          .address(f.getAddress())
          .latitude(f.getLatitude())
          .longitude(f.getLongitude())
          .rawDescription(f.getRawDescription())
          .build();
      placeRepository.save(e);
    }
  }
}
