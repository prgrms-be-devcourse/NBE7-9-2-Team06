package com.backend.petplace.domain.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.petplace.domain.place.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

}
