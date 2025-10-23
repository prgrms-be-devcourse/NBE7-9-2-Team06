package com.backend.petplace.domain.place.repository;

import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.place.projection.PlaceSearchRow;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

  Optional<Place> findByUniqueKey(String uniqueKey);

  @Query(value = """
    SELECT
      p.id              AS id,
      p.name            AS name,
      p.category2       AS category2,
      p.latitude        AS latitude,
      p.longitude       AS longitude,
      ST_Distance_Sphere(POINT(:lon, :lat), POINT(p.longitude, p.latitude)) AS distanceMeters,
      p.average_rating  AS averageRating,
      p.address         AS address
    FROM place p
    WHERE
      p.latitude  BETWEEN :minLat AND :maxLat
      AND p.longitude BETWEEN :minLon AND :maxLon
      AND ST_Distance_Sphere(POINT(:lon, :lat), POINT(p.longitude, p.latitude)) <= :radiusMeters
      AND (:category2Count = 0 OR p.category2 IN (:category2List))
      AND (:keyword IS NULL OR p.name LIKE CONCAT('%', :keyword, '%'))
    ORDER BY distanceMeters ASC
    LIMIT :limit OFFSET :offset
  """, nativeQuery = true)
  List<PlaceSearchRow> searchWithinRadius(
      @Param("lat") double lat,
      @Param("lon") double lon,
      @Param("minLat") double minLat,
      @Param("maxLat") double maxLat,
      @Param("minLon") double minLon,
      @Param("maxLon") double maxLon,
      @Param("radiusMeters") int radiusMeters,
      @Param("category2List") List<String> category2List,
      @Param("category2Count") int category2Count,
      @Param("keyword") String keyword,
      @Param("limit") int limit,
      @Param("offset") int offset
  );
}