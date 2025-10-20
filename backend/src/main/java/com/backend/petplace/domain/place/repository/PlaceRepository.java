package com.backend.petplace.domain.place.repository;

import com.backend.petplace.domain.place.entity.Place;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

  Optional<Place> findByUniqueKey(String uniqueKey);

}
