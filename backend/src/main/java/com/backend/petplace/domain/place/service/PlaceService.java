package com.backend.petplace.domain.place.service;

import com.backend.petplace.domain.place.dto.response.PlaceDetailResponse;
import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.place.repository.PlaceRepository;
import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceService {

  private final PlaceRepository placeRepository;

  @Transactional(readOnly = true)
  public PlaceDetailResponse getPlaceDetail(Long placeId) {
    Place place = placeRepository.findById(placeId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PLACE));

    return PlaceDetailResponse.from(place);
  }

}
